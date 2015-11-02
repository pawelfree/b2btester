package pl.com.bsb.crypto.pkcs11;

import pl.com.bsb.crypto.CryptoException;

public class ConfigException extends CryptoException {
	/**
	 * Błąd konfiguracji
	 */
	private static final long serialVersionUID = 1L;

	public ConfigException(String message){		
		super(message);		
	}

}
