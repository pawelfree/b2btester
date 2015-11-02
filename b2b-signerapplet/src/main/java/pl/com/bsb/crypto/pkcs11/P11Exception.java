package pl.com.bsb.crypto.pkcs11;

import pl.com.bsb.crypto.CryptoException;

/**
 * @author bkubacki
 *	Błędy PKCS#11
 */
public class P11Exception extends CryptoException {
	
	private static final long serialVersionUID = 2828409270090119260L;

	P11Exception(String message){		
		super(message);		
	}
}
