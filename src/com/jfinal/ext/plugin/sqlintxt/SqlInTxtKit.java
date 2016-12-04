package com.jfinal.ext.plugin.sqlintxt;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Sqls;

public class SqlInTxtKit {
	private StringBuilder sqls = new StringBuilder();
	
	public SqlInTxtKit(){}
	
	private static SqlInTxtKit get(){
		return new SqlInTxtKit();
	}
	
	public static SqlInTxtKit get(String sqlKey){
		return get().connect(Sqls.get(sqlKey));
	}
	
	public static SqlInTxtKit get(String sqlFileName, String sqlKey){
		return get().connect(Sqls.get(sqlFileName, sqlKey));
	}
	
	private SqlInTxtKit connect(String sql){
		sqls.append(sql);
		return this;
	}
	
	public String sql(){
		return sqls.toString();
	}
	
	public SqlInTxtKit where(String whereSql){
		if(StrKit.isBlank(whereSql)){
			return this;
		}
		
		String sql = sqls.toString();
		if(!StrKit.isBlank(sql)){
			if(sql.indexOf("#where#") != -1){
				sqls.setLength(0);
				sqls.append(sql.replace("#where#", whereSql));
			}
			else{
				sqls.append(" ").append(whereSql);
			}
		}
		return this;
	}
}
