package pl.bsb.b2btester.signature;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.util.CertificateHelper;

/**
 *
 * @author bartoszk
 */
public class CertificateVerifier implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(CertificateVerifier.class);
    private boolean checkCrl;
    private X509Certificate root;
    private X509CRL crl = null;

    static {
        if (Security.getProvider("BC") == null) {
            Security.insertProviderAt(new BouncyCastleProvider(), 0);
        }
    }

    public CertificateVerifier(byte[] rootCertificate, byte[] crlBytes, boolean checkCrl) {
        this.checkCrl = checkCrl;
        if (true == this.checkCrl) {
            if (this.crl == null) {
                this.checkCrl = false;
                logger.error("Verifier: crl is null but checkCrl is true");
            } else {
                this.crl = CertificateHelper.parseCrl(crlBytes);
                if (this.crl == null) {
                    this.checkCrl = false;
                    logger.error("Verifier: cant parse crl");                   
                }
            }
        }
        root = CertificateHelper.parseCertificate(rootCertificate);
        if (root == null) {
            logger.error("Verifier: can not parse root certificate");
        }
    }

    public void checkSignature(X509Certificate certificate) throws VerifierException {
        logger.debug("checkSignature: certificate={}", certificate != null ? certificate.getSubjectDN().toString() : "NULL");
        if (root == null) {
            logger.error("checkSignature: root is null");
            throw new VerifierException(Verifier.VerifySignatureErrCode.ROOT_IS_EMPTY.getDescription(),
                    Verifier.VerifySignatureErrCode.ROOT_IS_EMPTY);
        }
        try {
            certificate.verify(root.getPublicKey());
        } catch (SignatureException ex) {
            logger.error("verifySignerCertificate: signature invalid={}", ex.getMessage());
            throw new VerifierException(Verifier.VerifySignatureErrCode.CERT_VERIFY_INVALID_SIGNATURE.getDescription() + ": " + ex.getMessage(),
                    Verifier.VerifySignatureErrCode.CERT_VERIFY_INVALID_SIGNATURE);
        } catch (Exception ex) {
            logger.error("verifySignerCertificate: signature verify error={}", ex.getMessage());
            throw new VerifierException(Verifier.VerifySignatureErrCode.CERT_VERIFY_ERROR.getDescription() + ": " + ex.getMessage(),
                    Verifier.VerifySignatureErrCode.CERT_VERIFY_ERROR);
        }
    }

    private void checkKeyUsage(X509Certificate certificate, boolean signer, boolean encrypter) throws VerifierException {
        Object[] logParams = {certificate != null ? certificate.getSubjectDN().toString() : "NULL", signer, encrypter};
        logger.debug("checkKeyUsage: certificate={}, signer={}, encrypter={}", logParams);
        boolean sig = certificate.getKeyUsage()[0];
        boolean enc = certificate.getKeyUsage()[7];
        boolean result = true;
        if (signer) {
            if (!sig) {
                result = false;
            }
        }
        if (encrypter) {
            if (!enc) {
                result = false;
            }
        }
        logger.debug("checkKeyUsage: result={}", result);
        if (!result) {
            throw new VerifierException(Verifier.VerifySignatureErrCode.CERT_KEY_USAGE_INVALID.getDescription(),
                    Verifier.VerifySignatureErrCode.CERT_KEY_USAGE_INVALID);
        }
    }

    private void checkValidity(X509Certificate certificate) throws VerifierException {
        logger.debug("checkValidity: certificate={}", certificate != null ? certificate.getSubjectDN().getName().toString() : "NULL");
        Date now = new Date();
        if (now.compareTo(certificate.getNotBefore()) < 0 || now.compareTo(certificate.getNotAfter()) > 0) {
            logger.error("checkValidity: cert not valid");
            throw new VerifierException(Verifier.VerifySignatureErrCode.CERT_DATE_NOT_VALID.getDescription(), Verifier.VerifySignatureErrCode.CERT_DATE_NOT_VALID);
        }
    }

    private void checkCrl(X509Certificate certificate) throws VerifierException {
        logger.debug("checkCrl: certificate={}", certificate != null ? certificate.getSubjectDN().getName().toString() : "NULL");
        if (!checkCrl) {
            logger.debug("checkCrl: DISABLED");
            return;
        }
        try {
            crl.verify(root.getPublicKey());
        } catch (CRLException | NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException e) {
            logger.error("checkCrl: crl verify error={}", e.getMessage(), e);
            throw new VerifierException(Verifier.VerifySignatureErrCode.CRL_VERIFY_INVALID_SIGNATURE.getDescription() + ": " + e.getMessage(),
                    Verifier.VerifySignatureErrCode.CRL_VERIFY_INVALID_SIGNATURE);
        }
        Date now = new Date();
        if (now.compareTo(crl.getThisUpdate()) < 0 || now.compareTo(crl.getNextUpdate()) > 0) {
            logger.debug("checkCrl: crl expired or not valid");
            throw new VerifierException(Verifier.VerifySignatureErrCode.CRL_DATE_NOT_VALID.getDescription(),
                    Verifier.VerifySignatureErrCode.CRL_DATE_NOT_VALID);
        }
        if (crl.isRevoked(certificate)) {
            throw new VerifierException(Verifier.VerifySignatureErrCode.CRL_CERTIFICATE_REVOKED.getDescription(),
                    Verifier.VerifySignatureErrCode.CRL_CERTIFICATE_REVOKED);
        }
    }

    public void verifySignerCertificate(X509Certificate certificate) throws VerifierException {
        logger.debug("verifySignerCertificate: certificate={}", certificate != null ? certificate.getSubjectDN().toString() : "NULL");
        checkSignature(certificate);
        checkKeyUsage(certificate, true, false);
        checkValidity(certificate);
        checkCrl(certificate);
    }
}
