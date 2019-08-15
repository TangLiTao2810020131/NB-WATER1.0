package com.ets.business.nb_iot.test;

import com.ets.business.nb_iot.cmdinfo.command.uitls.TemplateUtils;
import com.ets.business.nb_iot.hac.body.CommandBody;
import com.ets.business.nb_iot.hac.model.ReportDataHAC;

/**
 * @author 姚轶文
 * @create 2018- 12-17 19:39
 */

public class CmdQueueTest {

    String deviceId = "5e09f776-0538-488c-b39d-fc9622346a6c";

    public void putQueue()
    {
        long nowtime = System.currentTimeMillis();

        CommandBody commandBody = new CommandBody();
        TemplateUtils templateUtils = new TemplateUtils();

       // ReportData checkTimeReportData = commandBody.getCheckTimeCmd();
        ReportDataHAC deliverReportData = (ReportDataHAC)commandBody.getDeliveryCmd("1000");
        ReportDataHAC valveControlReportData = (ReportDataHAC) commandBody.getValveControlCmd(0);
        ReportDataHAC waterMeterBasicReportData = (ReportDataHAC)commandBody.getWaterMeterBasicCmd("100");

        templateUtils.CheckTime(deviceId);
        templateUtils.AssemblyDeliveryCommand(deviceId,deliverReportData,nowtime+6000);
        templateUtils.AssemblyWaterMeterBasicCommand(deviceId,waterMeterBasicReportData,nowtime+6000+6000);
        templateUtils.AssemblyValveControlCommand(deviceId,valveControlReportData,nowtime+6000+6000+6000);

    }

}
