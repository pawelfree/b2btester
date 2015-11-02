package pl.bsb.b2b.h2o.dao;

import hk.hku.cecid.ebms.spa.dao.MessageDAO;
import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.MessageDataSourceDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author paweld
 */
public class HermesMessageManager {

    private static final Logger logger = LoggerFactory.getLogger(HermesMessageManager.class);
    private MessageDAO messageDAO;
    static String MESSAGE_TYPE_ERROR = "Error";
    static String MESSAGE_TYPE_ACKNOWLEDGEMENT = "Acknowledgement";
    static String MESSAGE_TYPE_ORDER = "Order";
    static String MESSAGE_TYPE_PROCESSED_ERROR = "ProcessedError";

    HermesMessageManager(DAOFactory factory) {
        try {
            messageDAO = (MessageDAO) factory.createDAO(MessageDAO.class);
        } catch (DAOException ex) {
            logger.error("HermesMessageManager: error={}", ex.getMessage(), ex);
            throw new IllegalStateException("HermesMessageManager initialization exception", ex);
        }
    }
    
    public MessageDVO getMessage(String messageId, String messageBox) {
        Properties properties = new Properties();
        properties.put("messageType", MESSAGE_TYPE_ORDER);
        properties.put("messageBox", messageBox);
        properties.put("messageId",messageId);
        MessageDVO messageDVO = createDVO(properties);
        try {
            if (false == messageDAO.findMessage(messageDVO)) {
                messageDVO = null;
            }
        } catch (DAOException ex) {
            logger.error("getMessageAcknowledgement: error={}", ex.getMessage(), ex);
        }
        return messageDVO;
    }
    
    public MessageDVO getMessageError(String messageId, String messageBox) {
        Properties properties = new Properties();
        properties.put("messageType", MESSAGE_TYPE_ERROR);
        properties.put("messageBox", messageBox);
        properties.put("refToMessageId",messageId);
        MessageDVO messageDVO = createDVO(properties);
        try {
            if (false == messageDAO.findRefToMessage(messageDVO)) {
                messageDVO = null;
            }
        } catch (DAOException ex) {
            logger.error("getMessageError: error={}", ex.getMessage(), ex);
        }
        return messageDVO;
    }   
    
    public MessageDVO getMessageAcknowledgement(String messageId, String messageBox) {
        Properties properties = new Properties();
        properties.put("messageType", MESSAGE_TYPE_ACKNOWLEDGEMENT);
        properties.put("messageBox", messageBox);
        properties.put("refToMessageId",messageId);
        MessageDVO messageDVO = createDVO(properties);
        try {
            if (false == messageDAO.findRefToMessage(messageDVO)) {
                messageDVO = null;
            }
        } catch (DAOException ex) {
            logger.error("getMessageAcknowledgement: error={}", ex.getMessage(), ex);
        }
        return messageDVO;
    }
    
    public MessageDVO createDVO(Properties properties) {
        MessageDataSourceDVO dvo = new MessageDataSourceDVO();
        dvo.setData(properties);
        return dvo;
    }
}
