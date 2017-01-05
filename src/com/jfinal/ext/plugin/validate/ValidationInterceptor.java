package com.jfinal.ext.plugin.validate;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.base.ReturnResult;

public class ValidationInterceptor implements Interceptor  {
	@Override
	public void intercept(Invocation inv) {
		ReturnResult result = ValidationBuilder.validate(inv.getMethod(), inv.getController());
		if(result.isSucceed()){
			inv.invoke();
		} else {
			inv.getController().renderJson(result);
		}
	}
}
