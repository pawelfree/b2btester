package pl.com.bsb.crypto.utils;

import pl.com.bsb.crypto.utils.Verifier.VerifySignatureErrCode;

/**
 *
 * @author bartoszk
 */
public class VerifierException extends Exception{
    
    private Verifier.VerifySignatureErrCode errCode;
    
    public VerifierException (String message, Verifier.VerifySignatureErrCode errCode){
        super(message);
        this.errCode = errCode;        
    }

    public VerifySignatureErrCode getErrCode() {
        return errCode;
    }
    
}
