package com.jfinal.ext.plugin.validate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.base.BaseConfig;
import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import groovy.lang.Binding;

class ValidationBuilder {
	private static Map<Method, Validation[]> map;
	
	public static void addRule(Method method, Validation[] validations) {
		if(map == null){
			map = new HashMap<Method, Validation[]>();
		}
		map.put(method, validations);
	}
	
	public static ReturnResult validate(Method method, Controller controller){
		if(method != null && controller != null && map.containsKey(method)){
			return validate(controller, map.get(method));
		}
		return ReturnResult.success();
	}
	
	@SuppressWarnings("unchecked")
	private static ReturnResult validate(Controller controller, Validation[] validates){
		Map<String, String[]> paraMap = controller.getParaMap();
		Map<String, Object> map = new HashMap<String, Object>();
		Binding bind = ValidationKit.groovyBinding(map);
		String name;
		Object value;
		for(Validation v : validates){
			name = v.name();
			if(v.required() && paraMap.get(name) == null || paraMap.get(name).length == 0 || StrKit.isBlank(paraMap.get(name)[0])){
				return BaseConfig.attrNotNull(name);
			}
			
			value = paraMap.get(name)[0];
			if(value != null && !StrKit.isBlank(v.groovyExp())){
				bind.getVariables().put(name, value);
				if(!ValidationKit.groovyCheck(bind, v.groovyExp())){
					return BaseConfig.attrValueError(name);
				}
			}
		}
		return ReturnResult.success();
	}
}
