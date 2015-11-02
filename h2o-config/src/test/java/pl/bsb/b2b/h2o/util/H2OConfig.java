package pl.bsb.b2b.h2o.util;

import java.util.Properties;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 15:04
 */
public class H2OConfig {

    protected H2OConfig() {
    }

    public static Properties getMessageParameters() {
        Properties properties = new Properties();
        properties.setProperty("statusWSURL", "http://localhost:8080/corvus/httpd/ebms/status");
        properties.setProperty("wspingWSURL", "http://localhost:8080/corvus/httpd/wsping");
        properties.setProperty("receiverListWSURL", "http://localhost:8080/corvus/httpd/ebms/receiver_list");
        properties.setProperty("receiverWSURL", "http://localhost:8080/corvus/httpd/ebms/receiver");
        properties.setProperty("senderWSURL", "http://localhost:8080/corvus/httpd/ebms/sender");
        properties.setProperty("cpaId", "cpaId");
        properties.setProperty("service", "service");
        properties.setProperty("fromPartyId", "fromPartyId");
        properties.setProperty("fromPartyType", "fromPartyType");
        properties.setProperty("toPartyId", "toPartyId");
        properties.setProperty("toPartyType", "toPartyType");
        properties.setProperty("numOfMessages", "100");
        return properties;
                
    }
}
