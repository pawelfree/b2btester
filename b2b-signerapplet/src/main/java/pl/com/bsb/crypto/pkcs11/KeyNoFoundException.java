package pl.com.bsb.crypto.pkcs11;

import pl.com.bsb.crypto.CryptoException;

public class KeyNoFoundException extends CryptoException {
	/**
	 * Brak klucza prywatnego
	 */
	private static final long serialVersionUID = 2267283131045748619L;

	KeyNoFoundException(String message){		
		super(message);		
	}
}
