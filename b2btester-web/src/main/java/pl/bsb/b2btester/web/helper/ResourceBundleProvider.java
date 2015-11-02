package pl.bsb.b2btester.web.helper;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.enterprise.inject.Produces;

/**
 *
 * @author paweld
 */
public class ResourceBundleProvider {

    private ResourceBundle bundle;
//    @Inject
//    private FacesContext context;

    @Produces
    @MessageBundle
    public ResourceBundle getBundle() {
        if (this.bundle == null) {
            Locale locale = Locale.forLanguageTag("PL");
//            if (null == context) { //dostep do bundle z managers
//                locale = context.getViewRoot().getLocale();
//            }
            bundle = ResourceBundle.getBundle("i18n", locale);
        }
        return this.bundle;
    }
}
