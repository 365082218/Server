
//import java.util.ArrayList;
//import java.util.Collection;
//import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class Test {
	Connection connection;

	public void getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			String url = "jdbc:mysql://idevgame.com:3306/meteor";
			// String user = "Winson";
			// String password = "xuwen1013";

			Properties properties = new Properties();

			properties.setProperty("user", "Winson");
			properties.setProperty("password", "xuwen1013");
			properties.setProperty("useSSL", "false");
			properties.setProperty("verifyServerCertificate", "false");
			properties.setProperty("serverTimezone", "UTC");
			properties.setProperty("characterEncoding", "utf-8");

			DriverManager.getConnection(url, properties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Test test = new Test();
		test.getConnection();
	}

}
