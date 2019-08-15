package com.ets.business.remote.web;

import com.ets.business.commandsendlog.entity.nb_command_send_log;
import com.ets.business.commandsendlog.service.CommandSendLogService;
import com.ets.business.init.daelaytime.entity.nb_delay_time_basicnum;
import com.ets.business.init.daelaytime.entity.nb_delay_time_delivery;
import com.ets.business.init.daelaytime.entity.nb_delay_time_valvecontrol;
import com.ets.business.init.daelaytime.service.DelayTimeService;
import com.ets.business.logopr.entity.tb_log_opr_customer;
import com.ets.business.logopr.service.LogOprCustomerService;
import com.ets.business.nb_iot.cmdinfo.command.uitls.TemplateUtils;
import com.ets.business.nb_iot.cmdinfo.iotinit.BathTaskService;
import com.ets.business.nb_iot.cmdinfo.iotinit.DataCollectionService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.ets.business.nb_iot.cmdinfo.iotinit.NbIotConfig;
import com.ets.business.nb_iot.cmdinfo.iotinit.SignalDeliveryService;
import com.ets.business.readlog.entity.nb_read_log;
import com.ets.business.readlog.service.ReadLogService;
import com.ets.business.region.residential.service.ResidentialService;
import com.ets.business.remote.entity.DeviceInfoEntity;
import com.ets.business.remote.entity.HistoryCmdEntity;
import com.ets.business.remote.entity.HistoryDataEntity;
import com.ets.business.remote.entity.RemoteListEntity;
import com.ets.business.remote.service.RemoteService;
import com.ets.common.DateTimeUtils;
import com.ets.common.PageListData;
import com.ets.common.dtree.DtreeEntity;
import com.ets.dictionary.district.entity.tb_area;
import com.ets.dictionary.district.entity.tb_city;
import com.ets.dictionary.district.entity.tb_province;
import com.ets.dictionary.district.service.AreaService;
import com.ets.dictionary.district.service.CityService;
import com.ets.dictionary.district.service.ProvinceService;
import com.ets.system.cus.entity.nb_cus;
import com.ets.system.cus_region.entity.tb_cus_region_area;
import com.ets.system.cus_region.entity.tb_cus_region_city;
import com.ets.system.cus_region.entity.tb_cus_region_province;
import com.ets.system.cus_region.service.CusRegionSerivce;
import com.ets.system.shiro.cache.RedisClientTemplate;
import com.ets.utils.Message;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.*;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2018- 11-20 11:15
 */
@Controller
@RequestMapping("remote")
public class RemoteController {

	@Resource
	CommandSendLogService commandSendLogService;

	@Resource
	ReadLogService readLogService;

	@Resource
	AreaService areaService;

	@Resource
	CityService cityService;

	@Resource
	ProvinceService provinceService;

	@Autowired
	NbIotConfig nbIotConfig;

	@Autowired
	RemoteService remoteService;

	@Autowired
	DataCollectionService dataCollectionService;

	@Autowired
	BathTaskService bathTaskService;

	@Autowired
	IntiClient initClient;

	@Autowired
	SignalDeliveryService signalDeliveryService;

	@Resource
	LogOprCustomerService logOprCustomerService;

	@Resource
	DelayTimeService delayTimeService;
	
	@Autowired
    RedisClientTemplate redisClientTemplate;

    @Resource
    CusRegionSerivce cusRegionSerivce;

	String baseUrl = "business/remote/";

	String modulename = "客户管理-设备管理-远程控制";

	//将"省份,城市,地区"拼接在一起
	String provinceCityArea;

	@RequestMapping("tree")
	public String input(HttpServletRequest request)
	{
		return "business/remote/remote-tree";
	}

