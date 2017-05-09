package com.jfinal.ext.plugin.sql;

import com.jfinal.kit.StrKit;

import java.util.*;

@SuppressWarnings("unchecked")
class CndSelect<M extends CndSelect<M>> extends CndBaseSelect<M> {
	private Map<String,Cnd.OrderByType> orderBys = new HashMap<String, Cnd.OrderByType>();
	//分组值设置
	private Set<String> groupBys = new HashSet<String>();

	private Integer offset;
	private Integer limit;

	public Map<String, OrderByType> getOrderBys() {
		return orderBys;
	}

	public Set<String> getGroupBys() {
		return groupBys;
	}

	public Integer getOffset() {
		return offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public M orderBy(String column){
		return orderBy(column, OrderByType.asc);
	}

	public M orderBy(String column, Cnd.OrderByType orderByType){
		orderBys.put(column, orderByType);
		return (M)this;
	}

	/**
	 * 设置GroupBy属性
	 */
	public M groupBy(String groupBy){
		if(StrKit.notBlank(groupBy)){
			groupBys.add(groupBy);
		}
		return (M)this;
	}

	public M limit(int limit){
		this.limit = limit;
		return (M)this;
	}
	public M limit(int offset, int limit){
		this.offset = offset;
		return limit(limit);
	}

	@Override
	public M build() {
		ArrayList<Object> paramArrayList = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder();

		CndWhere where = getWhere();

		//构建where条件
		CndBuilder.build$CndWhere(where, sb, paramArrayList);
		CndBuilder.build$Symbol(where, sb);
		//构建分组
		CndBuilder.build$GroupBy(sb, groupBys);
		//构建排序
		CndBuilder.build$OrderBy(sb, orderBys);
		//构建limit
		CndBuilder.build$Limit(sb, offset, limit);

		sql.append(sb.toString());
		paramList.addAll(paramArrayList);
		return (M)this;
	}

	public static void main(String[] args) {
		Cnd cnd = new CndSelect<>()
				.select(Cnd.$SELECT_)
				.from("from talble")
				.where()
				.and("ccc", "1,2,3")
				.or("a", 1)
				.orGroup(
						Cnd.$group().create("a", "1").and("b", ">1")
				)
				.limit(10, 100)
				.build();
		System.out.println(cnd.getSql());
		System.out.println(cnd.getParas());
	}
}
