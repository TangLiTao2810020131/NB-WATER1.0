package com.ets.business.nb_iot.test;

import com.ets.business.nb_iot.cmdinfo.command.concurrent.CmdQueue;

import java.util.concurrent.DelayQueue;

/**
 * @author 姚轶文
 * @create 2018- 12-18 9:25
 */
public class CmdBatchAction {

    Thread thread[]  = new Thread[1];

    //启动数据压入线程
    public void action()
    {
        for(int i= 0 ; i<thread.length ; i++)
        {
            thread[i]  = new Thread(new CmdAdapter());
            thread[i].start();
            System.out.println("启动线程："+thread[i].getName());
        }
    }

    //启动队列监听线程
    public void listeningQueue()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DelayQueue delayQueue = CmdQueue.getDelayQueue();
                while (true)
                {
                    System.out.println("delayQueue队列长度："+delayQueue.size());
                }
            }
        });
        thread.setName("listeningQueue");
    }
}
