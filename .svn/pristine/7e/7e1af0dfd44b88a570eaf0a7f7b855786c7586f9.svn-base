package ch.pschatzmann.starschema.engine.filter;

import java.util.Collection;

import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.dimension.IDimension;

/**
 * Filter value on dimension field
 * 
 * @author pschatzmann
 *
 */
public class FilterCriteria {
	private IDimension dimension;
	private String attribute;
	private Collection<String> filterValues;
	
	public FilterCriteria(IDimension dimension, String attribute,Collection<String> filterValues) {
		this.dimension = dimension;
		this.attribute = attribute;
		this.filterValues = filterValues;
	}

	public IDimension getDimension() {
		return dimension;
	}

	public String getAttribute() {
		return attribute;
	}

	public Collection<String> getFilterValues() {
		return filterValues;
	}
}
