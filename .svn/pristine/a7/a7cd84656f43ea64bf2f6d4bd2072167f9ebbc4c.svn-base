package ch.pschatzmann.starschema.engine.dimension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.ICache;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.Utils;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * A dimension has a name and a set of defined dimension attributes. The
 * dimension data records all existing combinations of attribute values. The
 * dimension records are stored in memory!
 * 
 * @author philschatzmann
 * 
 */
public class Dimension implements IDimension {
	private static final Logger LOG = Logger.getLogger(Dimension.class);
	private String name;
	private Map<Map<String, Object>, IDimensionRecord> dimensionMap = new ConcurrentHashMap<Map<String, Object>, IDimensionRecord>();
	private Collection<String> attributeNames = new ArrayList<String>();
	private Set<String> calculatedAttributeNames = new HashSet<String>();
	private Collection<ICalculatedAttributes> calculatedAttributes = new ArrayList();
	private int nextId = 0;
	private StarDatabase star;

	/**
	 * Constructor which records the dimension name and the corresponding field
	 * names.
	 * 
	 * @param name
	 * @param attributeNames
	 * @throws StarDBException
	 */
	public Dimension(String name, Collection<String> attributeNames) throws StarDBException {
		this.name = name;
		// we store the collection. Please note that the import changes the
		// attributes after calling this constructor!
		this.attributeNames.addAll(attributeNames);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IDimension#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IDimension#getDimensionRecords()
	 */
	@Override
	public Collection<IDimensionRecord> getDimensionRecords() {
		return dimensionMap.values(); // data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IDimension#getAttributeNames()
	 */
	@Override
	public Collection<String> getAttributeNames() {
		return attributeNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IDimension#getRecord(java.util.Map)
	 */
	@Override
	public IDimensionRecord getRecord(Map<String, Object> rec) {
		Map<String, Object> search = this.getDimensionSearchRecord(rec);
		IDimensionRecord dim = dimensionMap.get(search);
		return dim;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IDimension#addRecord(java.util.Map,
	 * org.smartlib.starschema.engine.DimensionRecord)
	 */
	@Override
	public void addRecord(Map<String, Object> recordMap, IDimensionRecord dimRecord) {
		dimensionMap.put(recordMap, dimRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartlib.starschema.engine.IDimension#getDimensionSearchRecord(java.
	 * util.Map)
	 */
	@Override
	public Map<String, Object> getDimensionSearchRecord(Map<String, Object> selectMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		for (Entry<String, Object> entry : selectMap.entrySet()) {
			if (getAttributeNames().contains(entry.getKey())) {
				result.put(entry.getKey(), Utils.toString(entry.getValue()));
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.smartlib.starschema.engine.IDimension#select(java.util.Map)
	 */
	@Override
	public Collection<FactRecord> select(Map<String, Object> search) {
		Collection<FactRecord> result = new ArrayList<FactRecord>();
		for (IDimensionRecord rec : this.getDimensionRecords()) {
			if (rec.matches(search)) {
				result.addAll(rec.getFacts());
			}
		}
		return result;
	}

	@Override
	public boolean exists(Map<String, Object> search) {
		// if all attributes have been provided, we use the map
		Map<String, Object> normalized = this.getDimensionSearchRecord(search);
		if (normalized.size() == attributeNames.size()) {
			return dimensionMap.get(search) != null;
		} else {
			// search sequentially
			Collection<FactRecord> result = new ArrayList<FactRecord>();
			for (IDimensionRecord rec : this.getDimensionRecords()) {
				if (rec.matches(search)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Dimension:" + this.name + ", Keys:" + attributeNames + " Records: " + dimensionMap.size();
	}

	/**
	 * Provides the next available number. These ids are used as references when
	 * the data is serialized (e.g. into XML).
	 * 
	 * @return
	 */
	public int getNextId() {
		return ++this.nextId;
	}

	/**
	 * Clears the dimension data
	 */
	@Override
	public void clear() {
		dimensionMap.clear();
		nextId = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.smartlib.starschema.engine.IDimension#getDimensionValues(java.lang.
	 * String)
	 */
	@Override
	public Collection<String> getDimensionValues(String attributeName) {
		Collection<String> dimensionValues = getCachedList(attributeName);
		if (dimensionValues == null) {
			dimensionValues = new HashSet<String>();
			for (IDimensionRecord rec : getDimensionRecords()) {
				dimensionValues.add(rec.getData().get(attributeName).trim());
			}
			if (this.star!=null && this.star.getCache() != null) {
				this.star.getCache().put(attributeName, dimensionValues);
			}
		}
		return dimensionValues;
	}

	private Collection<String> getCachedList(String attributeName) {
		ICache c = this.star == null ? null : this.star.getCache();
		return c == null ? null : (Collection<String>) c.get(attributeName);
	}

	public void addCalculatedAttributes(ICalculatedAttributes attributes) {
		this.calculatedAttributes.add(attributes);
		for (ICalculatedAttribute att : attributes.getCalculatedAttributes()) {
			this.attributeNames.add(att.getName());
			calculatedAttributeNames.add(att.getName());
		}
	}

	@Override
	public Collection<ICalculatedAttributes> getCalculatedAttributes() {
		return this.calculatedAttributes;
	}

	@Override
	public boolean isCalculated(String fieldName) {
		return calculatedAttributeNames.contains(fieldName);
	}

	public void setStarDatabase(StarDatabase starDatabase) {
		this.star = starDatabase;
	}
	
	/**
	 * Adds a new attribute
	 */
	public void addAttribute(String name,  Function<Map<String,String>,String> f) {
		attributeNames.add(name);
		for (IDimensionRecord rec : this.getDimensionRecords()) {
			Map<String,String> data = rec.getData();
			data.put(name, f.apply(data));
		}
	}

}
