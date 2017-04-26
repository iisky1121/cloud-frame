package com.jfinal.ext.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hang on 2017/4/26 0026.
 */
class Cnd$Where {
    private int groupIndex = 0;
    //默认初始化一个
    private Map<Integer, Cnd$Group> wheres = new HashMap<Integer, Cnd$Group>(){{
        put(groupIndex, new Cnd$Group());
    }};

    public Cnd$Where addGroup(Cnd$Group group){
        groupIndex++;
        wheres.put(groupIndex, group);
        return this;
    }

    public Cnd$Where and(String key, Object val){
        return and(key, val, val.getClass());
    }

    public Cnd$Where and(String key, Object val, Class<?> classType){
        wheres.get(groupIndex).and(Param.create(key, val, classType));
        return this;
    }

    public Cnd$Where and(String key, Cnd.Type type, Object val){
        wheres.get(groupIndex).and(new Param(key, type, val));
        return this;
    }

    public Cnd$Where or(String key, Object val){
        return or(key, val, val.getClass());
    }

    public Cnd$Where or(String key, Object val, Class<?> classType){
        wheres.get(groupIndex).or(Param.create(key, val, classType));
        return this;
    }

    public Cnd$Where or(String key, Cnd.Type type, Object val){
        wheres.get(groupIndex).or(new Param(key, type, val));
        return this;
    }
}
