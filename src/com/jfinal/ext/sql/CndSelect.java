package com.jfinal.ext.sql;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;

import java.util.*;
import java.util.Map.Entry;

class CndSelect<M extends CndSelect> {
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

	M addQuery(String key, Param value){
    	querys.put(key, value);
    	return (M)this;
	}

	M addColumn(String key, Class<?> classType){
		columnMap.put(key, classType);
		return (M)this;
	}

	M addFuzzyQueryColumn(String column){
		fuzzyQueryColumnSet.add(column);
		return (M)this;
	}

	M addOrderByColumn(String column){
		orderByColumnSet.add(column);
		return (M)this;
	}
    
    public M toCnd(){
		return (M)this;
    }
    /**
	 * Model转换成Cnd
	 */
    @SuppressWarnings({ "rawtypes" })
    public M toCnd(Model m){
    	return toCnd(m, "");
    }
    
	public M toCnd(Object ...modelAndAlias) {
		CndBuilder.init(this, modelAndAlias);
		return (M)this;
	}
	
	public M queryToCnd(Map<String, String[]> paras, Class<?>... classes){
		List<Object> list = new ArrayList<Object>();
		for(Class<?> c : classes){
			list.add(c);
			list.add(StrKit.firstCharToLowerCase(c.getSimpleName()));
		}
		return queryToCnd(paras, list.toArray());
	}
	
	public M queryToCnd(Map<String, String[]> paras, Object ...modelClassAndAlias){
		if (paras == null) {
			throw new IllegalArgumentException("paras参数不能为空");
		}
		//CndSelect cnd = new CndSelect();
		CndBuilder.init(this, paras, modelClassAndAlias);

		if(paras.get("fuzzyQuery")!=null){
			this.setFuzzyQuery(paras.get("fuzzyQuery")[0]);
		}
		if(paras.get("orderBy")!=null){
			String orderBy = paras.get("orderBy")[0];
			if(!StrKit.isBlank(orderBy)){
				this.setOrderBy(orderBy.split(","));
			}
		}
		return (M)this;
	}
	
	/**
     * 是否拼接where
     */
    public M where(){
    	this.hasWhere = true;
    	return (M)this;
    }
    
    /**
     * 设置值
     */
    public M set(String key, Class<?> classType, Object val){
    	querys.put(key, Param.create(key, val, classType));
    	return (M)this;
    }
    
    /**
     * 设置值
     */
    public M set(String key, Object val){
    	querys.put(key, Param.create(key, val));
    	return (M)this;
    }
    
    /**
     * 设置默认值
     */
    public M setDefault(String key, Cnd.Type type){
    	return setDefault(key, type, null);
    }
    
    /**
     * 设置默认值
     */
    public M setDefault(String key, Cnd.Type type, Object value){
    	defaults.put(key, new Param(key, type, value));
    	return (M)this;
    }

    public M setRemove(String ... keys){
    	for(String key : keys){
    		removes.add(key);
		}
    	return (M)this;
	}
    
    /**
     * 设置fuzzyQuery属性
     */
	public M setFuzzyQuery(String queryStr, String... columns){
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
    	return (M)this;
    }
    
	/**
	 * 设置OrderBy属性(会清空其他配置)
	 */
	private static List<String> reg = Arrays.asList(new String[]{"asc","desc"});
	public M setOrderBy(String... orderByStrs){
		if(orderByStrs == null  || orderByStrs.length == 0){
			return (M)this;
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
		return (M)this;
	}

	public M addOrderBy(String column, OrderByType orderByType){
		orderBys.put(column, orderByType);
		return (M)this;
	}
	
    /**
	 * 设置GroupBy属性
	 */
	public M setGroupBy(String... groupByStrs){
		if(groupByStrs == null  || groupByStrs.length == 0){
			return (M)this;
		}
		groupBys.clear();
		for(String groupByStr : groupByStrs){
			if(StrKit.isBlank(groupByStr)){
				continue;
			}
			groupBys.add(groupByStr);
		}
		return (M)this;
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
    public M build(){
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
			sql.append(sb.toString().replaceFirst(Cnd.AND, Cnd.WHERE));
		} else {
			sql.append(sb.toString());
		}
		paramList.addAll(paramArrayList);
    	return (M)this;
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
