package aps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class PostgresHelper {

	private Connection conn;
	private String host;
	private String dbName;
	private String user;
	private String pass;
	
	public PostgresHelper(String host, String dbName, String user, String pass) {
		this.host = host;
		this.dbName = dbName;
		this.user = user;
		this.pass = pass;
	}
	
	public boolean connect() throws SQLException, ClassNotFoundException {
		if (host.isEmpty() || dbName.isEmpty() || user.isEmpty() || pass.isEmpty()) {
			throw new SQLException("Database credentials missing");
		}
		
		Class.forName("org.postgresql.Driver");
		this.conn = DriverManager.getConnection(
				this.host + this.dbName,
				this.user, this.pass);
		return true;
	}
	
	public ResultSet execQuery(String query) throws SQLException {
		return this.conn.createStatement().executeQuery(query);
	}
	
	public int insert(String table, Map<String,Object> values) throws SQLException {
		
		StringBuilder columns = new StringBuilder();
		StringBuilder vals = new StringBuilder();
		
		for (String col : values.keySet()) {
			System.out.println(col);
			columns.append(col).append(",");			
			if (values.get(col) instanceof String) {
				vals.append("'").append(values.get(col)).append("', ");
			}
			else vals.append(values.get(col)).append(",");
		}	
		System.out.println(columns.length());
		columns.setLength(columns.length()-1);
		vals.setLength(vals.length()-1);
		String query = String.format("INSERT INTO %s (%s) VALUES (%s)", table,
				columns.toString(), vals.toString());
		System.out.print(query);
		return this.conn.createStatement().executeUpdate(query);
	}
	
	public int updateMT(int user, float value, int alarm) throws SQLException {
		
		//UPDATE mt_values SET mt_value=1 WHERE user_id=19775920
		String query;
		if (alarm==0) {
			query = String.format("UPDATE mt_values SET mt_value=(%s) WHERE user_id=(%s)", 
					Float.toString(value), Integer.toString(user));
		}
		else {
			query = String.format("UPDATE mt_values SET mt_value=(%s), alarm_tweet=(%s) WHERE user_id=(%s)", 
					Float.toString(value), Integer.toString(alarm), Integer.toString(user));
		}
		
		return this.conn.createStatement().executeUpdate(query);
	}
	
}
