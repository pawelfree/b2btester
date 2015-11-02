package pl.bsb.b2btester.jdbc;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.ds.DataSourceDAOFactory;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author paweld
 */
@Named
@SessionScoped
public class JDBCDataSource extends DataSourceDAOFactory implements DataSource, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(JDBCDataSource.class);
    private static final long serialVersionUID = 5L;
    private String connectionString;
    private Properties connectionProperties;

    public void setParameters(String dbAddress, Integer dbPort, String dbName, String dbUser, String dbUserPassword) {
        connectionString = "jdbc:postgresql://".concat(dbAddress).concat(":").concat(dbPort.toString()).concat("/").concat(dbName);
        connectionProperties = new Properties();
        connectionProperties.put("user", dbUser);
        connectionProperties.put("password", dbUserPassword);

    }

    @Override
    public void initFactory() throws DAOException {
        setDataSource(this);
    }

    public boolean testConnection() {
        try (Connection connection = getConnection();) {
            return true;
        } catch (Exception ex) {
            logger.error("testConnection: error={}", ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(
                connectionString,
                connectionProperties);
        return conn;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
