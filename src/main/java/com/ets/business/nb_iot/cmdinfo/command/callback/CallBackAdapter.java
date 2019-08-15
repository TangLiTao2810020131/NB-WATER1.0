package com.ets.business.nb_iot.cmdinfo.command.callback;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ets.business.commandsendlog.entity.nb_command_send_log;
import com.ets.business.commandsendlog.service.CommandSendLogService;
import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.equipment.service.EquipmentService;
import com.ets.business.nb_iot.cmdinfo.iotinit.NbIotConfig;
import com.ets.business.nb_iot.hac.body.ObjectClass;
import com.ets.business.nb_iot.hac.model.CallBackObject;
import com.ets.business.nb_iot.hac.model.ReportDataHAC;
import com.ets.business.nb_iot.hac.model.ValveControl;
import com.ets.business.nb_iot.json.JsonUtils;
import com.ets.business.remote.service.RemoteService;
import com.ets.common.ObjectCode;
import com.ets.system.shiro.cache.ByteSourceUtils;
import com.ets.system.shiro.cache.RedisClientTemplate;
import com.ets.system.sysCommandSendLog.entity.tb_sys_command_send_log;
import com.ets.system.sysCommandSendLog.service.SysCommandSendLogService;
import com.ets.system.sysEquipment.entity.tb_sys_equipment;
import com.ets.system.sysEquipment.service.SysEquipmentService;
import com.ets.system.sysdaelaytime.service.SysDelayTimeService;

import net.sf.json.JSONObject;

