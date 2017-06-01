package com.jfinal.ext.plugin.redisdb;

import com.jfinal.ext.kit.KVPFactory;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.serializer.FstSerializer;

import java.util.Collection;
import java.util.Map;

/**
 * Created by hang on 2017/3/15 0015.
 */
class RedisDbBuilder {
    /**
     * 查询
     * @param redisDb
     * @param alias
     * @param idKey
     * @param filterAttrs
     */
    @SuppressWarnings("unchecked")
	static void select(RedisDb redisDb, String alias, String idKey, String[] filterAttrs){
        if(isList(redisDb.getObject())){
            Collection<Object> collection = (Collection<Object>)redisDb.getObject();
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
        Object idValue = KVPFactory.get(object, idKey);
        Map<String,Object> redisMap = redisDb.getRedisDbMap(alias, idValue, filterAttrs);
        KVPFactory.conditionMap(object, redisMap, alias.concat("."));
    }

    @SuppressWarnings("unchecked")
	static void addObject(RedisDb redisDb, Object object, String alias, String idKey){
        if(isList(object)){
            Collection<Object> collection = (Collection<Object>)object;
            for(Object obj : collection){
            	addSingleObjectByKey(redisDb, obj, alias, idKey);
            }
        } else{
        	addSingleObjectByKey(redisDb, object, alias, idKey);
        }
    }

    static void addSingleObjectByValue(RedisDb redisDb, Object object, String alias, Object idValue){
        if(object == null){
            return;
        }
        Map<String,Object> map = KVPFactory.toMap(object);
        if(map != null){
            setRedisData(redisDb, alias, map, idValue);
        }
    }

    private static void addSingleObjectByKey(RedisDb redisDb, Object object, String alias, String idKey){
        if(object == null){
            return;
        }
        Map<String,Object> map = KVPFactory.toMap(object);
        if(map != null){
            setRedisData(redisDb, alias, map, map.get(idKey));
        }
    }

    static void setRedisData(RedisDb redisDb, String alias, Map<String,Object> map, Object idValue){
        if(StrKit.isBlank(alias) || map == null || idValue == null){
            return;
        }
        String key = RedisDbBuilder.getKey(alias, idValue);
        if(redisDb.isOpenPipeline()){
        	if(redisDb.getSeconds() > 0){
        		redisDb.pipeline().setex(keyToBytes(key), redisDb.getSeconds(), valueToBytes(map));
        	} else {
        		redisDb.pipeline().set(keyToBytes(key), valueToBytes(map));
        	}
        } else{
            redisDb.cache().set(key, map);
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
        return beanClass.getName();
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
