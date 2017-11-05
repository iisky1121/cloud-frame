package com.jfinal.plugin.redis.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.base.ReturnResult;
import com.jfinal.ext.kit.ModelKit;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import java.util.*;

/**
 * 高级的redis model 拓展  属性分组缓存model
 * Created by hang on 2017/8/4 0004.
 */
public abstract class RedisGroupAttrModel<M extends RedisModel<M>> extends RedisModel<M> {

    public abstract Enum<?> cacheAttr();
    /**
     * 通过属性获取数据集合
     * @param attr
     * @param value
     * @return
     */
    @Override
    public List<M> getByWhat(Enum<?> attr, Object value) {
        if(cacheAttr() != null && cacheAttr() == attr){
            Object[] idValues = RedisKeyKit.get(attr, value);
            //如果缓存存在，直接取缓存内容
            if(idValues != null){
                List<M> list = new ArrayList<M>();
                for(Object id : Arrays.asList(idValues)){
                    M m = findById(id);
                    //如果缓存里面的id的属性值和查询的缓存不一致，排除掉
                    if(m != null && StrKit.valueEquals(m.get(attr.name()), value)){
                        list.add(m);
                    }
                }
                return list;
            } else { //不存在的话，从数据库缓存进去
                List<M> list = super.getByWhat(attr, value);
                Set<Object> attrSet = new HashSet<>();
                for(M m : list){
                    attrSet.add(m.getIdValue());
                }
                RedisKeyKit.setEx(attr, value, attrSet.toArray(), getRedisExSeconds());
                return list;
            }
        }
        return super.getByWhat(attr, value);
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
            resetAttrCache(m, false);
        }
        super._deleteFromRedis(idValues);
    }

    @Override
    void _addToRedis(Model<?> m, Object idValue) {
        resetAttrCache(m, false);
        super._addToRedis(m, idValue);
    }

    @Override
    void _updateToRedis(Model<?> m, Object idValue) {
        resetAttrCache(m, true);
        super._updateToRedis(m, idValue);
    }

    private void resetAttrCache(Model<?> m, boolean isUpdate){
        if(cacheAttr() != null && m.get(cacheAttr().name()) != null){
            if(isUpdate && !getModifyFlag().contains(cacheAttr().name())){
                return;
            }
            RedisKeyKit.del(cacheAttr(), m.get(cacheAttr().name()));
        }
    }

    /**
     * 批量操作（支持save，update，delete混用）
     * @param attr
     * @param value
     * @param list
     * @param autoDelete
     * @return
     */
    public ReturnResult batch(Enum<?> attr, Object value, List<M> list, boolean autoDelete){
        List<Object> ids = Db.query(String.format("select %s from %s where %s=?", getPkName(), getTableName(), attr.name()), value);
        List<M> add_list = new ArrayList<>();
        List<M> update_list = new ArrayList<>();

        for(M m : list){
            m.set(attr, value);
            if(m.getIdValue() == null){
                add_list.add(m);
                continue;
            }

            if(ids.contains(m.getIdValue())){
                update_list.add(m);
                ids.remove(m.getIdValue());
                continue;
            }
            add_list.add(m);
        }

        Set<Object> attrSet = new HashSet<>();
        //新增
        for(M m : add_list){
            m.save();
            attrSet.add(m.getIdValue());
        }
        //修改
        for(M m : update_list){
            m.update();
            attrSet.add(m.getIdValue());
        }
        //删除
        if(ids.size() > 0){
            if(autoDelete){
                deletes(ids.toArray());
            } else {
                attrSet.addAll(ids);
            }
        }

        if(cacheAttr() != null && cacheAttr() == attr){
            RedisKeyKit.setEx(attr, value, attrSet.toArray(), getRedisExSeconds());
        }
        return ReturnResult.success(new JSONObject(){{
            put("add", ModelKit.toJsonArray(add_list));
            put("update", ModelKit.toJsonArray(update_list));
            if(autoDelete) {
                put("delete", ids);
            }
        }});
    }

    public ReturnResult batch(Enum<?> attr, Object value, List<M> list){
        return batch(attr, value, list, true);
    }
}
