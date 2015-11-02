package pl.bsb.b2b.h2o;

import pl.bsb.b2b.h2o.services.IEbmsReceiverService;
import pl.bsb.b2b.h2o.services.IEbmsSenderService;
import pl.bsb.b2b.h2o.services.IEbmsStatusService;
import pl.bsb.b2b.h2o.services.IWspingService;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 09:38
 */
public interface IHermes2 {
    public IEbmsSenderService getSenderService();
    public IEbmsReceiverService getReceiverService();
    public IEbmsStatusService getStatusService();
    public IWspingService getWspingService();
}
