package com.jfinal.ext.kit;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Maps;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

public class ModelKit {
	
	@SuppressWarnings("unchecked")
	public static <M extends Model<M>> M getModel(String tableName){
		Table table = getTable(tableName);
		return (M) (table==null?null:newInstance(table.getModelClass()));
	}
	
	public static Table getTable(String tableName){
		if(StrKit.isBlank(tableName)){
			return null;
		}
		Map<Class<? extends Model<?>>, Table> mappings = TableMapping.me().getMappings();
		for(Entry<Class<? extends Model<?>>, Table> entry : mappings.entrySet()){
			if(tableName.equals(entry.getValue().getName())){
				return entry.getValue();
			}
		}
		return null;
	}
	
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
    	try {
    		Model<?> model = (Model<?>) entryClass.newInstance();
    		
    		Map<String, Class<?>> mappings = TableMapping.me().getTable(model.getClass()).getColumnTypeMap();
    		for(Entry<String, Class<?>> entry : mappings.entrySet()){
    			if(map.get(entry.getKey()) != null)
    				model.set(entry.getKey(), map.get(entry.getKey()));
    		}
    		return (T) model;
    	} catch (InstantiationException e) {
    		e.printStackTrace();
    	} catch (IllegalAccessException e) {
    		e.printStackTrace();
    	}
    	return null;
    }

	@SuppressWarnings("unchecked")
	public static <T> T toModel(String tableName, Map<String,Object> map) {
		Model<?> model = getModel(tableName);
		if(model == null){
			return null;
		}
		
		Map<String, Class<?>> newMap = TableMapping.me().getTable(model.getClass()).getColumnTypeMap();
		for(Entry<String, Class<?>> entry : newMap.entrySet()){
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

    public static <M extends Model<M>> Collection<Map<String,Object>> collectionModelToMap(Collection<M> list){
		Collection<Map<String,Object>> collection = new ArrayList<>();
		for(M m : list){
			collection.add(toMap(m));
		}
		return collection;
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
    public static void copyColumns(Model src, Model desc, String... columns){
		copyColumns(src, desc, false, columns);
    }
    
    @SuppressWarnings("rawtypes")
    public static void copyNotNullColumns(Model src, Model desc, String... columns){
		copyColumns(src, desc, true, columns);
    }

	public static void copyColumns(Model src, Model desc, boolean onlyNotNull, String... columns){
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
	public static boolean notNullValueEquals(Model src, String[] columns, String[] values){
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

	@SuppressWarnings("rawtypes")
	public static boolean notNullValueEquals(Model src, Model desc, String... columns){
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
