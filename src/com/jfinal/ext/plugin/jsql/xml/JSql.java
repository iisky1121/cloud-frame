package com.jfinal.ext.plugin.jsql.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("jsql")
public class JSql {
	
	@XStreamAsAttribute
	private String namespace;
	
	@XStreamImplicit(itemFieldName="sql")
	private List<Sql> sqls;
	
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public List<Sql> getSqls() {
		return sqls;
	}

	public void setSqls(List<Sql> sqls) {
		this.sqls = sqls;
	}
	
}
