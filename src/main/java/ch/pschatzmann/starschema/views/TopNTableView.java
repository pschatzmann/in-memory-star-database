package ch.pschatzmann.starschema.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.Utils;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.filter.Filter;
import ch.pschatzmann.starschema.engine.filter.FilterCriteria;
import ch.pschatzmann.starschema.engine.filter.IFilter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.calculation.CalculationResultMerger;
import ch.pschatzmann.starschema.views.calculation.ICalculationVisitor;
import ch.pschatzmann.starschema.views.calculation.Operations;
import ch.pschatzmann.starschema.views.calculation.TotalForOneFact;

/**
 * This view provides the top n records for each dimension. This view is maily
 * used because the fx charts behave pretty badly when they are passed too many
 * items: so we just determine the top n records and merge the other ones into
 * an OTHERS line.
 * 
 * @author philschatzmann
 * 
 */
public class TopNTableView implements ITableView, Serializable {
	private static final Logger LOG = Logger.getLogger(TopNTableView.class);
	private TableView view;
	private int max;
	private List<String> rows = new ArrayList();
	private List<String> columns = new ArrayList();
	private Utils utils = new Utils();

	public TopNTableView(TableView view, int max) throws StarDBException {
		this.view = view;
		this.max = max;
		// setup rows and columns
		this.setColumnAttribute(view.getColumnAttribute());
		this.setRowAttribute(view.getRowAttribute());
		if (view.getCalculationResult().isEmpty()) {
			view.recalculate();
		}
		if (tooManyRows() && totalsNotAvailable()) {
			calculateOtherValues();
		}
	}

	private boolean totalsNotAvailable() throws StarDBException {
		Number totals = this.getValue(TOTAL, OTHERS);
		return totals==null || (totals instanceof Double && ((Double)totals).isNaN());
	}

	protected boolean tooManyRows() throws StarDBException {
		return view.getRows().size() != this.getRows().size() || view.getColumns().size() != this.getColumns().size();
	}

	protected void calculateOtherValues() throws StarDBException {
		CalculationResultMerger merger = new CalculationResultMerger(view.getCalculationResult());
		// check if we need to consilidate
		Set<String> otherRows = new HashSet(view.getRows());
		otherRows.removeAll(this.getRows());
		Set<String> otherColumns = new HashSet(view.getColumns());
		otherColumns.removeAll(this.getColumns());
		Map<Map, ICalculationVisitor> resultCopy = new HashMap(view.getCalculationResult());
		for (Entry<Map, ICalculationVisitor> entry : resultCopy.entrySet()) {
			// check if the entry contains any attribute names for other
			Map<String, String> keyMap = entry.getKey();
			boolean isOtherColumn = (otherColumns.contains(keyMap.get(this.getColumnAttribute())));
			boolean isOtherRow = (otherRows.contains(keyMap.get(this.getRowAttribute())));
			// if either a conlumn or a row is another value we need to add
			// a new totals entry
			if (isOtherColumn || isOtherRow) {
				Map<String, String> newKeyMap = new HashMap(2);
				if (isOtherRow) {
					newKeyMap.put(this.getRowAttribute(), OTHERS);
				} else {
					newKeyMap.put(this.getRowAttribute(), keyMap.get(this.getRowAttribute()));
				}
				if (isOtherColumn) {
					newKeyMap.put(this.getColumnAttribute(), OTHERS);
				} else {
					newKeyMap.put(this.getColumnAttribute(), keyMap.get(this.getColumnAttribute()));
				}
				merger.mergeResult(newKeyMap, (TotalForOneFact) entry.getValue());
			}
		}
	}

	@Override
	public List<String> getRows() {
		return !view.isDimensionChanged() ? this.rows : this.columns;
	}

	@Override
	public List<String> getColumns() {
		return !view.isDimensionChanged() ? this.columns : this.rows;
	}

	@Override
	public StarDatabase getStarDB() {
		return view.getStarDB();
	}

	@Override
	public String getRowAttribute() {
		return view.getRowAttribute();
	}

	@Override
	public String getColumnAttribute() {
		return view.getColumnAttribute();
	}

	@Override
	public Number getValue(String colValue, String rowValue) throws StarDBException {
		Number result = view.getValue(colValue, rowValue);
		return result;
	}

	@Override
	public String getFactAttribute() {
		return view.getFactAttribute();
	}

	@Override
	public void setRowAttribute(String string) throws StarDBException {
		boolean merge = false;
		if (!string.equals(view.getRowAttribute())) {
			view.setRowAttribute(string);
			view.recalculate();
			merge = true;
		}
		this.rows = view.getRows();
		Collections.sort(this.rows, view.getRowComparator(ITableView.TOTAL, false));
		this.rows = rows.subList(0, Math.min(max, view.getRows().size()));
		if (this.rows.size() < view.getRows().size() && !view.getRows().contains(OTHERS)) {
			this.rows.add(ITableView.OTHERS);
			merge = true;
		}
		if (merge && this.totalsNotAvailable()) {
			this.calculateOtherValues();
			Collections.sort(this.rows, view.getRowComparator(ITableView.TOTAL, false));
		}
	}

	@Override
	public void setColumnAttribute(String string) throws StarDBException {
		boolean merge = false;
		if (!string.equals(view.getColumnAttribute())) {
			view.setColumnAttribute(string);
			view.recalculate();
			merge = true;
		}
		this.columns = view.getColumns();
		Collections.sort(this.columns, view.getColumnComparator(ITableView.TOTAL, false));
		this.columns = columns.subList(0, Math.min(max, view.getColumns().size()));
		if (this.columns.size() < view.getColumns().size() && !view.getColumns().contains(OTHERS)) {
			this.columns.add(ITableView.OTHERS);
			merge = true;
		}
		// finally we display it sorted by column names
		Collections.sort(this.columns);
		if (merge && this.totalsNotAvailable()) {
			this.calculateOtherValues();
		}
	}

	@Override
	public int getValueCount() {
		return view.getValueCount();
	}

	@Override
	public void switchDimensions() throws StarDBException {
	}

	@Override
	public void setFactAttribute(String factAttributeName) throws StarDBException {
		if (!factAttributeName.equals(view.getFactAttribute())) {
			view.setFactAttribute(factAttributeName);
			view.recalculate();
			this.calculateOtherValues();
		}
	}

	@Override
	public void setOperation(Operations operation) {
		view.setOperation(operation);

	}

	@Override
	public boolean isDimensionChanged() {
		return view.isDimensionChanged();
	}

	@Override
	public Operations getOperation() {
		return view.getOperation();
	}

	@Override
	public void addFilter(IFilter filter) {
		this.view.addFilter(filter);
	}

	@Override
	public List<FactRecord> getFacts() throws StarDBException {
		return view.getFacts();
	}

}
