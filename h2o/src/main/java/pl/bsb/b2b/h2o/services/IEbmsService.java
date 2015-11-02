package pl.bsb.b2b.h2o.services;

import java.util.Properties;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 14:56
 */
public interface IEbmsService {
    public static String EMPTY_PARAMETERS = "EMPTY_PARAMETERS";
    public void setServiceProperties(Properties properties);
}
