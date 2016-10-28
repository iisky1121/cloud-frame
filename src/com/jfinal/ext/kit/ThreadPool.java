package com.jfinal.ext.kit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	
	public static void create(Runnable... runnables){
		if(runnables != null){
			for(Runnable runnable : runnables){
				cachedThreadPool.execute(runnable);
			}
		}
	}
}
