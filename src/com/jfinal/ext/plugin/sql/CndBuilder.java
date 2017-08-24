package com.jfinal.ext.plugin.sql;

import com.jfinal.kit.StrKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class CndBuilder {
	public static Object[] buildValue(Cnd.Type queryType, Object fieldValue) {
		// 非空的时候进行设置
		if (StrKit.notNull(fieldValue)) {
			if (Cnd.Type.equal.equals(queryType)) {
				return new Object[]{" = ? ", fieldValue};
			}
			else if (Cnd.Type.not_equal.equals(queryType)) {
				return new Object[]{" <> ? ", fieldValue};
			}
			else if (Cnd.Type.less_then.equals(queryType)) {
				return new Object[]{" < ? ", fieldValue};
			}
			else if (Cnd.Type.less_equal.equals(queryType)) {
				return new Object[]{" <= ? ", fieldValue};
			}
			else if (Cnd.Type.greater_then.equals(queryType)) {
				return new Object[]{" > ? ", fieldValue};
			}
			else if (Cnd.Type.greater_equal.equals(queryType)) {
				return new Object[]{" >= ? ", fieldValue};
			}
			else if (Cnd.Type.fuzzy.equals(queryType)) {
				return new Object[]{" like ? ", "%" + fieldValue + "%"};
			}
			else if (Cnd.Type.fuzzy_left.equals(queryType)) {
				return new Object[]{" like ? ", "%" + fieldValue};
			}
			else if (Cnd.Type.fuzzy_right.equals(queryType)) {
				return new Object[]{" like ? ", fieldValue + "%"};
			}
			else if (Cnd.Type.in.equals(queryType)) {
				Object[] values = CndKit.toValues(queryType, fieldValue);
				if(values == null){
					throw new IllegalArgumentException("使用IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
				}
				StringBuilder instr = new StringBuilder();
				for (int i =0; i< values.length; i++) {
					instr.append(StrKit.notBlank(instr.toString()) ? ",?" : "?");
				}
				return new Object[]{" in (" + instr + ") ", values};
			}
			else if (Cnd.Type.not_in.equals(queryType)) {
				Object[] values = CndKit.toValues(queryType, fieldValue);
				if(values == null){
					throw new IllegalArgumentException("使用Not IN条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔)");
				}
				StringBuilder instr = new StringBuilder();
				for (int i =0; i< values.length; i++) {
					instr.append(StrKit.notBlank(instr.toString()) ? ",?" : "?");
				}
				return new Object[]{" not in (" + instr + ") ", values};
			}
			else if (Cnd.Type.between_and.equals(queryType)) {
				Object[] values = CndKit.toValues(queryType, fieldValue);
				if(values == null){
					throw new IllegalArgumentException("使用BETWEEN And条件的时候传入的值必须是个Collection对象或者Object[]对象或者String对象(多个以,分隔),且长度为2");
				}

				if (values.length != 2) {
					throw new IllegalArgumentException(String.format("Illegal between params size:%s", values.length));
				}
				return new Object[]{" between ? and ? ", values};
			}
		}
		else {
			if (Cnd.Type.empty.equals(queryType)) {
				return new Object[]{" is null ", null};
			}
			else if (Cnd.Type.not_empty.equals(queryType)) {
				return new Object[]{" is not null ", null};
			}
		}
		return null;
	}

    static void buildSql(StringBuilder sb, CndParam param, List<Object> params){
		Object[] values = buildValue(param.getType(), param.getValue());
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

	static String getAlias(String alias, Class<?> clazz){
		return (alias==null?StrKit.firstCharToLowerCase(clazz.getSimpleName()):("".equals(alias.trim())?"":(alias+".")));
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
	static void bulid$FuzzyQuery(StringBuilder sb, List<Object> paramList, Set<String> fuzzy_cloumns, String fuzzyQueryValue){
		if(fuzzy_cloumns != null && fuzzy_cloumns.size() > 0 && StrKit.notBlank(fuzzyQueryValue)){
			int i = 0;
			if(sb.length() > 0){
				sb.append(" and (");
			} else {
				sb.append(" where (");
			}
			for(String column : fuzzy_cloumns){
				if (i > 0) {
					sb.append(" or ");
		    	}
				sb.append(column + " like ?");
		    	paramList.add("%" + fuzzyQueryValue + "%");
		    	i++;
			}
			sb.append(")");
		    i++;
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
				params.add(incrBy.getVal());
			} else {
				sb.append(String.format("%s = ?", entry.getKey()));
				params.add(entry.getValue());
			}
			if(i<setSize-1){
				sb.append(",");
			}
			i++;
		}
	}

	static void build$Symbol(CndWhere where, StringBuilder sb){
		//设置where关键字，解决1=1效率的问题
		if(!StrKit.isBlank(sb.toString())){
			if(where.hasWhere()){
				sb.insert(0, String.format(Cnd.$BLANK_FNT, Cnd.$WHERE));
			} else {
				sb.insert(0, String.format(Cnd.$BLANK_FNT, Cnd.Symbol.and.name()));
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
