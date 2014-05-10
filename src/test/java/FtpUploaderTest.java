import static org.junit.Assert.*;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/*
 * Здесь интеграционные тесты
 * */
public class FtpUploaderTest {
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
	
	@Ignore
	@Test(timeout=2000)
	public void testMultipleUploadToFTP() throws IOException {
		File temp = File.createTempFile(FILE_name, FILE_ext);
		System.out.println(temp.getAbsolutePath());
				
		OutputStream os = new FileOutputStream(temp);
		os.write(FILE_CONTENTS.getBytes());
		os.close();

		ftpUploader.uploadToFTP(temp, HOME_DIR);
		ftpUploader.dropConnection();
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

		fakeFtpServer.start();
		int port = fakeFtpServer.getServerControlPort();
		System.out.println("fake Server port:" + port);

		ftpUploader = new FtpUploader("localhost", port, login, password);
		ftpUploader.doFtpStart();
	}

	@After
	public void tearDown() throws Exception {
		//System.in.read();
		fakeFtpServer.stop();
		ftpUploader.doFtpEnd();
	}
}
