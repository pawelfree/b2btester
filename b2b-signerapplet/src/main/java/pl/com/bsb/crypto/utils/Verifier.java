package pl.com.bsb.crypto.utils;

import java.io.IOException;
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

/**
 * Created by bartoszk
 */
public class Verifier {
    
   
    public enum VerifySignatureErrCode{
        DECODE_SIGNATIRE_ERR ("Nie można zdekodować podpisu"),
        CAN_NOT_FIND_LAST_SIGNER_EMAIL ("Nie można odczytać adresu emial ostatniego podpisującego"),
        CAN_NOT_FIND_CERTIFICATE ("Nie można odszukać certyfikatu użytego do podpisu"),
        SIGNATURE_NOT_VERIFIED ("Podpis nie jest poprawny"),
        SIGNATURE_VERIFY_EXCEPTION ("Błąd podczas weryfikacji podpisu"),
        VERIFIER_PROVIDER_ERR ("Wewnętrzny błąd środowiska kryptograficznego"),
        ROOT_IS_EMPTY ("Certyfikat root niepoprawny lub nieustawiony"),
        CERT_VERIFY_INVALID_SIGNATURE ("Niepoprawny podpis w certyfikacie"),
        CERT_VERIFY_ERROR ("Błąd weryfikacji certyfikatu"),
        CERT_KEY_USAGE_INVALID ("Certyifkat nie posiada wymaganego użycia klucza"),
        CERT_DATE_NOT_VALID ("Certyfikat wygasł lub nie uzyskał jeszcze ważności"),
        CRL_CAN_NOT_DOWNLOAD ("Nie można pobrać listy CRL"),
        CRL_DECODE_ERR ("Nie można zdekodować listy CRL"),
        CRL_VERIFY_INVALID_SIGNATURE ("Niepoprawny podpis pod listą CRL"),
        CRL_DATE_NOT_VALID ("Lista CRL nie jest ważna"),
        CRL_CERTIFICATE_REVOKED ("Certyfikat został odwołany")
        ;
        
        private VerifySignatureErrCode(String description){
            this.description = description;
        }
        
        private final String description;
        
        public String getDescription(){
            return description;
        }
        
    }
    
    private String crlAddress;
    private boolean checkCrl;
    private X509Certificate root;
    
    private List<String> signerEmails = new ArrayList<String>();
    private String lastSignerEmail;
    private byte[] content;
    private X509Certificate lastSignerCertificate;
    
    public Verifier(byte[] rootCertificate, String crlAddress, boolean checkCrl){
        this.crlAddress = crlAddress;
        this.checkCrl = checkCrl;
     
    }
    
    private void clear(){
        signerEmails.clear();
        lastSignerEmail = "";
        content = null;
        lastSignerCertificate = null;
    }
    
    private CMSSignedData parseSignature(byte[] signedData) throws VerifierException{
        try {            
            CMSSignedData signature = new CMSSignedData(signedData);  
            return signature;
        } catch (CMSException ex) {
            throw new VerifierException("Błąd weryfikacji: nie można zdekodować podpisu", VerifySignatureErrCode.DECODE_SIGNATIRE_ERR);
        }
    }
    
    private List<SignerId> getSignerIds(CMSSignedData signature){        
        List<SignerId> result = new ArrayList<SignerId>();
        for (SignerInformation si : getSignerInformations(signature)) {
            result.add(si.getSID());
        }
        return result;
    }
   
    private List<SignerInformation> getSignerInformations(CMSSignedData signature){
        List<SignerInformation> result = new ArrayList<SignerInformation>();
        for (Object o : signature.getSignerInfos().getSigners()){
            SignerInformation si = (SignerInformation)o;
            result.addAll(getSignerInformations(si));
        }
        return result;
    }
    
    private List<SignerInformation> getSignerInformations(SignerInformation si){
        List<SignerInformation> result = new ArrayList<SignerInformation>();
        result.add(si);
        if (si.getCounterSignatures().size() > 0){            
            for (Object o : si.getCounterSignatures().getSigners()){
                SignerInformation siChild = (SignerInformation)o;
                result.addAll(getSignerInformations(siChild));
            }
        }
        return result;
    }
    
    private X509Certificate getCertificateFromStore(SignerId signerId, Store certStore) throws VerifierException{
        Collection certCollection = certStore.getMatches(signerId);
        if (certCollection != null){
            Iterator itCert = certCollection.iterator();
            while (itCert.hasNext()){
                Object certHolderObj = itCert.next();
                if (certHolderObj instanceof X509CertificateHolder){
                    X509CertificateHolder certHolder = (X509CertificateHolder)certHolderObj;
                    try{
                        X509Certificate cert = CertificateHelper.parseCertificate(certHolder.getEncoded());
                        if (cert == null){

                            continue;
                        }
                        return cert;
                    }
                    catch (IOException e){

                    }                    
                }
                else{

                }                    
            }
        }
        throw new VerifierException("Nie można odszukać certyfikatu dla: " + signerId, VerifySignatureErrCode.CAN_NOT_FIND_CERTIFICATE);
    }
    
