package com.jfinal.ext.plugin.validate;

import java.lang.reflect.Method;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.ext.kit.JFinalKit;
import com.jfinal.plugin.IPlugin;

public class ValidationPlugin implements IPlugin{
	private ValidationInterceptor interceptor;
	public ValidationInterceptor getInterceptor() {
		if(interceptor == null){
			interceptor = new ValidationInterceptor();
		}
		return interceptor;
	}

	public void setInterceptor(ValidationInterceptor interceptor) {
		this.interceptor = interceptor;
	}
	
	public boolean start() {
		//扫描注解
		List<Class<? extends Controller>> controllers = ClassSearcher.of(Controller.class).includeAllJarsInLib(false).search();
		Method[] methods;
		Validation[] validations;
		for(Class<? extends Controller> controllerClass : controllers){
			methods = controllerClass.getMethods();
			for(Method method : methods){
				validations = method.getDeclaredAnnotationsByType(Validation.class);
				if(validations != null && validations.length > 0){
					ValidationBuilder.addRule(method, validations);
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
