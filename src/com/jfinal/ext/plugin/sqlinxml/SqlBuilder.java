package com.jfinal.ext.plugin.sqlinxml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jfinal.ext.kit.XmlHelper;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Sqls;

class SqlBuilder {
	private static Vector<File> vecFiles = new Vector<File>();
	private static Map<String, Map<String, String>> sqls = new HashMap<String, Map<String, String>>();

	static void clearSqlMap() {
		Sqls.clear();
	}

	static void init(String fileAbsolutePath) {
		getTxtFileName(PathKit.getRootClassPath()+fileAbsolutePath);
		loadXml();
	}

	static void getTxtFileName(String fileAbsolutePath) {
		File file = new File(fileAbsolutePath);
		File[] files = file.listFiles();
		if (files == null) {
			return;
		} else {
			for (File f : files) {
				if (f.isDirectory()) {
					getTxtFileName(f.getAbsolutePath());// 采用递归调用

				} else if (f.getName().endsWith("-sql.xml")) {
					vecFiles.add(f);
				}
			}
		}
	}
	
	static void loadXml(){
		for(File file : vecFiles){
			Node sqlsNode = XmlHelper.of(file).getDocument().getChildNodes().item(0);
			String namespace = XmlHelper.getNodeAttribute(sqlsNode, "namespace");
			if(!sqlsNode.getNodeName().equals("sqls") || StrKit.isBlank(namespace)){
				throw new IllegalArgumentException(String.format("模板文件错误：[%s] 第一级节点应该为[<sqls namespace=\"#namespace\">]", file.getName()));
			}
			if(sqls.containsKey(namespace)){
				throw new IllegalArgumentException(String.format("模板文件错误：[%s] namespace[%s] 已存在！", file.getName(), namespace));
			}
			Map<String,String> sqlsMap = new HashMap<String,String>();
			if(sqlsNode.hasChildNodes()){
				NodeList list = sqlsNode.getChildNodes();
				Node node;
				String sqlId;
				for (int i = 0; i < list.getLength(); i++) {
					node = list.item(i);
					if(node.getNodeName().equals("#text")){
						continue;
					}
					sqlId = XmlHelper.getNodeAttribute(node, "id");
					if(!node.getNodeName().equals("sql") || StrKit.isBlank(sqlId)){
						throw new IllegalArgumentException(String.format("模板文件错误：[%s] 第二级节点应该为[<sql id=\"#id\">]", file.getName()));
					}
					if(sqlsMap.containsKey(sqlId)){
						throw new IllegalArgumentException(String.format("模板文件错误：[%s] id[%s] 已存在！", file.getName(), sqlId));
					}
					
					sqlsMap.put(sqlId, node.getTextContent());
				}
				sqls.put(namespace, sqlsMap);
			}
		}
		System.out.println(sqls);
	}

	static String get(String sqlId){
		if(StrKit.isBlank(sqlId)){
			return null;
		}
		String[] values = sqlId.split(".");
		if(values.length != 2){
			return null;
		}
		return get(values[0], values[1]);
	}
	
	static String get(String namespace, String sqlId){
		if(StrKit.isBlank(namespace) || StrKit.isBlank(sqlId)){
			return null;
		}
		Map<String, String> map = sqls.get(namespace);
		if(map != null){
			return map.get(sqlId);
		}
		return null;
	}
}