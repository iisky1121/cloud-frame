package com.jfinal.ext.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jfinal.ext.kit.ModelKit;
import com.jfinal.ext.plugin.jsql.JSqlKit;
import com.jfinal.ext.plugin.jsql.core.SqlArgs;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class CndKit {
	@SuppressWarnings("rawtypes")
	public static Object[] toValues(Cnd.Type queryType, Object object){
		Object[] values = null;
    	if (object instanceof Collection) {
			values = ((Collection)object).toArray();
    	}
    	else if(object instanceof Object[]){
    		values = ((String[]) object);
    	}
    	else if(object instanceof String && queryType.equals(Cnd.Type.between_and)){
    		values = ((String) object).split("-");
    	}
    	else if(object instanceof String && (queryType.equals(Cnd.Type.not_in) || queryType.equals(Cnd.Type.in))){
    		values = ((String) object).split(",");
    	}
    	return values;
	}
	
	public static Object[] buildValue(Cnd.Type queryType, Object fieldValue) {
        // 非空的时候进行设置
        if (StrKit.notNull(fieldValue)) {
            if (Cnd.Type.equal.equals(queryType)) {
            	return new Object[]{" = ? ", fieldValue};
            } 
            else if (Cnd.Type.not_equal.equals(queryType)) {
            	return new Object[]{" <> ? ", fieldValue};
            } 
            else if (Cnd.Type.less_then.equals(queryType)) {
            	return new Object[]{" < ? ", fieldValue};
            } 
            else if (Cnd.Type.less_equal.equals(queryType)) {
            	return new Object[]{" <= ? ", fieldValue};
            } 
            else if (Cnd.Type.greater_then.equals(queryType)) {
            	return new Object[]{" > ? ", fieldValue};
            } 
            else if (Cnd.Type.greater_equal.equals(queryType)) {
            	return new Object[]{" >= ? ", fieldValue};
            } 
            else if (Cnd.Type.fuzzy.equals(queryType)) {
            	return new Object[]{" like ? ", "%" + fieldValue + "%"};
            } 
            else if (Cnd.Type.fuzzy_left.equals(queryType)) {
            	return new Object[]{" like ? ", "%" + fieldValue};
            } 
            else if (Cnd.Type.fuzzy_right.equals(queryType)) {
            	return new Object[]{" like ? ", fieldValue + "%"};
            } 
            else if (Cnd.Type.in.equals(queryType)) {
            	Object[] values = CndKit.toValues(queryType, fieldValue);
            	if(values == null){
            		throw new IllegalArgumentException("使用IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
            	}
                StringBuilder instr = new StringBuilder();
                for (int i =0; i< values.length; i++) {
                    instr.append(StrKit.notBlank(instr.toString()) ? ",?" : "?");
                }
                return new Object[]{" in (" + instr + ") ", values};
            } 
            else if (Cnd.Type.not_in.equals(queryType)) {
            	Object[] values = CndKit.toValues(queryType, fieldValue);
            	if(values == null){
            		throw new IllegalArgumentException("使用Not IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
            	}
                StringBuilder instr = new StringBuilder();
                for (int i =0; i< values.length; i++) {
                    instr.append(StrKit.notBlank(instr.toString()) ? ",?" : "?");
                }
                return new Object[]{" not in (" + instr + ") ", values};
            } 
            else if (Cnd.Type.between_and.equals(queryType)) {
            	Object[] values = CndKit.toValues(queryType, fieldValue);
            	if(values == null){
            		throw new IllegalArgumentException("使用BETWEEN And条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔),且长度为2");
            	}
            	
            	if (values.length != 2) {
        			throw new IllegalArgumentException(String.format("Illegal between params size:%s", values.length));
        		}
                return new Object[]{" between ? and ? ", values};
            }
        } 
        else {
            if (Cnd.Type.empty.equals(queryType)) {
            	return new Object[]{" is null ", null};
            } 
            else if (Cnd.Type.not_empty.equals(queryType)) {
            	return new Object[]{" is not null ", null};
            }
        }
        return null;
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String,Object> formatValues(Map<String, String[]> paras, Class<?> entryClass){
		Map<String,Object> map = new HashMap<String, Object>();
		
		Param param;
		if(Model.class.isAssignableFrom(entryClass)){
			Model model = ModelKit.newInstance(entryClass);
	    	if(model != null){
	    		Set<Entry<String, Class<?>>> attrs = model.getColumns().entrySet();
	    		for(Entry<String, Class<?>> entry : attrs){
	    			if(paras.containsKey(entry.getKey()) && !StrKit.isBlank(paras.get(entry.getKey())[0])){
	        			param = Param.create(entry.getKey(), paras.get(entry.getKey())[0], entry.getValue());
	        			map.put(param.getKey(), param.getValue());
	        		}
	    		}
	    	}
		}
		else if(IBean.class.isAssignableFrom(entryClass)){
			Field[] fields = entryClass.getDeclaredFields();
    		for(Field field : fields){
    			if(paras.containsKey(field.getName()) && !StrKit.isBlank(paras.get(field.getName())[0])){
        			param = Param.create(field.getName(), paras.get(field.getName())[0], field.getType());
        			map.put(param.getKey(), param.getValue());
        		}
    		}
		}
		return map;
	}
	
	public static SqlArgs getSqlArgs(String sqlId){
		return JSqlKit.getSqlArgs(sqlId);
	}
	
	public static SqlArgs getSqlArgs(String sqlId, Object args){
		return JSqlKit.getSqlArgs(sqlId, args);
	}
	
	public static Long getCount(String sqlId, Map<String, String[]> params, Class<?> entryClass){
		return getCount(sqlId, false, params, entryClass);
	}
	public static Long getCount(String sqlId, boolean isGroupBy, Map<String, String[]> params, Class<?> entryClass){
		params.remove("pageNumber");
		params.remove("pageSize");
		Map<String,Object> map = formatValues(params, entryClass);
		SqlArgs sqlArgs = getSqlArgs(sqlId.concat("$count"), map);
		if(isGroupBy){
			return (long) Db.query(sqlArgs.getSql(), sqlArgs.getArgs().toArray()).size();
		}
		return Db.queryLong(sqlArgs.getSql(), sqlArgs.getArgs().toArray());
	}
	
	public static List<Record> getList(String sqlId, Map<String, String[]> params, Class<?> entryClass){
		Map<String,Object> map = formatValues(params, entryClass);
		SqlArgs sqlArgs = getSqlArgs(sqlId, map);
		return Db.find(sqlArgs.getSql(), sqlArgs.getArgs().toArray());
	}
	
	public static List<Record> getList(String sqlId, int pageNumber, int pageSize, Map<String, String[]> params, Class<?> entryClass){
		Map<String,Object> map = formatValues(params, entryClass);
		map.put("limit", pageSize);
		map.put("offset", (pageNumber-1)*pageSize);
		
		SqlArgs sqlArgs = getSqlArgs(sqlId, map);
		return Db.find(sqlArgs.getSql(), sqlArgs.getArgs().toArray());
	}
	
	public static Page<Record> paginate(String sqlId, int pageNumber, int pageSize, Map<String, String[]> params, Class<?> entryClass){
		return paginate(sqlId, pageNumber, pageSize, false, params, entryClass);
	}
	
	public static Page<Record> paginate(String sqlId, int pageNumber, int pageSize, boolean isGroupBy, Map<String, String[]> params, Class<?> entryClass){
		Long totalRow = getCount(sqlId, isGroupBy, params, entryClass);
		if(totalRow != null && totalRow > 0){
			List<Record> list = getList(sqlId, pageNumber, pageSize, params, entryClass);
			
			int total = Integer.parseInt(String.valueOf(totalRow));
			int totalPage = total%pageSize==0?(total/pageSize):(total/pageSize+1);
			return new Page<Record>(list, pageNumber, pageSize, totalPage, total);
		}
		return new Page<Record>(new ArrayList<Record>(), pageNumber, pageSize, 0, 0); 
	}
}