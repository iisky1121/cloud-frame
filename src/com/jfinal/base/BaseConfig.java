package com.jfinal.base;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.StrKit;

public class BaseConfig {
	public final static String loginUserSessionAttr = "login_sys_user";
	public final static String userAllowUrlAttr = "user_allow_urls";
	public final static String userMenuAttr = "user_menus";
	public final static String success_code = "200";
	public final static String success_msg = "操作成功";
	public final static String failure_code = "400";
	public final static String failure_msg = "操作失败";
	
	public static Map<String, Object> succ(){
		return render(success_code, success_msg, null, null);
	}

	public static Map<String, Object> succ(String succStr){
		return render(success_code, succStr, null);
	}
	
	public static Map<String, Object> succ(Object result){
		return render(success_code, success_msg, null, result, null);
	}
	
	public static Map<String, Object> error(){
		return render(failure_code, failure_msg, null);
	}
	
	public static Map<String, Object> error(Exception e){
		return render(failure_code, failure_msg, null, e);
	}
	
	public static Map<String, Object> error(String errorStr){
		return render(failure_code, errorStr, null);
	}
	
	public static Map<String, Object> notLogin(){
		return render(failure_code, "请先登录再操作。", "authorization_exception");
	}
	
	public static Map<String, Object> notPermission(String actionKey){
		return render(failure_code, "未被授权访问该资源[" + actionKey + "]", "no_permission");
	}
	
	public static Map<String, Object> attrValueError(String attr){
		return render(failure_code, "属性["+attr+"]值有误", "attribute_value_error");
	}
	
	public static Map<String, Object> attrNotNull(String attr){
		return render(failure_code, "属性["+attr+"]不允许为空", "attribute_not_null");
	}
	
	public static Map<String, Object> renderResult(ReturnResult result){
		return render(result.getCode(), result.getMsg(), result.getError_code(), result.getResult(), result.getException());
	}
	
	private static Map<String, Object> render(String code, String msg, String error_code){
		return render(code, msg, error_code, null, null);
	}
	
	private static Map<String, Object> render(String code, String msg, String error_code, Exception e){
		return render(code, msg, error_code, null, e);
	}
	
	private static Map<String, Object> render(String code, String msg, String error_code, Object result, Exception e){
		Map<String,Object> map = new HashMap<String,Object>();
		if(!StrKit.isBlank(code)){
			map.put("code", code);
		}
		if(!StrKit.isBlank(msg)){
			map.put("msg", msg);
		}
		if(!StrKit.isBlank(error_code)){
			map.put("error_code", error_code);
		}
		if(result != null){
			map.put("result", result);
		}
		if(e != null){
			map.put("exception", StrKit.isBlank(e.getMessage())?e.getCause():e.getMessage());
		}
		return map;
	}
}
