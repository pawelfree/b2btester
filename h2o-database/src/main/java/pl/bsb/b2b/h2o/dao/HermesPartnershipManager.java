package pl.bsb.b2b.h2o.dao;

import hk.hku.cecid.ebms.spa.dao.PartnershipDAO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import hk.hku.cecid.ebms.spa.dao.PartnershipDataSourceDVO;
import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author paweld
 *
 */
public class HermesPartnershipManager {

    private static final Logger logger = LoggerFactory.getLogger(HermesPartnershipManager.class);
    private PartnershipDAO partnershipDAO;

    HermesPartnershipManager(DAOFactory factory) {
        try {
            partnershipDAO = (PartnershipDAO) factory.createDAO(PartnershipDAO.class);
        } catch (DAOException ex) {
            logger.error("HermesPartnershipManager: error={}", ex.getMessage(), ex);
            throw new IllegalStateException("HermesPartnershipManager initialization exception", ex);
        }
    }

//TODO  D:\B2Btester\Oprogramowanie\hermes2_src_20100121\CorvusEbMS.Admin\src\hk\hku\cecid\ebms\admin\listener\PartnershipPageletAdaptor      
    public Properties computeProtocol(Properties properties) throws MalformedURLException {
        URL transportEndpointURL = new URL(properties.getProperty("transportEndpoint"));
        properties.setProperty("transportProtocol", transportEndpointURL.getProtocol());
        return properties;
    }

    public boolean update(PartnershipDVO partnershipDVO) {
        boolean result = false;
        try {
            result = partnershipDAO.persist(partnershipDVO);
        } catch (DAOException ex) {
            logger.error("HermesPartnershipManager.update: error={}", ex.getMessage(), ex);
        }
        return result;
    }
    
    public boolean delete(PartnershipDVO partnershipDVO) {
        boolean result = false;
        try {
            result = partnershipDAO.remove(partnershipDVO);
        } catch (DAOException ex) {
            logger.error("HermesPartnershipManager.update: error={}", ex.getMessage(), ex);
        }
        return result;
    }

    public List<PartnershipDVO> getAllPartnerships() {
        List<PartnershipDVO> partnershipList = new ArrayList<>();
        try {
            partnershipList.addAll(partnershipDAO.findAllPartnerships());
        } catch (DAOException ex) {
            logger.error("getAllPartnerships: error={}", ex.getMessage(), ex);
        }
        return partnershipList;
    }
    
    public boolean addPartnership(PartnershipDVO partnershipDVO) {
        boolean result = false;
        try {
            partnershipDAO.create(partnershipDVO);
            result = true;
        } catch (DAOException ex) {
            logger.error("addPartnership: error={}", ex.getMessage(), ex);
        }
        return result;
    }

    public boolean addPartnership(Properties properties) {
        boolean result = false;
        try {
            result = addPartnership(createDVO(computeProtocol(properties)));
        } catch (MalformedURLException ex) {
            logger.error("addPartnership: error={}", ex.getMessage(), ex);
        }
        return result;
    }

    public PartnershipDVO createDVO(Properties properties) {
        PartnershipDataSourceDVO dvo = new PartnershipDataSourceDVO();
        dvo.setData(properties);
        return dvo;
    }
}
