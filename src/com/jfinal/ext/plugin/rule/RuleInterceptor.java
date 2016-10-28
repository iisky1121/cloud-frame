package com.jfinal.ext.plugin.rule;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * RuleInterceptor
 */
public class RuleInterceptor implements Interceptor {
	@Override
	public void intercept(Invocation ai) {
		//规则判断
		String actionKey = ai.getActionKey();
		if(RuleBuilder.contains(actionKey)){
			RuleBuilder.execute(actionKey, ai);
		}
		else{
			ai.invoke();
		}
	}
}