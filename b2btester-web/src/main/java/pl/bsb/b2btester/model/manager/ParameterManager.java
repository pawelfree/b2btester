package pl.bsb.b2btester.model.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.dao.DAOException;
import pl.bsb.b2btester.model.dao.DAOTemplate;
import pl.bsb.b2btester.model.dao.JpaCallback;
import pl.bsb.b2btester.model.entities.Parameter;
import pl.bsb.b2btester.type.ParamGroup;
import pl.bsb.b2btester.type.ParamKey;
import pl.bsb.b2btester.web.helper.MessageBundle;

/**
 *
 * @author aszatkowski
 */
@Named
public class ParameterManager extends DAOTemplate<Parameter> {

    private static final long serialVersionUID = 10L;
    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;
    private static final Logger logger = LoggerFactory.getLogger(ParameterManager.class);

    public ParameterManager() {
        super(Parameter.class);
    }

    public Long getAllCount() {
        try {
            return execute(new JpaCallback<Long>() {
                @Override
                public Long doInJpa(EntityManager em) throws PersistenceException {
                    return (Long) em.createNamedQuery("Parameter.getCount").getSingleResult();
                }
            });
        } catch (DAOException ex) {
            logger.error("Get all count problem!!! {}", ex);
            throw new RuntimeException(ex);
        }
    }

    public List<Parameter> getByGroup(final ParamGroup group) {
        List<Parameter> result = new ArrayList<>();
        try {
            result = execute(new JpaCallback<List<Parameter>>() {
                @Override
                public List<Parameter> doInJpa(EntityManager em) throws PersistenceException {
                    return em.createNamedQuery("Parameter.findByGroup", Parameter.class)
                            .setParameter("group", group)
                            .getResultList();
                }
            });
        } catch (DAOException ex) {
            logger.error("getGroupBy failed.", ex);
        }
        return result;
    }

    public Parameter getByKey(final ParamKey key) {
        try {
            return execute(new JpaCallback<Parameter>() {
                @Override
                public Parameter doInJpa(EntityManager em) throws PersistenceException {
                    return (Parameter) em.createNamedQuery("Parameter.findByKey")
                            .setParameter("key", key).getSingleResult();
                }
            });
        } catch (DAOException ex) {
            logger.error("getByKey failed.", ex);
        } catch (NoResultException ex) {
            logger.debug("getByKey failed.", ex);
        }
        return null;
    }