	@RequestMapping(value="treeData" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public DtreeEntity tree(HttpServletRequest request,String nodeId , String parentId , String isLeaf , String context , String level,String spared)
	{
		Map loginMap = (Map) SecurityUtils.getSubject().getSession().getAttribute("userSession");
		String cusid = (String)loginMap.get("CID"); //从session里获取客户ID
		List<tb_cus_region_province> regionP = cusRegionSerivce.queryCusRegionProvince(cusid);
		List<tb_cus_region_city> regionC = cusRegionSerivce.queryCusRegionCity(cusid,regionP.get(0).getProvinceid());
		parentId = regionC.get(0).getCityid();
		List<tb_cus_region_area> regionA = cusRegionSerivce.queryCusRegionArea(cusid,regionC.get(0).getCityid());
		DtreeEntity dtreeEntity = null;
		dtreeEntity = remoteService.getAreas(parentId,regionA);

/*		if(level==null)
		{
			dtreeEntity = remoteService.getProvinces(regionP);
		}
		else if(level.equals("1")){
			dtreeEntity = remoteService.getCitys(parentId,regionC);
		}
		else if(level.equals("2"))
		{
			dtreeEntity = remoteService.getAreas(parentId,regionA);
		}*/

		return dtreeEntity;
	}

	@RequestMapping("list")
	public String list(String areaId,HttpServletRequest request,String parentId)
	{

		tb_area area=areaService.infoArea(areaId);
		tb_city city=cityService.infoCityid(parentId);
		String proid=cityService.findProvinceIdByCityId(parentId);
		tb_province province=provinceService.infoProvinceid(proid);
		provinceCityArea="["+province.getProvince()+","+city.getCity()+","+area.getArea()+"]";

		//将"查看区域列表"信息记录到操作日志
		tb_log_opr_customer log = new tb_log_opr_customer();
		log.setIp(logOprCustomerService.getIp(request));//ip地址
		log.setModulename(modulename+provinceCityArea);
		log.setOprcontent("查看区域列表列表");
		nb_cus cus=(nb_cus)SecurityUtils.getSubject().getSession().getAttribute("customerSession");
		log.setCustomercode(cus.getId());
		logOprCustomerService.addLog(log);

		request.setAttribute("areaId",areaId);
		return "business/remote/remote-list";
	}

	@RequestMapping(value="listData" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public PageListData<RemoteListEntity> listData(int page,int limit , String areaId,String useraccount,String username)
	{
		Map loginMap = (Map) SecurityUtils.getSubject().getSession().getAttribute("userSession");
		String cid = (String)loginMap.get("CID"); //从session里获取客户ID

		//获取此客户 此地区的用户列表
		Map<String,Object>  map = new HashMap<String,Object>();
		map.put("cid", cid);
		map.put("areaId", areaId);
		map.put("page", (page)*limit);//oracle
		map.put("limit", (page-1)*limit);//oracle
		
		map.put("useraccount", useraccount);//户号
		map.put("username", username);//业主名
		List<RemoteListEntity> remoteList = remoteService.list(map);

		long count = remoteService.getCount(map);

		PageListData<RemoteListEntity> pageData = new PageListData<RemoteListEntity>();

		pageData.setCode("0");
		pageData.setCount(count);
		pageData.setMessage("");
		pageData.setData(remoteList);

		//Gson gson = new Gson();
		//String listJson = gson.toJson(pageData);
		return pageData;
	}

	@RequestMapping("history-data-list")
	public String historyDataList(String deviceId,HttpServletRequest request)
	{
		request.setAttribute("deviceId",deviceId);
		return "business/remote/remote-history-data";
	}

	@RequestMapping("history-cmd-list")
	public String historyCmdList(String deviceId,HttpServletRequest request)
	{
		request.setAttribute("deviceId",deviceId);
		return "business/remote/remote-history-cmd";
	}

	@RequestMapping(value="history-data-data" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String deviceHistoryData(HttpServletRequest request , String deviceId ,int page,int limit)
	{
		Map<?, ?> loginMap = (Map<?, ?>)SecurityUtils.getSubject().getSession().getAttribute("userSession");
		String customerId = (String)loginMap.get("CID");
		Gson gson = new Gson();
		try {
			//System.out.println("page="+page+",limit="+limit);
			Map<String,Object> map = new HashMap<String,Object>();
			//			map.put("page", (page-1)*limit);//mysql
			//			map.put("limit", limit);//mysql
			map.put("page", (page)*limit);//oracle
			map.put("limit", (page-1)*limit);//oracle
			map.put("customercode", customerId);//客户ID

			map.put("deviceid", deviceId);//设备ID
			List<nb_read_log> comsend = readLogService.getReadLog(map);
			long count = readLogService.getCount(map);


			PageListData<nb_read_log> pageData = new PageListData<nb_read_log>();

			pageData.setCode("0");
			pageData.setCount(count);
			pageData.setMessage("");
			pageData.setData(comsend);


			String listJson = gson.toJson(pageData);
			return listJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  null;
	}

	@RequestMapping(value="history-cmd-data" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String deviceHistoryCmd(HttpServletRequest request , String deviceId ,int page,int limit)
	{
		Map<?, ?> loginMap = (Map<?, ?>)SecurityUtils.getSubject().getSession().getAttribute("userSession");
		String customerId = (String)loginMap.get("CID");
		Gson gson = new Gson();
		try {
			//System.out.println("page="+page+",limit="+limit);
			Map<String,Object> map = new HashMap<String,Object>();
			//			map.put("page", (page-1)*limit);//mysql
			//			map.put("limit", limit);//mysql
			map.put("page", (page)*limit);//oracle
			map.put("limit", (page-1)*limit);//oracle
			map.put("customercode", customerId);//客户ID

			map.put("deviceid", deviceId);//设备ID
			List<nb_command_send_log> comsend = commandSendLogService.getCommandSendLog(map);
			long count = commandSendLogService.getCount(map);


			PageListData<nb_command_send_log> pageData = new PageListData<nb_command_send_log>();

			pageData.setCode("0");
			pageData.setCount(count);
			pageData.setMessage("");
			pageData.setData(comsend);


			String listJson = gson.toJson(pageData);
			return listJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping("device-info")
	public String deviceInfo(HttpServletRequest request , String deviceId)
	{
		try {
			QuerySingleDeviceInfoOutDTO querySingleDeviceInfoOutDTO = dataCollectionService.querySingleDeviceInfo(deviceId,null);
			DeviceInfo deviceInfo = querySingleDeviceInfoOutDTO.getDeviceInfo();

			DeviceInfoEntity entity = new DeviceInfoEntity();

			entity.setDeviceId(querySingleDeviceInfoOutDTO.getDeviceId());
			entity.setCreateTime(querySingleDeviceInfoOutDTO.getCreateTime());
			entity.setGatewayId(querySingleDeviceInfoOutDTO.getGatewayId());
			entity.setNodeType(querySingleDeviceInfoOutDTO.getNodeType());

			entity.setNodeId(deviceInfo.getNodeId());
			entity.setName(deviceInfo.getName());
			entity.setDescription(deviceInfo.getDescription());
			entity.setManufacturerId(deviceInfo.getManufacturerId());
			entity.setManufacturerName(deviceInfo.getManufacturerName());
			entity.setMac(deviceInfo.getMac());
			entity.setLocation(deviceInfo.getLocation());
			entity.setDeviceType(deviceInfo.getDeviceType());
			entity.setModel(deviceInfo.getModel());
			entity.setSwVersion(deviceInfo.getSwVersion());
			entity.setFwVersion(deviceInfo.getFwVersion());
			entity.setHwVersion(deviceInfo.getHwVersion());
			entity.setProtocolType(deviceInfo.getProtocolType());
			entity.setBridgeId(deviceInfo.getBridgeId());
			entity.setStatus(deviceInfo.getStatus());
			entity.setStatusDetail(deviceInfo.getStatusDetail());
			entity.setMute(deviceInfo.getMute());
			entity.setSupportedSecurity(deviceInfo.getSupportedSecurity());
			entity.setIsSecurity(deviceInfo.getIsSecurity());
			entity.setSignalStrength(deviceInfo.getSignalStrength());
			entity.setSigVersion(deviceInfo.getSigVersion());
			entity.setSerialNumber(deviceInfo.getSerialNumber());
			entity.setBatteryLevel(deviceInfo.getBatteryLevel());
			request.setAttribute("info",entity);

		} catch (NorthApiException e) {
			e.printStackTrace();
		}
		return "business/remote/remote-device-info";
	}

	/**
	 * 开水阀
	 * @return
	 */
	@RequestMapping(value="open" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String open(HttpServletRequest request ,String id[],String[] initNames,String[] doornums)
	{

		int value = 0;
		//将所有的小区名拼接
		StringBuilder sb1=new StringBuilder();
		for(String initName:initNames)
		{
			sb1.append(initName+",");
		}
		//将所有户名拼接
		StringBuilder sb2=new StringBuilder();
		for(String doornum:doornums)
		{
			sb2.append(doornum+",");
		}

		//将小区开阀操作记录到操作日志
		tb_log_opr_customer log=new tb_log_opr_customer();
		log.setIp(logOprCustomerService.getIp(request));//ip地址
		log.setModulename(modulename+provinceCityArea);
		log.setOprcontent("为小区["+sb1.substring(0,sb1.length()-2)+"]户号["+sb2.substring(0,sb2.length()-2)+"]开阀");
		nb_cus cus=(nb_cus)SecurityUtils.getSubject().getSession().getAttribute("customerSession");
		log.setCustomercode(cus.getId());
		logOprCustomerService.addLog(log);
/*		long delayTime = getDelayTime(nbIotConfig.getValve_control());
		long delayTime1 = getDelayTime(nbIotConfig.getDelivery());*/
		Gson gson = new Gson();
		for(String deviceId : id)
		{
			try {
/*				String dbTime = commandSendLogService.queryNewestTime(deviceId);
				boolean fig = DateTimeUtils.judgmentDate(dbTime,DateTimeUtils.getnowdate());
				if(fig){
					System.out.println("若最新上报时间上报时间距离当前时间小于24小时测立即执行命令");
					signalDeliveryService.optionWM(deviceId,nbIotConfig.getValve_control(),value,new TemplateUtils(),delayTime);
					String times = String.valueOf(43200*3600);//开发后没12小时上报一次数据
					signalDeliveryService.reportCycle(deviceId,nbIotConfig.getValve_control(),times,new TemplateUtils(),delayTime + delayTime1);
				}else{
					
				}*/
				signalDeliveryService.optionWMJedis(deviceId,nbIotConfig.getValve_control(),value);
				String times = String.valueOf(43200*3600);//开发后没12小时上报一次数据
				signalDeliveryService.reportCycleJedis(deviceId,times,nbIotConfig.getDelivery());
				//remoteService.update(deviceId);
			} catch (Exception e) {
				e.printStackTrace();
				return gson.toJson(new Message("0","开阀失败!"));
			}
		}
		return gson.toJson(new Message("1","开阀命令缓存成功!等待执行。。。"));
	}

	/**
	 * 关闭水阀
	 * @return
	 */
	@RequestMapping(value="close" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String close(HttpServletRequest request ,String id[],String[] initNames,String[] doornums)
	{
		int value = 1;
		//将所有的小区名拼接
		StringBuilder sb1=new StringBuilder();
		for(String initName:initNames)
		{
			sb1.append(initName+",");
		}
		//将所有户名拼接
		StringBuilder sb2=new StringBuilder();
		for(String doornum:doornums)
		{
			sb2.append(doornum+",");
		}

		//将小区关阀操作记录到操作日志
		tb_log_opr_customer log=new tb_log_opr_customer();
		log.setModulename(modulename+provinceCityArea);
		log.setIp(logOprCustomerService.getIp(request));//ip地址
		log.setOprcontent("为小区["+sb1.substring(0,sb1.length()-2)+"]户号["+sb2.substring(0,sb2.length()-2)+"]关阀");
		nb_cus cus=(nb_cus)SecurityUtils.getSubject().getSession().getAttribute("customerSession");
		log.setCustomercode(cus.getId());
		logOprCustomerService.addLog(log);
		/*long delayTime = getDelayTime(nbIotConfig.getValve_control());
		long delayTime1 = getDelayTime(nbIotConfig.getDelivery());*/
		Gson gson = new Gson();
		for(String deviceId : id)
		{
			try {
/*				String dbTime = commandSendLogService.queryNewestTime(deviceId);
				boolean fig = DateTimeUtils.judgmentDate(dbTime,DateTimeUtils.getnowdate());
				if(fig){
					System.out.println("若最新上报时间上报时间距离当前时间小于24小时测立即执行命令");
					signalDeliveryService.optionWM(deviceId,nbIotConfig.getValve_control(),value,new TemplateUtils(),delayTime);
					String times = String.valueOf(15*3600);//关阀后每15分钟上报一次
					signalDeliveryService.reportCycle(deviceId,nbIotConfig.getValve_control(),times,new TemplateUtils(),delayTime + delayTime1);
				}else{

				}*/
				signalDeliveryService.optionWMJedis(deviceId,nbIotConfig.getValve_control(),value);
				String times = String.valueOf(15*3600);//关阀后每15分钟上报一次
				signalDeliveryService.reportCycleJedis(deviceId,times,nbIotConfig.getDelivery());
				//remoteService.update(deviceId);
			} catch (Exception e) {
				e.printStackTrace();
				return gson.toJson(new Message("0","开阀失败!"));
			}
		}
		return gson.toJson(new Message("1","关阀命令缓存成功成功!等待执行。。。"));

	}

	@RequestMapping("toReportCycle")
	public String input(HttpServletRequest request,String ids,String initNames,String doornums)
	{
		request.setAttribute("ids", ids);
		request.setAttribute("initNames", initNames);
		request.setAttribute("doornums", doornums);
		tb_log_opr_customer log = new tb_log_opr_customer();
		log.setModulename(modulename+provinceCityArea);
		log.setIp(logOprCustomerService.getIp(request));//ip地址
		log.setOprcontent("访问设置上报时间页面");
		nb_cus cus=(nb_cus)SecurityUtils.getSubject().getSession().getAttribute("customerSession");
		log.setCustomercode(cus.getId());
		logOprCustomerService.addLog(log);
		return baseUrl+"remote-reportCycle";
	}

	/**
	 * 上报时间
	 * @return
	 */
	@RequestMapping(value="reportCycle" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String reportCycle(HttpServletRequest request ,String id[],String time,String initNames,String doornums)
	{
		String times = "900";
		if(!"0".equals(time)){
			double a = Double.valueOf(time) * 3600;
			int b = (int) a;
			times = String.valueOf(b);
		}

/*		long delayTime = getDelayTime(nbIotConfig.getDelivery());*/
		//将设置上报周期操作记录到操作日志
		tb_log_opr_customer log=new tb_log_opr_customer();
		log.setModulename(modulename+provinceCityArea);
		log.setIp(logOprCustomerService.getIp(request));//ip地址
		log.setOprcontent("为小区["+initNames.substring(0,initNames.length()-1)+"]户号["
				+doornums.substring(0,doornums.length()-1)+"]设置上报周期[上报周期:"+time+"]小时");
		nb_cus cus=(nb_cus)SecurityUtils.getSubject().getSession().getAttribute("customerSession");
		log.setCustomercode(cus.getId());
		logOprCustomerService.addLog(log);

		Gson gson = new Gson();
		for(String deviceId : id)
		{
			try {
/*				String dbTime = commandSendLogService.queryNewestTime(deviceId);
				boolean fig = DateTimeUtils.judgmentDate(dbTime,DateTimeUtils.getnowdate());
				if(fig){
					System.out.println("若最新上报时间上报时间距离当前时间小于24小时测立即执行命令");
					new TemplateUtils().CheckTime(deviceId);
					signalDeliveryService.reportCycle(deviceId,nbIotConfig.getValve_control(),times,new TemplateUtils(),delayTime);
				}else{
					
				}*/
				signalDeliveryService.reportCycleJedis(deviceId,times,nbIotConfig.getDelivery());
			} catch (Exception e) {
				e.printStackTrace();
				return gson.toJson(new Message("0","设置上报周期失败!"));
			}
		}
		return gson.toJson(new Message("1","设置上报周期命令缓存成功!等待执行。。。"));

	}

	@RequestMapping("toBasicNum")
	public String toBasicNum(HttpServletRequest request,String ids,String initNames,String doornums)
	{

		request.setAttribute("ids", ids);
		request.setAttribute("initNames", initNames);
		request.setAttribute("doornums", doornums);
		return baseUrl+"remote-basicnum";
	}

	/**
	 * 表读数
	 * @return
	 */
	@RequestMapping(value="readBasicNum" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String readBasicNum(HttpServletRequest request ,String id[],String basenum,String initNames,String doornums)
	{

		//将设置表读数操作记录到操作日志
		tb_log_opr_customer log=new tb_log_opr_customer();
		log.setModulename(modulename+provinceCityArea);
		log.setIp(logOprCustomerService.getIp(request));//ip地址
		log.setOprcontent("为小区["+initNames.substring(0,initNames.length()-1)+"]户号["
				+doornums.substring(0,doornums.length()-1)+"]设置表读数[表读数:"+basenum+"]");
		nb_cus cus=(nb_cus)SecurityUtils.getSubject().getSession().getAttribute("customerSession");
		log.setCustomercode(cus.getId());
		logOprCustomerService.addLog(log);

/*		long delayTime = getDelayTime(nbIotConfig.getDelivery());*/
		Gson gson = new Gson();

		for(String deviceId : id)
		{
			try {
				String deviceid = deviceId.split("\\*")[0];
				String ismagnetism = deviceId.split("\\*")[1];
/*				String dbTime = commandSendLogService.queryNewestTime(deviceId);
				boolean fig = DateTimeUtils.judgmentDate(dbTime,DateTimeUtils.getnowdate());
				if(fig){
					System.out.println("若最新上报时间上报时间距离当前时间小于24小时测立即执行命令");
					if("1".equals(ismagnetism)){
						signalDeliveryService.readBasicNumHAC(deviceid,basenum,nbIotConfig.getWater_meter_basic(),new TemplateUtils(),delayTime);
					}
					if("0".equals(ismagnetism)){
						signalDeliveryService.readBasicNumTLV(deviceid,basenum,"SETRAW",new TemplateUtils(),delayTime);
					}
				}else{
				}*/
				if("1".equals(ismagnetism)){
					signalDeliveryService.readBasicNumHACJedis(deviceid,basenum,nbIotConfig.getWater_meter_basic());
				}
				if("0".equals(ismagnetism)){
					signalDeliveryService.readBasicNumTLVJedis(deviceid,basenum,"SETRAW");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return gson.toJson(new Message("0","设置表读数失败!"));
			}
		}
		return gson.toJson(new Message("1","设置表读数命令缓存成功!等待执行。。。"));

	}
	
	@RequestMapping("toCommandCler")
	public String toCommandCler(HttpServletRequest request,String ids,String initNames,String doornums)
	{

		request.setAttribute("ids", ids);
		request.setAttribute("initNames", initNames);
		request.setAttribute("doornums", doornums);
		return baseUrl + "remote-commandcler";
	}

	
	/**
	 * 表读数
	 * @return
	 */
	@RequestMapping(value="cleanCommand" ,produces = "application/json; charset=utf-8")
	@ResponseBody
	public String cleanCommand(HttpServletRequest request ,String id[],String type[])
	{

		Gson gson = new Gson();

		for(String deviceId : id)
		{
			try {
				
				for (String str : type) {
					String keyPrefix = "";
					String jedisKey = "";
					if(nbIotConfig.getDelivery().equals(str)){
						jedisKey = deviceId + "SETREPORTCYCLE";
						keyPrefix = "shiro_redis_command_" + nbIotConfig.getDelivery()+ ":" + jedisKey;
					}
					if(nbIotConfig.getWater_meter_basic().equals(str)){
						jedisKey = deviceId + "SETMETERREADING";
						keyPrefix = "shiro_redis_command_" + nbIotConfig.getWater_meter_basic()+ ":" + jedisKey;
					}
					if(nbIotConfig.getValve_control().equals(str)){
						jedisKey = deviceId + "VALVECONTROL";
						keyPrefix = "shiro_redis_command_" + nbIotConfig.getValve_control()+ ":" + jedisKey;
					}
					redisClientTemplate.del(keyPrefix);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return gson.toJson(new Message("0","取消下发命令失败!"));
			}
		}
		return gson.toJson(new Message("1","取消下发命令成功!"));

	}
	
/*	private long getDelayTime(String type){
		Map<?, ?> loginMap = (Map<?, ?>)SecurityUtils.getSubject().getSession().getAttribute("userSession");
		String customerId = (String)loginMap.get("CID");
		
		nb_delay_time_delivery delivery = delayTimeService.infoDelivery(customerId);
		long deliverytime = Long.valueOf(delivery.getValue()) * 1000;

		nb_delay_time_basicnum basicnum = delayTimeService.infoBasicNum(customerId);
		long basicnumtime = Long.valueOf(basicnum.getValue()) * 1000;

		nb_delay_time_valvecontrol valvecontrol = delayTimeService.infoValveControl(customerId);
		long valvecontroltime = Long.valueOf(valvecontrol.getValue()) * 1000;
		
		if("SETREPORTCYCLE".equals(type)){return deliverytime;}
		if("SETMETERREADING".equals(type)){return basicnumtime;}
		if("VALVECONTROL".equals(type)){return valvecontroltime;}
		return 0;
	}*/

}
