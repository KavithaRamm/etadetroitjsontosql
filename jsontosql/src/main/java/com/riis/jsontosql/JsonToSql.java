package com.riis.jsontosql;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.riis.dao.JDBCPreparedStatementSelectExample;

public class JsonToSql {

	public static void main(String args[]) {

		JsonToSql jsonToSql = new JsonToSql();
		jsonToSql.convertJsonToSql();

	}

	private void convertJsonToSql() {
		JSONParser parser = new JSONParser();
		Set<String> routeNumber = new HashSet<>();
		JDBCPreparedStatementSelectExample jdbc = new JDBCPreparedStatementSelectExample();
		try {
			Object obj = parser.parse(new FileReader("/home/kavi/dumps/smartbus/route280Saturdaynorthbound"));
			JSONArray jsonArray = (JSONArray) obj;
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);
				String stopName = (String) jsonObj.get("Name");
				System.out.println("Stop name " + stopName);
				JSONArray timesJSONArray = (JSONArray) jsonObj.get("Times");
				List<String> times = getTimeFromJSON(routeNumber, timesJSONArray);
				jdbc.insertIntoDBTable(stopName, times);
				System.out.println(times);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(routeNumber);
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
}