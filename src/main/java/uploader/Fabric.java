package uploader;

import timeout.annotation.processor.TimeoutProxy;

public class Fabric {
	public static Uploadable createFtpUploader(String server, int port, String userName, String pass){
		
		FtpUploader uploader = new FtpUploader(server, port, userName, pass);
		
		Uploadable instance = (Uploadable) TimeoutProxy.getNewProxy(uploader, Uploadable.class); // собственно на этой строчке инстанцируется экземпляр.

		uploader.setInstance(instance);
		
		return instance;
	}
}
