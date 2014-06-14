package main;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.configuration.ConfigurationException;

import controller.Controller;
import controller.Event;
import controller.Event.Events;
import session.Session;

public class Starter implements Runnable{
	public static final String lookupFolder_=Config.getInstance().getLookupFoder();
	public static final String destFolder_ = Config.getInstance().getDestFoder();
	public static final String ftpFolder_ = Config.getInstance().getFtpFoder(); // "/public_html"
		
	public Starter(){
	}
		
	public static void main(String[] args) {
		new Starter().run();
	}
	
	public void run() {
		System.out.println("HELLO.");
		//System.in.read();
						
		final File lookupFolder = new File(lookupFolder_);
		
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
        final FilenameFilter fileNameFilter = new ZipFilenameFilter();
        
        Session[] ftpUploaders; // для каждого сервера -- свой Uploadable 
		try {
			ftpUploaders = Config.getInstance().createFtpUploaderArray();
			
			Thread[] threads = new Thread[ftpUploaders.length];
			final CyclicBarrier cb = new CyclicBarrier(ftpUploaders.length+1);
			
			int i=0;
			for(; i<ftpUploaders.length; ){
				final Session ftpUploader=ftpUploaders[i];
				threads[i++] = new Thread(new Runnable(){
					
					public void run(){						
						ftpUploader.doStart();
						System.out.println("\nРаботаем с FTP " + ftpUploader.getServer());
						for(File zippedFile : lookupFolder.listFiles(fileNameFilter)){
							System.out.println("\nЗаливаем файл "+zippedFile.getName() + " на FTP...");
							System.out.println("Полный путь к файлу "+zippedFile.getAbsolutePath());
							
							ftpUploader.upload(zippedFile, ftpFolder_);
						}
							
						ftpUploader.doEnd();
						try {
							cb.await();
						} catch (InterruptedException e) {
//							e.printStackTrace();
						} catch (BrokenBarrierException e) {
//							e.printStackTrace();
						}
					}
				});
			}
			
			for(Thread thread : threads){
				System.out.println("для потока id="+thread.getId());
				// создаём окно ассоциированное с потоком
				Controller.getInstance().fireEvent(new Event(Events.NEW_PROGRESS_WINDOW, thread.getId()));
				// запускаем поток
				thread.start();
			}
			//System.in.read();
			try {
				cb.await();
			} catch (InterruptedException e) {
//				e.printStackTrace();
			} catch (BrokenBarrierException e) {
//				e.printStackTrace();
			}
			
		} catch (ConfigurationException e) {
			System.out.println("Ошибка при загрузке списка серверов : " + e.getStackTrace());
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}finally{
			Controller.getInstance().fireEvent(new Event(Events.EXIT, null));
		}
	}
}

class ZipFilenameFilter implements FilenameFilter{
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
}