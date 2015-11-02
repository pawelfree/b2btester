package pl.com.bsb.crypto.messagedigest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import pl.com.bsb.crypto.CryptoException;
import pl.com.bsb.crypto.pkcs11.ConfigException;
import pl.com.bsb.crypto.utils.Base64;

public class SHA1Digest {
	public static byte[] generate(byte[] data) throws CryptoException{
		try{
			MessageDigest md = MessageDigest.getInstance("SHA1");	 
			return md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new ConfigException("Niepoprawny algorytm skrótu dla zdefiniowanego dostawcy kryptografii.");
		}
	}
	
	public static String generateB64(byte[] data) throws CryptoException{
		byte[] hash = generate(data);
		return Base64.encode(hash);
	}

}
