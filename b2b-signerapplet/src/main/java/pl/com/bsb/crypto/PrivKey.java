package pl.com.bsb.crypto;

/**
 * @author bkubacki
 *	Klucz prywatny umożliwiający wykonanie podpisu
 */
public interface PrivKey {
	public byte[] sign(byte[]data) throws CryptoException;
	public byte[] signDigest(byte[]digest) throws CryptoException;
	public void setParam(int paramName, String paramValue) throws CryptoException;
}
