import static org.junit.Assert.*;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import org.mockftpserver.core.command.CommandNames;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.stub.command.StorCommandHandler;
import timeout.annotation.processor.TimeoutInvocationHandler;
import uploader.Fabric;
import uploader.Uploadable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/*
 * Здесь интеграционные тесты
 * */
public class FtpUploaderTest  {
	private final static int methodDepth = 9;
	private static final String HOME_DIR = "/";
	
	private static final String FILE_path = "/dir";
	private static final String FILE_name = "sample";
	private static final String FILE_ext = ".txt";
	private static final String FILE_name_ext = FILE_name + FILE_ext;
	private static final String FILE_full_name = FILE_path + "/" + FILE_name_ext;
	private static final String FILE_CONTENTS = "abcdef 1234567890";
	
	private static final String login = "login";
	private static final String password = "password";

	private Uploadable ftpUploader;
	private FakeFtpServer fakeFtpServer;

	private FileSystem fileSystem;
	
	@Test(timeout=2000)
	public void testGetListOfFile() throws IOException {
		printStartTest();
		
		FTPFile[] xFiles = ftpUploader.getListOfFile(FILE_path);
		assertThat(xFiles, is(not(nullValue())));
		
		// Утверждаем что у нас 1 файл
		assertThat(xFiles.length, is(1));
		assertThat(xFiles[0].getName(), is(FILE_name_ext));
		
		printEndTest();
	}
	
	@Test(timeout=5000)
	public void testSingleUploadToFTP() throws Exception {
		printStartTest();

		ftpUploader.uploadToFTP(createTempFile(), FILE_path);
		
		if (! TimeoutInvocationHandler.timeoutElapsed==true) wait();
		TimeoutInvocationHandler.timeoutElapsed=false;
		
		printEndTest();
	}
	
	@Test(timeout=20000) // думаю, если поиграться с клиентскими(FTPClient) таймаутами, то можно будет уменьшить таймаут теста
	public void testWorkAfterUploadToFTP() throws Exception {
		printStartTest();

		for(int i=0; i<2; i++){
			ftpUploader.uploadToFTP(createTempFile(), FILE_path+i);
			ftpUploader.checkCompleted();

			if (! TimeoutInvocationHandler.timeoutElapsed==true) wait();
		}
		
		printEndTest();
	}
		
	private File createTempFile() throws IOException{
		// Запись файла в ФС
		File temp = File.createTempFile(FILE_name, FILE_ext);
		System.out.println("Создан временный файл "+temp.getAbsolutePath() + " для заливки на сервер\n");
		OutputStream os = new FileOutputStream(temp);
		os.write(FILE_CONTENTS.getBytes());
		os.flush();
		os.close();
		temp.deleteOnExit();

		return temp;
	}

	@Before
	public void setUp() throws Exception {	
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.setServerControlPort(0); // use any free port

		fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new FileEntry(FILE_full_name, FILE_CONTENTS));
		fileSystem.add(new FileEntry("/file0", "qwerty"));
		fileSystem.add(new FileEntry("/file1", "zxcv"));
		fakeFtpServer.setFileSystem(fileSystem);

		UserAccount userAccount = new UserAccount(login, password, HOME_DIR);
		fakeFtpServer.addUserAccount(userAccount);
		
		// Задаём наш тормознутый обработчик
		DelayedAfterUploadCommandHandler handler = new DelayedAfterUploadCommandHandler();
		fakeFtpServer.setCommandHandler(CommandNames.STOR, handler);
		
		fakeFtpServer.start();
		
		int port = fakeFtpServer.getServerControlPort();
		System.out.println("fake Server port:" + port); // можем зайти на сервер через FileZilla

		ftpUploader = Fabric.createFtpUploader("localhost", port, login, password);
		//ftpUploader.addObserver(this);
		ftpUploader.doStart();
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()");
		//System.in.read();
		fakeFtpServer.stop();
		ftpUploader.doEnd();
	}

	/*public void update(final Observable o, final Object arg) {
		if (arg==null){
			System.out.println("update()");
			updateLatch.countDown();
		}
	}*/
	
	/**
	 * http://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
	 * @return method name
	 */
	public static String getMethodName(final int depth)	{
	  final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

	  //System. out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
	  // return ste[ste.length - depth].getMethodName();  //Wrong, fails for depth = 0
	  return ste[ste.length - 1 - depth].getMethodName(); //Thank you Tom Tresansky
	}
	
	public static void printStartTest(){
		System.out.println("\n[НАЧАЛО TECTА " + getMethodName(methodDepth) +"() ]");
	}
	
	public static void printEndTest(){
		System.out.println("[КОНЕЦ TECTА " + getMethodName(methodDepth) + "() ]\n");
	}
}

class DelayedAfterUploadCommandHandler extends StorCommandHandler {
	private final int SLEEP_TIME = 100;
	
    protected void sendFinalReply(Session session) {
		System.out.println("Сервак завис на "+SLEEP_TIME+" секунды...");
        try {
			Thread.sleep(SLEEP_TIME*1000);
		} catch (InterruptedException e) {
			//e.printStackTrace();
		}
        System.out.println("проснулся");

        sendReply(session, finalReplyCode, finalReplyMessageKey, finalReplyText, null);
    }

}
