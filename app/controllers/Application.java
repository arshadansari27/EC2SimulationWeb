package controllers;

import play.data.validation.Constraints.*;

import play.*;
import play.mvc.*;
import play.mvc.BodyParser.Json;

import views.html.*;
import models.Task;
import play.data.*;
import java.util.*;
import java.util.Map.Entry;

import org.codehaus.jackson.node.ObjectNode;
import org.olivelabs.simulation.StatisticsCollector;

public class Application extends Controller {
  

  static Form<Task> taskForm = form(Task.class);
  public static Result index() {
	  return redirect(routes.Application.tasks());
  }
  
  public static Result tasks(){
	  return ok(views.html.index.render(Task.all(), taskForm));
  }
  
  public static Result newTask(){
	  Form<Task> filledForm = taskForm.bindFromRequest();
	  if(filledForm.hasErrors()) {
		  flash("error","Oops, something is <span style=\"color:@color\">wrong</span>");
		   
	    return badRequest(
	      views.html.index.render(Task.all(), filledForm)
	    );
	  } else {
		  Task t = filledForm.get();
		  t.id = "Task"+new Random().nextInt();
	    Task.create(t);
	    flash("success","The task has been <span style=\"color:@color\">created</span>!");
	    return redirect(routes.Application.tasks());  
	  }
  }
  
  public static Result deleteTask(String id){
	  Task.delete(id);
	  return redirect(routes.Application.tasks());
  }
  
  @BodyParser.Of(Json.class)
  public static Result progress(){
	  List<Task> allTasks = Task.all();
	  
	  ObjectNode result = play.libs.Json.newObject();
	  if(allTasks!=null && !allTasks.isEmpty()){
		  for(Task task : allTasks){
			  Double d = task.getProgress(task);
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
	  ObjectNode result = play.libs.Json.newObject();
	  result.put("status", "KO");
	  StatisticsCollector stats = Task.getDetails(id);
	  if(stats!=null){
		  result.put("simulation", getJsonNodes(stats));
		  return ok(result);
	  }
	  else{
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
		ObjectNode history = play.libs.Json.newObject();
		history.put("SECONDS", secondsNode);
		history.put("MINUTES", minsNode);
		history.put("HOURS", hrsNode);
		return history;
		
  }
}