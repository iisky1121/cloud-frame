package com.jfinal.ext.plugin.jsql.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("jsqlconfig")
public class JSqlConfig {
	
	@XStreamImplicit(itemFieldName="jsqlpath")
	private List<JSqlPath> jsqlpaths;

	public List<JSqlPath> getJsqlpaths() {
		return jsqlpaths;
	}

	public void setJsqlpaths(List<JSqlPath> jsqlpaths) {
		this.jsqlpaths = jsqlpaths;
	}
	
}