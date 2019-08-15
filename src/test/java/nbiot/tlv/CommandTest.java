package nbiot.tlv;

import com.ets.common.DateTimeUtils;
import com.ets.common.dtree.Data;
import com.google.common.base.StandardSystemProperty;
import oracle.sql.DATE;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 吴浩
 * @create 2018-12-25 15:07
 */
public class CommandTest {


    public String makeChecksum(String data)
    {


        int iTotal = 0;
        int iLen = data.length();
        int iNum = 0;

        while (iNum < iLen)
        {
            String s = data.substring(iNum, iNum + 2);
            //System.out.println(s);
            iTotal += Integer.parseInt(s, 16);
            iNum = iNum + 2;
        }

        /**
         * 用256求余最大是255，即16进制的FF
         */
        int iMod = iTotal % 256;
        String sHex = Integer.toHexString(iMod);
        iLen = sHex.length();
        //如果不够校验位的长度，补0,这里用的是两位校验
        if (iLen < 2)
        {
            sHex = "0" + sHex;
        }
        return sHex;
    }



    @Test
    public void jiexi(){
        String baseNum = Integer.toHexString(Integer.valueOf("24"));
        System.out.println(baseNum);
        System.out.println(baseNum.length());
        String baseNumcd = "";
        String baseNumlen = "";
        if(baseNum.length() == 1){
            baseNum = "000" + baseNum;
        }
        if(baseNum.length() == 2){
            baseNum = "00" + baseNum;
        }
        if(baseNum.length() == 3){
            baseNum = "0" + baseNum;
        }


        System.out.println("baseNum:"+baseNum);

        System.out.println("baseNumLength:"+baseNum.length());
        String num = "";
        for(int i = 0; i < baseNum.length();i++){
             num += "0";
        }
        System.out.println("num:"+num);
        String dateCD = num + baseNum;
        System.out.println(dateCD);
        String dataNum = String.valueOf(dateCD.length()/2);
        System.out.println(dataNum);
        String dataNumLength = "000" + dataNum;
        System.out.println(dataNumLength);
        String bt = "68";
        String mllx = "81";
        String data = bt + mllx + dataNumLength + dateCD;
        System.out.println(data);
        String dataallLenth = makeChecksum(data);
        System.out.println(dataallLenth);
        String fengefu = "FEFE";
        String middle = fengefu + data + dataallLenth;
        System.out.println(middle);
        String head = "FA";
        String tail = "16";
        String allLen = String.valueOf((middle + tail).length()/2);
        System.out.println(allLen);
        String strHex = Integer.toHexString(Integer.valueOf(allLen));
        if(strHex.length() == 1){
            strHex = "000" + strHex;
        }
        if(strHex.length() == 2){
            strHex = "00" + strHex;
        }
        if(strHex.length() == 3){
            strHex = "0" + strHex;
        }
        System.out.println(strHex);

        String allData = head + strHex + middle + tail;
        System.out.println(allData);
 /*       String baseNum = "9999";
        String strHex = Integer.toHexString(Integer.valueOf(baseNum));
        if(baseNum.length()%2==1){
            strHex = "0"+strHex;
        }
        System.out.println(strHex);
        String d = ("680100040000"+strHex);
        char[] c=d.toCharArray();
        String valStr = "";
        for (int i = 0;i < c.length;i++){
            valStr += (c[i]+"");
            if (i%2==1){
                valStr +=("-");
            }
        }
        String strs[] = valStr.split("-");
        int sum = 0;
        for (int i = 0;i < strs.length;i++){
            String val = strs[i];
            System.out.println(val);
            if(val != ""){
                Integer xzbval = Integer.parseInt(val);
                sum += xzbval;
            }
        }
        System.out.println(sum);*/
    	Map map = new HashMap();
    	map.put("serviceId", "Reading");
    	map.put("method", "SETRAW");
    	map.put("paras", "SETRAW");
    	System.out.println(map);

       /* String time = ("2018-12-24 09:20:54");
        String [] times = (time.split(" "));
        String riqi = (times[0]);
        String shijian = (times[1]);
        String [] riqis = riqi.split("-");
        String [] shijians = shijian.split(":");
        String curtime ="";
        for (int i = 0;i < riqis.length;i++){
            riqis[0] = DateTimeUtils.getDate(new Date());
            String sq = riqis[i];
            System.out.println(sq);
            String strHex = Integer.toHexString(Integer.valueOf(sq));
            if(strHex.length()==1){
                strHex = "0" + strHex;
            }
            System.out.println(strHex);
            curtime += strHex;
        }

        for (int i = 0;i < shijians.length;i++){
            String sj = shijians[i];
            System.out.println(sj);
            String strHex = Integer.toHexString(Integer.valueOf(sj));
            if(strHex.length()==1){
                strHex = "0" + strHex;
            }
            System.out.println(strHex);
            curtime += strHex;
        }
        System.out.println(curtime);
        String data = "1c00073700c3010110141d000201661e0006120c18091436fa000cfefe68000004000015078816";
        String datas[] = data.split("fa");
        xhzl(datas[0]);
        dcdy(datas[0]);
        time(datas[0]);
        wcbxy(datas[1]);*/
    }

