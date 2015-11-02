package pl.bsb.b2b.h2o.services;

import pl.bsb.b2b.h2o.helper.EmptyParametersException;
import pl.bsb.b2b.h2o.helper.IPayload;
import pl.bsb.b2b.h2o.helper.Payload;
import pl.bsb.b2b.h2o.util.SOAPUtil;

import javax.xml.soap.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Pawel Dudek (paweld) Date: 04.09.11 Time: 10:18
 */
public class EbmsReceiverService extends EbmsService implements IEbmsReceiverService {

    @Override
    public List<String> getReceivedMessagesIds() throws EmptyParametersException {
        if (properties == null) {
            throw new EmptyParametersException();
        }

        try {
            SOAPMessage request = prepareMesssage(properties);
            return getMessagesIds(request, new URL(properties.getProperty("receiverListWSURL")));
        } catch (SOAPException | MalformedURLException | UnsupportedOperationException e) {
            Logger.getLogger(EbmsSenderService.class.getName()).log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getMessage()});
            return new ArrayList<>(1);
        }
    }

    @Override
    public InputStream getMessage(String messageId) throws EmptyParametersException {
        if (properties == null) {
            throw new EmptyParametersException();
        }
        try {
            Iterator<IPayload> iterator;
            iterator = downloadPlds(messageId, new URL(properties.getProperty("receiverWSURL")));
            IPayload payload = new Payload();
            if (iterator.hasNext()) {
                payload = iterator.next();
                if (iterator.hasNext()) {
                    //TODO duży problem wiele payloads
                    System.err.println("ERROR, ERROR, ERROR !!!!! ----- many payloads ----- !!!!!");
                }
            }
            return payload.getInputStream();
        } catch (SOAPException | IOException e) {
            Logger.getLogger(EbmsSenderService.class.getName()).log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getMessage()});
            return null;
        }
    }

    //TODO cos trzeba zrobic z tym IOException
    private Iterator<IPayload> downloadPlds(String messageId, URL receiverWSURL) throws SOAPException, IOException {
        // Make a SOAP Connection and SOAP Message.
        SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
        SOAPMessage request = MessageFactory.newInstance().createMessage();

        SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
        soapBody.addChildElement(SOAPUtil.createElement("messageId", nsPrefix, nsURI, messageId));
        request.saveChanges();

        SOAPMessage response = soapConn.call(request, receiverWSURL);
        SOAPBody responseBody = response.getSOAPBody();

        /*
         * The response is something like:
         * <soap-body>
         * 	<hasMessage>
         * </soap-body>
         * 		.
         * attachment as a MIME part.
         */

        if (!responseBody.hasFault()) {
            // See whether has <hasMessage> element.
            SOAPElement hasMessageElement = SOAPUtil.getFirstChild(responseBody, "hasMessage", nsURI);
            List<IPayload> payloadsList = new ArrayList<>();
            if (hasMessageElement != null) {
                Iterator attachmentPartIter = response.getAttachments();
                while (attachmentPartIter.hasNext()) {
                    AttachmentPart attachmentPart = (AttachmentPart) attachmentPartIter.next();
                    // Add a new payload to the payload list.
                    Payload payload = new Payload(
                            attachmentPart.getDataHandler().getInputStream(),
                            attachmentPart.getContentType());
                    payloadsList.add(payload);
                }
            }
            return payloadsList.iterator();
        } else {
            throw new SOAPException(responseBody.getFault().getFaultString());
        }
    }

    private SOAPMessage prepareMesssage(Properties properties) throws SOAPException {

        SOAPMessage request = MessageFactory.newInstance().createMessage();

        SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();

        soapBody.addChildElement(SOAPUtil.createElement("cpaId", nsPrefix, nsURI, properties.getProperty("cpaId")));
        soapBody.addChildElement(SOAPUtil.createElement("service", nsPrefix, nsURI, properties.getProperty("service")));
        soapBody.addChildElement(SOAPUtil.createElement("action", nsPrefix, nsURI, properties.getProperty("action")));
        if (properties.getProperty("convId") != null) {
            soapBody.addChildElement(SOAPUtil.createElement("convId", nsPrefix, nsURI, properties.getProperty("convId")));
        }
        //przy komunikatach odbieranych następuje zamiana nadawcy z odbiorcą
        soapBody.addChildElement(SOAPUtil.createElement("fromPartyId", nsPrefix, nsURI, properties.getProperty("toPartyId")));
        soapBody.addChildElement(SOAPUtil.createElement("fromPartyType", nsPrefix, nsURI, properties.getProperty("toPartyType")));
        soapBody.addChildElement(SOAPUtil.createElement("toPartyId", nsPrefix, nsURI, properties.getProperty("fromPartyId")));
        soapBody.addChildElement(SOAPUtil.createElement("toPartyType", nsPrefix, nsURI, properties.getProperty("fromPartyType")));

        soapBody.addChildElement(SOAPUtil.createElement("numOfMessages", nsPrefix, nsURI, properties.getProperty("numOfMessages")));
        request.saveChanges();

        Logger.getLogger(EbmsReceiverService.class.getName()).log(Level.FINE, "Recive message ids for : "
                .concat("\ncpaId : ").concat(properties.getProperty("cpaId", "empty"))
                .concat("\nservice : ").concat(properties.getProperty("service", "empty"))
                .concat("\naction : ").concat(properties.getProperty("action", "empty"))
                .concat("\nconvId : ").concat(properties.getProperty("convId", "empty"))
                .concat("fromPartyId : ").concat(properties.getProperty("toPartyId", "empty"))
                .concat("fromPartyType : ").concat(properties.getProperty("toPartyType", "empty"))
                .concat("toPartyId : ").concat(properties.getProperty("fromPartyId", "empty"))
                .concat("toPartyType : ").concat(properties.getProperty("fromPartyType", "empty")));
        return request;
    }

    private List<String> getMessagesIds(SOAPMessage request, URL receiverListWSURL) throws UnsupportedOperationException, SOAPException {

        SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
        SOAPMessage response = soapConn.call(request, receiverListWSURL);
        SOAPBody responseBody = response.getSOAPBody();

        if (!responseBody.hasFault()) {
            SOAPElement messageIdsElement = SOAPUtil.getFirstChild(responseBody, "messageIds", nsURI);
            Iterator messageIdElementIter = SOAPUtil.getChildren(messageIdsElement, "messageId", nsURI);

            List<String> messageIdsList = new ArrayList<>();
            while (messageIdElementIter.hasNext()) {
                SOAPElement messageIdElement = (SOAPElement) messageIdElementIter.next();
                messageIdsList.add(messageIdElement.getValue());
            }
            return messageIdsList;
        } else {
            Logger.getLogger(EbmsReceiverService.class.getName()).log(Level.SEVERE, responseBody.getFault().getFaultString());
            throw new SOAPException(responseBody.getFault().getFaultString());
        }
    }
}
