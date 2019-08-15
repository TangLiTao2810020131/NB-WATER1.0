package com.ets.business.nb_iot.hac.body;

import java.util.Map;

import com.ets.business.nb_iot.hac.model.DeviceInfo;
import com.ets.business.nb_iot.hac.model.Signal;
import com.ets.business.nb_iot.hac.model.ValveControl;
import com.ets.business.nb_iot.hac.model.WaterMeterBasic;


public class ObjectClass {
	
	public DeviceInfo MapToDeviceInfo(Map<String, Object> map) {
		DeviceInfo info = null;
		try {
			info = new DeviceInfo();
			String infoStr = (map.toString());
			infoStr = infoStr.substring(1,infoStr.length()-1);
			String infoStrs[] = infoStr.split(",");
			System.out.println(infoStr);
			info.setErrorCode(infoStrs[0].split("=")[1]);
			info.setCurrentTime(infoStrs[1].split("=")[1]);
			info.setUtcOffset(infoStrs[2].split("=")[1]);
			info.setDeviceType(infoStrs[3].split("=")[1]);
			info.setHardwareVersion(infoStrs[4].split("=")[1]);
			info.setSoftwareVersion(infoStrs[5].split("=")[1]);
			info.setBn(infoStrs[6].split("=")[1]);
			info.setManufacturer(infoStrs[7].split("=")[1]);
			info.setModel(infoStrs[8].split("=")[1]);
			info.setSerialNember(infoStrs[9].split("=")[1]);
			info.setAvailablePowerSources(infoStrs[10].split("=")[1]);
			info.setPowerSourceVoltage(infoStrs[11].split("=")[1]);
			info.setBatteryStatus(infoStrs[12].split("=")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}
	
	/**
	 * 根据map集合内容 转化为Value对象
	 * @param map
	 * @return Value
	 */
	public ValveControl MapToValue(Map<String,Object> map){
		ValveControl value = null;
		try {
			value = new ValveControl();
			String valueStr = (map.toString());
			valueStr = valueStr.substring(1,valueStr.length()-1);
			String values[] = valueStr.split(",");
			System.out.println(valueStr);
			value.setValveCurrentStatus(values[0].split("=")[1]);
			value.setValvefaultStatus(values[1].split("=")[1]);
			value.setType(values[2].split("=")[1]);
			value.setBn(values[3].split("=")[1]);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 根据map集合内容 转化为WaterMeterBasic对象
	 * @param map
	 * @return WaterMeterBasic
	 */
	public WaterMeterBasic MapToWMBasic(Map<String,Object> map){
		WaterMeterBasic basic = null;
		try {
			basic = new WaterMeterBasic();
/*			basic.setWatermetertype(map.get("0").toString());
			basic.setMeasurementmodel(map.get("1").toString());
			basic.setMeasurementfaultStatus(map.get("6").toString());
			basic.setWaterRead(map.get("16").toString());
			basic.setReadMeterTime(map.get("21").toString());
			basic.setBn(map.get("bn").toString());*/
			String wmBasic = (map.toString());
			wmBasic = wmBasic.substring(1,wmBasic.length()-1);
			String wmBasics[] = wmBasic.split(",");
			//System.out.println(wmBasic);
			basic.setWatermetertype(wmBasics[0].split("=")[1]);
			basic.setMeasurementmodel(wmBasics[1].split("=")[1]);
			basic.setWaterRead(wmBasics[2].split("=")[1]);
			basic.setMeasurementfaultStatus(wmBasics[3].split("=")[1]);
			basic.setBn(wmBasics[4].split("=")[1]);
			basic.setReadMeterTime(wmBasics[5].split("=")[1]);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return basic;
	}
	
	public Signal MapToSignal(Map<String,Object> map){
		Signal signal = null;
		try {
			
			signal = new Signal();
			String strSignal = (map.toString());
			strSignal = strSignal.substring(1,strSignal.length()-1);
			String strSignals[] = strSignal.split(",");
			signal.setRssi(strSignals[1].split("=")[1]);
			signal.setSnr(strSignals[2].split("=")[1]);
			signal.setBn(strSignals[3].split("=")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signal;
	}

}
