package pl.bsb.b2btester.applet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JApplet;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import pl.com.bsb.crypto.CryptoException;
import pl.com.bsb.crypto.PrivKey;
import pl.com.bsb.crypto.messagedigest.SHA1Digest;
import pl.com.bsb.crypto.pkcs11.Library;
import pl.com.bsb.crypto.pkcs11.LibraryInfo;
import pl.com.bsb.crypto.pkcs11.P11Exception;
import pl.com.bsb.crypto.pkcs11.P11RsaPrivKey;
import pl.com.bsb.crypto.pkcs11.P11X509Certificate;
import pl.com.bsb.crypto.pkcs11.Token;
import pl.com.bsb.crypto.pkcs11.pkcs7.P7Engine;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_TOKEN_INFO;

public class SignerApplet extends JApplet {

    private static final long serialVersionUID = 4055317578720417949L;

    private static final String[] PKCS11_LIB_NAMES = {"enigmap11", "CCPkiP11"};

    private final List<String> libraryNames = new ArrayList<String>();

    private final List<Library> librarys = new ArrayList<Library>();

    private boolean initError = true;

    @Override
    public void init() {
        System.err.println("Inicjalizacja apletu");

        librarys.clear();
        libraryNames.clear();
        libraryNames.addAll(Arrays.asList(PKCS11_LIB_NAMES));

        try {
            Security.insertProviderAt(new BouncyCastleProvider(), 0);
            initError = false;
        } catch (Exception e) {
            //e.printStackTrace();
            initError = true;
            System.err.println("Błąd podczas inicjalizacji biblioteki pkcs11: " + e.getMessage());
        }

        Set<String> infoSet = new HashSet<String>();
        for (String libName : libraryNames) {
            try {
                Library library = new Library(libName);
                LibraryInfo libraryInfo = library.getLibInfo();
                if (infoSet.contains(libraryInfo.toString())) {
                    System.err.println(libName + " jest już załadowana - pomijamy...");
                    continue;
                }
                infoSet.add(libraryInfo.toString());
                System.err.println("LIBRARY INFO: " + libraryInfo.toString());
                librarys.add(library);
            } catch (P11Exception e) {
                System.err.println("Nie można zainicjować biblioteki pkcs11: " + e.getMessage() + " :: pomijamy");
            }
        }

        initError = librarys.isEmpty();
        initialize();
        super.init();
    }

    //ta przedziwna funkcja musi być wywołana w metodzie init, gdyż
    //ponieważ jak nie jest to później wywala się wyjątek, że aplet nie 
    //jest bezpieczny:
    private void initialize() {
        P11RsaPrivKey privKey = new P11RsaPrivKey(null, 0, null);
        CK_MECHANISM mechanism = new CK_MECHANISM(1L);
        char[] x = "aa".toCharArray();
        CK_TOKEN_INFO ti = new CK_TOKEN_INFO(x, x, x, x, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, null, null, x);
        CK_ATTRIBUTE attr = new CK_ATTRIBUTE();
    }

    private String normalizeCertInfo(X509Certificate cert, String label) throws CertificateEncodingException {
        String certStr = "";

        X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.EmailAddress)[0];

