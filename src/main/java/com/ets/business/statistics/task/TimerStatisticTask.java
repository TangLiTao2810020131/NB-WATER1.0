package com.ets.business.statistics.task;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ets.business.meter.meterread.service.MeterreadService;
import com.ets.business.statistics.owner.entity.nb_owner_water_statistics;
import com.ets.business.statistics.owner.service.OwnerStatisticService;

@Component
public class TimerStatisticTask {
	
	/*@Resource
	OwnerStatisticService ownerStatisticService;
	@Resource
	MeterreadService meterreadService;
	
	
	@Scheduled(cron = "0 30 01 * * ?")  //每天凌晨1点30执行一次定时任务
	protected void execute() throws Exception  {
		List list = meterreadService.queryAllRead();
		for (int i = 0; i < list.size(); i++) {
			nb_owner_water_statistics ows = (nb_owner_water_statistics) list.get(i);
			ownerStatisticService.addOwnerStatistic(ows);
		}
		System.out.println(list);
		nb_owner_water_statistics ows = new nb_owner_water_statistics();
		ownerStatisticService.addOwnerStatistic(ows);
		
		//System.out.println("1111");
	}*/

}
