package com.jfinal.ext.kit;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class RecordKit {

    public static Model<?> toModel(Class<? extends Model<?>> clazz, Record record) {
        Model<?> model = ModelKit.newInstance(clazz);;
        for (String columnName : record.getColumnNames()) {
            model.set(columnName, record.get("columnName"));
        }
        return model;
    }

    public static Map<String, Object> toMap(Record record) {
        Map<String, Object> map = new HashMap<String, Object>();
        Set<Entry<String, Object>> attrs = record.getColumns().entrySet();
        for (Entry<String, Object> entry : attrs) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static void copyColumns(Record source, Record target, String... columns){
        copyColumns(source, target, false, columns);
    }

    public static void copyNotNullColumns(Record source, Record target, String... columns){
        copyColumns(source, target, true, columns);
    }

    private static void copyColumns(Record source, Record target, boolean onlyNotNull, String... columns){
        for(String column:columns) {
            if (!onlyNotNull || source.get(column) != null){
                target.set(column, source.get(column));
            }
        }
    }

    public static boolean valueEquals(Record record, String[] columns, Object[] values){
        for(int i=0,len = columns.length; i < len; i++){
            if(!StrKit.valueEquals(record.get(columns[i]), values[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean valueEquals(Record source, Record target, String... columns){
        for(String column : columns){
            if(!StrKit.valueEquals(source.get(column), target.get(column))) {
                return false;
            }
        }
        return true;
    }
}
