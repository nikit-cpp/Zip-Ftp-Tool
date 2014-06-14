package session;

import java.io.File;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPFile;


public interface Session {

	public abstract void doStart();

	public abstract void doEnd();

	public abstract boolean upload(File file, String remoteFolder);
	
	public abstract String getServer();
	
	public boolean checkCompleted();

	/**
	 * Используется только в тесте
	 */
	public abstract FTPFile[] getFtpFiles(String remoteFolder) throws IOException;
}