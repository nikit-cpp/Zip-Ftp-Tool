import static org.junit.Assert.*;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.command.CommandNames;
import org.mockftpserver.core.command.InvocationRecord;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.stub.command.StorCommandHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

/*
 * Здесь интеграционные тесты
 * */
public class FtpUploaderTest implements Observer {
	private static CountDownLatch updateLatch;
	
	public static CountDownLatch getUpdateLatch() {
		return updateLatch;
	}

	private static final String HOME_DIR = "/";
	
	private static final String FILE_path = "/dir";
	private static final String FILE_name = "sample";
	private static final String FILE_ext = ".txt";
	private static final String FILE_name_ext = FILE_name + FILE_ext;
	private static final String FILE_full_name = FILE_path + "/" + FILE_name_ext;
	private static final String FILE_CONTENTS = "abcdef 1234567890";
	
	private static final String login = "login";
	private static final String password = "password";

	private FtpUploader ftpUploader;
	private FakeFtpServer fakeFtpServer;

	private FileSystem fileSystem;
	
	@Test(timeout=2000)
	public void testGetListOfFile() throws IOException {
		FTPFile[] xFiles = ftpUploader.getListOfFile(FILE_path);
		assertThat(xFiles, is(not(nullValue())));
		
		// Утверждаем что у нас 1 файл
		assertThat(xFiles.length, is(1));
		assertThat(xFiles[0].getName(), is(FILE_name_ext));
	}
	
	@Test(timeout=5000)
	public void testSingleUploadToFTP() throws Exception {
		// Запись файла в ФС
		File temp = File.createTempFile(FILE_name, FILE_ext);
		System.out.println("[ТЕСТ] Создан временный файл "+temp.getAbsolutePath() + " для заливки на сервер\n");
		OutputStream os = new FileOutputStream(temp);
		os.write(FILE_CONTENTS.getBytes());
		os.flush();
		os.close();
		temp.deleteOnExit();
		
		ftpUploader.uploadToFTP(temp, FILE_path);
		
		updateLatch.await(); // Здесь приказываем честно ждать
	}

	@Before
	public void setUp() throws Exception {
		updateLatch = new CountDownLatch(1);
		
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

		ftpUploader = new FtpUploader("localhost", port, login, password);
		ftpUploader.addObserver(this);
		ftpUploader.doFtpStart();
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("tearDown()");
		//System.in.read();
		fakeFtpServer.stop();
		ftpUploader.doFtpEnd();
	}

	public void update(final Observable o, final Object arg) {
		System.out.println("update()");
		updateLatch.countDown();
	}
}

class DelayedAfterUploadCommandHandler extends StorCommandHandler {
	private final int SLEEP_TIME = 10;
	
    protected void sendFinalReply(Session session) {
		System.out.println("Сервак завис на "+SLEEP_TIME+" секунды...");
        try {
			Thread.sleep(SLEEP_TIME*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        System.out.println("проснулся");
        FtpUploaderTest.getUpdateLatch().countDown(); // "аварийное" завершение, на случай если у теста нет таймаута

        sendReply(session, finalReplyCode, finalReplyMessageKey, finalReplyText, null);
    }

}

