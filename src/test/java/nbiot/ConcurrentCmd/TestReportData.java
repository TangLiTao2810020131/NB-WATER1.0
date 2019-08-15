package nbiot.ConcurrentCmd;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 姚轶文
 * @create 2018- 12-10 18:28
 */
public class TestReportData implements Serializable {


    private static final long serialVersionUID = 3643884270589049243L;

    private String bver;
    private String msgType;
    private String code;
    private String msgId;
    private String payloadFormat;
    private Map[] dev;
    private Integer cmdType;

    public String getBver() {
        return bver;
    }

    public void setBver(String bver) {
        this.bver = bver;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getPayloadFormat() {
        return payloadFormat;
    }

    public void setPayloadFormat(String payloadFormat) {
        this.payloadFormat = payloadFormat;
    }

    public Map[] getDev() {
        return dev;
    }

    public void setDev(Map[] dev) {
        this.dev = dev;
    }

    public Integer getCmdType() {
        return cmdType;
    }

    public void setCmdType(Integer cmdType) {
        this.cmdType = cmdType;
    }
}
