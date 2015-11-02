package pl.bsb.b2btester.model.manager;

import java.io.Serializable;
import javax.inject.Named;
import pl.bsb.b2btester.model.dao.DAOTemplate;
import pl.bsb.b2btester.model.entities.Server;

/**
 *
 * @author paweld
 */
@Named
public class ServerManager extends DAOTemplate<Server> implements Serializable {

    private static final long serialVersionUID = 23L;

    public ServerManager() {
        super(Server.class);
    }    
}
