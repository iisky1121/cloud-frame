package com.jfinal.ext.sql;

import java.util.Collection;

import com.jfinal.ext.kit.ArrayKit;
import com.jfinal.ext.sql.Cnd.Type;
import com.jfinal.kit.StrKit;

class Param {
	private String key;
	private Object value;
	private Cnd.Type type;
	private Class<?> classType;
	private Cnd.Symbol symbol;

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

	public Cnd.Symbol getSymbol() {
		return symbol;
	}

	Param setSymbol(Cnd.Symbol symbol) {
		this.symbol = symbol;
		return this;
	}

	Param setType(Type type) {
		this.type = type;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getClassType() {
		return classType;
	}

	
	public static Param create(String key, Object val){
		return create(key, val, val.getClass());
	}
	
	public static Param create(String key, Object val, Class<?> classType){
		if(val instanceof String){
			String value = (String)val;
			if(!StrKit.isBlank(value)){
				//判断属性值 between_and
				if((CndKit.isDateOrTime(classType) || CndKit.isNumber(classType)) 
						&& value.indexOf("-") != -1 && value.split("-").length == 2){
					if(CndKit.isDateOrTime(classType)){
						return new Param(key,Cnd.Type.between_and,CndKit.timeFmt(value.split("-"), classType));
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
					return new Param(key,Cnd.Type.greater_equal,CndKit.isDateOrTime(classType)?CndKit.timeFmt(value, classType, true):value);
				}
				//判断属性值 >
				else if(value.startsWith(">")){
					value = value.replaceFirst(">", "");
					return new Param(key,Cnd.Type.greater_then,CndKit.isDateOrTime(classType)?CndKit.timeFmt(value, classType, true):value);
				}
				//判断属性值 <=
				else if(value.startsWith("<=")){
					value = value.replaceFirst("<=", "");
					return new Param(key,Cnd.Type.less_equal,CndKit.isDateOrTime(classType)?CndKit.timeFmt(value, classType, false):value);
				}
				//判断属性值 <
				else if(value.startsWith("<")){
					value = value.replaceFirst("<", "");
					return new Param(key,Cnd.Type.less_then,CndKit.isDateOrTime(classType)?CndKit.timeFmt(value, classType, false):value);
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
				else if(Cnd.$IS_NULL.equals(value)){
					return new Param(key,Cnd.Type.empty, null);
				}
				//判断属性值 isNotNull
				else if(Cnd.$IS_NOT_NULL.equals(value)){
					return new Param(key,Cnd.Type.not_empty, null);
				}
				//判断属性值 isEmpty
				else if(Cnd.$IS_EMPTY.equals(value)){
					return new Param(key,Cnd.Type.equal, "");
				}
				//判断属性值 isNotEmpty
				else if(Cnd.$IS_NOT_EMPTY.equals(value)){
					return new Param(key,Cnd.Type.not_equal, "");
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
	
}
