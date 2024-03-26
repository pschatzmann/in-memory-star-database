package ch.pschatzmann.starschema.errors;

/**
 * Application Errors 
 * @author pschatzmann
 *
 */
public class StarDBException extends Exception {
	private static final long serialVersionUID = -1415298737015909959L;

	public StarDBException(Exception ex) {
		super(ex);
	}

	public StarDBException(String string) {
		super(string);
	}
}
