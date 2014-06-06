package uploader;

import timeout.annotation.processor.TimeoutProxy;

public class Fabric {	
	public static Uploadable createFtpUploader(String server, int port, String userName, String pass){
		Uploadable instance = (Uploadable) TimeoutProxy.getNewProxy(
				new FtpUploader(server, port, userName, pass), Uploadable.class); // собственно на этой строчке инстанцируется экземпляр.

		return instance;
	}
}
