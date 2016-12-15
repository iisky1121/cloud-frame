package com.jfinal.ext.plugin.jsql;

import com.jfinal.ext.plugin.jsql.core.SqlArgs;
import com.jfinal.ext.plugin.jsql.core.SqlMapping;
import com.jfinal.ext.plugin.jsql.core.Template;

/**
 * JsqlKit
 * @author farmer
 *
 */
public class JSqlKit {
	
	
	public static SqlArgs getSqlArgs(String sqlId){
		return getSqlArgs(sqlId, new Object());
	}
	
	/**
	 * 
	 * @param sqlId
	 * @param args
	 * @return
	 */
	public static SqlArgs getSqlArgs(String sqlId,Object args){
		try {
			return Template.getTemplate().render(SqlMapping.get(sqlId), args);
		} catch (Exception e) {
			throw new RuntimeException("SQL语句ID:"+sqlId+"异常!",e);
		}
	}
	
}
