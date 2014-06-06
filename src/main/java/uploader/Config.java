package uploader;
import java.io.File;
import java.util.List;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;


public class Config {
	private String filename = "config.xml";
	private static Config instance = null;
	private XMLConfiguration xmlConfig;
	
	private final String lookupFolder = "lookupFolder";
	private final String destFolder = "destFolder";
	private final String ftpFolder = "ftpFolder";
	
	// Конструктор
	private Config() throws ConfigurationException{
		File configfile = new File(filename);
		if(!configfile.exists()){
			createDefaultConfigFile();
		}else{
			xmlConfig = new XMLConfiguration(filename);
		}
	}

	public static Config getInstance(){
		if (instance==null){
			try {
				instance=new Config();
			} catch (ConfigurationException e) {
				System.out.println("Error on creating Config class");
				e.printStackTrace();
				System.exit(1);
			}
		}
		return instance;
	}
	
	public String getLookupFoder() {
		String value = xmlConfig.getString(lookupFolder);
		System.out.println(lookupFolder +":" +value);
		return value;
	}
	
	public String getDestFoder() {
		String value = xmlConfig.getString(destFolder);
		System.out.println(destFolder +":" +value);
		return value;
	}
	
	public String getFtpFoder() {
		String value = xmlConfig.getString(ftpFolder);
		System.out.println(ftpFolder +":" +value);
		return value;
	}
	
	public FtpUploader[] createFtpUploaderArray() throws ConfigurationException {
		// получить количество_серверов
		int size = xmlConfig.getList("servers.server.url").size();
		//создать массив/аррэйлист
		FtpUploader[] ftpa = new FtpUploader[size];
		
		List<HierarchicalConfiguration> servers = 
			    xmlConfig.configurationsAt("servers.server");
		System.out.println("Loading servers configuration...");
		int i = 0;
		for(HierarchicalConfiguration sub : servers){
			// sub contains all data about a single field
			try{
				String url = sub.getString("url");
				int port = sub.getInt("port");
				String login = sub.getString("login");
				String password = sub.getString("password");
				
				System.out.println("loaded url: "+url + " port: " + port + " login: "+ login);
				
			    ftpa[i++] = new FtpUploader(url, port, login, password);
			}catch(ConversionException e){
				String msg0 = e.getMessages()[0];
				String msg1 = e.getMessages()[1];
				//String msg2 = e.getMessages()[2];
				System.out.println("Ошибка считывания: для элемента:\"" + msg0 + "\" ошибка:\"" + msg1 + "\"");
				throw e;
			}
		} 
		
		
		// фор и=0; и<количество_серверов; и++
		
		return ftpa;
	}

	private void createDefaultConfigFile() throws ConfigurationException{
		xmlConfig = new XMLConfiguration();
		xmlConfig.setEncoding("UTF-8");
		xmlConfig.setFileName(filename);
		xmlConfig.setRootElementName("sample-xml-configuration");
		xmlConfig.addProperty(lookupFolder, "E:\\Мои документы\\МИРЭА\\Защита Информации");
		xmlConfig.addProperty(destFolder, "E:\\Мои документы\\МИРЭА\\Защита Информации");
		xmlConfig.addProperty(ftpFolder, "public_html");
		
		xmlConfig.addProperty("servers.server(0).url", "ftp.server1.com");
		xmlConfig.addProperty("servers.server(0).port", "21");
		xmlConfig.addProperty("servers.server(0).login", "login1");
		xmlConfig.addProperty("servers.server(0).password", "pass1");

		xmlConfig.addProperty("servers.server(1).url", "1.2.3.4");
		xmlConfig.addProperty("servers.server(1).port", "21");
		xmlConfig.addProperty("servers.server(1).login", "login2");
		xmlConfig.addProperty("servers.server(1).password", "pass2");

		xmlConfig.save();
	}
}
