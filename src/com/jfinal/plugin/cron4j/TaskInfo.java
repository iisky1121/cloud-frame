package com.jfinal.plugin.cron4j;

import com.jfinal.kit.StrKit;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;

/**
 * Created by hang on 2017/11/20 0020.
 */
public class TaskInfo {
    Scheduler scheduler;

    String cron;
    Object task;
    boolean daemon;
    boolean enable;

    public TaskInfo(String cron, Object task, boolean daemon, boolean enable) {
        if (StrKit.isBlank(cron)) {
            throw new IllegalArgumentException("cron 不能为空.");
        }
        if (task == null) {
            throw new IllegalArgumentException("task 不能为 null.");
        }

        this.cron = cron.trim();
        this.task = task;
        this.daemon = daemon;
        this.enable = enable;
    }

    public void schedule() {
        if (enable) {
            scheduler = new Scheduler();
            if (task instanceof Runnable) {
                scheduler.schedule(cron, (Runnable) task);
            } else if (task instanceof Task) {
                scheduler.schedule(cron, (Task) task);
            } else {
                scheduler = null;
                throw new IllegalStateException("Task 必须是 Runnable、ITask、ProcessTask 或者 Task 类型");
            }
            scheduler.setDaemon(daemon);
        }
    }

    public void start() {
        if (enable) {
            scheduler.start();
        }
    }

    public void stop() {
        if (enable) {
            if (task instanceof ITask) {   // 如果任务实现了 ITask 接口，则回调 ITask.stop() 方法
                ((ITask)task).stop();
            }
            scheduler.stop();
        }
    }
}
