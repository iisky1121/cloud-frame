package com.jfinal.ext.plugin.sqlinmd;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;

class SqlBuilder {
	private static Configuration cfg;
	private static Vector<File> vecFiles = new Vector<File>();
	private static Map<String, Map<String, String>> sqls = new HashMap<String, Map<String, String>>();

	static void clearSqlMap() {
		sqls.clear();
	}

	static void init(String fileAbsolutePath, boolean scanSubFolder) {
		getFileName(PathKit.getRootClassPath()+fileAbsolutePath, scanSubFolder);
		if(vecFiles.size() > 0){
			load();
		}
	}

	static void getFileName(String fileAbsolutePath, boolean scanSubFolder) {
		File file = new File(fileAbsolutePath);
		File[] files = file.listFiles();
		if (files == null) {
			return;
		} else {
			for (File f : files) {
				if (scanSubFolder && f.isDirectory()) {
					getFileName(f.getAbsolutePath(), scanSubFolder);// 采用递归调用

				} else if (f.getName().endsWith("-sql.xml")) {
					vecFiles.add(f);
				}
			}
		}
	}
	
	static void load(){
		for(File file : vecFiles){
			
		}
	}
	
	static String get(String sqlId, Map<String, String> params){
		if(StrKit.isBlank(sqlId)){
			return null;
		}
		String[] values = sqlId.split("[.]");
		if(values.length != 2){
			return null;
		}
		return get(values[0], values[1], params);
	}

	private static String getSql(String namespace, String sqlId){
		if(StrKit.isBlank(namespace) || StrKit.isBlank(sqlId)){
			return null;
		}
		Map<String, String> map = sqls.get(namespace);
		if(map != null){
			return map.get(sqlId);
		}
		return null;
	}
	
	static String get(String namespace, String sqlId, Map<String, String> params){
		if(cfg == null){
			try {
				cfg = Configuration.defaultConfiguration();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String templateId = String.format("%s.%s", namespace, sqlId);
		
		String sql = getSql(namespace, sqlId);
		if(StrKit.isBlank(sql)){
			throw new IllegalArgumentException(String.format("sqlId:%s ,不存在", templateId));
		}
		
		try {
			StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
			GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
			Template t = gt.getTemplate("hello,${name}");
			t.binding("name", "beetl");
			String str = t.render();
			System.out.println(str);
			return str;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
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
		root.put("loginName","Tom");
		System.out.println(get("user.getusers1", root));
	}
}