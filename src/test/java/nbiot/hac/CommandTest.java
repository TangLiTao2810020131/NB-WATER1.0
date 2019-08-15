package nbiot.hac;

import com.ets.business.nb_iot.cmdinfo.iotinit.DeviceManagementService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.ets.business.nb_iot.hac.body.CommandBody;
import com.ets.business.nb_iot.tlv.model.ReportDataTLV;
import com.ets.common.ObjectCode;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.*;
import com.iotplatform.client.invokeapi.SignalDelivery;
import com.iotplatform.client.invokeapi.SubscriptionManagement;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

/**
 * @author 姚轶文
 * @create 2018- 12-04 18:11
 * 华奥通命令测试
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class CommandTest {

    @Autowired
    IntiClient initClient;

    @Autowired
    DeviceManagementService deviceManagementService;

    String deviceId = "01bdb7f4-af72-4057-adf4-ae77e158d79b";

    private NorthApiClient northApiClient;
    private SignalDelivery signalDelivery;
    private String accessToken;

    public void init()
    {
        northApiClient = initClient.GetNorthApiClient();
        signalDelivery = new SignalDelivery(northApiClient);
        accessToken = initClient.getAccessToken();
    }
    
	private Object getReportDataTLVCmd()
	{
		ReportDataTLV reportData = new ReportDataTLV();
        reportData.setRaw("FA000dFEFE6881000004000015090b16");
		reportData.setCode("701");
		reportData.setBver("1.01");
		reportData.setMsgType("1");
		reportData.setMsgId("33");
		reportData.setPayloadFormat("42");
		reportData.setCmdType(ObjectCode.NB_RAW);  //Delivery上报周期命令代码1
		return reportData;
	}
    
    //@Test
    public void raw()
    {
        init();

        CommandDTOV4 cmd = new CommandDTOV4();
        cmd.setServiceId("Reading");
        cmd.setMethod("SETRAW"); //"PUT" is the command name defined in the profile
        Map<String, Object> cmdParam = new HashedMap();
        cmdParam.put("sentData", getReportDataTLVCmd());//"brightness" is the command parameter name defined in the profile
        cmd.setParas(cmdParam);
        PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd);
        if (pdcOutDTO != null) {
            System.out.println("pdcOutDTO=" + pdcOutDTO);
            String commandId = pdcOutDTO.getCommandId();
            System.out.println("commandId=" + commandId);
        }
        System.out.println("SETRAW Test End");
    }


    //@Test
    public void checkTime()
    {
        init();

        CommandDTOV4 cmd = new CommandDTOV4();
        cmd.setServiceId("Reading");
        cmd.setMethod("CHECKTIME"); //"PUT" is the command name defined in the profile
        Map<String, Object> cmdParam = new HashedMap();
        cmdParam.put("sentData", new CommandBody().getCheckTimeCmd());//"brightness" is the command parameter name defined in the profile
        cmd.setParas(cmdParam);
        PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd);
        if (pdcOutDTO != null) {
            System.out.println("pdcOutDTO=" + pdcOutDTO.toString());
            String commandId = pdcOutDTO.getCommandId();
            System.out.println("commandId=" + commandId);
        }
        System.out.println("CheckTime Test End");
    }

    @Test
    public void valveControl()
    {
        init();

        CommandDTOV4 cmd = new CommandDTOV4();
        cmd.setServiceId("Reading");
        cmd.setMethod("VALVECONTROL"); //"PUT" is the command name defined in the profile
        Map<String, Object> cmdParam = new HashedMap();
        cmdParam.put("sentData", new CommandBody().getValveControlCmd(0));//"brightness" is the command parameter name defined in the profile
        cmd.setParas(cmdParam);
        PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken,cmd);
        if (pdcOutDTO != null) {
            System.out.println("pdcOutDTO=" + pdcOutDTO.toString());
            String commandId = pdcOutDTO.getCommandId();
            System.out.println("commandId=" + commandId);

        }
        System.out.println("ValveControl Test End");
    }


    private PostDeviceCommandOutDTO2 postCommand(SignalDelivery signalDelivery, String deviceId, String accessToken, CommandDTOV4 cmd) {
        PostDeviceCommandInDTO2 pdcInDTO = new PostDeviceCommandInDTO2();

        pdcInDTO.setDeviceId(deviceId);
        pdcInDTO.setExpireTime(0); //立即下发
        pdcInDTO.setMaxRetransmit(3); //最大重传次数
        pdcInDTO.setCommand(cmd);

        try {
            return signalDelivery.postDeviceCommand(pdcInDTO, null, accessToken);
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    @Test
    public void ttt() throws NorthApiException {
        init();
        SubscriptionManagement subscriptionManagement = new SubscriptionManagement(northApiClient);
        QueryBatchSubInDTO qbsInDTO = new QueryBatchSubInDTO();
        qbsInDTO.setAppId("xZ9K0UhzLO33ezR7HEs7eRQk6_Ma");
        QueryBatchSubOutDTO qbsOutDTO = subscriptionManagement.queryBatchSubscriptions(qbsInDTO, accessToken);
        System.out.println("================================================");
        System.out.println(qbsOutDTO.toString());
        System.out.println("================================================");
    }

    @Test
    public void ttt2() throws NorthApiException {
        init();
        SubscriptionManagement subscriptionManagement = new SubscriptionManagement(northApiClient);

        String callbackUrl = "https://117.71.61.79:58887/nb-water/devicedatachange/JsonToDeviceDataChange.action";//this is a test callbackUrl
        SubscriptionDTO subDTO = subDeviceData(subscriptionManagement, "deviceDataChanged", callbackUrl, accessToken);
        subDeviceManagementData(subscriptionManagement, "swUpgradeResultNotify", callbackUrl, accessToken);

        SubscriptionDTO subDTO2 = subscriptionManagement.querySingleSubscription(subDTO.getSubscriptionId(), "xZ9K0UhzLO33ezR7HEs7eRQk6_Ma", accessToken);
        System.out.println("================================================");
        System.out.println(subDTO2.toString());
        System.out.println("================================================");
    }

    private  SubscriptionDTO subDeviceData(SubscriptionManagement subscriptionManagement,
                                                 String notifyType, String callbackUrl, String accessToken) {
        SubDeviceDataInDTO sddInDTO = new SubDeviceDataInDTO();
        sddInDTO.setNotifyType(notifyType);
        sddInDTO.setCallbackUrl(callbackUrl);
        try {
            SubscriptionDTO subDTO = subscriptionManagement.subDeviceData(sddInDTO, null, accessToken);
            System.out.println(subDTO.toString());
            return subDTO;
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    @Test
    public void ttt3()
    {
        init();
        SubscriptionManagement subscriptionManagement = new SubscriptionManagement(northApiClient);
        String callbackUrl = "https://117.71.61.79:58887/nb-water/devicedatachange/JsonToDeviceDataChange.action";//this is a test callbackUrl
        SubscriptionDTO subDTO = subDeviceData(subscriptionManagement, "deviceDeleted", callbackUrl, accessToken);
        subDeviceData(subscriptionManagement, "deviceDeleted", callbackUrl, accessToken);
    }

    private  void subDeviceManagementData(SubscriptionManagement subscriptionManagement,
                                                String notifyType, String callbackUrl, String accessToken) {
        SubDeviceManagementDataInDTO sddInDTO = new SubDeviceManagementDataInDTO();
        sddInDTO.setNotifyType(notifyType);
        sddInDTO.setCallbackurl(callbackUrl);
        try {
            subscriptionManagement.subDeviceData(sddInDTO, accessToken);
            System.out.println("subscribe to device management data succeeds");
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }
        return;
    }


}
