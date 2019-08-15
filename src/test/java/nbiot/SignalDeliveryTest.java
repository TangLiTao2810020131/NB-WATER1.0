package nbiot;

import com.ets.business.nb_iot.cmdinfo.iotinit.DeviceManagementService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.CommandDTOV4;
import com.iotplatform.client.dto.PostDeviceCommandInDTO2;
import com.iotplatform.client.dto.PostDeviceCommandOutDTO2;
import com.iotplatform.client.invokeapi.SignalDelivery;
import nbiot.data.CheckTimeData;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2018- 11-27 10:24
 * 信令发送测试
 */

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class SignalDeliveryTest {

    @Autowired
    IntiClient initClient;

    @Autowired
    DeviceManagementService deviceManagementService;

    String deviceId = "03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a";

    @Test
    public void open() throws NorthApiException {
        NorthApiClient northApiClient = initClient.GetNorthApiClient();
        SignalDelivery signalDelivery = new SignalDelivery(northApiClient);
        String accessToken = initClient.getAccessToken();

        PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId, accessToken);
        if (pdcOutDTO != null) {
            System.out.println("pdcOutDTO="+pdcOutDTO.toString());
            String commandId = pdcOutDTO.getCommandId();
            System.out.println("commandId="+commandId);
            /**---------------------update device command------------------------
            System.out.println("\n======update device command======");
            UpdateDeviceCommandInDTO udcInDTO = new UpdateDeviceCommandInDTO();
            udcInDTO.setStatus("CANCELED");
            UpdateDeviceCommandOutDTO udcOutDTO = signalDelivery.updateDeviceCommand(udcInDTO, commandId, null, accessToken);
            System.out.println(udcOutDTO.toString());
             */
        }
    }

    private  PostDeviceCommandOutDTO2 postCommand(SignalDelivery signalDelivery, String deviceId, String accessToken) {
        PostDeviceCommandInDTO2 pdcInDTO = new PostDeviceCommandInDTO2();



        pdcInDTO.setDeviceId(deviceId);
        pdcInDTO.setExpireTime(0); //立即下发
        pdcInDTO.setMaxRetransmit(3); //最大重传次数
        CommandDTOV4 cmd = new CommandDTOV4();
        cmd.setServiceId("Reading");
        cmd.setMethod("CHECKTIME"); //"PUT" is the command name defined in the profile
        Map<String, Object> cmdParam = new HashedMap();
        cmdParam.put("sentData", jsonData());//"brightness" is the command parameter name defined in the profile

        cmd.setParas(cmdParam);
        pdcInDTO.setCommand(cmd);

        try {
            return signalDelivery.postDeviceCommand(pdcInDTO, null, accessToken);
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public Object jsonData()
    {
        CheckTimeData checkTimeData = new CheckTimeData();

        Map<String ,String> map = new HashMap<String,String>();
        map.put("2","1234567890");
        map.put("13",Long.toString(System.currentTimeMillis()));
        map.put("14","UTC+8");
        map.put("bn","/3/0");

        Map[] maps = new HashMap[1];
        maps[0] = map;
        checkTimeData.setDev(maps);

        checkTimeData.setCode("205");
        checkTimeData.setBver("1.01");
        checkTimeData.setMsgType("0");
        checkTimeData.setMsgId("20827");
        checkTimeData.setPayloadFormat("60");
        checkTimeData.setCmdType(10);
        return checkTimeData;
    }
}
