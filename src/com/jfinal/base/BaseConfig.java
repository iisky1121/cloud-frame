package com.jfinal.base;

public class BaseConfig {
	public final static String loginUserSessionAttr = "login_sys_user";
	public final static String userAllowUrlAttr = "user_allow_urls";
	public final static String userMenuAttr = "user_menus";
	public final static String success_code = "200";
	public final static String success_msg = "操作成功";
	public final static String failure_code = "400";
	public final static String failure_msg = "操作失败";
	
	public static ReturnResult notLogin(){
		return ReturnResult.failure("请先登录再操作", "sys_authorization_exception");
	}
	
	public static ReturnResult notPermission(String actionKey){
		return ReturnResult.failure(String.format("未被授权访问该资源[%s]", actionKey), "sys_no_permission");
	}
	
	public static ReturnResult attrValueError(String attr){
		return ReturnResult.failure(String.format("属性[%s]值有误", attr), "sys_attribute_value_error");
	}
	
	public static ReturnResult attrNotNull(String attr){
		return ReturnResult.failure(String.format("属性[%s]不允许为空", attr), "sys_attribute_not_allowed_to_empty");
	}
	
	public static ReturnResult dataNotExist(){
		return ReturnResult.failure("数据不存在", "sys_data_not_exist");
	}
	
	public static ReturnResult dataError(){
		return ReturnResult.failure("数据错误", "sys_data_error");
	}
	
	public static ReturnResult sysError(){
		return ReturnResult.failure("系统错误", "sys_error");
	}
}
