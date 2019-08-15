package com.ets.system.threadinfo.web;

import com.ets.system.log.opr.entity.tb_log_opr;
import com.ets.system.log.opr.service.LogOprService;
import com.ets.system.threadinfo.service.ThreadInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 姚轶文
 * @create 2018- 12-11 16:30
 */
@Controller
@RequestMapping("thread")
public class ThreadController {

    @Resource
    ThreadInfoService threadInfoService;
    @Resource
    LogOprService logService;


    @RequestMapping("info")
    public String info(HttpServletRequest request)
    {
        //将"查看线程池"信息保存到操作日志
        tb_log_opr log = new tb_log_opr();
        log.setModulename("系统管理-线程池状态");
        log.setOprcontent("访问线程池状态列表");
        logService.addLog(log);

        request.setAttribute("active",threadInfoService.getActiveCount());
        request.setAttribute("queue",threadInfoService.getQueueSize());
        request.setAttribute("task",threadInfoService.getTaskCount());
        request.setAttribute("completed",threadInfoService.getCompletedTaskCount());
        request.setAttribute("max",threadInfoService.getMaxPoolSize());
        return "system/thread/thread-info";
    }
}
