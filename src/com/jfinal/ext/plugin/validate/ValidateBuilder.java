package com.jfinal.ext.plugin.validate;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

class ValidateBuilder {
	private static Map<Method, Validate[]> validateMap;
	private static Map<Method, CheckNotNull> checkNotNullMap;
	
	public static void addRule(Method method, Validate[] validates) {
		if(validateMap == null){
			validateMap = new HashMap<Method, Validate[]>();
		}
		validateMap.put(method, validates);
	}
	
	public static void addRule(Method method, CheckNotNull checkNotNull) {
		if(checkNotNullMap == null){
			checkNotNullMap = new HashMap<Method, CheckNotNull>();
		}
		checkNotNullMap.put(method, checkNotNull);
	}
	
	public static ReturnResult validate(Method method, Controller controller){
		if(method != null && controller != null){
			if(checkNotNullMap != null && checkNotNullMap.containsKey(method)){
				ReturnResult result = validate(controller, checkNotNullMap.get(method));
				if(!result.isSucceed()){
					return result;
				}
			}
			if(validateMap != null && validateMap.containsKey(method)){
				return validate(controller, validateMap.get(method));
			}
		}
		return ReturnResult.success();
	}
	
	private static ReturnResult validate(Controller controller, CheckNotNull checkNotNull){
		return ValidateKit.checkNotNull(controller, checkNotNull.attrs());
	}
	
	private static ReturnResult validate(Controller controller, Validate[] validates){
		Map<String,String> expressMap = new HashMap<String, String>();
		ReturnResult result;
		for(Validate v : validates){
			if(v.required()){
				result = ValidateKit.checkNotNull(controller, v.name());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(v.enumClass() != null && v.enumClass() != Enum.class){
				result = ValidateKit.checkAttrValue(controller, v.name(), v.enumClass());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(v.eitherOr().length > 0){
				result = ValidateKit.checkEitherOr(controller, v.name(), v.eitherOr());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(v.association().length > 0){
				result = ValidateKit.checkAssociation(controller, v.name(), v.association());
				if(!result.isSucceed()){
					return result;
				}
			}
			if(!StrKit.isBlank(v.groovyExp())){
				expressMap.put(v.name(), v.groovyExp());
			}
		}
		if(expressMap.size() > 0){
			result = ValidateKit.groovyCheck(controller, expressMap);
			if(!result.isSucceed()){
				return result;
			}
		}
		return ReturnResult.success();
	}
}
