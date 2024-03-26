package ch.pschatzmann.starschema.engine.io.collections;

import java.util.List;

public interface IFieldSelectionSource {
	/**
	 * Returns the list of field names which need to be excluded from the output
	 * 
	 * @return
	 */
	public List<String> getExcludedFields();
	/**
	 * List of fields which need to be included in the output
	 * 
	 * @return
	 */
	public List<String> getIncludedFields();

	/**
	 * If this is true we only consider the explicitly listed fields
	 * 
	 * @return
	 */
	public boolean isIncludedFieldsOnly();

}
