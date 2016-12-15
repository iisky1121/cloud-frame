package com.jfinal.ext.plugin.jsql.core;

/**
 * 是否为开发模式
 * 开发默认不启用SQL、模板编译缓存功能
 * @author farmer
 *
 */
public class DevMode {

	private static boolean devMode = false;

	public static boolean isDevMode() {
		return devMode;
	}

	public static void setDevMode(boolean devMode) {
		DevMode.devMode = devMode;
	}
	
}
