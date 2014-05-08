import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import java.io.IOException;
import java.util.List;

public class FtpUploaderTest {
    private static final String HOME_DIR = "/";
    private static final String FILE = "/dir/sample.txt";
    private static final String CONTENTS = "abcdef 1234567890";

    private RemoteFile remoteFile;
    private FakeFtpServer fakeFtpServer;

    @Test
    public void testReadFile() throws Exception {
        String contents = remoteFile.readFile(FILE);
        assertEquals("contents", CONTENTS, contents);
    }

    @Test
    public void testReadFileThrowsException() {
        try {
            remoteFile.readFile("NoSuchFile.txt");
            fail("Expected IOException");
        }
        catch (IOException expected) {
            // Expected this
        }
    }

    @Before
    public void setUp() throws Exception {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.setServerControlPort(0);  // use any free port

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new FileEntry(FILE, CONTENTS));
        fakeFtpServer.setFileSystem(fileSystem);

        UserAccount userAccount = new UserAccount(RemoteFile.USERNAME, RemoteFile.PASSWORD, HOME_DIR);
        fakeFtpServer.addUserAccount(userAccount);

        fakeFtpServer.start();
        int port = fakeFtpServer.getServerControlPort();

        remoteFile = new RemoteFile();
        remoteFile.setServer("localhost");
        remoteFile.setPort(port);
    }

    @After
    public void tearDown() throws Exception {
        fakeFtpServer.stop();
    }
}
