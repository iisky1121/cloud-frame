package com.jfinal.plugin.redis.model;

import com.jfinal.ext.kit.ModelKit;
import com.jfinal.ext.plugin.redisdb.RedisDb;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public abstract class RedisModel<M extends Model<M>> extends Model<M> {
	protected final static int def_redis_ex_seconds = 0;//默认不过期

	/**
	 * 是否自动刷新redis key过期时间
	 * @return
	 */
	protected boolean autoRefreshRedisExTime(){
		return false;
	}

	/**
	 * 获取redis key过期时间
	 * @return
	 */
	protected int getRedisExSeconds(){
		return def_redis_ex_seconds;
	}

	protected Object getIdValue(){
		return this.get(getPkName());
	}

	protected RedisDb getRedisDb(){
		return RedisDb.use()
				.setExpireSeconds(getRedisExSeconds())
				.setAutoRefreshExpireTime(autoRefreshRedisExTime());
	}
	
	@Override
	public M findById(Object idValue){
		return findFromRedisById(idValue);
	}

	@Override
	protected void afterSave() {
		_addToRedis(this, this.getIdValue());
	}
	
	@Override
	protected void afterUpdate() {
		M old = findById(getIdValue());
		ModelKit.copyColumns(this, old, getModifyFlag().toArray(new String[]{}));
		_updateToRedis(old, getIdValue());
		afterUpdate(old);
	}

	/**
	 * redis model自带的update钩子，这里是当前数据完整的一行数据
	 * @param m
	 */
	protected void afterUpdate(M m) {

	}
	
	@Override
	public boolean deleteById(Table table, Object... idValues) {
		boolean result = super.deleteById(table, idValues);
		if(result && idValues!= null && idValues.length == 1){
			_deleteFromRedis(idValues[0]);
		}
		return result;
	}
	
	@Override
	public boolean deletes(Object[] idValues){
		boolean result = super.deletes(idValues);
		if(result){
			_deleteFromRedis(idValues);
		}
		return result;
	}

	/**
	 * 方便redis存储情况，通过ids集获取数据
	 * @param ids
	 * @return
	 */
	@Override
	public List<M> findByIds(List<?> ids){
		List<M> list = new ArrayList<M>();
		for(Object id : ids){
			M m = findById(id);
			if(m != null){
				list.add(m);
			}
		}
		return list;
	}

	M findFromRedisById(Object idValue){
		Map<String,Object> map = getRedisDb().getRedisDbMap(this.getClass(), idValue);
		if(map != null){
			//redis空值特殊处理
			if(map.size() == 0){
				return null;
			}
			return (M) ModelKit.toModel(this.getClass(), map);
		}
		M m = super.findById(idValue);
		_addToRedis(m, idValue);

		if(m == null){
			//处理空值情况，防止null进行DB穿透
			getRedisDb().add(this.getClass(), new HashMap<String,Object>(), idValue);
		}
		return m;
	}

	void _addToRedis(Model<?> m, Object idValue){
		if(idValue == null){
			return;
		}
		if(m != null){
			getRedisDb().add(this.getClass(), m, idValue);
		}
	}

	void _updateToRedis(Model<?> m, Object idValue){
		if(idValue == null){
			return;
		}
		if(m != null){
			getRedisDb().add(this.getClass(), m, idValue);
		}
	}

	void _deleteFromRedis(Object... idValues){
		getRedisDb().remove(this.getClass(), idValues);
	}
}
