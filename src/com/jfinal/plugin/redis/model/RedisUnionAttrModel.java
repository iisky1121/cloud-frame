package com.jfinal.plugin.redis.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.Set;

/**
 * 高级的redis model 拓展  多个唯一属性同时缓存model
 * Created by hang on 2017/8/4 0004.
 */
public abstract class RedisUnionAttrModel<M extends RedisModel<M>> extends RedisModel<M> {
    /**
     * 需要进行缓存的设置
     * @return
     */
    public abstract Set<Enum<?>> cacheSet();

    @Override
    void _addToRedis(Model<?> m, Object idValue){
        if(idValue == null){
            return;
        }
        if(m != null){
            getRedisDb().add(this.getClass(), m, idValue);
            refreshAttrCache(idValue, false);
        }
    }

    @Override
    void _updateToRedis(Model<?> m, Object idValue){
        if(idValue == null){
            return;
        }
        if(m != null){
            getRedisDb().add(this.getClass(), m, idValue);
            refreshAttrCache(idValue, true);
        }
    }

    @Override
    void _deleteFromRedis(Object... idValues){
        if(idValues == null){
            return;
        }
        for(Object idValue : idValues){
            M m = findById(idValue);
            if(m == null){
                return;
            }
            for(Enum<?> attr : cacheSet()){
                RedisKeyKit.del(attr, m.get(attr.name()));
            }
        }
        super._deleteFromRedis(idValues);
    }

    private void refreshAttrCache(Object idValue, boolean isUpdate){
        for(Enum<?> attr : cacheSet()){
            if(get(attr.name()) != null){
                if(isUpdate&& !getModifyFlag().contains(attr.name())){
                    return;
                }
                RedisKeyKit.setEx(attr, get(attr.name()), idValue, getRedisExSeconds());
            }
        }
    }

    /**
     * 通过属性获取单条数据
     * @param attr
     * @param value
     * @return
     */
    @Override
    public M getFirstByWhat(Enum<?> attr, Object value) {
        if(cacheSet().contains(attr)){
            Object idValue = RedisKeyKit.get(attr, value);
            if(idValue != null){
                return findById(idValue);
            }
        }
        M m = super.getFirstByWhat(attr, value);
        _addToRedis(m, get(getPkName()));
        return m;
    }
}
