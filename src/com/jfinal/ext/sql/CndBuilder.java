package com.jfinal.ext.sql;

import com.jfinal.ext.kit.ModelKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

class CndBuilder {
	/**
	 * 组建各种sql及赋值
	 */
	static void buildSQL(StringBuilder sb, Param param, ArrayList<Object> params) {
		buildSQL(sb, param.getType(), param.getKey(), param.getValue(), params);
	}
	static void buildSQL(StringBuilder sb, Cnd.Type queryType, String fieldName, Object fieldValue, ArrayList<Object> params) {
        // 非空的时候进行设置
        if ((StrKit.notNull(fieldValue) || queryType.equals(Cnd.Type.empty) || queryType.equals(Cnd.Type.not_empty)) 
        		&& StrKit.notNull(fieldName)) {
        	Object[] values = CndKit.buildValue(queryType, fieldValue);
        	if(values == null){
        		return;
        	}
        	sb.append(Cnd.AND +fieldName + values[0]);
        	if(values[1] == null){
        		return;
        	}
        	
        	if(values[1] instanceof Object[]){
        		for(Object obj : (Object[])values[1]){
        			params.add(obj);
        		}
        	}
        	else{
        		params.add(values[1]);
        	}
        }
    }
	
	
	/**
	 * 组建各种order by及赋值
	 */
	static void buildOrderBy(Map<String,Cnd.OrderByType> orderByMap, StringBuilder sql){
		int i = 0, size = orderByMap.size();
		for(Entry<String,Cnd.OrderByType> entry : orderByMap.entrySet()){
			if(i == 0){
				sql.append(" order by ");
			}
			if(entry.getValue() == null){
				sql.append(entry.getKey());
			} else {
				sql.append(entry.getKey()+ " " + entry.getValue().name());
			}
			if(i != size -1){
				sql.append(",");
			}
			i++;
		}
	}
	
	/**
	 * 组建各种group by及赋值
	 */
	static void buildGroupBy(Set<String> groupBySet, StringBuilder sql){
		int i = 0,size=groupBySet.size();
		for(String groupBy : groupBySet){
			if(i == 0){
				sql.append(" group by ");
			}
			sql.append(groupBy);
			if(i != size -1){
				sql.append(",");
			}
			i++;
		}
	}
	
	/**
	 * 组装全局模糊查询条件
	 */
	static void bulidFuzzyQuery(Map<String,String> fuzzyQueryMap, StringBuilder sql, List<Object> paramList){
		if(fuzzyQueryMap.size() > 0){
			int i = 0;
			sql.append(" and (");
			for(Entry<String,String> entry: fuzzyQueryMap.entrySet()){
				if (i > 0) {
		    		sql.append(" or ");
		    	}
				sql.append(entry.getKey() + " like ?");
		    	paramList.add("%" + entry.getValue() + "%");
		    	i++;
			}
		    sql.append(")");  
		    i++;
		}
	}

	/*#####################################################################################################################################*/
	static void init(Cnd cnd, Object ...objects){
		init(cnd, null, objects);
	}

	static void init(Cnd cnd, Map<String, String[]> paras, Object ...objects){
		int len = objects.length;
		if (len % 2 != 0 ) {
			throw new IllegalArgumentException("参数需为：成对的(key,value)列表");
		}

		for (int i =0 ; i < len; i+=2) {
			CndBuilder.init(cnd, paras, objects[i], objects[i + 1]);
		}
	}

