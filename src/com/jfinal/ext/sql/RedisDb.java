package com.jfinal.ext.sql;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.kit.RecordKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;

public class RedisDb {
	private Map<String,Object> map;
	void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public static RedisDb create(Map<String,Object> map){
		RedisDb db = new RedisDb();
		db.setMap(map);
		return db;
	}
	
	/**
	 * 保存
	 * @param beanId
	 * @param bean
	 */
	public static <T extends IBean> void save(Object beanId, T bean){
		setRedisBean(beanId, bean);
	}
	
	/**
	 * 更新
	 * @param beanId
	 * @param bean
	 */
	public static <T extends IBean> void update(Object beanId, T bean){
		setRedisBean(beanId, bean);
	}
	
	/**
	 * 查询拼接
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public RedisDb select(String key, Class<IBean> beanClass){
		Object beanId = map.get(key);
		if(beanId != null){
			Map<String, Object> redisMap = getRedisMap(beanClass, beanId.toString());
			selectCondition(beanClass, redisMap);
		}
		return this;
	}
	
	/**
	 * 对map进行数据拼接
	 * @param beanClass
	 * @param redisMap
	 */
	private void selectCondition(Class<IBean> beanClass, Map<String, Object> redisMap){
		if(redisMap == null){
			return;
		}
		//遍历设置 对应bean的值
		for(Entry<String, Object> entry : redisMap.entrySet()){
			map.put(beanClass.getSimpleName().concat(entry.getKey()), entry.getValue());
		}
	}
	
	/**
	 * 获取redis对应beanId集合
	 * @param beanClass
	 * @param beanId
	 * @return
	 */
	public static <T extends IBean> T getRedisBean(Class<T> beanClass, Object beanId){
		JSONObject redisJson = getRedisJson(beanClass, beanId);
		if(redisJson == null){
			return null;
		}
		return JSONObject.toJavaObject(redisJson, beanClass);
	}
	
	public static <T extends IBean> JSONObject getRedisJson(Class<T> beanClass, Object beanId){
		Map<String, Object> redisMap = getRedisMap(beanClass, beanId);
		if(redisMap == null){
			return null;
		}
		return (JSONObject) JSONObject.toJSON(redisMap);
	}
	
	public static <T extends IBean> Map<String, Object> getRedisMap(Class<T> beanClass, Object beanId){
		if(beanClass == null || beanId == null){
			return null;
		}
		return Redis.use().get(beanClass.getSimpleName().concat("."+beanId));
	}
	
	/**
	 * 设置redis对应beanId集合
	 * @param beanClass
	 * @param beanId
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IBean> void setRedisBean(Object beanId, T bean){
		if(beanId == null || bean == null){
			return;
		}
		Map<String, Object> map = (Map<String, Object>) JSONObject.toJSON(bean);
		setRedisMap(bean.getClass(), beanId, map);
	}
		
	public static <T extends IBean> void setRedisRecord(Class<T> beanClass, Object beanId, Record record){
		if(beanClass == null || beanId == null || record == null){
			return;
		}
		setRedisMap(beanClass, beanId, RecordKit.toMap(record));
	}
	
	public static <T extends IBean> void setRedisMap(Class<T> beanClass, Object beanId, Map<String, Object> map){
		if(beanClass == null || beanId == null || map == null){
			return;
		}
		Redis.use().set(beanClass.getSimpleName().concat("."+beanId), map);
	}
	
	/**
	 * 设置redis对应beanId列表集合
	 * @param beanClass
	 * @param beanId
	 * @param map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends IBean> void setRedisList(Class<T> beanClass, String idKey, List list){
		if(beanClass == null || StrKit.isBlank(idKey) || list == null){
			return;
		}
		for(Object object : list){
			if(object != null){
				if(object instanceof Model){
					Model model = (Model)object;
					setRedisBean(model.get(idKey), (IBean)object);
				}
				else if(object instanceof Record){
					Record record = (Record)object;
					setRedisRecord(beanClass, record.get(idKey), record);
				}
				else if(object instanceof Map){
					Map<String, Object> map = (Map<String, Object>)object;
					setRedisMap(beanClass, map.get(idKey), map);
				}
			}
		}
	}
}
