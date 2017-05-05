package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hang on 2017/4/26 0026.
 */
@SuppressWarnings("unchecked")
class CndUpdate<M extends CndUpdate<M>> extends CndBaseSelect<M> {
    //操作的表名
    private String tableName;
    //set
    private Map<String, Object> sets = new HashMap<String, Object>();

    public M table(String tableName){
        this.tableName = tableName;
        return (M)this;
    }

    public M set(Enum<?> key, Object val){
    	return set(key.name(), val);
    }
    public M set(String key, Object val){
        sets.put(key,val);
        return (M)this;
    }

    public M setIncrBy(Enum<?> key, double val){
    	return setIncrBy(key.name(), val);
    }
    public M setIncrBy(String key, double val){
        return setIncrBy(key, key, val);
    }

    public M setIncrBy(Enum<?> key, String byKey, double val){
    	return setIncrBy(key.name(), byKey, val);
    }
    public M setIncrBy(String key, String byKey, double val){
        sets.put(key, new IncrBy(byKey, val));
        return (M)this;
    }

    public M build(){
        int setSize = sets.size();
        if(setSize < 1){
            throw new IllegalArgumentException("至少需要set一个属性");
        }

        sql.append(String.format(Cnd.$UPDATE_TABLE, tableName).concat(" set "));
        CndBuilder.build$Set(sets, sql, paramList);

        StringBuilder  sb = new StringBuilder();
        List<Object> paramArrayList = new ArrayList<Object>();

        CndWhere where = getWhere();
        CndBuilder.build$CndWhere(where, sb, paramArrayList);
        CndBuilder.build$Symbol(where, sb);

        sql.append(sb.toString());
        paramList.addAll(paramArrayList);
        return (M)this;
    }

    public static class IncrBy{
        public IncrBy(String key, Double val) {
            this.key = key;
            this.val = val;
        }

        private String key;
        private Double val;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Double getVal() {
            return val;
        }

        public void setVal(Double val) {
            this.val = val;
        }
    }

    public static void main(String[] args) {
        Cnd cnd = Cnd.$update()
                .table("mmm")
                .set("ddd", 11).set("xxx", "3333")
                .setIncrBy("xeee", "xppp", -111)
                .where()
                .and("ccc", "1,2,3")
                .or("a", 1)
                .build();
        System.out.println(cnd.getSql());
        System.out.println(cnd.getParas());
    }
}
