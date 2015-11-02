package pl.bsb.b2b.h2o.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import pl.bsb.b2b.h2o.util.SOAPUtil;

/**
 * Created by Pawel Dudek (paweld) Date: 04.09.11 Time: 10:18
 */
public class WspingService extends EbmsService implements IWspingService {

    @Override
    public boolean ping() {
        if (properties == null) {
            Logger.getLogger(WspingService.class.getName()).log(Level.SEVERE, "Connection properties are empty");
            return false;
        }
        try {
            SOAPMessage request = prepareMesssage();
            return getPing(request, new URL(properties.getProperty("wspingWSURL")));
        } catch (SOAPException | MalformedURLException e) {
            Logger.getLogger(WspingService.class.getName()).log(Level.SEVERE, "{0} : {1}", new Object[]{e.getClass().getName(), e.getMessage()});
        }
        return false;
    }

    private boolean getPing(SOAPMessage request, URL wspingWSURL) throws UnsupportedOperationException, SOAPException {
        SOAPConnection soapConn = SOAPConnectionFactory.newInstance().createConnection();
        SOAPMessage response = soapConn.call(request, wspingWSURL);
        SOAPBody responseBody = response.getSOAPBody();

        if (!responseBody.hasFault()) {
            SOAPElement pong = SOAPUtil.getFirstChild(responseBody, "action", nsPingURI);
            return pong == null ? false : (pong.getValue() == null ? false : pong.getValue().equalsIgnoreCase("pong"));
        } else {
            throw new SOAPException(responseBody.getFault().getFaultString());
        }
    }

    private SOAPMessage prepareMesssage() throws SOAPException {

        SOAPMessage request = MessageFactory.newInstance().createMessage();
        SOAPBody soapBody = request.getSOAPPart().getEnvelope().getBody();
        soapBody.addChildElement(SOAPUtil.createElement("action", "ping"));
        request.saveChanges();

        return request;
    }
}
