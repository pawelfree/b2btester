package pl.com.bsb.crypto.pkcs11;

import java.math.BigInteger;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;

/**
 * @author bkubacki
 *	Klucz publiczny znajdujący się na urządzeniu PKCS#11
 */
public class P11RsaPubKey {
	private String label = "";
	private byte[] id = null;
	private BigInteger modulus = null;
	private BigInteger exponent = null;
	private long handle;
	//private Token token;
	
	P11RsaPubKey(CK_ATTRIBUTE[] attrs, long handle, Token token){
		this.handle = handle;
		//this.token = token;
		for (CK_ATTRIBUTE attr : attrs){
			long type = attr.type;
			if (type == Const.CKA_ID){
				id = attr.getByteArray();
			}
			else if (type == Const.CKA_LABEL){
				if (attr.pValue == null){
					label = "";
				}
				else{
					label = new String(attr.getCharArray());
				}								
			}
			else if (type == Const.CKA_MODULUS){
				modulus = attr.getBigInteger();				
			}
			else if (type == Const.CKA_PUBLIC_EXPONENT){
				exponent = attr.getBigInteger();				
			}			
		}		
	}
	
	public boolean verifyPkcs1(byte[] signature) throws P11Exception{
		try{
			//token.lib.function.c_v
			//TODO dokończyć!!!
			
		}catch (Exception e) {
			throw new P11Exception("Wystąpił błąd podczas weryfikacji podpisu.");
		}
		return false;
	}

	public BigInteger getExponent() {
		return exponent;
	}

	public byte[] getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public BigInteger getModulus() {
		return modulus;
	}
	
	public long getHandle(){
		return handle;
	}
		
	private String idToStr(byte[] id){
		String res = "";
		if (id != null){
			for (int i = 0; i < id.length; i++){
				//res = res + Integer.toHexString((int)id[i]);
				
				res = res + Integer.toString(((int)id[i] & 0xFF) + 0x100, 16).substring(1);
			}			
		}
		return res;		
	}
	
	@Override
	public String toString() {
		String info = "id: " + idToStr(id) + ", " +
					  "label: " + label + ", " + 
					  "modulus: " + modulus.toString() + ", " +
					  "exponent: " + exponent.toString();
		return info;
	}
	
}
