package com.jfinal.ext.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ParameterRequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String[]> params;
	public ParameterRequestWrapper(HttpServletRequest request) {
		super(request);
		Map<String, String[]> clone = new HashMap<String, String[]>();
		for (Iterator<Entry<String, String[]>> it = super.getParameterMap().entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>)it.next();
            clone.put(entry.getKey(), ((String[])entry.getValue()).clone());
        }
		params = clone;
	}
	
    public Map<String, String[]> getParameterMap() {  
        return params;  
    }
}
