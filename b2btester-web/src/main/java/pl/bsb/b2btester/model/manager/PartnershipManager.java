package pl.bsb.b2btester.model.manager;

import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2b.h2o.dao.HermesDAOFactory;
import pl.bsb.b2b.h2o.dao.HermesPartnershipManager;
import pl.bsb.b2btester.jdbc.JDBCDataSource;
import pl.bsb.b2btester.model.entities.Server;
import pl.bsb.b2btester.type.ParamKey;
import pl.bsb.b2btester.web.helper.ActionType;
import pl.bsb.b2btester.web.helper.MessageBundle;

/**
 *
 * @author paweld
 */
@Named
public class PartnershipManager implements Serializable {

    private static final long serialVersionUID = 9L;
    private static final Logger logger = LoggerFactory.getLogger(PartnershipManager.class);
    private Server server;
    @Inject
    private JDBCDataSource jDBCDataSource;
    @Inject
    private ParameterManager parameterManager;
    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;
    @Inject
    private UniqueId uniqueId;
    private transient HermesDAOFactory daoFactory;

    private HermesPartnershipManager getManager() {
        try {
            if (daoFactory == null) {
                daoFactory = new HermesDAOFactory(jDBCDataSource);
            }
        } catch (Exception ex) {
            logger.error("getManager: error={}", ex.getMessage(), ex);
            throw new IllegalStateException("getManager : error=Cant initialize HermesDAOFactory", ex);
        }
        return daoFactory.getPartnershipManager();
    }

    @PostConstruct
    public void init() {
        daoFactory = null;
    }

    private void updateDataSource() {
        if (server != null) {
            jDBCDataSource.setParameters(server.getDbAddress(), server.getDbPort(),
                    server.getDbName(), server.getDbUser(), server.getDbPassword());
        }
    }

    public boolean addPartnership(PartnershipDVO partnershipDVO) {
        partnershipDVO.setPartnershipId(partnershipDVO.getAction().concat("_").concat(uniqueId.nextId()));
        return getManager().addPartnership(partnershipDVO);
    }

    public PartnershipDVO createPartnershipDVO() {
        return getManager().createDVO(defaultPartnershipProperties());
    }

    public boolean updatePartnership(PartnershipDVO partnershipDVO) {
        return getManager().update(partnershipDVO);
    }

    public boolean deletePartnership(PartnershipDVO partnershipDVO) {
        return getManager().delete(partnershipDVO);
    }

    public List<PartnershipDVO> getAllPartnerships(Server server) {
        //setServer(server);
        return getManager().getAllPartnerships();
    }

    public List<PartnershipDVO> getAllPartnerships() {
        updateDataSource();
        return getManager().getAllPartnerships();
    }

    public void addAll() {
        updateDataSource();
        Properties properties = defaultPartnershipProperties();
        String uniquePartnershipID;
        for (ActionType action : ActionType.values()) {
            uniquePartnershipID = uniqueId.nextId();
            properties.setProperty("partnershipId", action.name().concat("_").concat(uniquePartnershipID));
            properties.setProperty("action", action.name());
            getManager().addPartnership(properties);
        }
    }

    private Properties defaultPartnershipProperties() {
        Properties properties = new Properties();
        try {
            properties.put("cpaId", parameterManager.getByKey(ParamKey.PARTNERSHIP_CPAID).getValue());
            properties.put("service", parameterManager.getByKey(ParamKey.PARTNERSHIP_SERVICE).getValue());
            properties.put("action", parameterManager.getByKey(ParamKey.PARTNERSHIP_ACTION).getValue());
            properties.put("disabled", parameterManager.getByKey(ParamKey.PARTNERSHIP_DISABLED).getValue());
            properties.put("syncReplyMode", parameterManager.getByKey(ParamKey.PARTNERSHIP_SYNC_REPLY_MODE).getValue());
            properties.put("transportEndpoint", parameterManager.getByKey(ParamKey.PARTNERSHIP_TRANSPORT_ENDPOINT).getValue());
            properties.put("ackRequested", parameterManager.getByKey(ParamKey.PARTNERSHIP_ACK_REQUESTED).getValue());
            properties.put("ackSignRequested", parameterManager.getByKey(ParamKey.PARTNERSHIP_ACK_SIGN_REQUESTED).getValue());
            properties.put("dupElimination", parameterManager.getByKey(ParamKey.PARTNERSHIP_DUP_ELIMINATION).getValue());
            properties.put("messageOrder", parameterManager.getByKey(ParamKey.PARTNERSHIP_MESSAGE_ORDER).getValue());
            properties.put("retries", Integer.parseInt(parameterManager.getByKey(ParamKey.PARTNERSHIP_RETRIES).getValue()));
            properties.put("retryInterval", Integer.parseInt(parameterManager.getByKey(ParamKey.PARTNERSHIP_RETRY_INTERVAL).getValue()));
            properties.put("signRequested", parameterManager.getByKey(ParamKey.PARTNERSHIP_SIGN_REQUESTED).getValue());
            properties.put("isHostnameVerified", parameterManager.getByKey(ParamKey.PARTNERSHIP_IS_HOSTNAME_VERIFIED).getValue());
            properties.put("encryptRequested", parameterManager.getByKey(ParamKey.PARTNERSHIP_ENCRYPT_REQUESTED).getValue());
            properties = getManager().computeProtocol(properties);
        } catch (MalformedURLException ex) {
            logger.error("defaultPartnershipProperties: error={}", ex.getMessage(), ex);
        }
        return properties;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public JDBCDataSource getjDBCDataSource() {
        return jDBCDataSource;
    }

    public void setjDBCDataSource(JDBCDataSource jDBCDataSource) {
        this.jDBCDataSource = jDBCDataSource;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public UniqueId getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(UniqueId uniqueId) {
        this.uniqueId = uniqueId;
    }
}
