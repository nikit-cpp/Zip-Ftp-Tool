package main;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.Window.Type;

import javax.swing.JButton;

import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JApplet;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.AbstractListModel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import config.Config;
import config.Server;
import config.Settings;
import controller.Controller;
import controller.Event;
import controller.Event.Events;
import controller.Listener;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.JTextComponent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JScrollPane;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JPanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class SettingsWindow implements Listener{

	private JFrame frame;
	private JTextField txtLookupFolder;
	private JTextField txtDestFolder;
	private JTextField txtFtpFolder;
	private final JCheckBox chbPoolEnable;
	
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
		chbPoolEnable = new JCheckBox("Включить кэширование информации о файлах на FTP");
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
	private JList<String> listServers;
	
	private JPopupMenu popup;
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Настройки");
		frame.setResizable(false);
		// frame.setType(Type.UTILITY);
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
				endServerEditing();
			}
		});

		btnSave.setEnabled(false);
		btnSave.setBounds(283, 359, 46, 112);
		frame.getContentPane().add(btnSave);
		
		
		btnDel.setEnabled(false);
		btnDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Config.getInstance().removeServer(listServers.getSelectedIndex());
			}
		});
		btnDel.setBounds(391, 218, 43, 36);
		frame.getContentPane().add(btnDel);


		JButton btnSelectLookupFolder = new JButton("lookupFolder");
		btnSelectLookupFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path =chooseDirectory();
				System.out.println("выбрали: " + path);
				if(path!=null){
					Config.getInstance().set(Settings.LOOKUP_FOLDER, path);
				}
			}
		});

		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startServerEditing();
			}
		});
		
		btnEdit.setEnabled(false);
		btnEdit.setBounds(391, 265, 43, 36);
		frame.getContentPane().add(btnEdit);

		btnSelectLookupFolder.setBounds(339, 50, 95, 23);
		frame.getContentPane().add(btnSelectLookupFolder);

		txtLookupFolder = new JTextField();
		txtLookupFolder.setBounds(10, 51, 298, 20);
		frame.getContentPane().add(txtLookupFolder);
		txtLookupFolder.setColumns(10);
		txtLookupFolder.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				configSetString(Settings.LOOKUP_FOLDER, txtLookupFolder.getText());
			}
		});

		txtDestFolder = new JTextField();
		txtDestFolder.setColumns(10);
		frame.getContentPane().add(txtDestFolder);
		txtDestFolder.setBounds(10, 82, 298, 20);
		txtDestFolder.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				configSetString(Settings.DEST_FOLDER, txtDestFolder.getText());
			}
		});

		JButton btnSelectDestFolder = new JButton("destFolder");
		btnSelectDestFolder.setBounds(339, 81, 95, 23);
		frame.getContentPane().add(btnSelectDestFolder);
		btnSelectDestFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path =chooseDirectory();
				System.out.println("выбрали: " + path);
				if(path!=null){
					Config.getInstance().set(Settings.DEST_FOLDER, path);
				}
			}
		});

		txtFtpFolder = new JTextField();
		txtFtpFolder.setColumns(10);
		txtFtpFolder.setBounds(10, 114, 298, 20);
		frame.getContentPane().add(txtFtpFolder);
		txtFtpFolder.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				configSetString(Settings.FTP_FOLDER, txtFtpFolder.getText());
			}
		});

		JButton btnSelectFtpFolder = new JButton("ftpFolder");
		btnSelectFtpFolder.setBounds(339, 113, 95, 23);
		frame.getContentPane().add(btnSelectFtpFolder);
		btnSelectFtpFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String path =chooseDirectory();
				System.out.println("выбрали: " + path);
				if(path!=null){
					Config.getInstance().set(Settings.FTP_FOLDER, path);
				}
			}
		});
		
		
		chbPoolEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Config.getInstance().set(Settings.IS_FTP_FILES_POOL, chbPoolEnable.isSelected());
			}
		});
		chbPoolEnable.setBounds(34, 141, 400, 23);
		frame.getContentPane().add(chbPoolEnable);

		JButton btnResetLookupFolder = new JButton("");
		btnResetLookupFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.getInstance().reset(Settings.LOOKUP_FOLDER);
			}
		});
		btnResetLookupFolder.setBounds(312, 50, 17, 20);
		frame.getContentPane().add(btnResetLookupFolder);

		JButton btnResetDestFolder = new JButton("");
		btnResetDestFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.getInstance().reset(Settings.DEST_FOLDER);
			}
		});
		btnResetDestFolder.setBounds(312, 81, 17, 20);
		frame.getContentPane().add(btnResetDestFolder);

		JButton btnResetFtpFolder = new JButton("");
		btnResetFtpFolder.setBounds(312, 113, 17, 20);
		frame.getContentPane().add(btnResetFtpFolder);
		btnResetFtpFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.getInstance().reset(Settings.FTP_FOLDER);
			}
		});

		JButton btnResetPoolEnable = new JButton("");
		btnResetPoolEnable.setBounds(10, 144, 17, 20);
		frame.getContentPane().add(btnResetPoolEnable);
		btnResetPoolEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.getInstance().reset(Settings.IS_FTP_FILES_POOL);
			}
		});

		populateTextFieldsAndCheckbox();

		
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
		
		JButton btnClose = new JButton("Закрыть");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().fireEvent(new Event(Events.EXIT, null));
			}
		});
		btnClose.setBounds(346, 448, 91, 23);
		frame.getContentPane().add(btnClose);
		
		JButton btnApply = new JButton("Применить");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.getInstance().saveConfig();
			}
		});
		btnApply.setBounds(346, 419, 91, 23);
		frame.getContentPane().add(btnApply);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 175, 371, 173);
		frame.getContentPane().add(scrollPane);
		listServers = new JList(serverListModel);
		scrollPane.setViewportView(listServers);
		
		listServers.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				int selected = ((JList)arg0.getSource()).getSelectedIndex();
				populateServerInfo(selected);
				unlockServerListManipulatebuttons();
			}
		});
		
		listServers.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					// получаем элемент и покажем его
					//int pos = listServers.locationToIndex(e.getPoint());
					startServerEditing();
				}
			}
		});
	}
	
	private void populateTextFieldsAndCheckbox(){
		txtLookupFolder.setText(Config.getInstance().getString(Settings.LOOKUP_FOLDER));
		txtDestFolder.setText(Config.getInstance().getString(Settings.DEST_FOLDER));
		txtFtpFolder.setText(Config.getInstance().getString(Settings.FTP_FOLDER));

		chbPoolEnable.setSelected(Config.getInstance().getBoolean(Settings.IS_FTP_FILES_POOL));
	}
	
	private void configSetString(Settings e, String s){
		Config.getInstance().set(e, s);
	}
	
	private void startServerEditing(){
		unlockServerinfo();
		btnSave.setEnabled(true);
		lockServerListManipulatebuttons();
		listServers.setEnabled(false);
	}
	
	private void endServerEditing(){
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
	
	private String chooseDirectory(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
        	return fileChooser.getSelectedFile().getPath();
        }
        return null;
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

	
	// Контекстное меню. этот класс будет отслеживать щелчки мыши
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
					int pos = listServers.getSelectedIndex();
					listServers.setListData(serverListModel.getData());
					System.out.println("processed SERVERS_LIST_CHANGED event in gui.");
					if(pos>=0 && pos<Config.getInstance().getServersCount()){
						listServers.setSelectedIndex(pos);
					}
					break;
				case CONFIG_CHANGED:
					populateTextFieldsAndCheckbox();
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

// http://stackoverflow.com/questions/2793940/why-right-click-is-not-working-on-java-application/2793959#2793959
class ContextMenuMouseListener extends MouseAdapter {
    private JPopupMenu popup = new JPopupMenu();

    private Action cutAction;
    private Action copyAction;
    private Action pasteAction;
    private Action undoAction;
    private Action selectAllAction;

    private JTextComponent textComponent;
    private String savedString = "";
    private Actions lastActionSelected;

    private enum Actions { UNDO, CUT, COPY, PASTE, SELECT_ALL };

    public ContextMenuMouseListener() {
        undoAction = new AbstractAction("Undo") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                    textComponent.setText("");
                    textComponent.replaceSelection(savedString);

                    lastActionSelected = Actions.UNDO;
            }
        };

        popup.add(undoAction);
        popup.addSeparator();

        cutAction = new AbstractAction("Cut") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.CUT;
                savedString = textComponent.getText();
                textComponent.cut();
            }
        };

        popup.add(cutAction);

        copyAction = new AbstractAction("Copy") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.COPY;
                textComponent.copy();
            }
        };

        popup.add(copyAction);

        pasteAction = new AbstractAction("Paste") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.PASTE;
                savedString = textComponent.getText();
                textComponent.paste();
            }
        };

        popup.add(pasteAction);
        popup.addSeparator();

        selectAllAction = new AbstractAction("Select All") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                lastActionSelected = Actions.SELECT_ALL;
                textComponent.selectAll();
            }
        };

        popup.add(selectAllAction);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getModifiers() == InputEvent.BUTTON3_MASK) {
            if (!(e.getSource() instanceof JTextComponent)) {
                return;
            }

            textComponent = (JTextComponent) e.getSource();
            textComponent.requestFocus();

            boolean enabled = textComponent.isEnabled();
            boolean editable = textComponent.isEditable();
            boolean nonempty = !(textComponent.getText() == null || textComponent.getText().equals(""));
            boolean marked = textComponent.getSelectedText() != null;

            boolean pasteAvailable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).isDataFlavorSupported(DataFlavor.stringFlavor);

            undoAction.setEnabled(enabled && editable && (lastActionSelected == Actions.CUT || lastActionSelected == Actions.PASTE));
            cutAction.setEnabled(enabled && editable && marked);
            copyAction.setEnabled(enabled && marked);
            pasteAction.setEnabled(enabled && editable && pasteAvailable);
            selectAllAction.setEnabled(enabled && nonempty);

            int nx = e.getX();

            if (nx > 500) {
                nx = nx - popup.getSize().width;
            }

            popup.show(e.getComponent(), nx, e.getY() - popup.getSize().height);
        }
    }
}