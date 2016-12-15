package com.jfinal.ext.plugin.jsql.xml.util;

import java.io.InputStream;

import com.jfinal.ext.plugin.jsql.xml.JSql;
import com.jfinal.ext.plugin.jsql.xml.JSqlConfig;
import com.thoughtworks.xstream.XStream;

/**
 * XML工具
 * @author farmer
 *
 */
public class JsqlXmlUtil {
	
	/**
	 * XML To JSqlConfig Object
	 * @param in
	 * 	流
	 * @return
	 */
	public static JSqlConfig toJSqlConfig(InputStream in){
		XStream stream = new XStream();
		stream.processAnnotations(JSqlConfig.class);
		return (JSqlConfig) stream.fromXML(in);
	}
	
	/**
	 * XML To JSql Object
	 * @param in
	 * 	流
	 * @return
	 */
	public static JSql toJSql(InputStream in){
		XStream stream = new XStream();
		stream.processAnnotations(JSql.class);
		return (JSql) stream.fromXML(in);
	}
}
