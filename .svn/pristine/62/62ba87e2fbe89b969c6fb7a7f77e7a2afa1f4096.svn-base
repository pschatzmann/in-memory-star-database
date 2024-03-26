package ch.pschatzmann.starschema.views.calculation;

import java.util.Map;

public class TotalRecord {
	private Map<String,Object> keys;
	private ICalculationVisitor calculation;

	public TotalRecord(ICalculationVisitor calculation) {
		this.calculation = calculation;
		this.calculation.clear();
	}

	/**
	 * @return the keys
	 */
	public Map<String,Object> getKeys() {
		return keys;
	}

	/**
	 * @param keys
	 *            the keys to set
	 */
	public void setKeys(Map<String,Object> keys) {
		this.keys = keys;
	}

	/**
	 * @return the calculation
	 */
	public ICalculationVisitor getCalculation() {
		return calculation;
	}

	/**
	 * @param calculation
	 *            the calculation to set
	 */
	public void setCalculation(ICalculationVisitor calculation) {
		this.calculation = calculation;
	}

	@Override
	public String toString() {
		return keys + ":" + calculation;
	}

}
