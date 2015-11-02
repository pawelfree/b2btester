package pl.bsb.b2b.h2o.dao;

import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDAO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDVO;
import hk.hku.cecid.ebms.spa.dao.RepositoryDataSourceDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author paweld
 */
public class HermesRepositoryManager {
    private static final Logger logger = LoggerFactory.getLogger(HermesRepositoryManager.class);
    private RepositoryDAO repositoryDAO;

    HermesRepositoryManager(DAOFactory factory) {
        try {
            repositoryDAO = (RepositoryDAO) factory.createDAO(RepositoryDAO.class);
        } catch (DAOException ex) {
            logger.error("HermesRepositoryManager: error={}", ex.getMessage(), ex);
            throw new IllegalStateException("HermesRepositoryManager initialization exception",ex);
        }
    }
    
    public RepositoryDVO getMessageFromRepository(MessageDVO message) {
        return getMessageFromRepository(message.getMessageId(), message.getMessageBox());
    }

    private RepositoryDVO getMessageFromRepository(String messageId, String messageBox) {
        Properties properties = new Properties();
        properties.put("messageBox", messageBox);
        properties.put("messageId",messageId);
        RepositoryDVO repositoryDVO = createDVO(properties);
        try {
            repositoryDAO.findRepository(repositoryDVO);
        } catch (DAOException ex) {
            logger.error("getMessageFromRepository : error={}", ex.getMessage(), ex);
        }
        return repositoryDVO;
    }    
    
    private RepositoryDVO createDVO(Properties properties) {
        RepositoryDataSourceDVO dvo = new RepositoryDataSourceDVO();
        dvo.setData(properties);
        return dvo;
    }    
}
