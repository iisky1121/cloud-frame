package com.jfinal.ext.plugin.sql;

import com.jfinal.ext.kit.ModelKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("unchecked")
class CndQuery<M extends CndQuery<M>> extends CndSelect<M> {
	private Map<String,Object> object = new HashMap<String,Object>();
	private String orderByValue;
	private String fuzzyQueryValue;
	//全部字段
	private Map<String, Class<?>> aliasCndMap = new HashMap<String, Class<?>>();
	//默认值设置
	private Map<String, CndParam> defaults = new HashMap<String, CndParam>();
	//排除值设置
	private Set<String> disables = new HashSet<String>();

	//获取默认值
	CndParam getDefault(String key) {
		return defaults.get(key);
	}
	//是否被禁止
	boolean isDisable(String key) {
		return disables.contains(key);
	}

	public M setFuzzyQuery(String value){
		fuzzyQueryValue = value;
		return (M)this;
	}

	//全部字段
	Map<String, Class<?>> all_cloumns = new HashMap<String, Class<?>>();
	//全文搜索值设置
	Set<String> fuzzy_cloumns;

	public M setMap(Map<String,Object> paras){
		if(paras.get("fuzzyQuery")!=null){
			setFuzzyQuery(paras.get("fuzzyQuery").toString());
			paras.remove("fuzzyQuery");
		}

		if(paras.get("orderBy")!=null){
			orderByValue = paras.get("orderBy").toString();
			paras.remove("orderBy");
		}
		
		object = paras;
		return (M)this;
	}
	public M setParaMap(Map<String, String[]> params){
		Map<String, Object> map = new HashMap<String,Object>();
		String[] value;
		for(Map.Entry<String,String[]> entry : params.entrySet()){
			value = entry.getValue();
			if(value != null){
				map.put(entry.getKey(), value[0]);
			}
		}
		return setMap(map);
	}

	Object getObject(String key){
		return object.get(key);
	}

	public M setCnd(Class<?> clazz, String alias){
		aliasCndMap.put(alias, clazz);
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

    /**
	 * 组建各种参数值
	 */
    public M build(){
		ArrayList<Object> paramArrayList = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();

		CndWhere where = getWhere();
		build$Class();

		CndBuilder.build$CndWhere(where, sb, paramArrayList);
		CndBuilder.build$Symbol(where, sb);

		//默认的全文查询
		if(fuzzy_cloumns == null && StrKit.notBlank(fuzzyQueryValue)){
			fuzzy_cloumns = new HashSet<String>();
			for(Map.Entry<String,Class<?>> entry: all_cloumns.entrySet()){
				if(entry.getValue() == String.class){
					fuzzy_cloumns.add(entry.getKey());
				}
			}
		}
		//构建全文搜索
		CndBuilder.bulid$FuzzyQuery(sb, paramArrayList, fuzzy_cloumns, fuzzyQueryValue);
		//构建分组
		CndBuilder.build$GroupBy(sb, getGroupBys());
		//构建排序
		if(getOrderBys().size() == 0){
			//只有在后台没有设置orderBy的时候，前端的参数才会生效
			set$OrderBy();
		}
		CndBuilder.build$OrderBy(sb, getOrderBys());
		//构建limit
		CndBuilder.build$Limit(sb, getOffset(), getLimit());

		sql.append(sb.toString());
		paramList.addAll(paramArrayList);
    	return (M)this;
    }

    void build$Class(){
    	String alias;
    	Class<?> clazz;
    	for(Map.Entry<String,Class<?>> entry : aliasCndMap.entrySet()){
    		clazz = entry.getValue();
    		alias = CndBuilder.getAlias(entry.getKey(), clazz);

    		if(Model.class.isAssignableFrom(clazz)) {
				build$ModelClass(this, alias, (Class<? extends Model<?>>) clazz);
			} else if(IBean.class.isAssignableFrom(clazz)){
				build$IBeanClass(this, alias, (Class<? extends IBean>) clazz);
			}
		}
	}

	static void build$ModelClass(CndQuery<?> cnd, String alias, Class<? extends Model<?>> clazz){
		if(cnd == null || clazz == null || alias == null){
			return;
		}
		Model<?> model = ModelKit.newInstance(clazz);
		for(Map.Entry<String, Class<?>> column : model.getColumns().entrySet()){
			String newKey = alias + column.getKey();
			Object value = cnd.getObject(newKey);

			cnd.all_cloumns.put(newKey, column.getValue());

			build$Param(cnd, newKey, value);
		}
	}

	static void build$IBeanClass(CndQuery<?> cnd, String alias, Class<? extends IBean> clazz){
		if(cnd == null || clazz == null || alias == null){
			return;
		}
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields){
			String newKey = alias + field.getName();
			Object value = cnd.getObject(newKey);

			cnd.all_cloumns.put(newKey, field.getType());

			build$Param(cnd, newKey, value);
		}
	}

	static void build$Param(CndQuery<?> cnd, String key, Object value){
		//禁止值或者为空
		if(StrKit.isBlank(key) || cnd.isDisable(key)){
			return;
		}
		//默认值
		CndParam param = cnd.getDefault(key);
		if(param != null){
			if(param.getValue() == null && value != null){
				param.setValue(value);
			}
			cnd.getWhere().and(param);
		} else if(value != null) {
			cnd.getWhere().and(CndParam.create(key, value, cnd.all_cloumns.get(key)));
		}
	}

	/**
	 * 只能设置一遍
	 * @return
	 */
	private void set$OrderBy(){
		if(StrKit.notBlank(orderByValue)){
			//先清除orderBy
			getOrderBys().clear();
			String[] orderBys = orderByValue.split(",");
			for(String orderBy : orderBys){
				if(!StrKit.isBlank(orderBy)){
					String[] values = orderBy.split("_");
					//前端传入的参数必须是一些存在的字段，而不是一些自定义的属性
//					if(!all_cloumns.containsKey(values[0])){
//						continue;
//					}
//					if(values.length == 1){
//						orderBy(values[0]);
//					} else if(values.length == 2){
//						orderBy(values[0], Cnd.OrderByType.valueOf(values[1]));
//					}
					if(all_cloumns.containsKey(values[0]) || isSpecialOrderBy(values[0])){
						if(values.length == 1){
							orderBy(values[0]);
						} else if(values.length == 2){
							orderBy(values[0], Cnd.OrderByType.valueOf(values[1]));
						}
					}
				}
			}
		}
	}
	
	/**
	 * 是否特殊的orderBy，一般指含有函数的orderBy
	 * @param value orderBy的值
	 */
	private boolean isSpecialOrderBy(String value){
		return isFunctionOrderBy("length", value) || isFunctionOrderBy("count", value) || isFunctionOrderBy("sum", value);
	}
	
	/**
	 * 是否含有函数的orderBy
	 * @param function 函数名
	 * @param value orderBy的值
	 */
	private boolean isFunctionOrderBy(String function, String value){
		String valueStart = value.substring(0, function.length() + 1);
		String valueCloumn = value.substring(function.length() + 1, value.length() - 1);
		String valueEnd = value.substring(value.length() - 1);
		return (function + "(").equals(valueStart.toLowerCase()) && all_cloumns.containsKey(valueCloumn) && ")".equals(valueEnd);
	}
}
