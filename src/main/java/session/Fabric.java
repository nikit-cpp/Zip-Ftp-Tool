package session;

import timeout.annotation.processor.TimeoutProxy;

public class Fabric {
	public static Session createFtpUploader(String server, int port, String userName, String pass){
		
		FtpSession uploader = new FtpSession(server, port, userName, pass);
		
		Session instance = (Session) TimeoutProxy.getNewProxy(uploader, Session.class); // собственно на этой строчке инстанцируется экземпляр.

		uploader.setInstance(instance);
		
		return instance;
	}
}
