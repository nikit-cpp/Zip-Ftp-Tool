package uploader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Observer;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;

import timeout.annotation.Timeout;
import timeout.annotation.processor.TimeoutInvocationHandler;
import uploader.messages.MessageEmitter;
import uploader.messages.MType;

public class FtpUploader extends MessageEmitter implements Uploadable{
	private String server;
	private int port;
	private String userName;
	private String pass;
	private FTPClient ftpClient;
	private final int timeout = 2;
		
	private MyCopyStreamListener listener = new MyCopyStreamListener();
	
	private Uploadable instance; // Аннотации будут работать, если вызывать методы через экземпляр интерфейса
	void setInstance(Uploadable instance){
		this.instance=instance;
	}
	
	/**
	 * Не вызывать напрямую, использовать фабрику!
	 * @param server
	 * @param port
	 * @param userName
	 * @param pass
	 */
	FtpUploader(String server, int port, String userName, String pass) {
		this.server = server;
		this.port = port;
		this.userName = userName;
		this.pass = pass;
	}

	public void doStart() {
		emitMessage(MType.SERVER_CHANGED, server); // уведомляем обсервера о имени сервера

		ftpClient = new FTPClient();

		try {
			System.out.println("Подключение...");
			ftpClient.connect(server, port);
			System.out.println("Connected to " + server + ".");
			checkReply();

			// //////////////////////////////////////////
			// http://stackoverflow.com/questions/2712967/apache-commons-net-ftpclient-and-listfiles/5183296#5183296
			// enter passive mode before you log in
			System.out.println("Вход в локальный пасивный режим...");
			ftpClient.enterLocalPassiveMode();
			checkReply();

			System.out.println("Логин...");
			ftpClient.login(userName, pass);
			// http://stackoverflow.com/questions/2712967/apache-commons-net-ftpclient-and-listfiles/5183296#5183296
			checkReply();

			System.out.println("Установка кодировки и бинарного режима...");
			ftpClient.setControlEncoding("UTF-8");
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			// //////////////////////////////////////////

			// After connection attempt, you should check the reply code to
			// verify
			// success.
			checkReply();
			System.out.println("Готовность к пидараче файлов");
		} catch (IOException e) {
			throw new RuntimeException("Не удалось подключицца...");
		}
	}

	/* (non-Javadoc)
	 * @see uploader.Uploadable#doFtpEnd()
	 */
	@Timeout(timeout)
	public void doEnd() {
		try {
			System.out.println("Разлогинивание...");
			ftpClient.logout();
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			if (ftpClient.isConnected()) {
				try {
					System.out.println("Отключение...");
					ftpClient.disconnect();
				} catch (IOException ioe) {
					//ioe.printStackTrace();
				}
			}
		}
	}

	FTPFile[] ftpFilesPool;
	public FTPFile[] getListOfFile(String folder) throws IOException {
		// Это переключение на отображение ТОЛЬКО скрытых файлов
		// System.out.println("Запрашиваю скрытые файлы...");
		// ftpClient.setListHiddenFiles(true);
		// checkReply();
		
		changeOrMakeFolder(folder);

		System.out.println("Запрос текущей папки: "
				+ ftpClient.printWorkingDirectory());
		checkReply();

		// возможно, в зависимости от сервера -- побочное действие: меняет
		// текущую директорию
		ftpFilesPool = ftpClient.listFiles(folder);
		System.out.println("Начало списка файлов в папке " + folder);
		for (FTPFile file : ftpFilesPool) {
			System.out.println("\"" + file.getName() + "\", " + file.getSize()
					+ " bytes");
		}
		System.out.println("Конец списка файлов на FTP в папке " + folder
				+ '\n');
		checkReply();

		System.out.println("Запрашиваю текущую папку в конце...");
		System.out.println(ftpClient.printWorkingDirectory());
		checkReply();

		return ftpFilesPool;
	}

	private final String ROOT_DIR = "/";

	private void changeOrMakeFolder(String ftpFolder) {
		// смена папки
		// http://stackoverflow.com/questions/4078642/create-a-folder-hierarchy-through-ftp-in-java/4079002#4079002
		System.out.println("Изменение с возможным созданием директории "
				+ ftpFolder);
		
		try{
			ftpClient.changeWorkingDirectory(ROOT_DIR);
			if (!ftpFolder.equals(ROOT_DIR))
				ftpClient.makeDirectory(ftpFolder);
			ftpClient.changeWorkingDirectory(ftpFolder);
			checkReply();
		}catch(Exception e){
			System.err.println("Ошибка при изменении или создании директории на сервере");
		}
	}

	private FileInputStream fileInputStream;
	private OutputStream ftpOutStream;

