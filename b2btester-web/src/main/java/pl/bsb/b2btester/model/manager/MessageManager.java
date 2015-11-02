package pl.bsb.b2btester.model.manager;

import hk.hku.cecid.ebms.spa.dao.MessageDVO;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2b.h2o.dao.HermesDAOFactory;
import pl.bsb.b2b.h2o.dao.HermesMessageManager;
import pl.bsb.b2btester.jdbc.JDBCDataSource;
import pl.bsb.b2btester.model.entities.Message;
import pl.bsb.b2btester.web.helper.Direction;

/**
 *
 * @author paweld
 */
@Named
public class MessageManager implements Serializable {

    private static final long serialVersionUID = 11L;
    private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);
    @Inject
    private JDBCDataSource jDBCDataSource;
    private transient HermesDAOFactory daoFactory;

    private HermesMessageManager getManager() {
        try {
            if (daoFactory == null) {
                daoFactory = new HermesDAOFactory(jDBCDataSource);
            }
        } catch (Exception ex) {
            logger.error("getManager: error={}", ex.getMessage(), ex);
            throw new IllegalStateException("getManager : error=Cant initialize HermesDAOFactory", ex);
        }
        return daoFactory.getMessageManager();
    }

    @PostConstruct
    public void init() {
        daoFactory = null;
    }

    public MessageDVO createEmptyDVO() {
        return getManager().createDVO(null);
    }

    public MessageDVO getMessage(Message message) {
        MessageDVO messageDVO = getManager().getMessage(message.getHermesMessageId(),
                message.getDirection() == Direction.In ? "inbox" : "outbox");
        if (messageDVO == null) {
            messageDVO = createEmptyDVO();
        }
        return messageDVO;
    }

    public MessageDVO getMessageAcknowledgement(Message message) {
        MessageDVO messageDVO = getManager().getMessageAcknowledgement(message.getHermesMessageId(),
                message.getDirection() == Direction.In ? "outbox" : "inbox");
        if (messageDVO == null) {
            messageDVO = createEmptyDVO();
        }
        return messageDVO;
    }

    public MessageDVO getMessageError(Message message) {
        MessageDVO messageDVO = getManager().getMessageError(message.getHermesMessageId(),
                message.getDirection() == Direction.In ? "outbox" : "inbox");
        if (messageDVO == null) {
            messageDVO = createEmptyDVO();
        }
        return messageDVO;
    }

    public JDBCDataSource getjDBCDataSource() {
        return jDBCDataSource;
    }

    public void setjDBCDataSource(JDBCDataSource jDBCDataSource) {
        this.jDBCDataSource = jDBCDataSource;
    }
}
