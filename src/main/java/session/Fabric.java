package session;

import java.util.ArrayList;

import config.Config;
import config.Server;
import timeout.annotation.processor.TimeoutProxy;

public class Fabric {
	public static Session createFtpUploader(String server, int port, String userName, String pass){
		
		FtpSession uploader = new FtpSession(server, port, userName, pass);
		
		Session instance = (Session) TimeoutProxy.getNewProxy(uploader, Session.class); // собственно на этой строчке инстанцируется экземпляр.

		uploader.setInstance(instance);
		
		return instance;
	}
	
	public static Session[] createFtpUploaderArray() {
		ArrayList<Server> serversList = Config.getInstance().getServersList();
		Session[] ftpa = new Session[serversList.size()];
		
		for(int i=0; i<serversList.size(); i++){
			Server s = serversList.get(i);
			ftpa[i] = Fabric.createFtpUploader(s.getAdress(), s.getPort(), s.getLogin(), s.getPassword());
		} 
				
		return ftpa;
	}

}
