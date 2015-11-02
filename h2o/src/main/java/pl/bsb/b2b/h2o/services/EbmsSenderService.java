package pl.bsb.b2b.h2o.services;

import pl.bsb.b2b.h2o.helper.EmptyParametersException;
import pl.bsb.b2b.h2o.helper.IPayload;
import pl.bsb.b2b.h2o.helper.Payload;
import pl.bsb.b2b.h2o.util.SOAPUtil;

import javax.xml.soap.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 10:10
 */
public class EbmsSenderService extends EbmsService implements IEbmsSenderService {

    public EbmsSenderService () {
    }

    @Override
    public String send(InputStream dataToSend) throws EmptyParametersException {
        if (properties == null) throw new EmptyParametersException();

        IPayload[] payloads = new IPayload[1];
        payloads[0] = new Payload(dataToSend,"application/octet-stream");

        try{
            SOAPMessage request = prepareMesssage(payloads, properties);
            return sendMessage(request,new URL(properties.getProperty("senderWSURL")));
        }
        catch (SOAPException | IOException e) {
            Logger.getLogger(EbmsSenderService.class.getName()).log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getMessage()});
            return "";
        }
    }

    private SOAPMessage prepareMesssage(IPayload[] payloads, Properties properties) throws SOAPException, IOException {

        SOAPMessage request = MessageFactory.newInstance().createMessage();
        
        SOAPBody soapBody = request.getSOAPBody();
        soapBody.addChildElement(SOAPUtil.createElement("cpaId", nsPrefix, nsURI, properties.getProperty("cpaId")));
        soapBody.addChildElement(SOAPUtil.createElement("service", nsPrefix, nsURI, properties.getProperty("service")));
        soapBody.addChildElement(SOAPUtil.createElement("action", nsPrefix, nsURI, properties.getProperty("action")));
        soapBody.addChildElement(SOAPUtil.createElement("convId", nsPrefix, nsURI, properties.getProperty("convId")));
        soapBody.addChildElement(SOAPUtil.createElement("fromPartyId", nsPrefix, nsURI, properties.getProperty("fromPartyId")));
        soapBody.addChildElement(SOAPUtil.createElement("fromPartyType", nsPrefix, nsURI, properties.getProperty("fromPartyType")));
        soapBody.addChildElement(SOAPUtil.createElement("toPartyId", nsPrefix, nsURI, properties.getProperty("toPartyId")));
        soapBody.addChildElement(SOAPUtil.createElement("toPartyType", nsPrefix, nsURI, properties.getProperty("toPartyType")));
        if (properties.getProperty("refToMessageId") != null){
            soapBody.addChildElement(SOAPUtil.createElement("refToMessageId", nsPrefix, nsURI, properties.getProperty("refToMessageId")));
        }
        request.saveChanges();
        
        for (IPayload payload : payloads) {
            AttachmentPart attachmentPart = request.createAttachmentPart();
            attachmentPart.setRawContent(payload.getInputStream(),payload.getContentType());
            request.addAttachmentPart(attachmentPart);
        }

        request.saveChanges();

        return request;
    }

    private String sendMessage(SOAPMessage request, URL senderWSURL) throws SOAPException {

        SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
        
        SOAPMessage response = soapConn.call(request, senderWSURL);
        SOAPBody responseBody = response.getSOAPBody();

        if (!responseBody.hasFault()) {
            SOAPElement messageIdElement = SOAPUtil.getFirstChild(responseBody, "message_id", nsURI);
            return messageIdElement == null ? null : messageIdElement.getValue();
        } else {
            throw new SOAPException(responseBody.getFault().getFaultString());
        }
    }


}
