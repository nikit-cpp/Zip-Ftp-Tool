import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public class RemoteFile {

    public static final String USERNAME = "user";
    public static final String PASSWORD = "password";

    private String server;
    private int port;

    public String readFile(String filename) throws IOException {

        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(USERNAME, PASSWORD);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        boolean success = ftpClient.retrieveFile(filename, outputStream);
        ftpClient.disconnect();

        if (!success) {
            throw new IOException("Retrieve file failed: " + filename);
        }
        return outputStream.toString();
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setPort(int port) {
        this.port = port;
    }

    // Other methods ...
}