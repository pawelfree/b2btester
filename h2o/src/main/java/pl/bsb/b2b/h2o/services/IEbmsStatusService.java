package pl.bsb.b2b.h2o.services;

import pl.bsb.b2b.h2o.helper.EmptyParametersException;
import pl.bsb.b2b.h2o.helper.MessageInfo;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 10:09
 */
public interface IEbmsStatusService extends IEbmsService {

    public MessageInfo getStatus(String messageId) throws EmptyParametersException;

}
