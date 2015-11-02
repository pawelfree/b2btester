package pl.bsb.b2btester.signature;

import java.io.IOException;
import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.util.CertificateHelper;

/**
 * Created by bartoszk
 */
public class Verifier implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Verifier.class);

    public enum VerifySignatureErrCode {

        DECODE_SIGNATIRE_ERR("Nie można zdekodować podpisu"),
        CAN_NOT_FIND_LAST_SIGNER_EMAIL("Nie można odczytać adresu emial ostatniego podpisującego"),
        CAN_NOT_FIND_CERTIFICATE("Nie można odszukać certyfikatu użytego do podpisu"),
        SIGNATURE_NOT_VERIFIED("Podpis nie jest poprawny"),
        SIGNATURE_VERIFY_EXCEPTION("Błąd podczas weryfikacji podpisu"),
        VERIFIER_PROVIDER_ERR("Wewnętrzny błąd środowiska kryptograficznego"),
        ROOT_IS_EMPTY("Certyfikat root niepoprawny lub nieustawiony"),
        CERT_VERIFY_INVALID_SIGNATURE("Niepoprawny podpis w certyfikacie"),
        CERT_VERIFY_ERROR("Błąd weryfikacji certyfikatu"),
        CERT_KEY_USAGE_INVALID("Certyifkat nie posiada wymaganego użycia klucza"),
        CERT_DATE_NOT_VALID("Certyfikat wygasł lub nie uzyskał jeszcze ważności"),
        CRL_CAN_NOT_DOWNLOAD("Nie można pobrać listy CRL"),
        CRL_DECODE_ERR("Nie można zdekodować listy CRL"),
        CRL_VERIFY_INVALID_SIGNATURE("Niepoprawny podpis pod listą CRL"),
        CRL_DATE_NOT_VALID("Lista CRL nie jest ważna"),
        CRL_CERTIFICATE_REVOKED("Certyfikat został odwołany");

        private VerifySignatureErrCode(String description) {
            this.description = description;
        }
        private final String description;

        public String getDescription() {
            return description;
        }
    }
    private byte[] content;
    private CertificateVerifier certificateVerifier;

    public Verifier() {
    }

    public void setConfig(byte[] rootCertificate, byte[] crlBytes, boolean checkCrl) {
        certificateVerifier = new CertificateVerifier(rootCertificate, crlBytes, checkCrl);
    }

    private void clear() {
        content = null;
    }

    private CMSSignedData parseSignature(byte[] signedData) throws VerifierException {
        try {
            CMSSignedData signature = new CMSSignedData(signedData);
            return signature;
        } catch (CMSException ex) {
            throw new VerifierException("Błąd weryfikacji: nie można zdekodować podpisu", Verifier.VerifySignatureErrCode.DECODE_SIGNATIRE_ERR);
        }
    }

    private List<SignerId> getSignerIds(CMSSignedData signature) {
        List<SignerId> result = new ArrayList<>();
        for (SignerInformation si : getSignerInformations(signature)) {
            result.add(si.getSID());
        }
        return result;
    }

    private List<SignerInformation> getSignerInformations(CMSSignedData signature) {
        List<SignerInformation> result = new ArrayList<>();
        for (Object o : signature.getSignerInfos().getSigners()) {
            SignerInformation si = (SignerInformation) o;
            result.addAll(getSignerInformations(si));
        }
        return result;
    }

    private List<SignerInformation> getSignerInformations(SignerInformation si) {
        List<SignerInformation> result = new ArrayList<>();
        result.add(si);
        if (si.getCounterSignatures().size() > 0) {
            for (Object o : si.getCounterSignatures().getSigners()) {
                SignerInformation siChild = (SignerInformation) o;
                result.addAll(getSignerInformations(siChild));
            }
        }
        return result;
    }

    private X509Certificate getCertificateFromStore(SignerId signerId, Store certStore) throws VerifierException {
        logger.debug("getCertificateFromStore: signerId={}", signerId);
        Collection certCollection = certStore.getMatches(signerId);
        if (certCollection != null) {
            Iterator itCert = certCollection.iterator();
            while (itCert.hasNext()) {
                Object certHolderObj = itCert.next();
                if (certHolderObj instanceof X509CertificateHolder) {
                    X509CertificateHolder certHolder = (X509CertificateHolder) certHolderObj;
                    try {
                        X509Certificate cert = CertificateHelper.parseCertificate(certHolder.getEncoded());
                        if (cert == null) {
                            logger.warn("getCertificateFromStore: cert decode error");
                            continue;
                        }
                        return cert;
                    } catch (IOException e) {
                        logger.warn("getCertificateFromStore: cert decode error: {}", e.getMessage());
                    }
                } else {
                    logger.warn("getCertificateFromStore: cert expacted, but was {}", certHolderObj.getClass());
                }
            }
        }
        throw new VerifierException("Nie można odszukać certyfikatu dla: " + signerId, Verifier.VerifySignatureErrCode.CAN_NOT_FIND_CERTIFICATE);
    }

    private List<X509Certificate> getSignersCertificates(CMSSignedData signature) throws VerifierException {
        List<SignerId> signerIds = getSignerIds(signature);
        List<X509Certificate> result = new ArrayList<>();
        Store certStore = signature.getCertificates();
        for (SignerId signerId : signerIds) {
            logger.debug("getSignersCertificates: signerId={}", signerId);
            X509Certificate cert = getCertificateFromStore(signerId, certStore);
            result.add(cert);
        }
        StringBuilder logMsg = new StringBuilder();
        String separator = "";
        for (X509Certificate cert : result) {
            logMsg.append(separator).append(cert.getSubjectDN().getName());
            separator = " || ";
        }
        logger.debug("getSignersCertificates: result=" + logMsg.toString());
        return result;
    }

    private void verifySignerCertificates(List<X509Certificate> certificates) throws VerifierException {
        logger.debug("verifySignerCertificates");
        for (X509Certificate cert : certificates) {
            verifySignerCertificate(cert);
        }
    }

    private void verifySignerCertificate(X509Certificate certificate) throws VerifierException {
        logger.debug("verifySignerCertificate: certificate={}", certificate != null ? certificate.getSubjectDN().toString() : "NULL");
        certificateVerifier.verifySignerCertificate(certificate);
    }

    private byte[] getSignatureContent(CMSSignedData signature) {
        byte[] result = (byte[]) signature.getSignedContent().getContent();
        return result;
    }

    public void verify(byte[] signedData) throws VerifierException {
        clear();
        CMSSignedData signature = parseSignature(signedData);
        List<X509Certificate> certs = getSignersCertificates(signature);
        verifySignerCertificates(certs);
        verifySignatures(signature);
        content = getSignatureContent(signature);
    }

    private void verifySignatures(CMSSignedData signatures) throws VerifierException {
        Store certStore = signatures.getCertificates();
        for (SignerInformation signerInformation : getSignerInformations(signatures)) {
            verifySignature(signerInformation, certStore);
        }
    }

    private void verifySignature(SignerInformation signerInformation, Store certStore) throws VerifierException {
        X509Certificate cert = getCertificateFromStore(signerInformation.getSID(), certStore);
        String userName = (cert != null ? cert.getSubjectDN().getName().toString() : "unknown");
        logger.debug("verifySignature: user={}", userName);
        try {
            boolean verifyResult = signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(new BouncyCastleProvider()).build(cert));
            Object[] logParams = {userName, verifyResult};
            logger.debug("verifySignature: user={}, result={}", logParams);
            if (!verifyResult) {
                throw new VerifierException("Podpis nie jest poprawny", Verifier.VerifySignatureErrCode.SIGNATURE_NOT_VERIFIED);
            }
        } catch (CMSException ex) {
            throw new VerifierException("Błąd podczas weryfikacji podpisu: " + ex.getMessage(), Verifier.VerifySignatureErrCode.SIGNATURE_VERIFY_EXCEPTION);
        } catch (OperatorCreationException ex) {
            throw new VerifierException("Wewnętrzny błąd środowiska kryptograficznego: " + ex.getMessage(), Verifier.VerifySignatureErrCode.VERIFIER_PROVIDER_ERR);
        }
    }

    public boolean isSigature(byte[] signedData) {
        try {
            parseSignature(signedData);
            return true;
        } catch (Exception ex) {
            logger.debug("isSignature : failed parsing signature", ex);
            return false;
        }
    }

    public void verifyTest(byte[] signedData) throws VerifierException {
        clear();
        CMSSignedData signature = parseSignature(signedData);
        verifySignatures(signature);
        List<X509Certificate> certs = getSignersCertificates(signature);
        verifySignerCertificates(certs);
        content = getSignatureContent(signature);
    }

    public byte[] getContent() {
        return content;
    }
}
