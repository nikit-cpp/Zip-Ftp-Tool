import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;

public class Launcher {
	public static final String lookupFolder_=Config.getInstance().getLookupFoder();
	public static final String destFolder_ = Config.getInstance().getDestFoder();
	public static final String ftpFolder_ = Config.getInstance().getFtpFoder(); // "public_html/zaschita informacii"
	
	public static void main(String[] args) throws IOException{
		System.out.println("HELLO.");
		//System.in.read();
						
		File lookupFolder = new File(lookupFolder_);
		
		System.out.println("Проверка просматриваемой папки...");
		System.out.println("Путь: "+lookupFolder.getAbsolutePath());
		System.out.println("Существует? "+lookupFolder.exists());
		System.out.println("Папка? "+lookupFolder.isDirectory());
		
		// Архивируем
		Zipper zipper = new Zipper();
		System.out.println("Папка расположения zip-архивов: " + destFolder_);
		for(File zippableFolder : lookupFolder.listFiles()){
			if(zippableFolder.isDirectory()){
				System.out.println("Папка-Кандидат на запаковку: " + zippableFolder.getAbsolutePath());
				zipper.zip(zippableFolder, destFolder_);
			}
		}
				
		// create new filename filter
        FilenameFilter fileNameFilter = new FilenameFilter() {
           public boolean accept(File dir, String name) {
              if(name.lastIndexOf('.')>0){
                 // get last index for '.' char
                 int lastIndex = name.lastIndexOf('.');
                 
                 // get extension
                 String str = name.substring(lastIndex);
                 
                 // match path name extension
                 if(str.equals(".zip")){
                    return true;
                 }
              }
              return false;
           }
        };
        
        FtpUploader[] ftpa;
		try {
			ftpa = Config.getInstance().createFtpUploaderArray();
		
			for(FtpUploader ftp: ftpa){
				System.out.println("\nРаботаем с FTP " + ftp.getUrl());
				for(File zippedFile : lookupFolder.listFiles(fileNameFilter)){
					System.out.println("\nЗаливаем файл "+zippedFile.getName() + " на FTP...");
					System.out.println("Полный путь к файлу"+zippedFile.getAbsolutePath());
					
					if(!ftp.isExists(zippedFile, ftpFolder_)){
						boolean isLoad = ftp.uploadToFTP(zippedFile, ftpFolder_);
						System.out.println("Залит? "+isLoad);
					}else{
						System.out.println("Файл " + zippedFile.getName() + " не был залит, поскольку он уже существует на FTP.");
					}
				}
					
				//ftp.close();
			}

		} catch (ConfigurationException e) {
			System.out.println("Ошибка при загрузке списка серверов : " + e.getStackTrace());
		}
	}

}
