package ch.pschatzmann.starschema.engine.dimension;

import java.util.Collection;
import java.util.Map;

import ch.pschatzmann.starschema.engine.FactRecord;

/**
 * Interface for a DimensionRecord. 
 * 
 * @author pschatzmann
 *
 */
public interface IDimensionRecord {

	/**
	 * Returns the information of the dimension record t
	 * 
	 * @return the data
	 */
	Map<String, String> getData();

	/**
	 * Returns all fact records
	 * 
	 * @return
	 */
	Collection<FactRecord> getFacts();

	/**
	 * Returns true if the dimension contains the value for the indicated
	 * dimension key
	 * 
	 * @param keyName
	 * @param value
	 * @return
	 */

	boolean hasValue(String keyName, String value);

	/**
	 * Returns the dimension
	 * 
	 * @return
	 */
	IDimension getDimension();

	/**
	 * Returns true if all field-value entries from the search are matching
	 * 
	 * @param search
	 * @return
	 */
	boolean matches(Map<String, Object> search);

	/**
	 * Returns the Id
	 * 
	 * @return
	 */
	int getId();

	/**
	 * Records a new fact which is related with this dimension.
	 * 
	 * @param factRecord
	 */
	void addFact(FactRecord factRecord);

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);

	@Override
	String toString();
}