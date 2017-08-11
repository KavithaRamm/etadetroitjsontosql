package com.riis.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JDBCPreparedStatementSelectExample {

	private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost/eta";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "abcd4561";

	public void insertIntoSmartBusSchedulesTable(String stopName, List<String> times) throws SQLException {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		for (int i = 0; i < times.size(); i++) {

			String selectSQL = "insert into Smart_Bus_Schedules values('SmartBus', '125', 'NORTHBOUND', 'SATURDAY', '112',?, ?, NULL)";

			try {
				dbConnection = getDBConnection();
				preparedStatement = dbConnection.prepareStatement(selectSQL);
				// preparedStatement.setInt(1, 1001);
				preparedStatement.setString(1, stopName);
				preparedStatement.setString(2, times.get(i));

				// execute select SQL stetement
				boolean rs = preparedStatement.execute();

			} catch (SQLException e) {

				System.out.println(e.getMessage());

			} finally {

				if (preparedStatement != null) {
					preparedStatement.close();
				}

				if (dbConnection != null) {
					dbConnection.close();
				}

			}
		}
	}

	private static Connection getDBConnection() {

		Connection dbConnection = null;

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {

			System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}

}