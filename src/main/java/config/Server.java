package config;

public class Server {
	@Override
	public String toString() {
		return adress + ":" + port + " [login="
				+ login + ", password=***]";
	}

	public Server(String adress, int port, String login, String password) {
		super();
		this.setAdress(adress);
		this.setPort(port);
		this.setLogin(login);
		this.setPassword(password);
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String adress;
	private int port;
	private String login;
	private String password;
}
