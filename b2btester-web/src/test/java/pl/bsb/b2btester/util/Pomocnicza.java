package pl.bsb.b2btester.util;

import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author aszatkowski
 */
public class Pomocnicza {

    private String podpis1 = "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";
    private String podpis2 = "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";

    public Pomocnicza() {
    }
    private static Provider provider;

    @BeforeClass
    public static void setProvider() {
        provider = Security.getProvider("BC");
        if (provider == null) {
            try {
                Security.addProvider(new BouncyCastleProvider());
                provider = Security.getProvider("BC");
            } catch (Exception ex) {
                System.err.println("<setProvider> failed : " + ex.getMessage());
            }
        }
    }

    @Test
    public void testMultipleSignatures() throws Exception {

        ASN1InputStream is = new ASN1InputStream(Base64.decode(podpis1));
        ASN1Primitive primitive = is.readObject();
        System.out.println(org.bouncycastle.asn1.util.ASN1Dump.dumpAsString(primitive));

        assertTrue(verify(podpis1) == 1);

        is = new ASN1InputStream(Base64.decode(podpis2));
        primitive = is.readObject();
        System.out.println(org.bouncycastle.asn1.util.ASN1Dump.dumpAsString(primitive));

        assertTrue(verify(podpis2) == 2);
    }

    private int verify(String signatureb64) {
        int verified = 0;
        try {
            CMSSignedData signature = new CMSSignedData(Base64.decode(signatureb64));

            //batch verification
            Store certs = signature.getCertificates();
            SignerInformationStore signers = signature.getSignerInfos();
            Collection c = signers.getSigners();
            Iterator it = c.iterator();

            while (it.hasNext()) {
                SignerInformation signer = (SignerInformation) it.next();
                Collection certCollection = certs.getMatches(signer.getSID());

                Iterator certIt = certCollection.iterator();
                X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
                System.out.println(verified);

                if (signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider(new BouncyCastleProvider()).build(certHolder))) {
                    verified++;
                }
            }
            System.out.println(verified);
        } catch (CMSException | StoreException | CertificateException | OperatorCreationException ex) {
            System.err.println("error : " + ex.getMessage());
        }
        return verified;
    }
}
