package nbiot;

import com.ets.business.nb_iot.cmdinfo.iotinit.IntiClient;
import com.iotplatform.client.NorthApiClient;
import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.AuthOutDTO;
import com.iotplatform.client.invokeapi.Authentication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author 姚轶文
 * @create 2018- 11-13 13:47
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class NbIotTest {

    @Autowired
    IntiClient initClient;
    //获取API接口，建立链接并获取accessToken
    @Test
    public void test_IntiClient() throws NorthApiException {
        NorthApiClient northApiClient = initClient.GetNorthApiClient();

        //得到NorthApiClient实例后，再使用northApiClient得到鉴权类实例
        Authentication authentication = new Authentication(northApiClient);

        //调用鉴权类实例authentication提供的业务接口，如getAuthToken
        AuthOutDTO authOutDTO = authentication.getAuthToken();

        //从返回的结构体authOutDTO中获取需要的参数，如accessToken
        String accessToken = authOutDTO.getAccessToken();
        System.out.println("accessToken="+accessToken);
    }

}
