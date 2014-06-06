package uploader;

import java.io.File;
import java.util.Observer;

public interface Uploadable {

	public abstract void doStart();

	public abstract void doEnd();

	public abstract boolean uploadToFTP(File file, String ftpFolder);
	
	public abstract String getServer();
	
	public abstract void addObserver(Observer o);

}