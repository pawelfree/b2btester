/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao.ds;

import hk.hku.cecid.piazza.commons.dao.DAOException;
import hk.hku.cecid.piazza.commons.dao.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * DataSourceTransaction is an implementation of a DAO transaction. It should
 * not be created directly and is usually created through DataSourceDAOFactory.
 * 
 * @see DataSourceDAOFactory
 * 
 * @author Hugo Y. K. Lam
 *
 */
public class DataSourceTransaction implements Transaction {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceTransaction.class);
    
    private DataSourceDAOFactory dsFactory;
    
    private Connection conn;
    
    /**
     * Creates a new instance of DataSourceTransaction.
     * 
     * @param dsFactory the data source factory which creates this transaction.
     * @throws DAOException if the no data source factory specified.
     */
    protected DataSourceTransaction(DataSourceDAOFactory dsFactory) throws DAOException {
        if (dsFactory == null) {
            throw new DAOException("No data source factory provided");
        }
        else {
            this.dsFactory = dsFactory;
        }
    }
    
    /**
     * Gets the connection that this transaction manages.
     * 
     * @return the connection this transaction manages.
     * @throws DAOException if the transaction has not begun or has been ended.
     */
    public Connection getConnection() throws DAOException {
        if (conn != null) {
            return conn;
        }
        else {
            throw new DAOException("Transaction not begun or ended"); 
        }
    }
    
    /**
     * Releases the connection that this transaction manages.
     */
    public void releaseConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException ex) {
            logger.error("Error in releasing connection: error={}", ex.getMessage(), ex);
        }
    }
    
    /**
     * Begins the transaction.
     * 
     * @throws DAOException if unable to begin the transaction or the transaction has already begun. 
     * @see hk.hku.cecid.piazza.commons.dao.Transaction#begin()
     */
    @Override
    public void begin() throws DAOException {
        if (conn == null) {
            try {
                conn = dsFactory.getDataSource().getConnection();
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new DAOException("Unable to begin transaction", e);
            }
        }
        else {
            throw new DAOException("Transaction already begun");
        }
    }

    /**
     * Commits the transaction.
     * 
     * @throws DAOException if unable to commit the transaction.
     * @see hk.hku.cecid.piazza.commons.dao.Transaction#commit()
     */
    @Override
    public void commit() throws DAOException {
        try {
            getConnection().commit();
        } catch (DAOException | SQLException e) {
            throw new DAOException("Unable to commit transaction", e);
        } finally {
            releaseConnection();
        }
    }

    /**
     * Rolls back the transaction.
     * 
     * @throws DAOException if unable to commit the transaction.
     * @see hk.hku.cecid.piazza.commons.dao.Transaction#rollback()
     */
    @Override
    public void rollback() throws DAOException {
        try {
            getConnection().rollback();
        } catch (DAOException | SQLException e) {
            throw new DAOException("Unable to rollback transaction", e);
        } finally {
            releaseConnection();
        }        
    }
}
