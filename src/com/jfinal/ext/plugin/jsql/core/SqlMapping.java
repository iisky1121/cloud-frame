package com.jfinal.ext.plugin.jsql.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.ext.plugin.jsql.xml.JSql;
import com.jfinal.ext.plugin.jsql.xml.JSqlConfig;
import com.jfinal.ext.plugin.jsql.xml.JSqlPath;
import com.jfinal.ext.plugin.jsql.xml.Sql;
import com.jfinal.ext.plugin.jsql.xml.util.JsqlXmlUtil;


/**
 * SQL语句与ID映射关系
 * @author farmer
 *
 */
public class SqlMapping {
	
	private static Map<String, String> sqlMap = new HashMap<String, String>();

	
	private static String configPath = null;
	
	/**
	 * 初始化SqlMapping
	 * @param path
	 */
	public static void init(String configPath){
		SqlMapping.configPath = configPath;
		sqlMap.clear();
		InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configPath);
		JSqlConfig jSqlConfig = JsqlXmlUtil.toJSqlConfig(inputStream);
		List<JSql> jSqls = new ArrayList<JSql>();
		for (JSqlPath jSqlPath : jSqlConfig.getJsqlpaths()) {
			jSqls.add(JsqlXmlUtil.toJSql(SqlMapping.class.getResourceAsStream(jSqlPath.getPath())));
		}
		for (JSql jSql : jSqls) {
			String namespace = jSql.getNamespace();
			List<Sql> sqls = jSql.getSqls();
			for (Sql sql : sqls) {
				SqlMapping.put(namespace+"."+sql.getId(), sql.getSql());
			}
		}
	}
	
	/**
	 * PUT
	 * @param key
	 * @param value
	 */
	public static void put(String key , String value){
		if(null !=sqlMap.get(key)){
			throw new RuntimeException("SQL语句的ID:"+key+"已经存在!");
		}
		sqlMap.put(key, value);
	}
	
	
	/**
	 * 获取SQL
	 * @param key
	 * @return
	 */
	public static String get(String key){
		String value = null;
		if(DevMode.isDevMode()){
			synchronized (SqlMapping.class) {
				init(configPath);
				value = sqlMap.get(key);
			}
		}
		else{
			value = sqlMap.get(key);
		}
		if(null == value){
			throw new RuntimeException("SQL语句的ID:"+key+"不存在!");
		}
		return value;
	}
}
