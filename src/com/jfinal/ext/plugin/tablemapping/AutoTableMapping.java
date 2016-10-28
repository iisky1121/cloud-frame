package com.jfinal.ext.plugin.tablemapping;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.StrKit;

public class AutoTableMapping {
	private static Map<String,String> tableMapping = new HashMap<String,String>();
	private static Map<String,Table> tableMappingMaps = new HashMap<String,Table>();
	
	public static void put(String controllerKey ,Table table){
		if(StrKit.isBlank(controllerKey) || table == null){
			return;
		}
		if(tableMapping.get(controllerKey) != null){
			try {
				throw new Exception("controllerKey:"+controllerKey+",出现重复");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		tableMapping.put(controllerKey, table.getName());
		tableMappingMaps.put(table.getName(), table);
	}
	
	public static Table getTable(String tableName){
		return tableMappingMaps.get(tableName);
	}
	
	public static String getTableName(String controllerKey){
		return tableMapping.get(controllerKey);
	}
}