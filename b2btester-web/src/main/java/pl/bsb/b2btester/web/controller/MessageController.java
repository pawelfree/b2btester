package pl.bsb.b2btester.web.controller;

import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIInput;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.encryption.CrypterException;
import pl.bsb.b2btester.model.dao.DAOException;
import pl.bsb.b2btester.model.entities.Message;
import pl.bsb.b2btester.model.entities.Server;
import pl.bsb.b2btester.model.manager.B2BMessageManager;
import pl.bsb.b2btester.model.manager.CryptoManager;
import pl.bsb.b2btester.model.manager.ParameterManager;
import pl.bsb.b2btester.model.manager.PartnershipManager;
import pl.bsb.b2btester.model.manager.UniqueId;
import pl.bsb.b2btester.signature.VerifierException;
import pl.bsb.b2btester.type.ParamKey;
import pl.bsb.b2btester.util.Base64;
import pl.bsb.b2btester.util.EbxmlUtil;
import pl.bsb.b2btester.web.helper.ActionType;
import pl.bsb.b2btester.web.helper.Direction;
import pl.bsb.b2btester.web.helper.MessageBundle;
import pl.bsb.b2btester.web.helper.MessageStatus;
import pl.bsb.b2btester.ws.manager.HermesManager;

/**
 *
 * @author paweld
 */
