package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import session.messages.MType;
import session.messages.Message;

import java.awt.GridLayout;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class UploadProgress implements Observer {

	private JFrame frame;
	private JTextField txtFtpservernet;
	private JTextField txtFilezip;
	private JProgressBar progressBar;
	private static Starter launcher;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		UploadProgress window = new UploadProgress();
		try {
			// Создаём поток Launcher, и запускаем его.
			// Экземпляр window уже создан и мы можем добавлять
			// обсерверы
			launcher = new Starter(window);
			Thread dataLoader = new Thread(launcher);
			dataLoader.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		window.frame.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public UploadProgress() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("Загрузка xy%");
		frame.setBounds(100, 100, 450, 94);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		txtFilezip = new JTextField();
		txtFilezip.setEditable(false);
		txtFilezip.setText("file.zip");
		frame.getContentPane().add(txtFilezip);
		txtFilezip.setColumns(10);

		txtFtpservernet = new JTextField();
		txtFtpservernet.setEditable(false);
		txtFtpservernet.setText("ftp.server.net");
		frame.getContentPane().add(txtFtpservernet);
		txtFtpservernet.setColumns(10);

		progressBar = new JProgressBar();
		progressBar.setValue(50);
		frame.getContentPane().add(progressBar);
	}

	// вызывается при изменении ...
	public void update(Observable o, Object arg) {
		if (arg == null || arg.getClass() != Message.class)
			return;

		final Message message = (Message) arg;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				switch (message.type) {
				case SERVER_CHANGED:// ... имени сервера
					txtFtpservernet.setText(MType
							.getServerChangedString(message));
					resetProgressBar();
					txtFilezip.setText("");
					break;
				case FILE_CHANGED: // ... файла
					txtFilezip.setText(MType.getFileChangedString(message));
					resetProgressBar();
					break;
				case PERSENT_CHANGED: // ... процента
					int proc = (int) MType.getPercentChangedDouble(message);
					setProgressBar(proc);
					break;
				case EXIT:
					System.exit(0);
					break;
				}
			}
		});
	}

	private void resetProgressBar() {
		progressBar.setValue(0);
		frame.setTitle("Загрузка...");
		progressBar.setString(null);
	}

	private void setProgressBar(int value) {
		progressBar.setValue(value);
		frame.setTitle("Загрузка " + value + "%");
		progressBar.setString(String.valueOf(value));
	}
}
