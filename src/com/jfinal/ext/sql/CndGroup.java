package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hang on 2017/4/26 0026.
 */
public class CndGroup {
    private Cnd.Symbol symbol;
    private List<CndGroup> groupList;
	private List<CndParam> params = new ArrayList<CndParam>();

    public CndGroup create(Enum<?> key, Object val){
        return create(key.name(), val);
    }
    public CndGroup create(String key, Object val){
        return and(key, val, val.getClass());
    }

    public CndGroup create(Enum<?> key, Object val, Class<?> classType){
        return create(key.name(), val, classType);
    }
    public CndGroup create(String key, Object val, Class<?> classType){
        add(CndParam.create(key, val, classType));
        return this;
    }

    public CndGroup create(Enum<?> key, Cnd.Type type, Object val){
        return create(key.name(), type, val);
    }
    public CndGroup create(String key, Cnd.Type type, Object val){
        add(new CndParam(key, type, val));
        return this;
    }

    CndGroup and(CndParam p){
        p.setSymbol(Cnd.Symbol.and);
        return add(p);
    }

    public CndGroup and(Enum<?> key, Object val){
        return and(key.name(), val);
    }
    public CndGroup and(String key, Object val){
        return and(key, val, val.getClass());
    }

    public CndGroup and(Enum<?> key, Object val, Class<?> classType){
        return  and(key.name(), val, classType);
    }
    public CndGroup and(String key, Object val, Class<?> classType){
        CndParam p = CndParam.create(key, val, classType);
        p.setSymbol(Cnd.Symbol.and);
        add(p);
        return this;
    }

    public CndGroup and(Enum<?> key, Cnd.Type type, Object val){
        return and(key.name(), type, val);
    }
    public CndGroup and(String key, Cnd.Type type, Object val){
        CndParam p = new CndParam(key, type, val);
        p.setSymbol(Cnd.Symbol.and);
        add(p);
        return this;
    }

    CndGroup or(CndParam p){
        p.setSymbol(Cnd.Symbol.or);
        return add(p);
    }

    public CndGroup or(Enum<?> key, Object val){
        return or(key.name(), val);
    }
    public CndGroup or(String key, Object val){
        return or(key, val, val.getClass());
    }

    public CndGroup or(Enum<?> key, Object val, Class<?> classType){
        return or(key.name(), val, classType);
    }
    public CndGroup or(String key, Object val, Class<?> classType){
        CndParam p = CndParam.create(key, val, classType);
        p.setSymbol(Cnd.Symbol.or);
        add(p);
        return this;
    }

    public CndGroup or(Enum<?> key, Cnd.Type type, Object val){
        return or(key.name(), type, val);
    }
    public CndGroup or(String key, Cnd.Type type, Object val){
        CndParam p = new CndParam(key, type, val);
        p.setSymbol(Cnd.Symbol.or);
        add(p);
        return this;
    }

    public CndGroup andGroup(CndGroup group){
        if(groupList == null){
            groupList = new ArrayList<CndGroup>();
        }
        group.setSymbol(Cnd.Symbol.and);
        groupList.add(group);
        return this;
    }

    public CndGroup orGroup(CndGroup group){
        if(groupList == null){
            groupList = new ArrayList<CndGroup>();
        }
        group.setSymbol(Cnd.Symbol.or);
        groupList.add(group);
        return this;
    }

	private CndGroup add(CndParam p) {
        if(p.getValue() != null){
            params.add(p);
        }
		return this;
	}

	public List<CndParam> getParams() {
		return params;
	}

	public boolean isEmtry() {
		return params.isEmpty();
	}

	public boolean hasGroup(){
        return groupList != null;
    }
    public List<CndGroup> getGroupList(){
	    return groupList;
    }

    public Cnd.Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Cnd.Symbol symbol) {
        this.symbol = symbol;
    }
}
