package models;

import integration.Redis;


import java.util.*;
import java.util.concurrent.TimeUnit;

import org.olivelabs.simulation.Parameters;
import org.olivelabs.simulation.StatisticsCollector;

import akka.util.Duration;

import play.data.validation.Constraints.*;
import play.libs.Akka;

public class Task {
	
	public String id;
	@Required
	public String label;
	public String description;
	public int concurrentRequestLimit;
	public int maxServer;
	public int hoursToSimulate;
	public long averageRequestPerSecond =  200;
	public long maxRequestPerSecond = 500;
	public long minRequestPerSecond = 100;
	public int averageRequestServiceTime = 15;
	public int maxRequestServiceTime = 30;
	public int minRequestServiceTime = 3;
	

	
	public static List<Task> all(){
		Map<String, Task> tasks = TaskPeer.getAllTask();
		if(tasks!=null){
			List<Task> taskList = new ArrayList<Task>(tasks.values());
			return taskList;
		}
		else return null;
	}
	
	public static Task getTask(String id){
		
		
		org.olivelabs.model.Task<Parameters, StatisticsCollector> taskProper =  new  org.olivelabs.model.Task<Parameters, StatisticsCollector>();
		
		Redis redis = new Redis();
		taskProper = (org.olivelabs.model.Task<Parameters, StatisticsCollector>) redis.get(id,taskProper.getClass());
		
		Task task = new Task();
		Parameters params = taskProper.inputData;
		task.concurrentRequestLimit = params.concurrentRequestLimit ;
		task.maxServer=params.maxServer;
		task.hoursToSimulate = (int) (params.MAX_CLOCK / 3600);
		task.averageRequestPerSecond = params.averageRequestPerSecond;
		task.maxRequestPerSecond = params.maxRequestPerSecond;
		task.minRequestPerSecond = params.minRequestPerSecond;
		task.averageRequestServiceTime = params.averageRequestServiceTime;
		task.maxRequestServiceTime = params.maxRequestServiceTime;
		task.minRequestServiceTime = params.minRequestServiceTime;
		return task;
	}
	
	public static void create(Task task){
		TaskPeer.insertTask(task);
		final org.olivelabs.model.Task<Parameters, StatisticsCollector> taskProper = new org.olivelabs.model.Task<Parameters, StatisticsCollector>();

		Parameters params = new Parameters();
		params.concurrentRequestLimit = task.concurrentRequestLimit;
		params.maxServer = task.maxServer;
		params.MAX_CLOCK = (long) task.hoursToSimulate * 3600;
		params.averageRequestPerSecond =  task.averageRequestPerSecond ;
		params.maxRequestPerSecond = task.maxRequestPerSecond ;
		params.minRequestPerSecond = task.minRequestPerSecond;
		params.averageRequestServiceTime = task.averageRequestServiceTime;
		params.maxRequestServiceTime = task.maxRequestServiceTime;
		params.minRequestServiceTime = task.minRequestServiceTime;
		
		taskProper.taskId  = task.id;
		taskProper.inputData = params;
		taskProper.status = "CREATED";
		
		Akka.system().scheduler().scheduleOnce(
				  Duration.create(100, TimeUnit.MILLISECONDS),
				  new Runnable() {
				    public void run() {
				    	Redis redis = new Redis();
				    	redis.set(taskProper.taskId, taskProper);
				    }
				  }
				); 
	}
	
	public static void delete(String id){
		if(TaskPeer.deleteTask(id)){
			Redis redis = new Redis();
	    	redis.delete(id);
		}
	}
	
	public static StatisticsCollector getDetails(String id){
		if(TaskPeer.getTask(id)!=null){
			Redis redis = new Redis();
			org.olivelabs.model.Task<Parameters, StatisticsCollector> t = new  org.olivelabs.model.Task<Parameters, StatisticsCollector>();
			t = (org.olivelabs.model.Task<Parameters, StatisticsCollector>) redis.get(id,t.getClass());
			return t.outputData;
		}
		return null;
	}
	
	public static String getProgress(Task task){
		if(TaskPeer.getTask(task.id)!=null){
			Redis redis = new Redis();
			org.olivelabs.model.Task<Parameters, StatisticsCollector> t = new  org.olivelabs.model.Task<Parameters, StatisticsCollector>();
			t = (org.olivelabs.model.Task<Parameters, StatisticsCollector>) redis.get(task.id,t.getClass());
			return t.status;
		}
		return null;
	}
}
