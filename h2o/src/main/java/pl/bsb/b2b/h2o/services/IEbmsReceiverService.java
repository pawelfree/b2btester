package pl.bsb.b2b.h2o.services;

import pl.bsb.b2b.h2o.helper.EmptyParametersException;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 10:18
 */
public interface IEbmsReceiverService extends IEbmsService {
    public List<String> getReceivedMessagesIds() throws EmptyParametersException;
    public InputStream getMessage(String messageId) throws EmptyParametersException ;
}
