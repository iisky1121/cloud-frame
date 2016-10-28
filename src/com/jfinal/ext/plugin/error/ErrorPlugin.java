package com.jfinal.ext.plugin.error;

import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class ErrorPlugin implements IPlugin{
	public boolean start() {
		//加入全局错误拦截器
		JFinalKit.getInterceptors().add(new ErrorInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
