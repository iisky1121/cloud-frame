package com.jfinal.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.ext.kit.ModelKit;
import com.jfinal.plugin.activerecord.Model;

public class ApiController extends CommonController{
	
	@SuppressWarnings("unchecked")
	@Before(NotAction.class)
	public <T> T getModel(Class<T> modelClass){
		Map<String,Object> map = new HashMap<String,Object>();
    	for(Entry<String, String[]> entry : getParaMap().entrySet()){
    		map.put(entry.getKey(), entry.getValue()[0]);
    	}
    	return (T) ModelKit.toModel((Class<? extends Model<?>>) modelClass, map);
	}
}