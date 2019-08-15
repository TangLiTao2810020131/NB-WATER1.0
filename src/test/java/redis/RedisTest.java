package redis;

import com.ets.system.shiro.cache.RedisClientTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author 姚轶文
 * @create 2018- 12-04 16:40
 */
@RunWith(SpringJUnit4ClassRunner.class) //使用junit4进行测试
@ContextConfiguration(locations={"classpath:spring-tx.xml"})
public class RedisTest {

    @Autowired
    RedisClientTemplate redisClientTemplate;

    @Test
    public void test01()
    {
        redisClientTemplate.set("test01", "HAHAHA");
    }

   /* @Test
    public void test001()
    {
        redisClientTemplate.set("test:test001","哈哈哈");
    }

    @Test
    public void test002()
    {
        System.out.println(redisClientTemplate.get("test:test001"));
    }

    @Test
    public void  test02()
    {
        String str = new String("Hello");
        ByteSourceUtils byteSourceUtils = new ByteSourceUtils();
        byte[] bytes = byteSourceUtils.serialize(str);

        Object obj = byteSourceUtils.deserialize(bytes);
        System.out.println("obj="+obj.toString());
    }

    @Test
    public void test03()
    {
        Object object = new CommandBody().getValveControlCmd(0);
        ByteSourceUtils byteSourceUtils = new ByteSourceUtils();
        byte[] bytes = byteSourceUtils.serialize(object);

        //Jedis jedis = redisClientTemplate.getJedis();
        redisClientTemplate.set("command01".getBytes(),bytes);

        System.out.println("命令序列化完成");

    }

    @Test
    public void test04()
    {
        //Jedis jedis = jedisClientSingle.getJedis();

        byte[] bytes = redisClientTemplate.get("XXXXXX-ValveControl".getBytes());

        ByteSourceUtils byteSourceUtils = new ByteSourceUtils();
        ReportDataHAC reportData = (ReportDataHAC)byteSourceUtils.deserialize(bytes);
        System.out.println("命令反序列化完成");

        System.out.println(reportData.getPayloadFormat());
    }

    @Test
    public void test05()
    {
        //Jedis jedis = jedisClientSingle.getJedis();

        Object object = new CommandBody().getValveControlCmd(0);
        ByteSourceUtils byteSourceUtils = new ByteSourceUtils();
        byte[] bytes = byteSourceUtils.serialize(object);
        redisClientTemplate.set("command02".getBytes(),bytes);


        ReportDataHAC reportData = (ReportDataHAC)byteSourceUtils.deserialize(bytes);
        System.out.println("命令反序列化完成");
        System.out.println(reportData.getPayloadFormat());
    }*/
}
