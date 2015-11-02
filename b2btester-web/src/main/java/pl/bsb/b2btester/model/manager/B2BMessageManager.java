package pl.bsb.b2btester.model.manager;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.xml.stream.XMLStreamException;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.dao.DAOTemplate;
import pl.bsb.b2btester.model.dao.JpaCallback;
import pl.bsb.b2btester.model.entities.Message;
import pl.bsb.b2btester.model.entities.Server;
import pl.bsb.b2btester.util.EbxmlUtil;
import pl.bsb.b2btester.web.helper.Direction;

/**
 *
 * @author paweld
 */
@Named
public class B2BMessageManager extends DAOTemplate<Message> implements Serializable {

    private static final long serialVersionUID = 12L;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(B2BMessageManager.class);

    public B2BMessageManager() {
        super(Message.class);
    }

    public List<Message> getMessages(final Date date, final Server server) {
        return execute(new JpaCallback<List<Message>>() {
            @Override
            public List<Message> doInJpa(EntityManager em) {
                return em.createNamedQuery("getMessagesByDate", Message.class)
                        .setParameter("date", date)
                        .setParameter("server", server)
                        .getResultList();
            }
        });
    }

    public List<Message> getRelatedMessages(final Message message, final Date date) {
        return execute(new JpaCallback<List<Message>>() {
            @Override
            public List<Message> doInJpa(EntityManager em) {
                return em.createNamedQuery("findRelatedMessages", Message.class)
                        .setParameter("documentId", message.getDocumentId())
                        .setParameter("id", message.getId())
                        .setParameter("direction", message.getDirection() == Direction.In ? Direction.Out : Direction.In)
                        .setParameter("date", date)
                        .getResultList();
            }
        });
    }

    public List<Message> getMessages(final Date date, final Server server, final Direction direction) {
        return execute(new JpaCallback<List<Message>>() {
            @Override
            public List<Message> doInJpa(EntityManager em) {
                return em.createNamedQuery("getMessagesByDirectionAndDate", Message.class)
                        .setParameter("direction", direction)
                        .setParameter("date", date)
                        .setParameter("server", server)
                        .getResultList();
            }
        });
    }

    public boolean isDuplicate(byte[] content, final Server server) {
        boolean result = false;
        try {
            final String documentId = EbxmlUtil.getDocumentIdentifier(content);
            result = execute(new JpaCallback<Boolean>() {
                @Override
                public Boolean doInJpa(EntityManager em) {
                    Query q = em.createNamedQuery("isMessageDuplicated");
                    q.setParameter("messageDate", new Date(), TemporalType.DATE);
                    q.setParameter("documentId", documentId);
                    q.setParameter("server", server);
                    return !q.getResultList().isEmpty();
                }
            });
        } catch (IOException | XMLStreamException ex) {
            logger.error("Błąd krytyczny przy sprawdzaniu duplikatu komunikatu", ex);
        }
        return result;
    }
}