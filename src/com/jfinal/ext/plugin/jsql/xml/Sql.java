package com.jfinal.ext.plugin.jsql.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("sql")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"sql"})
public class Sql {
	
	/**
	 * SQL语句的ID
	 */
	@XStreamAsAttribute
	private String id;
	/**
	 * SQL语句
	 */
	private String sql;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
