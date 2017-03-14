package com.jfinal.ext.kit;

import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.*;
import java.util.Map.Entry;

public class RecordKit {

	private static Log logger = Log.getLog(RecordKit.class);

    public static Model<?> toModel(Class<? extends Model<?>> clazz, Record record) {
        Model<?> model = null;
        try {
            model = clazz.newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return model;
        }
        for (String columnName : record.getColumnNames()) {
            model.set(columnName, record.get("columnName"));
        }
        return model;
    }

    public static Map<String, Object> toMap(Record record) {
        Map<String, Object> map = Maps.newHashMap();
        Set<Entry<String, Object>> attrs = record.getColumns().entrySet();
        for (Entry<String, Object> entry : attrs) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

	public static Collection<Map<String,Object>> collectionModelToMap(Collection<Record> list){
		Collection<Map<String,Object>> collection = new ArrayList<>();
		for(Record record : list){
            collection.add(toMap(record));
		}
		return collection;
	}
    

    public static void copyColumns(Record src, Record desc, String... columns){
		copyColumns(src, desc, false, columns);
    }

	public static void copyNotNullColumns(Record src, Record desc, String... columns){
		copyColumns(src, desc, true, columns);
	}
    
    public static void copyColumns(Record src, Record desc, boolean onlyNotNull, String... columns){
        for(String column:columns)  {
            String[] res = column.split(",");
            if(res.length==1){
            	if(onlyNotNull && src.get(column) != null)
            		desc.set(column,src.get(column));
            }else {
            	if(onlyNotNull && src.get(res[0]) != null)
            		desc.set(res[1],src.get(res[0]));
            }
        }
    }

    public static boolean notNullValueEquals(Record src, String[] columns, String[] values){
        if(!StrKit.notNull(src)){
            return false;
        }
        if(columns == null || values == null){
            return false;
        }

        for(int i =0; i < columns.length; i++){
            if(StrKit.isBlank(columns[i]) || values[i] == null){
                return false;
            }
            if(!StrKit.valueEquals(src.get(columns[i]), values[i])){
                return false;
            }
        }
        return true;
    }

    public static boolean notNullValueEquals(Record src, Record desc, String... columns){
        if(!StrKit.notNull(src, desc)){
            return false;
        }
        if(columns == null){
            return false;
        }
        for(String column : columns){
            if(StrKit.isBlank(column)){
                return false;
            }
            if(!StrKit.valueEquals(src.get(column), desc.get(column))){
                return false;
            }
        }
        return true;
    }
}
