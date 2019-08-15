package com.ets.business.timertask.task.service;

import com.ets.business.timertask.task.dao.TimerTaskMapper;
import com.ets.business.timertask.task.entity.nb_schedule_job;
import com.ets.business.timertask.task.quartz.QuartzJobFactory;
import com.ets.business.timertask.task.quartz.QuartzJobFactoryDisallowConcurrentExecution;
import com.ets.common.DateTimeUtils;
import com.ets.common.ObjectCode;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @Description: 计划任务管理
 * @author wuhao
 */
@Service
public class TimerTaskService {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@Autowired
	private TimerTaskMapper jobMapper;

	/**
	 * 从数据库中取 区别于getAllJob
	 * 
	 * @return
	 */
	public List<nb_schedule_job> getAllTask(Map<String, Object> map) {
		return jobMapper.getAllMap(map);
	}

	/**
	 * 添加到数据库中 区别于addJob
	 */
	public void addTask(nb_schedule_job job) {
		job.setCreatetime(DateTimeUtils.getnowdate());
		jobMapper.insertSelective(job);
	}

	/**
	 * 从数据库中查询job
	 */
	public nb_schedule_job getTaskById(String jobId) {
		return jobMapper.selectByPrimaryKey(jobId);
	}

	/**
	 * 更改任务状态
	 * 
	 * @throws SchedulerException
	 */
	public void changeStatus(String jobId, String cmd) throws SchedulerException {
		nb_schedule_job job = getTaskById(jobId);
		if (job == null) {
			return;
		}
		if ("stop".equals(cmd)) {
			deleteJob(job);
			job.setJobstatus(ObjectCode.TASK_STATUS_NOT_RUNNING);
		} else if ("start".equals(cmd)) {
			job.setJobstatus(ObjectCode.TASK_STATUS_RUNNING);
			addJob(job);
		}
		job.setCreatetime(DateTimeUtils.getnowdate());
		job.setUpdatetime(DateTimeUtils.getnowdate());
		jobMapper.updateByPrimaryKeySelective(job);
	}

	/**
	 * 更改任务 cron表达式
	 * 
	 * @throws SchedulerException
	 */
	public nb_schedule_job updateCron(nb_schedule_job job) throws SchedulerException {
		nb_schedule_job jobdb = getTaskById(job.getJobid());
		try {
			if (jobdb == null) {
				return null;
			}
			jobdb.setExecutordate(job.getExecutordate());
			jobdb.setCronexpression(job.getCronexpression());
			jobdb.setJobgroup(job.getJobgroup());
			if (ObjectCode.TASK_STATUS_RUNNING.equals(jobdb.getJobstatus())) {
				updateJobCron(jobdb);
			}
			jobMapper.updateByPrimaryKeySelective(jobdb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return job;

	}

	/**
	 * 添加任务
	 * 
	 * @throws SchedulerException
	 */
	public void addJob(nb_schedule_job job) throws SchedulerException {
		if (job == null || !ObjectCode.TASK_STATUS_RUNNING.equals(job.getJobstatus())) {
			return;
		}

		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		log.debug(scheduler + ".......................................................................................add");
		TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobname(), job.getJobgroup());

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		// 不存在，创建一个
		if (null == trigger) {
			Class clazz = ObjectCode.TASK_CONCURRENT_IS.equals(job.getIsconcurrent()) ? QuartzJobFactory.class : QuartzJobFactoryDisallowConcurrentExecution.class;

			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobname(), job.getJobgroup()).build();

			jobDetail.getJobDataMap().put("nb_schedule_job", job);

			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronexpression());

			trigger = TriggerBuilder.newTrigger().withIdentity(job.getJobname(), job.getJobgroup()).withSchedule(scheduleBuilder.withMisfireHandlingInstructionDoNothing()).build();

			scheduler.scheduleJob(jobDetail, trigger);
		} else {
			// Trigger已存在，那么更新相应的定时设置
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronexpression());

			// 按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder.withMisfireHandlingInstructionDoNothing()).build();

