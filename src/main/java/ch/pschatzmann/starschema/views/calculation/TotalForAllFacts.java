package ch.pschatzmann.starschema.views.calculation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Implementation of a calculator which is providing the sum, min, max, count
 * and average for all fact attributes.
 * 
 * @author philschatzmann
 * 
 */
public class TotalForAllFacts implements ICalculationVisitor, Serializable {
	private static final long serialVersionUID = 652628176863874183L;
	private Map<String, TotalForOneFact> totalMap = new HashMap<String, TotalForOneFact>();
	private StarDatabase db;
	/**
	 * Constructor which defines the total field and a field where we perform a
	 * count distinct.
	 * @param db
	 */
	public TotalForAllFacts(StarDatabase db) {
		this.db = db;
		for (String attribute : db.getFactTable().getAttributes()) {
			totalMap.put(attribute, new TotalForOneFact(attribute));
		}
	}

	@Override
	public void calculate(FactRecord record) throws StarDBException {
		for (String attribute : db.getFactTable().getAttributes()) {
			totalMap.get(attribute).calculate(record);
		}		
	}

	@Override
	public void clear() {
		for (String attribute : db.getFactTable().getAttributes()) {
			totalMap.get(attribute).clear();
		}				
	}

	@Override
	public void end() {
		for (String attribute : db.getFactTable().getAttributes()) {
			totalMap.get(attribute).end();
		}				
	}
	
	public TotalForOneFact getTotalFor(String factAttribute) {
		return totalMap.get(factAttribute);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (String attribute : db.getFactTable().getAttributes()) {
			sb.append(totalMap.get(attribute).toString());
			sb.append("; ");
		}				
		return sb.toString();
	}
	
}
