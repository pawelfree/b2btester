package pl.bsb.b2btester.web.controller;

import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.entities.Message;
import pl.bsb.b2btester.model.manager.MessageManager;
import pl.bsb.b2btester.model.manager.RepositoryManager;
import pl.bsb.b2btester.util.Base64;
import pl.bsb.b2btester.util.HtmlUtil;

/**
 *
 * @author paweld
 */
@Named
@SessionScoped
public class MessageDetailsController implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MessageDetailsController.class);
    private static final long serialVersionUID = 17L;    
    private MessageDVO message;
    private MessageDVO acknowledgement;
    private MessageDVO error;
    private String messageContent;
    private String acknowledgementContent;
    private String errorContent;
    private String rbeMessage;
    @Inject
    private MessageController messageController;
    @Inject
    private MessageManager messageManager;
    @Inject
    private RepositoryManager repositoryManager;

    @PostConstruct
    public void init() {
    }

    public void initMessagesContent() {
        Message b2bMessage = messageController.getSelectedMessage();
        if (b2bMessage != null) {
            rbeMessage = b2bMessage.getRbeMessage();
            message = messageManager.getMessage(b2bMessage);
            messageContent = HtmlUtil.transcode(repositoryManager.getMessageContent(message));
            acknowledgement = messageManager.getMessageAcknowledgement(b2bMessage);
            error = messageManager.getMessageError(b2bMessage);
            if (acknowledgement.getMessageId() != null) {
                acknowledgementContent = HtmlUtil.transcode(HtmlUtil.prettyFormat(repositoryManager.getMessageContent(acknowledgement),1));
            }
            if (rbeMessage != null) {
                rbeMessage = HtmlUtil.transcode(HtmlUtil.prettyFormat(new String(Base64.decode(rbeMessage)),1));
            }
            if (error.getMessageId() != null) {
                errorContent = HtmlUtil.transcode(HtmlUtil.prettyFormat(repositoryManager.getMessageContent(error),1));
            }
            
        } else {
            //TODO trzeba zabronic? wyswietlania i jakis lepszy tekst w logerze
            logger.error("getMessagesContent: error=Cant get contents, message isnt selected");
        }
    }
    
    public StreamedContent asStream(String content) {
        return new DefaultStreamedContent(new ByteArrayInputStream(HtmlUtil.transcodeReverse(content).getBytes()),"text/xml","content.xml");
    }

    public MessageDVO getMessage() {
        return message;
    }

    public void setMessage(MessageDVO message) {
        this.message = message;
    }

    public MessageDVO getAcknowledgement() {
        return acknowledgement;
    }

    public void setAcknowledgement(MessageDVO acknowledgement) {
        this.acknowledgement = acknowledgement;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getAcknowledgementContent() {
        return acknowledgementContent;
    }

    public void setAcknowledgementContent(String acknowledgementContent) {
        this.acknowledgementContent = acknowledgementContent;
    }

    public String getRbeMessage() {
        return rbeMessage;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public MessageDVO getError() {
        return error;
    }
}
