package ch.pschatzmann.starschema.views;

import java.util.List;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.filter.IFilter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.calculation.Operations;
/**
 * Interface for Data view which calculates each cell value for each cell in two dimensions
 * (rows, colums)
 * 
 * @author philschatzmann
 * 
 */
public interface ITableView extends IBasicTable {
	public String TOTAL = "[Total]";
	public String OTHERS = "[Others]";


	/**
	 * @return the db
	 */
	public abstract StarDatabase getStarDB();

	/**
	 * Returns the fact attribute name (numeric value) which will be displayed 
	 * @return
	 */
	public abstract String getFactAttribute();

	/**
	 * Defines the attribute name which will be displayed as rows
	 * @param string
	 * @throws StarDBException
	 */
	public abstract void setRowAttribute(String string) throws StarDBException;

	/**
	 * Defiens the attribute name which will be displayed as columns
	 * @param string
	 * @throws StarDBException
	 */
	public abstract void setColumnAttribute(String string) throws StarDBException;

	/**
	 * Determines the value of result cells
	 * @return
	 */
	public abstract int getValueCount();

	/**
	 * Replaces the columns and row attribues
	 * @throws StarDBException 
	 */
	public abstract void switchDimensions() throws StarDBException;

	/**
	 * Defines the fact attribute which is used for calculations
	 * @param string
	 * @throws StarDBException 
	 */
	public abstract void setFactAttribute(String string) throws StarDBException;

	public abstract  void setOperation(Operations operation);

	public abstract boolean isDimensionChanged();

	public abstract Operations getOperation();

	public abstract void addFilter(IFilter filter);

	public abstract List<FactRecord> getFacts() throws StarDBException;


}