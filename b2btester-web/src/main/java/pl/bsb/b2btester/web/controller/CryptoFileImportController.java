package pl.bsb.b2btester.web.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.io.IOUtils;
import org.omnifaces.util.Faces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.manager.CryptoManager;
import pl.bsb.b2btester.type.FileType;
import pl.bsb.b2btester.util.Base64;
import pl.bsb.b2btester.web.helper.FileImportHelper;
import pl.bsb.b2btester.web.helper.MessageBundle;

/**
 *
 * @author swarczak
 */
@Named
@SessionScoped
public class CryptoFileImportController extends FileImportHelper{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoFileImportController.class);
    private static final long serialVersionUID = 19L;    
    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;
    @Inject
    private ServerController serverController;
    @Inject
    private CryptoManager cryptoManager;
    
    private List<FileType> fileTypes = new ArrayList<>();
    private CryptoFileType cryptoFileType;
    
    private byte[] data;
    private String password;
    
    public enum CryptoFileType {
        
        ROOT,
        ENCODE,
        DECODE,
        CRL,
        ;
        
    }
    
    public void init(String cryptoFileType, String type) {
        super.init();
        this.cryptoFileType = CryptoFileType.valueOf(cryptoFileType);
        fileTypes.add(FileType.valueOf(type));
    }
    
    @Override
    protected void importFile(InputStream stream) throws Exception {
        data = IOUtils.toByteArray(stream);
    }
    
    public void saveFile() {
        String base64 = Base64.encode(data);
        switch (cryptoFileType) {
            case ROOT:
                if(CryptoManager.checkRootValidity(data)) {
                    serverController.getSelectedServer().setRootCertBase64(base64);
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("dialog.server.files.root.invalid"), null);
                    FacesContext.getCurrentInstance().addMessage("addFileCertMessages", msg);
                    Faces.getContext().validationFailed();
                }
                break;
            case ENCODE:
                serverController.getSelectedServer().setEncodeCertBase64(base64);
                break;
            case DECODE:
                if(password == null || password.isEmpty()) {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("dialog.server.files.password.empty"), null);
                    FacesContext.getCurrentInstance().addMessage("addFileCertMessages", msg);
                    Faces.getContext().validationFailed();
                } else {
                    if(CryptoManager.checkPasswordValidity(data, password)) {
                        serverController.getSelectedServer().setDecodeKeyBase64(base64);
                        serverController.getSelectedServer().setDecodeKeyPassword(password);
                    } else {
                        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString("dialog.server.files.password.invalid"), null);
                        FacesContext.getCurrentInstance().addMessage("addFileCertMessages", msg);
                        Faces.getContext().validationFailed();
                    }
                }
                break;
            case CRL:
                serverController.getSelectedServer().setCrlBase64(base64);
                break;
            default:
                throw new IllegalStateException(String.format("Illegal type of cryptographic file: %s", cryptoFileType.name()));
        }
        serverController.saveSelected();
    }

    @Override
    protected List<FileType> getFileTypes() {
        return fileTypes;
    }

    @Override
    protected void clean() {
        data = null;
        password = null;
    }

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected ResourceBundle getBudle() {
        return bundle;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CryptoFileType getCryptoFileType() {
        return cryptoFileType;
    }

    public void setCryptoFileType(CryptoFileType cryptoFileType) {
        this.cryptoFileType = cryptoFileType;
    }
    
}
