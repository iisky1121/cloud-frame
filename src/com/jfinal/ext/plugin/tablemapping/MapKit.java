package com.jfinal.ext.plugin.tablemapping;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.CaseFormat;
import com.jfinal.plugin.activerecord.Record;

class MapKit {
	
	public static Record toRecord(String tableName, Map<String,String[]> map) {
		Record record = new Record();
		String prex = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName);
		if(map != null){
			Map<String, Class<?>> cloumns = AutoTableMapping.getTable(tableName).getColumnTypeMap();
			for(Entry<String, Class<?>> entry: cloumns.entrySet()){
				record.set(entry.getKey(), map.get(prex+"."+entry.getKey())==null?null:map.get(prex+"."+entry.getKey())[0]);
			}
		}
		return record;
	}
}
