package com.jfinal.ext.plugin.validate;

import java.lang.reflect.Method;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

/**
 * 
 * @author iisky1121
 * 2017年1月6日 下午4:38:04
 */
public class ValidatePlugin implements IPlugin{
	private ValidateInterceptor interceptor;
	public ValidateInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new ValidateInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(ValidateInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	
	public boolean start() {
		//扫描注解
		List<Class<? extends Controller>> controllers = ClassSearcher.of(Controller.class).includeAllJarsInLib(false).search();
		Method[] methods;
		Validate[] validates;
		CheckNotNull checkNotNull;
		for(Class<? extends Controller> controllerClass : controllers){
			methods = controllerClass.getMethods();
			for(Method method : methods){
				checkNotNull = method.getAnnotation(CheckNotNull.class);
				if(checkNotNull != null){
					ValidateBuilder.addRule(method, checkNotNull);
				}
				validates = method.getDeclaredAnnotationsByType(Validate.class);
				if(validates != null && validates.length > 0){
					ValidateBuilder.addRule(method, validates);
				}
			}
		}
        //加入全局规则拦截器
		JFinalKit.getInterceptors().add(getInterceptor());
		return true;
	}

	public boolean stop() {
		return true;
	}
}
