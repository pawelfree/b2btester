package pl.bsb.b2btester.model.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import pl.bsb.b2btester.util.Base64;
import pl.bsb.b2btester.util.HtmlUtil;
import pl.bsb.b2btester.web.helper.ActionType;
import pl.bsb.b2btester.web.helper.Direction;
import pl.bsb.b2btester.web.helper.MessageStatus;

/**
 *
 * @author paweld
 */
@NamedQueries({
    @NamedQuery(name = "getMessagesByDirectionAndDate", query = "SELECT m FROM Message m WHERE "
            + "m.server = :server AND m.messageDate = :date AND m.direction = :direction ORDER BY m.messageTime DESC"),
    @NamedQuery(name = "getMessagesByDate", query = "SELECT m FROM Message m WHERE "
            + "m.messageDate = :date AND m.server = :server ORDER BY m.messageTime DESC"),
    @NamedQuery(name = "isMessageDuplicated", query = "SELECT m FROM Message m WHERE "
            + "m.messageDate = :messageDate AND m.documentId = :documentId AND m.server = :server"),
    @NamedQuery(name = "findRelatedMessages", query = "SELECT m FROM Message m WHERE "
            + "m.documentId = :documentId AND m.id <> :id AND m.direction = :direction AND "
            + "m.messageDate = :date ORDER BY m.messageTime DESC")
})
@Entity
public class Message implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date messageDate;
    @NotNull
    @Temporal(TemporalType.TIME)
    private Date messageTime;
    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Column(length = 1638400)
    private String rbeMessage;
    @NotNull
    @Length(min = 16)
    private String hermesMessageId;
    @NotNull
    @Length(max = 32)
    private String documentId;
    @NotNull
    private String service;
    @Enumerated(EnumType.STRING)
    private ActionType action;
    @ManyToOne
    private Server server;
    @Transient
    private String rawMessage;
    @Enumerated
    private MessageStatus lastStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastStatusDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Direction direction;

    public Message() {
        lastStatus = MessageStatus.NA;
    }

    public String prettyFormatedXML() {
        return HtmlUtil.transcode(HtmlUtil.prettyFormat(new String(Base64.decode(rbeMessage)), 2));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public String getRbeMessage() {
        return rbeMessage;
    }

    public void setRbeMessage(String message) {
        this.rbeMessage = message;
    }

    public String getHermesMessageId() {
        return hermesMessageId;
    }

    public void setHermesMessageId(String hermesMessageId) {
        this.hermesMessageId = hermesMessageId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(String rawMessage) {
        this.rawMessage = rawMessage;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public MessageStatus getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(MessageStatus lastStatus) {
        this.lastStatus = lastStatus;
    }

    public Date getLastStatusDate() {
        return lastStatusDate;
    }

    public void setLastStatusDate(Date lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
