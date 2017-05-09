package com.jfinal.ext.plugin.sql;

import com.jfinal.ext.kit.ArrayKit;
import com.jfinal.ext.kit.DataKit;
import com.jfinal.ext.kit.ModelKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class CndKit {
	private final static String START_TIME_STR="####0101000000";
	private final static String END_TIME_STR="####1231235959";
	public static String[] timeFmt(String[] timeStrs, Class<?> classType){
		if(timeStrs == null || timeStrs.length < 2){
			return timeStrs;
		}
		return new String[]{timeFmt(timeStrs[0], classType, true), timeFmt(timeStrs[1], classType, false)};
	}
	
	static String dateFmt(String timeStr, boolean isStart){
		return timeFmt(timeStr, java.sql.Date.class, isStart);
	}
	
	static String dateTimeFmt(String timeStr, boolean isStart){
		return timeFmt(timeStr, java.util.Date.class, isStart);
	}
	
	static String timeFmt(String timeStr, Class<?> classType, boolean isStart){
		if(StrKit.isBlank(timeStr)){
			return timeStr;
		}
		if(DataKit.isDate(classType)){
			return timeFormat(timeStr, isStart);
		}
		else if(DataKit.isDateTime(classType)){
			return dateTimeFormat(timeStr, isStart);
		}
		return timeStr;
	}

	private static String timeFormat(String timeStr, boolean isStart){
		timeStr = subTimeStr(timeStr, isStart, 8);
		return new StringBuffer()
				.append(timeStr.substring(0,4))
				.append("-")
				.append(timeStr.substring(4,6))
				.append("-")
				.append(timeStr.substring(6,8))
				.toString();
	}

	private static String dateTimeFormat(String timeStr, boolean isStart){
		timeStr = subTimeStr(timeStr, isStart, 14);
		return new StringBuffer()
				.append(timeStr.substring(0,4))
				.append("-")
				.append(timeStr.substring(4,6))
				.append("-")
				.append(timeStr.substring(6,8))
				.append(" ")
				.append(timeStr.substring(8,10))
				.append(":")
				.append(timeStr.substring(10,12))
				.append(":")
				.append(timeStr.substring(12,14))
				.toString();
	}
	
	private static String subTimeStr(String timeStr, boolean isStart, int length){
		int len = timeStr.length();
		if(len >length){
			return timeStr.substring(0, length);
		}
		else if(len == length){
			return timeStr;
		}
		else if(len >= 4){
			return timeStr.concat((isStart?START_TIME_STR:END_TIME_STR).substring(len, length));
		}
		else{
			throw new IllegalArgumentException("时间参数最少需要输入年份（4位）");
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static Object[] toValues(Cnd.Type queryType, Object object){
		Object[] values = null;
    	if (object instanceof Collection) {
			values = ((Collection)object).toArray();
    	}
    	else if(object instanceof Object[]){
    		values = ((Object[]) object);
    	}
    	else if(object.getClass().isArray()){
			return ArrayKit.toObjectArray(object);
		}
    	else if(object instanceof String && queryType.equals(Cnd.Type.between_and)){
    		values = ((String) object).split("-");
    	}
    	else if(object instanceof String && (queryType.equals(Cnd.Type.not_in) || queryType.equals(Cnd.Type.in))){
    		values = ((String) object).split(",");
    	}
    	return values;
	}
	

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String,Object> formatValues(Map<String, String[]> paras, Class<?> entryClass){
		Map<String,Object> map = new HashMap<String, Object>();
		
		CndParam param;
		if(Model.class.isAssignableFrom(entryClass)){
			Model model = ModelKit.newInstance(entryClass);
	    	if(model != null){
	    		Set<Entry<String, Class<?>>> attrs = model.getColumns().entrySet();
	    		for(Entry<String, Class<?>> entry : attrs){
	    			if(paras.containsKey(entry.getKey()) && !StrKit.isBlank(paras.get(entry.getKey())[0])){
	        			param = CndParam.create(entry.getKey(), paras.get(entry.getKey())[0], entry.getValue());
	        			map.put(param.getKey(), param.getValue());
	        		}
	    		}
	    	}
		}
		else if(IBean.class.isAssignableFrom(entryClass)){
			Field[] fields = entryClass.getDeclaredFields();
    		for(Field field : fields){
    			if(paras.containsKey(field.getName()) && !StrKit.isBlank(paras.get(field.getName())[0])){
        			param = CndParam.create(field.getName(), paras.get(field.getName())[0], field.getType());
        			map.put(param.getKey(), param.getValue());
        		}
    		}
		}
		return map;
	}
}
