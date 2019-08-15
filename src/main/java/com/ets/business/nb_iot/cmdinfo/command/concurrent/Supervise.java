package com.ets.business.nb_iot.cmdinfo.command.concurrent;

import com.ets.business.nb_iot.cmdinfo.command.send.CommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author 姚轶文
 * @create 2018- 12-11 10:33
 */
public class Supervise implements InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(Supervise.class);

    @Autowired
    protected ThreadPoolTaskExecutor taskExecutor;
    
    @Autowired
    protected CommandService commandService;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("命令下发监听启动");


       taskExecutor.execute(new Runnable(){
            @Override
            public void run() {
                while (true)
                {
                    try {
                        //ApplicationContext context = SpringContextUtils.getContext();
                        CmdAdapter cmdAdapter = SpringContextUtils.getContext().getBean(CmdAdapter.class);
                        cmdAdapter.setCmdDelay(CmdQueue.getDelayQueue().take());
                        taskExecutor.execute(cmdAdapter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
