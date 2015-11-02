package pl.com.bsb.crypto.pkcs11;

/**
 * @author bkubacki
 *	Rodzaj szukanego obiektu
 */
public enum FindObjectType {
	OBJECT, 
	RSAPUBLICKEY, 
	RSAPRIVATEKEY, 
	CERTIFICATE, 
	SECRETKEY
}

