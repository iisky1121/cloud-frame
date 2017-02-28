package com.jfinal.ext.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

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
	private Map<String, String[]> params;
	public JsonParameterRequestWrapper(HttpServletRequest request) {
		super(request);
		Map<String, String[]> clone = new HashMap<String, String[]>();
		String contentType = request.getContentType();
		if(!StrKit.isBlank(contentType) && "application/json;".equalsIgnoreCase(contentType)){
			String body = HttpKit.readData(request);
			if(!StrKit.isBlank(body)){
				System.out.println(body);
			}
		}
		params = clone;
		Collections.unmodifiableMap(clone);
	}
	
    public Map<String, String[]> getParameterMap() {  
        return params;  
    }
}