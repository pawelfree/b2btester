package pl.com.bsb.crypto.pkcs11.pkcs7;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSSignedDataGenerator;

@SuppressWarnings("unchecked")
public class CMSSignedHelper
{
    static final CMSSignedHelper INSTANCE = new CMSSignedHelper();

    private static final Map     encryptionAlgs = new HashMap();
    private static final Map     digestAlgs = new HashMap();
    private static final Map     digestAliases = new HashMap();

    private static void addEntries(ASN1ObjectIdentifier alias, String digest, String encryption)
    {
        digestAlgs.put(alias.getId(), digest);
        encryptionAlgs.put(alias.getId(), encryption);
    }

    static
    {
        addEntries(NISTObjectIdentifiers.dsa_with_sha224, "SHA224", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha256, "SHA256", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha384, "SHA384", "DSA");
        addEntries(NISTObjectIdentifiers.dsa_with_sha512, "SHA512", "DSA");
        addEntries(OIWObjectIdentifiers.dsaWithSHA1, "SHA1", "DSA");
        addEntries(OIWObjectIdentifiers.md4WithRSA, "MD4", "RSA");
        addEntries(OIWObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        addEntries(OIWObjectIdentifiers.md5WithRSA, "MD5", "RSA");
        addEntries(OIWObjectIdentifiers.sha1WithRSA, "SHA1", "RSA");
        addEntries(PKCSObjectIdentifiers.md2WithRSAEncryption, "MD2", "RSA");
        addEntries(PKCSObjectIdentifiers.md4WithRSAEncryption, "MD4", "RSA");
        addEntries(PKCSObjectIdentifiers.md5WithRSAEncryption, "MD5", "RSA");
        addEntries(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1", "RSA");
        addEntries(PKCSObjectIdentifiers.sha224WithRSAEncryption, "SHA224", "RSA");
        addEntries(PKCSObjectIdentifiers.sha256WithRSAEncryption, "SHA256", "RSA");
        addEntries(PKCSObjectIdentifiers.sha384WithRSAEncryption, "SHA384", "RSA");
        addEntries(PKCSObjectIdentifiers.sha512WithRSAEncryption, "SHA512", "RSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA1, "SHA1", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA224, "SHA224", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA256, "SHA256", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA384, "SHA384", "ECDSA");
        addEntries(X9ObjectIdentifiers.ecdsa_with_SHA512, "SHA512", "ECDSA");
        addEntries(X9ObjectIdentifiers.id_dsa_with_sha1, "SHA1", "DSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_1, "SHA1", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_224, "SHA224", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_256, "SHA256", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_384, "SHA384", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_ECDSA_SHA_512, "SHA512", "ECDSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_1, "SHA1", "RSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_v1_5_SHA_256, "SHA256", "RSA");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_1, "SHA1", "RSAandMGF1");
        addEntries(EACObjectIdentifiers.id_TA_RSA_PSS_SHA_256, "SHA256", "RSAandMGF1");

        encryptionAlgs.put(X9ObjectIdentifiers.id_dsa.getId(), "DSA");
        encryptionAlgs.put(PKCSObjectIdentifiers.rsaEncryption.getId(), "RSA");
        encryptionAlgs.put(TeleTrusTObjectIdentifiers.teleTrusTRSAsignatureAlgorithm, "RSA");
        encryptionAlgs.put(X509ObjectIdentifiers.id_ea_rsa.getId(), "RSA");
        encryptionAlgs.put(CMSSignedDataGenerator.ENCRYPTION_RSA_PSS, "RSAandMGF1");
        encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_94.getId(), "GOST3410");
        encryptionAlgs.put(CryptoProObjectIdentifiers.gostR3410_2001.getId(), "ECGOST3410");
        encryptionAlgs.put("1.3.6.1.4.1.5849.1.6.2", "ECGOST3410");
        encryptionAlgs.put("1.3.6.1.4.1.5849.1.1.5", "GOST3410");

        digestAlgs.put(PKCSObjectIdentifiers.md2.getId(), "MD2");
        digestAlgs.put(PKCSObjectIdentifiers.md4.getId(), "MD4");
        digestAlgs.put(PKCSObjectIdentifiers.md5.getId(), "MD5");
        digestAlgs.put(OIWObjectIdentifiers.idSHA1.getId(), "SHA1");
        digestAlgs.put(NISTObjectIdentifiers.id_sha224.getId(), "SHA224");
        digestAlgs.put(NISTObjectIdentifiers.id_sha256.getId(), "SHA256");
        digestAlgs.put(NISTObjectIdentifiers.id_sha384.getId(), "SHA384");
        digestAlgs.put(NISTObjectIdentifiers.id_sha512.getId(), "SHA512");
        digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
        digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
        digestAlgs.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
        digestAlgs.put(CryptoProObjectIdentifiers.gostR3411.getId(),  "GOST3411");
        digestAlgs.put("1.3.6.1.4.1.5849.1.2.1",  "GOST3411");

        digestAliases.put("SHA1", new String[] { "SHA-1" });
        digestAliases.put("SHA224", new String[] { "SHA-224" });
        digestAliases.put("SHA256", new String[] { "SHA-256" });
        digestAliases.put("SHA384", new String[] { "SHA-384" });
        digestAliases.put("SHA512", new String[] { "SHA-512" });
    }
    
    /**
     * Return the digest algorithm using one of the standard JCA string
     * representations rather than the algorithm identifier (if possible).
     */
    String getDigestAlgName(
        String digestAlgOID)
    {
        String algName = (String)digestAlgs.get(digestAlgOID);

        if (algName != null)
        {
            return algName;
        }

        return digestAlgOID;
    }

    String[] getDigestAliases(
        String algName)
    {
        String[] aliases = (String[])digestAliases.get(algName);

        if (aliases != null)
        {
            return aliases;
        }

        return new String[0];
    }

    MessageDigest getDigestInstance(
        String algorithm, 
        Provider provider)
        throws NoSuchAlgorithmException
    {
        try
        {
            return createDigestInstance(algorithm, provider);
        }
        catch (NoSuchAlgorithmException e)
        {
            String[] aliases = getDigestAliases(algorithm);
            for (int i = 0; i != aliases.length; i++)
            {
                try
                {
                    return createDigestInstance(aliases[i], provider);
                }
                catch (NoSuchAlgorithmException ex)
                {
                    // continue
                }
            }
            if (provider != null)
            {
                return getDigestInstance(algorithm, null); // try rolling back
            }
            throw e;
        }
    }

    private MessageDigest createDigestInstance(
        String algorithm,
        Provider provider)
        throws NoSuchAlgorithmException
    {
        if (provider != null)
        {
            return MessageDigest.getInstance(algorithm, provider);
        }
        else
        {
            return MessageDigest.getInstance(algorithm);
        }
    }

    AlgorithmIdentifier fixAlgID(AlgorithmIdentifier algId)
    {
        if (algId.getParameters() == null)
        {
            return new AlgorithmIdentifier(algId.getObjectId(), DERNull.INSTANCE);
        }

        return algId;
    }
}

