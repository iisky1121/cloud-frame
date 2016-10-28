package com.jfinal.ext.plugin.uribind;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class UriRuleService {
	private Controller controller;

	public Controller getController() {
		return controller;
	}

	void setController(Controller controller) {
		this.controller = controller;
	}

	protected void invoke(String ruleId, Invocation ai) throws Exception{
		this.controller = ai.getController();
		this.controller.isRuleControl(true);
		UriRuleBuilder.invoke(ruleId, ai);
	}
}
