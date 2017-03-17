package com.jfinal.ext.handler;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.StrKit;

/**
 * request json Parameter处理
 */
public class JsonParameterHandler extends Handler {
 
    @Override
    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, boolean[] isHandled) {
        next.handle(target, new JsonParameterRequestWrapper(request), response, isHandled);
    }
}

class JsonParameterRequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String[]> paramsMap;
	
	public JsonParameterRequestWrapper(HttpServletRequest request) {
		super(request);
		Map<String, String[]> clone = new HashMap<String, String[]>();
		String contentType = request.getContentType();
		boolean isChange = false;
		if(!StrKit.isBlank(contentType) && "application/json;charset=utf-8".equalsIgnoreCase(contentType)){
			String body = HttpKit.readData(request);
			if(!StrKit.isBlank(body)){
				JSONObject json = JSONObject.parseObject(body);
				for(Entry<String, Object> entry : json.entrySet()){
					if(entry.getValue() != null && entry.getValue() instanceof String){
						clone.put(entry.getKey(), new String[]{entry.getValue().toString()});
						isChange = true;
					}
				}
			}
		}
		
		if(isChange){
			paramsMap = clone;
			Collections.unmodifiableMap(paramsMap);
		} else{
			paramsMap = super.getParameterMap();
		}
	}
	
    public Map<String, String[]> getParameterMap() {  
        return paramsMap;  
    }
    
    public String[] getParameterValues(String name) {
        String[] value = ((String[])paramsMap.get(name));
        return value != null ? (String[])value.clone() : null;
    }

    public String getParameter(String name) {
        String[] values = (String[])paramsMap.get(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Enumeration getParameterNames() {
        return Collections.enumeration(paramsMap.keySet());
    }
}