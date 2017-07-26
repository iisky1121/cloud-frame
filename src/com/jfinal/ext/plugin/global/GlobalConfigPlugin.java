package com.jfinal.ext.plugin.global;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class GlobalConfigPlugin implements IPlugin{
	private GlobalInterceptor interceptor;
	public GlobalInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new GlobalInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(GlobalInterceptor interceptor) {
		this.interceptor = interceptor;
	}

	public boolean start() {
		//加入全局错误拦截器
		JFinalKit.getInterceptors().add(getInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
