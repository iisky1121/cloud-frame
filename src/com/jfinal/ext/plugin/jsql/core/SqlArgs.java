package com.jfinal.ext.plugin.jsql.core;

import java.util.List;

/**
 * SQL与参数
 * @author farmer
 */
public class SqlArgs {
	
	/**
	 * SQL语句
	 */
	private String sql;
	/**
	 * ARGS
	 */
	private List<Object> args;
	
	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<Object> getArgs() {
		return args;
	}

	public void setArgs(List<Object> args) {
		this.args = args;
	}	
	
}
