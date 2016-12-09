package com.jfinal.ext.plugin.sqlinxml;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Node;

import com.jfinal.ext.kit.XmlHelper;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Sqls;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

class SqlBuilder {
	private static Configuration cfg;
	private static Vector<File> vecFiles = new Vector<File>();
	private static Map<String, Map<String, String>> sqls = new HashMap<String, Map<String, String>>();

	static void clearSqlMap() {
		Sqls.clear();
	}

	static void init(String fileAbsolutePath, boolean scanSubFolder) {
		getXmlFileName(PathKit.getRootClassPath()+fileAbsolutePath, scanSubFolder);
		if(vecFiles.size() > 0){
			loadXml();
		}
	}

	static void getXmlFileName(String fileAbsolutePath, boolean scanSubFolder) {
		File file = new File(fileAbsolutePath);
		File[] files = file.listFiles();
		if (files == null) {
			return;
		} else {
			for (File f : files) {
				if (scanSubFolder && f.isDirectory()) {
					getXmlFileName(f.getAbsolutePath(), scanSubFolder);// 采用递归调用

				} else if (f.getName().endsWith("-sql.xml")) {
					vecFiles.add(f);
				}
			}
		}
	}
	
	static void loadXml(){
		for(File file : vecFiles){
			Node sqlsNode = XmlHelper.of(file).getDocument().getFirstChild();
			String namespace = XmlHelper.getNodeAttribute(sqlsNode, "namespace");
			if(!sqlsNode.getNodeName().equals("sqls") || StrKit.isBlank(namespace)){
				throw new IllegalArgumentException(String.format("模板文件错误：[%s] 第一级节点应该为[<sqls namespace=\"#namespace\">]", file.getName()));
			}
			if(sqls.containsKey(namespace)){
				throw new IllegalArgumentException(String.format("模板文件错误：[%s] namespace[%s] 已存在！", file.getName(), namespace));
			}
			//第二级节点 [sql]
			Map<String,String> sqlsMap = new HashMap<String,String>();
			if(sqlsNode.hasChildNodes()){
				List<Node> second_list = XmlHelper.getChildNodes(sqlsNode);
				for(Node second_node : second_list){
					String sqlId = XmlHelper.getNodeAttribute(second_node, "id");
					if(!second_node.getNodeName().equals("sql") || StrKit.isBlank(sqlId)){
						throw new IllegalArgumentException(String.format("模板文件错误：[%s] 第二级节点应该为[<sql id=\"#id\">]", file.getName()));
					}
					if(sqlsMap.containsKey(sqlId)){
						throw new IllegalArgumentException(String.format("模板文件错误：[%s] id[%s] 已存在！", file.getName(), sqlId));
					}
					sqlsMap.put(sqlId, second_node.getTextContent());
					
				}
				sqls.put(namespace, sqlsMap);
			}
		}
	}
	
	static String get(String sqlId, Map<String, Object> params){
		if(StrKit.isBlank(sqlId)){
			return null;
		}
		String[] values = sqlId.split("[.]");
		if(values.length != 2){
			return null;
		}
		return get(values[0], values[1], params);
	}

	private static String getXmlSql(String namespace, String sqlId){
		if(StrKit.isBlank(namespace) || StrKit.isBlank(sqlId)){
			return null;
		}
		Map<String, String> map = sqls.get(namespace);
		if(map != null){
			return map.get(sqlId);
		}
		return null;
	}
	
	final protected static Pattern CND_PATTERN = Pattern.compile(
    		"\\%\\{(.+)?\\}",
    		Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	static String get(String namespace, String sqlId, Map<String, Object> params){
		if(cfg == null){
			cfg =new Configuration();
		}
		
		String templateId = String.format("%s.%s", namespace, sqlId);
		
		String sql = getXmlSql(namespace, sqlId);
		if(StrKit.isBlank(sql)){
			throw new IllegalArgumentException(String.format("sqlId:%s ,不存在", templateId));
		}
		
		Matcher m = CND_PATTERN.matcher(sql);
		while(m.find()){
			System.out.println(m.group(0).replace("", ""));
		}
		
		try {
			StringTemplateLoader loader = new StringTemplateLoader();
			loader.putTemplate(templateId, sql);
			cfg.setTemplateLoader(loader);
			
			Template template = cfg.getTemplate(templateId);
			StringWriter sw = new StringWriter();
			template.process(params, sw);
			return sw.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException, TemplateException {
		/*Configuration cfg =new Configuration();
		cfg.setDirectoryForTemplateLoading(new File(PathKit.getRootClassPath()));
		Template t = cfg.getTemplate("/test.md");
		Map root =new HashMap();
		root.put("loginName","Tom");
		
		StringWriter sw = new StringWriter();
		t.process(root, sw);
		
		System.out.println(sw.toString());*/
		init("/", true);
		Map root =new HashMap();
		root.put("createTime","Tom");
		System.out.println(get("user.getusers1", root));
	}
}