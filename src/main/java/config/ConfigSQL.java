package config;

public class ConfigSQL {
	
	private String url;

	private String db = "chesstest3";

	private String driver = "com.mysql.jdbc.Driver";

	private String user = "root";

	private String pass;

	public ConfigSQL(String mode) {
		if(mode.equals("local3")) {
			url = "jdbc:mysql://localhost/";
			pass = "root";
			db = "chesstest3";
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