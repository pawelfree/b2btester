package pl.bsb.b2btester.encryption;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 24.09.11
 * Time: 12:42
 */
public class CrypterException extends Exception{
    public CrypterException (Exception ex) {
        super(ex);
    }

    public CrypterException(String message) {
        super (message);
    }
}
