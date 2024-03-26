package ch.pschatzmann.starschema.engine.io.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Loads the data from a java collection
 * 
 * @author pschatzmann
 *
 */
public class CollectionReader implements IFieldSelectionSource {
	private static transient Logger LOG = Logger.getLogger(CollectionReader.class);
	private StarDatabase star;
	private ObjectAccess oa = new ObjectAccess(this);
	private List<String> excludedFields;
	private List<String> includedFields;
	private boolean isIncludedFieldsOnly = false;

	public CollectionReader(StarDatabase db) {
		this.star = db;
	}

	/**
	 * Loads the collection into the star database
	 * 
	 * @param c
	 * @param name
	 * @param fieldOptions
	 * @throws StarDBException
	 */
	public void read(Collection c, String name, String... fieldOptions) throws StarDBException {

		this.excludedFields = Arrays.asList(fieldOptions).stream().filter(field -> field.startsWith("-"))
				.map(field -> field.substring(1)).collect(Collectors.toList());

		this.includedFields = Arrays.asList(fieldOptions).stream().filter(field -> !field.startsWith("-"))
				.map(field -> field.substring(1).trim()).collect(Collectors.toList());

		isIncludedFieldsOnly = Arrays.asList(fieldOptions).stream().filter(field -> field.startsWith("+")).findFirst()
				.isPresent();

		if (!star.isDimensionsDefined() || !star.isFactAttributesDefined()) {
			setupDatabase(c);
		}
		for (Object obj : c) {
			Map record = createRecord(obj, name);
			star.addRecord(record);
		}
	}

	protected Map createRecord(Object obj, String name) {
		Map record = new HashMap();
		record.put("name", name);
		for (String fld : oa.getFieldNames(obj)) {
			record.put(fld, oa.getValue(fld, obj));
		}
		return record;
	}

	protected void setupDatabase(Collection c) throws StarDBException {
		LOG.info("The database structure was not defined. We set up a default structure!");
		Object first = c.iterator().next();
		List facts = new ArrayList();
		for (String fieldName : oa.getFieldNames(first)) {
			Class type = oa.getReturnType(fieldName, first);
			if (type != null) {
				if (type.isAssignableFrom(Number.class)) {
					facts.add(fieldName);
				} else {
					// if (type == String.class || type == Date.class) {
					star.addDimension(new Dimension(fieldName, Arrays.asList(fieldName)));
					// }
				}
			}
		}
		star.addDimension(new Dimension("name", Arrays.asList("name")));
		star.setFactAttributes(facts);
		LOG.info(star);
	}

	/**
	 * Returns the list of field names which need to be excluded from the output
	 * 
	 * @return
	 */
	public List<String> getExcludedFields() {
		return excludedFields;
	}

	/**
	 * List of fields which need to be included in the output
	 * 
	 * @return
	 */
	public List<String> getIncludedFields() {
		return includedFields;
	}

	/**
	 * If this is true we only consider the explicitly listed fields
	 * 
	 * @return
	 */
	public boolean isIncludedFieldsOnly() {
		return isIncludedFieldsOnly;
	}

}
