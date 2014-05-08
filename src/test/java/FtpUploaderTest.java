import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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

public class FtpUploaderTest {
	private static final String HOME_DIR = "/";
	
	private static final String FILE_path = "/dir/";
	private static final String FILE_name = "sample";
	private static final String FILE_ext = ".txt";
	private static final String FILE_full_name = FILE_path + FILE_name + FILE_ext;
		
	private static final String FILE_CONTENTS = "abcdef 1234567890";
	private static final String login = "login";
	private static final String password = "password";

	private FtpUploader ftpUploader;
	private FakeFtpServer fakeFtpServer;

	private FileSystem fileSystem;

	@Test(timeout=2000)
	public void testUploadToFTP() throws IOException {
		File temp = File.createTempFile(FILE_name, FILE_ext);
		System.out.println(temp.getAbsolutePath());
				
		OutputStream os = new FileOutputStream(temp);
		os.write(FILE_CONTENTS.getBytes());
		os.close();

		ftpUploader.uploadToFTP(temp, HOME_DIR);
		ftpUploader.ftpClose2();
	}

	//@Before
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

		ftpUploader = new FtpUploader("localhost", port, login, password);
	}
	
	@Before
	public void setUp2() throws Exception {
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

		ftpUploader = new FtpUploader("localhost", port, login, password);
	}

	@After
	public void tearDown() throws Exception {
		fakeFtpServer.stop();
	}
}
