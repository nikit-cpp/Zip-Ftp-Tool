import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;

public class Launcher {
	public static final String lookupFolder=Config.getInstance().getLookupFoder();
	public static final String destFolder = Config.getInstance().getDestFoder();
	public static final String ftpFolder = Config.getInstance().getFtpFoder(); // "public_html/zaschita informacii"
	
	public static void main(String[] args) throws IOException{
		System.out.println("HELLO.");
						
		File rootDir = new File(lookupFolder);
		System.out.println("�������� ��������������� �����...");
		System.out.println("����: "+rootDir.getAbsolutePath());
		System.out.println("����������? "+rootDir.exists());
		System.out.println("�����? "+rootDir.isDirectory());
		
		Zipper zipper = new Zipper();
		System.out.println("����� ������������ zip-�������: " + destFolder);
		for(File zippableFolder : rootDir.listFiles()){
			if(zippableFolder.isDirectory()){
				System.out.println("�����-�������� �� ���������: " + zippableFolder.getAbsolutePath());
				zipper.zip(zippableFolder, destFolder);
			}
		}
		
		File lookup = new File(lookupFolder);
		
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
			ftpa = Config.getInstance().createFtpWorkerArray();
		
			for(FtpUploader ftp: ftpa){
				System.out.println("\n�������� � FTP " + ftp.getUrl());
				for(File zippedFile : lookup.listFiles(fileNameFilter)){
					System.out.println("\n�������� ���� "+zippedFile.getName() + " �� FTP...");
					System.out.println("������ ���� � �����"+zippedFile.getAbsolutePath());
					
					if(!ftp.isExists(zippedFile, ftpFolder)){
						boolean isLoad = ftp.uploadToFTP(zippedFile, ftpFolder);
						System.out.println("�����? "+isLoad);
					}else{
						System.out.println("���� " + zippedFile.getName() + " �� ��� �����, ��������� �� ��� ���������� �� FTP.");
					}
				}
					
				//ftp.close();
			}

		} catch (ConfigurationException e) {
			System.out.println("������ ��� �������� ������ �������� : " + e.getStackTrace());
		}
	}

}
