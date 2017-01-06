package com.jfinal.ext.plugin.validate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

class ValidationBuilder {
	private static Map<Method, Validation[]> validationMap;
	private static Map<Method, CheckNotNull> checkNotNullMap;
	
	public static void addRule(Method method, Validation[] validations) {
		if(validationMap == null){
			validationMap = new HashMap<Method, Validation[]>();
		}
		validationMap.put(method, validations);
	}
	
	public static void addRule(Method method, CheckNotNull checkNotNull) {
		if(checkNotNullMap == null){
			checkNotNullMap = new HashMap<Method, CheckNotNull>();
		}
		checkNotNullMap.put(method, checkNotNull);
	}
	
	public static ReturnResult validate(Method method, Controller controller){
		if(method != null && controller != null){
			if(checkNotNullMap.containsKey(method)){
				ReturnResult result = validate(controller, checkNotNullMap.get(method));
				if(!result.isSucceed()){
					return result;
				}
			}
			if(validationMap.containsKey(method)){
				return validate(controller, validationMap.get(method));
			}
		}
		return ReturnResult.success();
	}
	
	private static ReturnResult validate(Controller controller, CheckNotNull checkNotNull){
		return ValidationKit.checkNotNull(controller, checkNotNull.attrs());
	}
	
	private static ReturnResult validate(Controller controller, Validation[] validates){
		Map<String,String> expressMap = new HashMap<String, String>();
		ReturnResult result;
		for(Validation v : validates){
			if(v.required()){
				result = ValidationKit.checkNotNull(controller, v.name());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(v.enumClass() != null && v.enumClass() != Enum.class){
				result = ValidationKit.checkAttrValue(controller, v.name(), v.enumClass());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(v.eitherOr().length > 0){
				result = ValidationKit.checkEitherOr(controller, v.name(), v.eitherOr());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(v.association().length > 0){
				result = ValidationKit.checkAssociation(controller, v.name(), v.association());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(!StrKit.isBlank(v.groovyExp())){
				expressMap.put(v.name(), v.groovyExp());
			}
		}
		if(expressMap.size() > 0){
			result = ValidationKit.groovyCheck(controller, expressMap);
			if(!result.isSucceed()){
				return result;
			}
		}
		return ReturnResult.success();
	}
}
