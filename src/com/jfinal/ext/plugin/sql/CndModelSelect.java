package com.jfinal.ext.plugin.sql;

import com.jfinal.plugin.activerecord.Model;

import java.util.*;

@SuppressWarnings("unchecked")
class CndModelSelect<M extends CndModelSelect<M>> extends CndSelect<M> {
	//全部字段
	Map<String, Model<?>> aliasCndMap = new HashMap<String, Model<?>>();
	//默认值设置
	Map<String, CndParam> defaults = new HashMap<String, CndParam>();
	//排除值设置
	Set<String>  disables = new HashSet<String>();

	//获取默认值
	CndParam getDefault(String key) {
		return defaults.get(key);
	}
	//是否被禁止
	boolean isDisable(String key) {
		return disables.contains(key);
	}

	public M toCnd(Model<?> m, String alias){
		if(aliasCndMap.containsKey(alias)){
			throw new IllegalArgumentException(String.format("别名:%s已存在"));
		}
		aliasCndMap.put(alias, m);
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

	public M setDisable(String ... keys){
		for(String key : keys){
			disables.add(key);
		}
		return (M)this;
	}

	public M build(){
		ArrayList<Object> paramArrayList = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();

		CndWhere where = getWhere();

		for(Map.Entry<String,Model<?>> entry : aliasCndMap.entrySet()){
			build$Model(entry.getKey(), entry.getValue());
		}

		CndBuilder.build$CndWhere(where, sb, paramArrayList);
		CndBuilder.build$Symbol(where, sb);

		//构建分组
		CndBuilder.build$GroupBy(sb, getGroupBys());
		//构建排序
		CndBuilder.build$OrderBy(sb, getOrderBys());
		//构建limit
		CndBuilder.build$Limit(sb, getOffset(), getLimit());

		sql.append(sb.toString());
		paramList.addAll(paramArrayList);
		return (M)this;
	}

	void build$Model(String alias, Model<?> model){
		if(model == null){
			return;
		}
		alias = CndBuilder.getAlias(alias, model.getClass());
		for(Map.Entry<String, Class<?>> entry : model.getColumns().entrySet()){
			String newKey = alias + entry.getKey();
			Object value = model.get(entry.getKey());

			build$Param(this, newKey, value);
		}
	}

	static void build$Param(CndModelSelect<?> cnd, String key, Object value){
		//禁止值或者为空
		if(cnd.isDisable(key) || value == null){
			return;
		}
		//默认值
		CndParam param = cnd.getDefault(key);
		if(param != null){
			if(param.getValue() == null && value != null){
				param.setValue(value);
			}
		} else {
			param = CndParam.create(key, value);
		}
		if(param.getValue() != null){
			cnd.getWhere().and(param);
		}
	}
}
