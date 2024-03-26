package ch.pschatzmann.starschema.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import ch.pschatzmann.common.table.ITable;
import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.FactTable;
import ch.pschatzmann.starschema.engine.StarDatabase;

/**
 * Provides the data of the Fact Table as ITable
 * 
 * @author pschatzmann
 *
 */
public class FactITable implements ITable<Object> {
	private static transient Logger LOG = Logger.getLogger(FactITable.class);
	private static List EMPTY = Arrays.asList();
	private FactTable factTable;
	private List<FactRecord> facts;
	private List<String> fields = new ArrayList();
	private int arrayPos;

	/**
	 * Constructor
	 * 
	 * @param ft
	 * @param facts
	 */
	public FactITable(FactTable ft, List<FactRecord> facts) {
		this.facts = facts;
		this.factTable = ft;

		fields.addAll(this.factTable.getStarDatabase().getDimensionAttributes());
		arrayPos = fields.size();
		fields.addAll(factTable.getAttributes());

	}

	@Override
	public List<String> getRowFieldNames() {
		return EMPTY;
	}

	@Override
	public int getColumnCount() {
		return fields.size();
	}

	@Override
	public String getColumnTitle(int col) {
		return fields.get(col);
	}

	@Override
	public int getRowCount() {
		return this.facts == null ? 0 : this.facts.size();
	}

	@Override
	public List<String> getRowValue(int row) {
		return EMPTY;
	}

	@Override
	public Object getValue(int col, int row) {
		Object result = 0.0;
		try {
			FactRecord fr = this.facts.get(row);
			if (col < arrayPos) {
				String colName = fields.get(col);
				result = fr.getDimensionValue(colName);
			} else {
				int colAdjusted = col - arrayPos;
				String attribute = factTable.getAttributes().get(colAdjusted);
				result = fr.getValue(attribute);
			}
		} catch (Exception ex) {
			LOG.error(ex,ex);
			result = Double.NaN;
		}
		return result;
	}

	public String toString() {
		return this.factTable.getStarDatabase().toString();
	}

	public List<String> getColumns() {
		return fields;
	}

	@Override
	public ITable getBaseTable() {
		return this;
	}

}
