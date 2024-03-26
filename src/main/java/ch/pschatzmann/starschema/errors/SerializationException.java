package ch.pschatzmann.starschema.errors;

/**
 * Errors related to the writing and reading of data
 * 
 * @author pschatzmann
 *
 */
public class SerializationException extends Exception {
	private static final long serialVersionUID = 1L;

	public SerializationException(Exception ex) {
		super(ex);
	}

	public SerializationException(String string) {
		super(string);
	}

}
