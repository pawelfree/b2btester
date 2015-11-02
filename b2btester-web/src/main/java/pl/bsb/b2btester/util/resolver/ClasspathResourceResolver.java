package pl.bsb.b2btester.util.resolver;

import java.io.InputStream;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 *
 * @author paweld
 */
public class ClasspathResourceResolver implements LSResourceResolver {

    private String path;

    public ClasspathResourceResolver(String path) {
        this.path = path;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(path+systemId);
        return new LSInputImpl(publicId, systemId, resourceAsStream);
    }
}
