package com.jfinal.ext.plugin.permission;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class PermissionPlugin implements IPlugin{
	private PermissionInterceptor interceptor;
	public PermissionInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new PermissionInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(PermissionInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public boolean start() {
		//加入全局权限拦截器
		JFinalKit.getInterceptors().add(getInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
