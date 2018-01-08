package db;

import org.apache.commons.dbutils.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyResultSetHandler implements ResultSetHandler {
	
	
	@Override
	public ArrayList<Map<String, String>> handle(ResultSet resultSet) throws SQLException {
		RowProcessor processor = new BasicRowProcessor();
		ArrayList<Map<String, String>> rows = new ArrayList<Map<String, String>>();
		
		ResultSetMetaData meta = resultSet.getMetaData();
		int cols = meta.getColumnCount();
		
		while(resultSet.next()) {
			Map<String, Object> raw = processor.toMap(resultSet);
			Map<String, String> processed = new HashMap<String, String>();
			
			for (String key : raw.keySet()) {
				processed.put(key, raw.get(key).toString());
			}
			
			rows.add(processed);
		}
		
		return rows;
	}
	
	
}
