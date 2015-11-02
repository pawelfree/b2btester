package pl.bsb.b2btester.model.manager;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.encryption.Crypter;
import pl.bsb.b2btester.encryption.CrypterException;
import pl.bsb.b2btester.signature.CertificateVerifier;
import pl.bsb.b2btester.signature.Verifier;
import pl.bsb.b2btester.signature.VerifierException;
import pl.bsb.b2btester.util.Base64;
import pl.bsb.b2btester.util.CertificateHelper;
import pl.bsb.b2btester.util.Compresser;

/**
 *
 * @author swarczak
 */
@Named
@SessionScoped
public class CryptoManager implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CryptoManager.class);
    private static final long serialVersionUID = 21L;
    Crypter crypter;
    Verifier verifier;

    @PostConstruct
    public void init() {
        crypter = new Crypter();
        verifier = new Verifier();
    }

    public void setEncodeConfig(String encodedCertb64) {
        List<X509Certificate> encryptionCertificates = new ArrayList<>();
        encryptionCertificates.add(CertificateHelper.parseCertificate(Base64.decode(encodedCertb64)));
        crypter.setCertificatesForEncryption(encryptionCertificates);
    }

    public void setDecodeConfig(String decodeKeyStore, String password, String rootCertb64, String crlb64, boolean checkCrl) throws CrypterException {
        crypter.setKeyStoreFile(Base64.decode(decodeKeyStore));
        crypter.setKeyStorePassword(password);
        crypter.resolveDecodeKeyAlias();
        verifier.setConfig(Base64.decode(rootCertb64), crlb64 != null ? Base64.decode(crlb64) : null, checkCrl);
    }

    public static boolean checkPasswordValidity(byte[] data, String password) {
        Crypter c = new Crypter();
        c.setKeyStoreFile(data);
        c.setKeyStorePassword(password);
        try {
            c.loadKeyStore();
            return true;
        } catch (CrypterException ex) {
            logger.error("Password verification failure.");
        }
        return false;
    }

    public static boolean checkRootValidity(byte[] data) {
        CertificateVerifier certificateVerifier = new CertificateVerifier(data, null, true);
        try {
            certificateVerifier.checkSignature(CertificateHelper.parseCertificate(data));
        } catch (VerifierException ex) {
            return false;
        }
        return true;
    }

    public String compressb64(byte[] dataToCompress) throws IOException {
        return Base64.encode(Compresser.compress(dataToCompress));
    }

    public String decompressb64(byte[] dataToDecompress) throws IOException {
        return Base64.encode(Compresser.decompress(dataToDecompress));
    }

    public String encodeB64(String messageToEncodeb64) throws CrypterException {
        crypter.setProvider();
        return crypter.encodeMessageb64(messageToEncodeb64);
    }

    public String decodeAndVerifyB64(String messageToDecodeb64) throws CrypterException, VerifierException {
        crypter.setProvider();
        logger.debug("decodeAndVerify - message to decrypt : " + messageToDecodeb64);
        verifier.verify(crypter.decodeMessage(messageToDecodeb64));
        logger.debug("decodeAndVerify - result : " + Base64.encode(verifier.getContent()));
        return Base64.encode(verifier.getContent());
    }

    public boolean isSignature(String signatureb64) {
        return verifier.isSigature(Base64.decode(signatureb64));
    }
}
