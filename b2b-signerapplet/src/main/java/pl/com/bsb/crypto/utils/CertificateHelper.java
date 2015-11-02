
package pl.com.bsb.crypto.utils;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
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
        if (Security.getProvider("BC") == null){
            Security.insertProviderAt(new BouncyCastleProvider(), 0);
        }
    }
    
    private static synchronized CertificateFactory getCertificateFactory(){
        if (CERTIFICATE_FACTORY == null){
            try{
                CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509", "BC");
            }
            catch (Exception e){
                logger.error("getCertificateFactory: error={}", e.getMessage(), e);
            }
        }
        return CERTIFICATE_FACTORY;
    }
    
    
    public static X509Certificate parseCertificate(byte[] cert){
        logger.debug("parseCertificate: cert.size={}", cert != null ? cert.length : "NULL");
        try{
            X509Certificate x509Cert = (X509Certificate)getCertificateFactory().generateCertificate(new ByteArrayInputStream(cert));
            logger.debug("parseCertificate: x509Cert={}", x509Cert.getSubjectDN().getName());
            return x509Cert;
        }
        catch (Exception e){
            logger.error("parseCertificate: error={}", e.getMessage(), e);
            return null;
        }
    }
    
    public static X509Certificate parseCertificateP12(byte[] cert, String password){
        Object[] logParaams = {cert != null ? cert.length : "NULL", password};
        logger.debug("parseCertificateP12: cert.size={}, password={}", logParaams);
        try{
            KeyStore keyStore = KeyStore.getInstance("pkcs12", "BC");
            keyStore.load(new ByteArrayInputStream(cert), password.toCharArray());
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()){
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)){
                    X509Certificate x509Cert = (X509Certificate) keyStore.getCertificate(alias);
                    logger.debug("parseCertificateP12: x509Cert={}", x509Cert.getSubjectDN().getName());
                    return x509Cert;
                }
            }
        }
        catch (Exception e){
            logger.error("parseCertificateP12: error={}", e.getMessage(), e);
            return null;
        }
        logger.debug("parseCertificateP12: x509Cert=NULL");
        return null;
    }
    
    public static String getIssuerDN(byte[] cert){
        logger.debug("getIssuerDN: cert.size={}", cert.length);
        try{
            X509Certificate x509Cert = parseCertificate(cert);
            if (cert == null){
                return null;
            }
            String result = x509Cert.getIssuerDN().toString();                
            logger.debug("getIssuerDN: result={}", result);
            return result;
        }
        catch (Exception e){
            logger.error("getIssuerDN: error={}", e.getMessage(), e);
            return null;
        }
    }
    
    
     public static String getSubjectDN(byte[] cert){
        logger.debug("getSubjectDN: cert.size={}", cert.length);
        try{
            X509Certificate x509Cert = parseCertificate(cert);
            if (cert == null){
                return null;
            }
            String result = x509Cert.getSubjectDN().toString();                
            logger.debug("getSubjectDN: result={}", result);
            return result;
        }
        catch (Exception e){
            logger.error("getSubjectDN: error={}", e.getMessage(), e);
            return null;
        }
    }
     
     public static String getCertSubjectProperty(String property, X509Certificate cert){
        if (cert == null){
            return "";
        }
        final Map<String, String> propertyMap = new HashMap<String, String>();
        propertyMap.put("E", "OID.1.2.840.113549.1.9.1");
        propertyMap.put("SN", "OID.2.5.4.4");
        propertyMap.put("G", "OID.2.5.4.42");
        propertyMap.put("CN", "CN");
        propertyMap.put("OU", "OU");
        propertyMap.put("O", "O");
        propertyMap.put("C", "C");
        
        String name = cert.getSubjectX500Principal().getName("RFC1779");
        if (propertyMap.containsKey(property)){
            property = propertyMap.get(property);
        }
        int id = name.indexOf(property + "=");
        if (id >= 0){
            int idEnd = name.indexOf(",", id);
            if (idEnd >= 0){
                return name.substring(id + property.length() + 1, idEnd).trim();
            }
            else{
                return name.substring(id + property.length() + 1).trim();
            }
        }        
        return "";
    }
    
   
}
