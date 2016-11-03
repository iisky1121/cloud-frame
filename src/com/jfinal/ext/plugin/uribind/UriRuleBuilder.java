package com.jfinal.ext.plugin.uribind;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.aop.Enhancer;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;

class UriRuleBuilder {
	private static Map<String, _UriRule> uriMaps;

	static void addRule(UriBind uriBind, UriRule rule){
		if(uriBind == null || rule == null){
			throw new RuntimeException("添加规则失败，规则为null");
		}
		
		if(uriMaps == null){
			uriMaps = new HashMap<String, _UriRule>();
		}
		
		if(contains(uriBind)){
			throw new RuntimeException(String.format("添加规则失败，规则：%s 已存在", uriBind.uri()));
		}
		uriMaps.put(uriBind.uri(), new _UriRule(uriBind, rule));
	}
	
	static boolean contains(UriBind uriBind){
		if(uriBind == null){
			return false;
		}
		return contains(uriBind.uri());
	}
	
	static boolean contains(String uri){
		if(StrKit.isBlank(uri) || uriMaps == null){
			return false;
		}
		return uriMaps.containsKey(uri);
	}
	
	static void execute(Invocation ai){
		_UriRule _rule = uriMaps.get(ai.getActionKey());
		if(_rule != null){
			Enhancer.enhance(_rule.getRule().getClass()).execute(ai, _rule.getUriBind());
		}
	}
}

class _UriRule{
	private String uri;
	private UriBind uriBind;
	private UriRule rule;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public UriBind getUriBind() {
		return uriBind;
	}
	public void setUriBind(UriBind uriBind) {
		this.uriBind = uriBind;
	}
	public UriRule getRule() {
		return rule;
	}
	public void setRule(UriRule rule) {
		this.rule = rule;
	}
	public _UriRule(UriBind uriBind, UriRule rule) {
		this.uri = uriBind.uri();
		this.uriBind = uriBind;
		this.rule = rule;
	}
}