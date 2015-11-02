package pl.bsb.b2btester.web.controller;

import hk.hku.cecid.ebms.spa.dao.PartnershipDVO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import pl.bsb.b2btester.jdbc.JDBCDataSource;
import pl.bsb.b2btester.model.entities.Server;
import pl.bsb.b2btester.model.manager.PartnershipManager;
import pl.bsb.b2btester.web.helper.MessageBundle;

/**
 *
 * @author paweld
 */
@Named
@SessionScoped
public class PartnershipController implements Serializable {

    //private static final Logger logger = LoggerFactory.getLogger(PartnershipController.class);
    private static final long serialVersionUID = 2L;
    private List<PartnershipDVO> partnershipList;
    @Inject
    @MessageBundle
    private transient ResourceBundle bundle;
    @Inject
    private ServerController serverController;
    @Inject
    protected JDBCDataSource jDBCDataSource;
    @Inject
    protected PartnershipManager partnershipManager;
    private PartnershipDVO selectedPartnership;
    private PartnershipDVO newPartnership;

    private void init() {
        Server server = serverController.getSelectedServer();
        partnershipList=null;
        if (server != null) {
            jDBCDataSource.setParameters(server.getDbAddress(), server.getDbPort(), server.getDbName(), server.getDbUser(), server.getDbPassword());
            partnershipManager.setjDBCDataSource(jDBCDataSource);
            partnershipList = partnershipManager.getAllPartnerships();
//            selectedPartnership = null;
        }
    }

    @PostConstruct
    public void construct() {
        partnershipList = new ArrayList<>();
        init();
    }

    public void addPartnership() {
        Server server = serverController.getSelectedServer();
        if (server != null) {
            partnershipManager.addPartnership(newPartnership);
        }
        //TODO cos na widok jesli blad
    }
    
    public void createNewPartnership() {
        newPartnership = partnershipManager.createPartnershipDVO();
    }
    
    public PartnershipDVO getNewPartnership() {
        return newPartnership;
    }    
    
    public void deleteAllPartnerships() {
        boolean error = false;
        init();
        for (PartnershipDVO partnershipDVO : partnershipList) {
            if (false == partnershipManager.deletePartnership(partnershipDVO)) {
                error = true;
            }
        }
        init();
        if (error) {
            Faces.getContext().validationFailed();
        }
    }

    public void deletePartnership() {
        if (selectedPartnership != null) {
            if (partnershipManager.deletePartnership(selectedPartnership)) {
                init();
            } else {
                Messages.addError("partnershipDelete", bundle.getString("partnership.dialog.delete.error"));
                Faces.getContext().validationFailed();

            }
        } else {
            Messages.addError("partnershipDelete", bundle.getString("partnership.dialog.delete.error"));
            Faces.getContext().validationFailed();
        }
    }

    public void editPartnership() {
        if (selectedPartnership != null) {
            if (partnershipManager.updatePartnership(selectedPartnership) == true) {
                init();
            } else {
                Messages.addError("partnershipUpdate", bundle.getString("partnership.dialog.update.error"));
                Faces.getContext().validationFailed();
            }
        } else {
            Messages.addError("partnershipUpdate", bundle.getString("partnership.dialog.update.error.notselected"));
            Faces.getContext().validationFailed();
        }
    }

    public List<PartnershipDVO> getPartnershipList() {
        init();
        return partnershipList;
    }

    public void setPartnershipList(List<PartnershipDVO> partnershipList) {
        this.partnershipList = partnershipList;
    }

    public ServerController getServerController() {
        return serverController;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public PartnershipDVO getSelectedPartnership() {
        return selectedPartnership;
    }

    public void setSelectedPartnership(PartnershipDVO selectedPartnership) {
        this.selectedPartnership = selectedPartnership;
    }

}
