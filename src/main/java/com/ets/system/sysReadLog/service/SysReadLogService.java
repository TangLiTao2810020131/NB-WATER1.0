package com.ets.system.sysReadLog.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.nb_iot.hac.model.ReceiveEquipment;
import com.ets.common.DateTimeUtils;
import com.ets.common.UUIDUtils;
import com.ets.system.sysEquipment.entity.tb_sys_equipment;
import com.ets.system.sysReadLog.dao.SysReadLogDao;
import com.ets.system.sysReadLog.entity.tb_sys_read_log;

@Service
@Transactional
public class SysReadLogService {
	
	@Resource
	SysReadLogDao sysReadLogDao;
	
	public void addSysReadLog(tb_sys_read_log entity){
		try {
			sysReadLogDao.insertSysReadLog(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<tb_sys_read_log> getReadLog(Map<String, Object> map) {
		try {
			return sysReadLogDao.selectSysReadLogs(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getCount(Map<String, Object> map) {
		try {
			return sysReadLogDao.selectCount(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public tb_sys_read_log info(Map<String, Object> map) {
		try {
			return sysReadLogDao.info(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 接收到上报数据 存入数据库
	 * @param equipmentReceive 上报数据对象
	 * @param equipment 设备对象
	 */
	public void addSysReadLog(String date,ReceiveEquipment equipmentReceive,tb_sys_equipment equipment,String ip,String baseNum){
		tb_sys_read_log entity = new tb_sys_read_log();
		entity.setId(UUIDUtils.getUUID());
		entity.setImei(equipment.getImei());
		entity.setCtime(DateTimeUtils.getnowdate());
		entity.setContent(date);
		entity.setDeviceId(equipmentReceive.getDeviceId());
		entity.setBaseNum(baseNum);
		entity.setIpaddress(ip);
		addSysReadLog(entity);
	}

}
