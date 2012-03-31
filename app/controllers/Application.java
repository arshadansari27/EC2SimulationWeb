package controllers;

import play.data.validation.Constraints.*;

import play.*;
import play.mvc.*;
import play.mvc.BodyParser.Json;

import views.html.*;
import models.SimulationTask;
import play.data.*;
import java.util.*;
import java.util.Map.Entry;

import org.codehaus.jackson.node.ObjectNode;
import org.olivelabs.simulation.StatisticsCollector;

public class Application extends Controller {
  

  static Form<SimulationTask> taskForm = form(SimulationTask.class);
  public static Result index() {
	  return redirect(routes.Application.tasks());
  }
  
  public static Result tasks(){
	  return ok(views.html.index.render(SimulationTask.all(), taskForm));
  }
  
  public static Result newTask(){
	  Form<SimulationTask> filledForm = taskForm.bindFromRequest();
	  if(filledForm.hasErrors()) {
		  flash("error","Oops, something is <span style=\"color:@color\">wrong</span>");
		   
	    return badRequest(
	      views.html.index.render(SimulationTask.all(), filledForm)
	    );
	  } else {
		  SimulationTask t = filledForm.get();
		  t.id = "Task"+new Random().nextInt();
		  SimulationTask.create(t);
	    flash("success","The task has been <span style=\"color:@color\">created</span>!");
	    return redirect(routes.Application.tasks());  
	  }
  }
  
  public static Result deleteTask(String id){
	  SimulationTask.delete(id);
	  return redirect(routes.Application.tasks());
  }
  
  @BodyParser.Of(Json.class)
  public static Result progress(){
	  List<SimulationTask> allTasks = SimulationTask.all();
	  
	  ObjectNode result = play.libs.Json.newObject();
	  if(allTasks!=null && !allTasks.isEmpty()){
		  for(SimulationTask task : allTasks){
			  String d = task.getProgress(task);
			  if(d != null) result.put(task.id, d);
			  else result.put(task.id, "100");
		  }
		  return ok(result);
	  }
	  else if(allTasks.isEmpty()){
		  return ok(result);
	  }
	  else{
		  result.put("message", "Couldn't gets progress");
		  return badRequest(result);		  
	  }
  }
  
  @BodyParser.Of(Json.class)
  public static Result details(String id){
	  
	  StatisticsCollector stats = SimulationTask.getDetails(id);
	  if(stats!=null){
		  return ok(getJsonNodes(stats));
	  }
	  else{
		  ObjectNode result = play.libs.Json.newObject();
		  result.put("message", "Couldn't gets statistics");
		  return badRequest(result);		  
	  }
  }
  
  private static ObjectNode getJsonNodes(StatisticsCollector stats){
	  	Map<Long, Long> serverHistoryInSecs = stats.serverHistoryInSecs;
		Map<Long, Long> serverHistoryInMins = stats.serverHistoryInMins;
		Map<Long, Long> serverHistoryInHours = stats.serverHistoryInHours;
		ObjectNode secondsNode = play.libs.Json.newObject();
		ObjectNode minsNode = play.libs.Json.newObject();
		ObjectNode hrsNode = play.libs.Json.newObject();
		
		for(Entry<Long,Long> serversPerSec : serverHistoryInSecs.entrySet()){
			secondsNode.put(""+serversPerSec.getKey(), ""+serversPerSec.getValue());
		}
		for(Entry<Long,Long> serversPerMin : serverHistoryInMins.entrySet()){
			minsNode.put(""+serversPerMin.getKey(), ""+serversPerMin.getValue());
		}
		for(Entry<Long,Long> serversPerHour : serverHistoryInHours.entrySet()){
			hrsNode.put(""+serversPerHour.getKey(), ""+serversPerHour.getValue());
		}
		ObjectNode output = play.libs.Json.newObject();
		output.put("SECONDS", secondsNode);
		output.put("MINUTES", minsNode);
		output.put("HOURS", hrsNode);
		output.put("TOTAL_DISPATCHED", stats.requestDipatchedCount);
		output.put("TOTAL_REJECTED", stats.requestRejectedCount);
		output.put("AVG_SERVICE_TIME", stats.averageServiceTime);
		output.put("AVG_WAIT_TIME", stats.averageWaitTime);
		output.put("AVG_ACTUAL_SERVICE_TIME", stats.averageActualServiceTime);
		output.put("AVG_SERVER_UTILIZATION", stats.averageServerUtilization);
		output.put("AVG_SERVER_USAGE", stats.averageServerUsage);
		return output;
		
  }
}