package com.jfinal.ext.plugin.tablemapping;

import com.jfinal.core.JFinal;
import com.jfinal.kit.StrKit;

public class TableNameKit {
	/**
	 * 表名转controllerKey
	 * 
	 * @return String
	 */
	public static String tableNametoControllerKey(String tableName){
		if(StrKit.isBlank(tableName)){
			return null;
		}
		return tableName.replaceAll("_", "/").toLowerCase();
	}

	/**
	 * requestURI转表名
	 * 
	 * @return String
	 */
	public static String requestURItoTableName(String requestURI){ 
		if(StrKit.isBlank(requestURI)){
			return null;
		}
		String controllerKey = requestURI.replaceFirst(JFinal.me().getContextPath(), "");
		return controllerKeytoTableName(controllerKey);
	}
	
	/**
	 * controllerKey转表名
	 * 
	 * @return String
	 */
	public static String controllerKeytoTableName(String controllerKey){ 
		return controllerKeytoTableName(controllerKey,0);
	}
	private static String controllerKeytoTableName(String controllerKey,int dep){ 
		if(StrKit.isBlank(controllerKey)){
			return null;
		}
		String tableName = AutoTableMapping.getTableName(controllerKey);
		if(StrKit.isBlank(tableName)){
			controllerKey = controllerKey.substring(0, controllerKey.lastIndexOf("/"));
			if(dep==1){
				return null;
			}
			return controllerKeytoTableName(controllerKey, dep++);
		}
		return tableName;
	}
}
