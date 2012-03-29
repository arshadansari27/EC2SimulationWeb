package models;
import java.sql.*;
import java.util.*;

public class TaskPeer extends Peer{
	
	/*
	id,label,description,concurrentRequestLimit, maxServer, 
	hoursToSimulate, averageRequestPerSecond, maxRequestPerSecond, minRequestPerSecond, 
	averageRequestServiceTime, maxRequestServiceTime, minRequestServiceTime
	?,?,?,?,?,?,?,?,?,?,?,?	 
	*/
	public static synchronized Task insertTask(Task t){
		getConnection();
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into task values (?,?,?,?,?,?,?,?,?,?,?,?);");
			prep.setString(1, t.id);
			prep.setString(2, t.label);
			prep.setString(3, t.description);
			prep.setInt(4, t.concurrentRequestLimit);
			prep.setInt(5, t.maxServer);
			prep.setInt(6, t.hoursToSimulate);
			prep.setLong(7, t.averageRequestPerSecond);
			prep.setLong(8, t.maxRequestPerSecond);
			prep.setLong(9, t.minRequestPerSecond);
			prep.setInt(10, t.averageRequestServiceTime);
			prep.setInt(11, t.maxRequestServiceTime);
			prep.setInt(12, t.minRequestServiceTime);
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
	
	public static synchronized Task getTask(String id){
		getConnection();
		try {
			PreparedStatement prep = conn.prepareStatement("select *  from task where id = ?;");
			prep.setString(1, id);
			ResultSet rs = prep.executeQuery();
			Task t = new Task();
			while (rs.next()) {
				t.id =  rs.getString("id");
				t.label =  rs.getString("label");
				t.description =  rs.getString("description");
				t.concurrentRequestLimit =   rs.getInt("concurrentRequestLimit");
				t.maxServer =   rs.getInt("maxServer");
				t.hoursToSimulate =   rs.getInt("hoursToSimulate");
				t.averageRequestPerSecond =  rs.getLong("averageRequestPerSecond");
				t.maxRequestPerSecond =  rs.getLong("maxRequestPerSecond");
				t.minRequestPerSecond =  rs.getLong("minRequestPerSecond");
				t.averageRequestServiceTime =  rs.getInt("averageRequestServiceTime");
				t.maxRequestServiceTime =  rs.getInt("maxRequestServiceTime");
				t.minRequestServiceTime =  rs.getInt("minRequestServiceTime");
			    }
			    rs.close();
			    return t;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static Map<String,Task> getAllTask(){
		getConnection();
		Map<String,Task> tasks = new HashMap<String, Task>();
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from task;");
			
			while (rs.next()) {
				Task t = new Task();
				t.id =  rs.getString("id");
				t.label =  rs.getString("label");
				t.description =  rs.getString("description");
				t.concurrentRequestLimit =   rs.getInt("concurrentRequestLimit");
				t.maxServer =   rs.getInt("maxServer");
				t.hoursToSimulate =   rs.getInt("hoursToSimulate");
				t.averageRequestPerSecond =  rs.getLong("averageRequestPerSecond");
				t.maxRequestPerSecond =  rs.getLong("maxRequestPerSecond");
				t.minRequestPerSecond =  rs.getLong("minRequestPerSecond");
				t.averageRequestServiceTime =  rs.getInt("averageRequestServiceTime");
				t.maxRequestServiceTime =  rs.getInt("maxRequestServiceTime");
				t.minRequestServiceTime =  rs.getInt("minRequestServiceTime");
				tasks.put(t.id, t);
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
