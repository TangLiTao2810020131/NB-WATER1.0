package com.ets.business.readlog.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.nb_iot.hac.model.ReceiveEquipment;
import com.ets.business.readlog.dao.ReadLogDao;
import com.ets.business.readlog.entity.nb_read_log;
import com.ets.common.DateTimeUtils;
import com.ets.common.UUIDUtils;

import net.sf.json.JSONObject;



@Service
@Transactional
public class ReadLogService {
	
	@Resource
	ReadLogDao readLogDao;
	
	public void addReadLog(nb_read_log entity){
		try {
			readLogDao.insertReadLog(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<nb_read_log> getReadLog(Map<String, Object> map) {
		try {
			return readLogDao.selectReadLog(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public long getCount(Map<String, Object> map) {
		try {
			return readLogDao.selectCount(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public nb_read_log info(Map<String, Object> map) {
		try {
			return readLogDao.info(map);
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
	public void AddReadLog(String date,ReceiveEquipment equipmentReceive,nb_watermeter_equipment equipment,String ip){
		nb_read_log entity = new nb_read_log();
		entity.setId(UUIDUtils.getUUID());
		entity.setCtime(DateTimeUtils.getnowdate());
		entity.setImei(equipment.getWatermetercode());
		entity.setContent(date);
		entity.setDeviceId(equipmentReceive.getDeviceId());
		entity.setCustomercode(equipment.getCustomercode());
		entity.setIpaddress(ip);
		addReadLog(entity);
	}

}
