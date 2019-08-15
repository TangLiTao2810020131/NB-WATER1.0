package com.ets.quartz.dao;

import java.util.List;
import java.util.Map;

import com.ets.quartz.entity.qrtz_triggers;

public interface QuartzDao {

	List<qrtz_triggers> selectQrtzTriggers(Map<String, Object> map);

	long selectCount();

}
