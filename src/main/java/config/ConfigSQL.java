package config;

public class ConfigSQL {
	
	private String url;

	private String db = "chessdb";

	private String driver = "com.mysql.jdbc.Driver";

	private String user = "root";

	private String pass;

	public ConfigSQL(String mode) {
		if(mode.equals("local")) {
			url = "jdbc:mysql://localhost/";
			pass = "root";
			db = "chessdb";
		}
	}

	public String getUrl() {
		return url;
	}

	public String getDb() {
		return db;
	}

	public String getDriver() {
		return driver;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}
	
}