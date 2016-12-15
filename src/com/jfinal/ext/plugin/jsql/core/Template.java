package com.jfinal.ext.plugin.jsql.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.alibaba.fastjson.JSON;

/**
 * 模板引擎
 * 核心使用underscore的模板引擎
 * @author farmer
 */
public class Template {
	/**
	 * 单例
	 */
	private static Template template = null;
	/**
	 * 
	 */
	private Context rhino = null;
	
	private Scriptable scope = null;
	
	private Map<String, Scriptable> compiledMap = new HashMap<String, Scriptable>();
	
	/**
	 * 
	 */
	private Template(){
		rhino = Context.enter();
		scope = rhino.initStandardObjects();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(getClass().getResourceAsStream("/Template.js"));
			rhino.evaluateReader(scope, reader, "Template", 0, null);
			ScriptableObject.putConstProperty(scope, "out", Context.javaToJS(System.out, scope));
		} catch (IOException e) {
			throw new RuntimeException("模板JS初始化异常!", e);
		}
		finally{
			Context.exit();
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 渲染模板
	 * @param template
	 * @param json
	 * @return
	 * @throws Exception 
	 */
	public synchronized SqlArgs render(String template ,Object para) throws Exception{
		rhino = Context.enter();
		rhino.setOptimizationLevel(-1);
		try {
			SqlArgs sqlArgs = new SqlArgs();
			List<Object> args = new ArrayList<Object>();
			ScriptableObject.putProperty(scope, "tpl", template);
			ScriptableObject.putProperty(scope, "json", JSON.toJSONString(para));
			Scriptable compiled =  compiledMap.get(template);
			if(DevMode.isDevMode()){	//开发模式
				rhino.evaluateString(scope, "var compiled = _.template(tpl)",null,1,null);
				compiled = (Scriptable) ScriptableObject.getProperty(scope, "compiled");
			}
			else
			{
				if(null == compiled){		
					rhino.evaluateString(scope, "var compiled = _.template(tpl)",null,1,null);
					compiled = (Scriptable) ScriptableObject.getProperty(scope, "compiled");
					compiledMap.put(template, compiled);
				}
			}
			ScriptableObject.putProperty(scope, "compiled", compiled);
			ScriptableObject.putProperty(scope, "args", Context.javaToJS(args, scope));
			sqlArgs.setSql((String) rhino.evaluateString(scope, "compiled(eval('('+json+')'),args)", null, 1, null));
			sqlArgs.setArgs(args);
			return sqlArgs;
		} catch (Exception e) {
			throw new Exception("模板处理异常!", e);
		}
		finally{
			Context.exit();
		}
	}
	
	/**
	 * 获取模板引擎单例
	 * @return
	 */
	public static Template getTemplate() {
		if(template == null){
			template = new Template();
		}
		return template;
	}
	


}
