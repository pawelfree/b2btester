package pl.bsb.b2b.h2o.util;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import java.util.Iterator;


public class SOAPUtil {

    public static SOAPElement createElement(String localName, String nsPrefix, String nsURI, String value)
            throws SOAPException {
        SOAPElement soapElement = SOAPFactory.newInstance().createElement(localName, nsPrefix, nsURI);
        soapElement.addTextNode(value);
        return soapElement;
    }

    public static SOAPElement createElement(String localName, String value)
            throws SOAPException {
        SOAPElement soapElement = SOAPFactory.newInstance().createElement(localName);
        soapElement.addTextNode(value);
        return soapElement;
    }
    
    public static SOAPElement getFirstChild(SOAPElement soapElement, String childLocalName, String childNsURI)
            throws SOAPException {
        Name childName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
        Iterator childIter = soapElement.getChildElements(childName);
        if (childIter.hasNext()) {
            return (SOAPElement) childIter.next();
        } else {
            return null;
        }
    }

    public static Iterator getChildren(SOAPElement soapElement, String childLocalName, String childNsURI)
            throws SOAPException {
        Name childrenName = SOAPFactory.newInstance().createName(childLocalName, null, childNsURI);
        return soapElement.getChildElements(childrenName);
    }

}
