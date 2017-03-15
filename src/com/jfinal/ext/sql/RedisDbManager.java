package com.jfinal.ext.sql;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.kit.ModelKit;
import com.jfinal.ext.kit.RecordKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.serializer.FstSerializer;

import java.util.Collection;
import java.util.Map;

/**
 * Created by hang on 2017/3/15 0015.
 */
public class RedisDbManager {
    /**
     * 查询
     * @param redisDb
     * @param alias
     * @param idKey
     * @param filterAttrs
     */
    static void select(RedisDb redisDb, String alias, String idKey, String[] filterAttrs){
        if(isList(redisDb.getObject())){
            Collection<Object> collection = (Collection)redisDb.getObject();
            for(Object object : collection){
                selectObject(redisDb, object, alias, idKey, filterAttrs);
            }
        } else{
            selectObject(redisDb, redisDb.getObject(), alias, idKey, filterAttrs);
        }
    }
    private static void selectObject(RedisDb redisDb, Object object, String alias, String idKey, String[] filterAttrs){
        if(object == null){
            return;
        }
        if(Model.class.isAssignableFrom(object.getClass())){
            Model<?> model = (Model<?>)object;
            Map<String,Object> redisMap = redisDb.getRedisDbMap(alias, model.get(idKey), filterAttrs);
            modelConditionMap(model, redisMap, alias);
        } else if(object instanceof Record){
            Record record = (Record)object;
            Map<String,Object> redisMap = redisDb.getRedisDbMap(alias, record.get(idKey), filterAttrs);
            recordConditionMap(record, redisMap, alias);
        } else if(object instanceof JSONObject){
            JSONObject json = (JSONObject)object;
            Map<String,Object> redisMap = redisDb.getRedisDbMap(alias, json.get(idKey), filterAttrs);
            jsonConditionMap(json, redisMap, alias);
        } else if(object instanceof Map){
            Map map = (Map)object;
            Map<String,Object> redisMap = redisDb.getRedisDbMap(alias, map.get(idKey), filterAttrs);
            mapConditionMap(map, redisMap, alias);
        }
    }

    static void addObject(RedisDb redisDb, Object object, String alias, String idKey){
        if(isList(object)){
            Collection<Object> collection = (Collection)object;
            for(Object obj : collection){
                addSingleObject(redisDb, obj, alias, idKey);
            }
        } else{
            addSingleObject(redisDb, object, alias, idKey);
        }
    }
    private static void addSingleObject(RedisDb redisDb, Object object, String alias, String idKey){
        if(object == null){
            return;
        }
        Map map;
        if(Model.class.isAssignableFrom(object.getClass())){
            Model<?> model = (Model<?>)object;
            map = ModelKit.toMap(model);
            setRedisData(redisDb, alias, map, model.get(idKey));
        } else if(object instanceof Record){
            Record record = (Record)object;
            map = RecordKit.toMap(record);
            setRedisData(redisDb, alias, map, record.get(idKey));
        } else if(object instanceof JSONObject){
            JSONObject json = (JSONObject)object;
            setRedisData(redisDb, alias, json, json.get(idKey));
        } else if(object instanceof Map){
            map = (Map)object;
            setRedisData(redisDb, alias, map, map.get(idKey));
        }
    }

    static void setRedisData(RedisDb redisDb, String alias, Map<String,Object> map, Object idValue){
        if(StrKit.isBlank(alias) || map == null || idValue == null){
            return;
        }
        String key = RedisDbManager.getKey(alias, idValue);
        if(redisDb.isOpenPipeline()){
            redisDb.pipeline().set(keyToBytes(key), valueToBytes(map));
        } else{
            redisDb.cache().set(key, valueToBytes(map));
        }
    }

    /**
     * Map拼接map
     * @param object
     * @param redisMap
     * @param alias
     */
    static void mapConditionMap(Map object, Map<String,Object> redisMap, String alias){
        if(object == null || redisMap == null || StrKit.isBlank(alias)){
            return;
        }
        for(Map.Entry<String,Object> entry : redisMap.entrySet()){
            object.put(getKey(alias, entry.getKey()), entry.getValue());
        }
    }

    /**
     * Model拼接map
     * @param model
     * @param redisMap
     * @param alias
     */
    static void modelConditionMap(Model model, Map<String,Object> redisMap, String alias){
        if(model == null || redisMap == null || StrKit.isBlank(alias)){
            return;
        }
        for(Map.Entry<String,Object> entry : redisMap.entrySet()){
            model.put(getKey(alias, entry.getKey()), entry.getValue());
        }
    }

    /**
     * Record拼接map
     * @param record
     * @param redisMap
     * @param alias
     */
    static void recordConditionMap(Record record, Map<String,Object> redisMap, String alias){
        if(record == null || redisMap == null || StrKit.isBlank(alias)){
            return;
        }
        for(Map.Entry<String,Object> entry : redisMap.entrySet()){
            record.set(getKey(alias, entry.getKey()), entry.getValue());
        }
    }

    /**
     * Json拼接map
     * @param json
     * @param redisMap
     * @param alias
     */
    static void jsonConditionMap(JSONObject json, Map<String,Object> redisMap, String alias){
        if(json == null || redisMap == null || StrKit.isBlank(alias)){
            return;
        }
        for(Map.Entry<String,Object> entry : redisMap.entrySet()){
            json.put(getKey(alias, entry.getKey()), entry.getValue());
        }
    }

    /**
     * 别名规则
     * @param alias
     * @param idValue
     * @return
     */
    static String getKey(String alias, Object idValue){
        return String.format("%s.%s", alias, idValue);
    }

    /**
     * bean别名
     * @param beanClass
     * @return
     */
    static String getAlias(Class<?> beanClass){
        return StrKit.firstCharToLowerCase(beanClass.getSimpleName());
    }

    /**
     * 是否列表集合
     * @param object
     * @return
     */
    static boolean isList(Object object){
        return object != null && object instanceof Collection;
    }

    /**
     * 序列化
     * @param object
     * @return
     */
    public static byte[] valueToBytes(Object object){
        return FstSerializer.me.valueToBytes(object);
    }

    public static byte[] keyToBytes(String key){
        return FstSerializer.me.keyToBytes(key);
    }
}
