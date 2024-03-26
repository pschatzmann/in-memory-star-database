package ch.pschatzmann.starschema.engine.filter;

import java.util.ArrayList;
import java.util.List;

import ch.pschatzmann.starschema.engine.FactRecord;

/**
 * Filter which is providing the intersection of to input filters
 * @author pschatzmann
 *
 */
public class AndFilter implements IFilter {
	private IFilter f1; 
	private IFilter f2;
	
	public AndFilter(IFilter f1, IFilter f2){
		this.f1 = f1;
		this.f2 = f2;
	}
	
	public List <FactRecord> getValues() {
		List <FactRecord> result = new ArrayList(f1.getValues());
		result.retainAll(f2.getValues());
		return result;
	}
}
