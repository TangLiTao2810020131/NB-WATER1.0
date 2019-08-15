package com.ets.business.nb_iot.cmdinfo.command.uitls;

import com.ets.business.nb_iot.cmdinfo.command.concurrent.CmdDelay;
import com.ets.business.nb_iot.cmdinfo.command.concurrent.CmdQueue;
import com.ets.business.nb_iot.hac.body.CommandBody;
import com.ets.business.nb_iot.hac.model.ReportDataHAC;
import com.ets.business.nb_iot.tlv.body.CommandBodyTLV;
import com.ets.business.nb_iot.tlv.model.ReportDataTLV;

/**
 * 组合操作类
 * @author wuhao
 *
 */
public class TemplateUtils {
	
	
	/**
	 * 组装校时需要发送的命令,并存储到命令集合
	 * @param deviceId 平台设备ID
	 */
	public  void CheckTime(String deviceId){

		ReportDataHAC reportData = (ReportDataHAC)new CommandBody().getCheckTimeCmd();
		if(reportData != null){
			CmdDelay delay = new CmdDelay(deviceId,reportData,System.currentTimeMillis());
			CmdQueue.getDelayQueue().put(delay);
		}

	}

	/**
	 * 封装上报周期名命令体,并存储到命令集合
	 * @param deviceId 平台设备ID
	 * @param reportData 命令体
	 * @param time 延迟执行时间
	 */
	public  void AssemblyDeliveryCommand(String deviceId,ReportDataHAC reportData,long time){

		if(reportData != null){
			CmdDelay delay = new CmdDelay(deviceId,reportData,System.currentTimeMillis() + time);
			CmdQueue.getDelayQueue().put(delay);
		}
	}

	/**
	 * 封装设置表读数命令体，,并存储到命令集合
	 * @param deviceId 平台设备ID
	 * @param reportData 命令体
	 * @param time 延迟执行时间
	 */
	public  void AssemblyWaterMeterBasicCommand(String deviceId,ReportDataHAC reportData,long time){

		if(reportData != null){
			CmdDelay delay = new CmdDelay(deviceId,reportData,System.currentTimeMillis() + time);
			CmdQueue.getDelayQueue().put(delay);
		}
	}

	/**
	 * 封装阀控命令体，,并存储到命令集合
	 * @param deviceId 平台设备ID
	 * @param reportData 命令体
	 * @param time 延迟执行时间
	 */
	public  void AssemblyValveControlCommand(String deviceId,ReportDataHAC reportData,long time){

		if(reportData != null){
			CmdDelay delay = new CmdDelay(deviceId,reportData,System.currentTimeMillis() + time);
			CmdQueue.getDelayQueue().put(delay);
		}
	}

	/**
	 * 组装校时需要发送的命令,并存储到命令集合
	 * @param deviceId 平台设备ID
	 */
	public  void CheckTimeTLV(String deviceId){

		ReportDataTLV reportData = (ReportDataTLV)new CommandBodyTLV().getCheckTimeCmd();
		if(reportData != null){
			CmdDelay delay = new CmdDelay(deviceId,reportData,System.currentTimeMillis());
/*			logger.info("TLV校时执行设备ID:"+delay.getDeviceId());
			logger.info("TLV校时执行命令："+delay.getReportData().getDev());
			logger.info("TLV校时当前时间："+DateTimeUtils.getnowdate());
			logger.info("TLV校时系统时间："+System.currentTimeMillis());*/
			CmdQueue.getDelayQueue().put(delay);
		}

	}
	
	public void AssemblyTLVBasicCommand(String deviceId, ReportDataTLV reportData, long time) {
		if(reportData != null){
			CmdDelay delay = new CmdDelay(deviceId,reportData,System.currentTimeMillis() + time);
/*			logger.info("TLV设置表读数执行设备ID:"+delay.getDeviceId());
			logger.info("TLV设置表读数执行命令："+delay.getReportData().getDev());
			logger.info("TLV设置表读数当前时间："+DateTimeUtils.getnowdate());
			logger.info("TLV设置表读数系统时间："+System.currentTimeMillis());
			logger.info("TLV设置表读数延迟时间："+String.valueOf(time));*/
			CmdQueue.getDelayQueue().put(delay);
		}
		
	}
}
