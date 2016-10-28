package com.jfinal.ext.plugin.sqlintxt;

import com.jfinal.plugin.activerecord.Sqls;

public class SqlKit {
	private StringBuilder sqls = new StringBuilder();
	
	public SqlKit(){}
	
	private static SqlKit get(){
		return new SqlKit();
	}
	
	private SqlKit connect(String sql){
		sqls.append(sql);
		return this;
	}
	
	public String sql(){
		return sqls.toString();
	}
	
	public static SqlKit get(String sqlKey){
		return get().connect(Sqls.get(sqlKey));
	}

	public SqlKit where(String sqlKey){
		return get().connect(Sqls.get(sqlKey));
	}
	
	public static void main(String[] args) {
		SqlKit.get("sqlKey").where("sqlKey").sql();
	}
}
