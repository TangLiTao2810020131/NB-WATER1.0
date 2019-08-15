package jxls;
import org.junit.Test;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import com.ets.utils.JxlsUtils;

public class jxls {
	
	@Test
    public void test1() throws Exception
    {// 模板路径和输出流

		Calendar   ctime   =   Calendar.getInstance(); 
		SimpleDateFormat   fymd   =   new   SimpleDateFormat   ( "yyyyMMddhhmm"); 
		Date   date   =   ctime.getTime(); 
		String   sDate   =   fymd.format(date);
		System.out.println(sDate);
    }
}
