package pl.bsb.b2btester.helper;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import pl.bsb.b2btester.web.helper.MessageBundle;

/**
 *
 * @author aszatkowski
 */
@Named
@SessionScoped
public class BeanHelper implements Serializable {

    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;

    public String getCurrentUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public boolean isUserAdmin() {
        return FacesContext.getCurrentInstance().getExternalContext().isUserInRole("ADMIN");
    }

    public boolean isUserInRole(List<String> roles) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        for (String role : roles) {
            if (externalContext.isUserInRole(role)) {
                return true;
            }
        }
        return false;
    }

    public void addInfoMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void addErrorMessage(String message) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void addErrorMessageFromResource(String key, Object... params) {
        final String msg = getMessage(key, params);
        addErrorMessage(msg);
    }

    public void addInfoMessageFromResource(String key, Object... params) {
        final String msg = getMessage(key, params);
        addInfoMessage(msg);
    }

    public String getMessage(String key, Object... params) {
        if (key == null || key.isEmpty()) {
            return "";
        }
        String result;
        boolean error = false;
        if (bundle.containsKey(key)) {
            result = bundle.getString(key);
        } else {
            error = true;
            for (Object param : params) {
                key += " " + param;
            }
            result = "###" + key + "###";
        }

        if (!error) {
            if (params != null && params.length > 0) {
                result = MessageFormat.format(result, params);
            }
        }
        return result;
    }

    public void throwValidatorError(String msgKey, Object... params) {
        final String errMsg = getMessage(msgKey, params);
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, errMsg, errMsg);
        throw new ValidatorException(facesMessage);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
