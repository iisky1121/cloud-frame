package com.jfinal.ext.plugin.sqlinxml;

import com.jfinal.plugin.IPlugin;

public class SqlInXmlPlugin implements IPlugin {
	String fileAbsolutePath;
	
	public SqlInXmlPlugin(String fileAbsolutePath){
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