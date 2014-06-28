package config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import controller.Controller;
import controller.Event;
import controller.Event.Events;


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
				readServers();
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
	
	synchronized public void reset(Settings e){
		set(e, defaults.get(e));
	}
	
	public void set(Settings e, Object object) {
		System.out.println("Config: set \"" + object + "\" " + e);
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
		Controller.getInstance().fireEvent(new Event(Events.CONFIG_CHANGED, null));
	}
	
	private ArrayList<Server> serversList = new ArrayList<Server>();
//	public ArrayList<Server> getServersList() {
//		if(serversList.size()==0)
//			readServers();
//		return serversList;
//	}

	synchronized public int getServersCount(){
		return serversList.size();
	}
	
	private boolean checkServerIndex(int index){
		if(index>=serversList.size() || index<0)
			return false;
		return true;
	}
	
	synchronized public Server getServer(int index){
		if(!checkServerIndex(index))
			return null;
		return serversList.get(index);
	}
	
	synchronized public void setServer(int index, Server server){
		if(!checkServerIndex(index) || server==null)
			return;
		serversList.set(index, server);
	}
	
	public synchronized void removeServer(int index) {
		if(!checkServerIndex(index))
			return;
		serversList.remove(index);
		Controller.getInstance().fireEvent(new Event(Events.SERVERS_LIST_CHANGED, null));
	}
	
	public synchronized void addServer(Server server) {
		serversList.add(server);
		Controller.getInstance().fireEvent(new Event(Events.SERVERS_LIST_CHANGED, null));
	}
	
	/**
	 * Считывает серверы из xmlConfig в serversList.
	 */
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
	
	/**
	 * Сохраняет серверы из serversList в xmlConfig.
	 */
	private void saveServers(){
		System.out.println("saveServers();");
		for(int i=0; i<serversList.size(); ++i){
			Server server=serversList.get(i);
			xmlConfig.setProperty(buildPropertyString(i)+".url", server.getAdress());
			xmlConfig.setProperty(buildPropertyString(i)+".port", String.valueOf(server.getPort()));
			xmlConfig.setProperty(buildPropertyString(i)+".login", server.getLogin());
			xmlConfig.setProperty(buildPropertyString(i)+".password", server.getPassword());
		}
	}
	
	private String buildPropertyString(int i){
		return "servers.server("+i+")";
	}

	private void createDefaultConfigFile() throws ConfigurationException{
		xmlConfig = new XMLConfiguration();
		xmlConfig.setEncoding("UTF-8");
		xmlConfig.setFileName(filename);
		xmlConfig.setRootElementName("sample-xml-configuration");

		reset(Settings.DEST_FOLDER);
		reset(Settings.FTP_FOLDER);
		reset(Settings.LOOKUP_FOLDER);
		reset(Settings.IS_FTP_FILES_POOL);

		addServer(new Server("ftp.server1.com", 21, "login1", "password1"));
		addServer(new Server("1.2.3.4", 21, "login2", "password2"));

		saveConfig();
	}
	
	public void saveConfig(){
		try {
			saveServers();
			xmlConfig.save();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}

