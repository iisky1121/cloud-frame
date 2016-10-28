package com.jfinal.ext.plugin.rule;

import java.lang.reflect.Method;
import java.util.List;

import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;

public class RulePlugin implements IPlugin{
	public boolean start() {
		//扫描注解
		List<Class<? extends RuleService>> serviceClasses = ClassSearcher.of(RuleService.class).includeAllJarsInLib(false).search();
        RuleBefore rb;
        RuleAfter ra;
        String ruleId;
        Method[] methods;
        for (Class<? extends RuleService> serviceClass : serviceClasses) {
        	methods = serviceClass.getMethods();
        	for(Method method : methods){
        		//设置before
        		rb = (RuleBefore)method.getAnnotation(RuleBefore.class);
            	if (rb != null) {
            		ruleId = rb.rule();
            		if(!StrKit.isBlank(ruleId)){
            			RuleBuilder.setBefore(ruleId, new Rule(serviceClass, method, rb.level()));
            		}
            	}
            	//设置after
            	ra = (RuleAfter)method.getAnnotation(RuleAfter.class);
            	if (ra != null) {
            		ruleId = ra.rule();
            		if(!StrKit.isBlank(ruleId)){
            			RuleBuilder.setAfter(ruleId, new Rule(serviceClass, method, ra.level()));
            		}
            	}
        	}
        	
        }
        //加入全局规则拦截器
		JFinalKit.getInterceptors().add(new RuleInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
