package ch.pschatzmann.starschema.views.calculation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.ITableView;
import ch.pschatzmann.starschema.views.TableView;

/**
 * We sequentially read thru the whole fact table and add up each record
 * using TotalForOneFact fact records for each key. 
 * 
 * @author philschatzmann
 *
 */

public class Calculator implements ICalculator {
	private final static Logger LOG = Logger.getLogger(TableView.class);
	private Map<Map<String,String>, ICalculationVisitor> calculationResultMap = new HashMap<Map<String,String>, ICalculationVisitor>(5000);
	private StarDatabase db;
	private ITableView view;

	
	public Calculator(ITableView view) {
		this.view = view;		
	}
	
	@Override
	public Map<Map<String,String>, ICalculationVisitor> getResult(ITableView view) throws StarDBException {
		this.view = view;		
		this.db = view.getStarDB();
		return calculateTotals(view.getFacts());
	}

	protected Map<Map<String,String>, ICalculationVisitor> calculateTotals(Collection<FactRecord> facts) throws StarDBException {
		LOG.info("calculateTotals for facts:"+facts.size());
		facts.stream().filter(f -> f!=null).forEach(f -> tryCalculateRow(f));

		LOG.info("calculateTotals-end: "+calculationResultMap.size());
		return calculationResultMap;
	}

	protected void tryCalculateRow(FactRecord fact)  {
		try {
			calculateRow(fact);
		} catch (StarDBException e) {
			throw new RuntimeException(e);
		}
	}

	protected void calculateRow(FactRecord fact) throws StarDBException {
		String col = fact.getDimensionValue(this.getColumnAttribute());
		String row = fact.getDimensionValue(this.getRowAttribute());
		if (col != null && row != null) {
			// cell total
			Map<String, String> key = getKeyMap(col, row);
			updateRecord(key, fact);
			// row total
			Map<String,String> keyRow = getKeyMap(ITableView.TOTAL, row);
			updateRecord(keyRow, fact);
			// col total
			Map<String, String> keyCol = getKeyMap(col, ITableView.TOTAL);
			updateRecord(keyCol, fact);
			// grand total
			Map<String, String> total = getKeyMap(ITableView.TOTAL, ITableView.TOTAL);
			updateRecord(total, fact);		
			
		} else {
			if (col == null)
				LOG.warn("The column value can not be determined for  '" + getColumnAttribute() + "'");
			if (row == null)
				LOG.warn("The row value can not be determined for  '" + getRowAttribute() + "'");
		}
	}

	@Override
	public Map<String,String> getKeyMap(String colValue, String rowValue) {
		Map<String,String>  rec = new HashMap<String, String>(2);
		rec.put(this.getRowAttribute(), rowValue);
		rec.put(this.getColumnAttribute(), colValue);
		return rec;
	}	
	

	protected void updateRecord(Map<String, String> key, FactRecord fact) throws StarDBException {
		// find existing total
		ICalculationVisitor total = calculationResultMap.get(key);
		if (total==null) {
			// add a new total record
			total = new TotalForOneFact(this.getFactAttribute());
			calculationResultMap.put(key, total);
		}
		// add current information to totals
		total.calculate(fact);		
	}

	protected String getColumnAttribute() {
		return this.view.getColumnAttribute();
	}
	protected String getRowAttribute() {
		return this.view.getRowAttribute();
	}

	protected String getFactAttribute() {
		return this.view.getFactAttribute();
	}	
	
}
