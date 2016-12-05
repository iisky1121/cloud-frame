package com.jfinal.ext.plugin.sqlinxml;

import com.jfinal.plugin.IPlugin;

public class SqlInXmlPlugin implements IPlugin {
	String fileAbsolutePath;
	boolean scanSubFolder = false;
	
	public SqlInXmlPlugin(String fileAbsolutePath){
		this.fileAbsolutePath = fileAbsolutePath;
	}
	
	public SqlInXmlPlugin(String fileAbsolutePath, boolean scanSubFolder){
		this.fileAbsolutePath = fileAbsolutePath;
	}
	
	public boolean start() {
		SqlBuilder.init(fileAbsolutePath, scanSubFolder);
		return true;
	}

	public boolean stop() {
		SqlBuilder.clearSqlMap();
		return true;
	}
}