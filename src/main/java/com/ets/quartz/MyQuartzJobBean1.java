package com.ets.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author 姚轶文
 * @create 2019- 05-06 10:02
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution// 不允许并发执行
public class MyQuartzJobBean1 extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(MyQuartzJobBean1.class);

    @Override
    protected void executeInternal(JobExecutionContext jobexecutioncontext) throws JobExecutionException {

        SimpleService simpleService = getApplicationContext(jobexecutioncontext).getBean("simpleService",SimpleService.class);
        simpleService.testMethod1();
        simpleService.testMethod2();

    }

    private ApplicationContext getApplicationContext(final JobExecutionContext jobexecutioncontext) {
        try {
            return (ApplicationContext) jobexecutioncontext.getScheduler().getContext().get("applicationContextKey");
        } catch (SchedulerException e) {
            logger.error("jobexecutioncontext.getScheduler().getContext() error!", e);
            throw new RuntimeException(e);
        }
    }}
