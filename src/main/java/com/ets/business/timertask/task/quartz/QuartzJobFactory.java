package com.ets.business.timertask.task.quartz;

import com.ets.business.timertask.task.entity.nb_schedule_job;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 计划任务执行处 无状态
 * @author wuhao
 *
 */
public class QuartzJobFactory implements Job {
	public final Logger log = LoggerFactory.getLogger(this.getClass());

	public void execute(JobExecutionContext context) throws JobExecutionException {
		nb_schedule_job scheduleJob = (nb_schedule_job) context.getMergedJobDataMap().get("nb_schedule_job");
		QuartzJobTaskUtils.invokMethod(scheduleJob);
		System.out.println("QuartzJobFactory");
	}
}