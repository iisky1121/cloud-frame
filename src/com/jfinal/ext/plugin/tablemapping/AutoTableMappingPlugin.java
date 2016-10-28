package com.jfinal.ext.plugin.tablemapping;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;

public class AutoTableMappingPlugin implements IPlugin {
	private static Log log = Log.getLog(AutoTableMappingPlugin.class);
	
	private String dbName;
	private IDataSourceProvider containerFactory;
	private List<String> excludeTables = new ArrayList<String>();
	private String excludePrefix = null;
	public AutoTableMappingPlugin(String dbName, IDataSourceProvider containerFactory){
		this.dbName = dbName;
		this.containerFactory = containerFactory;
	}

	public boolean start() {
		autoMapping(dbName, containerFactory);
		return true;
	}

	public boolean stop() {
		return true;
	}
	
	public void addExcludeTables(String tables) {
		excludeTables.add(tables);
	}
	
	public void setExcludePrefix(String prefix){
		this.excludePrefix = prefix.toLowerCase();
	}
	
	private void autoMapping(String dbName, IDataSourceProvider containerFactory){
		
		//查询所有表名 
		List<String> allTables = Db.query("select table_name from information_schema.tables where table_schema=?", dbName); 
	
		//获取已经映射的表名
		List<String> mappedTables = new ArrayList<String>();
		Map<Class<? extends Model<?>>, Table> modelToTableMap = TableMapping.me().getMappings();
		for(Entry<Class<? extends Model<?>>, Table> entry: modelToTableMap.entrySet()){
			mappedTables.add(entry.getValue().getName());
		}
		
		allTables.removeAll(mappedTables);
		allTables.removeAll(excludeTables);
		
		//自动映射数据库表
		boolean b = StrKit.isBlank(excludePrefix);
		String controllerKey = "";
		for(String tableName : allTables){
			if(b || !tableName.toLowerCase().startsWith(excludePrefix)){
				controllerKey = "/"+TableNameKit.tableNametoControllerKey(tableName);
				JFinalKit.getRoutes().add(controllerKey, AutoMappingController.class);
				
				build(containerFactory, controllerKey, tableName);
				
				log.debug("Auto addMapping(" + tableName + ", " + controllerKey + ")");
			}
		}
	}
	
	private void build(IDataSourceProvider containerFactory, String controllerKey, String tableName){
		com.jfinal.ext.plugin.tablemapping.Table table = new com.jfinal.ext.plugin.tablemapping.Table(tableName);
		try {
			AutoTableMapping.put(controllerKey, table);
			com.jfinal.ext.plugin.tablemapping.TableBuilder.doBuild(table, containerFactory.getDataSource().getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