@Service
@Scope(value = "singleton")
public class CallBackAdapter implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(CallBackAdapter.class);

	CallBackDelay callBackDelay;

	@Autowired
	NbIotConfig nbIotConfig;
	
	@Autowired
	RemoteService remoteService;

	@Resource
	EquipmentService equipmentService;

	@Resource
	SysEquipmentService sysEquipmentService;

	@Resource
	SysDelayTimeService sysDelayTimeService;

	@Resource
	RedisClientTemplate redisClientTemplate;

	@Resource
	CommandSendLogService commandSendLogService;

	@Resource
	SysCommandSendLogService sysCommandSendLogService;


	@Override
	public void run() {

		logger.info("根据命令执行状态，进行操作，若未成功则重发命令");

		if(callBackDelay != null){

			String commandId = callBackDelay.getCommandId();
			String deviceId = callBackDelay.getDeviceId();
			String type = callBackDelay.getType();

			String jedisKey = deviceId + type;//拼接Redis内设备命令key

			String keyPrefix = "redis_command_" + type + ":" + jedisKey;//拼接Redis内设备命令区域key

			String keyPrefixCount = "redis_command_count_" + type + ":" + jedisKey;//拼接Redis内设备命令下发重复次数区域key

			nb_watermeter_equipment equipment = equipmentService.queryWMEinfoByDeviceId(deviceId);

			if(equipment != null){

				Map<String,Object> map = new HashMap<String,Object>();
				map.put("commandid", commandId);
				nb_command_send_log log = commandSendLogService.info(map);

				if(ObjectCode.NB_SUCCESSFUL.equals(log.getStatus())){

					logger.info("客户端命令下发成功删除命令" + type);

					redisClientTemplate.del(keyPrefix);//删除命令

					redisClientTemplate.del(keyPrefixCount);//删除命令重复次数
					
					if(nbIotConfig.getValve_control().equals(type)){
						//更新阀控状态
						CallBackObject  object = new JsonUtils().JsonToCallbackObject(log.getRcommand());
						if(object != null){

							optinControl(object);//更新阀控状态
						}
					}else{
						
						repeatCommand(type,keyPrefix,keyPrefixCount);//重发命令
					}
				}
			}

			tb_sys_equipment sysEquipment = sysEquipmentService.querySysWMEinfoByDeviceId(deviceId);

			if(sysEquipment != null){

				Map<String,Object> map = new HashMap<String,Object>();
				map.put("commandid", commandId);
				tb_sys_command_send_log log = sysCommandSendLogService.info(map);

				if(ObjectCode.NB_SUCCESSFUL.equals(log.getStatus())){

					logger.info(type + "命令重复下发成功删除命令");

					redisClientTemplate.del(keyPrefix);//删除命令

					redisClientTemplate.del(keyPrefixCount);//删除命令重复次数

					if(nbIotConfig.getValve_control().equals(type)){
						//更新阀控状态
						CallBackObject  object = new JsonUtils().JsonToCallbackObject(log.getRcommand());
						if(object != null){

							optinSysControl(object);
						}
					}

				}else{
					
					repeatCommand(type,keyPrefix,keyPrefixCount);
				}
			}
		}
	}
	
	/**
	 * 重复发送命令
	 * @param type 命令类型
	 * @param keyPrefix Redis内的命令值KEY
	 * @param keyPrefixCount Redis内的命令区域值KEY
	 */
	private void repeatCommand(String type,String keyPrefix,String keyPrefixCount){

		ByteSourceUtils byteSourceUtils = new ByteSourceUtils();

		byte[] bytesCount = redisClientTemplate.get(keyPrefixCount.getBytes());//获取Redis内的下发重复次数值

		if(bytesCount != null){

			int count = (int) byteSourceUtils.deserialize(bytesCount);//转化为整型

			if(count <= 2){//若命令重复下发次数小于2则继续下发命令

				count += 1;//命令重复次数累加

				redisClientTemplate.set(keyPrefixCount.getBytes(),byteSourceUtils.serialize(count));//更新Redis内的命令重复次数

				logger.info(type + "命令重复下发次数：" + count);

			}else{

				logger.info(type + "命令重复下发次数超过：" + count + "删除命令");
				redisClientTemplate.del(keyPrefix);//删除命令
				redisClientTemplate.del(keyPrefixCount);//删除命令重复次数
			}
		}else{

			logger.info(type + "命令重复下发次数为空则删除命令或计数");
			redisClientTemplate.del(keyPrefix);//删除命令
			redisClientTemplate.del(keyPrefixCount);//删除命令重复次数

		}
	
	}
	
	
	/**
	 * 客户端阀控状态更新
	 * @param object 响应命令体
	 */
	private void optinControl(CallBackObject  object ){
		
		ObjectClass o = new ObjectClass();

		JSONObject json1 = JSONObject.fromObject(object.getResult());
		JSONObject resultDetailJson = JSONObject.fromObject(json1.get("resultDetail"));
		JSONObject reportDataJson = JSONObject.fromObject(resultDetailJson.get("reportData"));
		JSONObject serviceDataJson = JSONObject.fromObject(reportDataJson.get("serviceData"));
		JSONObject jsonObject = JSONObject.fromObject(serviceDataJson.get("reportData"));
		ReportDataHAC rdata = (ReportDataHAC)JSONObject.toBean(jsonObject, ReportDataHAC.class); 

		Map<String,Object>[] devmap = rdata.getDev();
		
		for(int i = 0;i< devmap.length;i++){

			Map<String,Object> mapd = (devmap[i]);

			String bn = (mapd.get("bn")).toString();

			if(bn.equals(ObjectCode.VALVE_CONTROL_CODE)){

				ValveControl value = o.MapToValue(mapd);

				logger.info("客户端更新阀门状态：" + value.toString());

				if("0".equals(value.getValveCurrentStatus())){

					remoteService.open(object.getDeviceId());
				}
				if("1".equals(value.getValveCurrentStatus())){

					remoteService.close(object.getDeviceId());
				}
			}
		}

	}

	/**
	 * 系统端阀控更新
	 * @param object
	 */
	private void optinSysControl(CallBackObject  object ){
		
		ObjectClass o = new ObjectClass();

		JSONObject json1 = JSONObject.fromObject(object.getResult());
		JSONObject resultDetailJson = JSONObject.fromObject(json1.get("resultDetail"));
		JSONObject reportDataJson = JSONObject.fromObject(resultDetailJson.get("reportData"));
		JSONObject serviceDataJson = JSONObject.fromObject(reportDataJson.get("serviceData"));
		JSONObject jsonObject = JSONObject.fromObject(serviceDataJson.get("reportData"));
		ReportDataHAC rdata = (ReportDataHAC)JSONObject.toBean(jsonObject, ReportDataHAC.class); 

		Map<String,Object>[] devmap = rdata.getDev();

		for(int i = 0;i< devmap.length;i++){

			Map<String,Object> mapd = (devmap[i]);

			String bn = (mapd.get("bn")).toString();

			if(bn.equals(ObjectCode.VALVE_CONTROL_CODE)){

				ValveControl value = o.MapToValue(mapd);

				logger.info("系统更新阀门状态：" + value.toString());

				if("0".equals(value.getValveCurrentStatus())){

					sysEquipmentService.open(object.getDeviceId());
				}
				if("1".equals(value.getValveCurrentStatus())){

					sysEquipmentService.close(object.getDeviceId());
				}
			}
		}

	}

	public CallBackDelay getCallBackDelay() {
		return callBackDelay;
	}

	public void setCallBackDelay(CallBackDelay callBackDelay) {
		this.callBackDelay = callBackDelay;
	}
}