package com.jfinal.ext.plugin.sql;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hang on 2017/4/26 0026.
 */
class CndWhere {
    //是否拼接where
    private boolean hasWhere = false;
    private int groupIndex = 0;
    //默认初始化一个
    @SuppressWarnings("serial")
	private Map<Integer, CndGroup> wheres = new HashMap<Integer, CndGroup>(){{
        put(groupIndex, new CndGroup());
    }};
    
    public Map<Integer, CndGroup> getWheres(){
    	return wheres;
    }

    public CndWhere andGroup(CndGroup group){
        group.setSymbol(Cnd.Symbol.and);
        wheres.put(wheres.size(), group);
        return this;
    }

    public CndWhere orGroup(CndGroup group){
        group.setSymbol(Cnd.Symbol.or);
        wheres.put(wheres.size(), group);
        return this;
    }

    public CndWhere and(CndParam p){
        wheres.get(groupIndex).and(p);
        return this;
    }

    public CndWhere and(String key, Object val){
        return and(key, val, val.getClass());
    }

    public CndWhere and(String key, Object val, Class<?> classType){
        wheres.get(groupIndex).and(key, val, classType);
        return this;
    }

    public CndWhere and(String key, Cnd.Type type, Object val){
        wheres.get(groupIndex).and(key, type, val);
        return this;
    }

    public CndWhere or(CndParam p){
        wheres.get(groupIndex).or(p);
        return this;
    }

    public CndWhere or(String key, Object val){
        return or(key, val, val.getClass());
    }

    public CndWhere or(String key, Object val, Class<?> classType){
        wheres.get(groupIndex).or(key, val, classType);
        return this;
    }

    public CndWhere or(String key, Cnd.Type type, Object val){
        wheres.get(groupIndex).or(key, type, val);
        return this;
    }

    public CndWhere where(){
        this.hasWhere = true;
        return this;
    }

    public boolean hasWhere() {
        return hasWhere;
    }

    public void setHasWhere(boolean hasWhere) {
        this.hasWhere = hasWhere;
    }
}
