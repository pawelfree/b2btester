package pl.com.bsb.crypto.pkcs11.pkcs7;


import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERConstructedOctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedGenerator;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SimpleAttributeTableGenerator;

import pl.com.bsb.crypto.CryptoException;
import pl.com.bsb.crypto.PrivKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import pl.com.bsb.crypto.pkcs11.Library;
import pl.com.bsb.crypto.pkcs11.P11RsaPrivKey;
import pl.com.bsb.crypto.pkcs11.P11X509Certificate;

import pl.com.bsb.crypto.pkcs11.Token;
import pl.com.bsb.crypto.utils.Verifier;

/**
 * general class for generating a pkcs7-signature message.
 * <p>
 * A simple example of usage.
 *
 * <pre>
 *      CertStore               certs...
 *      CMSSignedDataGenerator    gen = new CMSSignedDataGenerator();
 *
 *      gen.addSigner(privKey, cert, CMSSignedGenerator.DIGEST_SHA1);
 *      gen.addCertificatesAndCRLs(certs);
 *
 *      CMSSignedData           data = gen.generate(content, "BC");
 * </pre>
 */
@SuppressWarnings("unchecked")
public class P7Engine
        extends CMSSignedGenerator {

    List signerInfs = new ArrayList();

    private class SignerInf {

        private final PrivKey privKey;
        private final SignerIdentifier signerIdentifier;
        private final String digestOID;
        private final String encOID;
        private final CMSAttributeTableGenerator sAttr;
        private final CMSAttributeTableGenerator unsAttr;
        private final AttributeTable baseSignedTable;

        SignerInf(
                PrivKey privKey,
                SignerIdentifier signerIdentifier,
                String digestOID,
                String encOID,
                CMSAttributeTableGenerator sAttr,
                CMSAttributeTableGenerator unsAttr,
                AttributeTable baseSignedTable) {
            this.privKey = privKey;
            this.signerIdentifier = signerIdentifier;
            this.digestOID = digestOID;
            this.encOID = encOID;
            this.sAttr = sAttr;
            this.unsAttr = unsAttr;
            this.baseSignedTable = baseSignedTable;
        }

        AlgorithmIdentifier getDigestAlgorithmID() {
            return new AlgorithmIdentifier(new ASN1ObjectIdentifier(digestOID), DERNull.INSTANCE);
        }

        SignerInfo toSignerInfo(
                ASN1ObjectIdentifier contentType,
                CMSProcessable content,
                SecureRandom random,
                Provider sigProvider,
                boolean addDefaultAttributes,
                boolean isCounterSignature)
                throws IOException, SignatureException, InvalidKeyException, NoSuchAlgorithmException, CertificateEncodingException, CMSException, CryptoException {
            AlgorithmIdentifier digAlgId = getDigestAlgorithmID();
            String digestName = CMSSignedHelper.INSTANCE.getDigestAlgName(digestOID);
            MessageDigest dig = CMSSignedHelper.INSTANCE.getDigestInstance(digestName, sigProvider);
            AlgorithmIdentifier encAlgId = new AlgorithmIdentifier(new ASN1ObjectIdentifier(encOID), DERNull.INSTANCE);

            if (content != null) {
                content.write(new DigOutputStream(dig));
            }

            byte[] hash = dig.digest();
            digests.put(digestOID, hash.clone());

            AttributeTable signed;
            if (addDefaultAttributes) {
                Map parameters = getBaseParameters(contentType, digAlgId, hash);
                signed = (sAttr != null) ? sAttr.getAttributes(Collections.unmodifiableMap(parameters)) : null;
            } else {
                signed = baseSignedTable;
            }

            ASN1Set signedAttr = null;
            byte[] tmp;
            if (signed != null) {
                if (isCounterSignature) {
                    Hashtable tmpSigned = signed.toHashtable();
                    tmpSigned.remove(CMSAttributes.contentType);
                    signed = new AttributeTable(tmpSigned);
                }

                // TODO Validate proposed signed attributes

                signedAttr = getAttributeSet(signed);

                // sig must be composed from the DER encoding.
                tmp = signedAttr.getEncoded("DER");
            } else {
                // TODO Use raw signature of the hash value instead
                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                if (content != null) {
                    content.write(bOut);
                }
                tmp = bOut.toByteArray();
            }

            byte[] sigBytes = privKey.sign(tmp);

            ASN1Set unsignedAttr = null;
            if (unsAttr != null) {
                Map parameters = getBaseParameters(contentType, digAlgId, hash);
                parameters.put(CMSAttributeTableGenerator.SIGNATURE, sigBytes.clone());

                AttributeTable unsigned = unsAttr.getAttributes(Collections.unmodifiableMap(parameters));

                // TODO Validate proposed unsigned attributes

                unsignedAttr = getAttributeSet(unsigned);
            }

            return new SignerInfo(signerIdentifier, digAlgId,
                    signedAttr, encAlgId, new DEROctetString(sigBytes), unsignedAttr);
        }
    }

    /**
     * base constructor
     */
    public P7Engine() {
    }

    /**
     * constructor allowing specific source of randomness
     *
     * @param rand instance of SecureRandom to use
     */
    public P7Engine(
            SecureRandom rand) {
        super(rand);
    }

    /**
     * add a signer, specifying the digest encryption algorithm to use - no attributes other than the default ones will
     * be provided here.
     *
     * @param key signing key to use
     * @param cert certificate containing corresponding public key
     * @param encryptionOID digest encryption algorithm OID
     * @param digestOID digest algorithm OID
     */
    public void addSigner(
            PrivKey privKey,
            X509Certificate cert,
            String encryptionOID,
            String digestOID)
            throws IllegalArgumentException {
        signerInfs.add(new SignerInf(privKey, getSignerIdentifier(cert), digestOID, encryptionOID, new DefaultSignedAttributeTableGenerator(), null, null));
    }

    /**
     * Similar method to the other generate methods. The additional argument addDefaultAttributes indicates whether or
     * not a default set of signed attributes need to be added automatically. If the argument is set to false, no
     * attributes will get added at all.
     */
    public CMSSignedData generate(
            String eContentType,
            CMSProcessable content,
            boolean encapsulate,
            String sigProvider,
            boolean addDefaultAttributes)
            throws NoSuchAlgorithmException, NoSuchProviderException, CMSException, CryptoException {
        return generate(eContentType, content, encapsulate, CMSUtils.getProvider(sigProvider), addDefaultAttributes);
    }

    /**
     * Similar method to the other generate methods. The additional argument addDefaultAttributes indicates whether or
     * not a default set of signed attributes need to be added automatically. If the argument is set to false, no
     * attributes will get added at all.
     */
    public CMSSignedData generate(
            String eContentType,
            CMSProcessable content,
            boolean encapsulate,
            Provider sigProvider,
            boolean addDefaultAttributes)
            throws NoSuchAlgorithmException, CMSException, CryptoException {

        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        ASN1EncodableVector signerInfos = new ASN1EncodableVector();

        digests.clear();  // clear the current preserved digest state

        //
        // add the precalculated SignerInfo objects.
        //
        Iterator it = _signers.iterator();

        while (it.hasNext()) {
            SignerInformation signer = (SignerInformation) it.next();
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
            signerInfos.add(signer.toSignerInfo());
        }

        //
        // add the SignerInfo objects
        //
        boolean isCounterSignature = (eContentType == null);

        ASN1ObjectIdentifier contentTypeOID = isCounterSignature
                ? CMSObjectIdentifiers.data
                : new ASN1ObjectIdentifier(eContentType);

        it = signerInfs.iterator();

        while (it.hasNext()) {
            SignerInf signer = (SignerInf) it.next();

            try {
                digestAlgs.add(signer.getDigestAlgorithmID());
                signerInfos.add(signer.toSignerInfo(contentTypeOID, content, rand, sigProvider, addDefaultAttributes, isCounterSignature));
            } catch (IOException e) {
                throw new CMSException("encoding error.", e);
            } catch (InvalidKeyException e) {
                throw new CMSException("key inappropriate for signature.", e);
            } catch (SignatureException e) {
                throw new CMSException("error creating signature.", e);
            } catch (CertificateEncodingException e) {
                throw new CMSException("error creating sid.", e);
            }
        }

        ASN1Set certificates = null;

        if (!certs.isEmpty()) {
            certificates = CMSUtils.createBerSetFromList(certs);
        }

        ASN1Set certrevlist = null;

        if (!crls.isEmpty()) {
            certrevlist = CMSUtils.createBerSetFromList(crls);
        }

//        ASN1OctetString octs = null;
        ASN1Encodable octs = null;
        if (encapsulate) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            if (content != null) {
                try {
                    content.write(bOut);
                } catch (IOException e) {
                    throw new CMSException("encapsulation error.", e);
                }
            }

            octs = new BERConstructedOctetString(bOut.toByteArray());
        }

        ContentInfo encInfo = new ContentInfo(contentTypeOID, octs);

        SignedData sd = new SignedData(
                new DERSet(digestAlgs),
                encInfo,
                certificates,
                certrevlist,
                new DERSet(signerInfos));

        ContentInfo contentInfo = new ContentInfo(
                CMSObjectIdentifiers.signedData, sd);

        return new CMSSignedData(content, contentInfo);
    }

    /**
     * generate a signed object that for a CMS Signed Data object using the given provider - if encapsulate is true a
     * copy of the message will be included in the signature. The content type is set according to the OID represented
     * by the string signedContentType.
     */
    public CMSSignedData generate(
            String eContentType,
            CMSProcessable content,
            boolean encapsulate,
            String sigProvider)
            throws NoSuchAlgorithmException, NoSuchProviderException, CMSException, CryptoException {
        return generate(eContentType, content, encapsulate, CMSUtils.getProvider(sigProvider), true);
    }

    /**
     * generate a signed object that for a CMS Signed Data object using the given provider - if encapsulate is true a
     * copy of the message will be included in the signature with the default content type "data".
     */
    public CMSSignedData generate(
            CMSProcessable content,
            boolean encapsulate,
            String sigProvider)
            throws NoSuchAlgorithmException, NoSuchProviderException, CMSException, CryptoException {
        return this.generate(DATA, content, encapsulate, sigProvider);
    }

    /**
     * add a signer with extra signed/unsigned attributes.
     *
     * @param key signing key to use
     * @param cert certificate containing corresponding public key
     * @param digestOID digest algorithm OID
     * @param signedAttr table of attributes to be included in signature
     * @param unsignedAttr table of attributes to be included as unsigned
     */
    public void addSigner(
            PrivKey privKey,
            X509Certificate cert,
            String encryptionOID,
            String digestOID,
            AttributeTable signedAttr,
            AttributeTable unsignedAttr)
            throws IllegalArgumentException {
        signerInfs.add(new SignerInf(privKey, getSignerIdentifier(cert), digestOID, encryptionOID, new DefaultSignedAttributeTableGenerator(signedAttr), new SimpleAttributeTableGenerator(unsignedAttr), signedAttr));
    }

    static class DigOutputStream extends OutputStream {

        MessageDigest dig;

        public DigOutputStream(MessageDigest dig) {
            this.dig = dig;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            dig.update(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            dig.update((byte) b);
        }
    }

    static SignerIdentifier getSignerIdentifier(X509Certificate cert) {
        TBSCertificateStructure tbs;
        try {
            tbs = CMSUtils.getTBSCertificateStructure(cert);
        } catch (CertificateEncodingException e) {
            throw new IllegalArgumentException(
                    "can't extract TBS structure from this cert");
        }

        IssuerAndSerialNumber encSid = new IssuerAndSerialNumber(tbs
                .getIssuer(), tbs.getSerialNumber().getValue());
        return new SignerIdentifier(encSid);
    }

    static SignerIdentifier getSignerIdentifier(byte[] subjectKeyIdentifier) {
        return new SignerIdentifier(new DEROctetString(subjectKeyIdentifier));
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        Security.addProvider(new BouncyCastleProvider());
        Library library = new Library("ccpkip11");
        List<Token> tokens = library.getTokens();
        if (tokens.isEmpty()) {
            return;
        }
        Token token = tokens.get(0);
        try {
            Set<X509Certificate> certs = token.getCertificates().keySet();
            for (X509Certificate cert : certs) {
                token.login("1111", false);
                try {
                    P11X509Certificate p11Cert = token.findCertificate(cert.getEncoded());
                    P11RsaPrivKey privKey = token.findRsaPrivateKey(p11Cert);

                    P7Engine generator = new P7Engine();
                    generator.addSigner(privKey, cert, CMSSignedDataGenerator.ENCRYPTION_RSA, CMSSignedDataGenerator.DIGEST_SHA1);
                    ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
                    certList.add(cert);
                    CertStore cs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC");
                    generator.addCertificatesAndCRLs(cs);

                    CMSProcessable content = new CMSProcessableByteArray("ala ma kota".getBytes());
                    CMSSignedData signedData = generator.generate(content, true, "BC");

                    System.err.println("!! signature " + new String(Base64.encode(signedData.getEncoded())));
                    
//                    try (FileOutputStream fos = new FileOutputStream("d:\\b2btester\\cos\\test.sig")) {
//                        fos.write(Base64.encode(signedData.getEncoded()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    Verifier v = new Verifier(null, null, false);
                    v.verify(signedData.getEncoded());
                    break;
                } finally {
                    token.logout();
                }
            }
        } finally {
            token.closeSession();
        }
    }
}
