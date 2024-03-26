package ch.pschatzmann.starschema.views.calculation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Implementation of a calculator which is providing the sum, min, max, count
 * and average.
 * 
 * @author philschatzmann
 * 
 */
public class TotalCountDistinct implements ICalculationVisitor, Serializable {
	private static final long serialVersionUID = 652628176863874183L;
	private String countDistinctField;
	private transient Set<String> set = new HashSet<String>();
	private long count;

	/**
	 * Constructor which defines the total field and a field where we perform a
	 * count distinct for a single fact attribute.
	 * 
	 * @param distinctField
	 */
	public TotalCountDistinct(String distinctField) {
		this.countDistinctField = distinctField;
	}

	@Override
	public void calculate(FactRecord record) throws StarDBException {
		set.add(record.getDimensionValue(this.countDistinctField));
	}

	@Override
	public void end() {
		count = set.size();
	}

	public long getCount() {
		return this.count;
	}

	@Override
	public TotalCountDistinct clone() {
		TotalCountDistinct clone = new TotalCountDistinct(countDistinctField);
		clone.set = new HashSet<String>(set);
		return clone;
	}

	@Override
	public void clear() {
		set.clear();
	}

}
