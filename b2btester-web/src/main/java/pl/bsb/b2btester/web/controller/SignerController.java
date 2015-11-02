package pl.bsb.b2btester.web.controller;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/**
 *
 * @author paweld
 */
@Named
@SessionScoped
public class SignerController implements Serializable {

    private static final long serialVersionUID = 16L;    
    @Inject
    private MessageController messageController;
    
    @PostConstruct
    public void construct() {
    }

    @PreDestroy
    public void destroy() {
    }

    public String getAppletCodeBase() {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        final String realPath = ctx.getContextPath();
        return realPath + "/resources/applet-lib/";
    }

    public String getDataToSign() {
        return messageController.getDataToSign();
    }

    public void setDataToSign(String dataToSign) {
        messageController.setSignedData(dataToSign);
    }

    public MessageController getMessageController() {
        return messageController;
    }

    public void setMessageController(MessageController messageController) {
        this.messageController = messageController;
    }
}
