package pl.bsb.b2btester.ws.manager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2b.h2o.Hermes2;
import pl.bsb.b2b.h2o.helper.EmptyParametersException;
import pl.bsb.b2b.h2o.helper.MessageInfo;
import pl.bsb.b2b.h2o.services.IEbmsReceiverService;
import pl.bsb.b2b.h2o.services.IEbmsSenderService;
import pl.bsb.b2b.h2o.services.IEbmsStatusService;
import pl.bsb.b2btester.model.entities.Message;

/**
 *
 * @author paweld
 */
@Named
@RequestScoped
public class HermesManager {

    private static final Logger logger = LoggerFactory.getLogger(HermesManager.class);
    private Hermes2 hermes2;

    private Hermes2 getHermes() {
        if (hermes2 == null) {
            hermes2 = new Hermes2();
        }
        return hermes2;
    }

    //TODO we wszystkich metodach wyjątek jesli service bedzie nulowy
    public List<Message> getIncomingMessages(Properties properties) {
        List<Message> messages = new ArrayList<>();
        IEbmsReceiverService receiver = getHermes().getReceiverService();
        receiver.setServiceProperties(properties);
        logger.info("Starting to receive messages form Hermes");
        try {
            for (String messageId : receiver.getReceivedMessagesIds()) {
                logger.info("Receivig mesage with id : " + messageId);
                Message message = new Message();
                message.setRawMessage(streamToString(receiver.getMessage(messageId)));
                message.setHermesMessageId(messageId);
                messages.add(message);
                //TODO cpaid, service, action można us
            }
        } catch (EmptyParametersException | IOException ex) {
            logger.error("getIncomingMessages: error={}", ex.getMessage(), ex);
        }
        return messages;
    }

    private String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                out.append(line);
            }
        }
        return out.toString();
    }

    public String getMessageStatus(Properties properties, String messageId) {
        String messageStatus = "";
        IEbmsStatusService status = getHermes().getStatusService();
        status.setServiceProperties(properties);
        try {
            MessageInfo messageInfo = status.getStatus(messageId);
            if (messageInfo != null) {
                messageStatus = messageInfo.getStatus();
                if (messageStatus.compareTo("N/A") == 0) {
                    messageStatus = "NA";
                    logger.error("Message : ".concat(messageId).
                            concat(" status is not available. Reason : ").
                            concat(messageInfo.getStatusDescription()));
                }
            }
        } catch (EmptyParametersException ex) {
            logger.error("sendMessage: error={}", ex.getMessage(), ex);
        }
        return messageStatus;
    }

    public String sendMessage(Properties properties, String message) {
        logger.debug("sendMessage: message to send : ".concat(message));
        IEbmsSenderService sender = getHermes().getSenderService();
        sender.setServiceProperties(properties);
        try {
            return sender.send(new ByteArrayInputStream(message.getBytes()));
        } catch (EmptyParametersException ex) {
            logger.error("sendMessage: error={}", ex.getMessage(), ex);
        }
        return "";
    }
}
