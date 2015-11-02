package pl.bsb.b2b.h2o.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Payload implements IPayload {

    private String contentType;

    private InputStream inputStream;

    public Payload() {}

    public Payload(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    public Payload(InputStream inputStream) {
        this.inputStream = inputStream;
        this.contentType = "application/octet-stream";
    }


    public void setPayload(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
