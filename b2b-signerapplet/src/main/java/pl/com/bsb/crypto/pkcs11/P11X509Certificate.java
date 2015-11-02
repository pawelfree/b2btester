package pl.com.bsb.crypto.pkcs11;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import pl.com.bsb.crypto.CertException;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;

/**
 * @author bkubacki
 *	Ceryfikat X.509 znajdujący sie na urzadzeniu PKCS#11
 */
public class P11X509Certificate {
	private byte[] subject = null;
	private byte[] issuer = null;
	private byte[] serialNumber = null; 
	private byte[] id = null;
	private String label;
	private byte[] value = null;	
	private long handle;
	private static CertificateFactory cf = null;
	
	static{
		try{
			cf = CertificateFactory.getInstance("X.509");
		}
		catch (CertificateException e) {
			System.err.println("Nie można uzyskać dostępu do fabryki certyfikatów X.509: " + e.getMessage());
		}
	}
		
	public P11X509Certificate(CK_ATTRIBUTE[] attrs, long handle) {
		this.handle = handle;
		for (CK_ATTRIBUTE attr : attrs){
			long type = attr.type;
			if (attr.pValue != null){
				if (type == Const.CKA_SUBJECT){
					subject = attr.getByteArray();
				}
				else if (type == Const.CKA_ISSUER){
					issuer = attr.getByteArray();
				}
				else if (type == Const.CKA_SERIAL_NUMBER){
					serialNumber = attr.getByteArray();
				}
				else if (type == Const.CKA_ID){
					id = attr.getByteArray();
				}
				else if (type == Const.CKA_VALUE){
					value = attr.getByteArray();					
				}
				else if (type == Const.CKA_LABEL){
					label = new String(attr.getCharArray());
				}
			}
		}		
	}

	public byte[] getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public byte[] getSubject() {
		return subject;
	}

	public byte[] getValue() {
		return value;
	}

	public byte[] getIssuer() {
		return issuer;
	}

	public byte[] getSerialNumber() {
		return serialNumber;
	}
	public long getHandle(){
		return handle;
	}
	@Override
	public String toString() {
		return super.toString();
	}
	
	public X509Certificate toX509Certificate() throws CertException{
		try{
			X509Certificate result = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(value));
			return result;
		}
		catch (CertificateException e) {
			throw new CertException("Błąd podczas dekodowania certyfikatu: " + e.getMessage());
		}
	}
	
	public boolean equals(Object other) {
		if (this == other){
			return true;
		}
		if (other == null){
			return false;
		}
		return Arrays.equals(this.value, ((P11X509Certificate)other).value);
	}

}
