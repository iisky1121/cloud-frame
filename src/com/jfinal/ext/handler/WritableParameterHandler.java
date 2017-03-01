package com.jfinal.ext.handler;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.handler.Handler;

/**
 * request Parameter可写入处理
 */
public class WritableParameterHandler extends Handler {
 
    @Override
    public void handle(String target, HttpServletRequest request,
            HttpServletResponse response, boolean[] isHandled) {
        next.handle(target, new WritableRequestWrapper(request), response, isHandled);
    }
}

class WritableRequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String[]> paramsMap;
	public WritableRequestWrapper(HttpServletRequest request) {
		super(request);
		Map<String, String[]> clone = new HashMap<String, String[]>();
		for (Iterator<Entry<String, String[]>> it = super.getParameterMap().entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>)it.next();
            clone.put(entry.getKey(), ((String[])entry.getValue()).clone());
        }
		paramsMap = clone;
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