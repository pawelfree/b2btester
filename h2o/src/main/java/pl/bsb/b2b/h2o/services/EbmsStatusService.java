package pl.bsb.b2b.h2o.services;

import java.net.MalformedURLException;
import pl.bsb.b2b.h2o.util.SOAPUtil;

import javax.xml.soap.*;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.bsb.b2b.h2o.helper.EmptyParametersException;
import pl.bsb.b2b.h2o.helper.MessageInfo;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 10:18
 */
public class EbmsStatusService extends EbmsService implements IEbmsStatusService {

    @Override
    public MessageInfo getStatus(String messageId) throws EmptyParametersException {
        
        if (properties == null) throw new EmptyParametersException();
        
        try{
            SOAPMessage request = prepareMesssage(messageId);
            return getMessageStatus(request, new URL(properties.getProperty("statusWSURL")));
        }
        catch (SOAPException | MalformedURLException ex) {
            Logger.getLogger(EbmsStatusService.class.getName()).log(Level.SEVERE, "{0} : {1}", new Object[]{ex.getClass().getName(), ex.getMessage()});
        }
        return null;
    }

    
    private SOAPMessage prepareMesssage(String messageId) throws SOAPException {

        SOAPMessage request = MessageFactory.newInstance().createMessage();
        SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
        soapBody.addChildElement(SOAPUtil.createElement("messageId", messageId));
        request.saveChanges();
        
        return request;
    }
    
    
    private MessageInfo getMessageStatus(SOAPMessage request, URL receiverListWSURL) throws UnsupportedOperationException, SOAPException  {
        SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
        SOAPMessage response = soapConn.call(request, receiverListWSURL);
        SOAPBody responseBody = response.getSOAPBody();

        if (!responseBody.hasFault()) {

            SOAPElement messageInfoElement = SOAPUtil.getFirstChild(responseBody, "messageInfo", nsURI);

            MessageInfo messageInfo = new MessageInfo();
            SOAPElement soapElement = SOAPUtil.getFirstChild(messageInfoElement, "ackMessageId", nsURI);
            messageInfo.setAckMessageId(soapElement == null ? "" : soapElement.getValue());
            soapElement = SOAPUtil.getFirstChild(messageInfoElement, "ackStatus", nsURI);
            messageInfo.setAckStatus(soapElement == null ? "" : soapElement.getValue());
            soapElement = SOAPUtil.getFirstChild(messageInfoElement, "ackStatusDescription", nsURI);
            messageInfo.setAckStatusDescription(soapElement == null ? "" : soapElement.getValue());
            soapElement = SOAPUtil.getFirstChild(messageInfoElement, "status", nsURI);
            messageInfo.setStatus(soapElement == null ? "" : soapElement.getValue());
            soapElement = SOAPUtil.getFirstChild(messageInfoElement, "statusDescription", nsURI);
            messageInfo.setStatusDescription(soapElement == null ? "" : soapElement.getValue());
            return messageInfo;
        } else {
            Logger.getLogger(EbmsStatusService.class.getName()).log(Level.SEVERE, responseBody.getFault().getFaultString());
            throw new SOAPException(responseBody.getFault().getFaultString());
        }    
    }
}
