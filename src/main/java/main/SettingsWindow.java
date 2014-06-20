package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.Window.Type;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.AbstractListModel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import config.Config;
import config.Server;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
		btnAdd = new JButton("+");
		btnSave = new JButton("^");
		btnDel = new JButton("-");
		btnEdit = new JButton("...");
		ServerListModel slm = new ServerListModel();
		listServers = new JList(slm);
		initialize();
	}

	private JTextField txtAdress;
	private JTextField txtLogin;
	private JTextField txtPassword;
	private JTextField txtPort;

	private final JButton btnAdd;
	private final JButton btnSave;
	private final JButton btnDel;
	private final JButton btnEdit;
	private final JList<String> listServers;
	
	private JPopupMenu popup;
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
		
		// получаем всплывающее меню
		popup = createPopupMenu();

		
		btnAdd.setBounds(391, 171, 43, 36);
		frame.getContentPane().add(btnAdd);

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// тут сохранение в модель...
				
				btnSave.setEnabled(false);
				lockServerinfo();
				unlockServerListManipulatebuttons();
				listServers.setEnabled(true);
			}
		});

		btnSave.setEnabled(false);
		btnSave.setBounds(283, 328, 46, 143);
		frame.getContentPane().add(btnSave);
		
		
		btnDel.setEnabled(false);
		btnDel.setBounds(391, 218, 43, 36);
		frame.getContentPane().add(btnDel);

		JButton btnNewButton = new JButton("lookupFolder");

		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				unlockServerinfo();
				btnSave.setEnabled(true);
				lockServerListManipulatebuttons();
				listServers.setEnabled(false);
			}
		});
		
		btnEdit.setEnabled(false);
		btnEdit.setBounds(391, 265, 43, 36);
		frame.getContentPane().add(btnEdit);
				
		listServers.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selected = ((JList)arg0.getSource()).getSelectedIndex();
				populateServerInfo(selected);
				unlockServerListManipulatebuttons();
			}
		});
		
		listServers.setBounds(10, 171, 371, 146);
		frame.getContentPane().add(listServers);

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

		JCheckBox checkBox = new JCheckBox(
				"Включить кэширование информации о файлах на FTP");
		checkBox.setBounds(34, 141, 400, 23);
		frame.getContentPane().add(checkBox);

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

		txtAdress = new JTextField();
		txtAdress.addMouseListener(new ML());
		txtAdress.setEditable(false);
		txtAdress.setText("server.net");
		txtAdress.setBounds(10, 359, 263, 20);
		frame.getContentPane().add(txtAdress);
		txtAdress.setColumns(10);

		txtLogin = new JTextField();
		txtLogin.addMouseListener(new ML());
		txtLogin.setEditable(false);
		txtLogin.setText("login");
		txtLogin.setColumns(10);
		txtLogin.setBounds(10, 420, 263, 20);
		frame.getContentPane().add(txtLogin);

		txtPassword = new JTextField();
		txtPassword.addMouseListener(new ML());
		txtPassword.setEditable(false);
		txtPassword.setText("password");
		txtPassword.setColumns(10);
		txtPassword.setBounds(10, 451, 263, 20);
		frame.getContentPane().add(txtPassword);

		txtPort = new JTextField();
		txtPort.addMouseListener(new ML());
		txtPort.setEditable(false);
		txtPort.setText("port");
		txtPort.setBounds(10, 390, 86, 20);
		frame.getContentPane().add(txtPort);
		txtPort.setColumns(10);

		JButton btnNewButton_1 = new JButton("Завершить");
		btnNewButton_1.setBounds(343, 448, 91, 23);
		frame.getContentPane().add(btnNewButton_1);
	}
	
	private void populateServerInfo(int serverIndex){
		Server server = Config.getInstance().getServer(serverIndex);
		txtAdress.setText(server.getAdress());
		txtLogin.setText(server.getLogin());
		txtPassword.setText(server.getPassword());
		txtPort.setText(String.valueOf(server.getPort()));
	}
	
	private void unlockServerinfo(){
		txtAdress.setEditable(true);
		txtLogin.setEditable(true);
		txtPassword.setEditable(true);
		txtPort.setEditable(true);
	}
	
	private void lockServerinfo(){
		txtAdress.setEditable(false);
		txtLogin.setEditable(false);
		txtPassword.setEditable(false);
		txtPort.setEditable(false);
	}

	private void lockServerListManipulatebuttons(){
		btnEdit.setEnabled(false);
		btnDel.setEnabled(false);
		btnAdd.setEnabled(false);
	}
	
	private void unlockServerListManipulatebuttons() {
		btnEdit.setEnabled(true);
		btnDel.setEnabled(true);
		btnAdd.setEnabled(true);
	}

	// создаем наше всплывающее меню
	private JPopupMenu createPopupMenu() {
		// создаем само всплывающее меню
		JPopupMenu pm = new JPopupMenu();
		// создаем его пункты
		JMenuItem cut = new JMenuItem("Вырезать");
		JMenuItem copy = new JMenuItem("Копировать");
		JMenuItem paste = new JMenuItem("Вставить");
		// и добавляем все тем же методом add()
		pm.add(cut);
		pm.add(copy);
		pm.add(paste);
		return pm;
	}

	
	// этот класс будет отслеживать щелчки мыши
	class ML extends MouseAdapter {
		public void mouseClicked(MouseEvent me) {
			// проверим, что это правая кнопка, и покажем наше всплывающее меню
			if (SwingUtilities.isRightMouseButton(me)) {
				popup.show(((JTextField) me.getComponent()), me.getX(), me.getY());
			}
		}
	}

}

class ServerListModel extends AbstractListModel {
	private static final long serialVersionUID = 1L;

	public ServerListModel(){
	}
	// методы модели для выдачи данных списку
	public int getSize() {
		return Config.getInstance().getServersCount();
	}

	public Object getElementAt(int idx) {
		return Config.getInstance().getServer(idx);
	}
}