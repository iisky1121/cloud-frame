package com.jfinal.ext.plugin.error;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.base.BaseConfig;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;


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
			defRender(c, null);
		} catch (Exception e) {
			defRender(c, e);
			LogKit.error(c.getClass().getName(), e);
		}
	}
	
	protected void defRender(Controller c, Exception e){
		if(e != null){
			c.renderJson(BaseConfig.error(e));
		}
		else{
			if(c.getRender() == null){
				c.renderJson(BaseConfig.error());
			}
		}
	}
}