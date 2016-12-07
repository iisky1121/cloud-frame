package com.jfinal.ext.plugin.sqlinmd;

import com.jfinal.plugin.IPlugin;

public class SqlInMdPlugin implements IPlugin {
	String fileAbsolutePath;
	boolean scanSubFolder = false;
	
	public SqlInMdPlugin(String fileAbsolutePath){
		this.fileAbsolutePath = fileAbsolutePath;
	}
	
	public SqlInMdPlugin(String fileAbsolutePath, boolean scanSubFolder){
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