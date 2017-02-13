package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.ext.kit.RecordKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.serializer.FstSerializer;

import redis.clients.jedis.Pipeline;

@SuppressWarnings("rawtypes")
public class RedisDb extends Cache {
	private Object object;

	public static RedisDb create(Object Object){
		RedisDb db = new RedisDb();
		db.object = Object;
		return db;
	}
	
	/**
	 * 保存
	 * @param bean
	 * @param beanId
	 */
	public static void save(IBean bean, Object beanId){
		setRedisBean(beanId, bean);
	}
	
	/**
	 * 更新
	 * @param bean
	 * @param beanId
	 */
	public static void update(IBean bean, Object beanId){
		setRedisBean(beanId, bean);
	}
	
	/**
	 * 查找
	 * @param bean
	 * @param beanId
	 */
	public static <T extends IBean> RedisDb find(Class<T> beanClass, Object beanId){
		RedisDb db = new RedisDb();
		if(beanId != null){
			db.object = getRedisMap(beanClass, beanId.toString());
		}
		return db;
	}
	
	/**
	 * 删除
	 * @param beanClass
	 * @param beanId
	 */
	public static <T extends IBean> void delete(Class<T> beanClass, Object... beanId){
		if(beanId == null || beanClass == null){
			return;
		}
		List<String> keys = new ArrayList<String>();
		for(Object bId : beanId){
			keys.add(getKey(beanClass, bId.toString()));
		}
		Redis.use().del(keys.toArray());
	}
	
	/**
	 * 查询拼接
	 * @param key
	 * @param beanClass
	 * @return
	 */
	public <T extends IBean> RedisDb select(String key, Class<T> beanClass){
		return select(key, beanClass, null);
	}
	public <T extends IBean> RedisDb select(String key, Class<T> beanClass, String[] beanAttrs){
		if(object == null || StrKit.isBlank(key) || beanClass == null){
			return this;
		}
		if(object instanceof List){
			selectList(beanClass, key, beanAttrs, (List)object);
		}
		else{
			selectObject(beanClass, key, beanAttrs, object);
		}
		return this;
	}
	
	private <T extends IBean> void selectList(Class<T> beanClass, String key, String[] beanAttrs, List list){
		if(list == null || StrKit.isBlank(key) || beanClass == null){
			return;
		}
		for(Object o : list){
			selectObject(beanClass, key, beanAttrs, o);
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends IBean> void selectObject(Class<T> beanClass, String key, String[] beanAttrs, Object o){
		if(o == null || StrKit.isBlank(key) || beanClass == null){
			return;
		}
		Object beanId;
		if(o instanceof Model){
			Model model = (Model)o;
			beanId = model.get(key);
			
			if(beanId != null){
				Map<String, Object> redisMap = getRedisMap(beanClass, beanId.toString());
				if(redisMap == null){
					return;
				}
				//遍历设置 对应bean的值
				if(beanAttrs != null){
					for(String attr : beanAttrs){
						model.put(getKey(beanClass, attr), redisMap.get(attr));
					}
				}
				else{
					for(Entry<String, Object> entry : redisMap.entrySet()){
						model.put(getKey(beanClass, entry.getKey()), entry.getValue());
					}
				}
			}
		}
		else if(o instanceof Record){
			Record record = (Record)o;
			beanId = record.get(key);
			
			if(beanId != null){
				Map<String, Object> redisMap = getRedisMap(beanClass, beanId.toString());
				if(redisMap == null){
					return;
				}
				//遍历设置 对应bean的值
				if(beanAttrs != null){
					for(String attr : beanAttrs){
						record.set(getKey(beanClass, attr), redisMap.get(attr));
					}
				}
				else{
					for(Entry<String, Object> entry : redisMap.entrySet()){
						record.set(getKey(beanClass, entry.getKey()), entry.getValue());
					}
				}
			}
		}
		else if(o instanceof Map){
			Map<String, Object> map = (Map<String, Object>)o;
			beanId = map.get(key);
			
			if(beanId != null){
				Map<String, Object> redisMap = getRedisMap(beanClass, beanId.toString());
				if(redisMap == null){
					return;
				}
				//遍历设置 对应bean的值
				if(beanAttrs != null){
					for(String attr : beanAttrs){
						map.put(getKey(beanClass, attr), redisMap.get(attr));
					}
				}
				else{
					for(Entry<String, Object> entry : redisMap.entrySet()){
						map.put(getKey(beanClass, entry.getKey()), entry.getValue());
					}
				}
			}
		}
	}
	
	private static <T extends IBean> String getKey(Class<T> beanClass, String key){
		return StrKit.firstCharToLowerCase(beanClass.getSimpleName()).concat("."+key);
	}
	
	/**
	 * 获取redis对应beanId集合
	 * @param beanId
	 * @param beanClass
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
	
	@SuppressWarnings("unchecked")
	public static <T extends IBean> Map<String, Object> getRedisMap(Class<T> beanClass, Object beanId){
		if(beanClass == null || beanId == null){
			return null;
		}
		Object data = Redis.use().get(getKey(beanClass, beanId.toString()));
		if(data == null || "nil".equals(data.toString())){
			return null;
		}
		return (Map<String, Object>) data;
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
		Redis.use().set(getKey(beanClass, beanId.toString()), map);
	}
	
	/**
	 * 设置redis对应beanId列表集合
	 * @param beanClass
	 * @param beanId
	 * @param map
	 */
	@SuppressWarnings({ "unchecked" })
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
	
	@SuppressWarnings("unchecked")
	public static <T extends IBean> void setPipelinedList(Class<T> beanClass, String idKey, List list){
		Pipeline pipeline = Redis.use().getJedis().pipelined();
		Map<String, Object> map;
		Object beanId;
		for(Object object : list){
			if(object != null){
				if(object instanceof Model){
					Model model = (Model)object;
					map = (Map<String, Object>) JSONObject.toJSON((IBean)object);
					beanId = model.get(idKey);
				}
				else if(object instanceof Record){
					Record record = (Record)object;
					map = RecordKit.toMap(record);
					beanId = record.get(idKey);
				}
				else if(object instanceof Map){
					map = (Map<String, Object>)object;
					beanId = map.get(idKey);
				}
				else{
					continue;
				}
				
				pipeline.set(FstSerializer.me.valueToBytes(getKey(beanClass, beanId.toString())), FstSerializer.me.valueToBytes(map));
			}
		}
		
		pipeline.sync();
	}
}
