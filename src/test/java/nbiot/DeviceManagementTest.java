package nbiot;

import com.ets.business.nb_iot.cmdinfo.iotinit.DeviceManagementService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.RegDirectDeviceOutDTO;
import com.iotplatform.client.invokeapi.DeviceManagement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author 姚轶文
 * @create 2018- 11-13 16:39
 * 设备管理
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class DeviceManagementTest {
    @Autowired
    IntiClient initClient;

    @Autowired
    DeviceManagementService deviceManagementService;

    //DeviceId=82e3c6d6-3a59-4fc6-b625-af01242ad5db

    @Test
    public void addDevice() throws NorthApiException //注册设备
    {
        NorthApiClient northApiClient = initClient.GetNorthApiClient(); //获取北向接口
        DeviceManagement deviceManagement = new DeviceManagement(northApiClient); //设备管理类

        RegDirectDeviceOutDTO rddod = deviceManagementService.registerDevice(deviceManagement, 3000,"hello-nodeid2");
        System.out.println("设备注册信息");
        System.out.println("DeviceId="+rddod.getDeviceId());
        System.out.println("Psk="+rddod.getPsk());
        System.out.println("VerifyCode="+rddod.getVerifyCode());
        System.out.println("Timeout()="+rddod.getTimeout());

    }

    @Test
    public void modifyDeviceInfo()throws NorthApiException
    {
        NorthApiClient northApiClient = initClient.GetNorthApiClient(); //获取北向接口
        DeviceManagement deviceManagement = new DeviceManagement(northApiClient); //设备管理类

        deviceManagementService.modifyDeviceInfo(deviceManagement,"ea78e201-c3ef-4d2b-809b-3c565abfc46d",
                "test003","WaterMeter","001","HAC","test-01","CoAP");
        System.out.println("设备信息修改完成");
    }
}