    private List<X509Certificate> getSignersCertificates(CMSSignedData signature) throws VerifierException{        
        List<SignerId> signerIds = getSignerIds(signature);
        List<X509Certificate> result = new ArrayList<X509Certificate>(); 
        Store certStore = signature.getCertificates();
        for (SignerId signerId : signerIds){

            X509Certificate cert = getCertificateFromStore(signerId, certStore);
            result.add(cert);
        }  
        StringBuilder logMsg = new StringBuilder();
        String separator = "";
        for (X509Certificate cert : result){
            logMsg.append(separator).append(cert.getSubjectDN().getName());
            separator = " || ";
        }

        return result;
    }
    
    private byte[] getSignatureContent(CMSSignedData signature){
        byte[] result = (byte[])signature.getSignedContent().getContent();
        return result;
    }
    
    private List<String> prepareSignerEmails(List<X509Certificate> certificates){
        List<String> result = new ArrayList<String>();
        for (X509Certificate certificate : certificates){
            String email = getSubjectEmail(certificate);
            if (email == null || email.isEmpty()){
              }
            else{
                result.add(email);
            }
        }
        return result;
    }
    
    private String getSubjectEmail(X509Certificate cert){
        String result = CertificateHelper.getCertSubjectProperty("E", cert);
        return result;
    }
    
    private String prepareLastSignerEmail() throws VerifierException{
        if (signerEmails.isEmpty()){
            throw new VerifierException("Nie można odczytać adresu emial ostatniego podpisującego", VerifySignatureErrCode.CAN_NOT_FIND_LAST_SIGNER_EMAIL);
        } 
        //TODO: tymczas
        return signerEmails.get(signerEmails.size() - 1);
    }
    
    private X509Certificate prepareSignerCertificate(List<X509Certificate> certificates){        
        return certificates.get(certificates.size() - 1);
    }
    
    public void verify(byte[] signedData) throws VerifierException{

        clear();

        CMSSignedData signature = parseSignature(signedData);        
        List<X509Certificate> certs = getSignersCertificates(signature);
        verifySignatures(signature);
        content = getSignatureContent(signature);
        signerEmails = prepareSignerEmails(certs);
        lastSignerEmail = prepareLastSignerEmail();
        lastSignerCertificate = prepareSignerCertificate(certs);
    }
    
    private void verifySignatures(CMSSignedData signatures) throws VerifierException{
        Store certStore = signatures.getCertificates();
        for (SignerInformation signerInformation : getSignerInformations(signatures)){
            verifySignature(signerInformation, certStore);
        }
    }
    
    private void verifySignature(SignerInformation signerInformation, Store certStore) throws VerifierException{

        X509Certificate cert = getCertificateFromStore(signerInformation.getSID(), certStore);
        String userName = (cert != null ? cert.getSubjectDN().getName().toString() : "unknown");
        try{
            boolean verifyResult = signerInformation.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(new BouncyCastleProvider()).build(cert));
            Object[] logParams = {userName, verifyResult};
            if (!verifyResult){
                throw new VerifierException("Podpis nie jest poprawny", VerifySignatureErrCode.SIGNATURE_NOT_VERIFIED);
            }
        }
        catch (CMSException ex){
            throw new VerifierException("Błąd podczas weryfikacji podpisu: " + ex.getMessage(), VerifySignatureErrCode.SIGNATURE_VERIFY_EXCEPTION);            
        }
        catch (OperatorCreationException ex) {
            throw new VerifierException("Wewnętrzny błąd środowiska kryptograficznego: " + ex.getMessage(), VerifySignatureErrCode.VERIFIER_PROVIDER_ERR);
        }    
    }
    
    public void verifyTest(byte[] signedData) throws VerifierException{
        clear();
        CMSSignedData signature = parseSignature(signedData);  
        verifySignatures(signature);
        List<X509Certificate> certs = getSignersCertificates(signature);
        content = getSignatureContent(signature);
        signerEmails = prepareSignerEmails(certs);
        lastSignerEmail = prepareLastSignerEmail();
        lastSignerCertificate = prepareSignerCertificate(certs);
    }

    public String getLastSignerEmail() {
        return lastSignerEmail;
    }

    public byte[] getContent() {
        return content;
    }

    public List<String> getSignerEmails() {
        return signerEmails;
    }

    public X509Certificate getLastSignerCertificate() {
        return lastSignerCertificate;
    }
    
    /*
    public static void main(String[] args) throws Exception{
        
        Verifier verifier = new Verifier(FileUtils.readFileToByteArray(new File("d:\\bartoszk\\BGK-PROJEKT\\src\\b2b-certyfikaty\\BgkB2BTestCA.cer")), 
                "http://172.30.190.28:8080/crl/b2b.crl", false);        
        byte[] signedData = FileUtils.readFileToByteArray(new File("d:\\bartoszk\\signature\\s_2.sig"));
        verifier.verifyTest(signedData);
        System.out.println(verifier.getLastSignerEmail());
    }
    */
    
}
