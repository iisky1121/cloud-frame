package com.jfinal.ext.plugin.error;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;


/**
 * 
 * 全局异常错误处理
 *
 */
public class ErrorInterceptor implements Interceptor {
	
	public void intercept(Invocation ai) {
		Controller c = ai.getController();
		try {
			ai.invoke();
			defRender(c, null, null);
		} catch (Exception e) {
			String exceptionId = c.getClass().getName().concat("_"+DateKit.getDateTimeNumberStr()).concat("_"+StrKit.createNoncestr());
			defRender(c, e, exceptionId);
			LogKit.error(exceptionId, e);
		}
	}
	
	protected void defRender(Controller c, Exception e, String exceptionId){
		if(e != null){
			if(StrKit.isBlank(exceptionId)){
				c.renderJson(ReturnResult.failure(e, null).render());
			}
			else{
				c.renderJson(ReturnResult.failure(e, null).setCause(exceptionId).render());
			}
		}
		else{
			if(c.getRender() == null){
				c.renderJson(ReturnResult.failure().render());
			}
		}
	}
}