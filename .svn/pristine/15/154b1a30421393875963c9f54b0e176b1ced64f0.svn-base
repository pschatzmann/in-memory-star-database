package ch.pschatzmann.starschema.engine.io.collections;

import java.util.Collection;

/**
 * Field support for different data types
 * 
 * @author pschatzmann
 *
 */
public interface IAccess {
	public Collection<String> getFieldNames(Object obj);

	/**
	 * Gets the field value from the object
	 * @param fieldName
	 * @param obj
	 * @return
	 */
	Object getValue(String fieldName, Object obj);
	
	/**
	 * Determines the return type of the field
	 * @param fieldName
	 * @param obj
	 * @return
	 */
	Class getReturnType(String fieldName, Object obj);

	/**
	 * Checks if the current class can handle te indicated object
	 * @param obj
	 * @return
	 */
	boolean isValid(Object obj);
}
