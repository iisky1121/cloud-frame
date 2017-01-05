package com.jfinal.base;

import java.util.Arrays;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.kit.StrKit;

public abstract class CommonController extends Controller{
	/**
	 * 设置登录用户信息
	 * 
	 * @return T
	 */
	@Before(NotAction.class)
	public void setLoginUser(Object obj){
		setSessionAttr(BaseConfig.loginUserSessionAttr, obj);
		UserSession.set(obj);
	}
	
	/**
	 * 清除登录用户信息
	 * 
	 * @return T
	 */
	@Before(NotAction.class)
	public void clearLoginUser(){
		getSession().removeAttribute(BaseConfig.loginUserSessionAttr);
		UserSession.set(null);
	}
	
	/**
	 * 获取用户登录信息
	 * 
	 * @return T
	 */
	@Before(NotAction.class)
	public <T> T loginUser(){
		return getSessionAttr(BaseConfig.loginUserSessionAttr);
	}
	
	/**
	 * 智能判断各种返回结果,默认为失败
	 * 
	 */
	@Before(NotAction.class)
	public void renderResult(Object object) {
		if(object == null){
			renderJson(false);
		}
		else if(object instanceof Boolean){
			renderJson((Boolean)object);
		}
		else if(object instanceof ReturnResult){
			renderJson(object);
		}
		else if(object instanceof String){
			renderError((String)object);
		}
		else{
			renderSucc(object);
		}
	}
	
	/**
	 * 返回成功或者失败json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderJson(boolean b) {
		renderResult(b);
	}
	
	/**
	 * 返回失败json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderError(String errorStr){
		renderResult(ReturnResult.failure(errorStr));
	}
	
	/**
	 * 返回成功json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderSucc(String succStr){
		renderResult(ReturnResult.success(succStr));
	}
	
	/**
	 * 返回成功json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderSucc(Object object){
		renderResult(ReturnResult.success(object));
	}
	
	/**
	 * 检查必填属性
	 * 
	 */
	@Before(NotAction.class)
	public ReturnResult checkNotNull(String... attrs){
		Map<String, String[]> map = getParaMap();
		for(String str : attrs){
			if(map.get(str) == null || map.get(str).length == 0 || StrKit.isBlank(map.get(str)[0])){
				return BaseConfig.attrNotNull(str);
			}
			else{
				for(String attr : map.get(str)){
					if(StrKit.isBlank(attr)){
						return BaseConfig.attrNotNull(str);
					}
				}
			}
		}
		return ReturnResult.success();
	}
	
	/**
	 * 检查属性值
	 * 
	 */
	@Before(NotAction.class)
	public ReturnResult checkAttrValue(String attr, String... values){
		String attrValue = getPara(attr);
		if(!StrKit.isBlank(attrValue) && Arrays.asList(values).contains(attrValue)){
			return ReturnResult.success();
		}
		return BaseConfig.attrValueError(attr);
	}
	
	/**
	 * 检查属性值
	 * 
	 */
	@SuppressWarnings("rawtypes")
	@Before(NotAction.class)
	public ReturnResult checkAttrValue(String attr, Class enumClass){
		String attrValue = getPara(attr);
		if(!StrKit.isBlank(attrValue) && enumClass != null){
			if(Enum.class.isAssignableFrom(enumClass)){
				throw new IllegalArgumentException("Attribute enumClass type must be enumerated");
			}
			Object[] objects = enumClass.getEnumConstants();
			for(Object object : objects){
				if(attrValue.equals(object.toString())){
					return ReturnResult.success();
				}
			}
		}
		return BaseConfig.attrValueError(attr);
	}
}