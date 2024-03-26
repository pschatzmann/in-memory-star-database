package ch.pschatzmann.starschema.engine.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.pschatzmann.starschema.engine.FactRecord;

/**
 * Filter based on a collection of FilterCriteria
 * @author pschatzmann
 *
 */
public class Filter implements IFilter {
	private Collection<FilterCriteria> filters;
	
	public Filter(Collection<FilterCriteria> filters) {
		this.filters=filters;
	}
	
	/* (non-Javadoc)
	 * @see org.smartlib.starschema.engine.IFilter#getValues()
	 */
	@Override
	public List<FactRecord> getValues() {
		List<FactRecord> result = null;
		for (FilterCriteria filter : filters) {
			List<FactRecord> filterResult = new ArrayList<FactRecord>();
			for (String value : filter.getFilterValues()) {
				Map<String, Object> search = new HashMap<String, Object>();
				search.put(filter.getAttribute(), value);
				filterResult.addAll(filter.getDimension().select(search));
			}
			if (result == null) {
				result = filterResult;
			} else {
				result.retainAll(filterResult);
			}
		}
		return result;
	}
}
