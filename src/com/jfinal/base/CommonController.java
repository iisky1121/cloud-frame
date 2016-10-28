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
			renderJson(BaseConfig.renderResult((ReturnResult)object));
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
		if(b){
			renderJson(BaseConfig.succ());
		}
		else{
			renderJson(BaseConfig.error());
		}
	}
	
	/**
	 * 返回失败json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderError(String errorStr){
		renderJson(BaseConfig.error(errorStr));
	}
	
	/**
	 * 返回成功json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderSucc(String succStr){
		renderJson(BaseConfig.succ(succStr));
	}
	
	/**
	 * 返回成功json数据
	 * 
	 */
	@Before(NotAction.class)
	public void renderSucc(Object object){
		renderJson(BaseConfig.succ(object));
	}
	
	/**
	 * 检查必填属性
	 * 
	 * 返回null则全部通过
	 */
	@Before(NotAction.class)
	public String checkNotNull(String... attrs){
		Map<String, String[]> map = getParaMap();
		for(String str : attrs){
			if(map.get(str) == null || map.get(str).length == 0 || StrKit.isBlank(map.get(str)[0])){
				return "属性["+str+"]不允许为空";
			}
			else{
				for(String attr : map.get(str)){
					if(StrKit.isBlank(attr)){
						return "属性["+str+"]不允许为空";
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 检查属性值
	 * 
	 * 返回null则全部通过
	 */
	@Before(NotAction.class)
	public String checkAttrValue(String attr, String... values){
		String attrValue = getPara(attr);
		if(!StrKit.isBlank(attrValue) && Arrays.asList(values).contains(attrValue)){
			return null;
		}
		return "属性["+attr+"]值有误";
	}
	
	/**
	 * 检查属性值
	 * 
	 * 返回null则全部通过
	 */
	@SuppressWarnings("rawtypes")
	@Before(NotAction.class)
	public String checkAttrValue(String attr, Class enumClass){
		String attrValue = getPara(attr);
		if(!StrKit.isBlank(attrValue) && enumClass != null){
			if("class java.lang.Enum".equals(enumClass.getSuperclass())){
				throw new RuntimeException("Attribute enumClass type must be enumerated");
			}
			Object[] objects = enumClass.getEnumConstants();
			for(Object object : objects){
				if(attrValue.equals(object.toString())){
					return null;
				}
			}
		}
		return "属性["+attr+"]值有误";
	}
}