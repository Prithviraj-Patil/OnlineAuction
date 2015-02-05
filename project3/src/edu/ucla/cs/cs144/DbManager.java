package edu.ucla.cs.cs144;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbManager {
	static private String databaseURL = "jdbc:mysql://localhost:3306/";
	static private String dbname = "CS144";
	static private String username = "cs144";
	static private String password = "";

	/**
	 * Opens a database connection
	 * 
	 * @param dbName
	 *            The database name
	 * @param readOnly
	 *            True if the connection should be opened read-only
	 * @return An open java.sql.Connection
	 * @throws SQLException
	 */
	public static Connection getConnection(boolean readOnly) throws SQLException {

		Properties props = new Properties();
		props.put("user", username);
		props.put("password", password);
		props.put("useUnicode", "true");
		props.put("useServerPrepStmts", "false");
		props.put("characterEncoding", "UTF-8");

		Connection conn = DriverManager.getConnection(databaseURL + dbname, props);
		conn.setReadOnly(readOnly);
		return conn;
	}

	private DbManager() {
	}

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
