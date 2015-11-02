package pl.com.bsb.crypto.pkcs11;

import pl.com.bsb.crypto.CryptoException;

public class CertNoFoundException extends CryptoException{
	/**
	 * Brak certyfikatu
	 */
	private static final long serialVersionUID = -3263944654698810040L;

	public CertNoFoundException(String msg) {
		super(msg);
	}
	
}
