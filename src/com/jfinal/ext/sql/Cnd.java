package com.jfinal.ext.sql;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

import java.util.*;
import java.util.Map.Entry;
public class Cnd {
	public final static String SELECT_ = "select * ";
	public final static String SELECT_FROM = "select * from `%s`";
	public final static String _FROM = " from %s";
	public final static String DELETE_FROM = "delete from `%s`";
	public final static String IS_EMPTY = "$isEmpty";
	public final static String IS_NOT_EMPTY = "$isNotEmpty";
	public final static String IS_NULL = "$isNull";
	public final static String IS_NOT_NULL = "$isNotNull";
	public final static  String BLANK_FNT = " %s ";
	public final static String AND = "and";
	public final static String OR1 = "or";
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
	//字段存储
	private Map<String, Class<?>> columnMap = new HashMap<String, Class<?>>();
	private Set<String> fuzzyQueryColumnSet = new HashSet<String>();
	private Set<String> orderByColumnSet = new HashSet<String>();

	//需要进行查询的字段
	private Map<String, Param> querys = new HashMap<String, Param>();
	//默认值设置
	private Map<String, Param> defaults = new HashMap<String, Param>();
	//排除值设置
	private Set<String>  removes = new HashSet<String>();
	//全文搜索值设置
	private Map<String, String> fuzzyQuerys = new HashMap<String, String>();
	//排序值设置
	enum OrderByType{
		asc,desc;
	}
	private Map<String,OrderByType> orderBys = new HashMap<String, OrderByType>();
	//分组值设置
	private Set<String> groupBys = new HashSet<String>();
	//用于接收SQL语句
    private StringBuilder sql = new StringBuilder();
    //用于接收参数数组
    private List<Object> paramList = new ArrayList<Object>();

	Cnd addQuery(String key, Param value){
    	querys.put(key, value);
    	return this;
	}

	Cnd addColumn(String key, Class<?> classType){
		columnMap.put(key, classType);
		return this;
	}

	Cnd addFuzzyQueryColumn(String column){
		fuzzyQueryColumnSet.add(column);
		return this;
	}

	Cnd addOrderByColumn(String column){
		orderByColumnSet.add(column);
		return this;
	}
    
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
    
	public static Cnd toCnd(Object ...modelAndAlias) {
		Cnd cnd = new Cnd();
		CndBuilder.init(cnd, modelAndAlias);
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
	
	public static Cnd queryToCnd(Map<String, String[]> paras, Object ...modelClassAndAlias){
		if (paras == null) {
			throw new IllegalArgumentException("paras参数不能为空");
		}
		Cnd cnd = new Cnd();
		CndBuilder.init(cnd, paras, modelClassAndAlias);

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

    public Cnd setRemove(String ... keys){
    	for(String key : keys){
    		removes.add(key);
		}
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
    				if(fuzzyQueryColumnSet.contains(column)){
    					fuzzyQuerys.put(column, queryStr);
    				}
    			}
    		} else{
    			for(String column: fuzzyQueryColumnSet){
    				fuzzyQuerys.put(column, queryStr);
    			}
    		}
    	}
    	return this;
    }
    
	/**
	 * 设置OrderBy属性(会清空其他配置)
	 */
	private static List<String> reg = Arrays.asList(new String[]{"asc","desc"});
	public Cnd setOrderBy(String... orderByStrs){
		if(orderByStrs == null  || orderByStrs.length == 0){
			return this;
		}
		orderBys.clear();

		String column,orderType;
		for(String orderByStr : orderByStrs){
			if(StrKit.isBlank(orderByStr)){
				continue;
			}

			String[] strs = orderByStr.split("_");
			if(strs.length == 1){
				column = getAliasKey(strs[0]);

				if(orderByColumnSet.contains(column)){
					addOrderBy(column, null);
				}
			} else if(strs.length == 2){
				column = getAliasKey(strs[0]);
				orderType = strs[1].toLowerCase();

				if(orderByColumnSet.contains(column) && reg.contains(orderType)){
					addOrderBy(column, OrderByType.valueOf(orderType));
				}
			}
		}
		return this;
	}

	public Cnd addOrderBy(String column, OrderByType orderByType){
		orderBys.put(column, orderByType);
		return this;
	}
	
    /**
	 * 设置GroupBy属性
	 */
	public Cnd setGroupBy(String... groupByStrs){
		if(groupByStrs == null  || groupByStrs.length == 0){
			return this;
		}
		groupBys.clear();
		for(String groupByStr : groupByStrs){
			if(StrKit.isBlank(groupByStr)){
				continue;
			}
			groupBys.add(groupByStr);
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
			if(removes.contains(entry.getKey())){
				continue;
			}
			if(querys.containsKey(entry.getKey()) && entry.getValue().getValue()==null){
				value = querys.get(entry.getKey()).getValue();
				CndBuilder.buildSQL(sb, entry.getValue().getType(), entry.getValue().getKey(), value, paramArrayList);
				querys.remove(entry.getKey());
			} else {
				CndBuilder.buildSQL(sb, entry.getValue(), paramArrayList);
			}
		}
		//构建查询条件
		for(Entry<String, Param> entry : querys.entrySet()){
			if(removes.contains(entry.getKey())){
				continue;
			}
			CndBuilder.buildSQL(sb, entry.getValue().getType(), entry.getValue().getKey(), entry.getValue().getValue(), paramArrayList);
		}
		//构建全文搜索
		CndBuilder.bulidFuzzyQuery(fuzzyQuerys, sb, paramArrayList);
		//构建分组
		CndBuilder.buildGroupBy(groupBys, sb);
		//构建排序
		CndBuilder.buildOrderBy(orderBys, sb);
		
		//设置where关键字，解决1=1效率的问题
		if(hasWhere && !StrKit.isBlank(sb.toString())){
			sql.append(sb.toString().replaceFirst("and", "where"));
		} else {
			sql.append(sb.toString());
		}
		paramList.addAll(paramArrayList);
    	return this;
    }

	/**
	 * 根据属性名获取 获取带别名属性名
	 */
	private String getAliasKey(String attr){
		String key = null;
		
		if(columnMap.containsKey(attr)){
			return attr;
		}
		if(attr.indexOf(".") != -1){
			throw new IllegalArgumentException(String.format("属性:%s ,不存在", attr));
		}
		
		int count = 0;
		for(Entry<String, Class<?>> entry : columnMap.entrySet()){
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
