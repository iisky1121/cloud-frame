package com.jfinal.ext.sql;

public class Cnd {
	public final static String $SELECT_ = "select * ";
	public final static String $SELECT_FROM = "select * from `%s`";
	public final static String $_FROM = " from %s";
	public final static String $DELETE_FROM = "delete from `%s`";
	public final static String $IS_EMPTY = "$isEmpty";
	public final static String $IS_NOT_EMPTY = "$isNotEmpty";
	public final static String $IS_NULL = "$isNull";
	public final static String $IS_NOT_NULL = "$isNotNull";
	public final static String $BLANK_FNT = " %s ";
	public final static String WHERE = "where";
	public final static String AND = "and";
	public final static String OR = "or";
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

	public static class Update extends CndUpdate<Update>{}
	public static Update update(){
		return new Update();
	}

	public static class Select extends CndSelect<Select> {}
	public static Select select(){
		return new Select();
	}
}
