package com.jfinal.ext.kit;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.Map;

/**
 * 键值对类型处理器
 * ====================================================================
 *　　==       ==         ==         ===      ==       ==== == ==
 *　　==       ==       ==  ==       == ==    ==      ==
 *　　== == == ==      == == ==      ==  ==   ==      ==     = ==
 *　　==       ==     ==      ==     ==    == ==      ==       ==
 *　　==       ==    ==        ==    ==      ===       ==== == ==
 * ====================================================================
 * Created by iisky on 2017/3/23 0023.
 * ====================================================================
 */
public class KVPFactory {
    /**
     * 转换成map类型
     * @param object
     * @return
     */
    @SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Object object){
        if(object instanceof Map){
            return (Map<String, Object>) object;
        } else if(object instanceof JSONObject) {
            return (JSONObject) object;
        } else if(Model.class.isAssignableFrom(object.getClass())){
            return ModelKit.toMap((Model<?>)object);
        } else if(object instanceof Record){
            return RecordKit.toMap((Record)object);
        } else{
            throw new IllegalArgumentException(String.format("%s类型不支持，暂时只支持Map,JSONObject,Model和Record类型", object.getClass().getName()));
        }
    }

    /**
     * 获取属性
     * @param object
     * @param key
     * @return
     */
    public static Object get(Object object, String key){
        Map<String, Object> map = toMap(object);
        if(map == null){
            return null;
        }
        return map.get(key);
    }

    /**
     * 拼接map
     * @param object
     * @param map
     * @param prefix
     */
    @SuppressWarnings("unchecked")
	public static void conditionMap(Object object, Map<String,Object> map, String prefix){
        if(object instanceof Map){
            mapConditionMap((Map<String, Object>) object, map, prefix);
        } else if(object instanceof JSONObject) {
            jsonConditionMap((JSONObject) object, map, prefix);
        } else if(Model.class.isAssignableFrom(object.getClass())){
            modelConditionMap((Model<?>) object, map, prefix);
        } else if(object instanceof Record){
            recordConditionMap((Record) object, map, prefix);
        } else{
            throw new IllegalArgumentException(String.format("%s类型不支持，暂时只支持Map,JSONObject,Model和Record类型", object.getClass().getName()));
        }
    }
    public static void conditionMap(Object object, Map<String,Object> map){
        conditionMap(object, map, null);
    }

    /**
     * Map拼接map
     * @param object
     * @param map
     * @param prefix
     */
    static void mapConditionMap(Map<String, Object> object, Map<String,Object> map, String prefix){
        if(object == null || map == null){
            return;
        }
        for(Map.Entry<String,Object> entry : map.entrySet()){
            object.put(prefixKey(prefix, entry.getKey()), entry.getValue());
        }
    }

    /**
     * Model拼接map
     * @param model
     * @param model
     * @param map
     * @param prefix
     */
    static void modelConditionMap(Model<?> model, Map<String,Object> map, String prefix){
        if(model == null || map == null){
            return;
        }
        for(Map.Entry<String,Object> entry : map.entrySet()){
            model.put(prefixKey(prefix, entry.getKey()), entry.getValue());
        }
    }

    /**
     * Record拼接map
     * @param record
     * @param map
     * @param prefix
     */
    static void recordConditionMap(Record record, Map<String,Object> map, String prefix){
        if(record == null || map == null){
            return;
        }
        for(Map.Entry<String,Object> entry : map.entrySet()){
            record.set(prefixKey(prefix, entry.getKey()), entry.getValue());
        }
    }

    /**
     * Json拼接map
     * @param json
     * @param map
     * @param prefix
     */
    static void jsonConditionMap(JSONObject json, Map<String,Object> map, String prefix){
        if(json == null || map == null){
            return;
        }
        for(Map.Entry<String,Object> entry : map.entrySet()){
            json.put(prefixKey(prefix, entry.getKey()), entry.getValue());
        }
    }
    private static String prefixKey(String prefix, String key){
        if(StrKit.isBlank(prefix)){
            prefix = "";
        }
        return prefix.concat(key);
    }
}