    public void resetParameters() {
        List<Parameter> params = new ArrayList<>();
        params.add(new Parameter(ParamKey.SERVER_NAME, ParamGroup.DEFAULT_SERVER, "config.server.name", "Nazwa serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.server.name")));
        params.add(new Parameter(ParamKey.SERVER_ADDRESS, ParamGroup.DEFAULT_SERVER, "config.server.address", "Adres serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.server.address")));
        params.add(new Parameter(ParamKey.SENDER_PATH_ENDING, ParamGroup.HERMES_SERVICES, "config.sender.path.ending", "Adres usługi wysyłania komunikatu serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.sender.path.ending")));
        params.add(new Parameter(ParamKey.STATUS_PATH_ENDING, ParamGroup.HERMES_SERVICES, "config.status.path.ending", "Adres usługi statusu serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.status.path.ending")));
        params.add(new Parameter(ParamKey.RECEIVER_PATH_ENDING, ParamGroup.HERMES_SERVICES, "config.receiver.path.ending", "Adres usługi odbioru komunikatu serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.receiver.path.ending")));
        params.add(new Parameter(ParamKey.RECEIVER_LIST_PATH_ENDING, ParamGroup.HERMES_SERVICES, "config.receiver.list.path.ending", "Adres usługi listy komunikatów serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.receiver.list.path.ending")));
        params.add(new Parameter(ParamKey.WSPING_PATH_ENDING, ParamGroup.HERMES_SERVICES, "config.wsping.path.ending", "Adres usługi PING serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.wsping.path.ending")));
        params.add(new Parameter(ParamKey.DATABASE_ADDRESS, ParamGroup.DB_SERVER, "config.database.address", "Adres bazy danych serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.database.address")));
        params.add(new Parameter(ParamKey.DATABASE_PORT, ParamGroup.DB_SERVER, "config.database.port", "Port bazy danych serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.database.port")));
        params.add(new Parameter(ParamKey.DATABASE_NAME, ParamGroup.DB_SERVER, "config.database.name", "Nazwa bazy danych serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.database.name")));
        params.add(new Parameter(ParamKey.DATABASE_USER_NAME, ParamGroup.DB_SERVER, "config.database.user.name", "Nazwa użytkownika bazy danych serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.database.user.name")));
        params.add(new Parameter(ParamKey.DATABASE_USER_PASSWORD, ParamGroup.DB_SERVER, "config.database.user.password", "Hasło użytkownika bazy danych serwera ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("config.database.user.password")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_CPAID, ParamGroup.PARTNERSHIP, "partnership.default.cpaId", "Domyślny identyfikator powiązania", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.cpaId")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_SERVICE, ParamGroup.PARTNERSHIP, "partnership.default.service", "Domyślna usługa powiązania ", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.service")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_ACTION, ParamGroup.PARTNERSHIP, "partnership.default.action", "Domyślna akcja powiązania", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.action")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_TRANSPORT_ENDPOINT, ParamGroup.PARTNERSHIP, "partnership.default.transportEndpoint", "Adres odbiorcy komunikatów ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.transportEndpoint")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_IS_HOSTNAME_VERIFIED, ParamGroup.PARTNERSHIP, "partnership.default.isHostnameVerified", "Weryfikacja nazwy hosta w ssl", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.isHostnameVerified")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_SYNC_REPLY_MODE, ParamGroup.PARTNERSHIP, "partnership.default.syncReplyMode", "Tryb synchronicznej odpowiedzi", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.syncReplyMode")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_ACK_REQUESTED, ParamGroup.PARTNERSHIP, "partnership.default.ackRequested", "Potwierdzanie komunikatów ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.ackRequested")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_ACK_SIGN_REQUESTED, ParamGroup.PARTNERSHIP, "partnership.default.ackSignRequested", "Podpisywanie komunikatów potwierdzeń ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.ackSignRequested")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_DUP_ELIMINATION, ParamGroup.PARTNERSHIP, "partnership.default.dupElimination", "Eliminacja duplikatów komunikatów ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.dupElimination")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_MESSAGE_ORDER, ParamGroup.PARTNERSHIP, "partnership.default.messageOrder", "Zachowanie kolejności komunikatów", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.messageOrder")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_SIGN_REQUESTED, ParamGroup.PARTNERSHIP, "partnership.default.signRequested", "Podpisywanie komunikatu ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.messageOrder")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_ENCRYPT_REQUESTED, ParamGroup.PARTNERSHIP, "partnership.default.encryptRequested", "Szyfrowanie komunikatu ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.encryptRequested")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_RETRIES, ParamGroup.PARTNERSHIP, "partnership.default.retries", "Liczba ponownych powtórzeń", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.retries")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_RETRY_INTERVAL, ParamGroup.PARTNERSHIP, "partnership.default.retryInterval", "Czas ponowienia komunikatu (milisekundy) ", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.retryInterval")));
        params.add(new Parameter(ParamKey.PARTNERSHIP_DISABLED, ParamGroup.PARTNERSHIP, "partnership.default.disabled", "Określa czy powiązanie jest aktywne", (short) 1, (short) 1, (short) 0, bundle.getString("partnership.default.disabled")));
        params.add(new Parameter(ParamKey.MESSAGE_FROM_PARTY_ID, ParamGroup.MESSAGE, "message.default.fromPartyId", "Identyfikator nadawcy komunikatu ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.fromPartyId")));
        params.add(new Parameter(ParamKey.MESSAGE_FROM_PARTY_TYPE, ParamGroup.MESSAGE, "message.default.fromPartyType", "Typ nadawcy komunikatu ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.fromPartyType")));
        params.add(new Parameter(ParamKey.MESSAGE_TO_PARTY_ID, ParamGroup.MESSAGE, "message.default.toPartyId", "Identyfikator odbiorcy komunikatu ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.toPartyId")));
        params.add(new Parameter(ParamKey.MESSAGE_TO_PARTY_TYPE, ParamGroup.MESSAGE, "message.default.toPartyType", "Typ odbiorcy komunikatu ebXML", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.toPartyType")));
        params.add(new Parameter(ParamKey.MESSAGE_NUM_OF_MESSAGES, ParamGroup.MESSAGE, "message.default.numOfMessages", "Liczba komunikatów pobierana jednorazowo", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.numOfMessages")));
        params.add(new Parameter(ParamKey.MESSAGE_COMPRESSION, ParamGroup.MESSAGE, "message.default.compression", "Kompresja komunikatu RBE", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.compression")));
        params.add(new Parameter(ParamKey.MESSAGE_DUPLICATE_CONTROLL, ParamGroup.MESSAGE, "message.default.duplicate", "Kontrola duplikatów", (short) 1, (short) 1, (short) 0, bundle.getString("message.default.duplicate")));
        try {
            merge(params);
        } catch (DAOException ex) {
            logger.error("reset default parameters error!", ex);
        }
    }
}
