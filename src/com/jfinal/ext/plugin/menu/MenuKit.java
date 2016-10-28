package com.jfinal.ext.plugin.menu;

import java.util.List;

import javax.servlet.http.HttpSession;

public class MenuKit{
	public static <T> void setUserMenus(HttpSession session, List<T> menus){
		MenuBuilder.setUserMenus(session, menus);
	}
	
	public static void remove(HttpSession session){
		MenuBuilder.remove(session);
	}
	
	public static <T> List<T> getUserMenus(HttpSession session){
		return MenuBuilder.getUserMenus(session);
	}
}
