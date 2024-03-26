package ch.pschatzmann.starschema.views;

import java.util.List;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.filter.IFilter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.calculation.Operations;
import ch.pschatzmann.starschema.views.calculation.TotalForOneFact;

/**
 * Standarizes the values
 * 
 * @author pschatzmann
 *
 */

public class StandardizedValuesView implements ITableView {
	private ITableView view;
	private TotalForOneFact total;

	public StandardizedValuesView(ITableView view) throws StarDBException {
		this.view = view;
		TotalForOneFact total = new TotalForOneFact(view.getFactAttribute());
		view.getStarDB().getFactTable().calculate(null, total);
	}

	@Override
	public List<String> getRows() throws StarDBException {
		return view.getRows();
	}

	@Override
	public List<String> getColumns() throws StarDBException {
		return view.getColumns();
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
	public Number getValue(String col, String row) throws StarDBException {
		return ((Double) getValue(col, row) - total.getMin()) / (total.getMax() - total.getMin());
	}

	@Override
	public StarDatabase getStarDB() {
		return view.getStarDB();
	}

	@Override
	public String getFactAttribute() {
		return view.getFactAttribute();
	}

	@Override
	public void setRowAttribute(String string) throws StarDBException {
		view.setRowAttribute(string);
	}

	@Override
	public void setColumnAttribute(String string) throws StarDBException {
		view.setColumnAttribute(string);
	}

	@Override
	public int getValueCount() {
		return view.getValueCount();
	}

	@Override
	public void switchDimensions() throws StarDBException {
		view.switchDimensions();
	}

	@Override
	public void setFactAttribute(String string) throws StarDBException {
		view.setFactAttribute(string);

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
		view.addFilter(filter);
	}

	@Override
	public List<FactRecord> getFacts() throws StarDBException {
		return view.getFacts();
	}

}
