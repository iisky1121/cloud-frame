package com.jfinal.ext.quartz;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzKit {
	
	/**
	 * 添加任务
	 */
	@SuppressWarnings({ "rawtypes" })
	public static void addJob(Class entiryClass, String jobName){
		addJob(entiryClass, jobName, new HashMap());
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addJob(Class entiryClass, String jobName, Map map){
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			
			//判断是否存在
			JobKey jobKey = JobKey.jobKey(jobName);
			if(sched.checkExists(jobKey)){
				return;
			}
			
			JobDetail jobDetail= JobBuilder.newJob(entiryClass).withIdentity(jobName).build();
			if(map != null)
				jobDetail.getJobDataMap().putAll(map);
			sched.scheduleJob(jobDetail,TriggerBuilder.newTrigger().startNow().build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings({ "rawtypes" })
	public static void addJob(Class entiryClass, String jobName, String cron){
		addJob(entiryClass, jobName, cron, new HashMap());
	}
	@SuppressWarnings({ "rawtypes" })
	public static void addJob(Class entiryClass, String jobName, String cron, Map map){
		addJob(entiryClass, jobName, null, cron, map);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addJob(Class entiryClass, String jobName, String groupName, String cron, Map map){
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			
			//判断是否存在
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			if(sched.checkExists(jobKey)){
				return;
			}
			
			JobDetail jobDetail;
			//作业的触发器
			TriggerBuilder triggerBuilder;
			
			if(groupName == null){
				jobDetail = JobBuilder.newJob(entiryClass).withIdentity(jobName).build();
				triggerBuilder=TriggerBuilder.newTrigger();
				triggerBuilder.withIdentity(jobName);
			}
			else{
				jobDetail = JobBuilder.newJob(entiryClass).withIdentity(jobName, groupName).build();
				triggerBuilder=TriggerBuilder.newTrigger();
				triggerBuilder.withIdentity(jobName ,groupName);
			}
			if(map != null)
				jobDetail.getJobDataMap().putAll(map);
			
			if(cron == null){
				triggerBuilder.startNow();
			}
			else{
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
			}
			
			sched.scheduleJob(jobDetail,triggerBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes" })
	public static void addJob(Class entiryClass, String jobName, Date date){
		addJob(entiryClass, jobName, date, null);
	}
	@SuppressWarnings({ "rawtypes" })
	public static void addJob(Class entiryClass, String jobName, Date date, Map map){
		addJob(entiryClass, jobName, null, date, map, false);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addJob(Class entiryClass, String jobName, String groupName, Date date, Map map, boolean isReset){
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			
			//判断是否存在
			JobKey jobKey = JobKey.jobKey(jobName, groupName);
			if(sched.checkExists(jobKey)){
				if(!isReset){
					return;
				}
				sched.deleteJob(jobKey);
			}
			
			JobDetail jobDetail;
			//作业的触发器
			TriggerBuilder triggerBuilder;
			
			if(groupName == null){
				jobDetail = JobBuilder.newJob(entiryClass).withIdentity(jobName).build();
				triggerBuilder=TriggerBuilder.newTrigger();
				triggerBuilder.withIdentity(jobName);
			}
			else{
				jobDetail = JobBuilder.newJob(entiryClass).withIdentity(jobName, groupName).build();
				triggerBuilder=TriggerBuilder.newTrigger();
				triggerBuilder.withIdentity(jobName ,groupName);
			}
			if(map != null)
				jobDetail.getJobDataMap().putAll(map);
			
			if(date == null){
				triggerBuilder.startNow();
			}
			else{
				triggerBuilder.startAt(date);
			}
			
			sched.scheduleJob(jobDetail,triggerBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 暂停任务
	 */
	public static void pauseJob(String jobName){
		pauseJob(jobName, null);
	}
	public static void pauseJob(String jobName ,String groupName){
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler scheduler = sf.getScheduler();
			JobKey jobKey;
			if(groupName == null){
				jobKey = JobKey.jobKey(jobName);
			}
			else{
				jobKey = JobKey.jobKey(jobName, groupName);
			}
			scheduler.pauseJob(jobKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除任务
	 */
	public static void delJob(String jobName){
		delJob(jobName, null);
	}
	public static void delJob(String jobName, String groupName){
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			JobKey jobKey;
			if(groupName == null){
				jobKey = JobKey.jobKey(jobName);
			}
			else{
				jobKey = JobKey.jobKey(jobName, groupName);
			}
			sched.deleteJob(jobKey);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
