package com.jfinal.ext.plugin.globalerror;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class GlobalErrorPlugin implements IPlugin{
	private GlobalErrorInterceptor interceptor;
	public GlobalErrorInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new GlobalErrorInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(GlobalErrorInterceptor interceptor) {
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
