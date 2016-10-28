package com.jfinal.ext.plugin.permission;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.jfinal.base.BaseConfig;
import com.jfinal.ext.kit.CloneKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;

class PermissionBuilder{
	private static List<String> intercepted_urls = new ArrayList<String>();
	private static String user_allow_urls_attr = BaseConfig.userAllowUrlAttr;
	
	static void setInterceptedUrls(List<String> interceptedUrls){
		LogKit.debug("设置用户权限Session\n urls:?",interceptedUrls);
		intercepted_urls.addAll(interceptedUrls);
	}

	static void remove(HttpSession session){
		session.removeAttribute(user_allow_urls_attr);
	}
	
	static List<String> getInterceptedUrls(){
		return intercepted_urls;
	}
	
	static boolean isIntercepted(String url){
		return intercepted_urls.contains(url);
	}
	
	static boolean isAllow(HttpSession session, String url){
		if(StrKit.isBlank(url)){
			return false;
		}
		if(intercepted_urls.contains(url)){
			List<String> allowUrls = getUserAllowUrls(session);
			if(allowUrls != null && allowUrls.size() > 0 && allowUrls.contains(url)){
				return true;
			}
			return false;
		}
		return true;
	}
	
	static void setUserAllowUrls(HttpSession session, List<String> userAllowUrls){
		session.setAttribute(user_allow_urls_attr, userAllowUrls);
	}
	
	@SuppressWarnings("unchecked")
	static List<String> getUserAllowUrls(HttpSession session){
		Object object = session.getAttribute(user_allow_urls_attr);
		return (List<String>) CloneKit.deep(object);
	}
	
}
