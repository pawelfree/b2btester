package pl.bsb.b2btester.util;

import java.io.ByteArrayInputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bartoszk
 */
public class CertificateHelper {

    private static final Logger logger = LoggerFactory.getLogger(CertificateHelper.class);
    private static CertificateFactory CERTIFICATE_FACTORY = null;

    static {
        if (Security.getProvider("BC") == null) {
            Security.insertProviderAt(new BouncyCastleProvider(), 0);
        }
    }

    private static synchronized CertificateFactory getCertificateFactory() {
        if (CERTIFICATE_FACTORY == null) {
            try {
                CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509", "BC");
            } catch (CertificateException | NoSuchProviderException e) {
                logger.error("getCertificateFactory: error={}", e.getMessage(), e);
            }
        }
        return CERTIFICATE_FACTORY;
    }

    public static X509Certificate parseCertificate(byte[] cert) {
        logger.debug("parseCertificate: cert.size={}", cert != null ? cert.length : "NULL");
        try {
            X509Certificate x509Cert = (X509Certificate) getCertificateFactory().generateCertificate(new ByteArrayInputStream(cert));
            logger.debug("parseCertificate: x509Cert={}", x509Cert.getSubjectDN().getName());
            return x509Cert;
        } catch (CertificateException e) {
            logger.error("parseCertificate: error={}", e.getMessage(), e);
        }
        return null;
    }

    public static X509CRL parseCrl(byte[] crlBytes) {
        try {
            return (X509CRL) getCertificateFactory().generateCRL(new ByteArrayInputStream(crlBytes));
        } catch (Exception e) {
            logger.error("parseCRL: parse crl error={}", e.getMessage(), e);
        }
        return null;
    }
}
