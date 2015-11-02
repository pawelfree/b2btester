package pl.bsb.b2b.h2o.services;

import java.util.Properties;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 14:58
 */
public class EbmsService implements IEbmsService {

    protected Properties properties;

    // This is the XML namspace URI for Ebms. It is used when creating web services request.
    protected String nsURI = "http://service.ebms.edi.cecid.hku.hk/";
    protected String nsPingURI = "http://service.main.core.corvus.piazza.cecid.hku.hk/";
    // This is the XML namespace prefix for Ebms.
    protected String nsPrefix = "tns";


    @Override
    public void setServiceProperties(Properties properties) {
        this.properties = properties;
    }

}