	static void init(Cnd cnd, Map<String, String[]> paras, Object object, Object alias){
		if(object == null || alias == null){
			throw new IllegalArgumentException("参数不允许存在值为空值或者空字符串");
		}

		if(object instanceof Model && alias instanceof String) {
			initForModel(cnd, (Model)object, (String)alias);
		} else if(paras != null && object instanceof Class && alias instanceof String) {
			//初始化model
			if(Model.class.isAssignableFrom((Class<?>) object)){
				initForModelClass(cnd, paras, (Class<? extends Model>) object, (String) alias);
			} else if(IBean.class.isAssignableFrom((Class<?>) object)) {
				initForBeanClass(cnd, paras, (Class<?>) object, (String) alias);
			}
		} else {
			throw new IllegalArgumentException(String.format("%s 参数需为Model、IBean、Model.Class、IBean.class类型", object));
		}
	}
	/**
	 * 初始化Model, for modelClass
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void initForModelClass(Cnd cnd, Map<String, String[]> paras, Class<? extends Model> modelClass, String alias){
		alias = (alias==null?StrKit.firstCharToLowerCase(modelClass.getSimpleName()):("".equals(alias.trim())?"":(alias+".")));
		Model model = ModelKit.newInstance(modelClass);
		if(model == null){
			return;
		}

		List<String> fuzzyQueryList = Arrays.asList(model.getFuzzyQuery());
		List<String> orderByList = Arrays.asList(model.getOrderBy());
		Set<Entry<String, Class<?>>> attrs = model.getColumns().entrySet();
		for(Entry<String, Class<?>> entry : attrs){
			String newKey = alias + entry.getKey();
			cnd.addColumn(newKey, entry.getValue());
			//初始化query column, 默认String
			if((fuzzyQueryList.size() == 0 && entry.getValue().equals(String.class)) || fuzzyQueryList.contains(entry.getKey())){
				cnd.addFuzzyQueryColumn(newKey);
			}
			//初始化order column,默认全部
			if(orderByList.size() == 0 || orderByList.contains(entry.getKey())){
				cnd.addOrderByColumn(newKey);
			}
			//初始化query column
			if(paras.containsKey(newKey) && !StrKit.isBlank(paras.get(newKey)[0])){
				cnd.addQuery(newKey, Param.create(newKey, paras.get(newKey)[0], entry.getValue()));
			}
		}
	}
	/**
	 * 初始化Model, for beanClass
	 */
	static void initForBeanClass(Cnd cnd, Map<String, String[]> paras, Class<?> beanClass, String alias){
		if(beanClass == null){
			return;
		}
		alias = (alias==null?StrKit.firstCharToLowerCase(beanClass.getSimpleName()):("".equals(alias.trim())?"":(alias+".")));

		Field[] fields = beanClass.getDeclaredFields();
		for(Field field : fields){
			String newKey = alias + field.getName();
			cnd.addColumn(newKey, field.getType());
			//初始化query column, 默认String
			if(field.getType().equals(String.class)){
				cnd.addFuzzyQueryColumn(newKey);
			}
			//初始化order column,默认全部
			cnd.addOrderByColumn(newKey);
			//初始化query column
			if(paras.containsKey(newKey) && !StrKit.isBlank(paras.get(newKey)[0])){
				cnd.addQuery(newKey, Param.create(newKey, paras.get(newKey)[0], field.getType()));
			}
		}
	}

	/**
	 * 初始化Model, for model
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void initForModel(Cnd cnd, Model model, String alias){
		alias = (alias==null?StrKit.firstCharToLowerCase(model.getClass().getSimpleName()):("".equals(alias.trim())?"":(alias+".")));
		if(model == null){
			return;
		}

		List<String> fuzzyQueryList = Arrays.asList(model.getFuzzyQuery());
		List<String> orderByList = Arrays.asList(model.getOrderBy());
		List<String> attrNameList = Arrays.asList(model._getAttrNames());
		Set<Entry<String, Class<?>>> attrs = model.getColumns().entrySet();
		for(Entry<String, Class<?>> entry : attrs){
			String newKey = alias + entry.getKey();
			cnd.addColumn(newKey, entry.getValue());
			//初始化query column, 默认String
			if((fuzzyQueryList.size() == 0 && entry.getValue().equals(String.class)) || fuzzyQueryList.contains(entry.getKey())){
				cnd.addFuzzyQueryColumn(newKey);
			}
			//初始化order column,默认全部
			if((orderByList.size() == 0) || orderByList.contains(entry.getKey())){
				cnd.addOrderByColumn(newKey);
			}
			//初始化query column
			if(attrNameList.contains(entry.getKey()) && model.get(entry.getKey()) != null){
				cnd.addQuery(newKey, Param.create(newKey, model.get(entry.getKey()), entry.getValue()));
			}
		}
	}
}
