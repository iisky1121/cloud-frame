package com.jfinal.ext.plugin.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Enhancer;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Action;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.tx.Tx;

class RuleBuilder {
	private static Map<String,List<Rule>> beforeMap = new HashMap<String,List<Rule>>();
	private static Map<String,List<Rule>> afterMap = new HashMap<String,List<Rule>>();
	
	static void setBefore(String ruleId, Rule rule){
		if(beforeMap.containsKey(ruleId)){
			checkLevelInList(ruleId, rule, true);
			beforeMap.get(ruleId).add(rule);
		}
		else{
			List<Rule> rules = new ArrayList<Rule>();
			rules.add(rule);
			
			beforeMap.put(ruleId, rules);
		}
		LogKit.debug("设置Before规则[?] \n Rule:?", ruleId, rule);
	}
	
	static void setAfter(String ruleId, Rule rule){
		if(afterMap.containsKey(ruleId)){
			checkLevelInList(ruleId, rule, false);
			afterMap.get(ruleId).add(rule);
		}
		else{
			List<Rule> rules = new ArrayList<Rule>();
			rules.add(rule);
			
			afterMap.put(ruleId, rules);
		}
		LogKit.debug("设置After规则[?] \n Rule:?", ruleId, rule);
	}
	
	static boolean contains(String ruleId){
		return beforeMap.containsKey(ruleId) || afterMap.containsKey(ruleId);
	}

	static void invoke(String ruleId, Invocation ai) throws Exception{
		RuleService service;
		List<Rule> list = null;
		
		/////////////////////////////////
		list = beforeMap.get(ruleId);
		if(list != null){
			//按等级排序,执行顺序1-2-3
			sortByLevel(list);
			//执行before
			for(Rule rule : list){
				service = rule.getServiceClass().newInstance();
				service.setController(ai.getController());
				rule.getMethod().invoke(service);
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
			for(Rule rule : list){
				service = rule.getServiceClass().newInstance();
				service.setController(ai.getController());
				rule.getMethod().invoke(service);
	    	}
		}
	}
	
	static void execute(String ruleId, Invocation ai){
		//重新构建一个Invocation，备用
		Invocation cloneAi = new Invocation(new Action(ai.getControllerKey(), ai.getActionKey(), ai.getController().getClass(), ai.getMethod(), ai.getMethodName(), new Interceptor[]{}, ai.getViewPath()), ai.getController());
		try {
			Enhancer.enhance(RuleService.class, Tx.class).invoke(ruleId, ai);
		} catch (Exception e) {
			if(!(e instanceof RuleException)){
				//异常，默认执行原方法
				cloneAi.invoke();
				LogKit.error("规则["+ruleId+"] 执行错误,Error:"+e.getMessage()+",Cause:"+e.getCause()+",执行原方法。");
			}
			else{
				LogKit.error("规则["+ruleId+"] 执行错误,Error:"+e.getMessage()+",Cause:"+e.getCause()+",终止执行。");
			}
		}
	}
	
	private static void sortByLevel(List<Rule> rules){
		Collections.sort(rules, new Comparator<Rule>() {
            public int compare(Rule arg0, Rule arg1) {
                return arg0.getLevel().compareTo(arg1.getLevel());
            }
        });
	}
	
	private static void checkLevelInList(String ruleId, Rule rule, boolean isBefore){
		List<Rule> rules;
		if(isBefore){
			rules = beforeMap.get(ruleId);
		}
		else{
			rules = afterMap.get(ruleId);
		}
		for(Rule r : rules){
			if(r.getLevel() == rule.getLevel()){
				throw new IllegalArgumentException("规则["+ruleId+"] 属性[level=" + rule.getLevel() + "]重复，在[" + rule.getServiceClass().getName()+"."+ rule.getMethod().getName() + "() @"+(isBefore?"RuleBefore":"RuleAfter")+"]");
			}
		}
	}
}