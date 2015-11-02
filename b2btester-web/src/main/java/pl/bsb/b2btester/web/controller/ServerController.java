package pl.bsb.b2btester.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIInput;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2b.h2o.Hermes2;
import pl.bsb.b2b.h2o.services.IWspingService;
import pl.bsb.b2btester.helper.BeanHelper;
import pl.bsb.b2btester.jdbc.JDBCDataSource;
import pl.bsb.b2btester.model.dao.DAOException;
import pl.bsb.b2btester.model.entities.Server;
import pl.bsb.b2btester.model.manager.ParameterManager;
import pl.bsb.b2btester.model.manager.PartnershipManager;
import pl.bsb.b2btester.model.manager.ServerManager;
import pl.bsb.b2btester.type.ParamKey;
import pl.bsb.b2btester.web.helper.MessageBundle;

/**
 *
 * @author paweld
 */
@Named
@SessionScoped
public class ServerController implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private static final long serialVersionUID = 1L;
    //informacje o serwerze hermes
    private String serverName;
    private String serverAddress;
    private String serverSenderPath;
    private String serverStatusPath;
    private String serverReceiverPath;
    private String serverReceiverListPath;
    private String serverWspingPath;
    private String pingStatus;
    //informacje o serwerze db
    private String dbAddress;
    private Integer dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String databaseConnectionStatus;
    private Server server;
    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;
    @Inject
    protected JDBCDataSource jDBCDataSource;
    @Inject
    protected ServerManager serverManager;
    @Inject
    protected PartnershipManager partnershipManager;
    @Inject
    protected ParameterManager parameterManager;
    @Inject
    protected PartnershipController partnershipController;
    @Inject
    protected BeanHelper beanHelper;
    private List<Server> serverList;
    private Server selectedServer;
    private Boolean createPartnerships;

    public Boolean getCreatePartnerships() {
        return createPartnerships;
    }

    public void setCreatePartnerships(Boolean createPartnerships) {
        this.createPartnerships = createPartnerships;
    }

    public void saveSelected() {
        try {
            serverManager.merge(selectedServer);
        } catch (DAOException ex) {
            logger.error("Save server failed", ex);
        }
    }

    public void saveNew() {

        server = new Server();
        server.setName(getServerName());
        server.setAddress(getServerAddress());
        server.setReceiverListPath(getServerReceiverListPath());
        server.setSenderPath(getServerSenderPath());
        server.setStatusPath(getServerStatusPath());
        server.setReceiverPath(getServerReceiverPath());
        server.setWspingPath(getServerWspingPath());
        server.setDbAddress(getDbAddress());
        server.setDbPort(getDbPort());
        server.setDbName(getDbName());
        server.setDbUser(getDbUser());
        server.setDbPassword(getDbPassword());

        List<String> errors;
        for (Server srv : getAllServers()) {
            errors = srv.compare(server);
            if (errors.size() > 0) {
                logger.debug("Server already exists");
                for (String s : errors) {
                    Messages.addError("saveNewServer", bundle.getString(s));
                }
                Faces.getContext().validationFailed();
                return;
            }
        }

        try {
            serverManager.persist(server);
            if (createPartnerships) {
                addAllPartnerships(server);
            }
            addServer(server);
        } catch (DAOException ex) {
            logger.error("saveNew: error={}", ex.getMessage(), ex);
            Faces.getContext().validationFailed();
        }
    }

    public void addAllPartnerships(Server srv) {
        partnershipManager.setServer(srv);
        partnershipManager.addAll();
    }

    private void addServer(Server server) {
        serverList.add(server);
    }

    public void wspingPathChanged(AjaxBehaviorEvent e) {
        serverWspingPath = ((UIInput) e.getComponent()).getValue().toString();
    }

    public void dbAddressChanged(AjaxBehaviorEvent e) {
        dbAddress = ((UIInput) e.getComponent()).getValue().toString();
    }

    public void dbPortChanged(AjaxBehaviorEvent e) {
        dbPort = Integer.parseInt(((UIInput) e.getComponent()).getValue().toString());
    }

    public void dbNameChanged(AjaxBehaviorEvent e) {
        dbName = ((UIInput) e.getComponent()).getValue().toString();
    }

    public void dbUserChanged(AjaxBehaviorEvent e) {
        dbUser = ((UIInput) e.getComponent()).getValue().toString();
    }

    public void dbPasswordChanged(AjaxBehaviorEvent e) {
        dbPassword = ((UIInput) e.getComponent()).getValue().toString();
    }

    public void init() {
        serverList = getAllServers();
        createPartnerships = false;
        if (parameterManager.getAllCount() == 0L) {
            throw new IllegalStateException("Configuration table is empty !!!");
        }
        setServerAddress(parameterManager.getByKey(ParamKey.SERVER_ADDRESS).getValue());
        setServerName(parameterManager.getByKey(ParamKey.SERVER_NAME).getValue());
        setServerReceiverListPath(parameterManager.getByKey(ParamKey.RECEIVER_LIST_PATH_ENDING).getValue());
        setServerReceiverPath(parameterManager.getByKey(ParamKey.RECEIVER_PATH_ENDING).getValue());
        setServerSenderPath(parameterManager.getByKey(ParamKey.SENDER_PATH_ENDING).getValue());
        setServerStatusPath(parameterManager.getByKey(ParamKey.STATUS_PATH_ENDING).getValue());
        setServerWspingPath(parameterManager.getByKey(ParamKey.WSPING_PATH_ENDING).getValue());
        setPingStatus("ping.status.unknown");
        setDbAddress(parameterManager.getByKey(ParamKey.DATABASE_ADDRESS).getValue());
        setDbPort(Integer.parseInt(parameterManager.getByKey(ParamKey.DATABASE_PORT).getValue()));
        setDbName(parameterManager.getByKey(ParamKey.DATABASE_NAME).getValue());
        setDbUser(parameterManager.getByKey(ParamKey.DATABASE_USER_NAME).getValue());
        setDbPassword(parameterManager.getByKey(ParamKey.DATABASE_USER_PASSWORD).getValue());
        setDatabaseConnectionStatus("config.database.connection.status.unknown");
    }

    private List<Server> getAllServers() {
        List<Server> servers = new ArrayList<>();
        try {
            servers = serverManager.getAll();
        } catch (DAOException ex) {
            logger.error("getAllServers: error={}", ex.getMessage(), ex);
        }
        return servers;
    }

    @PostConstruct
    public void construct() {
        init();
    }

    @PreDestroy
    public void destroy() {
    }

    public void testDatabaseConnection() {
        testDatabaseConnection(getDbAddress(), getDbPort(), getDbName(), getDbUser(), getDbPassword());
    }

    public void testDatabaseConnection(String dbAddress, Integer dbPort, String dbName, String dbUser, String dbPassword) {
        jDBCDataSource.setParameters(dbAddress, dbPort, dbName, dbUser, dbPassword);
        if (jDBCDataSource.testConnection() == true) {
            setDatabaseConnectionStatus("config.database.connection.status.success");
        } else {
            setDatabaseConnectionStatus("config.database.connection.status.failure");
        }
    }

    public void ping() {
        ping(getServerAddress(), getServerWspingPath());
    }

    public void ping(String serverAddress, String serverWspingPath) {
        Hermes2 h2 = new Hermes2();
        Properties mp = new Properties();
        mp.setProperty("wspingWSURL", serverAddress.concat(serverWspingPath));
        IWspingService iws = h2.getWspingService();
        iws.setServiceProperties(mp);
        if (iws.ping() == true) {
            setPingStatus("ping.status.success");
        } else {
            setPingStatus("ping.status.failure");
        }
    }

    public void testServer() {
        ping(selectedServer.getAddress(), selectedServer.getWspingPath());
        if ("ping.status.success".equals(pingStatus)) {
            Messages.addInfo("testServerStatus", bundle.getString("dialog.server.test.succeed"));
        } else {
            Messages.addError("testServerStatus", bundle.getString("dialog.server.test.failed"));
        }
        testDatabaseConnection(selectedServer.getDbAddress(),
                selectedServer.getDbPort(),
                selectedServer.getDbName(),
                selectedServer.getDbUser(),
                selectedServer.getDbPassword());
        if ("config.database.connection.status.success".equals(databaseConnectionStatus)) {
            Messages.addInfo("testDatabaseStatus", bundle.getString("dialog.server.test.succeed"));
        } else {
            Messages.addError("testDatabaseStatus", bundle.getString("dialog.server.test.failed"));
        }
    }

    /**
     * Weryfikuje czy wczytane zostały dane kryptograficzne niezbędne do wysyłania komunikatu - certyfikat do
     * szyfrowania
     *
     * @return true jeśli dane zostały wczytane, false w przeciwnym wypadku
     */
    public boolean isServerCryptoConfiguredToSend() {
        return selectedServer == null ? false
                : (selectedServer.getEncodeCertBase64() == null ? false
                        : (!selectedServer.getEncodeCertBase64().isEmpty()));
    }

    /**
     * Weryfikuje czy wczytane zostały dane kryptograficzne niezbędne do wysyłania komunikatu - certyfikat głównego
     * urzędu certyfikacji oraz klucz do deszyfrowania (pfx)
     *
     * @return true jeśli dane zostały wczytane, false w przeciwnym wypadku
     */
    public boolean isServerCryptoConfiguredToReceive() {
        return selectedServer == null ? false
                : (selectedServer.getRootCertBase64() == null ? false
                        : (selectedServer.getRootCertBase64().isEmpty() ? false
                                : (selectedServer.getDecodeKeyBase64() == null ? false
                                        : (!selectedServer.getDecodeKeyBase64().isEmpty()))));
    }

    public void deleteServer(ActionEvent actionEvent) {
        if (null == selectedServer) {
            throw new IllegalStateException("Obiekt serwera jest pusty !!! nie może zostać usunięty");
        }
        try {
            String serverLocalName = selectedServer.getName();
            partnershipController.deleteAllPartnerships();
            serverManager.remove(selectedServer);
            logger.info("server '{}' deleted", serverLocalName);
        } catch (DAOException ex) {
            logger.error("Błąd usuwania serwera : {}", ex);
        } finally {
            selectedServer = null;
            init();
        }
    }

    public String getDeleteServerMessage() {
        //TODO: oprzeć o parametryzowalne message
        String result = "Czy na pewno usunąć serwer?";
        if (null != selectedServer) {
            result = beanHelper.getMessage("dialog.server.delete.question", selectedServer.getName());
        }
        return result;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerSenderPath() {
        return serverSenderPath;
    }

    public void setServerSenderPath(String serverSenderPath) {
        this.serverSenderPath = serverSenderPath;
    }

    public String getServerStatusPath() {
        return serverStatusPath;
    }

    public void setServerStatusPath(String serverStatusPath) {
        this.serverStatusPath = serverStatusPath;
    }

    public String getServerReceiverPath() {
        return serverReceiverPath;
    }

    public void setServerReceiverPath(String serverReceiverPath) {
        this.serverReceiverPath = serverReceiverPath;
    }

    public String getServerReceiverListPath() {
        return serverReceiverListPath;
    }

    public void setServerReceiverListPath(String serverReceiverListPath) {
        this.serverReceiverListPath = serverReceiverListPath;
    }

    public String getServerWspingPath() {
        return serverWspingPath;
    }

    public void setServerWspingPath(String serverWspingPath) {
        setPingStatus("ping.status.unknown");
        this.serverWspingPath = serverWspingPath;
    }

    public String getPingStatus() {
        return pingStatus;
    }

    public void setPingStatus(String pingStatus) {
        this.pingStatus = pingStatus;
    }

    public String getDbAddress() {
        return dbAddress;
    }

    public void setDbAddress(String dbAddress) {
        this.dbAddress = dbAddress;
    }

    public Integer getDbPort() {
        return dbPort;
    }

    public void setDbPort(Integer dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public String getDatabaseConnectionStatus() {
        return databaseConnectionStatus;
    }

    public void setDatabaseConnectionStatus(String databaseConnectionStatus) {
        this.databaseConnectionStatus = databaseConnectionStatus;
    }

    public List<Server> getServerList() {
        return serverList;
    }

    public void setServerList(List<Server> serverList) {
        this.serverList = serverList;
    }

    public Server getSelectedServer() {
        return selectedServer;
    }

    public void setSelectedServer(Server selectedServer) {
        this.selectedServer = selectedServer;
    }

    public PartnershipController getPartnershipController() {
        return partnershipController;
    }

    public void setPartnershipController(PartnershipController partnershipController) {
        this.partnershipController = partnershipController;
    }

    public BeanHelper getBeanHelper() {
        return beanHelper;
    }

    public void setBeanHelper(BeanHelper beanHelper) {
        this.beanHelper = beanHelper;
    }
}