    private void xhzl(String data){
        String shuzu[] = data.split("1c");
        String str = shuzu[1].split("1d")[0];
        System.out.println(str);
        String strval = str.substring(4);
        System.out.println("信号质量16进制数据：" + strval);
        String xhqd = strval.substring(0,2);
        System.out.println("信号强度16进制数据：" + xhqd);
        Integer xhqdval = Integer.parseInt(xhqd,16);
        System.out.println("信号强度10进制数据：" + xhqdval);
        String xzb = strval.substring(2,6);
        System.out.println("信噪比16进制数据：" + xzb);
        Integer xzbval = Integer.parseInt(xzb,16);
        System.out.println("信噪比10进制数据：" + xzbval);
        String fgdj = strval.substring(6,8);
        System.out.println("覆盖等级16进制数据：" + fgdj);
        Integer fgdjval = Integer.parseInt(fgdj,16);
        System.out.println("覆盖等级10进制数据：" + fgdjval);
        String xqh = strval.substring(8,12);
        System.out.println("小区号16进制数据：" + xqh);
        Integer xqhval = Integer.parseInt(xqh,16);
        System.out.println("小区号10进制数据：" + xqhval);
        String xhdj = strval.substring(12,14);
        System.out.println("信号等级16进制数据：" + xhdj);
        Integer xhdjval = Integer.parseInt(xhdj,16);
        System.out.println("信号等级10进制数据：" + xhdjval);
        System.out.println("信号质量解析完毕!");
    }

    private void dcdy(String data){
        String shuzu[] = data.split("1d");
        String str = shuzu[1].split("1e")[0];
        System.out.println(str);
        String strval = str.substring(4);
        System.out.println("电池电压16进制数据：" + strval);
        Integer val = Integer.parseInt(strval,16);
        System.out.println("电池电压10进制数据：" + val);
        System.out.println("电池电压解析完毕!");

    }

    private void time(String data){
        String shuzu[] = data.split("1e");
        String str = shuzu[1].substring(4);
        System.out.println("UTC时间16进制：" + str);
        char[] c=shuzu[1].substring(4).toCharArray();
        String valStr = "";
        for (int i = 0;i < c.length;i++){
            valStr += (c[i]+"");
                if (i%2==1){
                    valStr +=("-");
                }
        }
        System.out.print("UTC时间10进制：" );
        String strs[] = valStr.split("-");
        for (int i = 0;i < strs.length;i++){
            String val = strs[i];
            if(val != ""){
                Integer value = Integer.parseInt(val,16);
                System.out.print(value+" ");
            }
        }
        System.out.println("UTC时间解析完毕!");
    }

    private void wcbxy(String data){
        String shuzu[] = (data.split("fefe"));
        String str = shuzu[1];
        System.out.println("无磁表协议16进制数据：" + str);
        String bt = str.substring(0,2);
        System.out.println("报头16进制数据：" + bt);
        Integer btValue = Integer.parseInt(bt,16);
        System.out.println("报头10进制数据：" + btValue);
        String comandType = str.substring(2,4);
        System.out.println("命令类型16进制数据：" + comandType);
        Integer cvalue = Integer.parseInt(comandType,16);
        System.out.println("命令类型10进制数据：" + cvalue);
        String cwType = str.substring(4,6);
        System.out.println("错误类型16进制数据：" + cwType);
        Integer cwvalue = Integer.parseInt(cwType,16);
        System.out.println("错误类型10进制数据：" + cwvalue);
        String par = str.substring(6,8);
        System.out.println("参数长度16进制数据：" + par);
        Integer aprvalue = Integer.parseInt(par,16);
        System.out.println("参数长度10进制数据：" + aprvalue);
        String pardata = str.substring(8,8 + aprvalue * 2);
        System.out.println("参数16进制数据：" + pardata);
        Integer datavalue = Integer.parseInt(pardata,16);
        System.out.println("参数10进制数据：" + datavalue);

        System.out.println("无磁表协议解析完毕!");
    }

}
