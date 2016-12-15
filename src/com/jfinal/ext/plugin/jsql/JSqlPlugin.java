package com.jfinal.ext.plugin.jsql;

import com.jfinal.ext.plugin.jsql.core.SqlMapping;
import com.jfinal.plugin.IPlugin;

/**
 * Jsql插件
 * @author farmer
 *
 */
public class JSqlPlugin implements IPlugin{
	
	String configPath = null;
	
	
	/**
	 * 
	 * @param configPath
	 */
	public JSqlPlugin(String configPath) {
		this.configPath = configPath;
	}
	
	@Override
	public boolean start() {
		SqlMapping.init(configPath);
		return true;
	}

	@Override
	public boolean stop() {
		return true;
	}
	
}
