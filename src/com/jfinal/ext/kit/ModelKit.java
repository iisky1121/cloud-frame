package com.jfinal.ext.kit;

import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.TableMapping;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ModelKit {

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Record toRecord(Model model) {
        Record record = new Record();
        Set<Entry<String, Object>> attrs = model._getAttrsEntrySet();
        for (Entry<String, Object> entry : attrs) {
            record.set(entry.getKey(), entry.getValue());
        }
        return record;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T toModel(Class<T> entryClass, Map<String,Object> map) {
		Model model = newInstance(entryClass);

		Map<String, Class<?>> mappings = TableMapping.me().getTable(model.getClass()).getColumnTypeMap();
		for(Entry<String, Class<?>> entry : mappings.entrySet()){
			if(map.get(entry.getKey()) != null)
				model.set(entry.getKey(), map.get(entry.getKey()));
		}
		return (T) model;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, Object> toMap(Model model) {
        Map<String, Object> map = Maps.newHashMap();
        Set<Entry<String, Object>> attrs = model._getAttrsEntrySet();
        for (Entry<String, Object> entry : attrs) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void copyNotNull(Model source, Model target){
    	if(source.getClass().equals(target.getClass()) && source != null && target != null){
    		Set<Entry<String, Object>> attrs = source._getAttrsEntrySet();
    		for(Entry<String,Object> entry : attrs){
    			if(entry.getValue()!= null){
    				target.set(entry.getKey(), entry.getValue());
    			}
    		}
    	}
    }
    
    @SuppressWarnings("rawtypes")
    public static void copyColumns(Model source, Model target, String... columns){
		copyColumns(source, target, false, columns);
    }
    
    @SuppressWarnings("rawtypes")
    public static void copyNotNullColumns(Model source, Model target, String... columns){
		copyColumns(source, target, true, columns);
    }

	private static void copyColumns(Model<?> source, Model<?> target, boolean onlyNotNull, String... columns){
		for(String column:columns) {
			if (!onlyNotNull || source.get(column) != null){
				target.set(column, source.get(column));
			}
		}
	}
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T newInstance(Class modelClass){
    	if(modelClass != null && Model.class.isAssignableFrom((Class<?>) modelClass)){
    		try {
    			return (T) modelClass.newInstance();
    		} catch (InstantiationException e) {
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			e.printStackTrace();
    		}
    	}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static boolean valueEquals(Model model, String[] columns, Object[] values){
		for(int i=0,len = columns.length; i < len; i++){
			if(!StrKit.valueEquals(model.get(columns[i]), values[i])) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public static boolean valueEquals(Model source, Model target, String... columns){
		for(String column : columns){
			if(!StrKit.valueEquals(source.get(column), target.get(column))) {
				return false;
			}
		}
		return true;
	}

}
