package nbiot;

import com.ets.business.nb_iot.cmdinfo.iotinit.BathTaskService;
import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.BatchTaskCreateOutDTO;
import com.iotplatform.client.dto.CommandDTOV4;
import com.iotplatform.client.invokeapi.BatchProcess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2018- 11-14 15:01
 */
@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class BatchProcessTest {

    @Autowired
    BathTaskService bathTaskService;

    @Autowired
    IntiClient initClient;

    String DeviceId = "6e9449cf-f916-4854-82e9-eacc744295d1"; // 模拟设备在平台的ID

    @Test
    public void CmdBatch() throws NorthApiException
    {
        NorthApiClient northApiClient = initClient.GetNorthApiClient(); //获取北向接口
        BatchProcess batchProcess = new BatchProcess(northApiClient);//批处理对象

        CommandDTOV4 command = new CommandDTOV4();
        command.setServiceId("open");
        command.setMethod("CommondOpen"); //PUT is the command name
        Map<String, Object> cmdPara = new HashMap<String, Object>();
        cmdPara.put("value", 2); //brightness is a command parameter
        command.setParas(cmdPara);

        List<String> deviceList = new ArrayList<String>();
        deviceList.add(DeviceId);
        BatchTaskCreateOutDTO batchTaskCreateOutDTO = bathTaskService.batchDeviceCmdTask(batchProcess,deviceList,"myTask07",command,null);
        System.out.println("命令批处理已下发,批处理任务ID="+ batchTaskCreateOutDTO.getTaskID());
    }
}
