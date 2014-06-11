package main;

import java.awt.EventQueue;
import session.messages.Message;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class GUI implements Observer {
	private static Starter launcher;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		GUI gui = new GUI();
		try {
			// Создаём поток Launcher, и запускаем его.
			// Экземпляр window уже создан и мы можем добавлять
			// обсерверы
			launcher = new Starter(gui);
			
			launcher.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// вызывается при изменении ...
	public void update(final Observable o, Object arg) {
		if (arg == null || arg.getClass() != Message.class)
			return;

		final Message message = (Message) arg;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				switch (message.type) {
				case EXIT:
					System.exit(0);
					break;
				case NEW_PROGRESS_WINDOW:
					UploadProgress progressWindow = new UploadProgress();
					o.addObserver(progressWindow);
					progressWindow.frame.setVisible(true);
					break;
				default:
					break;
				}
			}
		});
	}

}
