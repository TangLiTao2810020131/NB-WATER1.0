package nbiot.ConcurrentCmd;

/**
 * @author 姚轶文
 * @create 2018- 12-10 20:24
 */

/*
public class Supervise implements Runnable{

    private DelayQueue<CmdData> cmdDatas;

    public Supervise(DelayQueue<CmdData> cmdDatas){
        this.cmdDatas = cmdDatas;
    }

    @Override
    public void run() {
        try {
            System.out.println(" test start");
            while(!Thread.interrupted()){
                cmdDatas.take().run();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}
*/