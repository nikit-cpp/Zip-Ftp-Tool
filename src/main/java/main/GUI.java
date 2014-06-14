package main;

import java.io.IOException;
import controller.Controller;

public class GUI {
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
		}
	}
}
