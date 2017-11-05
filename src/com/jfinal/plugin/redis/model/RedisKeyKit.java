package com.jfinal.plugin.redis.model;

import com.jfinal.interfaces.IDataLoader;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

import java.util.Set;

public class RedisKeyKit {
	public static String getKey(Enum<?> e, Object key){
		return e.getClass().getName()+"."+e.name()+(key==null?"":(":"+key));
	}

	private static Cache cache(){
		if(Redis.use() == null){
			throw new RuntimeException("redis没有配置或者启动失败");
		}
		return Redis.use();
	}

	/**
	 * redis 设置key值
	 * @param e
	 * @param key
	 * @param seconds
	 * @return
	 */
	public static void setEx(Enum<?> e, Object key, Object value, int seconds){
		if(seconds ==0 ){
			cache().set(getKey(e, key), value);
		} else {
			cache().setex(getKey(e, key), seconds, value);
		}
	}

	public static void setEx(Enum<?> e, Object value, int seconds){
		setEx(e, null, value, seconds);
	}

	public static void del(Enum<?> e){
		del(e, null);
	}

	public static void del(Enum<?> e, Object key){
		cache().del(getKey(e, key));
	}

	public static void set(Enum<?> e, Object value){
		set(e, null, value);
	}

	public static void set(Enum<?> e, Object key, Object value){
		setEx(e, key, value, 0);
	}

	/**
	 * redis 通过key获取
	 * @param e
	 * @param key
	 * @param <T>
	 * @return
	 */
	public static <T> T get(Enum<?> e, Object key){
		T t = cache().get(getKey(e, key));
		if(t == null || (t instanceof String && "nil".equals(t))){
			return null;
		}
		return t;
	}

	public static <T> T get(Enum<?> e){
		return get(e, null);
	}

	/**
	 * redis 通过key获取并设置
	 * @param e
	 * @param key
	 * @param seconds
	 * @param loader
	 * @param <T>
	 * @return
	 */
	public static <T> T getSetEx(Enum<?> e, Object key, int seconds, IDataLoader loader){
		T t = get(e, key);
		if(t != null){
			return t;
		}
		Object value = loader.load();
		if(value != null){
			setEx(e, key, value, seconds);
		}
		return (T) value;
	}

	public static <T> T getSet(Enum<?> e, IDataLoader loader){
		return getSet(e, null, loader);
	}

	public static <T> T getSetEx(Enum<?> e, int seconds, IDataLoader loader){
		return getSetEx(e, null,seconds, loader);
	}

	public static <T> T getSet(Enum<?> e, Object key, IDataLoader loader){
		return getSetEx(e, key,0, loader);
	}

	/**
	 * 计数器
	 * @param e
	 * @param key
	 * @param seconds
	 * @param longValue
	 * @return
	 */
	public static long counter(Enum<?> e, Object key, int seconds, long longValue){
		Long value;
		if(longValue > 0){
			value = cache().incrBy(getKey(e, key), longValue);
		} else {
			value = cache().decrBy(getKey(e, key), -longValue);
		}
		if(seconds > 0){
			cache().expire(getKey(e, key), seconds);
		}
		return value==null?0L:value;
	}

	public static long counter(Enum<?> e, long longValue){
		return counter(e, null,0, longValue);
	}

	public static long counter(Enum<?> e, int seconds, long longValue){
		return counter(e, null, seconds, longValue);
	}

	public static long counter(Enum<?> e, Object key, long longValue){
		return counter(e, key, 0, longValue);
	}

	/**
	 * 获取计数器
	 * @param e
	 * @param key
	 * @return
	 */
	public static long getCounter(Enum<?> e, Object key){
		Long value = cache().getCounter(getKey(e, key));
		return value==null?0L:value;
	}

	public static long getCounter(Enum<?> e){
		return getCounter(e, null);
	}

	public static Set<String> keys(Enum<?> e){
		return keys(e, "*");
	}

	public static Set<String> keys(Enum<?> e, String pattern){
		return cache().keys(get(e,pattern));
	}

	public static <T> T lock(Enum<?> e, int seconds, IDataLoader success, IDataLoader failure){
		return lock(e, null, seconds, success, failure);
	}
	public static <T> T lock(Enum<?> e, Object key, int seconds, IDataLoader success, IDataLoader failure){
		try{
			String lockKey = System.currentTimeMillis()+":"+ StrKit.createNum(6);
			String redisValue = cache().getSet(getKey(e, key), lockKey);
			if(seconds > 0){
				cache().expire(getKey(e, key), seconds);
			}

			//当前值和redis值相同的话，说明是获取到锁
			if(redisValue == null || "nil".equals(redisValue) || lockKey.equals(redisValue)){
				return (T)success.load();
			} else {
				return (T)failure.load();
			}
		} catch (Throwable exception){
			throw new RuntimeException(exception);
		} finally {
			cache().del(getKey(e, key));
		}
	}
}
