package pl.bsb.b2btester.jsf.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.bsb.b2btester.model.dao.DAOException;

/**
 *
 * @author aszatkowski
 */
public class AjaxExceptionHandler extends ExceptionHandlerWrapper {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AjaxExceptionHandler.class);
    /**
     * wrapped handler.
     */
    private final javax.faces.context.ExceptionHandler wrapped;
    /**
     * klucz atrybutu w sesji.
     */
    public final static String MESSAGE_DETAIL_KEY = "ERROR_DETAIL";
    private final static String SESSION_EXPIRED_PAGE = "/errorpages/expired.xhtml";
    private static final String ATTRIBUTE_ERROR_EXCEPTION = "javax.servlet.error.exception";
    private static final String ATTRIBUTE_ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    private static final String ATTRIBUTE_ERROR_MESSAGE = "javax.servlet.error.message";
    private static final String ATTRIBUTE_ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    private static final String ATTRIBUTE_ERROR_STATUS_CODE = "javax.servlet.error.status_code";

    /**
     * Konstruktor.
     *
     * @param wrapp wrapped handler
     */
    public AjaxExceptionHandler(final javax.faces.context.ExceptionHandler wrapp) {
        super();
        this.wrapped = wrapp;
    }

    /**
     * Pobiera opakowanego handlera.
     *
     * @return handler
     */
    @Override
    public final javax.faces.context.ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    /**
     * handle exception.
     *
     * @throws FacesException wyjatek
     */
    @Override
    public final void handle() throws FacesException {

        for (final Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator(); it.hasNext();) {
            Throwable throwable = it.next().getContext().getException();
            throwable = getRootException(throwable);

            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final ExternalContext externalContext = facesContext.getExternalContext();
            final HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            try {
                LOGGER.info("{}: {}", throwable.getClass().getSimpleName(), throwable.getMessage());
                String message = "";
                String requestPage = "/errorpages/error.jsf";
                if (throwable instanceof ViewExpiredException) {
                    requestPage = SESSION_EXPIRED_PAGE;
                    message = throwable.getLocalizedMessage();
                } else if (throwable instanceof javax.validation.ConstraintViolationException) {
                    final Set<ConstraintViolation<?>> constraintViolations = ((javax.validation.ConstraintViolationException) throwable).getConstraintViolations();
                    for (final ConstraintViolation c : constraintViolations) {
                        LOGGER.error("VoilatedConstraint " + c.getMessage());
                        LOGGER.error("VoilatedConstraint " + c.getInvalidValue());
                        LOGGER.error("VoilatedConstraint " + c.getPropertyPath().toString());
                        LOGGER.error("VoilatedConstraint " + c.getRootBeanClass());
                    }
                } else {
                    message = throwable.getLocalizedMessage();
                }

                final StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                request.getSession().setAttribute(ATTRIBUTE_ERROR_EXCEPTION, sw.toString());
                request.getSession().setAttribute(ATTRIBUTE_ERROR_EXCEPTION_TYPE, throwable.getClass());
                request.getSession().setAttribute(ATTRIBUTE_ERROR_MESSAGE, message);
                request.getSession().setAttribute(ATTRIBUTE_ERROR_REQUEST_URI, request.getRequestURI());
                request.getSession().setAttribute(ATTRIBUTE_ERROR_STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                LOGGER.info("redirect to " + requestPage);
                doRedirect(facesContext, requestPage);

                facesContext.responseComplete();
            } finally {
                it.remove();
            }

        }
        getWrapped().handle();
    }

    /**
     * Dotarcie do przyczyny błędu.
     *
     * @param throwable wyjatek
     * @return wyjatek
     */
    private Throwable getRootException(Throwable throwable) {
        while (null != throwable.getCause()
                && (throwable instanceof FacesException || throwable instanceof DAOException)) {
            throwable = throwable.getCause();
        }
        return throwable;
    }

    public void doRedirect(final FacesContext fc, final String page) throws FacesException {
        try {
            final ExternalContext ec = fc.getExternalContext();
            final ServletRequest request = (ServletRequest) ec.getRequest();
            if (ec.isResponseCommitted()) {
                LOGGER.error("Redirect not possible, response commited !!.");
                return;
            }
            final String partialAjax = request.getParameter("javax.faces.partial.ajax");
            if ("true".equals(partialAjax) && fc.getResponseWriter() == null && fc.getRenderKit() == null) {
                final UIViewRoot view = fc.getApplication().getViewHandler().createView(fc, "");
                fc.setViewRoot(view);
            }
            ec.redirect(ec.getRequestContextPath() + page);

        } catch (final IOException e) {
            LOGGER.error("Redirect to the specified page '" + page + "' failed");
            throw new FacesException(e);
        }
    }
}
