package com.jfinal.ext.plugin.uribind;

import com.jfinal.core.Controller;

public abstract class UriRule {
	private Controller controller;
	public Controller getController() {
		return controller;
	}
	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	public void before(){
		
	}
	
	public void after(){
		
	}
}
