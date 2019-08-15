package com.ets.quartz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ets.business.equipment.entity.nb_watermeter_equipment;
import com.ets.business.equipment.service.EquipmentService;
import com.ets.business.meter.meterread.entity.nb_meterread;
import com.ets.business.meter.meterread.service.MeterreadService;
import com.ets.business.meter.meterreadlog.entity.nb_meterread_log;
import com.ets.business.meter.meterreadlog.service.MeterreadlogService;
import com.ets.business.statistics.owner.entity.nb_owner_water_statistics;
import com.ets.business.statistics.owner.service.OwnerStatisticService;
import com.ets.common.DateTimeUtils;

/**
 * @author 姚轶文
 * @create 2019- 05-06 10:01
 */

@Service
public class SimpleService {
	private static final Logger logger = LoggerFactory.getLogger(SimpleService.class);

	@Autowired
	OwnerStatisticService ownerStatisticService;

	@Autowired
	MeterreadService meterreadService;
	
	@Resource
	EquipmentService equipmentService;

	@Autowired
	MeterreadlogService meterreadlogService;

	public void testMethod1(){

		logger.info("日用量统计");

		long totalRecord = meterreadService.queryAllReadCount();

		long totalPage = (totalRecord + 10 -1) / 10;

		Map<String,Object> map = null;

		List<nb_owner_water_statistics> list = null;

		List<nb_owner_water_statistics> listAll = new ArrayList<nb_owner_water_statistics>();

		for (int i = 1; i < totalPage; i++) {
			map = new HashMap<String,Object>();
			map.put("page", (i) * 10);//oracle
			map.put("limit", (i - 1) * 10);//oracle
			list = meterreadService.queryAllRead(map);
			listAll.addAll(list);
		}

		for (int i = 0; i < listAll.size(); i++) {
			nb_owner_water_statistics ows = (listAll.get(i));
			String eid = (ows.getEquipmentid());
			if("".equals(eid)) continue;
			String time = DateTimeUtils.beforeDay(listAll.get(i).getCtime()).split(" ")[0];
			map = new HashMap<String,Object>();
			map.put("eid", eid);
			map.put("time", time);
			System.out.println(time);

			nb_meterread_log mlog = meterreadlogService.queryLogByEidTime(map);
			double v2 = 0;
			if(mlog != null){
				v2 = Double.valueOf(mlog.getValue());
			} 
			double v1 = Double.valueOf(listAll.get(i).getDegrees());

			ows.setDegrees(String.valueOf((v1 - v2)));
		}

		for (int i = 0; i < listAll.size(); i++) {
			nb_owner_water_statistics ows = (nb_owner_water_statistics) listAll.get(i);
			ownerStatisticService.addOwnerStatistic(ows);
		}

	}

	public void testMethod2(){
		logger.info("设备离线状态更新");

		try {
			
			long totalRecord = meterreadService.queryAllReadCountOnLine();

			long totalPage = (totalRecord + 10 -1) / 10;

			Map<String,Object> map = null;

			List<nb_meterread> list = null;

			List<nb_meterread> listAll = new ArrayList<nb_meterread>();

			for (int i = 1; i < totalPage; i++) {
				map = new HashMap<String,Object>();
				map.put("page", (i) * 10);//oracle
				map.put("limit", (i - 1) * 10);//oracle
				list = meterreadService.queryAllReadOnLine(map);
				listAll.addAll(list);
			}

			String day = DateTimeUtils.getnowdate();
			String day2 = DateTimeUtils.getPastTime(day,2);//前2天时间

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date date2 = df.parse(day2);

			for (nb_meterread meterread : listAll) {
				String time = meterread.getOptiontime();
				String eid = meterread.getWatermeterid();
				nb_watermeter_equipment equipment = null;
				
				Date date = df.parse(time);
				if(date.before(date2)){
					equipment = new nb_watermeter_equipment();
					equipment.setId(eid);
					equipment.setIsonline("0");
					equipmentService.updateOnline(equipment);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}
