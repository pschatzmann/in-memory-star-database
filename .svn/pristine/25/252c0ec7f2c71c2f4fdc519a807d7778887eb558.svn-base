package ch.pschatzmann.starschema.views.output;

import java.util.Arrays;
import java.util.List;

import ch.pschatzmann.common.table.FormatException;
import ch.pschatzmann.common.table.ITable;
import ch.pschatzmann.common.table.ITableFormatter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.IBasicTable;
import ch.pschatzmann.starschema.views.ITableView;

public class Formatter {

	public String format(ITableView view, ITableFormatter fmt) throws FormatException {
		return fmt.format(new Table(view));
	}

	class Table implements ITable {
		IBasicTable basicTable = null;

		Table(IBasicTable t) {
			this.basicTable = t;
		}

		public List<String> getRowFieldNames() {
			try {
				return basicTable.getColumns();
			} catch (StarDBException e) {
				throw new RuntimeException(e);
			}
		}

		public int getColumnCount() {
			try {
				return basicTable.getColumns().size();
			} catch (StarDBException e) {
				throw new RuntimeException(e);
			}
		}

		public String getColumnTitle(int col) {
			try {
				return basicTable.getColumns().get(col);
			} catch (StarDBException e) {
				throw new RuntimeException(e);
			}
		}

		public int getRowCount() {
			try {
				return basicTable.getRows().size();
			} catch (StarDBException e) {
				throw new RuntimeException(e);
			}
		}

		public List<String> getRowValue(int row) {
			try {
				return Arrays.asList(basicTable.getRows().get(row));
			} catch (StarDBException e) {
				throw new RuntimeException(e);
			}
		}

		public Number getValue(int col, int row) {
			try {
				String colValue = basicTable.getColumns().get(col);
				String rowValue = basicTable.getRows().get(row);
				return this.basicTable.getValue(colValue, rowValue);
			} catch (StarDBException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public ITable getBaseTable() {
			return this;
		}
	}

}
