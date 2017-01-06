package com.jfinal.ext.plugin.validate;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.base.ReturnResult;

/**
 * 
 * @author iisky1121
 * 2017年1月6日 下午4:43:34
 */
public class ValidateInterceptor implements Interceptor  {
	@Override
	public void intercept(Invocation inv) {
		ReturnResult result = ValidateBuilder.validate(inv.getMethod(), inv.getController());
		if(result.isSucceed()){
			inv.invoke();
		} else {
			inv.getController().renderJson(result.render());
		}
	}
}
