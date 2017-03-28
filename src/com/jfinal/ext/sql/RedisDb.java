package com.jfinal.ext.sql;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RedisDb {
	private Cache cache;
	private Pipeline pipeline;
	private Object object;

	public static RedisDb use(){
		RedisDb db = new RedisDb();
		db.cache = Redis.use();
		return db;
	}
	public static RedisDb use(String cacheName){
		RedisDb db = new RedisDb();
		db.cache = Redis.use(cacheName);
		return db;
	}

	public RedisDb setObject(Object object){
		this.object = object;
		return this;
	}

	Object getObject(){
		return this.object;
	}

	/**
	 * 透传RedisCache
	 * @return
	 */
	public Cache cache(){
		return this.cache;
	}

	/**
	 * 透传RedisPipeline
	 * @return
	 */
	Pipeline pipeline(){
		return this.pipeline;
	}

	/**
	 * 智能查询&拼接
	 * @param idKey
	 * @param alias
	 * @param filterAttrs
	 * @return
	 */
	public RedisDb select(String idKey, String alias, String[] filterAttrs){
		RedisDbBuilder.select(this, alias, idKey, filterAttrs);
		return this;
	}
	public RedisDb select(String idKey, Class<?> beanClass, String[] filterAttrs){
		return select(idKey, RedisDbBuilder.getAlias(beanClass), filterAttrs);
	}
	public  RedisDb select(String idKey, Class<?> beanClass){
		return select(idKey, beanClass, null);
	}
	public RedisDb select(String idKey, String alias){
		return select(idKey, alias, null);
	}

	/**
	 * 获取redis Map数据并拼装
	 * @param idValue
	 * @param alias
	 * @param filterAttrs
	 * @return
	 */
	public Map<String,Object> getRedisDbMap(String alias, Object idValue, String[] filterAttrs){
		if(idValue == null || StrKit.isBlank(alias)){
			return null;
		}
		//从redis获取数据
		Map<String,Object> map = getRedisMap(RedisDbBuilder.getKey(alias, idValue));
		//过滤属性
		if(map != null && map.size() > 0 && filterAttrs != null && filterAttrs.length > 0){
			List<String> fAttrs = Arrays.asList(filterAttrs);
			for(Entry<String,Object> entry : map.entrySet()){
				if(!fAttrs.contains(entry.getKey())){
					map.remove(entry.getKey());
				}
			}
		}
		return map;
	}
	public Map<String,Object> getRedisDbMap(Class<?> beanClass, Object idValue, String[] filterAttrs){
		return getRedisDbMap(RedisDbBuilder.getAlias(beanClass), idValue, filterAttrs);
	}
	public Map<String,Object> getRedisDbMap(String alias, Object idValue){
		return getRedisDbMap(alias, idValue, null);
	}
	public Map<String,Object> getRedisDbMap(Class<?> beanClass, Object idValue){
		return getRedisDbMap(beanClass, idValue, null);
	}

	/**
	 * 获取redis List数据并拼装
	 * @param alias
	 * @param idValues
	 * @param filterAttrs
	 * @return
	 */
	public List<Map<String,Object>> getRedisDbList(String alias, List<Object> idValues, String[] filterAttrs){
		if(idValues == null || idValues.size()==0 || StrKit.isBlank(alias)){
			return null;
		}
		List<Map<String,Object>> list = new ArrayList<>();
		Map<String,Object> map;
		for(Object idValue : idValues){
			map = getRedisDbMap(alias, idValue, filterAttrs);
			if(map != null){
				list.add(map);
			}
		}
		return list;
	}
	public List<Map<String,Object>> getRedisDbList(Class<?> beanClass, List<Object> idValues, String[] filterAttrs){
		return getRedisDbList(RedisDbBuilder.getAlias(beanClass), idValues, filterAttrs);
	}
	public List<Map<String,Object>> getRedisDbList(String alias, List<Object> idValues){
		return getRedisDbList(alias, idValues, null);
	}
	public List<Map<String,Object>> getRedisDbList(Class<?> beanClass, List<Object> idValues){
		return getRedisDbList(beanClass, idValues, null);
	}

	/**
	 * 获取redis数据转换成map
	 * @param cacheKey
	 * @return
	 */
	@SuppressWarnings("unchecked")
	Map<String,Object> getRedisMap(String cacheKey){
		Object data = this.cache().get(cacheKey);
		if(data == null || "nil".equals(data.toString())){
			return null;
		}
		return (Map<String, Object>) data;
	}

	/**
	 * 设置 redis Object
	 * @param alias
	 * @param object
	 * @param idKey
	 * @return
	 */
	public RedisDb add(String alias, Object object, String idKey){
		if(StrKit.isBlank(alias) || object == null || StrKit.isBlank(idKey)){
			return this;
		}
		RedisDbBuilder.addObject(this,object, alias, idKey);
		return this;
	}
	public  RedisDb add(Class<?> beanClass, Object object, String idKey){
		return add(RedisDbBuilder.getAlias(beanClass), object, idKey);
	}

	/**
	 * 删除redis缓存
	 * @param alias
	 * @param idValues
	 * @return
	 */
	public RedisDb remove(String alias, Object... idValues){
		if(StrKit.isBlank(alias) || idValues==null || idValues.length == 0){
			return this;
		}
		for(Object idValue : idValues){
			String key = RedisDbBuilder.getKey(alias, idValue);
			if(isOpenPipeline()){
				this.pipeline.del(key);
			} else {
				this.cache().del(key);
			}
		}
		return this;
	}
	public  RedisDb remove(Class<?> beanClass, Object... idValues){
		return remove(RedisDbBuilder.getAlias(beanClass), idValues);
	}

	/**
	 * 是否开启管道
	 * @return
	 */
	public boolean isOpenPipeline(){
		return pipeline() != null;
	}

	/**
	 * 开启管道
	 * @return
	 */
	public RedisDb openPipeline(){
		if(!isOpenPipeline()){
			this.pipeline = this.cache().getJedis().pipelined();
		}
		return this;
	}

	/**
	 * 同步写入管道数据
	 */
	public void writeSync(){
		if(isOpenPipeline()){
			pipeline().sync();
		}
	}
}
