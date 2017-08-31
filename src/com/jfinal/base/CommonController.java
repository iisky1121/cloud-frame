package com.jfinal.base;

import com.jfinal.core.Controller;
import com.jfinal.ext.kit.ControllerKit;

public abstract class CommonController extends Controller{
	public int pageSize(){
		int pageSize = getParaToInt("pageSize", 10);
		return pageSize>0?pageSize:10;
	}
	public int pageNumber(){
		int pageNumber = getParaToInt("pageNumber", 1);
		return pageNumber>0?pageNumber:1;
	}
	
	/**
	 * 智能判断各种返回结果,默认为失败
	 * 
	 */
	public void renderResult(Object object) {
		renderJson(ReturnResult.toResult(object).render());
	}
	
	/**
	 * 返回成功或者失败json数据
	 * 统一使用renderResult
	 */
	@Deprecated
	public void renderJson(boolean b) {
		renderResult(b);
	}
	
	/**
	 * 返回失败json数据
	 * 统一使用renderResult
	 */
	@Deprecated
	public void renderError(String errorStr){
		renderResult(ReturnResult.failure(errorStr));
	}
	
	/**
	 * 返回成功json数据
	 * 统一使用renderResult
	 */
	@Deprecated
	public void renderSucc(String succStr){
		renderResult(ReturnResult.success(succStr));
	}
	
	/**
	 * 返回成功json数据
	 * 统一使用renderResult
	 */
	@Deprecated
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