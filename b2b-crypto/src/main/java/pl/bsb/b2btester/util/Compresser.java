package pl.bsb.b2btester.util;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 17.09.11
 * Time: 16:00
 */
public class Compresser {

    public static byte[] compress(byte[] inputBytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
        byte[] result;
        try (ByteArrayOutputStream outputStream = compress(inputStream)) {
            result = outputStream.toByteArray();
        }
        return result;
    }

    public static ByteArrayOutputStream compress(ByteArrayInputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(output)) {
            doCopyFast(inputStream, deflaterStream);
            deflaterStream.finish();
            inputStream.close();
        }
        return output;
    }

    public static byte[] decompress(byte[] inputBytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
        byte[] result;
        try (ByteArrayOutputStream outputStream = decompress(inputStream)) {
            result = outputStream.toByteArray();
        }
        return result;
    }

    public static ByteArrayOutputStream decompress(ByteArrayInputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InflaterInputStream inflaterStream = new InflaterInputStream(inputStream)) {
            doCopyFast(inflaterStream, output);
        }
        inputStream.close();
        return output;
    }

    private static final int BUFFER_SIZE = 1024 * 1024 * 10;

    public static void doCopyFast(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = is.read(buffer, 0, BUFFER_SIZE)) != -1) {
            os.write(buffer, 0, n);
        }
        os.flush();
    }
}
