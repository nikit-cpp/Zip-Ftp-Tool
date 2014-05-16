import java.awt.EventQueue;

import javax.swing.JFrame;

import java.awt.Window.Type;

import javax.swing.JProgressBar;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class UploadProgress implements Observer {

	private JFrame frame;
	private JTextField txtFtpservernet;
	private JTextField txtFilezip;
	private JProgressBar progressBar;
	private static Launcher launcher;
	
	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
				try {
					UploadProgress window = new UploadProgress();
					window.frame.setVisible(true);
					
					launcher = new Launcher();					
					launcher.main(null);

				} catch (Exception e) {
					e.printStackTrace();
				}
//			}
//		});
	}

	/**
	 * Create the application.
	 */
	public UploadProgress() {
		initialize();
		
		FtpUploader.observerUploadProgress=this;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("Загрузка 146%");
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

	public void update(Observable o, Object arg) {
		if(arg.getClass()==String.class){ // имя сервера
			txtFtpservernet.setText((String)arg);
			progressBar.setValue(0);
			frame.setTitle("Загрузка...");
			txtFilezip.setText("");
		}
		if(arg.getClass()==File.class){ // файл
			txtFilezip.setText(((File)arg).getAbsolutePath());
			progressBar.setValue(0);
			frame.setTitle("Загрузка...");
		}
		if(arg.getClass()==Double.class){ // процент
			int proc = ((Double)arg).intValue();
			progressBar.setValue(proc);
			frame.setTitle("Загрузка " + proc + "%");
		}
	}

}
