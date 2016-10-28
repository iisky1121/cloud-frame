package com.jfinal.ext.plugin.tablemapping;

import com.jfinal.aop.Before;
import com.jfinal.base.BaseController;
import com.jfinal.ext.interceptor.NotAction;
import com.jfinal.ext.sql.Cnd;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

@SuppressWarnings("rawtypes")
public class AutoMappingController extends BaseController {
	/**
	 * 获取Model的表名
	 * 
	 * @return String
	 */
	@Before(NotAction.class)
	public String getTableName() {
		return TableNameKit.requestURItoTableName(getRequest().getRequestURI());
	}
	
	@Before(NotAction.class)
	private Record getRecord(){
		return MapKit.toRecord(getTableName(), getParaMap());
	}

	@Before(NotAction.class)
	public Cnd getQuery() {
		return getQuery(getTableName(), getRecord());
	}
	/**
	 * 通用新增
	 * 
	 * @throws Exception
	 */
	public void save() {
		Record record = MapKit.toRecord(getTableName(), getParaMap());
		renderJson(Db.save(getTableName(), record));
	}

	/**
	 * 通用修改
	 * 
	 * @throws Exception
	 */
	public void update() {
		Record record = MapKit.toRecord(getTableName(), getParaMap());
		renderJson(Db.update(getTableName(), record));
	}
}
