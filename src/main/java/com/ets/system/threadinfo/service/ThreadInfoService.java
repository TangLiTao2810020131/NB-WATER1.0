package com.ets.system.threadinfo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 姚轶文
 * @create 2018- 12-11 16:18
 */
@Service
public class ThreadInfoService {
    @Autowired
    protected ThreadPoolTaskExecutor taskExecutor;


    public int getQueueSize() //当前排队线程数
    {
        ThreadPoolExecutor threadPoolExecutor = taskExecutor.getThreadPoolExecutor();
        int queueSize = threadPoolExecutor.getQueue().size();
        return queueSize;
    }

    public int getActiveCount() //当前活动线程数
    {
        ThreadPoolExecutor threadPoolExecutor = taskExecutor.getThreadPoolExecutor();
        int activeCount = threadPoolExecutor.getActiveCount();
        return activeCount;
    }

    public long getCompletedTaskCount() //当前执行完成线程数
    {
        ThreadPoolExecutor threadPoolExecutor = taskExecutor.getThreadPoolExecutor();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        return completedTaskCount;
    }

    public long getTaskCount() //总线程数
    {
        ThreadPoolExecutor threadPoolExecutor = taskExecutor.getThreadPoolExecutor();
        long taskCount =taskExecutor.getPoolSize();
        return taskCount;
    }

    public int getMaxPoolSize()
    {
        int max = taskExecutor.getMaxPoolSize();
        return max;
    }


}
