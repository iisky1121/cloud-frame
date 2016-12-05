package com.jfinal.ext.plugin.sqlinxml;

import java.util.Map;

public class SqlInXmlKit {
	public static String get(String sqlId, Map<String, String> params){
		return SqlBuilder.get(sqlId, params);
	}
	
	public static String get(String namespace, String sqlId, Map<String, String> params){
		return SqlBuilder.get(namespace, sqlId, params);
	}
}
