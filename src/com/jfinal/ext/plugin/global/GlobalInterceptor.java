package com.jfinal.ext.plugin.global;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.base.ReturnResult;
import com.jfinal.kit.LogKit;


/**
 * 
 * 全局异常错误处理
 *
 */
public class GlobalInterceptor implements Interceptor {
	
	public void intercept(Invocation ai) {
		try {
			ai.invoke();
			defRender(ai);
		} catch (Exception e) {
			LogKit.error(e.getMessage(), e);
		}
	}
	
	protected void defRender(Invocation ai){
		if(ai.getController().getRender() != null){
			return;
		}
		if(ai.getReturnValue() != null) {
			ai.getController().renderJson(ReturnResult.toResult(ai.getReturnValue()).render());
			return;
		}
		ai.getController().renderJson(ReturnResult.failure().render());
	}
}