package com.jfinal.base;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.ext.kit.ControllerKit;
import com.jfinal.ext.plugin.validate.ValidateKit;

public abstract class CommonController extends Controller{
	
	/**
	 * 智能判断各种返回结果,默认为失败
	 * 
	 */
	public void renderResult(Object object) {
		renderJson(ReturnResult.toResult(object));
	}
	
	/**
	 * 返回成功或者失败json数据
	 * 
	 */
	@Deprecated
	public void renderJson(boolean b) {
		renderResult(b);
	}
	
	/**
	 * 返回失败json数据
	 * 
	 */
	public void renderError(String errorStr){
		renderResult(ReturnResult.failure(errorStr));
	}
	
	/**
	 * 返回成功json数据
	 * 
	 */
	public void renderSucc(String succStr){
		renderResult(ReturnResult.success(succStr));
	}
	
	/**
	 * 返回成功json数据
	 * 
	 */
	public void renderSucc(Object object){
		renderResult(ReturnResult.success(object));
	}
	
	/**
	 * 检查必填属性
	 * 
	 */
	protected ReturnResult checkNotNull(String... attrs){
		return ControllerKit.checkNotNull(this, attrs);
	}
	
	/**
	 * 检查属性值
	 * 
	 */
	public ReturnResult checkAttrValue(String attr, Object... values){
		return ControllerKit.checkAttrValue(this, attr, values);
	}
	
	/**
	 * 检查属性值
	 * 
	 */
	public ReturnResult checkAttrValue(String attr, Class<Enum<?>> enumClass){
		return ControllerKit.checkAttrValue(this, attr, enumClass);
	}
}