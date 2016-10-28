package com.jfinal.ext.plugin.permission;

import java.util.List;

import javax.servlet.http.HttpSession;

public class PermissionKit {
	public static void setInterceptedUrls(List<String> interceptedUrls){
		PermissionBuilder.setInterceptedUrls(interceptedUrls);
	}
	
	public static List<String> getInterceptedUrls(){
		return PermissionBuilder.getInterceptedUrls();
	}
	
	public static void setUserAllowUrls(HttpSession session, List<String> userAllowUrls){
		PermissionBuilder.setUserAllowUrls(session, userAllowUrls);
	}
	
	public static void remove(HttpSession session){
		PermissionBuilder.remove(session);
	}
	
	public static List<String> getUserAllowUrls(HttpSession session){
		return PermissionBuilder.getUserAllowUrls(session);
	}
	
	public static boolean isIntercepted(String url){
		return PermissionBuilder.isIntercepted(url);
	}
	
	public static boolean isAllow(HttpSession session, String url){
		return PermissionBuilder.isAllow(session, url);
	}
}
