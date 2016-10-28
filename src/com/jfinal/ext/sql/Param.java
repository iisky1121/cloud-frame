package com.jfinal.ext.sql;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;

import com.jfinal.ext.kit.ArrayKit;
import com.jfinal.ext.sql.Cnd.Type;
import com.jfinal.kit.StrKit;

class Param {
	private String key;
	private Object value;
	private Cnd.Type type;
	private Class<?> classType;

	public Param(String key, Cnd.Type type) {
		this.key = key;
		this.type = type;
	}

	public Param(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Param(String key, Cnd.Type type, Object value) {
		this.key = key;
		this.type = type;
		this.value = value;
	}
	public Param(String key, Object value, Type type, Class<?> classType) {
		this.key = key;
		this.value = value;
		this.type = type;
		this.classType = classType;
	}

	public String getKey() {
		return key;
	}

	public Cnd.Type getType() {
		return type;
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getClassType() {
		return classType;
	}
	
	public static Param create(String key, Object val, Class<?> classType){
		if(val instanceof String){
			String value = (String)val;
			if(!StrKit.isBlank(value)){
				//判断属性值 between_and
				if((isTime(classType) || isNumber(classType)) 
						&& value.indexOf("-") != -1 && value.split("-").length == 2){
					if(isTime(classType)){
						return new Param(key,Cnd.Type.between_and,timeFmt(value.split("-"), classType));
					}
					else{
						return new Param(key,Cnd.Type.between_and,value.split("-"));
					}
				}
				//判断属性值 not equal
				else if((value.startsWith("!") || value.startsWith("<>")) && value.indexOf(",") == -1){
					value = value.replaceFirst("!", "");
					return new Param(key,Cnd.Type.not_equal,value);
				}
				//判断属性值 not in
				else if(value.startsWith("!") && value.indexOf(",") != -1){
					value = value.replaceFirst("!", "");
					return new Param(key,Cnd.Type.not_in,value.split("-"));
				}
				//判断属性值 in
				else if(value.indexOf(",") != -1){
					return new Param(key,Cnd.Type.in,value.split(","));
				}
				//判断属性值 >=
				else if(value.startsWith(">=")){
					value = value.replaceFirst(">=", "");
					return new Param(key,Cnd.Type.greater_equal,isTime(classType)?timeFmt(value, classType, true):value);
				}
				//判断属性值 >
				else if(value.startsWith(">")){
					value = value.replaceFirst(">", "");
					return new Param(key,Cnd.Type.greater_then,isTime(classType)?timeFmt(value, classType, true):value);
				}
				//判断属性值 <=
				else if(value.startsWith("<=")){
					value = value.replaceFirst("<=", "");
					return new Param(key,Cnd.Type.less_equal,isTime(classType)?timeFmt(value, classType, false):value);
				}
				//判断属性值 <
				else if(value.startsWith("<")){
					value = value.replaceFirst("<", "");
					return new Param(key,Cnd.Type.less_then,isTime(classType)?timeFmt(value, classType, false):value);
				}
				//判断属性值 %*%
				else if(value.startsWith("%") && value.endsWith("%")){
					value = value.replaceFirst("%", "");
					return new Param(key,Cnd.Type.fuzzy,value);
				}
				//判断属性值 %*
				else if(value.startsWith("%")){
					value = value.replaceFirst("%", "");
					return new Param(key,Cnd.Type.fuzzy_left,value);
				}
				//判断属性值 *%
				else if(value.endsWith("%")){
					value = value.replaceFirst("%", "");
					return new Param(key,Cnd.Type.fuzzy_right,value);
				}
				//判断属性值 isNull
				else if(Cnd.IS_NULL.equals(value)){
					return new Param(key,Cnd.Type.empty, null);
				}
				//判断属性值 isNotNull
				else if(Cnd.IS_NOT_NULL.equals(value)){
					return new Param(key,Cnd.Type.not_empty, null);
				}
				//判断属性值 isEmpty
				else if(Cnd.IS_EMPTY.equals(value)){
					return new Param(key,Cnd.Type.equal, "");
				}
				//判断属性值 isNotEmpty
				else if(Cnd.IS_NOT_EMPTY.equals(value)){
					return new Param(key,Cnd.Type.not_equal, "");
				}
				else if (key.toLowerCase().endsWith("name")
						|| key.toLowerCase().endsWith("content")
						|| key.toLowerCase().endsWith("remark")) {
					return new Param(key,Cnd.Type.fuzzy,value);
				}
			}
		}
		else if(val instanceof Object[] || val instanceof Collection){
			return new Param(key,Cnd.Type.in,val);
		}
		else if(val.getClass().isArray()){
			return new Param(key, Cnd.Type.in,ArrayKit.toObjectArray(val));
		}
		return new Param(key,Cnd.Type.equal, val);
	}
	
	private static boolean isTime(Class<?> classType){
		return classType.equals(Date.class) 
				|| classType.equals(Time.class)
				|| classType.equals(Timestamp.class)
				|| classType.equals(java.util.Date.class);
	}
	
	private static boolean isNumber(Class<?> classType){
		return classType.equals(Integer.class) || classType.equals(Long.class) || classType.equals(Double.class);
	}
	
	private final static String START_TIME_STR="####0101000000";
	private final static String END_TIME_STR="####1231235959";
	private static String[] timeFmt(String[] timeStrs, Class<?> classType){
		if(timeStrs == null || timeStrs.length < 2){
			return timeStrs;
		}
		return new String[]{timeFmt(timeStrs[0], classType, true), timeFmt(timeStrs[1], classType, false)};
	}
	private static String timeFmt(String timeStr, Class<?> classType, boolean isStart){
		if(StrKit.isBlank(timeStr)){
			return timeStr;
		}
		if(classType.equals(Date.class)){
			return subTimeStr(timeStr, isStart, 8);
		}
		else if(classType.equals(Timestamp.class)){
			return subTimeStr(timeStr, isStart, 14);
		}
		return timeStr;
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
}
