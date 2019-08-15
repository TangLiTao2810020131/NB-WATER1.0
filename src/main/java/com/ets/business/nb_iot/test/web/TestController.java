package com.ets.business.nb_iot.test.web;

import com.ets.business.nb_iot.test.CmdBatchAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 姚轶文
 * @create 2018- 12-18 10:05
 */
@Controller
@RequestMapping("nbtest")
public class TestController {

    @RequestMapping("test01")
    public String test01()
    {
        System.out.println("nbtest/test01");
        CmdBatchAction cmdBatchAction = new CmdBatchAction();
        cmdBatchAction.listeningQueue();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cmdBatchAction.action();

        return null;
    }
}
