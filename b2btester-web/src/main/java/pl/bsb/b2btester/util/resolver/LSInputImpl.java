package pl.bsb.b2btester.util.resolver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

/**
 *
 * @author paweld
 */
public class LSInputImpl implements LSInput {

    private String publicId;
    private String systemId;
    private BufferedInputStream inputStream;

    public LSInputImpl(String publicId, String systemId, InputStream resourceAsStream) {
        this.publicId = publicId;
        this.systemId = systemId;
        this.inputStream = new BufferedInputStream(resourceAsStream);
    }

    public BufferedInputStream getInputStream() {
        return this.inputStream;
    }

    public void setInputStream(BufferedInputStream _inputStream) {
        this.inputStream = _inputStream;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStringData() {
        synchronized (inputStream) {
            try {
                byte[] input = new byte[inputStream.available()];
                inputStream.read(input);
                String contents = new String(input);
                return contents;
            } catch (IOException e) {
                throw new RuntimeException(this.getClass()
                        .getName() + " - can't read from input stream. "
                        + e.getMessage());
            }
        }
    } 

    @Override
    public void setStringData(String stringData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public String getBaseURI() {
        return "";
    }

    @Override
    public void setBaseURI(String baseURI) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getCertifiedText() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
