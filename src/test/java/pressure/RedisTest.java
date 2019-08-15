package pressure;

import com.ets.common.UUIDUtils;
import com.ets.system.shiro.cache.RedisClientTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.UnsupportedEncodingException;
import java.util.Random;

/**
 * @author 姚轶文
 * @create 2018- 12-17 14:46
 */

@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class RedisTest {

    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


    @Autowired
    RedisClientTemplate redisClientTemplate;

    @Test
    public void test02() throws UnsupportedEncodingException {
        String str = getRandomString(200);
        System.out.println(str.getBytes("utf-8").length);
    }

    @Test
    public void test01()
    {
        //Jedis jedis = jedisClientSingle.getJedis();
        long stime = System.currentTimeMillis();
        for (int i=1 ; i<=1000000 ; i++)
        {
            String key = UUIDUtils.getUUID();
            redisClientTemplate.set(key,getRandomString(200));
            redisClientTemplate.expire(key,43200);//有效期12小时
            System.out.println("已写入"+i+"条");
        }
        long etime = System.currentTimeMillis();
        System.out.println("耗时："+(etime-stime)+"ms");
    }

    @Test
    public  void test03()
    {
        // key = efb848a9587c4aeea8d29dba5d4cbfae
        long stime = System.currentTimeMillis();
        redisClientTemplate.get("efb848a9587c4aeea8d29dba5d4cbfae");
        long etime = System.currentTimeMillis();
        System.out.println("耗时："+(etime-stime)+"ms");
    }

    public  String getRandomString(int length) { //获取指定长度随机字符串
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
