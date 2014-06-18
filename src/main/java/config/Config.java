package config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;


public class Config {
	private static Config instance = null;
	
	//@Inject
	private String filename = "config.xml";
	
	//@Inject
	private XMLConfiguration xmlConfig;
	
	private HashMap<Settings, Object> defaults = new HashMap<Settings, Object>();
	
	// Конструктор
	private Config(){
		defaults.put(Settings.LOOKUP_FOLDER, "E:\\Мои документы\\МИРЭА\\Защита Информации");
		defaults.put(Settings.DEST_FOLDER, "E:\\Мои документы\\МИРЭА\\Защита Информации");
		defaults.put(Settings.FTP_FOLDER, "/public_html");
		defaults.put(Settings.IS_FTP_FILES_POOL, true);
		
		try{
			File configfile = new File(filename);
		if(!configfile.exists()){
			createDefaultConfigFile();
		}else{
			xmlConfig = new XMLConfiguration(filename);
		}
		}catch(ConfigurationException e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	synchronized public static Config getInstance(){
		if (instance==null){
			instance=new Config();
		}
		return instance;
	}
	
	synchronized public String getString(Settings e){
		return xmlConfig.getString(e.toString());
	}
	
	synchronized public boolean getBoolean(Settings e){
		return xmlConfig.getBoolean(e.toString());
	}
	
	synchronized void reset(Settings e){
		set(e, defaults.get(e));
	}
	
	private void set(Settings e, Object object) {
		switch(e){
		case DEST_FOLDER:
		case FTP_FOLDER:
		case LOOKUP_FOLDER:
			xmlConfig.setProperty(e.toString(), (String)object);
			break;
		case IS_FTP_FILES_POOL:
			xmlConfig.setProperty(e.toString(), (Boolean)object);
			break;
		default:
			break;
		}
	}
	
	ArrayList<Server> serversList = new ArrayList<Server>();
	public ArrayList<Server> getServersList() {
		if(serversList.size()==0)
			readServers();
		return serversList;
	}

	private void readServers() {
		// получить количество_серверов
		//int size = xmlConfig.getList("servers.server.url").size();
		
		List<HierarchicalConfiguration> servers = xmlConfig.configurationsAt("servers.server");
		System.out.println("Loading servers configuration...");
		for(HierarchicalConfiguration sub : servers){
			// sub contains all data about a single field
			try{
				Server server = new Server(sub.getString("url"), sub.getInt("port"), sub.getString("login"), sub.getString("password"));
				serversList.add(server);
			}catch(ConversionException e){
				String msg0 = e.getMessages()[0];
				String msg1 = e.getMessages()[1];
				//String msg2 = e.getMessages()[2];
				System.out.println("Ошибка считывания: для элемента:\"" + msg0 + "\" ошибка:\"" + msg1 + "\"");
				throw e;
			}
		} 
	}

	private void createDefaultConfigFile() throws ConfigurationException{
		xmlConfig = new XMLConfiguration();
		xmlConfig.setEncoding("UTF-8");
		xmlConfig.setFileName(filename);
		xmlConfig.setRootElementName("sample-xml-configuration");
//		xmlConfig.addProperty(lookupFolder, "E:\\Мои документы\\МИРЭА\\Защита Информации");
//		xmlConfig.addProperty(destFolder, "E:\\Мои документы\\МИРЭА\\Защита Информации");
//		xmlConfig.addProperty(ftpFolder, "public_html");
//		xmlConfig.addProperty(isFtpFilesPool, "true");
//		
//		xmlConfig.addProperty("servers.server(0).url", "ftp.server1.com");
//		xmlConfig.addProperty("servers.server(0).port", "21");
//		xmlConfig.addProperty("servers.server(0).login", "login1");
//		xmlConfig.addProperty("servers.server(0).password", "pass1");
//
//		xmlConfig.addProperty("servers.server(1).url", "1.2.3.4");
//		xmlConfig.addProperty("servers.server(1).port", "21");
//		xmlConfig.addProperty("servers.server(1).login", "login2");
//		xmlConfig.addProperty("servers.server(1).password", "pass2");

		xmlConfig.save();
	}
	
	public void saveConfig(){
		try {
			xmlConfig.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}

