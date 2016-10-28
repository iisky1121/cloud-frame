package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jfinal.kit.StrKit;

class CndBuild {
	/**
	 * 组建各种sql及赋值
	 */
	public static void buildSQL(StringBuilder sb, Param param, ArrayList<Object> params) {
		buildSQL(sb, param.getType(), param.getKey(), param.getValue(), params);
	}
	public static void buildSQL(StringBuilder sb, Cnd.Type queryType, String fieldName, Object fieldValue, ArrayList<Object> params) {
        // 非空的时候进行设置
        if (StrKit.notNull(fieldValue) && StrKit.notNull(fieldName)) {
            if (Cnd.Type.equal.equals(queryType)) {
                sb.append(" and " +fieldName + " = ? ");
                params.add(fieldValue);
            } 
            else if (Cnd.Type.not_equal.equals(queryType)) {
                sb.append(" and " +fieldName + " <> ? ");
                params.add(fieldValue);
            } 
            else if (Cnd.Type.less_then.equals(queryType)) {
                sb.append(" and " +fieldName + " < ? ");
                params.add(fieldValue);
            } 
            else if (Cnd.Type.less_equal.equals(queryType)) {
                sb.append(" and " +fieldName + " <= ? ");
                params.add(fieldValue);
            } 
            else if (Cnd.Type.greater_then.equals(queryType)) {
                sb.append(" and " +fieldName + " > ? ");
                params.add(fieldValue);
            } 
            else if (Cnd.Type.greater_equal.equals(queryType)) {
                sb.append(" and " +fieldName + " >= ? ");
                params.add(fieldValue);
            } 
            else if (Cnd.Type.fuzzy.equals(queryType)) {
                sb.append(" and " +fieldName + " like ? ");
                params.add("%" + fieldValue + "%");
            } 
            else if (Cnd.Type.fuzzy_left.equals(queryType)) {
                sb.append(" and " +fieldName + " like ? ");
                params.add("%" + fieldValue);
            } 
            else if (Cnd.Type.fuzzy_right.equals(queryType)) {
                sb.append(" and " +fieldName + " like ? ");
                params.add(fieldValue + "%");
            } 
            else if (Cnd.Type.in.equals(queryType)) {
            	Object[] values = toValues(fieldValue);
            	if(values == null){
            		throw new IllegalArgumentException("使用IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
            	}
                StringBuilder instr = new StringBuilder();
                sb.append(" and " +fieldName + " in (");
                for (Object obj : values) {
                    instr.append(StrKit.notBlank(instr.toString()) ? ",?" : "?");
                    params.add(obj);
                }
                sb.append(instr + ") ");
            } 
            else if (Cnd.Type.not_in.equals(queryType)) {
            	Object[] values = toValues(fieldValue);
            	if(values == null){
            		throw new IllegalArgumentException("使用Not IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
            	}
                StringBuilder instr = new StringBuilder();
                sb.append(" and " +fieldName + " not in (");
                for (Object obj : values) {
                    instr.append(StrKit.notBlank(instr.toString()) ? ",?" : "?");
                    params.add(obj);
                }
                sb.append(instr + ") ");
            } 
            else if (Cnd.Type.between_and.equals(queryType)) {
            	Object[] values = toValues(fieldValue);
            	if(values == null){
            		throw new IllegalArgumentException("使用BETWEEN And条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔),且长度为2");
            	}
            	
            	if (values.length != 2) {
        			throw new IllegalArgumentException(String.format("Illegal between params size:%s", values.length));
        		}
            	sb.append(" and (" +fieldName + " between ? and ?) ");
                params.add(values[0]);
                params.add(values[1]);
            }
        } 
        else {
            if (Cnd.Type.empty.equals(queryType)) {
                sb.append(" and " +fieldName + " is null ");
            } 
            else if (Cnd.Type.not_empty.equals(queryType)) {
                sb.append(" and " +fieldName + " is not null ");
            }
        }
    }
	
	@SuppressWarnings("rawtypes")
	private static Object[] toValues(Object object){
		Object[] values = null;
    	if (object instanceof Collection) {
			values = ((Collection)object).toArray();
    	}
    	else if(object instanceof Object[]){
    		values = ((String[]) object);
    	}
    	else if(object instanceof String){
    		values = ((String) object).split(",");
    	}
    	return values;
	}
	
	/**
	 * 组建各种order by及赋值
	 */
	public static void buildOrderBy(Map<String,String> orderByMap, StringBuilder sql){
		int i = 0;
		for(Entry<String,String> entry : orderByMap.entrySet()){
			if(i == 0){
				sql.append(" order by ");
			}
			sql.append(entry.getKey()+ " " + entry.getValue());
			if(i != orderByMap.size() -1){
				sql.append(",");
			}
			i++;
		}
	}
	
	/**
	 * 组装全局模糊查询条件
	 */
	public static void bulidFuzzyQuery(Map<String,String> fuzzyQueryMap, StringBuilder sql, List<Object> paramList){
		if(fuzzyQueryMap.size() > 0){
			int i = 0;
			sql.append(" and (");
			for(Entry<String,String> entry: fuzzyQueryMap.entrySet()){
				if (i > 0) {
		    		sql.append(" or ");
		    	}
				sql.append(entry.getKey() + " like ?");
		    	paramList.add("%" + entry.getValue() + "%");
		    	i++;
			}
		    sql.append(")");  
		    i++;
		}
	}
}
