package inter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ets.business.meter.meterread.entity.nb_meterread;
import com.ets.business.meter.meterread.service.MeterreadService;
import com.ets.business.nb_interface.NBInterFace;
import com.ets.common.DateTimeUtils;

import net.sf.json.JSONObject;

/**
 * @author 姚轶文
 * @create 2018- 11-13 16:39
 * 设备管理
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class InterTest {

	@Autowired
	NBInterFace NBInterFace;

	@Autowired
	MeterreadService meterreadService;

	@Test
	public void test01() 
	{

		try {
			long totalRecord = meterreadService.queryAllReadCountOnLine();

			long totalPage = (totalRecord + 10 -1) / 10;

			Map<String,Object> map = null;

			List<nb_meterread> list = null;

			List<nb_meterread> listAll = new ArrayList<nb_meterread>();

			for (int i = 1; i < totalPage; i++) {
				map = new HashMap<String,Object>();
				map.put("page", (i) * 10);//oracle
				map.put("limit", (i - 1) * 10);//oracle
				list = meterreadService.queryAllReadOnLine(map);
				listAll.addAll(list);
			}

			String day = DateTimeUtils.getnowdate();
			String day2 = DateTimeUtils.getPastTime(day,2);//前2天时间

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			Date date2 = df.parse(day2);

			for (nb_meterread meterread : listAll) {
				String time = meterread.getOptiontime();
				Date date = df.parse(time);
				if(date.before(date2)){
					System.out.println("设备离线"+time+"===="+day2);
				}else{
					System.out.println("设备在线"+time+"===="+day2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
