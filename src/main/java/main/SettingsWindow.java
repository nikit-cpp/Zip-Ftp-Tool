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
import controller.Controller;
import controller.Event;
import controller.Event.Events;
import controller.Listener;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsWindow implements Listener{

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

	private final ServerListModel serverListModel;
	/**
	 * Create the application.
	 */
	public SettingsWindow() {
		Controller.getInstance().addListener(this);
		btnAdd = new JButton("+");
		btnSave = new JButton("^");
		btnDel = new JButton("-");
		btnEdit = new JButton("...");
		serverListModel = new ServerListModel();
		listServers = new JList(serverListModel);
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
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Server newServer = new Server("www.server.net", 21, "login_", "pass_");
				Config.getInstance().addServer(newServer);
			}
		});

		frame.getContentPane().add(btnAdd);

		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// тут сохранение в модель...
				int selected = listServers.getSelectedIndex();
				Server editableServer = Config.getInstance().getServer(selected);
				if(editableServer!=null){
					editableServer.setAdress(txtAdress.getText());
					editableServer.setLogin(txtLogin.getText());
					editableServer.setPassword(txtPassword.getText());
					editableServer.setPort(Integer.parseInt(txtPort.getText()));
				}
								
				btnSave.setEnabled(false);
				lockServerinfo();
				unlockServerListManipulatebuttons();
				listServers.setEnabled(true);
				Controller.getInstance().fireEvent(new Event(Events.SERVERS_LIST_CHANGED, null));
			}
		});

		btnSave.setEnabled(false);
		btnSave.setBounds(283, 328, 46, 143);
		frame.getContentPane().add(btnSave);
		
		
		btnDel.setEnabled(false);
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Config.getInstance().removeServer(listServers.getSelectedIndex());
			}
		});
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

		JButton btnResetLookupFolder = new JButton("");
		btnResetLookupFolder.setBounds(312, 50, 17, 20);
		frame.getContentPane().add(btnResetLookupFolder);

		JButton btnResetDestFolder = new JButton("");
		btnResetDestFolder.setBounds(312, 81, 17, 20);
		frame.getContentPane().add(btnResetDestFolder);

		JButton btnResetFtpFolder = new JButton("");
		btnResetFtpFolder.setBounds(312, 113, 17, 20);
		frame.getContentPane().add(btnResetFtpFolder);

		JButton btnResetPoolEnable = new JButton("");
		btnResetPoolEnable.setBounds(10, 144, 17, 20);
		frame.getContentPane().add(btnResetPoolEnable);

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

		JButton btnOk = new JButton("Ок");
		btnOk.setBounds(343, 448, 91, 23);
		frame.getContentPane().add(btnOk);
		
		JButton btnCancel = new JButton("Отмена");
		btnCancel.setBounds(343, 419, 91, 23);
		frame.getContentPane().add(btnCancel);
		
		JButton btnApply = new JButton("Применить");
		btnApply.setBounds(343, 388, 91, 23);
		frame.getContentPane().add(btnApply);
	}
	
	private void populateServerInfo(int serverIndex){
		Server server = Config.getInstance().getServer(serverIndex);
		if(server==null)
			return;
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


	@Override
	public void onEvent(final Event event) {
		if (event == null)
			return;

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				switch (event.type) {
				case SERVERS_LIST_CHANGED:
					listServers.setListData(serverListModel.getData());
					System.out.println("CONFIG_CHANGED");
					break;
				default:
					break;
				}
			}
		});

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
	
	public String[] getData(){
		String a[] = new String[getSize()];
		for(int i=0; i<getSize(); ++i){
			a[i]=getElementAt(i).toString();
		}
		return a;
	}
}