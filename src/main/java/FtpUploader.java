import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.eclipse.core.runtime.Path;

public class FtpUploader {
	private String server;
	private int port;
	private String userName;
	private String pass;
	private FTPClient ftpClient;

	public FtpUploader(String server, int port, String userName, String pass) {
		this.server = server;
		this.port = port;
		this.userName = userName;
		this.pass = pass;
	}

	public void doFtpStart() {
		if (ftpClient != null)
			return;

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

	public void doFtpEnd() {
		try {
			System.out.println("Разлогинивание...");
			ftpClient.logout();
		} catch (IOException e) {
			// do nothing
		} finally {
			if (ftpClient.isConnected()) {
				try {
					System.out.println("Отключение...");
					ftpClient.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
		}
	}

	@Deprecated
	public void connectToFTP() throws IOException {
		if (ftpClient != null)
			return;

		ftpClient = new FTPClient();

		ftpClient.connect(server, port);

		// http://stackoverflow.com/questions/2712967/apache-commons-net-ftpclient-and-listfiles/5183296#5183296
		// enter passive mode before you log in
		ftpClient.enterLocalPassiveMode();

		ftpClient.login(userName, pass);

		System.out.println("Подключился? " + ftpClient.isConnected());
		System.out.println("Доступен? " + ftpClient.isAvailable());
		// http://stackoverflow.com/questions/2712967/apache-commons-net-ftpclient-and-listfiles/5183296#5183296
		printStatus();
		showServerReply();
		// ftpClient.setCharset(Charset.forName("UTF-8"));
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	}

	public FTPFile[] getListOfFile(String folder) throws IOException {
		FTPClient tempFtpClient = ftpClient;
		ftpClient = null;
		connectToFTP();

		ftpClient.setListHiddenFiles(true);
		showServerReply();

		// побочное действие: меняет текущую директорию
		/*
		 * Path p = new Path(folder); p.append(folder); String s =
		 * p.toPortableString(); System.out.println("Path in getListOfFile(): "
		 * + s);
		 */
		String s = folder;

		FTPFile[] listFtpFile = ftpClient.listFiles(s);
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
		ftpCloseStub();

		dropConnection();
		ftpClient = tempFtpClient;
		System.out.println(ftpClient);
		return listFtpFile;
	}

	private void makeFtpFolder(String ftpFolder) throws IOException {
		// смена папки
		// http://stackoverflow.com/questions/4078642/create-a-folder-hierarchy-through-ftp-in-java/4079002#4079002
		ftpClient.changeWorkingDirectory("/");
		if (!ftpFolder.equals(ftpFolder)) // костыль для fake ftp
			ftpClient.makeDirectory(ftpFolder);
		ftpClient.changeWorkingDirectory(ftpFolder);
		showServerReply();

	}

	private FileInputStream fileInputStream;
	private OutputStream ftpOutStream;

	public boolean uploadToFTP(final File file, String ftpFolder)
			throws IOException {
		dropConnection();
		connectToFTP();
		fileInputStream = new FileInputStream(file);

		makeFtpFolder(ftpFolder);

		ftpOutStream = ftpClient.storeFileStream(file.getName());

		// Выходим, если файлы существуют на сервере
		if (isExists(file, ftpFolder)) {
			streamsClose();
			return false;
		}
		System.out.println("Готовность к заливке");

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

		/*
		 * System.out.println("Желаем залить файл " + file + "?"); int charcode
		 * = System.in.read(); if(!(charcode =='Y' || charcode =='y' )){
		 * streamsClose(); return false; }
		 */

		// ftpClient.setCopyStreamListener(listener);
		System.out.println("Загрузка началась ...");
		long c = Util.copyStream(fileInputStream, ftpOutStream,
				Util.DEFAULT_COPY_BUFFER_SIZE, file.length(), listener);
		System.out.printf("\n%-30S: %d\n", "Bytes sent", c);
		System.out.println("point -1");

		streamsClose();

		// судя по этому
		// http://mail-archives.apache.org/mod_mbox/commons-user/200412.mbox/%3CBAEAD6E55CF4ED4784B506D39CCD1D4131B89E@tshuscodenmbx02.ERF.THOMSON.COM%3E
		// в javadoc'е - устаревшая инфа.
		// я тупо решил проблему, убрав этот оператор
		System.out.println("point 1");
		// if (!ftpClient.completePendingCommand()) {
		// ftpCloseStub();
		// return false;
		// }

		System.out.println("point 2");
		// showServerReply();
		System.out.println("point 3");
		ftpCloseStub();
		System.out.println("point 4");
		return true;
	}

	public void printStatus() throws IOException {
		System.out.println("Статус: " + ftpClient.getStatus());
	}

	public void ftpCloseStub() throws IOException {
		// ftpClient.logout();
		// ftpClient.disconnect();
	}

	public void dropConnection() throws IOException {
		if (ftpClient == null)
			return;
		ftpClient.logout();
		ftpClient.disconnect();
		ftpClient = null;
	}

	private void streamsClose() {
		try {
			ftpOutStream.close();
			System.out.println("point 0");
			fileInputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkReply() throws RuntimeException{
		showServerReply();
		int reply = ftpClient.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.err.println("ftpClient server refused connection.");
			throw new RuntimeException("Негативный ответ сервера");
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

	public boolean isExists(File zippedFile, String ftpfolder)
			throws IOException {
		// boolean ret = true;

		FTPFile[] ftpfiles = getListOfFile(ftpfolder);
		for (FTPFile ftpFile : ftpfiles) {
			if (ftpFile.isFile()
					&& ftpFile.getName().equals(zippedFile.getName())) {
				System.out.println("Файл " + zippedFile
						+ " существует на FTP в папке " + ftpfolder);

				return true;
			}
		}
		System.out.println("Файл " + zippedFile
				+ " не существует на FTP в папке " + ftpfolder);

		return false;
	}

	public String getUrl() {
		return server;
	}
}
