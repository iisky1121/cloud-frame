package com.jfinal.ext.plugin.validate;

import com.jfinal.base.BaseConfig;
import com.jfinal.base.ReturnResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;

import java.util.Arrays;
import java.util.Map;


/**
 * 
 * @author iisky1121
 * 2017年1月6日 下午4:43:42
 */
public class ValidateKit {
	private static boolean isBlank(Map<String, String[]> paraMap, String attr){
		return paraMap.get(attr) == null || paraMap.get(attr).length == 0 || StrKit.isBlank(paraMap.get(attr)[0]);
	}

	private static boolean expressCheck(String name, String express, Object value){
		String booleanStr = Engine.use()
				.getTemplateByString(String.format("#(%s)", express))
				.renderToString(Kv.by(name, value));
		return booleanStr.equals("true");
	}
	public static ReturnResult expressCheck(Controller controller, Validate validate){
		Object object = controller.getParaByClazz(validate.name(), validate.clazz());
		if(object != null){
			if(!expressCheck(validate.name(), validate.express(), object)){
				return BaseConfig.attrValueError(validate.name())
						.setCause(String.format("属性[%s]值应符合规则[%s]", validate.name(), validate.express()));
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
        System.out.println(expressCheck("age", "age >1 && age <3", 1));
	}
}