@Named
@SessionScoped
public class MessageController implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private static final long serialVersionUID = 18L;
    private Message selectedMessage;
    private List<Message> messageList;
    private Date messageDate;
    private int direction;
    private String messageFileName;
    private String messageType;
    private String messageRbeVersion;
    private byte[] messageData;
    private String messageRbeSender;
    private String messageRbeReceiver;
    private Message messageToSend;
    private boolean signed;
    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;
    @Inject
    private ServerController serverController;
    @Inject
    private PartnershipManager partnershipManager;
    @Inject
    private HermesManager hermesManager;
    @Inject
    private UniqueId uniqueId;
    @Inject
    private B2BMessageManager b2BMessageManager;
    @Inject
    private ParameterManager parameterManager;
    @Inject
    private CryptoManager cryptoManager;
    private Properties connectionProperties;

    @PostConstruct
    public void init() {
        connectionProperties = new Properties();
        messageList = new ArrayList<>();
        messageDate = new Date();
        direction = 1;
        signed = false;
        clearMessageData();
        getMessages();
    }

    public void clearMessageData() {
        messageToSend = null;
        signed = false;
        messageFileName = bundle.getString("n/a");
        messageRbeVersion = messageFileName;
        messageType = messageFileName;
        messageRbeSender = messageFileName;
        messageRbeReceiver = messageFileName;
    }

    /**
     * Pobiera komunikaty z bay symulatora dla danego dnia.
     */
    private void getMessages() {
        Server server = serverController.getSelectedServer();
        if (server != null) {
            try {
                if (direction == 3) {
                    messageList = b2BMessageManager.getMessages(messageDate, server, Direction.Out);
                } else {
                    if (direction == 2) {
                        messageList = b2BMessageManager.getMessages(messageDate, server, Direction.In);
                    } else {
                        messageList = b2BMessageManager.getMessages(messageDate, server);
                    }
                }
            } catch (DAOException ex) {
                logger.error("sendMessage: error={}", ex.getMessage(), ex);
                messageList = new ArrayList<>();
            }
        } else {
            messageList = new ArrayList<>();
        }
    }

    public void messageStatus() {
        if (selectedMessage != null) {
            connectionProperties = new Properties();
            Server server = selectedMessage.getServer();
            connectionProperties.put("statusWSURL", server.getAddress().concat(server.getStatusPath()));
            selectedMessage.setLastStatus(MessageStatus.valueOf(hermesManager.getMessageStatus(connectionProperties, selectedMessage.getHermesMessageId())));
            selectedMessage.setLastStatusDate(new Date());
            try {
                selectedMessage = b2BMessageManager.merge(selectedMessage);
            } catch (DAOException ex) {
                logger.error("messageStatus: error={}", ex.getMessage(), ex);
            }
        }
    }

    public void processIncomingMessages() {
        Server server = serverController.getSelectedServer();
        if (server != null) {
            try {
                cryptoManager.setDecodeConfig(server.getDecodeKeyBase64(), server.getDecodeKeyPassword(),
                        server.getRootCertBase64(), server.getCrlBase64(), false);
            } catch (CrypterException ex) {
                logger.error("processIncomingMessages: error={}", ex.getMessage(), ex);
                //TODO na widok
                return;
            }
            for (PartnershipDVO partnership : partnershipManager.getAllPartnerships(server)) {
                connectionProperties = getDefaultProperties();
                connectionProperties.setProperty("cpaId", partnership.getCpaId());
                connectionProperties.setProperty("action", partnership.getAction());
                connectionProperties.setProperty("service", partnership.getService());
                connectionProperties.setProperty("receiverWSURL",
                        server.getAddress().concat(server.getReceiverPath()));
                connectionProperties.setProperty("receiverListWSURL",
                        server.getAddress().concat(server.getReceiverListPath()));
                for (Message message : hermesManager.getIncomingMessages(connectionProperties)) {
                    logger.debug(MessageController.class.getName().concat("processIncomingMessages: processing message with id : ").concat(message.getHermesMessageId()));
                    message.setServer(server);
                    message.setAction(ActionType.valueOf(partnership.getAction()));
                    message.setService(partnership.getService());
                    message.setMessageDate(parseDateFormMessageId(message.getHermesMessageId()));
                    message.setMessageTime(message.getMessageDate());
                    message.setDirection(Direction.In);

                    try {
                        if (parameterManager.getByKey(ParamKey.MESSAGE_COMPRESSION).getBooleanValue()) {
                            try {
                                message.setRbeMessage(cryptoManager.decompressb64(Base64.decode(cryptoManager.decodeAndVerifyB64(message.getRawMessage()))));
                            } catch (IOException ex) {
                                Messages.addError("processIncomingMessages", bundle.getString("dialog.message.validation.error.compression"));
                                Faces.getContext().validationFailed();
                                logger.error("processIncomingMessages-compression: error={}", ex.getMessage());
                                continue;
                            }
                        } else {
                            message.setRbeMessage(cryptoManager.decodeAndVerifyB64(message.getRawMessage()));
                        }
                        logger.debug("processIncomingMessages - rbe message to save : " + new String(Base64.decode(message.getRbeMessage())));
                        logger.debug("processIncomingMessages - rbe documentId : " + EbxmlUtil.getDocumentIdentifier(Base64.decode(message.getRbeMessage())));
                        message.setDocumentId(EbxmlUtil.getDocumentIdentifier(Base64.decode(message.getRbeMessage())));
                        b2BMessageManager.persist(message);
                    } catch (DAOException | CrypterException | VerifierException | IOException | XMLStreamException ex) {
                        logger.error("processIncomingMessages: error={}", ex.getMessage(), ex);
                    }
                }
            }
        } else {
            logger.info("processIncomingMessages : Cant process messages - server information not set");
        }
    }

    private Date parseDateFormMessageId(String messageId) {
        Calendar c = new GregorianCalendar();
        String yearString, monthString, dayString;
        int year, month, day;
        try {
            yearString = messageId.substring(0, 4);
            monthString = messageId.substring(4, 6);
            dayString = messageId.substring(6, 8);
            year = new Integer(yearString);
            month = new Integer(monthString);
            day = new Integer(dayString);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            logger.error("Incorrect Hermes message ID, can't create input date: ", e);
            return new Date();
        }
        c.set(year, month - 1, day);
        return c.getTime();
    }

    public void sendMessage() {
        Server server = serverController.getSelectedServer();
        if (server != null && messageToSend != null) {

            messageToSend.setServer(server);
            cryptoManager.setEncodeConfig(server.getEncodeCertBase64());
            connectionProperties = getDefaultProperties();
            connectionProperties.setProperty("senderWSURL", server.getAddress().concat(server.getSenderPath()));
            connectionProperties.setProperty("service", messageToSend.getService());
            connectionProperties.setProperty("action", messageToSend.getAction().name());
            connectionProperties.setProperty("convId", uniqueId.nextId());
            //to nie jest tutaj obsługiwane convId, refToMessageId
            messageToSend.setMessageDate(new Date());
            messageToSend.setMessageTime(messageToSend.getMessageDate());

            String messageId;
            try {
                messageId = hermesManager.sendMessage(connectionProperties, cryptoManager.encodeB64(messageToSend.getRawMessage()));
                if (messageId != null && !messageId.isEmpty()) {
                    messageToSend.setHermesMessageId(messageId);
                    messageToSend.setDirection(Direction.Out);
                    try {
                        //TODO czy na pewno messageTosend jest aktualizowane
                        b2BMessageManager.persist(messageToSend);
                        clearMessageData();
                    } catch (DAOException ex) {
                        logger.error("sendMessage - error persisting message"
                                + "\nmessageId : " + messageId
                                + "\ndocumentId : " + messageToSend.getDocumentId()
                                + "\nserverId : " + messageToSend.getServer().getId()
                                + "\nmessageData : " + cryptoManager.encodeB64(messageToSend.getRawMessage()));
                        logger.error("sendMessage : error={}", ex.getMessage(), ex);
                    }
                } else {
                    Messages.addError("sendMessages", bundle.getString("dialog.message.send.request.failure"));
                    Faces.getContext().validationFailed();
                    logger.error("sendMessage : error = Empty response from service");
                    clearMessageData();
                }
            } catch (CrypterException ex) {
                Messages.addError("sendMessages", bundle.getString("dialog.message.send.error.unknown"));
                Faces.getContext().validationFailed();
                logger.error("sendMessage: error={}", ex.getMessage(), ex);
                clearMessageData();
            }
        } else {
            Messages.addError("sendMessages", bundle.getString("dialog.message.validation.error.no.file"));
            Faces.getContext().validationFailed();
            logger.info("sendMessage : Cant send message - server information not set");
            clearMessageData();
        }
    }

    private Properties getDefaultProperties() {
        Properties properties = new Properties();
        //wartości domyślne
        properties.put("cpaId", parameterManager.getByKey(ParamKey.PARTNERSHIP_CPAID).getValue());
        properties.put("fromPartyId", parameterManager.getByKey(ParamKey.MESSAGE_FROM_PARTY_ID).getValue());
        properties.put("fromPartyType", parameterManager.getByKey(ParamKey.MESSAGE_FROM_PARTY_TYPE).getValue());
        properties.put("toPartyId", parameterManager.getByKey(ParamKey.MESSAGE_TO_PARTY_ID).getValue());
        properties.put("toPartyType", parameterManager.getByKey(ParamKey.MESSAGE_TO_PARTY_TYPE).getValue());
        properties.put("numOfMessages", parameterManager.getByKey(ParamKey.MESSAGE_NUM_OF_MESSAGES).getValue());
        properties.put("senderWSURL", parameterManager.getByKey(ParamKey.SERVER_ADDRESS).getValue().
                concat(parameterManager.getByKey(ParamKey.SENDER_PATH_ENDING).getValue()));
        properties.put("senderWSURL", parameterManager.getByKey(ParamKey.SERVER_ADDRESS).getValue().
                concat(parameterManager.getByKey(ParamKey.RECEIVER_PATH_ENDING).getValue()));
        properties.put("senderWSURL", parameterManager.getByKey(ParamKey.SERVER_ADDRESS).getValue().
                concat(parameterManager.getByKey(ParamKey.RECEIVER_LIST_PATH_ENDING).getValue()));
        return properties;
    }

    /**
     *
     * @return wiadomość do podpisania w postaci B64
     */
    public String getDataToSign() {
        String messageb64 = " ";
        if (messageToSend != null) {
            messageb64 = messageToSend.getRawMessage();
        }
        return messageb64;
    }

    public void setSignedData(String signatureb64) {
        signed = false;
        if (messageToSend != null) {
            if (cryptoManager.isSignature(signatureb64)) {
                messageToSend.setRawMessage(signatureb64);
                signed = true;
            } else {
                logger.debug("setSignedData : wrong signature format");
                signed = false;
            }
        }
        logger.debug("setSignedData - signed data : ".concat(signatureb64));
    }

    public void handleFileUpload(FileUploadEvent event) {
        messageData = event.getFile().getContents();
        messageFileName = event.getFile().getFileName();
        messageToSend = new Message();
        messageToSend.setRbeMessage(Base64.encode(messageData));
        if (parameterManager.getByKey(ParamKey.MESSAGE_COMPRESSION).getBooleanValue()) {
            try {
                messageToSend.setRawMessage(cryptoManager.compressb64(messageData));
            } catch (IOException ex) {
                Messages.addError("sendMessages", bundle.getString("dialog.message.validation.error.compression"));
                Faces.getContext().validationFailed();
                logger.error("handleFileUpload-compression: error={}", ex.getMessage());
                messageToSend = null;
            }
        } else {
            messageToSend.setRawMessage(Base64.encode(messageData));
        }
        //TODO parse message
        messageToSend.setService(parameterManager.getByKey(ParamKey.PARTNERSHIP_SERVICE).getValue());
        try {
            messageType = EbxmlUtil.getMessgeType(messageData);
            messageRbeSender = EbxmlUtil.getMessageSender(messageData);
            messageRbeReceiver = EbxmlUtil.getMessageReceiver(messageData);
            ActionType at = ActionType.valueOf(messageType);
            messageToSend.setAction(at);
            messageRbeVersion = EbxmlUtil.getMessageVersion(messageData);
            String path = "/RBE/".concat(messageRbeVersion).concat("/");
            if (false == EbxmlUtil.validateAgainstXSD(path, new StreamSource(new ByteArrayInputStream(messageData)),
                    new StreamSource(this.getClass().getResourceAsStream(path + at.name() + ".xsd")))) {
                Messages.addError("sendMessages", bundle.getString("dialog.message.validation.error.xsd"));
                Faces.getContext().validationFailed();
                messageToSend = null;
                signed = false;
            }
            try {
                if (parameterManager.getByKey(ParamKey.MESSAGE_DUPLICATE_CONTROLL).getValue().equals("true") && b2BMessageManager.isDuplicate(messageData, serverController.getSelectedServer())) {
                    Messages.addError("sendMessages", bundle.getString("dialog.message.validation.error.duplicate"));
                    Faces.getContext().validationFailed();
                    messageToSend = null;
                    signed = false;
                } else {
                    messageToSend.setDocumentId(EbxmlUtil.getDocumentIdentifier(messageData));
                }
            } catch (DAOException ex) {
                logger.error("handleFileUpload: error={}", ex.getMessage());
            }
        } catch (XMLStreamException | IOException ex) {
            Messages.addError("sendMessages", bundle.getString("dialog.message.validation.error.unknown"));
            Faces.getContext().validationFailed();
            logger.error("handleFileUpload: error={}", ex.getMessage());
            messageToSend = null;
            signed = false;
        } catch (IllegalArgumentException ex) {
            Messages.addError("sendMessages", bundle.getString("dialog.message.validation.error.unknown.type"));
            Faces.getContext().validationFailed();
            logger.error("handleFileUpload: error={}", ex.getMessage());
            messageToSend = null;
            signed = false;
        }
    }

    public List<Message> getRelatedMessages() {
        if (selectedMessage != null) {
            return b2BMessageManager.getRelatedMessages(selectedMessage, messageDate);
        } else {
            return new ArrayList<>();
        }
    }

    public void directionChanged(AjaxBehaviorEvent ev) {
        direction = (int) ((UIInput) ev.getComponent()).getValue();
    }

    public void dateChanged(AjaxBehaviorEvent ev) {
        messageDate = (Date) ((UIInput) ev.getComponent()).getValue();
    }

    public Message getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(Message selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    public List<Message> getMessageList() {
        getMessages();
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public Date getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getMessageFileName() {
        return messageFileName;
    }

    public void setMessageFileName(String messageFileName) {
        this.messageFileName = messageFileName;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public ServerController getServerController() {
        return serverController;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public HermesManager getHermesManager() {
        return hermesManager;
    }

    public void setHermesManager(HermesManager hermesManager) {
        this.hermesManager = hermesManager;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageRbeVersion() {
        return messageRbeVersion;
    }

    public Message getMessageToSend() {
        return messageToSend;
    }

    public void setParameterManager(ParameterManager parameterManager) {
        this.parameterManager = parameterManager;
    }

    public CryptoManager getCryptoManager() {
        return cryptoManager;
    }

    public void setCryptoManager(CryptoManager cryptoManager) {
        this.cryptoManager = cryptoManager;
    }

    public String getMessageRbeSender() {
        return messageRbeSender;
    }

    public String getMessageRbeReceiver() {
        return messageRbeReceiver;
    }

    public boolean isSigned() {
        return signed;
    }
}
