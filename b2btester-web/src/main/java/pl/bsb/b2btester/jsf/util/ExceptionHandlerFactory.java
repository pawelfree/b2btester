package pl.bsb.b2btester.jsf.util;

import javax.faces.context.ExceptionHandler;

/**
 * 
 * @author mkabacinski
 */
public class ExceptionHandlerFactory extends javax.faces.context.ExceptionHandlerFactory {
	/**
	 * parent factory.
	 */
	private final javax.faces.context.ExceptionHandlerFactory parent;

	/**
	 * Konstruktor fabryki.
	 * 
	 * @param prnt
	 *            rodzic fabryki
	 */
	public ExceptionHandlerFactory(final javax.faces.context.ExceptionHandlerFactory prnt) {
		super();
		this.parent = prnt;
	}

	/**
	 * Zwraca handler do obslugi bledow.
	 * 
	 * @return handler
	 */
	@Override
	public final ExceptionHandler getExceptionHandler() {
		return new AjaxExceptionHandler(this.parent.getExceptionHandler());
	}
}
