package com.jfinal.ext.plugin.validate;

import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author iisky1121
 * 2017年1月6日 下午4:43:28
 */
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
		return ValidateKit.checkNotNull(controller, checkNotNull.value());
	}
	
	private static ReturnResult validate(Controller controller, Validate[] validates){
		Map<String,Validate> expressMap = new HashMap<String, Validate>();
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
			if(!StrKit.isBlank(v.express())){
				result = ValidateKit.expressCheck(controller, v);
				if(!result.isSucceed()){
					return result;
				}
			}
		}
		return ReturnResult.success();
	}
}
