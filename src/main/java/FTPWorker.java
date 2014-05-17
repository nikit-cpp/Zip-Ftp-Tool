import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPFile;

public interface FTPWorker {

	public abstract void doFtpStart();

	public abstract void doFtpEnd();

	public abstract FTPFile[] getListOfFile(String folder) throws IOException;

	public abstract boolean uploadToFTP(File file, String ftpFolder);

	public abstract boolean isExists(File zippedFile, String ftpfolder);

}