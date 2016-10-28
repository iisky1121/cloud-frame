package com.jfinal.ext.plugin.tablemapping;

import java.util.Collections;
import java.util.Map;
import com.jfinal.kit.StrKit;

/**
 * Table save the table meta info like column name and column type.
 */
public class Table {
	
	private String name;
	private String[] primaryKey = null;
	private Map<String, Class<?>> columnTypeMap;	// config.containerFactory.getAttrsMap();
	
	
	public Table(String name) {
		if (StrKit.isBlank(name))
			throw new IllegalArgumentException("Table name can not be blank.");
		
		this.name = name.trim();
	}
	
	
	public Table(String name, String primaryKey) {
		if (StrKit.isBlank(name))
			throw new IllegalArgumentException("Table name can not be blank.");
		if (StrKit.isBlank(primaryKey))
			throw new IllegalArgumentException("Primary key can not be blank.");
		
		this.name = name.trim();
		setPrimaryKey(primaryKey.trim());
	}
	
	void setPrimaryKey(String primaryKey) {
		String[] arr = primaryKey.split(",");
		for (int i=0; i<arr.length; i++)
			arr[i] = arr[i].trim();
		this.primaryKey = arr;
	}
	
	void setColumnTypeMap(Map<String, Class<?>> columnTypeMap) {
		if (columnTypeMap == null)
			throw new IllegalArgumentException("columnTypeMap can not be null");
		
		this.columnTypeMap = columnTypeMap;
	}
	
	public String getName() {
		return name;
	}
	
	void setColumnType(String columnLabel, Class<?> columnType) {
		columnTypeMap.put(columnLabel, columnType);
	}
	
	public Class<?> getColumnType(String columnLabel) {
		return columnTypeMap.get(columnLabel);
	}
	
	/**
	 * Model.save() need know what columns belongs to himself that he can saving to db.
	 * Think about auto saving the related table's column in the future.
	 */
	public boolean hasColumnLabel(String columnLabel) {
		return columnTypeMap.containsKey(columnLabel);
	}
	
	/**
	 * update() and delete() need this method.
	 */
	public String[] getPrimaryKey() {
		return primaryKey;
	}
	
	
	public Map<String, Class<?>> getColumnTypeMap() {
		return Collections.unmodifiableMap(columnTypeMap);
	}
}