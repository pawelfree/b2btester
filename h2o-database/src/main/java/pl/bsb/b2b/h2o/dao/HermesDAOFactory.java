package pl.bsb.b2b.h2o.dao;

import hk.hku.cecid.piazza.commons.dao.DAOFactory;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAOFactory;
import hk.hku.cecid.piazza.commons.module.Component;
import java.util.Properties;
import javax.sql.DataSource;

/**
 *
 * @author paweld
 */
public class HermesDAOFactory extends DataSourceDAOFactory {

    private HermesPartnershipManager partnershipManager = null;
    private HermesMessageManager messageManager = null;
    private HermesRepositoryManager repositoryManager = null;
    private DAOFactory daoFactory = null;

    public HermesDAOFactory(DataSource dataSource) throws Exception {
        setDataSource(dataSource);
        Properties parameters = new Properties();
        parameters.put("config", "ebms.dao.xml");

        Component component = (Component) this;
        component.setId("daofactory");
        component.setName("System DAO Factory");
        component.setParameters(parameters);

        component.init();
        daoFactory = (DAOFactory) component;
    }

    @Override
    public void initFactory() {
    }
    
    public HermesRepositoryManager getRepositoryManager() {
        if (repositoryManager == null) {
            repositoryManager = new HermesRepositoryManager(daoFactory);
        }
        return repositoryManager;
    }

    public HermesPartnershipManager getPartnershipManager() {
        if (partnershipManager == null) {
            partnershipManager = new HermesPartnershipManager(daoFactory);
        }
        return partnershipManager;
    }

    public HermesMessageManager getMessageManager() {
        if (messageManager == null) {
            messageManager = new HermesMessageManager(daoFactory);
        }
        return messageManager;
    }
}
