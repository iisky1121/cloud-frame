package com.jfinal.ext.plugin.permission;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.base.BaseConfig;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Model;

public class PermissionInterceptor implements Interceptor{

	@SuppressWarnings("rawtypes")
	public void intercept(Invocation ai) {
		Controller controller = ai.getController();
		//权限判断
		String actionKey = ai.getActionKey();
		//判断url是否需要被拦截
		if(PermissionBuilder.isIntercepted(actionKey)){
			//登录拦截判断
			Model user = controller.getSessionAttr(BaseConfig.loginUserSessionAttr);
			if(user == null){
				controller.renderJson(BaseConfig.notLogin());
				return;
			}
			//判断url是否需要被拦截
			if(!PermissionBuilder.isAllow(controller.getSession(), actionKey)){
				controller.renderJson(BaseConfig.notPermission(actionKey));
				return;
			}
		}
		ai.invoke();
	}

}
