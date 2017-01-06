package com.jfinal.base;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.ext.plugin.validate.ValidateKit;

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
			renderJson(((ReturnResult)object).render());
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
		return ValidateKit.checkNotNull(this, attrs);
	}
	
	/**
	 * 检查属性值
	 * 
	 */
	public ReturnResult checkAttrValue(String attr, Object... values){
		return ValidateKit.checkAttrValue(this, attr, values);
	}
	
	/**
	 * 检查属性值
	 * 
	 */
	public ReturnResult checkAttrValue(String attr, Class<?> enumClass){
		return ValidateKit.checkAttrValue(this, attr, enumClass);
	}
}