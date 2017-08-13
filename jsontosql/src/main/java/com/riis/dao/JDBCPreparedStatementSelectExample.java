package com.riis.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JDBCPreparedStatementSelectExample {
	private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost/eta?autoReconnect=true&useSSL=false";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "abcd4561";
	private Connection dbConnection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	public void insertIntoDBTable(String routeNumber, String day, String direction, String stopName, List<String> times) throws SQLException {
		for (int i = 0; i < times.size(); i++) {
			resultSet = getStopID(stopName, direction);
			while (resultSet.next()) {
				insertIntoSmartBusSchedulesTable(routeNumber, day, direction, stopName, times, i);
			}
		}
	}

	/* Inserting into Smart_Bus_Schedules Table */
	private void insertIntoSmartBusSchedulesTable(String routeNumber, String day, String direction, String stopName, List<String> times, int i)
			throws SQLException {
		String stopID = resultSet.getString("stopId");
		System.out.println("stopID : " + stopID);
		String insertSQL = "insert into test_Smart_Bus_Schedules values('SmartBus',?,?,?,?,?,?, NULL)";
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(insertSQL);
			preparedStatement.setString(1, routeNumber);
			preparedStatement.setString(2, direction);
			preparedStatement.setString(3, day);
			preparedStatement.setString(4, stopID);
			preparedStatement.setString(5, stopName);
			preparedStatement.setString(6, times.get(i));
			preparedStatement.execute();
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

	/* Getting the stopID from DB */
	private ResultSet getStopID(String stopName, String direction) {
		String selectSQL = "SELECT distinct stopId FROM Smart_Bus_Schedules where stp_name =" + "'" + stopName + "'" + " and direction =" + "'"
				+ direction + "'";
		try {
			dbConnection = getDBConnection();
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			resultSet = preparedStatement.executeQuery();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return resultSet;
	}

	/* Creating DB connection */
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