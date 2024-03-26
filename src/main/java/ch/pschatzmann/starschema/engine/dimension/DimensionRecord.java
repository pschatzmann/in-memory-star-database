package ch.pschatzmann.starschema.engine.dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.Utils;

/**
 * A single dimension record which contains the values for the attributes which
 * are defined for the dimension. Related to each record we store a collection
 * which is pointing to the corresponding fact records.
 * 
 * @author philschatzmann
 *
 */

public class DimensionRecord implements IDimensionRecord {
	private final static Logger LOG = Logger.getLogger(DimensionRecord.class);
	private IDimension dim;
	private Map<String, String> dimensionData = new HashMap<String, String>();
	private Collection<FactRecord> facts = Collections.synchronizedList(new ArrayList<FactRecord>());
	private int hashCode = 0;
	private int id;

	/**
	 * Constructor
	 * 
	 * @param dim
	 * @param data
	 */
	public DimensionRecord(IDimension dim, Map<String, Object> data) {
		this.dim = dim;
		this.id = dim.getNextId();
		for (String attribute : dim.getAttributeNames()) {
			if (!dim.isCalculated(attribute)) {
				Object value = data.get(attribute);
				if (value == null) {
					value = "";
					LOG.warn("The attribute had no value: " + attribute + " for " + data);
				}
				this.dimensionData.put(attribute, Utils.toString(value));
				hashCode ^= value.hashCode();
			}
		}

		for (ICalculatedAttributes attributesCalc : this.dim.getCalculatedAttributes()) {
			for (ICalculatedAttribute attributeCalc : attributesCalc.getCalculatedAttributes()) {
				this.dimensionData.put(attributeCalc.getName(), attributeCalc.getValue(data));
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.dimension.IDimensionRecord#getData()
	 */
	@Override
	public Map<String, String> getData() {
		return dimensionData;
	}

	/**
	 * Defines the data
	 * 
	 * @param data
	 *            the data to set
	 */
	protected void setData(Map<String, String> data) {
		this.dimensionData = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.dimension.IDimensionRecord#getFacts()
	 */
	@Override
	public Collection<FactRecord> getFacts() {
		return facts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartlib.starschema.engine.dimension.IDimensionRecord#hasValue(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public boolean hasValue(String keyName, String value) {
		String actualValue = dimensionData.get(keyName);
		return value.equals(actualValue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartlib.starschema.engine.dimension.IDimensionRecord#getDimension()
	 */
	@Override
	public IDimension getDimension() {
		return dim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.dimension.IDimensionRecord#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.hashCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartlib.starschema.engine.dimension.IDimensionRecord#equals(java.
	 * lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DimensionRecord)) {
			return false;
		}
		DimensionRecord rec = (DimensionRecord) obj;
		if (hashCode() != rec.hashCode) {
			return false;
		}

		for (String attribute : dim.getAttributeNames()) {
			if (!dim.isCalculated(attribute) && !dimensionData.get(attribute).equals(rec.getData().get(attribute))) {
				return false;
			}
		}
		return true;

	}

	@Override
	public void addFact(FactRecord factRecord) {
		facts.add(factRecord);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartlib.starschema.engine.dimension.IDimensionRecord#matches(java.
	 * util.Map)
	 */
	@Override
	public boolean matches(Map<String, Object> search) {
		for (Entry<?, ?> entry : search.entrySet()) {
			if (!entry.getValue().equals(this.getData().get(entry.getKey()))) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.dimension.IDimensionRecord#getId()
	 */
	@Override
	public int getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.dimension.IDimensionRecord#toString()
	 */
	@Override
	public String toString() {
		return "Dimension Record: " + this.getDimension().getName();
	}
}
