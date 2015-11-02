package pl.bsb.b2b.h2o.helper;

/**
 *
 * @author paweld
 */
public class MessageInfo {

    protected String status;
    protected String statusDescription;
    protected String ackMessageId;
    protected String ackStatus;
    protected String ackStatusDescription;

    public MessageInfo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getAckMessageId() {
        return ackMessageId;
    }

    public void setAckMessageId(String ackMessageId) {
        this.ackMessageId = ackMessageId;
    }

    public String getAckStatus() {
        return ackStatus;
    }

    public void setAckStatus(String ackStatus) {
        this.ackStatus = ackStatus;
    }

    public String getAckStatusDescription() {
        return ackStatusDescription;
    }

    public void setAckStatusDescription(String ackStatusDescription) {
        this.ackStatusDescription = ackStatusDescription;
    }
}
