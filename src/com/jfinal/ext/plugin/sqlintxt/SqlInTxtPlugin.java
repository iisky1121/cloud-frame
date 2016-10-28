package com.jfinal.ext.plugin.sqlintxt;

import com.jfinal.plugin.IPlugin;

public class SqlInTxtPlugin implements IPlugin {
	String fileAbsolutePath;
	
	public SqlInTxtPlugin(String fileAbsolutePath){
		this.fileAbsolutePath = fileAbsolutePath;
	}
	
	public boolean start() {
		SqlBuilder.init(fileAbsolutePath);
		return true;
	}

	public boolean stop() {
		SqlBuilder.clearSqlMap();
		return true;
	}
}