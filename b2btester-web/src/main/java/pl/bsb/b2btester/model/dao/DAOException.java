package pl.bsb.b2btester.model.dao;

/**
 *
 * @author zosowiski
 */
public class DAOException extends RuntimeException {

    private String details = null;
    private Object entity = null;

    public DAOException() {
        super();
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(String message, Exception ex) {
        super(message + ex.getMessage());
        this.details = ex.toString();
    }

    public DAOException(String message, Object entity, Exception ex) {
        super(message);
        if (ex != null) {
            this.details = ex.getMessage();
        }
        if (entity != null) {
            this.entity = entity;
        }
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        String result = "DAO Exception: " + getMessage();
        if (entity != null) {
            result += (System.getProperty("line.separator") + "entity: " + getEntity());
        }
        if (details != null) {
            result += (System.getProperty("line.separator") + "detials: " + getDetails());
        }
        return result;
    }
}
