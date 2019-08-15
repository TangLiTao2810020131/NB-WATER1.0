package com.ets.business.nb_iot.cmdinfo.command.callback;

import com.ets.business.nb_iot.cmdinfo.command.concurrent.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class CallBackSupervise implements InitializingBean {
	
	private static Logger logger = LoggerFactory.getLogger(CallBackSupervise.class);

    @Autowired
    protected ThreadPoolTaskExecutor taskExecutor;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("命令执行结果监听启动");


       taskExecutor.execute(new Runnable(){
            @Override
            public void run() {
            	
                while (true)
                {
                    try {
                        //ApplicationContext context = SpringContextUtils.getContext();
                    	CallBackAdapter callBackAdapter = SpringContextUtils.getContext().getBean(CallBackAdapter.class);
                    	callBackAdapter.setCallBackDelay(CallBackQueue.getCallBackQueue().take());
                        taskExecutor.execute(callBackAdapter);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
}
