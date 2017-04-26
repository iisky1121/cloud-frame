package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hang on 2017/4/26 0026.
 */
class CndUpdate<M extends CndUpdate> extends Cnd {
    //操作的表名
    private String tableName;
    //set
    private Map<String, Object> sets = new HashMap<String, Object>();
    //where
    private Map<String, Param> wheres = new HashMap<String, Param>();

    public M table(String tableName){
        this.tableName = tableName;
        return (M)this;
    }

    public M set(String key, Object val){
        sets.put(key,val);
        return (M)this;
    }

    public M setIncrBy(String key, double val){
        return setIncrBy(key, key, val);
    }

    public M setIncrBy(String key, String byKey, double val){
        sets.put(key, new IncrBy(byKey, val));
        return (M)this;
    }

    public M where(String key, Object val){
        return where(key, val, val.getClass());
    }

    public M where(String key, Object val, Class<?> classType){
        wheres.put(key, Param.create(key, val, classType));
        return (M)this;
    }

    public M where(String key, Cnd.Type type, Object val){
        wheres.put(key, new Param(key, type, val));
        return (M)this;
    }

    public M build(){
        int whereSize = wheres.size(),setSize = sets.size();
        if(whereSize < 1){
            throw new IllegalArgumentException("必须拼接where才能调用，避免全表update");
        }
        if(setSize < 1){
            throw new IllegalArgumentException("至少需要set一个属性");
        }

        StringBuilder  sb = new StringBuilder();
        List<Object> paramArrayList = new ArrayList<Object>();

        sb.append(String.format("update `%s` set ", tableName));
        int i =0;
        for(Map.Entry<String,Object> entry : sets.entrySet()){
            if(entry.getValue() instanceof IncrBy){
                IncrBy incrBy = (IncrBy) entry.getValue();
                sb.append(String.format("%s = %s %s ?", entry.getKey(), incrBy.getKey(), incrBy.getVal()>=0?"+":"-"));
            } else {
                sb.append(String.format("%s = ?", entry.getKey()));
            }
            paramArrayList.add(entry.getValue());
            if(i<setSize-1){
                sb.append(",");
            }
            i++;
        }
        for(Map.Entry<String, Param> entry : wheres.entrySet()){
            CndBuilder.buildSQL(sb, entry.getValue().getType(), entry.getValue().getKey(), entry.getValue().getValue(), paramArrayList);
        }
        sql.append(sb.toString().replaceFirst(Symbol.and.name(), Cnd.$WHERE));
        paramList.addAll(paramArrayList);
        return (M)this;
    }

    public static void main(String[] args) {
        Cnd.Update cnd = Cnd.update().table("mmm").set("ddd", 11).set("xxx", "3333").setIncrBy("xeee", "xppp", -111).where("ccc", ">1").build();
        System.out.println(cnd.getSql());
        System.out.println(cnd.getParas());
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
}
