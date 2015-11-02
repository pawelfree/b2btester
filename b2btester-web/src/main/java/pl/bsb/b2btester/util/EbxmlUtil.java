package pl.bsb.b2btester.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.validation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import pl.bsb.b2btester.util.resolver.ClasspathResourceResolver;

/**
 *
 * @author paweld
 */
public class EbxmlUtil {

    private static final Logger logger = LoggerFactory.getLogger(EbxmlUtil.class);

    public static boolean validateAgainstXSD(String path, Source xml, Source xsd) {
        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new ClasspathResourceResolver(path));
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(xml);
            return true;
        } catch (SAXException | IOException ex) {
            logger.info("validateAgainstXSD: error={}", ex.getMessage(), ex);
            return false;
        }
    }

    public static String getDocumentIdentifier(byte[] message) throws IOException, XMLStreamException {
        return getMessageAttribute("DocumentIdentifier", message);
    }
    
    public static String getMessageSender(byte[] message) throws IOException, XMLStreamException {
        return getMessageAttribute("SenderIdentifier", message);        
    }

    public static String getMessageReceiver(byte[] message) throws IOException, XMLStreamException {
        return getMessageAttribute("ReceiverIdentifier", message);        
    }
    
    private static String getMessageAttribute(String attribute, byte[] message) throws IOException, XMLStreamException {
        XMLStreamReader r = null;
        try (InputStream is = new ByteArrayInputStream(message);) {
            XMLInputFactory f = XMLInputFactory.newInstance();
            r = f.createXMLStreamReader(is);
            int event = r.getEventType();
            boolean process = false;
            while (true) {
                switch (event) {
                    case XMLStreamConstants.CHARACTERS:
                        if (process && null != r.getText()) {
                            return r.getText().trim();
                        }
                        break;
                    case XMLStreamConstants.START_ELEMENT:
                        if (r.getLocalName().endsWith(attribute)) {
                            process = true;
                        }
                        break;
                }
                if (!r.hasNext()) {
                    break;
                }
                event = r.next();
            }
        } finally {
            if (r != null) {
                r.close();
            }
        }
        return "";
    }
       
    public static String getMessgeType(byte[] message) throws IOException, XMLStreamException {
        try (InputStream is = new ByteArrayInputStream(message);) {
            XMLInputFactory f = XMLInputFactory.newInstance();
            XMLStreamReader r = null;
            try {
                r = f.createXMLStreamReader(is);
                int event = r.getEventType();
                while (true) {
                    switch (event) {
                        case XMLStreamConstants.START_DOCUMENT:
                            break;
                        case XMLStreamConstants.START_ELEMENT:
                            try {
                                return r.getLocalName();
                            } catch (IllegalArgumentException ex) {
                                logger.info("getMessgeType: error={}", ex.getMessage(), ex);
                                return null;
                            }
                    }
                    if (!r.hasNext()) {
                        break;
                    }
                    event = r.next();
                }
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        }
        return null;
    }

    //TODO to chyyba jest zbyt grzeczne -  zweryfikowa czy schema location jest konieczne
    public static String getMessageVersion(byte[] message) throws IOException, XMLStreamException {
        String defaultVersion = "1.0";
        try (InputStream is = new ByteArrayInputStream(message);) {
            XMLInputFactory f = XMLInputFactory.newInstance();
            XMLStreamReader r = null;
            try {
                r = f.createXMLStreamReader(is);
                int event = r.getEventType();
                while (true) {
                    switch (event) {
                        case XMLStreamConstants.START_DOCUMENT:
                            break;
                        case XMLStreamConstants.START_ELEMENT:
                            try {
                                // Check attributes for first start tag
                                for (int i = 0; i < r.getAttributeCount(); i++) {
                                    // Get attribute name
                                    String attrName = r.getAttributeName(i).toString();
                                    if (attrName.endsWith("noNamespaceSchemaLocation")) {
                                        // Return value
                                        String attrValue = r.getAttributeValue(i);
                                        if (attrValue.indexOf("/") > 1) {
                                            return attrValue.substring(0, attrValue.indexOf("/"));
                                        } else {
                                            return defaultVersion;
                                        }
                                    }
                                }
                                return defaultVersion;
                            } catch (IllegalArgumentException ex) {
                                logger.info("getMessgeType: error={}", ex.getMessage(), ex);
                                return defaultVersion;
                            }
                    }
                    if (!r.hasNext()) {
                        break;
                    }
                    try {
                        event = r.next();
                    } catch (XMLStreamException ex) {
                        logger.info("getMessgeType: error={}", ex.getMessage(), ex);
                        return defaultVersion;
                    }
                }
            } finally {
                if (r != null) {
                    r.close();
                }
            }
        } catch(Exception ex) {
            logger.error("getMessageType: error{}", ex.getMessage(), ex); 
        }
        return defaultVersion;
    }
}
