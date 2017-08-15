package com.riis.jsontosql;

import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.base.CharMatcher;
import com.riis.dao.JDBCPreparedStatement;

public class JsonToSql {

	public static void main(String args[]) {

		JsonToSql jsonToSql = new JsonToSql();
		jsonToSql.convertJsonToSql();

	}

	private void convertJsonToSql() {
		JSONParser parser = new JSONParser();
		Set<String> routeNumber = new HashSet<>();
		JDBCPreparedStatement jdbc = new JDBCPreparedStatement();
		List<String> listOfFilePaths = getListOfFiles();
		for (String filePath : listOfFilePaths) {

			try {
				Object obj = parser.parse(new FileReader(filePath));
				JSONArray jsonArray = (JSONArray) obj;
				for (int i = 0; i < jsonArray.size(); i++) {
					getRouteNumberDayDirectionStopNameTimes(routeNumber, jdbc, filePath, jsonArray, i);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("RouteNumber"+routeNumber);
		}
	}

	private void getRouteNumberDayDirectionStopNameTimes(Set<String> routeNumber, JDBCPreparedStatement jdbc, String filePath,
			JSONArray jsonArray, int i) throws SQLException {
		JSONObject jsonObj = (JSONObject) jsonArray.get(i);
		String stopName = (String) jsonObj.get("Name");
		System.out.println("Stop name " + stopName);
		JSONArray timesJSONArray = (JSONArray) jsonObj.get("Times");
		List<String> times = getTimeFromJSON(routeNumber, timesJSONArray);
		String[] routeDetails = getRouteDirecctionDayFromFileName(filePath);
		String routeNumberFromFileName = routeDetails[0];
		String routeNum = getRouteNumber(routeNumberFromFileName);
		String day = routeDetails[1];
		String direction = routeDetails[2];
		jdbc.insertIntoDBTable(routeNum, day, direction, stopName, times);
		System.out.println("routeNum"+routeNum);
		System.out.println("day"+day);
		System.out.println("direction"+direction);
		System.out.println("stopname"+stopName);
		System.out.println(times);
	}

	private String getRouteNumber(String routeNumberFromFileName) {
		CharMatcher ASCII_DIGITS = CharMatcher.inRange('0', '9').precomputed();
		String routeNum = ASCII_DIGITS.retainFrom(routeNumberFromFileName);
		return routeNum;
	}

	private String[] getRouteDirecctionDayFromFileName(String filePath) {
		String string = filePath;
		String[] routeDetails = string.split("_");
		return routeDetails;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<String> getTimeFromJSON(Set<String> routeNumber, JSONArray timesJSONArray) {
		Iterator<Map.Entry> itr1;
		Iterator itr2 = timesJSONArray.iterator();
		List<String> times = new ArrayList<>();
		while (itr2.hasNext()) {
			itr1 = ((Map) itr2.next()).entrySet().iterator();
			while (itr1.hasNext()) {
				Map.Entry pair = itr1.next();
				if (pair.getKey().equals("RouteNumber") && routeNumber.size() == 0) {
					routeNumber.add((String) pair.getValue());
				}
				if (pair.getKey().equals("Time")) {
					times.add((String) pair.getValue());
				}
			}
		}
		return times;
	}

	public List<String> getListOfFiles() {
		File folder = new File("/home/kavi/dumps/dummysmartbus");
		File[] arrayOffiles = folder.listFiles();
		List<String> listOfFiles = new ArrayList<>();

		for (File file : arrayOffiles) {
			if (file.isFile()) {
				listOfFiles.add(file.getAbsolutePath());
			}
		}
		return listOfFiles;
	}
}