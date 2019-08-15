package nbiot.ConcurrentCmd;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.DelayQueue;

/**
 * @author 姚轶文
 * @create 2018- 12-10 20:06
 */

/*
public class EndCmd extends CmdData{

    private DelayQueue<CmdData> cmdDatas;
    private CountDownLatch countDownLatch;
    private Thread sendThread;

    public  EndCmd(){}

    public  EndCmd(DelayQueue<CmdData> cmdDatas, long workTime, CountDownLatch countDownLatch,Thread sendThread)
    {
        //super(null, workTime,countDownLatch);
        this.cmdDatas = cmdDatas;
        this.countDownLatch = countDownLatch;
        this.sendThread = sendThread;
    }

    @Override
    public void run() {
        sendThread.interrupt();
        CmdData cmdData;

        for (Iterator<CmdData> iterator = cmdDatas.iterator(); iterator.hasNext();) {
            cmdData = iterator.next();
            cmdData.setForce(true);
            cmdData.run();
        }
        countDownLatch.countDown();

    }


}
*/