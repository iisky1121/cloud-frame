package com.jfinal.ext.plugin.validate;

import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.StrKit;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ValidationKit {
	@SuppressWarnings("rawtypes")
	public static Boolean groovyCheck(Map map, String express){
        return groovyCheck(groovyBinding(map), express);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public static Boolean groovyCheck(final String name, final Object value, String express){
		if(StrKit.isBlank(name)){
			return false;
		}
		return groovyCheck(new HashMap(){{
			put(name, value);
		}}, express);
	}
	
	@SuppressWarnings("rawtypes")
	public static Binding groovyBinding(Map map){
		return new Binding(map);
	}
	
	public static Boolean groovyCheck(Binding bind, String express){
		if(bind == null){
			return false;
		}
		GroovyShell shell = new GroovyShell(bind);               
        return (Boolean) shell.evaluate(express);
	}
	
	
	public static void main(String[] args) {
        System.out.println(groovyCheck("age", "1!2", "age ==~ /\\d+/"));
	}
}
