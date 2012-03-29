package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.olivelabs.simulation.StatisticsCollector;

public class StatisticsPeer extends Peer{
	//id,simulationClock, requestDipatchedCount, requestRejectedCount, averageServiceTime, averageWaitTime, averageActualServiceTime, servers, averageServerUtilization, averageServerUsage, totalTimeInAction, totalTimeInSystem
	
	public static synchronized boolean insertStatistics(String id, StatisticsCollector stats){
		getConnection();
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into statistics values (?,?,?,?,?,?,?,?,?,?,?,?);");
			prep.setString(1, id);
			prep.setLong(2, stats.simulationClock);
			prep.setLong(3, stats.requestDipatchedCount);
			prep.setLong(4, stats.requestRejectedCount);
			prep.setDouble(5, stats.averageServiceTime);
			prep.setDouble(6, stats.averageWaitTime);
			prep.setDouble(7, stats.averageActualServiceTime);
			prep.setLong(8, stats.servers);
			prep.setDouble(9, stats.averageServerUtilization);
			prep.setDouble(10, stats.averageServerUsage);
			prep.setLong(11, stats.totalTimeInAction);
			prep.setLong(12, stats.totalTimeInSystem);
			conn.setAutoCommit(false);
		    prep.execute();
		    conn.setAutoCommit(true);
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static synchronized  boolean deleteStatistics(String id){
		getConnection();
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("delete from statistics where id = ?;");
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
	
	public static synchronized StatisticsCollector getStatistics(String id){
		getConnection();
		try {
			PreparedStatement prep = conn.prepareStatement("select *  from statistics where id = ?;");
			prep.setString(1, id);
			ResultSet rs = prep.executeQuery();
			StatisticsCollector stats = new StatisticsCollector();
			while (rs.next()) {
				String retrievedId =  rs.getString("id");
				stats.simulationClock =  rs.getLong("simulationClock");
				stats.requestDipatchedCount =  rs.getLong("requestDipatchedCount");
				stats.requestRejectedCount =   rs.getLong("requestRejectedCount");
				stats.averageServiceTime =   rs.getDouble("averageServiceTime");
				stats.averageWaitTime =   rs.getDouble("averageWaitTime");
				stats.averageActualServiceTime =  rs.getDouble("averageActualServiceTime");
				stats.averageServerUtilization =  rs.getDouble("averageServerUtilization");
				stats.averageServerUsage =  rs.getDouble("averageServerUsage");
				stats.totalTimeInAction =  rs.getLong("totalTimeInAction");
				stats.totalTimeInSystem =  rs.getLong("totalTimeInSystem");
				if(!retrievedId.equals(id))	
					throw new RuntimeException("Mismacthed Id!");
			    }
			    rs.close();
			    
			    return stats;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized static Map<String,StatisticsCollector> getAllTask(){
		getConnection();
		Map<String,StatisticsCollector> statsMap = new HashMap<String, StatisticsCollector>();
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery("select * from task;");
			
			while (rs.next()) {
				StatisticsCollector stats = new StatisticsCollector();
				String id  =  rs.getString("id");
				stats.simulationClock =  rs.getLong("simulationClock");
				stats.requestDipatchedCount =  rs.getLong("requestDipatchedCount");
				stats.requestRejectedCount =   rs.getLong("requestRejectedCount");
				stats.averageServiceTime =   rs.getDouble("averageServiceTime");
				stats.averageWaitTime =   rs.getDouble("averageWaitTime");
				stats.averageActualServiceTime =  rs.getDouble("averageActualServiceTime");
				stats.averageServerUtilization =  rs.getDouble("averageServerUtilization");
				stats.averageServerUsage =  rs.getDouble("averageServerUsage");
				stats.totalTimeInAction =  rs.getLong("totalTimeInAction");
				stats.totalTimeInSystem =  rs.getLong("totalTimeInSystem");
				statsMap.put(id, stats);
			}
			rs.close();
			return statsMap;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