	/* (non-Javadoc)
	 * @see uploader.Uploadable#uploadToFTP(java.io.File, java.lang.String)
	 */
	public boolean uploadToFTP(final File file, String ftpFolder) {
		emitMessage(MType.FILE_CHANGED, file.getAbsolutePath()); // уведомляем обсервера о файле

		if(TimeoutInvocationHandler.timeoutElapsed){
			reconnect();
		}
			
//		changeOrMakeFolder(ftpFolder);

		// Выходим, если файлы существуют на сервере
		if (isExists(file, ftpFolder)) {
			return false;
		}

		try {
			fileInputStream = new FileInputStream(file);
			System.out.println("Открытие потока отправки файла...");
			ftpOutStream = ftpClient.storeFileStream(file.getName());
			showServerReply();
			// checkReply(); // Вроде здесь нельзя(150), т. к. после открытия
			// потока сервер ждёт data connection

			System.out.println("Готовность к заливке");

			System.out.println("Загрузка началась ...");
			long c = Util.copyStream(fileInputStream, ftpOutStream,
					Util.DEFAULT_COPY_BUFFER_SIZE, file.length(), listener);
			System.out.printf("\n%-30S: %d\n", "Отправлено байт", c);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			streamsClose();
		}

		instance.checkCompleted();
		
		return true;
	}
	
	@Timeout(timeout)
	public void checkCompleted(){
		System.out.println("completePendingCommand()");
		try {
			if (!ftpClient.completePendingCommand()) {
				;
			}
			checkReply();
		} catch (IOException e) {
		}
	}

	public void reconnect() {
		TimeoutInvocationHandler.timeoutElapsed=false;
		instance.doEnd();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		instance.doStart();
	}

	public void printStatus() throws IOException {
		System.out.println("Статус: " + ftpClient.getStatus());
	}

	private void streamsClose() {
		System.out.println("Закрытие потоков...");
		try {
			if (ftpOutStream != null)
				ftpOutStream.close();
			if (fileInputStream != null)
				fileInputStream.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Бросает RuntimeException, если ответ сервера не положиетльный.
	 * Положительные ответы имеют вид 2xy.
	 * 
	 * @throws RuntimeException
	 */
	public void checkReply() throws RuntimeException {
		showServerReply();
		final String replyStr = ftpClient.getReplyString();
		int reply = ftpClient.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.err.println("Негативный ответ сервера.\"" + replyStr + "\" Отключено.");
			throw new RuntimeException("Негативный ответ сервера: " + replyStr);
		}
	}

	// http://stackoverflow.com/questions/15174271/apache-commons-net-ftp-ftpclient-not-uploading-the-file-to-the-required-folder/15179429#15179429
	private void showServerReply() {
		String[] replies = ftpClient.getReplyStrings();
		if (replies != null && replies.length > 0) {
			for (String aReply : replies) {
				System.out.println("SERVER: " + aReply);
			}
		}
	}

	public boolean isExists(File zippedFile, String ftpfolder) {
		final String localFileName = zippedFile.getName();
		System.out.println("Проверка наличия файла " + localFileName
				+ " в " + ftpfolder);
		try{			
			for(int i=0; i<2; i++){
				if(ftpFilesPool==null)
					ftpFilesPool= getListOfFile(ftpfolder); // инициализация пула
				for (FTPFile ftpFile : ftpFilesPool) {
					if (ftpFile.isFile() && ftpFile.getName().equals(localFileName)) { // ищем среди них нужный нам файл по имени
						System.out.println("Файл " + localFileName
								+ " существует "+ (i==0 ? "в пуле " : "на FTP ") +" в папке " + ftpfolder);
						return true;
					}
				}
				System.out.println("Файл " + localFileName	+ " не существует " + (i==0 ? "в пуле " : "на FTP ") + " в папке " + ftpfolder);
				
				if(i==0) // не нашли файл -- сброс пула
					ftpFilesPool=null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public String getServer() {
		return server;
	}
		
	public void addObserver(Observer o){
		super.addObserver(o);
		listener.addObserver(o);
	}
} // FtpUploader

class MyCopyStreamListener extends MessageEmitter implements CopyStreamListener {
	public void bytesTransferred(long totalBytesTransferred,
			int bytesTransferred, long streamSize) {
		double persent = totalBytesTransferred * 100.0 / streamSize;
		System.out.printf("\r%-30S: %d / %d байт (%f %%)", "Sent",
				totalBytesTransferred, streamSize, persent);
		
		emitMessage(MType.PERSENT_CHANGED, persent); // уведомляем обсервера об изменении процента
	}

	public void bytesTransferred(CopyStreamEvent event) {
	}
}