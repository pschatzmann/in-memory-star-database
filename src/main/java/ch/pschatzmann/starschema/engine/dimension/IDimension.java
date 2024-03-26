package ch.pschatzmann.starschema.engine.dimension;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;

/**
 * A dimension in the star schema which consists of dimension attributes and
 * contains all data related dimension records
 * 
 * @author pschatzmann
 *
 */
public interface IDimension {

	/**
	 * Returns the dimension name
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Returns all dimension records
	 * 
	 * @return
	 */
	Collection<IDimensionRecord> getDimensionRecords();

	/**
	 * Returns the attribute names for this dimension
	 * 
	 * @return
	 */
	Collection<String> getAttributeNames();

	/**
	 * Determines a unique dimension record for the requested data.
	 * 
	 * @param rec
	 * @return
	 */
	IDimensionRecord getRecord(Map<String, Object> rec);

	/**
	 * Adds a dimension record to the database
	 * 
	 * @param recordMap
	 * @param dimRecord
	 */
	void addRecord(Map<String, Object> recordMap, IDimensionRecord dimRecord);

	/**
	 * Returns the map which contains only the parameters and values which are
	 * valid for this dimension
	 * 
	 * @param selectMap
	 * @return
	 */
	Map<String, Object> getDimensionSearchRecord(Map<String, Object> selectMap);

	/**
	 * Returns all FactRecords for the indicated search values
	 * 
	 * @param search
	 * @return
	 */
	Collection<FactRecord> select(Map<String, Object> search);

	/**
	 * Returns true if the search record exists
	 * 
	 * @param search
	 * @return
	 */
	boolean exists(Map<String, Object> search);

	/**
	 * Returns the set of attribute values
	 * 
	 * @param attributeName
	 * @return
	 */
	Collection<String> getDimensionValues(String attributeName);

	/**
	 * Clears all the data
	 */
	void clear();

	int getNextId();

	void addCalculatedAttributes(ICalculatedAttributes attributes);

	Collection<ICalculatedAttributes> getCalculatedAttributes();

	boolean isCalculated(String fieldName);

	void setStarDatabase(StarDatabase starDatabase);
	
	void addAttribute(String name,  Function<Map<String,String>,String> function); 

}