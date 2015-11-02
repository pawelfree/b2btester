package pl.bsb.b2b.h2o;

import pl.bsb.b2b.h2o.services.EbmsReceiverService;
import pl.bsb.b2b.h2o.services.EbmsSenderService;
import pl.bsb.b2b.h2o.services.EbmsStatusService;
import pl.bsb.b2b.h2o.services.IEbmsReceiverService;
import pl.bsb.b2b.h2o.services.IEbmsSenderService;
import pl.bsb.b2b.h2o.services.IEbmsStatusService;
import pl.bsb.b2b.h2o.services.IWspingService;
import pl.bsb.b2b.h2o.services.WspingService;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 09:37
 */
public class Hermes2 implements IHermes2 {

    @Override
    public IEbmsSenderService getSenderService() {
        return new EbmsSenderService();
    }

    @Override
    public IEbmsReceiverService getReceiverService() {
        return new EbmsReceiverService();
    }

    @Override
    public IEbmsStatusService getStatusService() {
        return new EbmsStatusService();
    }

    @Override
    public IWspingService getWspingService() {
        return new WspingService();
    }
}
