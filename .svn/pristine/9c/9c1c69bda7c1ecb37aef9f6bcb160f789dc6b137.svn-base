package ch.pschatzmann.starschema.views.calculation;

import java.util.HashMap;
import java.util.Map;

/**
 * Merges total records to a grand total 
 * @author philschatzmann
 *
 */
public class CalculationResultMerger {
	private Map<Map<String,String>, ICalculationVisitor> calculationResultMap = new HashMap(10000);

	public CalculationResultMerger(Map<Map<String,String>, ICalculationVisitor> calculationResult) {
		this.calculationResultMap = calculationResult;
	}

	/**
	 * Merges the result record with the final total result
	 * 
	 * @param key
	 * @param value
	 */
	public void mergeResult(Map key,TotalForOneFact value ) {
		// LOG.info("mergeResultEntry");
		TotalForOneFact total = (TotalForOneFact) calculationResultMap.get(key);
		if (total == null) {
			calculationResultMap.put(key, value.clone());
		} else {
			TotalForOneFact toAdd = value;
			total.setTotal(total.getTotal() + toAdd.getTotal());
			total.setCount(total.getCount() + toAdd.getCount());
			total.setCountWithValue(total.getCountWithValue() + toAdd.getCountWithValue());
			total.setMin(Math.min(total.getMin(), toAdd.getMin()));
			total.setMax(Math.max(total.getMin(), toAdd.getMin()));
		}
	}
}
