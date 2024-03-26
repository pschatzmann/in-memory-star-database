package ch.pschatzmann.starschema.views;

import java.util.List;

import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Minimum definition of table which can be used for the formatting
 * 
 * @author pschatzmann
 *
 */
public interface IBasicTable {
	/**
	 * @return the rows
	 * @throws StarDBException 
	 */
	public abstract List<String> getRows() throws StarDBException;

	/**
	 * @return the columns
	 * @throws StarDBException 
	 */
	public abstract List<String> getColumns() throws StarDBException;
	
	/**
	 * Determines the attribute name for the row dimension
	 * @return the rowAttribute
	 */
	public abstract String getRowAttribute();

	/**
	 * Determines the attribute name for the column dimension
	 * @return the columnAttribute
	 */
	public abstract String getColumnAttribute();

	/**
	 * Provides the calculation result value for the inndicated col and row value
	 * 
	 * @param colValue
	 * @param rowValue
	 * @return
	 * @throws StarDBException
	 */
	public abstract Number getValue(String colValue, String rowValue) throws StarDBException;



}
