package main;

import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Window.Type;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.AbstractListModel;

import org.apache.commons.configuration.XMLConfiguration;

public class SettingsWindow {

	private JFrame frame;
	private JTextField txtE;
	private JTextField txtE_1;
	private JTextField txtpublichtml;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsWindow window = new SettingsWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SettingsWindow() {
		initialize();
	}

	// наша модель
	private DefaultListModel<String> dlm;
	private JTextField txtServernet;
	private JTextField txtLogin;
	private JTextField txtPassword;
	private JTextField txtPort;
	private JTextField txtDisplayName;
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Настройки");
		frame.setResizable(false);
		frame.setType(Type.UTILITY);
		frame.setBounds(100, 100, 453, 504);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnNewButton = new JButton("lookupFolder");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		dlm = new DefaultListModel<String>();
		dlm.add(0, "FTP-Сервер1");
		dlm.add(0, "FTP-Сервер2");
		dlm.add(0, "WebDAV-сервер");

		JList<String> list = new JList<String>();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"ftp1", "ftp2", "webdav"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setBounds(10, 171, 371, 146);
		frame.getContentPane().add(list);
		
		btnNewButton.setBounds(339, 50, 95, 23);
		frame.getContentPane().add(btnNewButton);
		
		txtE = new JTextField();
		txtE.setText("C:\\Мои документы\\МИРЭА\\Защита Информации");
		txtE.setBounds(10, 51, 298, 20);
		frame.getContentPane().add(txtE);
		txtE.setColumns(10);
		
		txtE_1 = new JTextField();
		txtE_1.setText("C:\\Мои документы\\МИРЭА\\Защита Информации");
		txtE_1.setColumns(10);
		txtE_1.setBounds(10, 82, 298, 20);
		frame.getContentPane().add(txtE_1);
		
		JButton btnDestfolder = new JButton("destFolder");
		btnDestfolder.setBounds(339, 81, 95, 23);
		frame.getContentPane().add(btnDestfolder);
		
		txtpublichtml = new JTextField();
		txtpublichtml.setText("/public");
		txtpublichtml.setColumns(10);
		txtpublichtml.setBounds(10, 114, 298, 20);
		frame.getContentPane().add(txtpublichtml);
		
		JButton btnFtpfolder = new JButton("ftpFolder");
		btnFtpfolder.setBounds(339, 113, 95, 23);
		frame.getContentPane().add(btnFtpfolder);
		
		JCheckBox checkBox = new JCheckBox("Включить кэширование информации о файлах на FTP");
		checkBox.setBounds(34, 141, 400, 23);
		frame.getContentPane().add(checkBox);		
		
		JButton button = new JButton("+");
		button.setBounds(391, 171, 43, 36);
		frame.getContentPane().add(button);
		
		JButton button_1 = new JButton("^");
		button_1.setEnabled(false);
		button_1.setBounds(283, 328, 46, 143);
		frame.getContentPane().add(button_1);
		
		JButton button_2 = new JButton("-");
		button_2.setEnabled(false);
		button_2.setBounds(391, 218, 43, 36);
		frame.getContentPane().add(button_2);
		
		JButton btnC = new JButton("");
		btnC.setBounds(312, 50, 17, 20);
		frame.getContentPane().add(btnC);
		
		JButton button_3 = new JButton("");
		button_3.setBounds(312, 81, 17, 20);
		frame.getContentPane().add(button_3);
		
		JButton button_4 = new JButton("");
		button_4.setBounds(312, 113, 17, 20);
		frame.getContentPane().add(button_4);
		
		JButton button_5 = new JButton("");
		button_5.setBounds(10, 144, 17, 20);
		frame.getContentPane().add(button_5);
		
		txtServernet = new JTextField();
		txtServernet.setEditable(false);
		txtServernet.setText("server.net");
		txtServernet.setBounds(10, 359, 263, 20);
		frame.getContentPane().add(txtServernet);
		txtServernet.setColumns(10);
		
		txtLogin = new JTextField();
		txtLogin.setEditable(false);
		txtLogin.setText("login");
		txtLogin.setColumns(10);
		txtLogin.setBounds(10, 420, 263, 20);
		frame.getContentPane().add(txtLogin);
		
		txtPassword = new JTextField();
		txtPassword.setEditable(false);
		txtPassword.setText("password");
		txtPassword.setColumns(10);
		txtPassword.setBounds(10, 451, 263, 20);
		frame.getContentPane().add(txtPassword);
		
		txtPort = new JTextField();
		txtPort.setEditable(false);
		txtPort.setText("port");
		txtPort.setBounds(10, 390, 86, 20);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setEnabled(false);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"ftp", "webDav"}));
		comboBox.setBounds(106, 390, 167, 20);
		frame.getContentPane().add(comboBox);
		
		JButton btnNewButton_1 = new JButton("Завершить");
		btnNewButton_1.setBounds(343, 448, 91, 23);
		frame.getContentPane().add(btnNewButton_1);
		
		txtDisplayName = new JTextField();
		txtDisplayName.setEditable(false);
		txtDisplayName.setText("display name");
		txtDisplayName.setColumns(10);
		txtDisplayName.setBounds(10, 328, 263, 20);
		frame.getContentPane().add(txtDisplayName);
		
		JButton btnV = new JButton("...");
		btnV.setEnabled(false);
		btnV.setBounds(391, 265, 43, 36);
		frame.getContentPane().add(btnV);
		
		XMLConfiguration xmlConfig = new XMLConfiguration();
		xmlConfig.setProperty("key", "value1");
		System.out.println(xmlConfig.getString("key"));
		
		xmlConfig.setProperty("key", "value2");
		System.out.println(xmlConfig.getString("key"));

	}
}
