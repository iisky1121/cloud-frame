package com.jfinal.ext.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.JsonKit;
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
	private Map<String, String[]> params;
	
	@SuppressWarnings("unchecked")
	public JsonParameterRequestWrapper(HttpServletRequest request) {
		super(request);
		Map<String, String[]> clone = new HashMap<String, String[]>();
		String contentType = request.getContentType();
		if(!StrKit.isBlank(contentType) && "application/json;charset=utf-8".equalsIgnoreCase(contentType)){
			String body = HttpKit.readData(request);
			if(!StrKit.isBlank(body)){
				Map<String,Object> map = JsonKit.parse(body, Map.class);
				for(Entry<String, Object> entry : map.entrySet()){
					if(entry.getValue() != null){
						if(entry.getValue().getClass().isArray()){
							
						} else{
							clone.put(entry.getKey(), new String[]{entry.getValue().toString()});
						}
					}
				}
			}
		}
		params = clone;
		Collections.unmodifiableMap(params);
	}
	
    public Map<String, String[]> getParameterMap() {  
        return params;  
    }
}