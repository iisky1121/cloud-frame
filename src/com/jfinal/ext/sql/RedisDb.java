package com.jfinal.ext.sql;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.plugin.redis.serializer.FstSerializer;
import redis.clients.jedis.Pipeline;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("rawtypes")
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
		if(isMap()){
			selectMap(idKey, alias, filterAttrs);
		} else if(isList()){
			selectList(idKey, alias, filterAttrs);
		}
		return this;
	}
	public <M extends IBean> RedisDb select(String idKey, Class<M> beanClass, String[] filterAttrs){
		return select(idKey, getAlias(beanClass), filterAttrs);
	}
	public <M extends IBean> RedisDb select(String idKey, Class<M> beanClass){
		return select(idKey, getAlias(beanClass));
	}
	public RedisDb select(String idKey, String alias){
		return select(idKey, alias, null);
	}

	/**
	 * 查询&拼接 Map
	 * @param alias
	 * @param idKey
	 * @param filterAttrs
	 * @return
	 */
	public RedisDb selectMap(String idKey, String alias, String[] filterAttrs){
		if(isMap()){
			Map map = (Map)this.object;
			if(map != null && map.get(idKey) != null){
				Map<String,Object> redisMap = getRedisDbMap(alias, map.get(idKey), filterAttrs);
				conditionMap(map, redisMap, alias);
			}
		}
		return this;
	}

	/**
	 * 查询&拼接 List
	 * @param alias
	 * @param idKey
	 * @param filterAttrs
	 * @return
	 */
	public RedisDb selectList(String idKey, String alias, String[] filterAttrs){
		if(isList()){
			Collection<Map> list = (Collection)this.object;
			for(Map map : list){
				if(map != null && map.get(idKey) != null){
					Map<String,Object> redisMap = getRedisDbMap(alias, map.get(idKey), filterAttrs);
					conditionMap(map, redisMap, alias);
				}
			}
		}
		return this;
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
		Map<String,Object> map = getRedisMap(getKey(alias, idValue));
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
	public Map<String,Object> getRedisDbMap(String alias, Object idValue){
		return getRedisDbMap(alias, idValue, null);
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
	public List<Map<String,Object>> getRedisDbList(String alias, List<Object> idValues){
		return getRedisDbList(alias, idValues, null);
	}

	/**
	 * 获取redis数据转换成map
	 * @param cacheKey
	 * @return
	 */
	private Map<String,Object> getRedisMap(String cacheKey){
		Object data = this.cache().get(cacheKey);
		if(data == null || "nil".equals(data.toString())){
			return null;
		}
		return (Map<String, Object>) data;
	}

	/**
	 * 两个map数据拼接
	 * @param object
	 * @param redisMap
	 * @param alias
	 */
	private static void conditionMap(Map object, Map<String,Object> redisMap, String alias){
		if(object == null || redisMap == null || StrKit.isBlank(alias)){
			return;
		}
		for(Entry<String,Object> entry : redisMap.entrySet()){
			object.put(getKey(alias, entry.getKey()), entry.getValue());
		}
	}

	/**
	 * 别名规则
	 * @param alias
	 * @param idValue
	 * @return
	 */
	private static String getKey(String alias, Object idValue){
		return String.format("%s.%s", alias, idValue);
	}

	/**
	 * bean别名
	 * @param beanClass
	 * @return
	 */
	private static String getAlias(Class<?> beanClass){
		return StrKit.firstCharToLowerCase(beanClass.getSimpleName());
	}

	/**
	 * 设置 redis Map
	 * @param alias
	 * @param map
	 * @param idKey
	 * @return
	 */
	public RedisDb setMap(String alias, Map<String,Object> map, String idKey){
		if(StrKit.isBlank(alias) || map == null || StrKit.isBlank(idKey) || map.get(idKey) == null){
			return this;
		}
		String key = getKey(alias, map.get(idKey));
		if(isOpenPipeline()){
			this.pipeline().set(serializer(key), serializer(map));
		} else{
			this.cache().set(key, map);
		}
		return this;
	}
	public <M extends IBean> RedisDb setMap(Class<M> beanClass, Map<String,Object> map, String idKey){
		return setMap(getAlias(beanClass), map, idKey);
	}

	/**
	 * 设置 redis List
	 * @param alias
	 * @param list
	 * @param idKey
	 * @return
	 */
	public RedisDb setList(String alias, Collection<Map<String,Object>> list, String idKey){
		if(StrKit.isBlank(alias) || list == null || StrKit.isBlank(idKey) || list.size()==0){
			return this;
		}
		for(Map<String,Object> map : list){
			setMap(alias, map, idKey);
		}
		return this;
	}
	public <M extends IBean> RedisDb setList(Class<M> beanClass, Collection<Map<String,Object>> list, String idKey){
		return setList(getAlias(beanClass), list, idKey);
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
			String key = getKey(alias, idValue);
			if(isOpenPipeline()){
				this.pipeline.del(key);
			} else {
				this.cache().del(key);
			}
		}
		return this;
	}
	public <M extends IBean> RedisDb remove(Class<M> beanClass, Object... idValues){
		return remove(getAlias(beanClass), idValues);
	}

	/**
	 * 是否开启管道
	 * @return
	 */
	public boolean isOpenPipeline(){
		return pipeline() != null;
	}

	/**
	 * 是否map
	 * @return
	 */
	public boolean isMap(){
		return this.object != null && this.object instanceof Map;
	}

	/**
	 * 是否List
	 * @return
	 */
	public boolean isList(){
		return this.object != null && this.object instanceof Collection;
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

	/**
	 * 序列化
	 * @param object
	 * @return
	 */
	public static byte[] serializer(Object object){
		return FstSerializer.me.valueToBytes(object);
	}
}
