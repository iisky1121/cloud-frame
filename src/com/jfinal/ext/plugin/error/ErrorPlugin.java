package com.jfinal.ext.plugin.error;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class ErrorPlugin implements IPlugin{
	private ErrorInterceptor interceptor;
	public ErrorInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new ErrorInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(ErrorInterceptor interceptor) {
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
