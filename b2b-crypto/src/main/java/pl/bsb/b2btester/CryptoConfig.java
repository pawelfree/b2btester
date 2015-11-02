package pl.bsb.b2btester;

import java.io.ByteArrayInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import pl.bsb.b2btester.encryption.CrypterException;

/**
 * Created by Pawel Dudek (paweld) Date: 24.09.11 Time: 12:53
 */
public class CryptoConfig {

    private final static Logger log = LoggerFactory.getLogger(CryptoConfig.class);
    protected List<X509Certificate> certificatesForEncryption = new ArrayList<>();
    protected Provider provider = null;
    protected String providerName = "BC";
    protected KeyStore keyStore = null;
    private byte[] keyStoreFile = null;
    protected String keyStorePassword = "";
    protected String keyForDecryptionAlias = "";

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public void setKeyForDecryptionAlias(String keyForDecryptionAlias) {
        this.keyForDecryptionAlias = keyForDecryptionAlias;
    }

    public void setCertificatesForEncryption(List<X509Certificate> certificatesForEncryption) {
        this.certificatesForEncryption = certificatesForEncryption;
    }

    public void addCertificateForEncryption(X509Certificate certificate) {
        certificatesForEncryption.add(certificate);
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    protected void verifyProvider() throws CrypterException {
        if (null == provider) {
            setProvider();
            if (null == provider) {
                throw new CrypterException("Provider isn't set");
            }
        }
    }

    public void setProvider() throws CrypterException {
        provider = Security.getProvider(providerName);
        if (provider == null) {
            try {
                Security.addProvider(new BouncyCastleProvider());
                provider = Security.getProvider(providerName);
            } catch (Exception ex) {
                log.error("<setProvider> failed.", ex);
                throw new CrypterException(ex);
            }
        }
    }

    public void resolveDecodeKeyAlias() throws CrypterException {
        loadKeyStore();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                keyForDecryptionAlias = aliases.nextElement();
                if (keyStore.isKeyEntry(keyForDecryptionAlias)) {
                    break;
                }
            }
        } catch (KeyStoreException ex) {
            throw new CrypterException(ex);
        }
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public void loadKeyStore() throws CrypterException {
        //TODO czy to jest konieczne?
        verifyProvider();
        try {
            keyStore = KeyStore.getInstance("PKCS12", providerName);
        } catch (KeyStoreException | NoSuchProviderException ex) {
            log.error("Cant create key store", ex);
            throw new CrypterException(ex.getMessage());
        }

        try (InputStream fis = new ByteArrayInputStream(keyStoreFile)) {
            char[] pass = keyStorePassword.toCharArray();
            keyStore.load(fis, pass);
        } catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
            log.error("Can't load store. {} {}", new Object[]{ex.getClass().getName(), ex.getMessage()});
            throw new CrypterException(ex.getMessage());
        }
    }

    public byte[] getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(byte[] keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }
}
