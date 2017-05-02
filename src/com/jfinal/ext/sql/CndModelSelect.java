package com.jfinal.ext.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings("unchecked")
class CndModelSelect<M extends CndModelSelect<M>> extends CndSelect<M> {
	//字段存储
	private Map<String, Class<?>> columnMap = new HashMap<String, Class<?>>();
	private Set<String> fuzzyQueryColumnSet = new HashSet<String>();
	private Set<String> orderByColumnSet = new HashSet<String>();
	//默认值设置
	private Map<String, CndParam> defaults = new HashMap<String, CndParam>();
	//排除值设置
	private Set<String>  removes = new HashSet<String>();
	//需要进行查询的字段
	private Map<String, CndParam> querys = new HashMap<String, CndParam>();
	M addQuery(String key, CndParam value){
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

	public Map<String, Class<?>> getColumnMap() {
		return columnMap;
	}

	public Set<String> getFuzzyQueryColumnSet() {
		return fuzzyQueryColumnSet;
	}

	public Set<String> getOrderByColumnSet() {
		return orderByColumnSet;
	}

	public Map<String, CndParam> getQuerys() {
		return querys;
	}

	public Map<String, CndParam> getDefaults() {
		return defaults;
	}

	public Set<String> getRemoves() {
		return removes;
	}

	/**
	 * Model转换成Cnd
	 */
	@Deprecated
	public M toCnd(Model<?> m){
		return toCnd(m, "");
	}

	@Deprecated
	public M toCnd(Object ...modelAndAlias) {
		CndBuilder.init(this, modelAndAlias);
		return (M)this;
	}
	
	public M setCnd(Model<?> m){
		return setCnd(m, m.getAlias());
	}
	
	public M setCnd(Model<?> m, String alias){
		CndBuilder.init(this, m, alias);
		return (M)this;
	}
	
	public M setCnd(Class<?> clazz, String alias){
		CndBuilder.init(this, clazz, alias);
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
		defaults.put(key, new CndParam(key, type, value));
		return (M)this;
	}

	public M setRemove(String ... keys){
		for(String key : keys){
			removes.add(key);
		}
		return (M)this;
	}

	public M build(){
		ArrayList<Object> paramArrayList = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();

		CndWhere where = getWhere();

		CndBuilder.build$DRQ(where, defaults, removes, querys);

		CndBuilder.build$CndWhere(where, sb, paramArrayList);
		//构建分组
		CndBuilder.build$GroupBy(sb, getGroupBys());
		//构建排序
		CndBuilder.build$OrderBy(sb, getOrderBys());

		CndBuilder.build$Symbol(where, sb, sql);

		paramList.addAll(paramArrayList);
		return (M)this;
	}

	public static void main(String[] args) {
		Cnd cnd = Cnd.$modelselect()
				.toCnd(IBean.class, "test")
				.where()
				.and("ccc", "1,2,3")
				.or("a", 1)
				.limit(10, 100)
				.build();
		System.out.println(cnd.getSql());
		System.out.println(cnd.getParas());
	}
}
