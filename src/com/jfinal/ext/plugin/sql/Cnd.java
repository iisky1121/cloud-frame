package com.jfinal.ext.plugin.sql;

import java.util.ArrayList;
import java.util.List;

public abstract class Cnd {
	public final static String $SELECT_ = "select * ";
	public final static String $SELECT_FROM_TABLE = "select * from `%s`";
	public final static String $_FROM_TABLE = " from %s";
	public final static String $DELETE_FROM_TABLE = "delete from `%s`";
	public final static String $UPDATE_TABLE = "update `%s`";
	public final static String $IS_EMPTY = "$isEmpty";
	public final static String $IS_NOT_EMPTY = "$isNotEmpty";
	public final static String $IS_NULL = "$isNull";
	public final static String $IS_NOT_NULL = "$isNotNull";
	public final static String $BLANK_FNT = " %s ";
	public final static String $WHERE = "where";
	public enum Symbol{
		and,or
	}
	public enum OrderByType{
		asc,desc;
	}
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

	//用于接收SQL语句
	StringBuilder sql = new StringBuilder();
	//用于接收参数数组
	List<Object> paramList = new ArrayList<Object>();

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

	public static class Update extends CndUpdate<Update>{}
	public static Update $update(){
		return new Update();
	}

	public static class Delete extends CndDelete<Delete>{}
	public static Delete $delete(){
		return new Delete();
	}

	public static class Query extends CndQuery<Query> {}
	public static Query $query(){
		return new Query();
	}

	public static class Select extends CndSelect<Select>{}
	public static Select $select(){
		return new Select();
	}

	public static class ModelSelect extends CndModelSelect<ModelSelect>{}
	public static ModelSelect $modelselect(){
		return new ModelSelect();
	}

	public static class Template extends CndTemplate<Template>{}
	public static Template $template(){
		return new Template();
	}

	public static CndGroup $group(){
		return new CndGroup();
	}
}
