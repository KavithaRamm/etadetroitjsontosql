package com.riis.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
		ResultSet resultSet = null;
		String direction = "northbound";

		for (int i = 0; i < times.size(); i++) {

			String selectSQL = "SELECT distinct stopId FROM Smart_Bus_Schedules where stp_name =" + "'" + stopName + "'" + " and direction =" + "'"
					+ direction + "'";
			try {
				dbConnection = getDBConnection();
				preparedStatement = dbConnection.prepareStatement(selectSQL);
				resultSet = preparedStatement.executeQuery();

			} catch (SQLException e) {

				System.out.println(e.getMessage());

			}
			while (resultSet.next()) {
				String stopID = resultSet.getString("stopId");
				System.out.println("stopID : " + stopID);
				String insertSQL = "insert into Smart_Bus_Schedules values('SmartBus', '125', 'NORTHBOUND', 'SUNDAY',?,?, ?, NULL)";
				try {
					dbConnection = getDBConnection();
					preparedStatement = dbConnection.prepareStatement(insertSQL);
					preparedStatement.setString(1, stopID);
					preparedStatement.setString(2, stopName);
					preparedStatement.setString(3, times.get(i));
					// execute select SQL statement
					boolean entryResult = preparedStatement.execute();

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

// SELECT distinct stopId FROM Smart_Bus_Schedules where stp_name ="+stopName+"
// and direction ="+"northbound