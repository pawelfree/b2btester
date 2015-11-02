package pl.bsb.b2btester.model.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bartoszk, paweld
 */
public class DAOHelper {

    enum DB_TYPE {

        POSTGRESQL_9("postgresql_9", "b2btester_postgresql_9");

        private DB_TYPE(String strValue, String persistenceUnitName) {
            this.strValue = strValue;
            this.persistenceUnitName = persistenceUnitName;
        }
        final private String strValue;
        final private String persistenceUnitName;

        public String getPersistenceUnitName() {
            return persistenceUnitName;
        }

        public String getStrValue() {
            return strValue;
        }

        static DB_TYPE fromString(String strValue) {
            for (DB_TYPE dbType : DB_TYPE.values()) {
                if (dbType.getStrValue().equals(strValue)) {
                    return dbType;
                }
            }
            return POSTGRESQL_9; //domyślne
        }
    }
    
    static private EntityManagerFactory emf;
    private static final Logger logger = LoggerFactory.getLogger(DAOHelper.class);

    private static DB_TYPE getDatabaseType() {
        String adapterDatabase = System.getenv("B2BTESTER_DATABASE");
        logger.debug("B2BTESTER_DATABASE: " + adapterDatabase);
        return DB_TYPE.fromString(adapterDatabase);
    }

    public static void closeEntityManager(EntityManager em) {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    static public EntityManagerFactory getEntityManagerFactory() {
        try {
            if (emf == null) {
                DB_TYPE dbType = getDatabaseType();
                emf = Persistence.createEntityManagerFactory(dbType.getPersistenceUnitName());
                logger.debug("getEntityManagerFactory - create factory");                
            }
            return emf;
        } catch (Exception e) {
            logger.error("getEntityManagerFactory: error create EntityManagerFactory", e);
            return null;
        }
    }

    static public void initializeEntityManagerFactory() {
        getEntityManagerFactory();
    }

    static public void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

}
