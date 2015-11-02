package pl.bsb.b2btester.web.helper;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

/**
 *
 * @author paweld
 */
class FacesContextProducer {
     @Produces
     @RequestScoped
     public FacesContext getFacesContext() {
         return FacesContext.getCurrentInstance();
      }
  }