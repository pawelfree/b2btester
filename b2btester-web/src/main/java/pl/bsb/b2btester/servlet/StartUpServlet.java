/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.bsb.b2btester.servlet;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.manager.ParameterManager;

/**
 *
 * @author aszatkowski
 */
@Named
public class StartUpServlet extends HttpServlet {

    private static final long serialVersionUID = 6L;
    @Inject
    ParameterManager parameterManager;
    private static final Logger logger = LoggerFactory.getLogger(StartUpServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.info("StartUpServlet starting...");
        super.init(config);
        String loadConfig = config.getInitParameter("loadConfig");
        if (null != loadConfig && loadConfig.equals("true") && parameterManager.getAllCount().equals(0L)) {
            parameterManager.resetParameters();
        }
        logger.info("StartUpServlet started...");
    }
}
