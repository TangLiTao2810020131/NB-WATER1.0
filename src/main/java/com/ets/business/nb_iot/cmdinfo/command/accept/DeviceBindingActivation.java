package com.ets.business.nb_iot.cmdinfo.command.accept;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ets.common.Common;

@Component
@RequestMapping("devicebindingactivation")
public class DeviceBindingActivation {
	

	String baseUrl = "business/nb_lot/cmdinfo/command/accept/";
	


	@RequestMapping("JsonToDeviceBindingActivation")
	public String JsonToDeviceBindingActivation(HttpServletRequest request) throws Exception{
		String date = Common.getPostData(request.getInputStream(),request.getContentLength(),null);
		System.out.println("设备绑定激活");
		System.out.println(date);
		return baseUrl + "accept-devicebindingactivation";
	}
	

}
