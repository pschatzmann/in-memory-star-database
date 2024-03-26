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
public class TotalForOneFact implements ICalculationVisitor, Serializable {
	private static final long serialVersionUID = 652628176863874183L;
	private Double total = 0.0;
	private Double min = null;
	private Double max = null;
	private Long count = 0l;
	private Long countWithValue = 0l;
	private int hostCount = 0;
	private String totalField;
	private String countDistinctField;
	private transient Set<String> set = new HashSet<String>();

	/**
	 * Constructor which defines the total field and a field where we perform a
	 * count distinct for a single fact attribute.
	 * 
	 * @param totalField
	 * @param distinctField
	 */
	public TotalForOneFact(String totalField, String distinctField) {
		this.totalField = totalField;
		this.countDistinctField = distinctField;
	}

	public TotalForOneFact(String totalField) {
		this.totalField = totalField;
	}

	@Override
	public void calculate(FactRecord record) throws StarDBException {
		this.count++;
		double value = record.getFactValue(totalField).doubleValue();
		this.total += value;
		if (min == null || value < min) {
			min = value;
		}
		if (max == null || value > max) {
			max = value;
		}
		if (countDistinctField != null) {
			set.add(record.getDimensionValue(this.countDistinctField));
		}
		if (value!=0.0) {
			this.countWithValue++;
		}
	}

	public double getAvg() {
		return total / count;
	}

	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}

	/**
	 * @return the min
	 */
	public Double getMin() {
		return min;
	}

	/**
	 * @return the max
	 */
	public Double getMax() {
		return max;
	}

	/**
	 * @return the count
	 */
	public Long getCount() {
		return count;
	}

	/**
	 * @return the count
	 */
	public int getHostCount() {
		return hostCount;
	}

	@Override
	public void end() {
		hostCount = set.size();
	}

	@Override
	public String toString() {
		return "Count:" + count + ", Total:" + total;
	}
	
	@Override
	public TotalForOneFact clone() {
		TotalForOneFact clone = new TotalForOneFact(totalField,countDistinctField);
		clone.total = total;
		clone.min = min;
		clone.max = max;
		clone.count = count;
		clone.hostCount = hostCount;
		clone.set = new HashSet<String>(set);
		return clone;
	} 

	@Override
	public void clear() {
		total = 0.0;
		min = null;
		max = null;
		count = 0l;
		hostCount = 0;
		set.clear();
	}

	protected void setTotal(double d) {
		this.total =d;		
	}

	protected void setCount(long l) {
		this.count = l;
	}

	protected void setMin(double min2) {
		this.min = min2;		
	}

	protected void setMax(double max2) {
		this.max = max2;		
	}

	/**
	 * @return the countWithValue
	 */
	public Long getCountWithValue() {
		return countWithValue;
	}

	/**
	 * @param countWithValue the countWithValue to set
	 */
	protected void setCountWithValue(Long countWithValue) {
		this.countWithValue = countWithValue;
	}

	public TotalForOneFact negative() {
		TotalForOneFact clone = new TotalForOneFact(totalField,countDistinctField);
		clone.total = -total;
		clone.min = min;
		clone.max = max;
		clone.count = -count;
		clone.hostCount = -hostCount;
		clone.set = new HashSet<String>(set);

		return clone;
	}
}
