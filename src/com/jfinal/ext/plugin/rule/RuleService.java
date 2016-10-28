package com.jfinal.ext.plugin.rule;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;

public class RuleService {
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
		RuleBuilder.invoke(ruleId, ai);
	}
}
