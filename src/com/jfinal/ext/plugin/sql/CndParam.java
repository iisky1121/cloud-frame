package com.jfinal.ext.plugin.sql;

import com.jfinal.ext.kit.ArrayKit;
import com.jfinal.ext.kit.DataKit;
import com.jfinal.ext.plugin.sql.Cnd.Type;
import com.jfinal.kit.StrKit;

import java.util.Collection;

class CndParam {
	private String key;
	private Object value;
	private Cnd.Type type;
	private Class<?> classType;
	private Cnd.Symbol symbol;

	public CndParam(String key, Cnd.Type type) {
		this.key = key;
		this.type = type;
	}

	public CndParam(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public CndParam(String key, Cnd.Type type, Object value) {
		this.key = key;
		this.type = type;
		setValue(value);
	}

	public CndParam(String key, Object value, Type type, Class<?> classType) {
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

	CndParam setSymbol(Cnd.Symbol symbol) {
		this.symbol = symbol;
		return this;
	}

	CndParam setType(Type type) {
		this.type = type;
		return this;
	}

	CndParam setValue(Object value) {
		this.value = value;
		if(value != null)
			this.classType = value.getClass();
		return this;
	}

	public Object getValue() {
		return value;
	}

	public Class<?> getClassType() {
		return classType;
	}

	
	public static CndParam create(String key, Object val){
		return create(key, val, val.getClass());
	}
	
	public static CndParam create(String key, Object val, Class<?> classType){
		if(val instanceof String){
			String value = (String)val;
			if(!StrKit.isBlank(value)){
				//判断属性值 between_and
				if((DataKit.isDateTime(classType) || DataKit.isDate(classType) || DataKit.isNumber(classType))
						&& value.indexOf("-") != -1 && value.split("-").length == 2){
					if(DataKit.isNumber(classType)){
						return new CndParam(key,Cnd.Type.between_and,value.split("-"));
					}
					else{
						return new CndParam(key,Cnd.Type.between_and,CndKit.timeFmt(value.split("-"), classType));
					}
				}
				//判断属性值 not equal
				else if((value.startsWith("!") || value.startsWith("<>")) && value.indexOf(",") == -1){
					value = value.replaceFirst("!", "");
					return new CndParam(key,Cnd.Type.not_equal,value);
				}
				//判断属性值 not in
				else if(value.startsWith("!") && value.indexOf(",") != -1){
					value = value.replaceFirst("!", "");
					return new CndParam(key,Cnd.Type.not_in,value.split("-"));
				}
				//判断属性值 in
				else if(value.indexOf(",") != -1){
					return new CndParam(key,Cnd.Type.in,value.split(","));
				}
				//判断属性值 >=
				else if(value.startsWith(">=")){
					value = value.replaceFirst(">=", "");
					return new CndParam(key,Cnd.Type.greater_equal,DataKit.isDateTime(classType)?CndKit.timeFmt(value, classType, true):value);
				}
				//判断属性值 >
				else if(value.startsWith(">")){
					value = value.replaceFirst(">", "");
					return new CndParam(key,Cnd.Type.greater_then,DataKit.isDateTime(classType)?CndKit.timeFmt(value, classType, true):value);
				}
				//判断属性值 <=
				else if(value.startsWith("<=")){
					value = value.replaceFirst("<=", "");
					return new CndParam(key,Cnd.Type.less_equal,DataKit.isDateTime(classType)?CndKit.timeFmt(value, classType, false):value);
				}
				//判断属性值 <
				else if(value.startsWith("<")){
					value = value.replaceFirst("<", "");
					return new CndParam(key,Cnd.Type.less_then,DataKit.isDateTime(classType)?CndKit.timeFmt(value, classType, false):value);
				}
				//判断属性值 %*%
				else if(value.startsWith("%") && value.endsWith("%")){
					value = value.replaceFirst("%", "");
					return new CndParam(key,Cnd.Type.fuzzy,value);
				}
				//判断属性值 %*
				else if(value.startsWith("%")){
					value = value.replaceFirst("%", "");
					return new CndParam(key,Cnd.Type.fuzzy_left,value);
				}
				//判断属性值 *%
				else if(value.endsWith("%")){
					value = value.replaceFirst("%", "");
					return new CndParam(key,Cnd.Type.fuzzy_right,value);
				}
				//判断属性值 isNull
				else if(Cnd.$IS_NULL.equals(value)){
					return new CndParam(key,Cnd.Type.empty, null);
				}
				//判断属性值 isNotNull
				else if(Cnd.$IS_NOT_NULL.equals(value)){
					return new CndParam(key,Cnd.Type.not_empty, null);
				}
				//判断属性值 isEmpty
				else if(Cnd.$IS_EMPTY.equals(value)){
					return new CndParam(key,Cnd.Type.equal, "");
				}
				//判断属性值 isNotEmpty
				else if(Cnd.$IS_NOT_EMPTY.equals(value)){
					return new CndParam(key,Cnd.Type.not_equal, "");
				}
			}
		}
		else if(val instanceof Object[] || val instanceof Collection){
			return new CndParam(key,Cnd.Type.in,val);
		}
		else if(val.getClass().isArray()){
			return new CndParam(key, Cnd.Type.in,ArrayKit.toObjectArray(val));
		}
		return new CndParam(key,Cnd.Type.equal, val);
	}
	
}
