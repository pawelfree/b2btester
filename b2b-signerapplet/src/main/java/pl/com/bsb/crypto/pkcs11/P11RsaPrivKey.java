package pl.com.bsb.crypto.pkcs11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import pl.com.bsb.crypto.CryptoException;
import pl.com.bsb.crypto.PrivKey;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 * @author bkubacki
 *	Klasa reprezetuje klucz RSA urządzenia PKCS#11
 */
/**
 * @author bartoszk
 *
 */
public class P11RsaPrivKey implements PrivKey{
	public static final int HASH_PROVIDER = 0; 
	public static final int HASH_ALG = 1;	
	public static final String HASH_ALG_SHA1 = "sha1";
	
	private long handle;
	private Token token = null;
	private String label = "";
	private byte[] id = null;
	//private BigInteger modulus = null;
	//private BigInteger exponent = null;
	
	private String hashProvider = "";
	private String hashAlg = HASH_ALG_SHA1;	
	
	/**
	 * Konstruktor
	 * @param attrs Lista atrybutów
	 * @param handle uchwyt 
	 * @param token urządzenie PKCS#11
	 */
	public P11RsaPrivKey(CK_ATTRIBUTE[] attrs, long handle, Token token) {
		if (attrs == null){
			return;
		}
		this.handle = handle;
		this.token = token;
		for (int i = 0; i < attrs.length; i++){
			long type = attrs[i].type;
			if (attrs[i].pValue != null){
				if (type == Const.CKA_ID){
					id = attrs[i].getByteArray();
				}
				else if (type == Const.CKA_LABEL){
					label = new String(attrs[i].getCharArray());				
				}
				/*
				else if (type == Const.CKA_MODULUS){
					modulus = attrs[i].getBigInteger();
				}
				else if (type == Const.CKA_PUBLIC_EXPONENT){
					exponent = attrs[i].getBigInteger(); 
				}
				else if (type == Const.CKA_SUBJECT){
					
				}
				*/
			}
		}		
	}
	
	private byte[] doHashSHA1(byte[] data) throws CryptoException{
		MessageDigest md;
		try{			
			if (hashProvider.equals("")){
				md = MessageDigest.getInstance("SHA1");			 
			}
			else{
				md = MessageDigest.getInstance("SHA1", hashProvider);			
			}
			return md.digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new ConfigException("Niepoprawny algorytm skrótu dla zdefiniowanego dostawcy kryptografii.");
		}
		catch (NoSuchProviderException e){
			throw new ConfigException("Niepoprawny dostawca kryptografii.");
		}
	}
	
	private byte[] createDataToSign(byte[] data) throws CryptoException{
		if (hashAlg != HASH_ALG_SHA1){
			new ConfigException("Nieobsługiwany algorytm skrótu.");
		}
		byte[] digest = doHashSHA1(data);		
		return prepareDigestToSign(digest);		
	}
	
	private byte[] prepareDigestToSign(byte[] digest) throws CryptoException{
		if (hashAlg != HASH_ALG_SHA1){
			new ConfigException("Nieobsługiwany algorytm skrótu.");
		}		
		if (digest.length != 20){
			throw new P11Exception("Niepoprawna długość danych do podpisania!");
		}
		byte ASN1_SHA1[] = {0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2B, 0x0E, 0x03, 0x02, 0x1A, 0x05, 0x00, 0x04, 0x14};
		//ByteArrayInputStream is = new ByteArrayInputStream();
		
		ByteArrayOutputStream result = new ByteArrayOutputStream(35);
		try {
			result.write(ASN1_SHA1);
			result.write(digest);
		} catch (IOException e) {
			throw new P11Exception("Błąd podczas przygotowywania danych do podpisu.");
		}		
		return result.toByteArray();		
	}
	
	/**
	 * Funkcja wykonuje podpis RSA
	 * @param data dane do podpisania
	 * @return podpisane dane 
	 */
	public byte[] sign(byte[] data) throws CryptoException{
		try{			
			if (data == null){
				return null;
			}
			byte[] toSign = createDataToSign(data);
			Library lib = token.lib;
			long session = token.session;
			//CK_MECHANISM mechanism = new CK_MECHANISM(Const.CKM_RSA_PKCS);
			CK_MECHANISM mechanism = new CK_MECHANISM(1L);
			lib.function.C_SignInit(session, mechanism, handle);			
			byte[] signature = lib.function.C_Sign(session, toSign);
			return signature;
		}
		catch (CryptoException e) {
			throw e;
		}catch (PKCS11Exception e) {
			throw new P11Exception("Nie można utworzyć podpisu PKCS#1: " + e.getErrorCode());
		}		
	}
	
	/**
	 * Funkcja wykonuje podpis RSA
	 * @param digest skrót z danych do podpisania
	 * @return podpisane dane
	 */
	public byte[] signDigest(byte[] digest) throws CryptoException {
		try{		
			byte[] toSign = prepareDigestToSign(digest);
			
			Library lib = token.lib;
			long session = token.session;
			
			CK_MECHANISM mechanism = new CK_MECHANISM(Const.CKM_RSA_PKCS);
			lib.function.C_SignInit(session, mechanism, handle);			
			
			byte[] signature = lib.function.C_Sign(session, toSign);
			return signature;
		}catch (CryptoException e) {
			throw e;
		}catch (Exception e) {
			throw new P11Exception("Nie można utworzyć podpisu PKCS#1");
		}		
	}
	
	public void setParam(int paramName, String paramValue) throws CryptoException{
		if (paramName == HASH_PROVIDER){
			hashProvider = paramValue;
		}
		else if (paramName == HASH_ALG){
			if (hashAlg != HASH_ALG_SHA1){
				new ConfigException("Nieobsługiwany algorytm skrótu.");
			}
			hashAlg = paramValue;			
		}
		else{
			new ConfigException("Nieobsługiwany parametr.");			
		}
	}

	public long getHandle() {
		return handle;
	}

	public byte[] getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getHashProvider() {
		return hashProvider;
	}

	public void setHashProvider(String hashProvider) {
		this.hashProvider = hashProvider;
	}

	public String getHashAlg() {
		return hashAlg;
	}

	public void setHashAlg(String hashAlg) {
		this.hashAlg = hashAlg;
	}

	
	
	
	

}
