package ch.pschatzmann.starschema.engine.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.dimension.IDimension;

/**
 * Filter based on a FilterCriteria which contains a list of criteria values.
 * We make sure that we use only one criteria value per uniqueDimension/uniqueField 
 * The sequence of the criteria values is used as priority.
 * 
 * @author pschatzmann
 *
 */
public class PriorityFilter implements IFilter {
	private FilterCriteria filter;
	private IDimension uniqueDimension;
	private String unniqueField;
	private Map<String,String> uniqueValueMap = new HashMap();
	
	public PriorityFilter(FilterCriteria filter, IDimension uniqueDimension, String unniqueField) {
		this.filter = filter;
		this.uniqueDimension = uniqueDimension;
		this.unniqueField = unniqueField;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IFilter#getValues()
	 */
	@Override
	public List<FactRecord> getValues() {
		List<FactRecord> result = new ArrayList();
		for (String value : filter.getFilterValues()) {
			Map<String, Object> search = new HashMap<String, Object>();
			search.put(filter.getAttribute(), value);
			for (FactRecord fact : filter.getDimension().select(search)){
				String uniqueValue = fact.getDimension(uniqueDimension).getData().get(unniqueField);
				String criteriaValue = value;
				if (isValid(uniqueValue,criteriaValue)) {				
					result.add(fact);
				}
			}
		}
		return result;
	}

	private boolean isValid(String uniqueValue, String criteriaValue) {
		Boolean result = null;
		String mapValue = uniqueValueMap.get(uniqueValue);
		if (mapValue==null) {
			result = true;
			uniqueValueMap.put(uniqueValue, criteriaValue);
		} else {
			result = mapValue.equals(criteriaValue);
		}
		return result;
	}
}
