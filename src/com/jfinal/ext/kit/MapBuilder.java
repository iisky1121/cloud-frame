package com.jfinal.ext.kit;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.Map;

/**
 * Created by hang on 2017/3/22 0022.
 */
public class MapBuilder {
    public static Map<String, Object> build(Object object){
        if(object instanceof Map){
            return (Map<String, Object>) object;
        } else if(object instanceof JSONObject) {
            return (JSONObject) object;
        } else if(Model.class.isAssignableFrom(object.getClass())){
            return ModelKit.toMap((Model)object);
        } else if(object instanceof Record){
            return RecordKit.toMap((Record)object);
        } else{
            throw new IllegalArgumentException(String.format("%s类型不支持，暂时只支持Map,JSONObject,Model和Record类型", object.getClass().getName()));
        }
    }
}
