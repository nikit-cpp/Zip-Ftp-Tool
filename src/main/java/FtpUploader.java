import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;

public class FtpUploader {
	private String domain;
	private int port;
	private String userName;
	private String pass;
	private final FTPClient ftpClient;

	public FtpUploader(String domain, int port, String userName, String pass) {
		this.domain = domain;
		this.port = port;
		this.userName = userName;
		this.pass = pass;
		this.ftpClient = new FTPClient();
		// ftpClient.setCharset(Charset.forName("UTF-8"));
		ftpClient.setControlEncoding("UTF-8");
	}

	public FTPClient connectToFTP(String address, int port, String username,
			String password) throws IOException {
		// FTPClient ftpClient = new FTPClient();
		ftpClient.connect(address, port);
		ftpClient.login(username, password);
		System.out.println("Подключился? " + ftpClient.isConnected());
		System.out.println("Доступен? " + ftpClient.isAvailable());
		// http://stackoverflow.com/questions/2712967/apache-commons-net-ftpclient-and-listfiles/5183296#5183296
		ftpClient.enterLocalPassiveMode();
		// ftpClient.ent
		printStatus();
		return ftpClient;
	}

	public FTPFile[] getListOfFile(String folder) throws IOException {
		FTPClient ftpClient = connectToFTP(domain, port, userName, pass);
		ftpClient.setListHiddenFiles(true);
		// побочное действие: меняет текущую директорию
		FTPFile[] listFtpFile = ftpClient.listFiles("/"+folder);
		System.out.println("Список файлов на FTP в папке " + folder);
		for (FTPFile ftpFile1 : listFtpFile) {
			System.out.println("Name - \"" + ftpFile1.getName().toString()
					+ "\" " + "Size - " + ftpFile1.getSize() + "Link - "
					+ /*
					 * ftpFile1.getLink() != null ?
					 * ftpFile1.getLink().toString() :
					 */"" + "Type - " + ftpFile1.getType());
		}
		System.out.println("Конец списка файлов на FTP в папке " + folder);
		showServerReply();
		close();
		return listFtpFile;
	}

	private void changeOrMake(String ftpFolder) throws IOException {
		// смена папки
		// http://stackoverflow.com/questions/4078642/create-a-folder-hierarchy-through-ftp-in-java/4079002#4079002
		ftpClient.changeWorkingDirectory("/");
		ftpClient.makeDirectory(ftpFolder);
		ftpClient.changeWorkingDirectory(ftpFolder);
		showServerReply();

	}

	public boolean uploadToFTP(final File file, String ftpFolder)
			throws IOException {
		FTPClient ftpClient = connectToFTP(domain, port, userName, pass);
		FileInputStream fileInputStream = new FileInputStream(file);

		changeOrMake(ftpFolder);

		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
		OutputStream ftpOutStream = ftpClient.storeFileStream(file.getName());

		CopyStreamListener listener = new CopyStreamListener() {
			public void bytesTransferred(long totalBytesTransferred,
					int bytesTransferred, long streamSize) {
				double persent = totalBytesTransferred * 100.0 / streamSize;
				System.out.printf("\r%-30S: %d / %d (%f %%)", "Sent",
						totalBytesTransferred, streamSize, persent);
			}

			public void bytesTransferred(CopyStreamEvent event) {
			}
		};

		// ftpClient.setCopyStreamListener(listener);
		System.out.println("Загрузка началась ...");

		long c = Util.copyStream(fileInputStream, ftpOutStream,
				Util.DEFAULT_COPY_BUFFER_SIZE, file.length(), listener);
		System.out.printf("\n%-30S: %d\n", "Bytes sent", c);
		System.out.println("point -1");
		ftpOutStream.close();
		System.out.println("point 0");
		fileInputStream.close();

		//boolean isUploaded = // ftpClient.storeFile(file.getName(),
								// fileInputStream);

		//showServerReply();
		
		// storeFileStream() требует вызова completePendingCommand() после всего
		// 150 - это норма (c) Е. Малышева. А если серьёзно, то http://stackoverflow.com/questions/14307898/apache-commons-net-completependingcommand-returning-false/15915879#15915879
		
		// Закомментировал, потому что тупой sitescopy.ru не может нормально отдать запрос
		//int r;
		//if((r = ftpClient.getReply())!=150){
			System.out.println("point 1");
			if (!ftpClient.completePendingCommand()) {
				close();
				System.err.println("File transfer failed.");
				System.exit(1);
			}
			
		//}
		//System.out.println("reply: " + r);
		System.out.println("point 2");
//		showServerReply();
		System.out.println("point 3");
		close();
		System.out.println("point 4");
		return true;
	}

	public void printStatus() throws IOException {
		System.out.println("Статус: " + ftpClient.getStatus());
	}

	public void close() throws IOException {
		ftpClient.logout();
		ftpClient.disconnect();
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

	public boolean isExists(File zippedFile, String ftpfolder)
			throws IOException {
		//boolean ret = true;
		
		FTPFile[] ftpfiles = getListOfFile(ftpfolder);
		for (FTPFile ftpFile : ftpfiles) {
			if (ftpFile.isFile()
					&& ftpFile.getName().equals(zippedFile.getName())) {
				System.out.println("Файл " + zippedFile
						+ " существует на FTP в папке " + ftpfolder);
				
				return true;
			}
		}
		System.out.println("Файл " + zippedFile + " не существует на FTP в папке "
				+ ftpfolder);
		
		return false;
	}

	public String getUrl() {
		return domain;
	}
}
