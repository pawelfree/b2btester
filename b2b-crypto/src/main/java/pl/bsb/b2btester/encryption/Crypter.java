package pl.bsb.b2btester.encryption;

import java.io.IOException;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.jcajce.JceKeyTransRecipientInfoGenerator;
import pl.bsb.b2btester.CryptoConfig;

import java.io.Serializable;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.cert.X509CertificateHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.util.Base64;

/**
 * Created by Pawel Dudek (paweld) Date: 24.09.11 Time: 12:41
 */
public class Crypter extends CryptoConfig implements Serializable {

    private final static Logger log = LoggerFactory.getLogger(Crypter.class);

    public String encodeMessageb64(String dataToEncryptb64) throws CrypterException {
        return Base64.encode(encodeMessage(Base64.decode(dataToEncryptb64)));
    }

    private byte[] encodeMessage(byte[] dataToEncrypt) throws CrypterException {

        //TODO weryfikacja czy provider jest zainstalowany?

        try {
            CMSEnvelopedDataGenerator envelopedDataGenerator = new CMSEnvelopedDataGenerator();
            if (certificatesForEncryption.isEmpty()) {
                throw new CrypterException("Empty certificates for encryption");
            }
            for (X509Certificate aCertificateForEncryption : certificatesForEncryption) {
                envelopedDataGenerator.addRecipientInfoGenerator(new JceKeyTransRecipientInfoGenerator(aCertificateForEncryption).setProvider(providerName));
            }
            CMSEnvelopedData envelopedData = envelopedDataGenerator.generate(new CMSProcessableByteArray(dataToEncrypt),
                    new JceCMSContentEncryptorBuilder(CMSAlgorithm.DES_EDE3_CBC).setProvider(providerName).build());
            return envelopedData.getEncoded();
        } catch (CrypterException | CMSException | CertificateEncodingException | IOException ex) {
            log.error("<encodeMessage(byte[])> failed.", ex);
            throw new CrypterException(ex);
        }
    }

    public byte[] decodeMessage(String messageToDecodeb64) throws CrypterException {

        ASN1InputStream cmsMessage = new ASN1InputStream(Base64.decode(messageToDecodeb64));
        CMSEnvelopedDataParser envelopedDataParser;
        try {
            envelopedDataParser = new CMSEnvelopedDataParser(cmsMessage);
        } catch (CMSException | IOException ex) {
            log.debug(Crypter.class.getName().concat("decodeMessage - message to decode : ".concat(messageToDecodeb64)));
            log.error("<decodeMessage(InputStream)> can't create parser.", ex);
            throw new CrypterException(ex);
        }

        RecipientInformationStore recipients = envelopedDataParser.getRecipientInfos();
        if (recipients.size() < 1) {
            throw new CrypterException("There are no recipients in message.");
        }

        log.debug("decodeMessage - recipients size : " + recipients.size());

        Collection c = recipients.getRecipients();
        Iterator it = c.iterator();

        if (keyStore == null) {
            loadKeyStore();
        }

        resolveDecodeKeyAlias();

        byte[] recData = null;
        try {
            X509Certificate cert = (X509Certificate) keyStore.getCertificate(keyForDecryptionAlias);
            if (cert == null) {
                throw new CrypterException("decodeMessage: Can't find certificate for decryption in key store");
            }
            X509CertificateHolder holder = new X509CertificateHolder(cert.getEncoded());
            while (it.hasNext()) {
                RecipientInformation recipient = (RecipientInformation) it.next();

                if ((!holder.getIssuer().equals(((KeyTransRecipientId) recipient.getRID()).getIssuer())
                        || (!holder.getSerialNumber().equals(((KeyTransRecipientId) recipient.getRID()).getSerialNumber())))) {
                    final String errMsg = "decodeMessage: encryption certificate invalid: expected ["
                            + holder.getIssuer() + " :: " + holder.getSerialNumber() + "], "
                            + "found [" + ((KeyTransRecipientId) recipient.getRID()).getIssuer() + " :: " + ((KeyTransRecipientId) recipient.getRID()).getSerialNumber() + "]";
                    log.error(errMsg);
                } else {
                    recData = recipient.getContent(new JceKeyTransEnvelopedRecipient(
                            (PrivateKey) keyStore.getKey(keyForDecryptionAlias, keyStorePassword.toCharArray()))
                            .setProvider("BC"));
                    break;
                }
            }
            cmsMessage.close();
            if (recData == null) {
                log.error("<decodeMessage(InputStream)> message isn't encrypted for any of recipients");
                cmsMessage.close();
                throw new CrypterException("message isn't encrypted for any of recipients");
            }
            return recData;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CMSException | IOException | CertificateEncodingException ex) {
            log.error("<decodeMessage(InputStream)> cant decrypt message.", ex);
            try {
                cmsMessage.close();
            } catch (IOException e) {
            }
            throw new CrypterException(ex);
        }
    }
}
