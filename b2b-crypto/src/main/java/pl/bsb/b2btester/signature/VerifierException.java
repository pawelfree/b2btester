package pl.bsb.b2btester.signature;

import pl.bsb.b2btester.signature.Verifier.VerifySignatureErrCode;

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
