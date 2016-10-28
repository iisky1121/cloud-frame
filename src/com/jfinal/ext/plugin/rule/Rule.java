package com.jfinal.ext.plugin.rule;

import java.lang.reflect.Method;

class Rule {   
	private final Class<? extends RuleService> serviceClass;
	private final Method method;
	private final Integer level;
	
	public Rule(Class<? extends RuleService> serviceClass, Method method, Integer level) {
		this.serviceClass = serviceClass;
		this.method = method;
		this.level = level;
	}
	public Method getMethod() {
		return method;
	}
	public Class<? extends RuleService> getServiceClass() {
		return serviceClass;
	}
	public Integer getLevel() {
		return level;
	}
}