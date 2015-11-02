package pl.bsb.b2b.h2o.helper;

/**
 * Created by Pawel Dudek (paweld)
 * Date: 04.09.11
 * Time: 17:28
 */
public class EmptyParametersException extends Exception{
    public EmptyParametersException(Exception ex) {
        super(ex);
    }

    public EmptyParametersException() {
        super();
    }

}