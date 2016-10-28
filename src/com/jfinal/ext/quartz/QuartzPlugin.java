package com.jfinal.ext.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.jfinal.plugin.IPlugin;

public class QuartzPlugin implements IPlugin {
	private SchedulerFactory sf = null;
	private Scheduler sched = null;

	public QuartzPlugin() {

	}

	public boolean start() {
		sf = new StdSchedulerFactory();
		try {
			sched = sf.getScheduler();
			sched.start();
		}
		catch (SchedulerException e) {
			new RuntimeException(e);
		}
		return true;
	}

	public boolean stop() {
		try {
			sched.shutdown();
		} 
		catch (SchedulerException e) {
			return false;
		}
		return true;
	}
}