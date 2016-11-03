package com.jfinal.ext.plugin.uribind;

import java.util.List;

import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class UriRulePlugin implements IPlugin{
	private UriRuleInterceptor interceptor;
	public UriRuleInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new UriRuleInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(UriRuleInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	public boolean start() {
		//扫描注解
		List<Class<? extends UriRule>> rules = ClassSearcher.of(UriRule.class).includeAllJarsInLib(false).search();
        UriBind ub;
        for (Class<? extends UriRule> rule : rules) {
        	ub = rule.getAnnotation(UriBind.class);
        	if (ub != null) {
        		try {
					UriRuleBuilder.addRule(ub, rule.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
        	}
        }
        //加入全局规则拦截器
		JFinalKit.getInterceptors().add(getInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
