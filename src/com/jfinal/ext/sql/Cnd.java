package com.jfinal.ext.sql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.jfinal.ext.kit.ModelKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
public class Cnd {
	public final static String SELECT_ = "select * ";
	public final static String SELECT_FROM = "select * from `%s`";
	public final static String _FROM = " from %s";
	public final static String DELETE_FROM = "delete from `%s`";
	public final static String IS_EMPTY = "$isEmpty";
	public final static String IS_NOT_EMPTY = "$isNotEmpty";
	public final static String IS_NULL = "$isNull";
	public final static String IS_NOT_NULL = "$isNotNull";
	public final static String AND = " and ";
	public final static String OR = " or ";
	public enum Type {
		equal,// 相等
		not_equal,// 不相等
		less_then,// 小于
		less_equal,// 小于等于
		greater_equal,// 大于等于
		greater_then,// 大于
		fuzzy,// 模糊匹配 %xxx%
		fuzzy_left,// 左模糊 %xxx
		fuzzy_right,// 右模糊 xxx%
		not_empty,// 不为空值的情况
		empty,// 空值的情况
		in,// 在范围内
		not_in, // 不在范围内
		between_and;// 在范围内
	}
	//是否拼接where
	private boolean hasWhere = false;
	//所有的查询字段
	private Map<String, Class<?>> columns = new HashMap<String, Class<?>>();
	//需要进行查询的字段
	private Map<String, Param> querys = new HashMap<String, Param>();
	//默认值设置
	private Map<String, Param> defaults = new HashMap<String, Param>();
	//全文搜索值设置
	private Map<String, String> fuzzyQuerys = new HashMap<String, String>();
	private Set<String> fuzzyQueryColumns = new HashSet<String>();
	//排序值设置
	private Map<String,String> orderBys = new HashMap<String, String>();
	private Set<String> orderByColumns = new HashSet<String>();
	//分组值设置
	private Set<String> groupBys = new HashSet<String>();
	//用于接收SQL语句
    private StringBuilder sql = new StringBuilder();
    //用于接收参数数组
    private List<Object> paramList = new ArrayList<Object>();
    
    public static Cnd toCnd(){
    	Cnd cnd = new Cnd();
    	return cnd;
    }
    /**
	 * Model转换成Cnd
	 */
    @SuppressWarnings({ "rawtypes" })
    public static Cnd toCnd(Model m){
    	return toCnd(m, "");
    }
    
	@SuppressWarnings({ "rawtypes"})
	public static Cnd toCnd(Object ...modelAndAlias) {
		Cnd cnd = new Cnd();
		if (modelAndAlias == null) {
			return cnd;
		}
		else if (modelAndAlias.length % 2 != 0 ) {
			throw new IllegalArgumentException("modelAndalias参数需为：成对的(Model-alias)列表");
		}
		
		Object object = null,alias = null;
		
		for (int i =0 ; i < modelAndAlias.length ; i+=2) {
			object = modelAndAlias[i];
			alias = modelAndAlias[i + 1];
			
			if(object == null || alias == null){
				throw new IllegalArgumentException("参数不允许存在值为空值或者空字符串");
			}
			
			if(object instanceof Model && alias instanceof String){
				cnd.initForModel((Model)object, (String)alias);
			}
			else{
				throw new IllegalArgumentException(String.format("%s 参数需为Model.Class、String类型,参数位置：%s", object, i+1));
			}
		}
		
		return cnd;
	}
	
