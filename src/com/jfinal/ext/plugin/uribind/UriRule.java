package com.jfinal.ext.plugin.uribind;

import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.ext.kit.ThreadPool;
import com.jfinal.kit.StrKit;

public abstract class UriRule {
	private Controller controller;
	private String id;
	
	public final Controller getController() {
		return controller;
	}
	
	void setController(Controller controller) {
		this.controller = controller;
	}

	public String getId() {
		return id;
	}

	void setId(String id) {
		this.id = id;
	}

	public void before(){
		
	}
	
	public void after(){
		
	}
	
	public final void execute(Invocation ai, UriBind uriBind){
		if(ai == null || uriBind == null){
			return;
		}
		
		setId(System.currentTimeMillis()+StrKit.createNum(8));
		setController(ai.getController());
		
		if(uriBind.beforeAsync()){
			ThreadPool.create(new Runnable(){
				public void run() {
					before();
				}
			});
		}
		else{
			before();
		}
		
		ai.invoke();
		
		if(uriBind.afterAsync()){
			after();
		}
		else{
			ThreadPool.create(new Runnable(){
				public void run() {
					after();
				}
			});
		}
	}
}
