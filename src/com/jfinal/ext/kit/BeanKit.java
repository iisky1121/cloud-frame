package com.jfinal.ext.kit;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanKit {
	/**
	 * 通用功能，实体类bean属性复制功能
	 * 描述：把source不为空的属性复制到target中
	 * 注意：
	 * 1、该方法只适用于实体类中
	 * 2、source和target必须为同一类型参数，否则直接返回target
	 * @Title: copyNotNullVal
	 * @return Object
	 */
	public static Object copyNotNullVal(Object source, Object target ){
		if(!source.getClass().equals(target.getClass())){
			return target;
		}
		
		Map<String,Object> map = build(source, target);
		Field[] targetFields = (Field[]) map.get("targetFields");
		Object[] sourceObjects = (Object[]) map.get("sourceObjects");
		Object[] targetObjects = (Object[]) map.get("targetObjects");
		
		try{
			for(int i = 0; i < targetFields.length; i++){			
				if(sourceObjects[i]!=null&&sourceObjects[i]!=targetObjects[i]){
					targetFields[i].setAccessible(true);
					targetFields[i].set(target, sourceObjects[i]);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return target;
	}
	
	/**
	 * 通用功能，实体类bean属性复制功能
	 * 描述：把target属性值到sources为空属性中
	 * 注意：
	 * 1、该方法只适用于实体类中
	 * 2、source和target必须为同一类型参数，否则直接返回target
	 * @Title: setNullAttrFrom
	 * @return Object
	 */
	public static Object setNullAttrFrom(Object source, Object target ){
		if(!source.getClass().equals(target.getClass())){
			return target;
		}
		
		Map<String,Object> map = build(source, target);
		Field[] sourceFields = (Field[]) map.get("sourceFields");
		Object[] sourceObjects = (Object[]) map.get("sourceObjects");
		Object[] targetObjects = (Object[]) map.get("targetObjects");
		
		try{
			for(int i = 0; i < sourceFields.length; i++){
				if(sourceObjects[i]!=null&&targetObjects[i]==null){
					sourceFields[i].setAccessible(true);
					sourceFields[i].set(source, targetObjects[i]);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return target;
	}
	
	private static Map<String,Object> build(Object source, Object target){
		try{
			Map<String,Object> map = new HashMap<String, Object>();
			
			Field[] sourceField = source.getClass().getDeclaredFields();
			Field[] targetField = target.getClass().getDeclaredFields();
			Field filed;
			Method m;
			List<Object> list1 = new ArrayList<Object>();
			List<Object> list2 = new ArrayList<Object>();
			for (int i = 0; i < sourceField.length; i++){
				//遍历所有属性
				filed=sourceField[i];
				////遍历所有属性get方法
				m = new PropertyDescriptor(filed.getName(), source.getClass()).getReadMethod();
				
				list1.add(m.invoke(source, new Object[]{}));
				list2.add(m.invoke(target, new Object[]{}));
			}
			map.put("sourceFields", sourceField);
			map.put("targetFields", targetField);
			map.put("sourceObjects", list1.toArray());
			map.put("targetObjects", list2.toArray());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new HashMap<String,Object>();
	}
}
