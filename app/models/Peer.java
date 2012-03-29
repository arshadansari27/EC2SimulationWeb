package models;

import java.sql.*;

public class Peer {
	protected static Connection conn;
	private static boolean dataBaseChecked = false;
	
	protected static Connection getConnection(){
		if(conn == null){
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:simulation.db");
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(!dataBaseChecked){
			if(conn==null){
				throw new RuntimeException("Connection not created");
			}
			readyDatabase();
		}
		return conn;
	}
	
	private static boolean readyDatabase(){
		try {
			Statement stat = conn.createStatement();
			stat.executeUpdate("create table if not exists task (id,label,description,concurrentRequestLimit, maxServer, hoursToSimulate, averageRequestPerSecond, maxRequestPerSecond, minRequestPerSecond, averageRequestServiceTime, maxRequestServiceTime, minRequestServiceTime);");
			stat = conn.createStatement();
			stat.executeUpdate("create table if not exists statistics (id,simulationClock, requestDipatchedCount, requestRejectedCount, averageServiceTime, averageWaitTime, averageActualServiceTime, servers, averageServerUtilization, averageServerUsage, totalTimeInAction, totalTimeInSystem);");
			stat = conn.createStatement();
			stat.executeUpdate("create table if not exists serverhistory (id,timeunit, eventtime, servercount);");
			dataBaseChecked = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}
