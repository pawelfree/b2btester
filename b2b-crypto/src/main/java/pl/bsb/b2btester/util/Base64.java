package pl.bsb.b2btester.util;

/**
 *
 * @author paweld
 */
public class Base64 {
    public static String encode(byte[] data){
        byte[] encoded = org.bouncycastle.util.encoders.Base64.encode(data);
        return new String(encoded);
    }
    
    public static byte[] decode(String data){
        return org.bouncycastle.util.encoders.Base64.decode(data);
    }    
}
