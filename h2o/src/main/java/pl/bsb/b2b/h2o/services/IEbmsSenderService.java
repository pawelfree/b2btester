package pl.bsb.b2b.h2o.services;

import pl.bsb.b2b.h2o.helper.EmptyParametersException;

import java.io.InputStream;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 10:09
 */
public interface IEbmsSenderService extends IEbmsService {

    public String send(InputStream dataToSend) throws EmptyParametersException;

}
