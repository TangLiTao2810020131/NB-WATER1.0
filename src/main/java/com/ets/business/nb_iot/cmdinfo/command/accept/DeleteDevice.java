package com.ets.business.nb_iot.cmdinfo.command.accept;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ets.common.Common;

@Component
@RequestMapping("deletedevice")
public class DeleteDevice {
	

	String baseUrl = "business/nb_lot/cmdinfo/command/accept/";
	


	@RequestMapping("JsonToDeleteDevice")
	public String JsonToDeleteDevice(HttpServletRequest request) throws Exception{
		String date = Common.getPostData(request.getInputStream(),request.getContentLength(),null);
		System.out.println("删除设备");
		System.out.println(date);
		return baseUrl + "accept-deletedevice";
	}
	

}
