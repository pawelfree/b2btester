package pl.com.bsb.crypto.pkcs11;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;
import sun.security.pkcs11.wrapper.PKCS11Exception;

/**
 * @author bkubacki Urządznie PKCS#11
 */
public class Token {

    private TokenInfo tokenInfo = null;
    private long slot = 0;
    long session = 0;
    private static CertificateFactory cf = null;

    static {
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            System.err.println("Nie można uzyskać dostępu do fabryki certyfikatów X.509: " + e.getMessage());
        }
    }
    Library lib = null;

    Token(Library lib, long slot) throws P11Exception {
        this.lib = lib;
        this.slot = slot;
        try {
            CK_TOKEN_INFO info = lib.function.C_GetTokenInfo(slot);
            tokenInfo = new TokenInfo(info);
            session = openSession(slot);
        } catch (P11Exception e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new P11Exception("Nie można uzyskać informacji o urządzeniach");
        }

    }

    public long getSlot() {
        return slot;
    }

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    private long openSession(long slot) throws P11Exception {
        try {
            return lib.function.C_OpenSession(slot, Const.CKF_SERIAL_SESSION, null, null);
        } catch (Exception e) {
            throw new P11Exception("Nie można otworzyć sesji.");
        }
    }

    public void closeSession() {
        try {
            lib.function.C_CloseSession(session);
        } catch (PKCS11Exception e) {
            System.err.println("Błąd podczas zamykania sesji: " + e.getMessage() + " [" + e.getErrorCode() + "]");
        }

    }

    private long[] findObject(FindObjectType type) throws P11Exception {
        try {
            long objType = -1;
            long keyType = -1;
            switch (type) {
                case OBJECT:
                    objType = Const.CKO_DATA;
                    break;
                case RSAPUBLICKEY:
                    objType = Const.CKO_PUBLIC_KEY;
                    keyType = Const.CKK_RSA;
                    break;
                case RSAPRIVATEKEY:
                    objType = Const.CKO_PRIVATE_KEY;
                    keyType = Const.CKK_RSA;
                    break;
                case CERTIFICATE:
                    objType = Const.CKO_CERTIFICATE;
                    break;
                case SECRETKEY:
                    objType = Const.CKO_SECRET_KEY;
            }

            CK_ATTRIBUTE attr0 = new CK_ATTRIBUTE(Const.CKA_CLASS, objType);
            int attrCount = 1;
            CK_ATTRIBUTE attr1 = null;
            if (keyType != -1) {
                attr1 = new CK_ATTRIBUTE(Const.CKA_KEY_TYPE, keyType);
                attrCount++;
            }
            CK_ATTRIBUTE attrs[] = new CK_ATTRIBUTE[attrCount];
            attrs[0] = attr0;
            if (attr1 != null) {
                attrs[1] = attr1;
            }

            lib.function.C_FindObjectsInit(session, attrs);
            long[] handles = lib.function.C_FindObjects(session, 1000);
            lib.function.C_FindObjectsFinal(session);
            return handles;
        } catch (Exception e) {

            throw new P11Exception("Wystąpił błąd podczas wyszukiwania obiektów.");
        }
    }

    public List<P11RsaPubKey> getRsaPublicKeys() throws P11Exception {
        try {
            CK_ATTRIBUTE[] template = {
                new CK_ATTRIBUTE(Const.CKA_ID),
                new CK_ATTRIBUTE(Const.CKA_LABEL),
                new CK_ATTRIBUTE(Const.CKA_MODULUS),
                new CK_ATTRIBUTE(Const.CKA_PUBLIC_EXPONENT)
            };
            long[] handles = findObject(FindObjectType.RSAPUBLICKEY);
            ArrayList<P11RsaPubKey> keys = new ArrayList<P11RsaPubKey>();
            for (int i = 0; i < handles.length; i++) {
                lib.function.C_GetAttributeValue(session, handles[i], template);
                keys.add(new P11RsaPubKey(template, handles[i], this));
            }
            return keys;
        } catch (P11Exception e) {
            throw e;
        } catch (Exception e) {
            throw new P11Exception("Nie można uzyskać informacji o kluczach publicznych zawartych w tokenie.");
        }

    }

    public Map<X509Certificate, String> getCertificates() throws P11Exception {
        List<P11X509Certificate> certs = getP11Certificates();
        Map<X509Certificate, String> result = new HashMap<X509Certificate, String>();
        for (P11X509Certificate p11Cert : certs) {
            try {
                X509Certificate cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(p11Cert.getValue()));
                result.put(cert, p11Cert.getLabel());
            } catch (CertificateException e) {
                System.err.println("Błąd dekodowania certyfikatu: " + p11Cert.getLabel());
            }
        }
        return result;
    }

    public List<P11X509Certificate> getP11Certificates() throws P11Exception {
        try {
            CK_ATTRIBUTE[] template = {
                new CK_ATTRIBUTE(Const.CKA_SUBJECT),
                new CK_ATTRIBUTE(Const.CKA_ISSUER),
                new CK_ATTRIBUTE(Const.CKA_SERIAL_NUMBER),
                new CK_ATTRIBUTE(Const.CKA_ID),
                new CK_ATTRIBUTE(Const.CKA_VALUE),
                new CK_ATTRIBUTE(Const.CKA_LABEL),};
            long[] handles = findObject(FindObjectType.CERTIFICATE);
            List<P11X509Certificate> certs = new ArrayList<P11X509Certificate>();
            for (int i = 0; i < handles.length; i++) {
                lib.function.C_GetAttributeValue(session, handles[i], template);
                P11X509Certificate cert = new P11X509Certificate(template, handles[i]);
                if (cert.toX509Certificate().getKeyUsage()[0] == true) {
                    certs.add(cert);
                }
            }
            return certs;
        } catch (P11Exception e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new P11Exception("Nie można uzyskać informacji o certyfikatach X.509 zawartych w tokenie.");
        }
    }

    public void login(String pin, boolean logAsSO) throws P11Exception {
        try {
            long userType = logAsSO ? Const.CKU_SO : Const.CKU_USER;
            lib.function.C_Login(session, userType, pin.toCharArray());
        } catch (Exception e) {
            throw new P11Exception("Wystąpił błąd podczas logowania.");
        }
    }

    public void logout() throws P11Exception {
        try {
            lib.function.C_Logout(session);
        } catch (Exception e) {
            throw new P11Exception("Wystąpił błąd podczas wylogowywania");
        }
    }

    public P11X509Certificate findCertificate(byte[] certData) throws P11Exception, CertNoFoundException {
        List<P11X509Certificate> certs = getP11Certificates();
        Iterator<P11X509Certificate> itCert = certs.iterator();
        while (itCert.hasNext()) {
            P11X509Certificate cert = itCert.next();
            if (Arrays.equals(cert.getValue(), certData)) {
                return cert;
            }
        }
        throw new CertNoFoundException("Nie można odszukać certyfikatu.");
    }

    public P11RsaPrivKey findRsaPrivateKey(P11X509Certificate cert) throws P11Exception, KeyNoFoundException {
        //szukamy najpierw po CKA_ID,
        //a jak nie pojdzie, to po: CKA_MODULUS + CKA_PUBLIC_EXPONENT!!!
        try {
            final CK_ATTRIBUTE[] findAttrs = {
                new CK_ATTRIBUTE(Const.CKA_CLASS, Const.CKO_PRIVATE_KEY),
                new CK_ATTRIBUTE(Const.CKA_KEY_TYPE, Const.CKK_RSA),
                new CK_ATTRIBUTE(Const.CKA_ID, cert.getId()), //szukamy po ID									
            };
            lib.function.C_FindObjectsInit(session, findAttrs);
            long[] handles = lib.function.C_FindObjects(session, 1);
            lib.function.C_FindObjectsFinal(session);
            if (handles.length == 0) {
                RSAPublicKey pub = (RSAPublicKey) cert.toX509Certificate().getPublicKey();
                final CK_ATTRIBUTE[] findAttrs2 = {
                    new CK_ATTRIBUTE(Const.CKA_CLASS, Const.CKO_PRIVATE_KEY),
                    new CK_ATTRIBUTE(Const.CKA_KEY_TYPE, Const.CKK_RSA),
                    new CK_ATTRIBUTE(Const.CKA_MODULUS, pub.getModulus()),
                    new CK_ATTRIBUTE(Const.CKA_PUBLIC_EXPONENT, pub.getPublicExponent())
                };
                lib.function.C_FindObjectsInit(session, findAttrs2);
                handles = lib.function.C_FindObjects(session, 1);
                lib.function.C_FindObjectsFinal(session);

                if (handles.length == 0) {
                    throw new KeyNoFoundException("Nie można uzyskać referencji do klucza prywatnego.");
                }
            }
            CK_ATTRIBUTE[] getAttrs = {
                new CK_ATTRIBUTE(Const.CKA_ID),
                new CK_ATTRIBUTE(Const.CKA_LABEL), //mozna tu pobrac tez modulus i exponent - ale po co??
            };
            lib.function.C_GetAttributeValue(session, handles[0], getAttrs);

            P11RsaPrivKey key = new P11RsaPrivKey(getAttrs, handles[0], this);
            return key;
        } catch (KeyNoFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new P11Exception("Nie można uzyskać referencji do klucza prywatnego.");
        }
    }
}
