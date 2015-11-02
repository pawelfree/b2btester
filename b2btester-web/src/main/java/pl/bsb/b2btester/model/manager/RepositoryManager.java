package pl.bsb.b2btester.model.manager;

import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDVO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2b.h2o.dao.HermesDAOFactory;
import pl.bsb.b2b.h2o.dao.HermesRepositoryManager;
import pl.bsb.b2btester.jdbc.JDBCDataSource;

/**
 *
 * @author paweld
 */
@Named
public class RepositoryManager implements Serializable {

    private static final long serialVersionUID = 8L;
    private static final Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
    @Inject
    private JDBCDataSource jDBCDataSource;
    private transient HermesDAOFactory daoFactory;

    private HermesRepositoryManager getManager() {
        try {
            if (daoFactory == null) {
                daoFactory = new HermesDAOFactory(jDBCDataSource);
            }
        } catch (Exception ex) {
            logger.error("getManager: error={}", ex.getMessage(), ex);
            throw new IllegalStateException("getManager : error=Cant initialize HermesDAOFactory", ex);
        }
        return daoFactory.getRepositoryManager();
    }

    @PostConstruct
    public void init() {
        daoFactory = null;
    }

    public String getMessageContent(MessageDVO message) {

        try {

            RepositoryDVO dvo = getManager().getMessageFromRepository(message);
            byte[] content = dvo.getContent();
            String contentType = dvo.getContentType();
            //TODO obsluzyc pustke
            // reconstruct the ebxml message
            MimeHeaders mimeHeaders = new MimeHeaders();
            mimeHeaders.setHeader("Content-Type", contentType);
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage(mimeHeaders, new ByteArrayInputStream(content));
            OutputStream baos = new ByteArrayOutputStream();
            soapMessage.writeTo(baos);
            return ((ByteArrayOutputStream) baos).toString();
        } catch (IOException | SOAPException ex) {
            logger.error("getMessage: error={}", ex.getMessage(), ex);
            return "";
        }

    }
}
