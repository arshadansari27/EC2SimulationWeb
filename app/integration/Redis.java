package integration;

import org.olivelabs.util.marshalling.JSONUtil;

import redis.clients.jedis.Jedis;

public class Redis {
	Jedis jedis = new Jedis("localhost");
	
	public void set(String key, Object value){
		jedis.set(key,new String(JSONUtil.objectToJson(value)));
	}
	
	public Object get(String key, Class cls){
		return JSONUtil.jsonToObject(jedis.get(key).getBytes(),cls);	
	}
	
	public void publish(String channel, Object message){
		jedis.publish(channel, new String(JSONUtil.objectToJson(message)));
	}
	
	public void delete(String key){
		jedis.del(key);
	}

}
