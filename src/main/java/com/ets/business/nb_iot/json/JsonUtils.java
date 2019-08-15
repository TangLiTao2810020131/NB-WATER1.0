package com.ets.business.nb_iot.json;

import com.ets.business.nb_iot.hac.model.CallBackObject;
import com.ets.business.nb_iot.hac.model.ReceiveEquipment;
import com.ets.business.nb_iot.hac.model.ReportDataHAC;
import com.ets.business.nb_iot.tlv.model.ReportDataTLV;

import net.sf.json.JSONObject;

public class JsonUtils {

	/**
	 * 解析JSON数据得到 CallbackObject对象
	 * @param josndate
	 * @return CallbackObject
	 */
	public CallBackObject JsonToCallbackObject(String josndate){
		CallBackObject object = null;
		try {
			if(!"".equals(josndate)){
				//josndate = "{\"deviceId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"commandId\":\"0384acb1bb7d40538c12e262784b3041\",\"result\":{\"resultCode\":\"DELIVERED\",\"resultDetail\":null}}";
				JSONObject json = JSONObject.fromObject(josndate);
				object = (CallBackObject)JSONObject.toBean(json, CallBackObject.class); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 解析JSON数据得到设备相关信息
	 * @param josndate
	 * @return
	 */
	public ReceiveEquipment JsonReceiveEquipment(String josndate){

		ReceiveEquipment equipment = null;
		try {
			if(!"".equals(josndate)){
				JSONObject json = JSONObject.fromObject(josndate);
				equipment = new ReceiveEquipment();
				equipment.setNotifyType(json.get("notifyType") == null ? "" : json.get("notifyType").toString());
				equipment.setDeviceId(json.get("deviceId") == null ? "" : json.get("deviceId").toString());
				equipment.setGatewayId(json.get("gatewayId") == null ? "" : json.get("gatewayId").toString());
				equipment.setRequestId(json.get("requestId") == null ? "" : json.get("requestId").toString());
				equipment.setService(json.get("service") == null ? "" : json.get("service").toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return equipment;

	}
	
	
	/**
	 * 解析JSON数据得到 协议类型
	 * @param josnStr
	 * @return payloadFormat 协议类型
	 */
	public String JsonToPayloadFormat(String josndate){
		String payloadFormat = null;
		try {
			if(!"".equals(josndate)){
				//josndate = "{\"notifyType\":\"deviceDataChanged\",\"deviceId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"gatewayId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"requestId\":null,\"service\":{\"serviceId\":\"Reading\",\"serviceType\":\"Reading\",\"data\":{\"reportData\":{\"bver\":1.01,\"msgType\":0,\"code\":2,\"payloadFormat\":60,\"messageId\":51948,\"dev\":[{\"11\":1,\"13\":1544078731,\"14\":\"UTC+8\",\"17\":1,\"18\":\"PV1.2\",\"19\":\"V1.16\",\"bn\":\"/3/0\",\"0\":\"Hac\",\"1\":\"NBh\",\"2\":\"1810300002\",\"6\":2,\"7\":359,\"20\":0},{\"0\":\"LXC-20\",\"1\":0,\"16\":60600,\"6\":0,\"bn\":\"/80/0\",\"21\":1544078731},{\"1\":0,\"2\":0,\"3\":1,\"bn\":\"/81/0\"},{\"0\":0,\"1\":0,\"bn\":\"/82/0\"},{\"0\":3600,\"1\":130,\"2\":2,\"3\":1200,\"bn\":\"/84/0\"},{\"13\":-600,\"14\":191,\"bn\":\"/99/0\"}]}},\"eventTime\":\"20181206T065131Z\"}}";
				JSONObject jsonObject = JSONObject.fromObject(josndate);
				JSONObject dataJson = JSONObject.fromObject(jsonObject.get("data").toString());
				JSONObject reportDataJson = JSONObject.fromObject(dataJson.get("reportData").toString());
				payloadFormat = reportDataJson.get("payloadFormat").toString(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payloadFormat;
	}

	/**
	 * 解析JSON数据得到 ReportData对象
	 * @param josnStr
	 * @return
	 */
	public ReportDataHAC JsonToReportDataHAC(String josndate){
		ReportDataHAC rdata = null;
		try {
			if(!"".equals(josndate)){
				//josndate = "{\"notifyType\":\"deviceDataChanged\",\"deviceId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"gatewayId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"requestId\":null,\"service\":{\"serviceId\":\"Reading\",\"serviceType\":\"Reading\",\"data\":{\"reportData\":{\"bver\":1.01,\"msgType\":0,\"code\":2,\"payloadFormat\":60,\"messageId\":51948,\"dev\":[{\"11\":1,\"13\":1544078731,\"14\":\"UTC+8\",\"17\":1,\"18\":\"PV1.2\",\"19\":\"V1.16\",\"bn\":\"/3/0\",\"0\":\"Hac\",\"1\":\"NBh\",\"2\":\"1810300002\",\"6\":2,\"7\":359,\"20\":0},{\"0\":\"LXC-20\",\"1\":0,\"16\":60600,\"6\":0,\"bn\":\"/80/0\",\"21\":1544078731},{\"1\":0,\"2\":0,\"3\":1,\"bn\":\"/81/0\"},{\"0\":0,\"1\":0,\"bn\":\"/82/0\"},{\"0\":3600,\"1\":130,\"2\":2,\"3\":1200,\"bn\":\"/84/0\"},{\"13\":-600,\"14\":191,\"bn\":\"/99/0\"}]}},\"eventTime\":\"20181206T065131Z\"}}";
				JSONObject jsonObject = JSONObject.fromObject(josndate);
				JSONObject dataJson = JSONObject.fromObject(jsonObject.get("data").toString());
				JSONObject reportDataJson = JSONObject.fromObject(dataJson.get("reportData").toString());
				rdata = (ReportDataHAC)JSONObject.toBean(reportDataJson, ReportDataHAC.class); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rdata;
	}
	
	/**
	 * 解析JSON数据得到 ReportData对象
	 * @param josnStr
	 * @return
	 */
	public ReportDataTLV JsonToReportDataTLV(String josndate){
		ReportDataTLV rdata = null;
		try {
			if(!"".equals(josndate)){
				//josndate = "{\"notifyType\":\"deviceDataChanged\",\"deviceId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"gatewayId\":\"03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a\",\"requestId\":null,\"service\":{\"serviceId\":\"Reading\",\"serviceType\":\"Reading\",\"data\":{\"reportData\":{\"bver\":1.01,\"msgType\":0,\"code\":2,\"payloadFormat\":60,\"messageId\":51948,\"dev\":[{\"11\":1,\"13\":1544078731,\"14\":\"UTC+8\",\"17\":1,\"18\":\"PV1.2\",\"19\":\"V1.16\",\"bn\":\"/3/0\",\"0\":\"Hac\",\"1\":\"NBh\",\"2\":\"1810300002\",\"6\":2,\"7\":359,\"20\":0},{\"0\":\"LXC-20\",\"1\":0,\"16\":60600,\"6\":0,\"bn\":\"/80/0\",\"21\":1544078731},{\"1\":0,\"2\":0,\"3\":1,\"bn\":\"/81/0\"},{\"0\":0,\"1\":0,\"bn\":\"/82/0\"},{\"0\":3600,\"1\":130,\"2\":2,\"3\":1200,\"bn\":\"/84/0\"},{\"13\":-600,\"14\":191,\"bn\":\"/99/0\"}]}},\"eventTime\":\"20181206T065131Z\"}}";
				JSONObject jsonObject = JSONObject.fromObject(josndate);
				JSONObject dataJson = JSONObject.fromObject(jsonObject.get("data").toString());
				JSONObject reportDataJson = JSONObject.fromObject(dataJson.get("reportData").toString());
				rdata = (ReportDataTLV)JSONObject.toBean(reportDataJson, ReportDataTLV.class); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rdata;
	}

}