        certStr += "email : " + IETFUtils.valueToString(cn.getFirst().getValue());
        certStr += ", nr seryjny : " + cert.getSerialNumber();
        return certStr;
    }

    public String[] getCertificates() {
        Map<X509Certificate, String> certs = new HashMap<X509Certificate, String>();

        for (Library library : librarys) {
            try {
                List<Token> tokens = library.getTokens();
                try {
                    for (Token token : tokens) {
                        try {
                            certs.putAll(token.getCertificates());
                        } catch (P11Exception e) {
                            System.err.println("Błąd podczas odczytu certyfikatów z tokena: " + e.getMessage());
                        }
                    }
                } finally {
                    for (Token token : tokens) {
                        token.closeSession();
                    }
                }
            } catch (P11Exception e) {
                System.err.println("Błąd podczas odczytu tokenów: " + e.getMessage());
            }
        }
        String[] result = new String[certs.size()];
        int i = 0;
        for (X509Certificate cert : certs.keySet()) {
            try {
                result[i++] = normalizeCertInfo(cert, certs.get(cert));
            } catch (CertificateEncodingException ex) {
                System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            }
        }
        return result;
    }

    private AttributeTable generateSigningCertificate(X509Certificate cert) throws CryptoException {
        DERObjectIdentifier oid = new DERObjectIdentifier("1.2.840.113549.1.9.16.2.12");
        byte[] digest;
        try {
            digest = SHA1Digest.generate(cert.getEncoded());
            ESSCertID essCertID = new ESSCertID(digest);
            SigningCertificate sc = new SigningCertificate(essCertID);
            @SuppressWarnings("UseOfObsoleteCollectionType")
            Hashtable<DERObjectIdentifier, Attribute> attrs = new Hashtable<DERObjectIdentifier, Attribute>();
            DERSet derSet = new DERSet(sc);
            Attribute attr = new Attribute(oid, derSet);
            attrs.put(oid, attr);
            AttributeTable attributeTable = new AttributeTable(attrs);
            return attributeTable;
        } catch (CertificateEncodingException ex) {
            System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            throw new CryptoException("Błąd dekodowania certyfikatu: " + ex.getMessage());
        }
    }

    private byte[] sign(PrivKey privKey, X509Certificate cert, byte[] data) throws CryptoException {

        CMSSignedData signedData = isSignature(data);
        P7Engine generator = new P7Engine();
        CMSProcessable content;

        if (signedData != null) {
            try {
                SignerInformationStore signers = signedData.getSignerInfos();
                Store existingCerts = signedData.getCertificates();
                Store existingAttributeCerts = signedData.getAttributeCertificates();
                Store exisingCrls = signedData.getCRLs();

                generator.addCertificates(existingCerts);
                generator.addAttributeCertificates(existingAttributeCerts);
                generator.addCRLs(exisingCrls);
                generator.addSigners(signers);

                content = signedData.getSignedContent();
            } catch (CMSException ex) {
                System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
                throw new CryptoException("Wystąpił błąd podczas przygotowywanie danych wielopodpisu do podpisu : " + ex.getMessage());
            }
        } else {
            content = new CMSProcessableByteArray(data);
        }

        try {
            generator.addSigner(privKey, cert,
                CMSSignedDataGenerator.ENCRYPTION_RSA,
                CMSSignedDataGenerator.DIGEST_SHA1,
                generateSigningCertificate(cert), null);
            generator.addCertificates(new JcaCertStore(Arrays.asList(cert)));
            signedData = generator.generate(content, true, "BC");
            return signedData.getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            throw new CryptoException("Wystąpił błąd podczas składania podpisu : " + ex.getMessage());
        } catch (NoSuchProviderException ex) {
            System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            throw new CryptoException("Wystąpił błąd podczas składania podpisu : " + ex.getMessage());
        } catch (CMSException ex) {
            System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            throw new CryptoException("Wystąpił błąd podczas składania podpisu : " + ex.getMessage());
        } catch (CertificateEncodingException ex) {
            System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            throw new CryptoException("Wystąpił błąd podczas składania podpisu : " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
            throw new CryptoException("Wystąpił błąd podczas składania podpisu : " + ex.getMessage());
        }

    }

    private CMSSignedData isSignature(byte[] dataToSign) {
        CMSSignedData signedData;
        try {
            signedData = new CMSSignedData(dataToSign);
        } catch (CMSException ex) {
            System.err.println("Dane nie są podpisem : ".concat(ex.getLocalizedMessage()));
            signedData = null;
        }
        return signedData;
    }

    public String signData(String dataToSignb64, String cert, String pin) {

        String result = "ERROR: ";
        boolean certFound = false;
        boolean privKeyFound = false;
        boolean isErrDesc = false;
        byte dataToSign[] = Base64.decode(dataToSignb64);
        
        for (Library library : librarys) {
            try {
                List<Token> tokens = library.getTokens();
                try {
                    for (Token token : tokens) {
                        Map<X509Certificate, String> certs = token.getCertificates();
                        for (X509Certificate c : certs.keySet()) {
                            String certInfo = normalizeCertInfo(c, certs.get(c));
                            if (certInfo.equals(cert)) {
                                certFound = true;
                                token.login(pin, false);
                                try {
                                    P11X509Certificate p11Cert = token.findCertificate(c.getEncoded());
                                    P11RsaPrivKey privKey = token.findRsaPrivateKey(p11Cert);
                                    privKeyFound = true;
                                    byte[] signature = sign(privKey, c, dataToSign);

                                    result = new String(Base64.encode(signature));
                                } finally {
                                    token.logout();
                                }
                            }
                        }
                    }
                } finally {
                    for (Token token : tokens) {
                        token.closeSession();
                    }
                }
            } catch (P11Exception ex) {
                System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
                result += ex.getMessage();
                isErrDesc = true;
            } catch (CryptoException ex) {
                System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
                result += ex.getMessage();
                isErrDesc = true;
            } catch (CertificateEncodingException ex) {
                System.err.println("".concat(SignerApplet.class.getName()).concat(ex.getMessage()));
                result += "Błąd dekodowania certyfikatu: " + ex.getMessage();
                isErrDesc = true;
            }
        }
        if (!isErrDesc) {
            if (!certFound) {
                result += "Nie znaleziono certyfikatu użytkownika";
            } else if (!privKeyFound) {
                result += "Nie znaleziono klucza prywatnego użytkownika";
            }
        }
        return result;
    }

    public boolean isInitError() {
        return initError;
    }
}
