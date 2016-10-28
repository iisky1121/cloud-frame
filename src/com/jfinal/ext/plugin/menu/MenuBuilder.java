package com.jfinal.ext.plugin.menu;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.jfinal.base.BaseConfig;
import com.jfinal.ext.kit.CloneKit;
import com.jfinal.kit.LogKit;

class MenuBuilder{
	private static String user_menus_attr = BaseConfig.userMenuAttr;
	
	static <T> void setUserMenus(HttpSession session,List<T> menus){
		LogKit.debug("设置菜单Session\n attrobite:?\n value:?",user_menus_attr,menus);
		session.setAttribute(user_menus_attr, menus);
	}
	
	@SuppressWarnings({ "unchecked" })
	static <T> List<T> getUserMenus(HttpSession session){
		Object object = session.getAttribute(user_menus_attr);
		return (List<T>) CloneKit.deep(object);
	}
	
	static void remove(HttpSession session){
		session.removeAttribute(user_menus_attr);
	}
}
