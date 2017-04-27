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
	static void buildSQL(StringBuilder sb, CndParam param, List<Object> params) {
		buildSQL(sb, param.getSymbol(), param.getType(), param.getKey(), param.getValue(), params);
	}
	static void buildSQL(StringBuilder sb, Cnd.Symbol symbol, Cnd.Type queryType, String fieldName, Object fieldValue, List<Object> params) {
        // 非空的时候进行设置
        if ((StrKit.notNull(fieldValue) || queryType.equals(Cnd.Type.empty) || queryType.equals(Cnd.Type.not_empty)) 
        		&& StrKit.notNull(fieldName)) {
        	Object[] values = CndKit.buildValue(queryType, fieldValue);
        	if(values == null){
        		return;
        	}
        	sb.append(String.format(Cnd.$BLANK_FNT, symbol.name()) +fieldName + values[0]);
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
    static void buildSql(StringBuilder sb, CndParam param, List<Object> params){
		Object[] values = CndKit.buildValue(param.getType(), param.getValue());
		if(values == null){
			return;
		}
		sb.append(param.getKey() + values[0]);
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

	/**
	 * 组建各种order by及赋值
	 */
	static void build$Limit(StringBuilder sb, Integer offset, Integer limit){
		if(offset != null){
			sb.append(String.format(" limit %s,%s", offset, limit));
		} else if(limit != null){
			sb.append(String.format(" limit %s", limit));
		}
	}
	
	/**
	 * 组建各种order by及赋值
	 */
	static void build$OrderBy(StringBuilder sb, Map<String,Cnd.OrderByType> orderByMap){
		int i = 0, size = orderByMap.size();
		for(Entry<String,Cnd.OrderByType> entry : orderByMap.entrySet()){
			if(i == 0){
				sb.append(" order by ");
			}
			if(entry.getValue() == null){
				sb.append(entry.getKey());
			} else {
				sb.append(entry.getKey()+ " " + entry.getValue().name());
			}
			if(i != size -1){
				sb.append(",");
			}
			i++;
		}
	}
	
	/**
	 * 组建各种group by及赋值
	 */
	static void build$GroupBy(StringBuilder sb, Set<String> groupBySet){
		int i = 0,size=groupBySet.size();
		for(String groupBy : groupBySet){
			if(i == 0){
				sb.append(" group by ");
			}
			sb.append(groupBy);
			if(i != size -1){
				sb.append(",");
			}
			i++;
		}
	}
	
	/**
	 * 组装全局模糊查询条件
	 */
	static void bulid$FuzzyQuery(StringBuilder sb, List<Object> paramList, Map<String,String> fuzzyQueryMap){
		if(fuzzyQueryMap.size() > 0){
			int i = 0;
			sb.append(" and (");
			for(Entry<String,String> entry: fuzzyQueryMap.entrySet()){
				if (i > 0) {
					sb.append(" or ");
		    	}
				sb.append(entry.getKey() + " like ?");
		    	paramList.add("%" + entry.getValue() + "%");
		    	i++;
			}
			sb.append(")");
		    i++;
		}
	}

	/*#####################################################################################################################################*/
	static void init(CndModelSelect cnd, Object ...objects){
		init(cnd, null, objects);
	}

	static void init(CndModelSelect cnd, Map<String, String[]> paras, Object ...objects){
		int len = objects.length;
		if (len % 2 != 0 ) {
			throw new IllegalArgumentException("参数需为：成对的(key,value)列表");
		}

		for (int i =0 ; i < len; i+=2) {
			CndBuilder.init(cnd, paras, objects[i], objects[i + 1]);
		}
	}

	@SuppressWarnings("unchecked")
	static void init(CndModelSelect cnd, Map<String, String[]> paras, Object object, Object alias){
		if(object == null || alias == null){
			throw new IllegalArgumentException("参数不允许存在值为空值或者空字符串");
		}

		if(object instanceof Model && alias instanceof String) {
			initForModel(cnd, (Model<?>)object, (String)alias);
		} else if(paras != null && object instanceof Class && alias instanceof String) {
			//初始化model
			if(Model.class.isAssignableFrom((Class<?>) object)){
				initForModelClass(cnd, paras, (Class<? extends Model<?>>) object, (String) alias);
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
	static void initForModelClass(CndModelSelect cnd, Map<String, String[]> paras, Class<? extends Model> modelClass, String alias){
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
				cnd.addQuery(newKey, CndParam.create(newKey, paras.get(newKey)[0], entry.getValue()));
			}
		}
	}
	/**
	 * 初始化Model, for beanClass
	 */
	static void initForBeanClass(CndModelSelect cnd, Map<String, String[]> paras, Class<?> beanClass, String alias){
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
				cnd.addQuery(newKey, CndParam.create(newKey, paras.get(newKey)[0], field.getType()));
			}
		}
	}

	/**
	 * 初始化Model, for model
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static void initForModel(CndModelSelect cnd, Model model, String alias){
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
				cnd.addQuery(newKey, CndParam.create(newKey, model.get(entry.getKey()), entry.getValue()));
			}
		}
	}
	
	static void build$CndWhere(CndWhere where, StringBuilder sb, List<Object> params) {
		for(Entry<Integer, CndGroup> entry : where.getWheres().entrySet()){
			build$CndGroup(entry.getValue(), sb, params);
		}
	}

	static void build$CndGroup(CndGroup group, StringBuilder sb, List<Object> params) {
		if(group.isEmtry()){
			return;
		}
		if(group.getSymbol() != null){
			sb.append(String.format(Cnd.$BLANK_FNT, group.getSymbol().name()));
		}
		int index = 0;
		sb.append("(");
		for(CndParam param : group.getParams()){
			if(index > 0 && param.getSymbol() != null){
				sb.append(String.format(Cnd.$BLANK_FNT, param.getSymbol().name()));
			}
			buildSql(sb, param, params);
			index++;
		}
		if(group.hasGroup()){
			for(CndGroup g : group.getGroupList()){
				build$CndGroup(g, sb, params);
			}
		}
		sb.append(")");
	}

	static void build$Set(Map<String, Object> sets, StringBuilder sb,List<Object> params){
		int i =0, setSize = sets.size();
		for(Map.Entry<String,Object> entry : sets.entrySet()){
			if(entry.getValue() instanceof CndUpdate.IncrBy){
				CndUpdate.IncrBy incrBy = (CndUpdate.IncrBy) entry.getValue();
				sb.append(String.format("%s = %s %s ?", entry.getKey(), incrBy.getKey(), incrBy.getVal()>=0?"+":"-"));
			} else {
				sb.append(String.format("%s = ?", entry.getKey()));
			}
			params.add(entry.getValue());
			if(i<setSize-1){
				sb.append(",");
			}
			i++;
		}
	}

	static void build$DRQ(CndWhere where, Map<String, CndParam> defaults, Set<String> removes, Map<String, CndParam> querys){
		//默认值优先
		for(Map.Entry<String, CndParam> entry : defaults.entrySet()){
			if(removes.contains(entry.getKey())){
				continue;
			}
			if(querys.containsKey(entry.getKey()) && entry.getValue().getValue()==null){
				entry.getValue().setValue(querys.get(entry.getKey()).getValue());
				where.and(entry.getValue());
				querys.remove(entry.getKey());
			} else {
				where.and(entry.getValue());
			}
		}
		//构建查询条件
		for(Map.Entry<String, CndParam> entry : querys.entrySet()){
			if(removes.contains(entry.getKey())){
				continue;
			}
			where.and(entry.getValue());
		}
	}

	static void build$Symbol(CndWhere where, StringBuilder sb, StringBuilder sql){
		//设置where关键字，解决1=1效率的问题
		if(!StrKit.isBlank(sb.toString())){
			if(where.hasWhere()){
				sql.append(String.format(Cnd.$BLANK_FNT, Cnd.$WHERE).concat(sb.toString()));
			} else {
				sql.append(String.format(Cnd.$BLANK_FNT, Cnd.Symbol.and.name()).concat(sb.toString()));
			}
		}
	}
	
    public static void main(String[] args) {
    	StringBuilder sb = new StringBuilder();
    	List<Object> params = new ArrayList<Object>();
    	CndWhere where = new CndWhere()
				.andGroup(
					Cnd.$group().create("a", "1").and("b", ">1").orGroup(
							Cnd.$group().create("a", "1").and("b", ">1")
					)
				)
				.orGroup(
						Cnd.$group().create("a", "1").and("b", ">1")
				)
				.or("ccc", 1)
				.and("aaa", ">111");

    	build$CndWhere(where, sb, params);
    	
    	System.out.println(sb.toString());
	}
}
