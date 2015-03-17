package main;

import java.io.IOException;

import logic.Starter;
import controller.Controller;

public class StartUpload {
	private static Starter launcher;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		try {
			Controller.getInstance().allowGui(true);
			launcher = new Starter();
			launcher.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Какая-то ошибка. Запустите ConfigEditor для создания и/или исправления config.xml");
		}
	}
}
