package ch.pschatzmann.starschema.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.jcs.access.exception.CacheException;
import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.dimension.DimensionRecord;
import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.engine.dimension.IDimensionRecord;
import ch.pschatzmann.starschema.engine.io.collections.CollectionReader;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.ITableView;
import ch.pschatzmann.starschema.views.TableView;
import ch.pschatzmann.starschema.views.calculation.Operations;

/**
 * In memory implementation of the star schema.Before loading the data you need
 * to define the dimension (attributes) and the fact attributes.
 * 
 * The data records need to be represented as key-value maps - where the key is
 * representing the attribute name.
 * 
 * @author philschatzmann
 * 
 */
public class StarDatabase implements IStarDatabaseProvider {
	private static transient Logger LOG = Logger.getLogger(StarDatabase.class);
	private Collection<IDimension> dimensions = new ArrayList();
	private Map<String, IDimension> dimensionMap = new HashMap();
	private FactTable factTable = null;
	private transient ICache cache = null;
	private Boolean multiThreadsActive = true;
	private long recordGetCountNew = 0;
	private long recordGetCountFound = 0;

	/**
	 * Default constructor
	 * 
	 */
	public StarDatabase() {
	}

	/**
	 * Constructor whihc loads the dimension information from the indicated
	 * properties
	 * 
	 * @param p
	 * @throws StarDBException
	 */
	public StarDatabase(Properties p) throws StarDBException {
		this();
		this.loadConfiguration(p);
	}

	/**
	 * Defines the attributes for a star dimension
	 * 
	 * @param dimensions
	 */
	protected void setDimensions(Collection<IDimension> dimensions) {
		this.dimensions = dimensions;
		for (IDimension dimension : dimensions) {
			dimension.setStarDatabase(this);
			dimensionMap.put(dimension.getName(), dimension);
		}
	}

	/**
	 * Returns the dimension with the indicated name.
	 * 
	 * @param name
	 * @return
	 */
	public IDimension getDimension(String name) {
		// if the map is out of synch we need to reload it
		if (this.dimensionMap.size() != this.dimensions.size()) {
			setDimensions(this.dimensions);
		}
		return dimensionMap.get(name);
	}

	/**
	 * Checks if the indicated dimension is valid
	 * 
	 * @param name
	 * @return
	 */
	public boolean isDimensionValid(String name) {
		return getDimension(name) != null;
	}

	/**
	 * Checks if the star dimension have been defined
	 * 
	 * @return
	 */
	public boolean isDimensionsDefined() {
		return this.dimensions != null;
	}

	/**
	 * Defines the attributes which are made available in the fact table
	 * 
	 * @param fieldNames
	 */
	public void setFactAttributes(List<String> fieldNames) {
		factTable = new FactTable(this, fieldNames);
	}

	/**
	 * Verifies if the fact attributes have been defined
	 * 
	 * @return
	 */
	public boolean isFactAttributesDefined() {
		return factTable != null;
	}

	/**
	 * Adds a record into the star database. We add new dimension records if
	 * necessary and add the record to the fact table.
	 * 
	 * @param data
	 * @throws StarDBException
	 */
	public void addRecord(Map<String, Object> data) throws StarDBException {
		if (this.dimensionMap.size() != this.dimensions.size()) {
			setDimensions(this.dimensions);
		}
		if (dimensions == null) {
			throw new StarDBException("The dimensions are not defined");
		}
		if (factTable == null) {
			throw new StarDBException("The fact attributes are not defined");
		}

		// determine dimensions
		Collection<IDimensionRecord> dim = new ArrayList();
		for (IDimension dimension : dimensions) {
			dim.add(getAndAddRecord(dimension, data));
		}

		// add the data and the dimensions to the
		factTable.add((Map) data, dim);
	}

	protected IDimensionRecord getAndAddRecord(IDimension dimension, Map<String, Object> rec) {
		Map<String, Object> search = dimension.getDimensionSearchRecord(rec);
		IDimensionRecord dim = null;
		synchronized (dimensionMap) {
			dim = dimension.getRecord(search);
			if (dim == null) {
				dim = new DimensionRecord(dimension, rec);
				dimension.addRecord(search, dim);
				++recordGetCountNew;
			} else {
				++recordGetCountFound;
			}
		}

		// log statistics
		if ((recordGetCountNew + recordGetCountFound) % 10000 == 0) {
			LOG.debug("Dimension '" + dimension.getName() + "': New Dimension records " + recordGetCountNew
					+ " vs access to stored records " + recordGetCountFound);
		}
		return dim;
	}

	/**
	 * Loads the data from a java collection
	 * 
	 * @param c
	 * @param name
	 * @throws StarDBException
	 */
	public void addRecords(Collection c, String name, String...fieldOptions) throws StarDBException {
		new CollectionReader(this).read(c, name, fieldOptions);
	}

	/**
	 * Returns all star dimensions
	 * 
	 * @return
	 */
	public Collection<IDimension> getDimensions() {
		return this.dimensions;
	}

	public IDimension addDimension(IDimension dimension) throws StarDBException {
		if (this.getDimension(dimension.getName()) == null) {
			this.dimensions.add(dimension);
			dimensionMap.put(dimension.getName(), dimension);
			dimension.setStarDatabase(this);
		}
		return dimension;
	}

	/**
	 * Returns the distinct content values of the indicated dimension attributes
	 * 
	 * @param attributeName
	 * @return
	 * @throws StarDBException
	 */
	public Collection<String> getDimensionValues(String attributeName) throws StarDBException {
		LOG.info("getDimensionValues " + attributeName);
		if (attributeName == null) {
			LOG.warn("The attribute is not defined so we just return an empty result");
			return new ArrayList();
		}
		IDimension dim = getDimensionByAttribute(attributeName);
		return dim.getDimensionValues(attributeName);
	}

