package com.ets.business.nb_iot.cmdinfo.command.send;

import com.ets.business.commandsendlog.entity.nb_command_send_log;
import com.ets.business.commandsendlog.service.CommandSendLogService;
import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.equipment.service.EquipmentService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.ets.business.nb_iot.cmdinfo.iotinit.NbIotConfig;
import com.ets.business.nb_iot.hac.model.ReportDataHAC;
import com.ets.business.nb_iot.tlv.model.ReportDataTLV;
import com.ets.common.DateTimeUtils;
import com.ets.system.shiro.cache.RedisClientTemplate;
import com.ets.system.sysCommandSendLog.entity.tb_sys_command_send_log;
import com.ets.system.sysCommandSendLog.service.SysCommandSendLogService;
import com.ets.system.sysEquipment.entity.tb_sys_equipment;
import com.ets.system.sysEquipment.service.SysEquipmentService;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.CommandDTOV4;
import com.iotplatform.client.dto.PostDeviceCommandInDTO2;
import com.iotplatform.client.dto.PostDeviceCommandOutDTO2;
import com.iotplatform.client.invokeapi.SignalDelivery;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class CommandService {

	private static Logger logger = LoggerFactory.getLogger(CommandService.class);

	@Resource
	EquipmentService equipmentService;

	@Resource
	SysEquipmentService sysEquipmentService;

	@Autowired
	NbIotConfig nbIotConfig;

	@Autowired
	IntiClient initClient;

	@Autowired
	RedisClientTemplate redisClientTemplate;

	@Autowired
	CommandSendLogService commandSendLogService;

	@Resource
	SysCommandSendLogService sysCommandSendLogService;

	NorthApiClient northApiClient ;
	SignalDelivery signalDelivery ;
	String accessToken ;

	private void init()
	{
		northApiClient = initClient.GetNorthApiClient();
		signalDelivery = new SignalDelivery(northApiClient);
		accessToken = initClient.getAccessToken();
	}


	/**
	 * 发送命令
	 * @param signalDelivery
	 * @param deviceId
	 * @param accessToken
	 * @param cmd
	 * @return
	 */
	public PostDeviceCommandOutDTO2 postCommandTLV(SignalDelivery signalDelivery, String deviceId, String accessToken,CommandDTOV4 cmd,String method) {
		try {
			PostDeviceCommandInDTO2 pdcInDTO = new PostDeviceCommandInDTO2();
			pdcInDTO.setDeviceId(deviceId);
			pdcInDTO.setExpireTime(0); //立即下发
			pdcInDTO.setMaxRetransmit(3); //最大重传次数
			pdcInDTO.setCommand(cmd);
			if(method.equals("SETRAW")){
				pdcInDTO.setCallbackUrl(nbIotConfig.getCallback_tlv_basic_url());
			}

			return signalDelivery.postDeviceCommand(pdcInDTO, null, accessToken);
		} catch (NorthApiException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 发送命令
	 * @param signalDelivery
	 * @param deviceId
	 * @param accessToken
	 * @param cmd
	 * @return
	 */
	public PostDeviceCommandOutDTO2 postCommand(SignalDelivery signalDelivery, String deviceId, String accessToken,CommandDTOV4 cmd,String method) {
		try {
			PostDeviceCommandInDTO2 pdcInDTO = new PostDeviceCommandInDTO2();
			pdcInDTO.setDeviceId(deviceId);
			pdcInDTO.setExpireTime(0); //立即下发
			pdcInDTO.setMaxRetransmit(3); //最大重传次数
			pdcInDTO.setCommand(cmd);
			if(method.equals(nbIotConfig.getValve_control())){
				pdcInDTO.setCallbackUrl(nbIotConfig.getCallback_valve_control_url());
			}else if(method.equals(nbIotConfig.getWater_meter_basic())){
				pdcInDTO.setCallbackUrl(nbIotConfig.getCallback_water_meter_basic_url());
			}else if(method.equals(nbIotConfig.getDelivery())){
				pdcInDTO.setCallbackUrl(nbIotConfig.getCallback_delivery_url());
			}else if(method.equals(nbIotConfig.getCheck_time())){
				pdcInDTO.setCallbackUrl(nbIotConfig.getCallback_check_time_url());
			}else{
				pdcInDTO.setCallbackUrl(null);
			}

			return signalDelivery.postDeviceCommand(pdcInDTO, null, accessToken);
		} catch (NorthApiException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 组装校时命令,并发送命令
	 * @param deviceId 设备ID
	 * @param reportData 命令体
	 */
	public void CheckTime(String deviceId,ReportDataHAC reportData){

		try {
			if(reportData != null){

				init();
				CommandDTOV4 cmd = new CommandDTOV4();
				cmd.setServiceId(nbIotConfig.getService_name());
				cmd.setMethod(nbIotConfig.getCheck_time()); //"PUT" is the command name defined in the profile
				Map<String, Object> cmdParam = new HashMap<String, Object>();
				cmdParam.put(nbIotConfig.getAttribute_name(), reportData);
				cmd.setParas(cmdParam);

				PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd,nbIotConfig.getCheck_time());
				if (pdcOutDTO != null) {
					//logger.info("校时命令体："+pdcOutDTO.getCommand().getParas());
					//logger.info("校时命令返回结果：="+pdcOutDTO.getResult());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 组装阀控命令,并发送命令
	 * @param deviceId 设备ID
	 * @param reportData 命令体
	 */
	public void AssemblyValveControlCommand(String deviceId,ReportDataHAC reportData){




		try {

			String jedisKey = deviceId + nbIotConfig.getValve_control();

			nb_watermeter_equipment e = equipmentService.queryWMEinfoByDeviceId(deviceId);

			tb_sys_equipment sysEquipment = sysEquipmentService.querySysWMEinfoByDeviceId(deviceId);

			if(reportData != null){
				init();
				CommandDTOV4 cmd = new CommandDTOV4();
				cmd.setServiceId(nbIotConfig.getService_name());
				cmd.setMethod(nbIotConfig.getValve_control()); //"PUT" is the command name defined in the profile
				Map<String, Object> cmdParam = new HashMap<String, Object>();
				cmdParam.put(nbIotConfig.getAttribute_name(), reportData);
				cmd.setParas(cmdParam);

				PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd,nbIotConfig.getValve_control());
				if (pdcOutDTO != null) {

					logger.info("pdcOutDTO:========AssemblyValveControlCommand==========:"+pdcOutDTO.toString());
					
					String commandId = pdcOutDTO.getCommandId();

					if(e != null){
						nb_command_send_log cmdlog = new nb_command_send_log();
						cmdlog.setCustomercode(e.getCustomercode());
						cmdlog.setDeviceid(deviceId);
						cmdlog.setImei(e.getWatermetercode());
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setStatus(pdcOutDTO.getStatus());
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						commandSendLogService.addCommandSendLog(cmdlog);
					}

					if(sysEquipment != null){
						tb_sys_command_send_log cmdlog = new tb_sys_command_send_log();
						cmdlog.setDeviceid(deviceId);
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setImei(sysEquipment.getImei());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setStatus(pdcOutDTO.getStatus());
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						sysCommandSendLogService.addSysCommandSendLog(cmdlog);
					}


					//logger.info("阀控命令体："+JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
					//logger.info("阀控命令返回结果：="+pdcOutDTO.getResult());
					//String keyPrefix = "shiro_redis_command_" + nbIotConfig.getValve_control()+ ":" + jedisKey;
					//redisClientTemplate.del(keyPrefix);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 组装上报周期命令,并发送命令
	 * @param deviceId 设备ID
	 * @param reportData 命令体
	 */
	public void AssemblyDeliveryCommand(String deviceId,ReportDataHAC reportData){
		try {


			String jedisKey = deviceId + nbIotConfig.getDelivery();
			nb_watermeter_equipment e = equipmentService.queryWMEinfoByDeviceId(deviceId);
			tb_sys_equipment sysEquipment = sysEquipmentService.querySysWMEinfoByDeviceId(deviceId);

			if(reportData != null){
				init();
				CommandDTOV4 cmd = new CommandDTOV4();
				cmd.setServiceId(nbIotConfig.getService_name());
				cmd.setMethod(nbIotConfig.getDelivery()); 
				Map<String, Object> cmdParam = new HashMap<String, Object>();
				cmdParam.put(nbIotConfig.getAttribute_name(), reportData);
				cmd.setParas(cmdParam);


				PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd,nbIotConfig.getDelivery());
				if (pdcOutDTO != null) {
					
					logger.info("pdcOutDTO:========AssemblyDeliveryCommand==========:"+pdcOutDTO.toString());

					String commandId = pdcOutDTO.getCommandId();

					if(e != null){
						nb_command_send_log cmdlog = new nb_command_send_log();
						cmdlog.setCustomercode(e.getCustomercode());
						cmdlog.setImei(e.getWatermetercode());
						cmdlog.setDeviceid(deviceId);
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setStatus(pdcOutDTO.getStatus());
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						commandSendLogService.addCommandSendLog(cmdlog);
					}

					if(sysEquipment != null){
						tb_sys_command_send_log cmdlog = new tb_sys_command_send_log();
						cmdlog.setDeviceid(deviceId);
						cmdlog.setImei(sysEquipment.getImei());
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setStatus(pdcOutDTO.getStatus());
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						sysCommandSendLogService.addSysCommandSendLog(cmdlog);
					}

					//logger.info("上报周期命令体："+JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
					//logger.info("上报周期命令返回结果：="+pdcOutDTO.getResult());
					//String keyPrefix = "shiro_redis_command_" + nbIotConfig.getDelivery()+ ":" + jedisKey;
					//redisClientTemplate.del(keyPrefix);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 组装表读数命令,并发送命令
	 * @param deviceId 设备ID
	 * @param reportData 命令体
	 */
	public void AssemblyWaterMeterBasicCommand(String deviceId,ReportDataHAC reportData){
		try {


			String jedisKey = deviceId + nbIotConfig.getWater_meter_basic();

			nb_watermeter_equipment e = equipmentService.queryWMEinfoByDeviceId(deviceId);
			tb_sys_equipment sysEquipment = sysEquipmentService.querySysWMEinfoByDeviceId(deviceId);

			if(reportData != null){
				init();
				CommandDTOV4 cmd = new CommandDTOV4();
				cmd.setServiceId(nbIotConfig.getService_name());
				cmd.setMethod(nbIotConfig.getWater_meter_basic()); //"PUT" is the command name defined in the profile
				Map<String, Object> cmdParam = new HashMap<String, Object>();
				cmdParam.put(nbIotConfig.getAttribute_name(), reportData);
				cmd.setParas(cmdParam);

				PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd,nbIotConfig.getWater_meter_basic());

				if (pdcOutDTO != null) {
					
					logger.info("pdcOutDTO:========AssemblyWaterMeterBasicCommand==========:"+pdcOutDTO.toString());

					String commandId = pdcOutDTO.getCommandId();

					if(e != null){
						nb_command_send_log cmdlog = new nb_command_send_log();
						cmdlog.setImei(e.getWatermetercode());
						cmdlog.setCustomercode(e.getCustomercode());
						cmdlog.setDeviceid(deviceId);
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setStatus(pdcOutDTO.getStatus());
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						commandSendLogService.addCommandSendLog(cmdlog);
					}

					if(sysEquipment != null){
						tb_sys_command_send_log cmdlog = new tb_sys_command_send_log();
						cmdlog.setDeviceid(deviceId);
						cmdlog.setImei(sysEquipment.getImei());
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						cmdlog.setStatus(pdcOutDTO.getStatus());
						sysCommandSendLogService.addSysCommandSendLog(cmdlog);
					}

					//logger.info("设置读数命令体："+JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
					//logger.info("设置读数命令返回结果：="+pdcOutDTO.getResult());
					//String keyPrefix = "shiro_redis_command_" + nbIotConfig.getWater_meter_basic()+ ":" + jedisKey;
					//redisClientTemplate.del(keyPrefix);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void AssemblyTLVBasicCommand(String deviceId, ReportDataTLV reportData) {

		try {


			String jedisKey = deviceId + "SETRAW";

			nb_watermeter_equipment e = equipmentService.queryWMEinfoByDeviceId(deviceId);

			tb_sys_equipment sysEquipment = sysEquipmentService.querySysWMEinfoByDeviceId(deviceId);

			if(reportData != null){
				init();
				CommandDTOV4 cmd = new CommandDTOV4();
				cmd.setServiceId("Reading");
				cmd.setMethod("SETRAW"); //"PUT" is the command name defined in the profile
				Map<String, Object> cmdParam = new HashMap<String, Object>();
				cmdParam.put(nbIotConfig.getAttribute_name(), reportData);
				cmd.setParas(cmdParam);

				PostDeviceCommandOutDTO2 pdcOutDTO = postCommandTLV(signalDelivery, deviceId, accessToken,cmd,"SETRAW");

				if (pdcOutDTO != null) {
					
					logger.info("pdcOutDTO:========AssemblyTLVBasicCommand==========:"+pdcOutDTO.toString());

					String commandId = pdcOutDTO.getCommandId();

					if(e != null){
						nb_command_send_log cmdlog = new nb_command_send_log();
						cmdlog.setImei(e.getWatermetercode());
						cmdlog.setCustomercode(e.getCustomercode());
						cmdlog.setDeviceid(deviceId);
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						cmdlog.setStatus(pdcOutDTO.getStatus());
						commandSendLogService.addCommandSendLog(cmdlog);
					}

					if(sysEquipment != null){
						tb_sys_command_send_log cmdlog = new tb_sys_command_send_log();
						cmdlog.setDeviceid(deviceId);
						cmdlog.setImei(sysEquipment.getImei());
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						cmdlog.setStatus(pdcOutDTO.getStatus());
						sysCommandSendLogService.addSysCommandSendLog(cmdlog);
					}

					//String keyPrefix = "shiro_redis_command_SETRAW:" + jedisKey;
					//redisClientTemplate.del(keyPrefix);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public void CheckTimeTLV(String deviceId, ReportDataTLV reportData) {

		try {

			nb_watermeter_equipment e = equipmentService.queryWMEinfoByDeviceId(deviceId);

			tb_sys_equipment sysEquipment = sysEquipmentService.querySysWMEinfoByDeviceId(deviceId);

			if(reportData != null){

				init();
				CommandDTOV4 cmd = new CommandDTOV4();
				cmd.setServiceId(nbIotConfig.getService_name());
				cmd.setMethod(nbIotConfig.getCheck_time()); //"PUT" is the command name defined in the profile
				Map<String, Object> cmdParam = new HashMap<String, Object>();
				cmdParam.put(nbIotConfig.getAttribute_name(), reportData);
				cmd.setParas(cmdParam);

				PostDeviceCommandOutDTO2 pdcOutDTO = postCommandTLV(signalDelivery, deviceId, accessToken,cmd,nbIotConfig.getCheck_time());

				if (pdcOutDTO != null) {
					
					logger.info("pdcOutDTO:========CheckTimeTLV==========:"+pdcOutDTO.toString());

					String commandId = pdcOutDTO.getCommandId();

					if(e != null){
						nb_command_send_log cmdlog = new nb_command_send_log();
						cmdlog.setImei(e.getWatermetercode());
						cmdlog.setCustomercode(e.getCustomercode());
						cmdlog.setDeviceid(deviceId);
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						cmdlog.setStatus(pdcOutDTO.getStatus());
						commandSendLogService.addCommandSendLog(cmdlog);
					}

					if(sysEquipment != null){
						tb_sys_command_send_log cmdlog = new tb_sys_command_send_log();
						cmdlog.setDeviceid(deviceId);
						cmdlog.setImei(sysEquipment.getImei());
						cmdlog.setSendtime(DateTimeUtils.getnowdate());
						cmdlog.setCommandid(commandId);
						cmdlog.setCommand(JSONObject.fromObject(pdcOutDTO.getCommand()).toString());
						cmdlog.setSendtime(DateTimeUtils.getTime(pdcOutDTO.getPlatformIssuedTime()));
						cmdlog.setExecutetime(pdcOutDTO.getExecuteTime());
						cmdlog.setStatus(pdcOutDTO.getStatus());
						sysCommandSendLogService.addSysCommandSendLog(cmdlog);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
