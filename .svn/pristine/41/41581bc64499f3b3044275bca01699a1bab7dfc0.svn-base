package ch.pschatzmann.starschema.engine.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import ch.pschatzmann.starschema.engine.FactRecord;

/**
 * Filter which is providing the union of two input filters
 * 
 * @author pschatzmann
 *
 */
public class OrFilter  implements IFilter {
	private IFilter f1;
	private IFilter f2;

	public OrFilter(IFilter f1, IFilter f2) {
		this.f1 = f1;
		this.f2 = f2;
	}

	public List<FactRecord> getValues() {
		Collection<FactRecord> result = new HashSet(f1.getValues());
		result.addAll(f2.getValues());
		return new ArrayList(result);
	}
}
