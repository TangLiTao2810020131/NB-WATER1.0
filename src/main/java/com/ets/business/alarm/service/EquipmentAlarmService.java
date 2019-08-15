package com.ets.business.alarm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ets.business.alarm.dao.EquipmentAlarmDao;
import com.ets.business.alarm.entity.nb_equipment_alarm;
import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.nb_iot.hac.model.DeviceInfo;
import com.ets.business.nb_iot.hac.model.ValveControl;
import com.ets.business.nb_iot.hac.model.WaterMeterBasic;
import com.ets.business.owner.dao.OwnerDao;
import com.ets.business.owner.entity.OwnerModel;
import com.ets.common.DateTimeUtils;
import com.ets.common.UUIDUtils;

/**
 * 社保告警
 * @author wuhao
 *
 */
@Service
@Transactional
public class EquipmentAlarmService {
	
	@Resource
	EquipmentAlarmDao alarmDao;
	
	@Resource
	OwnerDao ownerDao;

	public List<nb_equipment_alarm> getAlarm(Map<String, Object> map) {
		try {
			return alarmDao.selectAlarm(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getCount(Map<String, Object> map) {
		try {
			return alarmDao.selectCount(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void opentionAlarm(nb_equipment_alarm alarm) {
		try {
			alarm.setId(UUIDUtils.getUUID());
			alarm.setCtime(DateTimeUtils.getnowdate());
			alarmDao.insertAlarm(alarm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public nb_equipment_alarm infoAlarm(Map<String, Object> map) {
		try {
			return alarmDao.infoAlarm(map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据上报来的数据，添加告警日志
	 * @param equipment 设备
	 * @param basic 表基础数据
	 * @param info 设备数据
	 */
	public void addEquipmentAlarm(nb_watermeter_equipment equipment, WaterMeterBasic basic, DeviceInfo info,ValveControl value) {

		Map<String,Object> map = new HashMap<String,Object>();
		
		map.put("customercode", equipment.getCustomercode());
		map.put("doornumid", equipment.getDoornumid());
		OwnerModel oModel = ownerDao.ownerInfo(map);
		
		if(oModel != null){
			nb_equipment_alarm alarm = new nb_equipment_alarm();
			
			String status1 = info.getBatteryStatus();//电池状态
			String status2 = basic.getMeasurementfaultStatus();//计量错误状态
			String status3 = value.getValvefaultStatus();//阀门故障状态
			
			if(!"0".equals(status1)){
				alarm = new nb_equipment_alarm();
				alarm.setUseraccount(oModel.getUseraccount());
				alarm.setUsername(oModel.getUsername());
				alarm.setWatermetercode(equipment.getWatermetercode());
				alarm.setCustomercode(equipment.getCustomercode());
				alarm.setAddress(oModel.getAddress());
				alarm.setAlarmtime(DateTimeUtils.getnowdate());
				alarm.setAlarmstatus("BS-" + status1);
				alarm.setAlarmtype("电池告警");
				opentionAlarm(alarm);
			}
			if(!"0".equals(status2)){
				alarm = new nb_equipment_alarm();
				alarm.setUseraccount(oModel.getUseraccount());
				alarm.setUsername(oModel.getUsername());
				alarm.setWatermetercode(equipment.getWatermetercode());
				alarm.setCustomercode(equipment.getCustomercode());
				alarm.setAddress(oModel.getAddress());
				alarm.setAlarmtime(DateTimeUtils.getnowdate());
				alarm.setAlarmstatus("MFS-" + status2);
				alarm.setAlarmtype("计量错误状态");
				opentionAlarm(alarm);
			}
			
			if(!"0".equals(status3)){
				alarm = new nb_equipment_alarm();
				alarm.setUseraccount(oModel.getUseraccount());
				alarm.setUsername(oModel.getUsername());
				alarm.setWatermetercode(equipment.getWatermetercode());
				alarm.setCustomercode(equipment.getCustomercode());
				alarm.setAddress(oModel.getAddress());
				alarm.setAlarmtime(DateTimeUtils.getnowdate());
				alarm.setAlarmstatus("RSOI-" + status3);
				alarm.setAlarmtype("阀门告警");
				opentionAlarm(alarm);
			}
		}
	}

	public void addBaseNumAlarm(nb_watermeter_equipment equipment) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("customercode", equipment.getCustomercode());
		map.put("doornumid", equipment.getDoornumid());
		OwnerModel oModel = ownerDao.ownerInfo(map);
		
		if(oModel != null){
			nb_equipment_alarm alarm = new nb_equipment_alarm();;
			
			alarm.setUseraccount(oModel.getUseraccount());
			alarm.setUsername(oModel.getUsername());
			alarm.setWatermetercode(equipment.getWatermetercode());
			alarm.setCustomercode(equipment.getCustomercode());
			alarm.setAddress(oModel.getAddress());
			alarm.setAlarmtime(DateTimeUtils.getnowdate());
			alarm.setAlarmstatus("MFS-1");
			alarm.setAlarmtype("计量数据错误告警");
		}
	}
}
