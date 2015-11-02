/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.bsb.b2btester.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.dao.DAOException;
import pl.bsb.b2btester.model.entities.Parameter;
import pl.bsb.b2btester.model.manager.ParameterManager;
import pl.bsb.b2btester.type.ParamGroup;

/**
 *
 * @author aszatkowski
 */
@Named
@SessionScoped
public class ParameterController implements Serializable {

    private static final long serialVersionUID = 14L;
    @Inject
    ParameterManager parameterManager;
    private Parameter selected = null;
    private List<Parameter> parameters = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(ParameterController.class);
    private ParamGroup paramGroupDisplayed = ParamGroup.DEFAULT_SERVER;

    @PostConstruct
    private void init() {
        List<Parameter> parameters1 = parameterManager.getByGroup(ParamGroup.DEFAULT_SERVER);
        parameters = parameters1;
    }

    public List<Parameter> getParameterList() {
        return parameters;
    }

    public void getServerParams() {
        getParamsByGroup(ParamGroup.DEFAULT_SERVER);
    }

    public void getDatabaseParams() {
        getParamsByGroup(ParamGroup.DB_SERVER);
    }

    public void getHermesServicesParams() {
        getParamsByGroup(ParamGroup.HERMES_SERVICES);
    }

    public void getPartnerShipParams() {
        getParamsByGroup(ParamGroup.PARTNERSHIP);
    }

    public void getMessageParams() {
        getParamsByGroup(ParamGroup.MESSAGE);
    }

    public void getParamsByGroup() {
        parameters = parameterManager.getByGroup(paramGroupDisplayed);
    }

    private void getParamsByGroup(ParamGroup group) {
        parameters = parameterManager.getByGroup(group);
    }

    public void save() {
        if (null != selected) {
            logger.info("Zapis parametru: " + selected);
            try {
                parameterManager.merge(selected);
            } catch (DAOException ex) {
                logger.error("Save parameter error.", ex);
            }
        }
    }

    public Parameter getSelected() {
        return selected;
    }

    public void setSelected(Parameter selected) {
        this.selected = selected;
    }

    public ParamGroup getParamGroupDisplayed() {
        return paramGroupDisplayed;
    }

    public void setParamGroupDisplayed(ParamGroup paramGroupDisplayed) {
        this.paramGroupDisplayed = paramGroupDisplayed;
    }
}