	public static Cnd queryToCnd(Map<String, String[]> paras, Class<?>... classes){
		List<Object> list = new ArrayList<Object>();
		for(Class<?> c : classes){
			list.add(c);
			list.add(StrKit.firstCharToLowerCase(c.getSimpleName()));
		}
		return queryToCnd(paras, list.toArray());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Cnd queryToCnd(Map<String, String[]> paras, Object ...modelClassAndAlias){
		Cnd cnd = new Cnd();
		if (modelClassAndAlias == null) {
			return cnd;
		}
		else if (modelClassAndAlias.length % 2 != 0 ) {
			throw new IllegalArgumentException("modelClassAndAlias参数需为：成对的(Model.Class-alias)、(TableName-alias)列表");
		}
		
		Object object = null,alias = null;
		
		for (int i =0 ; i < modelClassAndAlias.length ; i+=2) {
			object = modelClassAndAlias[i];
			alias = modelClassAndAlias[i + 1];
			
			if(object == null || alias == null){
				throw new IllegalArgumentException("参数不允许存在值为空值或者空字符串");
			}
		
			if(object instanceof Class && alias instanceof String){
				//初始化model
				if(Model.class.isAssignableFrom((Class<?>) object)){
					cnd.initForModelClass(paras, (Class<? extends Model>) object, (String) alias);
				}
				else if(IBean.class.isAssignableFrom((Class<?>) object)){
					cnd.initForBeanClass(paras, (Class<?>) object, (String) alias);
				}
			}
			else{
				throw new IllegalArgumentException(String.format("%s 参数需为Model.Class、IBean.class类型,参数位置：%s", object, i+1));
			}
		}
		if(paras.get("fuzzyQuery")!=null){
			cnd.setFuzzyQuery(paras.get("fuzzyQuery")[0]);
		}
		if(paras.get("orderBy")!=null){
			String orderBy = paras.get("orderBy")[0];
			if(!StrKit.isBlank(orderBy)){
				cnd.setOrderBy(orderBy.split(","));
			}
		}
		return cnd;
	}
	
	/**
     * 是否拼接where
     */
    public Cnd where(){
    	this.hasWhere = true;
    	return this;
    }
    
    /**
     * 设置值
     */
    public Cnd set(String key, Class<?> classType, Object val){
    	querys.put(key, Param.create(key, val, classType));
    	return this;
    }
    
    /**
     * 设置值
     */
    public Cnd set(String key, Object val){
    	querys.put(key, Param.create(key, val));
    	return this;
    }
    
    /**
     * 设置默认值
     */
    public Cnd setDefault(String key, Cnd.Type type){
    	return setDefault(key, type, null);
    }
    
    /**
     * 设置默认值
     */
    public Cnd setDefault(String key, Cnd.Type type, Object value){
    	defaults.put(key, new Param(key, type, value));
    	return this;
    }
    
    /**
     * 设置fuzzyQuery属性
     */
	public Cnd setFuzzyQuery(String queryStr, String... columns){
    	if(!StrKit.isBlank(queryStr)){
    		fuzzyQuerys.clear();
    		if(columns != null  && columns.length > 0){
    			for(String column : columns){
    				column = getAliasKey(column);
    				if(fuzzyQueryColumns.contains(column)){
    					fuzzyQuerys.put(column, queryStr);
    				}
    			}
    		}
    		else{
    			for(String column: fuzzyQueryColumns){
    				fuzzyQuerys.put(column, queryStr);
    			}
    		}
    	}
    	return this;
    }
    
	/**
	 * 设置OrderBy属性
	 */
	private static List<String> reg = Arrays.asList(new String[]{"asc","desc"});
	public Cnd setOrderBy(String... orderByStrs){
		if(orderByStrs != null  && orderByStrs.length > 0){
			orderBys.clear();
			for(String orderByStr : orderByStrs){
				if(StrKit.isBlank(orderByStr)){
					continue;
				}
				String[] strs = orderByStr.split("_");
				String column;
				if(strs.length == 1){
					column = getAliasKey(strs[0]);
					if(orderByColumns.contains(column)){
						orderBys.put(column, "");
					}
				}
				else if(strs.length == 2){
					column = getAliasKey(strs[0]);
					if(orderByColumns.contains(column) && reg.contains(strs[1].toLowerCase())){
						orderBys.put(column, strs[1].toLowerCase());
					}
				}
			}
		}
		return this;
	}
	
    /**
	 * 设置GroupBy属性
	 */
	public Cnd setGroupBy(String... groupByStrs){
		if(groupByStrs != null  && groupByStrs.length > 0){
			groupBys.clear();
			for(String groupByStr : groupByStrs){
				if(StrKit.isBlank(groupByStr)){
					continue;
				}
				groupBys.add(groupByStr);
			}
		}
    	return this;
    }
    
    /**
	 * 获取参数集合
	 */
    public Object[] getParas() {
        return paramList.toArray();
    }
    
    /**
	 * 获取sql
	 */
    public String getSql() {
        return sql.toString();
    }
    
    /**
	 * 组建各种参数值
	 */
    public Cnd build(){
		ArrayList<Object> paramArrayList = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();
		Object value;
		//默认值优先
		for(Entry<String, Param> entry : defaults.entrySet()){
			if(querys.containsKey(entry.getKey())){
				value = entry.getValue().getValue()==null?querys.get(entry.getKey()).getValue():entry.getValue().getValue();
				CndBuild.buildSQL(sb, entry.getValue().getType(), entry.getValue().getKey(), value, paramArrayList);
				querys.remove(entry.getKey());
			}
			else{
				CndBuild.buildSQL(sb, entry.getValue(), paramArrayList);
			}
		}
		//构建查询条件
		for(Entry<String, Param> entry : querys.entrySet()){
			CndBuild.buildSQL(sb, entry.getValue().getType(), entry.getValue().getKey(), entry.getValue().getValue(), paramArrayList);
		}
		//构建全文搜索
		CndBuild.bulidFuzzyQuery(fuzzyQuerys, sb, paramArrayList);
		//构建分组
		CndBuild.buildGroupBy(groupBys, sb);
		//构建排序
		CndBuild.buildOrderBy(orderBys, sb);
		
		//设置where关键字，解决1=1效率的问题
		if(hasWhere && !StrKit.isBlank(sb.toString())){
			sql.append(sb.toString().replaceFirst("and", "where"));
		}
		else{
			sql.append(sb.toString());
		}
		paramList.addAll(paramArrayList);
    	return this;
    }
    
    /*#####################################################################################################################################*/
    /**
     * 初始化Model, for modelClass
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initForModelClass(Map<String, String[]> paras, Class<? extends Model> modelClass, String alias){
    	alias = (alias==null?StrKit.firstCharToLowerCase(modelClass.getSimpleName()):("".equals(alias.trim())?"":(alias+".")));
    	Model model = ModelKit.newInstance(modelClass);
    	if(model == null){
    		return;
    	}
    	
    	Set<Entry<String, Class<?>>> attrs = model.getColumns().entrySet();
    	for(Entry<String, Class<?>> entry : attrs){
    		String newKey = alias + entry.getKey();
    		columns.put(newKey, entry.getValue());
    		//初始化query column, 默认String
    		if((model.getFuzzyQuery().length == 0 && entry.getValue().equals(String.class)) || Arrays.asList(model.getFuzzyQuery()).contains(entry.getKey())){
    			fuzzyQueryColumns.add(newKey);
    		}
    		//初始化order column,默认全部
    		if(model.getOrderBy().length == 0 || Arrays.asList(model.getOrderBy()).contains(entry.getKey())){
    			orderByColumns.add(newKey);
    		}
    		//初始化query column
    		if(paras.containsKey(newKey) && !StrKit.isBlank(paras.get(newKey)[0])){
    			querys.put(newKey, Param.create(newKey, paras.get(newKey)[0], entry.getValue()));
    		}
    	}
    }
    /**
     * 初始化Model, for beanClass
     */
    private void initForBeanClass(Map<String, String[]> paras, Class<?> beanClass, String alias){
    	if(beanClass == null){
    		return;
    	}
    	alias = (alias==null?StrKit.firstCharToLowerCase(beanClass.getSimpleName()):("".equals(alias.trim())?"":(alias+".")));
    	
    	Field[] fields = beanClass.getDeclaredFields();
    	for(Field field : fields){
    		String newKey = alias + field.getName();
    		columns.put(newKey, field.getType());
    		//初始化query column, 默认String
    		if(field.getType().equals(String.class)){
    			fuzzyQueryColumns.add(newKey);
    		}
    		//初始化order column,默认全部
    		orderByColumns.add(newKey);
    		//初始化query column
    		if(paras.containsKey(newKey) && !StrKit.isBlank(paras.get(newKey)[0])){
    			querys.put(newKey, Param.create(newKey, paras.get(newKey)[0], field.getType()));
    		}
    	}
    }
    
    /**
     * 初始化Model, for model
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void initForModel(Model model, String alias){
    	alias = (alias==null?StrKit.firstCharToLowerCase(model.getClass().getSimpleName()):("".equals(alias.trim())?"":(alias+".")));
    	if(model == null){
    		return;
    	}
    	
    	Set<Entry<String, Class<?>>> attrs = model.getColumns().entrySet();
    	for(Entry<String, Class<?>> entry : attrs){
    		String newKey = alias + entry.getKey();
    		columns.put(newKey, entry.getValue());
    		//初始化query column, 默认String
    		if((model.getFuzzyQuery().length == 0 && entry.getValue().equals(String.class)) || Arrays.asList(model.getFuzzyQuery()).contains(entry.getKey())){
    			fuzzyQueryColumns.add(newKey);
    		}
    		//初始化order column,默认全部
    		if((model.getOrderBy().length == 0) || Arrays.asList(model.getOrderBy()).contains(entry.getKey())){
    			orderByColumns.add(newKey);
    		}
    		//初始化query column
    		if(Arrays.asList(model._getAttrNames()).contains(entry.getKey()) && model.get(entry.getKey()) != null){
    			querys.put(newKey, Param.create(newKey, model.get(entry.getKey()), entry.getValue()));
    		}
    	}
    }
	
	/**
	 * 根据属性名获取 获取带别名属性名
	 */
	private String getAliasKey(String attr){
		String key = null;
		
		if(columns.containsKey(attr)){
			return attr;
		}
		if(attr.indexOf(".") != -1){
			throw new IllegalArgumentException(String.format("属性:%s ,不存在", attr));
		}
		
		int count = 0;
		for(Entry<String, Class<?>> entry : columns.entrySet()){
			if(entry.getKey().endsWith("."+attr)){
				key = entry.getKey();
				count++;
			}
		}
		if(count == 1){
			return key;
		}
		throw new IllegalArgumentException(String.format("存在%s个属性:%s ,请指定相应别名区分", count, attr));
	}
}
