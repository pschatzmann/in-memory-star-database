package ch.pschatzmann.starschema.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import ch.pschatzmann.common.table.FormatException;
import ch.pschatzmann.common.table.TableFormatterCSV;
import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.IStarDatabaseProvider;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.Utils;
import ch.pschatzmann.starschema.engine.filter.AndFilter;
import ch.pschatzmann.starschema.engine.filter.IFilter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.calculation.Calculator;
import ch.pschatzmann.starschema.views.calculation.CalculatorWithThreads;
import ch.pschatzmann.starschema.views.calculation.ICalculationVisitor;
import ch.pschatzmann.starschema.views.calculation.ICalculator;
import ch.pschatzmann.starschema.views.calculation.Operations;
import ch.pschatzmann.starschema.views.calculation.TotalForOneFact;
import ch.pschatzmann.starschema.views.output.Formatter;

/**
 * Data view which calculates each cell value for each cell in two dimensions
 * (rows, colums)
 * 
 * @author philschatzmann
 * 
 */
public class TableView implements ITableView, Serializable {
	private static final long serialVersionUID = 6431685726028213644L;
	private final static transient Logger LOG = Logger.getLogger(TableView.class);
	private Map<Map<String, String>, ICalculationVisitor> calculationResultMap = new HashMap(50000);
	private transient ICalculator calculator = new CalculatorWithThreads();
	private transient StarDatabase db;
	private List<String> rows = new ArrayList();
	private List<String> columns = new ArrayList();
	private String rowAttribute;
	private String columnAttribute;
	private String factAttribute;
	private Operations operation;
	private boolean multiThreadsActive = false;
	private boolean isDimensionChanged = false;
	private IFilter filter = null;

	/**
	 * Provides all information which is necessary to display the information in
	 * a 2 dimensional table. The total operations is set to Sum.
	 * 
	 * @param db
	 * @param rowAttribute
	 * @param columnAttribute
	 * @param factAttribute
	 * @throws StarDBException
	 */
	public TableView(IStarDatabaseProvider db, String columnAttribute, String rowAttribute, String factAttribute)
			throws StarDBException {
		this(db, columnAttribute, rowAttribute, factAttribute, Operations.Sum);
	}

	/**
	 * Provides all information which is necessary to display the information in
	 * a 2 dimensional table
	 * 
	 * @param db
	 * @param rowAttribute
	 * @param columnAttribute
	 * @param factAttribute
	 * @param operation
	 * @throws StarDBException
	 */
	public TableView(IStarDatabaseProvider db, String columnAttribute, String rowAttribute, String factAttribute,
			Operations operation) throws StarDBException {
		this.db = db.getStarDatabase();
		this.factAttribute = factAttribute;
		this.operation = operation;

		this.setColumnAttribute(columnAttribute);
		this.setRowAttribute(rowAttribute);

	}

	public void recalculate() throws StarDBException {
		if (calculationResultMap.isEmpty()) {
			long startTime = System.currentTimeMillis();
			this.calculationResultMap = calculator.getResult(this);
			setupColumnsAndRows();
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			LOG.info("Calculation time was: " + elapsedTime / 1000.0 + " sec with " + calculationResultMap.size()
					+ " records");

			this.getStarDB().putCache(this);
		}
	}

