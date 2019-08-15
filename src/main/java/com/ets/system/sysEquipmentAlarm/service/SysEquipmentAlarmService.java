package com.ets.system.sysEquipmentAlarm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.nb_iot.hac.model.DeviceInfo;
import com.ets.business.nb_iot.hac.model.ValveControl;
import com.ets.business.nb_iot.hac.model.WaterMeterBasic;
import com.ets.business.owner.dao.OwnerDao;
import com.ets.business.owner.entity.OwnerModel;
import com.ets.common.DateTimeUtils;
import com.ets.common.UUIDUtils;
import com.ets.system.sysEquipment.entity.tb_sys_equipment;
import com.ets.system.sysEquipmentAlarm.dao.SysEquipmentAlarmDao;
import com.ets.system.sysEquipmentAlarm.entity.tb_sys_equipment_alarm;

/**
 * 社保告警
 * @author wuhao
 *
 */
@Service
@Transactional
public class SysEquipmentAlarmService {
	
	@Resource
	SysEquipmentAlarmDao sysAlarmDao;
	
	@Resource
	OwnerDao ownerDao;

	public List<tb_sys_equipment_alarm> getAlarm(Map<String, Object> map) {
		try {
			return sysAlarmDao.selectSysAlarm(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getCount(Map<String, Object> map) {
		try {
			return sysAlarmDao.selectCount(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void opentionSysAlarm(tb_sys_equipment_alarm alarm) {
		try {
			alarm.setId(UUIDUtils.getUUID());
			alarm.setCtime(DateTimeUtils.getnowdate());
			sysAlarmDao.insertSysAlarm(alarm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public tb_sys_equipment_alarm infoSysAlarm(Map<String, Object> map) {
		try {
			return sysAlarmDao.infoSysAlarm(map);
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
	public void addEquipmentAlarm(tb_sys_equipment equipment, WaterMeterBasic basic, DeviceInfo info,ValveControl value) {

		
			tb_sys_equipment_alarm alarm = new tb_sys_equipment_alarm();;
			
			alarm.setAlarmtime(DateTimeUtils.getnowdate());
			String status1 = info.getBatteryStatus();//电池状态
			String status2 = basic.getMeasurementfaultStatus();//计量错误状态
			String status3 = value.getValvefaultStatus();//阀门故障状态
			
			
			if(!"0".equals(status1)){
				alarm = new tb_sys_equipment_alarm();;
				alarm.setImei(equipment.getImei());
				alarm.setDeviceid(equipment.getDeviceid());
				alarm.setAlarmtime(DateTimeUtils.getnowdate());
				alarm.setAlarmstatus("BS-" + status1);
				alarm.setAlarmtype("电池告警");
				
				opentionSysAlarm(alarm);
			}
			if(!"0".equals(status2)){
				alarm = new tb_sys_equipment_alarm();;
				alarm.setImei(equipment.getImei());
				alarm.setDeviceid(equipment.getDeviceid());
				alarm.setAlarmtime(DateTimeUtils.getnowdate());
				alarm.setAlarmstatus("MFS-" + status2);
				alarm.setAlarmtype("计量错误状态");
				opentionSysAlarm(alarm);
			}
			
			if(!"0".equals(status3)){
				alarm = new tb_sys_equipment_alarm();;
				alarm.setImei(equipment.getImei());
				alarm.setDeviceid(equipment.getDeviceid());
				alarm.setAlarmtime(DateTimeUtils.getnowdate());
				alarm.setAlarmstatus("RSOI-" + status3);
				alarm.setAlarmtype("阀门告警");
				opentionSysAlarm(alarm);
			}
	}
}
