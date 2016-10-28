package com.jfinal.ext.plugin.uribind;

import java.lang.reflect.Method;
import java.util.List;

import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.IPlugin;

public class UriRulePlugin implements IPlugin{
	public boolean start() {
		//扫描注解
		List<Class<? extends UriRuleService>> serviceClasses = ClassSearcher.of(UriRuleService.class).includeAllJarsInLib(false).search();
        UriBind ub;
        String ruleId;
        Method[] methods;
        for (Class<? extends UriRuleService> serviceClass : serviceClasses) {
        	methods = serviceClass.getMethods();
        	for(Method method : methods){
        		ub = (UriBind)method.getAnnotation(UriBind.class);
            	if (ub != null) {
            		ruleId = ub.rule();
            		if(!StrKit.isBlank(ruleId)){
            			if(ub.before()){//设置before
            				UriRuleBuilder.setBefore(ruleId, new UriRule(serviceClass, method, ub.level()));
            			}
            			else{//设置after
            				UriRuleBuilder.setAfter(ruleId, new UriRule(serviceClass, method, ub.level()));
            			}
            		}
            	}
        	}
        	
        }
        //加入全局规则拦截器
		JFinalKit.getInterceptors().add(new UriRuleInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