	private void setupColumnsAndRows() throws StarDBException {
		if (this.filter == null) {
			this.columns = new ArrayList(this.db.getDimensionValues(columnAttribute));
			Collections.sort(this.columns);
			this.rows = new ArrayList(this.db.getDimensionValues(rowAttribute));
			Collections.sort(this.rows);
		} else {
			Set<String> rows = new TreeSet();
			Set<String> cols = new TreeSet();

			for (Map<String, String> map : this.calculationResultMap.keySet()) {
				rows.add(map.get(this.getRowAttribute()));
				cols.add(map.get(this.getColumnAttribute()));
			}
			rows.remove(ITableView.TOTAL);
			this.rows = new ArrayList(rows);
			cols.remove(ITableView.TOTAL);
			this.columns = new ArrayList(cols);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getRows()
	 */
	@Override
	public List<String> getRows() throws StarDBException {
		recalculate();
		return rows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getColumns()
	 */
	@Override
	public List<String> getColumns() throws StarDBException {
		recalculate();
		return columns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getStarDB()
	 */
	@Override
	public StarDatabase getStarDB() {
		return db;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getRowAttribute()
	 */
	@Override
	public String getRowAttribute() {
		return rowAttribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getColumnAttribute()
	 */
	@Override
	public String getColumnAttribute() {
		return columnAttribute;
	}

	/**
	 * @return the operation
	 */
	@Override
	public Operations getOperation() {
		return operation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getValue(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Number getValue(String colValue, String rowValue) throws StarDBException {
		recalculate();
		Map<String, String> selectMap = calculator.getKeyMap(colValue, rowValue);
		TotalForOneFact result = (TotalForOneFact) this.calculationResultMap.get(selectMap);
		if (result != null) {
			switch (getOperation()) {
			case Sum:
				return result.getTotal();
			case Min:
				return result.getMin();
			case Max:
				return result.getMax();
			case Count:
				return result.getCount();
			case Avg:
				return result.getAvg();
			case CountWithValues:
				return result.getCountWithValue();
			}
		}
		return Double.NaN;
	}

	/**
	 * Searches the database for the indicated key value pairs and calculates
	 * the totals using the indicated calculator.
	 * 
	 * @param selectMap
	 * @return
	 * @throws StarDBException
	 */
	public ICalculationVisitor calculate(Map selectMap, ICalculationVisitor calculator) throws StarDBException {
		ICalculationVisitor result = calculationResultMap.get(selectMap);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.views.ITableView#getFactAttribute()
	 */
	@Override
	public String getFactAttribute() {
		return factAttribute;
	}

	/**
	 * Creates a table model for this view
	 * 
	 * @return
	 * @throws StarDBException
	 */
	public TableModel createTableModel() throws StarDBException {
		return new StarTableModel(this);
	}

	private void setRowAttribute1(String string) throws StarDBException {
		if (!string.equals(this.rowAttribute)) {
			this.rowAttribute = string;
		}
	}

	private void setColumnAttribute1(String string) throws StarDBException {
		if (!string.equals(this.columnAttribute)) {
			this.columnAttribute = string;
		}
	}

	@Override
	public void setRowAttribute(String string) throws StarDBException {
		if (!string.equals(this.rowAttribute)) {
			this.calculationResultMap.clear();
		}
		//setRowAttribute1(string);
		this.rowAttribute = string;

	}

	@Override
	public void setColumnAttribute(String string) throws StarDBException {
		if (!string.equals(this.columnAttribute)) {
			this.calculationResultMap.clear();
		}
		//setColumnAttribute1(string);
		this.columnAttribute = string;

	}

	@Override
	public void setFactAttribute(String factAttributeName) {
		if (factAttributeName.equals(this.factAttribute)) {
			this.factAttribute = factAttributeName;
			this.calculationResultMap.clear();
		}
	}

	public boolean isMultiThreadsActive() {
		return this.multiThreadsActive;
	}

	public void setMultiThreadsActive(boolean multiThreadsActive) {
		this.multiThreadsActive = multiThreadsActive;
	}

	public ICalculator getCalculator() {
		return calculator;
	}

	public void setCalculator(ICalculator calculator) {
		this.calculator = calculator;
	}

	@Override
	public int getValueCount() {
		return calculationResultMap.size();
	}

	@Override
	public void switchDimensions() throws StarDBException {
		String temp = getRowAttribute();
		String rowAttribute = getColumnAttribute();
		String columnAttribute = temp;
		this.setRowAttribute1(rowAttribute);
		this.setColumnAttribute1(columnAttribute);
		List<String> tempList = this.rows;
		rows = columns;
		columns = tempList;
		this.isDimensionChanged = !isDimensionChanged;
	}

	@Override
	public void setOperation(Operations operation) {
		this.operation = operation;
	}

	@Override
	public boolean isDimensionChanged() {
		return isDimensionChanged;
	}

	protected Map<Map<String, String>, ICalculationVisitor> getCalculationResult() {
		return calculationResultMap;
	}

	public Comparator getRowComparator(final String sortField, boolean ascending) {
		final int inverse = ascending ? 1 : -1;
		return new Comparator<String>() {
			@Override
			public int compare(String rowTitle1, String rowTitle2) {
				try {
					Object rowValue1 = getValue(sortField, rowTitle1);
					Object rowValue2 = getValue(sortField, rowTitle2);
					return Utils.compare(rowValue1, rowValue2) * inverse;
				} catch (Exception ex) {
					LOG.error("Error in sort for " + rowTitle1 + " and " + rowTitle2 + "", ex);
				}
				return 0;
			}
		};
	}

	public Comparator getColumnComparator(final String sortField, boolean ascending) {
		final int inverse = ascending ? 1 : -1;
		return new Comparator<String>() {
			@Override
			public int compare(String colTitle1, String colTitle2) {
				try {
					Object colValue1 = getValue(colTitle1, sortField);
					Object colValue2 = getValue(colTitle2, sortField);
					return Utils.compare(colValue1, colValue2) * inverse;
				} catch (Exception ex) {
					LOG.error("Error in sort for " + colTitle1 + " and " + colTitle2 + "", ex);
				}
				return 0;
			}
		};
	}

	@Override
	public void addFilter(IFilter filter) {		
		this.filter = this.filter==null ? filter : new AndFilter(this.filter, filter);
	}

	@Override
	public List<FactRecord> getFacts() throws StarDBException {
		return this.db.getFactTable().getFacts(this.filter);
	}
	
	/**
	 * Returns the top n values with the remaining values summarized
	 * @param top
	 * @return
	 * @throws StarDBException 
	 */
	public ITableView getTopN(int top) throws StarDBException {
		return new TopNTableView(this,top);
	}
	
	/**
	 * Returns the standarized values
	 * @return
	 * @throws StarDBException
	 */
	public ITableView getStandardized() throws StarDBException {
		return new StandardizedValuesView(this);
	}
	
	@Override
	public String toString() {
		String result = "";
		try {
			result = new Formatter().format(this, new TableFormatterCSV());
		} catch (FormatException e) {
		}
		return result;
	}

}
