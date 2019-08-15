package nbiot.ConcurrentCmd;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author 姚轶文
 * @create 2018- 12-10 18:31
 */
public class CmdData implements Delayed {

    private TestReportData testReportData;
    String name;
    private long workTime;
//    private long submitTime;
//    private boolean isForce = false;
//    private CountDownLatch countDownLatch;



    public  CmdData(){}

    public  CmdData(String name  , TestReportData testReportData , long workTime )
    {
        this.name = name;
        this.testReportData = testReportData;
        this.workTime = workTime;
    }

    @Override
    public int compareTo(Delayed o) {
        if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS))
            return -1;
        else if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS))
            return 1;
        else
            return 0;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(workTime - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    public TestReportData getTestReportData() {
        return testReportData;
    }

    public void setTestReportData(TestReportData testReportData) {
        this.testReportData = testReportData;
    }

    public long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(long workTime) {
        this.workTime = workTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name +"--"+ workTime;
    }
}
