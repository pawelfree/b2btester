package pl.bsb.b2btester.type;

/**
 * @author aszatkowski
 */
public enum ParamKey {

    SERVER_NAME(Type.STRING, null, null),
    SERVER_ADDRESS(Type.STRING, null, null),
    //hermes services
    SENDER_PATH_ENDING(Type.STRING, null, null),
    STATUS_PATH_ENDING(Type.STRING, null, null),
    RECEIVER_PATH_ENDING(Type.STRING, null, null),
    RECEIVER_LIST_PATH_ENDING(Type.STRING, null, null),
    WSPING_PATH_ENDING(Type.STRING, null, null),
    EBMS_PARTNERSHIP(Type.STRING, null, null),
    //db server config
    DATABASE_ADDRESS(Type.STRING, null, null),
    DATABASE_PORT(Type.STRING, null, null),
    DATABASE_NAME(Type.STRING, null, null),
    DATABASE_USER_NAME(Type.STRING, null, null),
    DATABASE_USER_PASSWORD(Type.STRING, null, null),
    //partnership
    PARTNERSHIP_CPAID(Type.STRING, null, null),
    PARTNERSHIP_SERVICE(Type.STRING, null, null),
    PARTNERSHIP_ACTION(Type.STRING, null, null),
    PARTNERSHIP_TRANSPORT_ENDPOINT(Type.STRING, null, null),
    PARTNERSHIP_IS_HOSTNAME_VERIFIED(Type.STRING, null, null),
    PARTNERSHIP_SYNC_REPLY_MODE(Type.STRING, null, null),
    PARTNERSHIP_ACK_REQUESTED(Type.STRING, null, null),
    PARTNERSHIP_ACK_SIGN_REQUESTED(Type.STRING, null, null),
    PARTNERSHIP_DUP_ELIMINATION(Type.STRING, null, null),
    PARTNERSHIP_MESSAGE_ORDER(Type.STRING, null, null),
    PARTNERSHIP_SIGN_REQUESTED(Type.BOOLEAN, null, null),
    PARTNERSHIP_ENCRYPT_REQUESTED(Type.BOOLEAN, null, null),
    PARTNERSHIP_RETRIES(Type.INTEGER, null, null),
    PARTNERSHIP_RETRY_INTERVAL(Type.INTEGER, null, null),
    PARTNERSHIP_DISABLED(Type.BOOLEAN, null, null),
    //message
    MESSAGE_FROM_PARTY_ID(Type.STRING, null, null),
    MESSAGE_FROM_PARTY_TYPE(Type.STRING, null, null),
    MESSAGE_TO_PARTY_ID(Type.STRING, null, null),
    MESSAGE_TO_PARTY_TYPE(Type.STRING, null, null),
    MESSAGE_NUM_OF_MESSAGES(Type.INTEGER, null, null),
    MESSAGE_COMPRESSION(Type.BOOLEAN, null, null),
    MESSAGE_DUPLICATE_CONTROLL(Type.BOOLEAN, null, null);
    private final Type type;
    private final Integer minValue;
    private final Integer maxValue;

    private ParamKey(Type type) {
        this.type = type;
        this.minValue = null;
        this.maxValue = null;
    }

    private ParamKey(Type type, Integer minValue, Integer maxValue) {
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public Type getType() {
        return type;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public Object getMinValue() {
        return minValue;
    }
}
