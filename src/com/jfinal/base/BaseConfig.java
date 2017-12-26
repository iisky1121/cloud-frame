package com.jfinal.base;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BaseConfig {
	public final static String loginUserSessionAttr = "login_sys_user";
	public final static String success_code = "200";
	public final static String success_msg = "操作成功";
	public final static String failure_code = "400";
	public final static String failure_msg = "操作失败";
	
	public static ReturnResult notLogin(){
		return ReturnResult.failure("请先登录再操作", "sys_no_login", null);
	}
	
	public static ReturnResult notPermission(String actionKey){
		return ReturnResult.failure(String.format("未被授权访问该资源[%s]", actionKey), "sys_no_permission", null);
	}
	
	public static ReturnResult attrValueError(String attr){
		return ReturnResult.failure(String.format("属性[%s]值有误", attr), "sys_validate_attr_value_error", null);
	}
	
	public static ReturnResult attrValueEmpty(String attr){
		return ReturnResult.failure(String.format("属性[%s]不允许为空", attr), "sys_validate_attr_value_empty", null);
	}
	
	public static ReturnResult attrEitherOr(String attr, String[] eitherOr){
		Set<String> set = new HashSet<String>();
		set.add(attr);
		for(String eOr : eitherOr){
			set.add(eOr);
		}
		return ReturnResult.failure(String.format("属性%s至少一个不为空", Arrays.toString(set.toArray())), "sys_validate_attr_eitherOr", null);
	}
	
	public static ReturnResult attrAssociation(String attr, String[] association){
		Set<String> set = new HashSet<String>();
		for(String ass : association){
			set.add(ass);
		}
		return ReturnResult.failure(String.format("属性[%s]存在,属性%s都不能为空", attr, Arrays.toString(set.toArray())), "sys_validate_attr_association", null);
	}
	
	public static ReturnResult dataNotExist(){
		return ReturnResult.failure("数据不存在", "sys_data_not_exist", null);
	}
	
	public static ReturnResult dataError(){
		return ReturnResult.failure("数据错误", "sys_data_error", null);
	}
	
	public static ReturnResult sysError(){
		return ReturnResult.failure("系统错误", "sys_error", null);
	}
}
