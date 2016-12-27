package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
        if ((StrKit.notNull(fieldValue) || queryType.equals(Cnd.Type.empty) || queryType.equals(Cnd.Type.not_empty)) 
        		&& StrKit.notNull(fieldName)) {
        	Object[] values = CndKit.buildValue(queryType, fieldValue);
        	if(values == null){
        		return;
        	}
        	sb.append(Cnd.AND +fieldName + values[0]);
        	if(values[1] == null){
        		return;
        	}
        	
        	if(values[1] instanceof Object[]){
        		for(Object obj : (Object[])values[1]){
        			params.add(obj);
        		}
        	}
        	else{
        		params.add(values[1]);
        	}
        }
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
	 * 组建各种group by及赋值
	 */
	public static void buildGroupBy(Set<String> groupBySet, StringBuilder sql){
		int i = 0;
		for(String groupBy : groupBySet){
			if(i == 0){
				sql.append(" group by ");
			}
			sql.append(groupBy);
			if(i != groupBySet.size() -1){
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
