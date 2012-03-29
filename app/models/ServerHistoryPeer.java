package models;

import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

import org.olivelabs.simulation.StatisticsCollector;

public class ServerHistoryPeer extends Peer{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static synchronized boolean insertServerHistory(String id, StatisticsCollector stats){
		getConnection();
		Map<Long, Long> serverHistoryInSecs = stats.serverHistoryInSecs;
		Map<Long, Long> serverHistoryInMins = stats.serverHistoryInMins;
		Map<Long, Long> serverHistoryInHours = stats.serverHistoryInHours;
		ArrayList data; 
		List<ArrayList> list = new ArrayList<ArrayList>();
		for(Entry<Long,Long> serversPerSec : serverHistoryInSecs.entrySet()){
			data = new ArrayList();
			data.add("SECONDS");
			data.add(serversPerSec.getKey());
			data.add(serversPerSec.getValue());
			list.add(data);
		}
		for(Entry<Long,Long> serversPerMin : serverHistoryInMins.entrySet()){
			data = new ArrayList();
			data.add("MINUTES");
			data.add(serversPerMin.getKey());
			data.add(serversPerMin.getValue());
			list.add(data);
		}
		for(Entry<Long,Long> serversPerHour : serverHistoryInHours.entrySet()){
			data = new ArrayList();
			data.add("HOURS");
			data.add(serversPerHour.getKey());
			data.add(serversPerHour.getValue());
			list.add(data);
		}
			
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("insert into serverhistory values (?,?,?,?);");
			for(ArrayList items : list){
				prep.setString(1, id);
				prep.setString(2, (String)items.get(0));
		    	prep.setLong(3, (Long) items.get(1));
		    	prep.setLong(4, (Long)items.get(2));
		    	prep.addBatch();
			}
			conn.setAutoCommit(false);
		    prep.executeBatch();
		    conn.setAutoCommit(true);
		    return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static synchronized  boolean deleteServerHistory(String id){
		getConnection();
		PreparedStatement prep;
		try {
			prep = conn.prepareStatement("delete from serverhistory where id = ?;");
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
	
	//timeunit, eventtime, servercount
	public static synchronized StatisticsCollector getServerHistory(String id, StatisticsCollector stats){
		getConnection();
		try {
			PreparedStatement prep = conn.prepareStatement("select *  from serverhistory where id = ?;");
			prep.setString(1, id);
			ResultSet rs = prep.executeQuery();
			TreeMap<Long, Long> serverHistoryInSecs = new TreeMap<Long, Long>();
			TreeMap<Long, Long> serverHistoryInMins = new TreeMap<Long, Long>();
			TreeMap<Long, Long> serverHistoryInHours = new TreeMap<Long, Long>();
			String retrievedId;
			while (rs.next()) {
				retrievedId =  rs.getString("id");
				String timeUnit =  rs.getString("timeunit");
				if("SECONDS".equals(timeUnit)){
					serverHistoryInSecs.put(rs.getLong("eventtime"),rs.getLong("servercount") );
				}
				else if("MINUTES".equals(timeUnit)){
					serverHistoryInMins.put(rs.getLong("eventtime"),rs.getLong("servercount") );
				}
				else if("HOURS".equals(timeUnit)){
					serverHistoryInHours.put(rs.getLong("eventtime"),rs.getLong("servercount") );
				}
				else{
					throw new RuntimeException("Error happend when retrieving the damn hisotry");
				}
			}
			stats.serverHistoryInSecs  = serverHistoryInSecs;
			stats.serverHistoryInMins  = serverHistoryInMins;
			stats.serverHistoryInHours = serverHistoryInHours;
			rs.close();    
			return stats;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