			// 按新的trigger重新设置job执行
			scheduler.rescheduleJob(triggerKey, trigger);
		}
	}

	@PostConstruct
	public void init() throws Exception {

		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		// 这里获取任务信息数据
		List<nb_schedule_job> jobList = jobMapper.getAll();
	
		for (nb_schedule_job job : jobList) {
			addJob(job);
		}
	}

	/**
	 * 获取所有计划中的任务列表
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<nb_schedule_job> getAllJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		GroupMatcher<JobKey> matcher = GroupMatcher.anyJobGroup();
		Set<JobKey> jobKeys = scheduler.getJobKeys(matcher);
		List<nb_schedule_job> jobList = new ArrayList<nb_schedule_job>();
		for (JobKey jobKey : jobKeys) {
			List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
			for (Trigger trigger : triggers) {
				nb_schedule_job job = new nb_schedule_job();
				job.setJobname(jobKey.getName());
				job.setJobgroup(jobKey.getGroup());
				job.setDescription("触发器:" + trigger.getKey());
				Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
				job.setJobstatus(triggerState.name());
				if (trigger instanceof CronTrigger) {
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String cronExpression = cronTrigger.getCronExpression();
					job.setCronexpression(cronExpression);
				}
				jobList.add(job);
			}
		}
		return jobList;
	}

	/**
	 * 所有正在运行的job
	 * 
	 * @return
	 * @throws SchedulerException
	 */
	public List<nb_schedule_job> getRunningJob() throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
		List<nb_schedule_job> jobList = new ArrayList<nb_schedule_job>(executingJobs.size());
		for (JobExecutionContext executingJob : executingJobs) {
			nb_schedule_job job = new nb_schedule_job();
			JobDetail jobDetail = executingJob.getJobDetail();
			JobKey jobKey = jobDetail.getKey();
			Trigger trigger = executingJob.getTrigger();
			job.setJobname(jobKey.getName());
			job.setJobgroup(jobKey.getGroup());
			job.setDescription("触发器:" + trigger.getKey());
			Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
			job.setJobstatus(triggerState.name());
			if (trigger instanceof CronTrigger) {
				CronTrigger cronTrigger = (CronTrigger) trigger;
				String cronExpression = cronTrigger.getCronExpression();
				job.setCronexpression(cronExpression);
			}
			jobList.add(job);
		}
		return jobList;
	}

	/**
	 * 暂停一个job
	 * 
	 * @param nb_schedule_job
	 * @throws SchedulerException
	 */
	public void pauseJob(nb_schedule_job nb_schedule_job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(nb_schedule_job.getJobname(), nb_schedule_job.getJobgroup());
		scheduler.pauseJob(jobKey);
	}

	/**
	 * 恢复一个job
	 * 
	 * @param nb_schedule_job
	 * @throws SchedulerException
	 */
	public void resumeJob(nb_schedule_job nb_schedule_job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(nb_schedule_job.getJobname(), nb_schedule_job.getJobgroup());
		scheduler.resumeJob(jobKey);
	}

	/**
	 * 删除一个job
	 * 
	 * @param nb_schedule_job
	 * @throws SchedulerException
	 */
	public void deleteJob(nb_schedule_job nb_schedule_job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(nb_schedule_job.getJobname(), nb_schedule_job.getJobgroup());
		scheduler.deleteJob(jobKey);

	}

	/**
	 * 立即执行job
	 * 
	 * @param nb_schedule_job
	 * @throws SchedulerException
	 */
	public void runAJobNow(nb_schedule_job nb_schedule_job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = JobKey.jobKey(nb_schedule_job.getJobname(), nb_schedule_job.getJobgroup());
		scheduler.triggerJob(jobKey);
	}

	/**
	 * 更新job时间表达式
	 * 
	 * @param nb_schedule_job
	 * @throws SchedulerException
	 */
	public void updateJobCron(nb_schedule_job nb_schedule_job) throws SchedulerException {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		TriggerKey triggerKey = TriggerKey.triggerKey(nb_schedule_job.getJobname(), nb_schedule_job.getJobgroup());

		CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(nb_schedule_job.getCronexpression());

		trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

		scheduler.rescheduleJob(triggerKey, trigger);
	}

	public nb_schedule_job infoJob(Map<String, Object> map) {
		try {
			return jobMapper.infoJob(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int isCkeckJobName(Map<String, Object> map) {
		int sum = 0;
		try {
			sum = jobMapper.isCkeckJobName(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sum;
	}
}
