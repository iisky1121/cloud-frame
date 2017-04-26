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

    public Cnd$Where where(String key, Object val){
        return where(key, val, val.getClass());
    }

    public Cnd$Where where(String key, Object val, Class<?> classType){
        wheres.put(key, Param.create(key, val, classType));
        return this;
    }

    public Cnd$Where where(String key, Cnd.Type type, Object val){
        wheres.put(key, new Param(key, type, val));
        return this;
    }
}