	/**
	 * Checks if the information in the fact record is matching with the entries in
	 * the indicated key value map
	 * 
	 * @param rec
	 * @param selectMap
	 * @return
	 */
	private boolean isRecordValid(FactRecord rec, Map<String, Object> selectMap) {
		// check record in all dimensions
		for (IDimension dim : dimensions) {
			Map<String, Object> search = dim.getDimensionSearchRecord(selectMap);
			IDimensionRecord dimRec = rec.getDimension(dim);
			if (!dimRec.matches(search)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines the Dimension to which the indicated attribute belongs
	 * 
	 * @param attributeName
	 * @return
	 * @throws StarDBException
	 */
	public IDimension getDimensionByAttribute(String attributeName) throws StarDBException {
		for (IDimension dim : getDimensions()) {
			for (String name : dim.getAttributeNames()) {
				if (name.equals(attributeName)) {
					return dim;
				}
			}
		}
		throw new StarDBException("The attribute '" + attributeName + "' does not exist");
	}

	/**
	 * Defines the cache which should be used if available
	 * 
	 * @param cache
	 */
	public void setCache(ICache cache) {
		this.cache = cache;
	}

	public ICache getCache() {
		return this.cache;
	}

	/**
	 * Clears the dimension and fact tables
	 */
	public void clear() {
		for (IDimension dimension : this.getDimensions()) {
			dimension.clear();
		}
		factTable.clear();
	}

	/**
	 * Returns the fact table
	 * 
	 * @return
	 */
	public FactTable getFactTable() {
		return factTable;
	}

	/**
	 * Returns a reference to itself
	 */
	@Override
	public StarDatabase getStarDatabase() {
		return this;
	}

	/**
	 * @return the multiThreadsActive
	 */
	public boolean isMultiThreadsActive() {
		return multiThreadsActive;
	}

	/**
	 * @param multiThreadsActive
	 *            the multiThreadsActive to set
	 */
	public void setMultiThreadsActive(boolean multiThreadsActive) {
		this.multiThreadsActive = multiThreadsActive;
	}

	/**
	 * Determines all dimension attributes
	 * 
	 * @return
	 */
	public List<String> getDimensionAttributes() {
		List<String> result = new ArrayList();
		for (IDimension dim : getDimensions()) {
			for (String name : dim.getAttributeNames()) {
				result.add(name);
			}
		}
		return result;
	}

	/**
	 * Creates a new TwoDimensionTableView result object. If the cache has been set
	 * up we get it from the cache.
	 * 
	 * @param columnAttribute
	 * @param rowAttribute
	 * @param factAttribute
	 * @param operation
	 * @return
	 * @throws StarDBException
	 */
	public ITableView createTableView(String columnAttribute, String rowAttribute, String factAttribute,
			Operations operation) throws StarDBException {
		ITableView result = null;
		if (this.cache != null) {
			result = (TableView) this.cache.get(getKey(rowAttribute, columnAttribute, factAttribute));
		}

		if (result == null) {
			result = new TableView(this, columnAttribute, rowAttribute, factAttribute, operation);
		}
		return result;
	}

	/**
	 * Convert the row and column to a string that can be used as key
	 * 
	 * @param rowAttribute
	 * @param columnAttribute
	 * @param factAttribute
	 * @return
	 */
	protected String getKey(String rowAttribute, String columnAttribute, String factAttribute) {
		StringBuffer sb = new StringBuffer(rowAttribute);
		sb.append(";");
		sb.append(columnAttribute);
		sb.append(";");
		sb.append(factAttribute);
		return sb.toString();
	}

	/**
	 * Adds an entry to the cache
	 * 
	 * @param view
	 */
	public void putCache(TableView view) {
		if (this.cache != null) {
			this.cache.put(getKey(view.getRowAttribute(), view.getColumnAttribute(), view.getFactAttribute()), view);
		}
	}

	/**
	 * Loads the configuration from the indicated properties
	 * 
	 * @param p
	 * @throws StarDBException
	 */

	public void loadConfiguration(Properties p) throws StarDBException {
		String dimStr = p.getProperty("dimensions");
		multiThreadsActive = Boolean.valueOf(p.getProperty("multiThreadsActive", multiThreadsActive.toString()));
		if (dimStr != null) {
			for (String dimension : Utils.csvToList(dimStr, ",")) {
				this.addDimension(new Dimension(dimension, Utils.csvToList(p.getProperty(dimension), ",")));
			}
		}
	}

	/**
	 * Returns a list of the dimension names
	 * 
	 * @return
	 */
	public List<String> getDimensionNames() {
		return this.getDimensions().stream().map(d -> d.getName()).collect(Collectors.toList());
	}

	/**
	 * Saves the current configuration into the indicated properties
	 * 
	 * @param p
	 */
	public void saveConfiguration(Properties p) {
		p.put("dimensions", Utils.toCSV(getDimensionNames(), ", "));
		p.put("multiThreadsActive", multiThreadsActive);
		for (IDimension d : this.getDimensions()) {
			p.put(d.getName(), Utils.toCSV(d.getAttributeNames(), ", "));
		}
	}

	/**
	 * Prints basic information aboout
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Dimension: ");
		sb.append(this.getDimensions());
		if (this.getFactTable() != null) {
			sb.append(" / Facts: ");
			sb.append(this.getFactTable().getAttributes());
			sb.append(" / Records: ");
			sb.append(this.getFactTable().size());
		} else {
			sb.append(" / No facts");
		}

		return sb.toString();
	}

}
