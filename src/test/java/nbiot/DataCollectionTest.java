package nbiot;

import com.ets.business.nb_iot.cmdinfo.iotinit.DataCollectionService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author 姚轶文
 * @create 2018- 11-15 10:56
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class DataCollectionTest {

    @Autowired
    DataCollectionService dataCollectionService;

    String DeviceId = "03d79fbd-ce3b-487d-8b70-a79c2dcc7b5a"; // 模拟设备在平台的ID

    @Test
    public void querySingleDeviceInfo() throws NorthApiException {
        QuerySingleDeviceInfoOutDTO querySingleDeviceInfoOutDTO = dataCollectionService.querySingleDeviceInfo(DeviceId,null);
        System.out.println("设备信息查询完成：");

        System.out.println("DeviceId="+querySingleDeviceInfoOutDTO.getDeviceId());
        System.out.println("CreateTime="+querySingleDeviceInfoOutDTO.getCreateTime());
        System.out.println("GatewayId="+querySingleDeviceInfoOutDTO.getGatewayId());
        System.out.println("NodeType="+querySingleDeviceInfoOutDTO.getNodeType());

        DeviceInfo deviceInfo = querySingleDeviceInfoOutDTO.getDeviceInfo();
        System.out.println("deviceInfo：");
        System.out.println("NodeId="+deviceInfo.getNodeId()); //设备的唯一标识
        System.out.println("Name="+deviceInfo.getName()); //设备名称
        System.out.println("Description="+deviceInfo.getDescription()); //设备描述信息
        System.out.println("ManufacturerId="+deviceInfo.getManufacturerId());//厂家ID
        System.out.println("ManufacturerName="+deviceInfo.getManufacturerName());//厂家名称
        System.out.println("Mac="+deviceInfo.getMac()); //MAC地址
        System.out.println("Location="+deviceInfo.getLocation()); //设备位置信息
        System.out.println("DeviceType="+deviceInfo.getDeviceType());//设备类型
        System.out.println("Model="+deviceInfo.getModel());//设备型号
        System.out.println("SwVersion="+deviceInfo.getSwVersion());//设备软件版本
        System.out.println("FwVersion="+deviceInfo.getFwVersion());//设备固件版本
        System.out.println("HwVersion="+deviceInfo.getHwVersion());//设备硬件版本
        System.out.println("ProtocolType="+deviceInfo.getProtocolType()); //使用的协议
        System.out.println("BridgeId="+deviceInfo.getBridgeId());//Bridge标识，标识设备通过哪个bridge接入物联网
        System.out.println("Status="+deviceInfo.getStatus());//设备状态 是否在线
        System.out.println("StatusDetail="+deviceInfo.getStatusDetail());//设备状态详情
        System.out.println("Mute="+deviceInfo.getMute()); //设备是否处于冻结状态 true冻结 false非冻结
        System.out.println("SupportedSecurity="+deviceInfo.getSupportedSecurity()); //设备是否支持安全模式 true支持 false不支持
        System.out.println("IsSecurity="+deviceInfo.getIsSecurity());//设备当前是否启用安全模式 true启用  false未启用
        System.out.println("SignalStrength="+deviceInfo.getSignalStrength()); // 设备信号强度
        System.out.println("SigVersion="+deviceInfo.getSigVersion());//设备SIG版本
        System.out.println("SerialNumber="+deviceInfo.getSerialNumber());//设备序列号
        System.out.println("BatteryLevel="+deviceInfo.getBatteryLevel());//设备电池电量

        List<DeviceService> list = querySingleDeviceInfoOutDTO.getServices();
        System.out.println("Services List:");
        if(list !=null && list.size()>0)
        {
            for(int i=0 ; i<list.size() ; i++)
            {
                DeviceService deviceService = list.get(i);
                System.out.println("ServiceId = "+deviceService.getServiceId());
                System.out.println("ServiceType = "+deviceService.getServiceType());
                System.out.println("EventTime = "+deviceService.getEventTime());

                ObjectNode objectNode = deviceService.getData();
                System.out.println("objectNode = "+objectNode.toString());

                /*
                ServiceInfo serviceInfo = deviceService.getServiceInfo(); //这里获取到serviceInfo=null
                List listCmds = serviceInfo.getMuteCmds();
                System.out.println("Cmds = "+listCmds);
                */
            }
        }
    }

    @Test
    public void queryBatchDevicesInfo() throws NorthApiException {
        QueryBatchDevicesInfoOutDTO queryBatchDevicesInfoOutDTO = dataCollectionService.queryBatchDevicesInfo(0,10);

        System.out.println("查询数据记录数量="+queryBatchDevicesInfoOutDTO.getTotalCount());
        System.out.println("查询的页码="+queryBatchDevicesInfoOutDTO.getPageNo());
        System.out.println("每页查询的数量="+queryBatchDevicesInfoOutDTO.getPageSize());
        System.out.println("list size = "+queryBatchDevicesInfoOutDTO.getDevices().size());
    }

    @Test
    public void queryDeviceDataHistory() throws NorthApiException {
        QueryDeviceDataHistoryOutDTO queryDeviceDataHistoryOutDTO = dataCollectionService.queryDeviceDataHistory(DeviceId,0,100);

        System.out.println("查询的记录数量="+queryDeviceDataHistoryOutDTO.getTotalCount());
        System.out.println("查询的页码="+queryDeviceDataHistoryOutDTO.getPageNo());
        System.out.println("查询的每页信息数量="+queryDeviceDataHistoryOutDTO.getPageSize());

        List<DeviceDataHistoryDTO> list = queryDeviceDataHistoryOutDTO.getDeviceDataHistoryDTOs();

        if(list!=null && list.size()>0)
        {
            for(int i=0 ; i <list.size() ; i++)
            {
                System.out.println("具体数据：");
                DeviceDataHistoryDTO deviceDataHistoryDTO = list.get(i);
                System.out.println("serviceId = "+deviceDataHistoryDTO.getServiceId());
                System.out.println("deviceId = "+deviceDataHistoryDTO.getDeviceId());
                System.out.println("getwayId = "+deviceDataHistoryDTO.getGatewayId());
                System.out.println("appId = "+deviceDataHistoryDTO.getAppId());
                System.out.println("data = "+deviceDataHistoryDTO.getData());
                System.out.println("timestamp = "+deviceDataHistoryDTO.getTimestamp());
            }
        }
    }

    @Test
    public void queryDeviceCapabilities() throws NorthApiException
    {
        QueryDeviceCapabilitiesOutDTO queryDeviceCapabilitiesOutDTO = dataCollectionService.queryDeviceCapabilities(DeviceId);

        List<DeviceCapabilityDTO> list = queryDeviceCapabilitiesOutDTO.getDeviceCapabilities();

        if(list != null && list.size()>0)
        {
            for(int i= 0 ; i<list.size() ; i++)
            {
                DeviceCapabilityDTO deviceCapabilityDTO = list.get(i);
                System.out.println("设备ID="+deviceCapabilityDTO.getDeviceId()+"的服务如下：");

                List<ServiceCapabilityDTO> serviceList = deviceCapabilityDTO.getServiceCapabilities();

                if(serviceList!=null && serviceList.size()>0)
                {
                    for(int j= 0 ; j<serviceList.size() ; j++)
                    {
                        ServiceCapabilityDTO serviceCapabilityDTO = serviceList.get(j);
                        System.out.print("服务ID="+serviceCapabilityDTO.getServiceId()+" ");
                        System.out.print("服务类型="+serviceCapabilityDTO.getServiceType()+" ");
                        System.out.print("服务选项="+serviceCapabilityDTO.getOption()+" ");
                        System.out.println("服务描述="+serviceCapabilityDTO.getDescription());

                        List<ServiceCommand> comList = serviceCapabilityDTO.getCommands();
                        List<ServiceProperty> proList = serviceCapabilityDTO.getProperties();

                        System.out.println("命令列表：");
                        if(comList != null && comList.size()>0)
                        {
                            for(int k=0 ; k<comList.size() ; k++)
                            {
                                ServiceCommand serviceCommand = comList.get(k);
                                System.out.println("命令名称="+serviceCommand.getCommandName());
                            }
                        }
                        System.out.println("属性列表：");
                        if(proList!=null && proList.size()>0)
                        {
                            for(int k=0 ; k<proList.size() ; k++)
                            {
                                ServiceProperty serviceProperty = proList.get(k);
                                System.out.println("属性名称="+serviceProperty.getPropertyName());
                                System.out.println("数据类型="+serviceProperty.getDataType());
                                System.out.println("最小值="+serviceProperty.getMin());
                                System.out.println("最大值="+serviceProperty.getMax());
                            }
                        }
                    }
                }
            }

        }
    }
}
