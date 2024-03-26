package ch.pschatzmann.starschema.views;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Table model backed by star database. We need to define the horizontal and
 * vertical attributes and the operation of the fact attribute which should be
 * displayed in the cells.
 * 
 * @author philschatzmann
 * 
 */
public class StarTableModel implements TableModel {
	private ITableView view;

	public StarTableModel(ITableView view) throws StarDBException {
		this.view = view;
	}

	@Override
	public int getRowCount() {
		try {
			return view.getRows().size();
		} catch (StarDBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getColumnCount() {
		try {
			return view.getColumns().size();
		} catch (StarDBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		try {
			return view.getColumns().get(columnIndex);
		} catch (StarDBException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return (columnIndex == 0) ? String.class : Number.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		try {
			if (columnIndex == 0) {
				return view.getRows().get(rowIndex);
			}			
			return view.getValue(view.getColumns().get(columnIndex), view.getRows().get(rowIndex));
		} catch (Exception e) {
			return "Error: " + e;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}

}
