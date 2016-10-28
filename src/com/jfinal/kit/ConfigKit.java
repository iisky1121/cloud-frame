package com.jfinal.kit;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ConfigKit {
	private static Map<String,String> config = new HashMap<String, String>();
	static{
		config = (Map)PropKit.use("config.txt").getProperties();
	}
	
	public static String get(String key){
		return get(key, null);
	}
	public static String get(String key, String defaultValue){
		return config.get(key)==null?defaultValue:config.get(key);
	}
	
	public static Integer getInt(String key){
		return get(key)==null?null:Integer.valueOf(get(key));
	}
	public static Integer getInt(String key, int defaultValue){
		return get(key)==null?defaultValue:Integer.valueOf(get(key));
	}
	
	public static Long getLong(String key){
		return get(key)==null?null:Long.valueOf(get(key));
	}
	public static Long getLong(String key, long defaultValue){
		return get(key)==null?defaultValue:Long.valueOf(get(key));
	}
	
	public static Boolean getBoolean(String key){
		return get(key)==null?null:Boolean.valueOf(get(key));
	}
	public static Boolean getBoolean(String key, boolean defaultValue){
		return get(key)==null?defaultValue:Boolean.valueOf(get(key));
	}
	
	public static Double getDouble(String key){
		return get(key)==null?null:Double.valueOf(get(key));
	}
	public static Double getDouble(String key, double defaultValue){
		return get(key)==null?defaultValue:Double.valueOf(get(key));
	}
}
