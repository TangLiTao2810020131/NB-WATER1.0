package com.ets.business.nb_iot.test;

/**
 * @author 姚轶文
 * @create 2018- 12-17 19:59
 */
public class CmdAdapter implements Runnable {

    @Override
    public void run() {


            CmdQueueTest cmdQueueTest = new CmdQueueTest();
            cmdQueueTest.putQueue();


    }
}
