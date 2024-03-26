package ch.pschatzmann.starschema.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.engine.dimension.IDimensionRecord;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * A fact record stores the values of all fact attributes and the references to
 * the dimension records.
 * 
 * @author philschatzmann
 * 
 */
public class FactRecord {
	private static final Logger LOG = Logger.getLogger(FactRecord.class);
	private Map<IDimension, IDimensionRecord> dimensionInfo = new HashMap();
	private Map<String, Number> factAttributes = new TreeMap();
	private FactTable parentFactTable;

	/**
	 * Stores the fact record and a back link to fact table. Adds the fact
	 * record to all dimension records
	 * 
	 * @param fact
	 * @param record
	 * @param dimensions
	 * @throws StarDBException
	 */
	public FactRecord(FactTable fact, Map<String, Number> record, Collection<IDimensionRecord> dimensions) throws StarDBException {
		this.parentFactTable = fact;

		if (dimensions.size() == 0) {
			throw new StarDBException("The dimensions must not be empty");
		}
		
		// put fact attributes only into fact record map
		for (String factAttribute: this.parentFactTable.getAttributes()) {
			Object objValue = record.get(factAttribute);
			Double value = 0.0;
			if ((objValue instanceof Number)) {
				value = (Double) objValue;
			} else {
				String strValue = objValue.toString();
				if (!isEmpty(strValue)) {
					value = Double.valueOf(strValue);
				}
			}
			factAttributes.put(factAttribute, value);
		}

		for (IDimensionRecord dimensionRecord : dimensions) {
			if (dimensionRecord!=null) {
				dimensionRecord.addFact(this);
				this.dimensionInfo.put(dimensionRecord.getDimension(), dimensionRecord);
			}
		}
	}

	private boolean isEmpty(String strValue) {
		return strValue==null || strValue.isEmpty() || strValue.equals("-");
	}

	/**
	 * Determines the parameter value from the dimension table
	 * 
	 * @param attribute
	 * @return
	 * @throws StarDBException
	 */
	public String getDimensionValue(String attribute) throws StarDBException {
		IDimension dim = parentFactTable.getStarDatabase().getDimensionByAttribute(attribute);
		IDimensionRecord dr = this.dimensionInfo.get(dim);
		return dr.getData().get(attribute);
	}

	/**
	 * Determines the parameter value from the fact record.
	 * 
	 * @param totalField
	 * @return
	 */
	public Number getFactValue(String totalField) {
		return factAttributes.get(totalField);
	}

	/**
	 * Determines the dimension record of the indicated dimension
	 * 
	 * @param dim
	 * @return
	 */
	public IDimensionRecord getDimension(IDimension dim) {
		return dimensionInfo.get(dim);
	}

	/**
	 * Returns the fact table
	 * 
	 * @return
	 */
	public FactTable getFactTable() {
		return this.parentFactTable;
	}

	/**
	 * Returns the fact record
	 * 
	 * @return
	 */
	public Map<String, Number> getFactData() {
		return this.factAttributes;
	}

	/**
	 * Returns a record with both: the dimension attribute values and the fact
	 * attribute values.
	 * 
	 * @return
	 */
	public Map<String, Object> getRecord() {
		Map<String, Object> rec = new TreeMap();
		// add all dimension attribute values
		for (IDimension dim : this.getStarDatabase().getDimensions()) {
			rec.putAll(this.getDimension(dim).getData());
		}
		// add all fact values
		rec.putAll(this.getFactData());
		return rec;
	}

	/**
	 * Determins the value of the indicated parameter name. This method returns
	 * the content of both: the fact and dimension values.
	 * 
	 * @param attribute
	 * @return
	 */
	public Object getValue(String attribute)   {
		Object obj = this.getFactValue(attribute);
		if (obj == null) {
			try {
				obj = this.getDimensionValue(attribute);
			} catch (StarDBException e) {
				LOG.warn("Could not determine value for "+attribute);
			}
		}
		return obj;
	}

	/**
	 * Returns the actual star database.
	 * 
	 * @return
	 */
	public StarDatabase getStarDatabase() {
		return this.getFactTable().getStarDatabase();
	}

	@Override
	public String toString() {
		return "Dimensions: " + dimensionInfo + ", Facts: " + this.factAttributes;
	}
}
