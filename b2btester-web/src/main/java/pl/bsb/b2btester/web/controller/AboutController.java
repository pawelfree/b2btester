package pl.bsb.b2btester.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.jar.Manifest;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author bkubacki
 */
@Named
@javax.enterprise.context.SessionScoped
public class AboutController implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutController.class);
    private static final String MANIFEST_BUILD_TIME = "Build-Time";
    private static final String MANIFEST_BUILD_VERSION = "Build-Version";
    private static final String MANIFEST_IMPLEMENTATION_VENDOR = "Implementation-Vendor";
    private static final String MANIFEST_IMPLEMENTATION_VENDOR_PAGE = "Implementation-Vendor-Page";
    private static final String MANIFEST_IMPLEMENTATION_VENDOR_PHONE = "Implementation-Vendor-Phone";
    private static final String MANIFEST_IMPLEMENTATION_VENDOR_EMAIL = "Implementation-Vendor-Email";
    private static final String MANIFEST_IMPLEMENTATION_TITLE = "Implementation-Title";
    private static final String MANIFEST_IMPLEMENTATION_VERSION = "Implementation-Version";
    private static final String MANIFEST_IMPLEMENTATION_ELIXIROK_VERSION = "Implementation-ElixirOk-Version";
    private static final String MANIFEST_SVN_REVISION = "SVN-Revision";
    private String applicationVersion = "1.00";
    private String applicationVendor = "BSB";
    private String applicationVendorPage = "http://www.bsb.pl";
    private String applicationTitle = "b2btester";
    private String applicationBuildTime = "15:30";
    private String applicationCodeRevision = "1.1.1.1";

    public AboutController() {
    }

    @PostConstruct
    public void init() {
        loadManifest();
    }

    public void loadManifest() {
        try {
            InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest();
            manifest.read(is);
            applicationVersion = manifest.getMainAttributes().getValue(MANIFEST_IMPLEMENTATION_VERSION);
            applicationVendor = manifest.getMainAttributes().getValue(MANIFEST_IMPLEMENTATION_VENDOR);
            applicationVendorPage = manifest.getMainAttributes().getValue(MANIFEST_IMPLEMENTATION_VENDOR_PAGE);
            applicationTitle = manifest.getMainAttributes().getValue(MANIFEST_IMPLEMENTATION_TITLE);
            applicationBuildTime = manifest.getMainAttributes().getValue(MANIFEST_BUILD_TIME);
            applicationCodeRevision = manifest.getMainAttributes().getValue(MANIFEST_SVN_REVISION);

        } catch (IOException ioe) {
            LOGGER.error("Unable to read the Manifest file from classpath", ioe);
            throw new RuntimeException("Unable to read the Manifest file from classpath");
        }
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public String getApplicationVendor() {
        return applicationVendor;
    }

    public String getApplicationVendorPage() {
        return applicationVendorPage;
    }

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public String getApplicationBuildTime() {
        return applicationBuildTime;
    }

    public String getApplicationCodeRevision() {
        return applicationCodeRevision;
    }
}
