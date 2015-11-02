package pl.bsb.b2btester.web.helper;

/**
 *
 * @author paweld
 */
public enum MessageStatus {
    RC, //INTERNAL_STATUS_RECEIVED    
    PD, //INTERNAL_STATUS_PENDING
    PR, //INTERNAL_STATUS_PROCESSING
    PS, //INTERNAL_STATUS_PROCESSED
    PE, //INTERNAL_STATUS_PROCESSED_ERROR
    DL, //INTERNAL_STATUS_DELIVERED
    DF, //INTERNAL_STATUS_DELIVERY_FAILURE
    NA;
}
