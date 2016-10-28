package com.jfinal.ext.plugin.uribind;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * RuleInterceptor
 */
public class UriRuleInterceptor implements Interceptor {
	@Override
	public void intercept(Invocation ai) {
		//规则判断
		String actionKey = ai.getActionKey();
		if(UriRuleBuilder.contains(actionKey)){
			UriRuleBuilder.execute(actionKey, ai);
		}
		else{
			ai.invoke();
		}
	}
}