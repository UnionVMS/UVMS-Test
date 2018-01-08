package db;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class RulesDBClient {
	
	private static Connection conn ;
	static {
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/db71u", "postgres", "");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	private static QueryRunner run = new QueryRunner();
	
	
	
	public String getDBRulesResponse(String messageGuid) throws Exception {
		
		ArrayList<Map<String, String>> result = new ArrayList<Map<String, String>>();
		String query = String.format("select log_type_ref_message from exchange.log where log_type_ref_guid = (select log_guid from exchange.log where log_type_ref_guid = '%s')", messageGuid);
		
		int waitCycles = 0;
		
		while (result.size() == 0 && waitCycles<30){
			Thread.currentThread().sleep(2000);
			waitCycles++;
			result.addAll((ArrayList<Map<String, String>>) run.query(conn, query, new MyResultSetHandler()));
		}
		
		if(result.size() > 0){
			return result.get(0).get("log_type_ref_message");
		}
		
		return "";
	}
	
	public void closeConnection(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
