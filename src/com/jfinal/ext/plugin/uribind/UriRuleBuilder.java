package com.jfinal.ext.plugin.uribind;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Enhancer;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.tx.Tx;

class UriRuleBuilder {
	private static Map<String,List<UriRule>> beforeMap = new HashMap<String,List<UriRule>>();
	private static Map<String,List<UriRule>> afterMap = new HashMap<String,List<UriRule>>();
	
	static void setBefore(String ruleId, UriRule UriRule){
		if(beforeMap.containsKey(ruleId)){
			checkLevelInList(ruleId, UriRule, true);
			beforeMap.get(ruleId).add(UriRule);
		}
		else{
			List<UriRule> UriRules = new ArrayList<UriRule>();
			UriRules.add(UriRule);
			
			beforeMap.put(ruleId, UriRules);
		}
		LogKit.debug("设置Before规则[?] \n Rule:?", ruleId, UriRule);
	}
	
	static void setAfter(String ruleId, UriRule UriRule){
		if(afterMap.containsKey(ruleId)){
			checkLevelInList(ruleId, UriRule, false);
			afterMap.get(ruleId).add(UriRule);
		}
		else{
			List<UriRule> UriRules = new ArrayList<UriRule>();
			UriRules.add(UriRule);
			
			afterMap.put(ruleId, UriRules);
		}
		LogKit.debug("设置After规则[?] \n Rule:?", ruleId, UriRule);
	}
	
	static boolean contains(String ruleId){
		return beforeMap.containsKey(ruleId) || afterMap.containsKey(ruleId);
	}

	static void invoke(String ruleId, Invocation ai) throws Exception{
		UriRuleService service;
		List<UriRule> list = null;
		
		/////////////////////////////////
		list = beforeMap.get(ruleId);
		if(list != null){
			//按等级排序,执行顺序1-2-3
			sortByLevel(list);
			//执行before
			for(UriRule UriRule : list){
				service = UriRule.getServiceClass().newInstance();
				service.setController(ai.getController());
				UriRule.getMethod().invoke(service);
	    	}
		}
		
		/////////////////////////////////
		//执行拦截方法
		ai.invoke();
		
		/////////////////////////////////
		list = afterMap.get(ruleId);
		if(list != null){
			//按等级排序,执行顺序1-2-3
			sortByLevel(list);
			//执行after
			for(UriRule UriRule : list){
				service = UriRule.getServiceClass().newInstance();
				service.setController(ai.getController());
				UriRule.getMethod().invoke(service);
	    	}
		}
	}
	
	static void execute(String ruleId, Invocation ai){
		try {
			Enhancer.enhance(UriRuleService.class, Tx.class).invoke(ruleId, ai);
		} catch (Exception e) {
			LogKit.error("规则["+ruleId+"] 执行错误,Error:"+e.getMessage()+",Cause:"+e.getCause()+",终止执行。");
		}
	}
	
	private static void sortByLevel(List<UriRule> UriRules){
		Collections.sort(UriRules, new Comparator<UriRule>() {
            public int compare(UriRule arg0, UriRule arg1) {
                return arg0.getLevel().compareTo(arg1.getLevel());
            }
        });
	}
	
	private static void checkLevelInList(String ruleId, UriRule UriRule, boolean isBefore){
		List<UriRule> UriRules;
		if(isBefore){
			UriRules = beforeMap.get(ruleId);
		}
		else{
			UriRules = afterMap.get(ruleId);
		}
		for(UriRule r : UriRules){
			if(r.getLevel() == UriRule.getLevel()){
				throw new IllegalArgumentException("规则["+ruleId+"] 属性[level=" + UriRule.getLevel() + "]重复，在[" + UriRule.getServiceClass().getName()+"."+ UriRule.getMethod().getName() + "() @"+(isBefore?"RuleBefore":"RuleAfter")+"]");
			}
		}
	}
	
}

class UriRule {   
	private final Class<? extends UriRuleService> serviceClass;
	private final Method method;
	private final Integer level;
	
	public UriRule(Class<? extends UriRuleService> serviceClass, Method method, Integer level) {
		this.serviceClass = serviceClass;
		this.method = method;
		this.level = level;
	}
	public Method getMethod() {
		return method;
	}
	public Class<? extends UriRuleService> getServiceClass() {
		return serviceClass;
	}
	public Integer getLevel() {
		return level;
	}
}