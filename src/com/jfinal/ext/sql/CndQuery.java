package com.jfinal.ext.sql;

import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class CndQuery<M extends CndQuery> extends CndModelSelect<M> {
	//全文搜索值设置
	private Map<String, String> fuzzyQuerys = new HashMap<String, String>();

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
		CndBuilder.init(this, paras, modelClassAndAlias);

		if(paras.get("fuzzyQuery")!=null){
			this.setFuzzyQuery(paras.get("fuzzyQuery")[0]);
		}
		if(paras.get("orderBy")!=null){
			String orderBy = paras.get("orderBy")[0];
			if(!StrKit.isBlank(orderBy)){
				String[] orderBys = orderBy.split(",");
				for(String ob : orderBys){
					String[] c = ob.split("_");
					if(c.length == 1){
						orderBy(c[0]);
					} else if(c.length == 2){
						orderBy(c[0], Cnd.OrderByType.valueOf(c[1].toLowerCase()));
					}
				}
			}
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
    				if(getFuzzyQueryColumnSet().contains(column)){
    					fuzzyQuerys.put(column, queryStr);
    				}
    			}
    		} else{
    			for(String column: getFuzzyQueryColumnSet()){
    				fuzzyQuerys.put(column, queryStr);
    			}
    		}
    	}
    	return (M)this;
    }

    /**
	 * 组建各种参数值
	 */
    public M build(){
		ArrayList<Object> paramArrayList = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();

		CndWhere where = getWhere();

		CndBuilder.build$DRQ(where, getDefaults(), getRemoves(), getQuerys());

		CndBuilder.build$CndWhere(where, sb, paramArrayList);
		//构建全文搜索
		CndBuilder.bulid$FuzzyQuery(sb, paramArrayList, fuzzyQuerys);
		//构建分组
		CndBuilder.build$GroupBy(sb, getGroupBys());
		//构建排序
		CndBuilder.build$OrderBy(sb, getOrderBys());

		CndBuilder.build$Symbol(where, sb, sql);

		paramList.addAll(paramArrayList);
    	return (M)this;
    }

	/**
	 * 根据属性名获取 获取带别名属性名
	 */
	private String getAliasKey(String attr){
		String key = null;
		
		if(getColumnMap().containsKey(attr)){
			return attr;
		}
		if(attr.indexOf(".") != -1){
			throw new IllegalArgumentException(String.format("属性:%s ,不存在", attr));
		}
		
		int count = 0;
		for(Entry<String, Class<?>> entry : getColumnMap().entrySet()){
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
