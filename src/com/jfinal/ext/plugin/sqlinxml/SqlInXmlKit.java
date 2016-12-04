package com.jfinal.ext.plugin.sqlinxml;

public class SqlInXmlKit {
	public static String get(String sqlId){
		return SqlBuilder.get(sqlId);
	}
	
	public static String get(String namespace, String sqlId){
		return SqlBuilder.get(namespace, sqlId);
	}
}
