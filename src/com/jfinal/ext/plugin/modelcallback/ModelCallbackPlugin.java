package com.jfinal.ext.plugin.modelcallback;

import com.jfinal.plugin.IPlugin;

public class ModelCallbackPlugin implements IPlugin{
	public boolean start() {
		ModelCallbackBuilder.init();
		return true;
	}

	public boolean stop() {
		ModelCallbackBuilder.close();
		return true;
	}
}
