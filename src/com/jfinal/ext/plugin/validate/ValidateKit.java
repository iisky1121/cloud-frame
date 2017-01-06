package com.jfinal.ext.plugin.validate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jfinal.base.BaseConfig;
import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ValidateKit {
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
	
	private static boolean isBlank(Map<String, String[]> paraMap, String attr){
		return paraMap.get(attr) == null || paraMap.get(attr).length == 0 || StrKit.isBlank(paraMap.get(attr)[0]);
	}
	
	private static Map<String,String> toMap(Controller controller){
		Map<String, String[]> paraMap = controller.getParaMap();
		Map<String, String> map = new HashMap<String,String>();
		for(Entry<String, String[]> entry : paraMap.entrySet()){
			map.put(entry.getKey(), entry.getValue()==null?null:entry.getValue()[0]);
		}
		return map;
	}
	
	public static ReturnResult groovyCheck(Controller controller, Map<String,String> expressMap){
		Map<String, String> paraMap = toMap(controller);
		Binding bind = ValidateKit.groovyBinding(paraMap);
		for(Entry<String, String> express : expressMap.entrySet()){
			if(!isBlank(controller.getParaMap(), express.getKey()) && !ValidateKit.groovyCheck(bind, express.getValue())){
				return BaseConfig.attrValueError(express.getKey())
						.setCause(String.format("属性[%s]值应符合规则[%s]", express.getKey(), express.getValue()));
			}
		}
		return ReturnResult.success();
	}
	
	public static ReturnResult checkNotNull(Controller controller, String... attrs){
		Map<String, String[]> paraMap = controller.getParaMap();
		for(String attr : attrs){
			if(isBlank(paraMap, attr)){
				return BaseConfig.attrValueEmpty(attr);
			}
		}
		return ReturnResult.success();
	}
	
	public static ReturnResult checkEitherOr(Controller controller, String attr, String[] eitherOr){
		Map<String, String[]> paraMap = controller.getParaMap();
		if(isBlank(paraMap, attr)){
			for(String eOr : eitherOr){
				if(!isBlank(paraMap, eOr)){
					return ReturnResult.success();
				}
			}
		}
		return BaseConfig.attrEitherOr(attr, eitherOr);
	}
	
	public static ReturnResult checkAssociation(Controller controller, String attr, String[] association){
		Map<String, String[]> paraMap = controller.getParaMap();
		if(!isBlank(paraMap, attr)){
			for(String ass : association){
				if(isBlank(paraMap, ass)){
					return BaseConfig.attrAssociation(attr, association);
				}
			}
			return ReturnResult.success();
		}
		return BaseConfig.attrValueEmpty(attr);
	}
	
	public static ReturnResult checkAttrValue(Controller controller, String attr, Object... values){
		String attrValue = controller.getPara(attr);
		if(!StrKit.isBlank(attrValue) && Arrays.asList(values).contains(attrValue)){
			return ReturnResult.success();
		}
		return BaseConfig.attrValueError(attr)
				.setCause(String.format("属性[%s]值应在列表%s中", Arrays.toString(values)));
	}
	
	public static ReturnResult checkAttrValue(Controller controller, String attr, Class<?> enumClass){
		if(enumClass != null){
			if(!Enum.class.isAssignableFrom(enumClass)){
				throw new IllegalArgumentException("Attribute enumClass type must be enumerated");
			}
			Object[] objects = enumClass.getEnumConstants();
			return checkAttrValue(controller, attr, objects);
		}
		return BaseConfig.attrValueError(attr);
	}
	
	public static void main(String[] args) {
        System.out.println(groovyCheck("age", "1!2", "age ==~ /\\d+/"));
	}
}
