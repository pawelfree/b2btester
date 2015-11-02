package pl.bsb.b2btester;

import org.bouncycastle.x509.X509StreamParser;
import org.junit.BeforeClass;
import org.junit.Test;
import pl.bsb.b2btester.encryption.Crypter;
import pl.bsb.b2btester.encryption.CrypterException;

import java.io.*;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import static junit.framework.Assert.*;
import pl.bsb.b2btester.util.Base64;

/**
 * Created by Pawel Dudek (paweld) Date: 24.09.11 Time: 14:09
 */
public class CrypterTest {

    private static Crypter crypter = new Crypter();
    static String keyStorePassword = "B2Btest2013";
    static String certificateForEncryption = "MIIDhjCCAm6gAwIBAgIDAwpeMA0GCSqGSIb3DQEBBQUAMHwxCzAJBgNVBAYTAlBMMREwDwYDVQQHEwhXYXJzemF3YTEdMBsGA1UEChMUTmFyb2Rvd3kgQmFuayBQb2xza2kxKDAmBgNVBAsTH0NlbnRydW0gQ2VydHlmaWthY2ppIEtsdWN6eSBOQlAxETAPBgNVBAMTCENDSy1URVNUMB4XDTEzMDUyMDA4NTIwOVoXDTE0MDUyMDIzNTk1OVowgYoxCzAJBgNVBAYTAlBMMQwwCgYDVQQKEwNOQlAxEDAOBgNVBAoTB05CUCBCMkIxDDAKBgNVBAsTA0RJVDEMMAoGA1UEAxMDRElUMQ8wDQYDVQQqEwZzZXJ3ZXIxDzANBgNVBAQTBnNlcndlcjEdMBsGCSqGSIb3DQEJARYOc2Vyd2VyQGIyYi5uYnAwgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALmbsVE5wX0DorH05pDegdpxld6tma6gTz/TLUJ5H2zPEI8Fo7Fo8fOAHgz55mEJSG7MwJ0sejy0k7W3rsvKpCMdOETDuKaB0om0yX4t0lt/w2vIe14og2kwvCFQuoV3lHeLWxccBvzqkzmtVXgYySFTQ3xDFSMv7ayyF0KXtTBnAgMBAAejgYUwgYIwCwYDVR0PBAQDAgP4MAwGA1UdEwEB/wQCMAAwHwYDVR0jBBgwFoAU7n1tjIR89B0hO6i/lrZEIOXTQ90wRAYDVR0fBD0wOzA5oDegNYYzaHR0cDovL2RvY2VydC5uYnAucGwvQ2VydHlmaWthdHkvT3N0YXRuaUNybHRlc3QuY3JsMA0GCSqGSIb3DQEBBQUAA4IBAQBjiP2LvVqAIr8u0CuJQ7ipQ/aeDnoSoBMqQ0rlEmdcsThJWOT/Ks0CCV6yOc47CY4o/Au4Lxc5R3J8NxbNFAwjiCIB0CPT0C1zfuoqJLCNEsPh97KvzbUOQxfANQU8n3Aleo1jExK7me9+C3wYh9Xjrhj9bCbVUQEkcNcHC1fAonsRu4BoF/rAdlwzopJG/svfqDCBul+TW99v/n1E+RpqWa6BemE+ySJWv7bQOEfDqzZz0MJSI4X0RHHRvOOtORuGGUU0SQFS4AlbYhuKhRVxZfSz2RpjG2Iq+iv+2Kig7Oh5emDhaAxXLgVCvl7bNwQcRFxqEDGUhwS64NQXrcxq";
    static String keyStore = "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";

//    public static void readFile(String fileName) {
//        File reader = new File(fileName);
//        long length = reader.length();
//        byte[] fileData = new byte[(int) length];
//        try (DataInputStream instr = new DataInputStream(new BufferedInputStream(new FileInputStream(reader)))) {
//            instr.read(fileData);
//        } catch (Exception e) {
//            System.out.println(e.getLocalizedMessage());
//        }
//        System.out.println(Base64.encode(fileData));
//    }

    @BeforeClass
    public static void before() {
        
//        readFile("D:\\B2Btester\\cos\\req-bsb.txt.bin");
        
        try {
            crypter.setProvider();
            crypter.setKeyStorePassword(keyStorePassword);
            crypter.setKeyStoreFile(Base64.decode(keyStore));

            X509StreamParser parser = X509StreamParser.getInstance("Certificate", "BC");

            parser.init(new ByteArrayInputStream(Base64.decode(certificateForEncryption)));
            X509Certificate encryptionCert = (X509Certificate) parser.read();
            assertNotNull(encryptionCert);
            crypter.addCertificateForEncryption(encryptionCert);

            KeyStore decryptionKeyStore = KeyStore.getInstance("PKCS12", "BC");
            ByteArrayInputStream stream = new ByteArrayInputStream(Base64.decode(keyStore));

            decryptionKeyStore.load(stream, keyStorePassword.toCharArray());
            assertNotNull(decryptionKeyStore);

            crypter.setKeyStore(decryptionKeyStore);

        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    public void encryptDecryptTest() {
        String stringToTest = "Przykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczonyPrzykladowy tekst, ktory bedzie zabezpieczony";
        byte[] dataToTest = stringToTest.getBytes();
        try {
//            String encryptedData = "MIAGCSqGSIb3DQEHA6CAMIACAQAxggEfMIIBGwIBADCBgzB8MQswCQYDVQQGEwJQTDERMA8GA1UEBxMIV2Fyc3phd2ExHTAbBgNVBAoTFE5hcm9kb3d5IEJhbmsgUG9sc2tpMSgwJgYDVQQLEx9DZW50cnVtIENlcnR5ZmlrYWNqaSBLbHVjenkgTkJQMREwDwYDVQQDEwhDQ0stVEVTVAIDAwpeMA0GCSqGSIb3DQEBAQUABIGAdK6jKQx8cpuBYSIC6sYdgKVfAzz9TFghzJQLH7qSu3dRSMmYZSjKMvPV8l0Ftni0lHaTOl6UXDgvco6489LglJUHXrN0IfHZLBaXwDpBOiq1tGScE36wWImHnTGm8Qq8RVcN5wehJBRn0GrBPattK7233KBZXNnx/o/JV7IBXGgwgAYJKoZIhvcNAQcBMBQGCCqGSIb3DQMHBAi/ipiXaAAWL6CABDD4HyTVCYJjaYsvixblXC0Rd2T4FnoYett/JDWZLdlcZXvMygaWUZw1HCmDMlOcT/IAAAAAAAAAAAAA";
            String encryptedData = crypter.encodeMessageb64(Base64.encode(dataToTest));
            assertNotNull(encryptedData);
            byte[] decryptedData = crypter.decodeMessage(encryptedData);
            assertNotNull(decryptedData);
            assertTrue(stringToTest.compareTo(new String(decryptedData)) == 0);
        } catch (CrypterException e) {
            fail(e.getLocalizedMessage());
        }
    }
}