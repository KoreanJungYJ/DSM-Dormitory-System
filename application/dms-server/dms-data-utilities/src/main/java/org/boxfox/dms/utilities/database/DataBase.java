package org.boxfox.dms.utilities.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.boxfox.dms.utilities.config.SecureConfig;
import org.boxfox.dms.utilities.log.Log;

import static org.boxfox.dms.utilities.database.QueryUtils.queryBuilder;

public class DataBase {
	private static final String DB_TARGET;
	private static final String DB_ATTRIBUTE = "?allowMultiQueries=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
	private static final String DB_ID;
	private static final String DB_PW;

	private Statement statement;
	private Connection connection;

	static{
		DB_TARGET = SecureConfig.get("DB_HOST")+":"+SecureConfig.get("DB_PORT")+"/"+SecureConfig.get("DB_NAME");
		DB_ID = SecureConfig.get("DB_ID");
		DB_PW = SecureConfig.get("DB_PW");
	}

	private DataBase() {
		connect();
	}

	private static class DataBaseSinglethon{
		private static final DataBase DB = new DataBase();
	}

	public static DataBase getInstance() {
		/*
		 * Problems occur when more than one object accesses the database at the
		 * same time. So use singleton pattern.
		 */
		return DataBaseSinglethon.DB;
	}
	
	public Statement getStatement(){
		return statement;
	}
	
	public boolean close(){
		boolean result = false;
		try {
			statement.close();
			connection.close();
			result = true;
		} catch (SQLException | NullPointerException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean connect() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection("jdbc:mysql://" + DB_TARGET + DB_ATTRIBUTE, DB_ID, DB_PW);
				statement = connection.createStatement();
				Log.l("Database connected successfully!");
			}
			return true;
		} catch (SQLException e) {
			Log.e(e.toString());
		}
		return false;
	}

	public synchronized boolean execute(String query) throws SQLException {
		return statement.execute(query);
	}

	public synchronized int executeUpdate(String query) throws SQLException {
		return statement.executeUpdate(query);
	}

	public synchronized SafeResultSet executeQuery(String query) throws SQLException {
		return new SafeResultSet(statement.executeQuery(query));
	}

	public synchronized boolean execute(Object... args) throws SQLException {
		String query = queryBuilder(args);
		return statement.execute(query);
	}

	public synchronized int executeUpdate(Object... args) throws SQLException {
		String query = queryBuilder(args);
		return executeUpdate(query);
	}

	public synchronized SafeResultSet executeQuery(Object... args) throws SQLException {
		String query = queryBuilder(args);
		return new SafeResultSet(statement.executeQuery(query));
	}

	public synchronized SafeResultSet getResultSet() throws SQLException {
		return new SafeResultSet(statement.getResultSet());
	}

	public int executeUpdate(DataSaveable data) throws SQLException {
		return executeUpdate(data.toQuery());
	}

}
