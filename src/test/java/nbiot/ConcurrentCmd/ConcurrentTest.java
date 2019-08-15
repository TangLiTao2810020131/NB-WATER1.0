package nbiot.ConcurrentCmd;

import com.ets.business.nb_iot.cmdinfo.iotinit.DeviceManagementService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.invokeapi.SignalDelivery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.DelayQueue;

/**
 * @author 姚轶文
 * @create 2018- 12-10 20:32
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class ConcurrentTest {

    @Autowired
    IntiClient initClient;

    @Autowired
    DeviceManagementService deviceManagementService;

    String deviceId = "baa91ecb-df14-4294-b576-87d04d9ec40f";

    private NorthApiClient northApiClient;
    private SignalDelivery signalDelivery;
    private String accessToken;

    public void init()
    {
        northApiClient = initClient.GetNorthApiClient();
        signalDelivery = new SignalDelivery(northApiClient);
        accessToken = initClient.getAccessToken();
    }

    @Test
    public void test01() throws InterruptedException {
        System.out.println("=============test01===================");
        DelayQueue< CmdData> CmdDatas = new DelayQueue<CmdData>();
        long now = System.currentTimeMillis();
        Random random = new Random();
        for(int i=0 ; i<50 ; i++)
        {
            CmdDatas.put(new CmdData("name"+i,getTestReportData(),now+random.nextInt(8000)));
        }

        for(int i=0 ; i<50 ; i++)
        {
            System.out.println(CmdDatas.take() +" -- i="+i);
        }

        System.out.println("=============test01 END===================");
    }

    public TestReportData getTestReportData()
    {
        TestReportData reportData = new TestReportData();

        Map<String ,String> map = new HashMap<String,String>();
        map.put("2","1234567890");
        map.put("13",Long.toString(System.currentTimeMillis()));
        map.put("14","UTC+8");
        map.put("bn","/3/0");

        Map[] maps = new HashMap[1];
        maps[0] = map;
        reportData.setDev(maps);

        reportData.setCode("205");
        reportData.setBver("1.01");
        reportData.setMsgType("0");
        reportData.setMsgId("20827");
        reportData.setPayloadFormat("60");
        reportData.setCmdType(10);

        return reportData;
    }
}
