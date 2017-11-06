package com.jfinal.ext.model;

import com.jfinal.interfaces.IDataLoader;
import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * 高级的redis model 拓展  全表缓存model
 * 理论上该表只应该存在少量数据的配置表，建议不要超过1k
 * Created by hang on 2017/8/28 0028.
 */
public abstract class RedisAllAttrModel<M extends RedisModel<M>> extends RedisModel<M> {
    enum AttrEnum{all}
    /**
     * 通过所有数据集合
     * @return
     */
    @Override
    public List<M> getAll() {
        return RedisKeyKit.getSet(AttrEnum.all, new IDataLoader() {
            @Override
            public Object load() {
                return dao().getAll();
            }
        });
    }

    @Override
    void _deleteFromRedis(Object... idValues){
        super._deleteFromRedis(idValues);
        RedisKeyKit.del(AttrEnum.all);
    }

    @Override
    void _addToRedis(Model<?> m, Object idValue){
        super._addToRedis(m, idValue);
        RedisKeyKit.del(AttrEnum.all);
    }

    @Override
    void _updateToRedis(Model<?> m, Object idValue){
        super._updateToRedis(m, idValue);
        RedisKeyKit.del(AttrEnum.all);
    }
}
