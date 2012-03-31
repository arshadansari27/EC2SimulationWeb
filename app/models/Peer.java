package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
			stat.executeUpdate("create table if not exists task (id);");
			dataBaseChecked = true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	public static synchronized SimulationTask insertTask(SimulationTask t){
		getConnection();
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into task values (?);");
			prep.setString(1, t.id);
			conn.setAutoCommit(false);
		    prep.execute();
		    conn.setAutoCommit(true);
		    t = getTask(t.id);
		    return t;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static synchronized  boolean deleteTask(String id){
		getConnection();
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("delete from task where id = ?;");
			prep.setString(1, id);
			conn.setAutoCommit(false);
		    prep.execute();
		    conn.setAutoCommit(true);
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static synchronized SimulationTask getTask(String id){
		getConnection();
		try {
			PreparedStatement prep = conn.prepareStatement("select *  from task where id = ?;");
			prep.setString(1, id);
			ResultSet rs = prep.executeQuery();
			SimulationTask t = new SimulationTask();
			while (rs.next()) {
				t.id =  rs.getString("id");
			    }
			    rs.close();
			    return t;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static List<String> getAllTask(){
		getConnection();
		List<String> tasks = new ArrayList<String>();
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from task;");
			while (rs.next()) {
				tasks.add(rs.getString("id"));
			}
			rs.close();
			return tasks;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
