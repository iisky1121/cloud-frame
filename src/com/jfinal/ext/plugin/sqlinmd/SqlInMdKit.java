package com.jfinal.ext.plugin.sqlinmd;

import java.util.Map;

public class SqlInMdKit {
	public static String get(String sqlId, Map<String, String> params){
		return SqlBuilder.get(sqlId, params);
	}
	
	public static String get(String namespace, String sqlId, Map<String, String> params){
		return SqlBuilder.get(namespace, sqlId, params);
	}
}
