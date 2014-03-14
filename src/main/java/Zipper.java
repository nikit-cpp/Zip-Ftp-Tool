import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Zipper {
	// http://www.quizful.net/post/java-zip-archiving
	public void zip(File zippableFolder, String destFolder) throws IOException {
		System.out.println("\nzip(): ������ " + zippableFolder + "; � "+destFolder);
		final String filename = destFolder+"\\"+zippableFolder.getName()+".zip";
		System.out.println("��� ����� zip-������: " + filename);
		if(new File(filename).exists()){
			System.out.println("������� �� ������� ���������, �. �. zip-����� ��� ����������.\n");
			return;
		}
		
        FileOutputStream fout = new FileOutputStream(filename);
        ZipOutputStream zout = new ZipOutputStream(fout);
        byte[] buf = new byte[1024]; 
        
        for(File zippableFile : zippableFolder.listFiles()){
        	if(zippableFile.isDirectory()) continue;
        	System.out.println("���� ��� �������������: " + zippableFile);
            ZipEntry ze = new ZipEntry(zippableFile.getName());//��� ����� - ��� ����� � ������
            zout.putNextEntry(ze);
            //�������� ������ � ����� zout
            FileInputStream in = new FileInputStream(zippableFile); 
            int len; 
            while ((len = in.read(buf)) > 0) { 
             zout.write(buf, 0, len); 
            } 
            zout.closeEntry();
            in.close();
        }
        zout.close();
        System.out.println("zip() END\n");
	}

}
