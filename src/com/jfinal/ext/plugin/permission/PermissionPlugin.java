package com.jfinal.ext.plugin.permission;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class PermissionPlugin implements IPlugin{
	public boolean start() {
		//加入全局权限拦截器
		JFinalKit.getInterceptors().add(new PermissionInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
