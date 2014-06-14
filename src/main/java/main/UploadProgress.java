package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import controller.Controller;
import controller.Event;
import controller.Listener;

import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

public class UploadProgress implements Listener {

	JFrame frame;
	private JTextField txtFtpservernet;
	private JTextField txtFilezip;
	private JProgressBar progressBar;
	
	private long id; // id для того чтобы слушать сообщения только своего потока
	/**
	 * Create the application.
	 */
	public UploadProgress(long id) {
		initialize();
		this.id=id;
		System.out.println("created UploadProgress window for thread id = " + this.id);
		Controller.getInstance().addListener(this);
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
	
	public void show() {
		frame.setVisible(true);
	}


	// вызывается при изменении ...
	public void onEvent(final Event event) {
		if (event == null)
			return;
		if(Event.getThreadId(event)!=id)
			return;

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				switch (event.type) {
				case SERVER_CHANGED:// ... имени сервера
					txtFtpservernet.setText(Event.getServerChangedString(event));
					resetProgressBar();
					txtFilezip.setText("");
					break;
				case FILE_CHANGED: // ... файла
					txtFilezip.setText(Event.getFileChangedString(event));
					resetProgressBar();
					break;
				case PERSENT_CHANGED: // ... процента
					int proc = (int) Event.getPercentChangedDouble(event);
					setProgressBar(proc);
					break;
				default:
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
