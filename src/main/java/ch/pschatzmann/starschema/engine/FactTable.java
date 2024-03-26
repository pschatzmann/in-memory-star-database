package ch.pschatzmann.starschema.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.pschatzmann.common.table.ITable;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.engine.dimension.IDimensionRecord;
import ch.pschatzmann.starschema.engine.filter.IFilter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.FactITable;
import ch.pschatzmann.starschema.views.calculation.ICalculationVisitor;

/**
 * Container which holds all facts
 * 
 * @author pschatzmann
 *
 */

public class FactTable implements IStarDatabaseProvider {
	private List<FactRecord> facts = Collections.synchronizedList(new ArrayList<FactRecord>(500000));
	private List<String> attributes = new ArrayList<String>();
	private StarDatabase database;

	protected FactTable(StarDatabase sd, List<String> attributes) {
		this.database = sd;
		this.attributes = attributes;
	}

	protected void add(Map<String, Number> data, Collection<IDimensionRecord> dimensions) throws StarDBException {
		FactRecord rec = new FactRecord(this, data, dimensions);
		add(rec);
	}

	public void add(FactRecord rec) {
		if (rec != null)
			facts.add(rec);
	}

	public List<String> getAttributes() {
		return attributes;
	}

	@Override
	public StarDatabase getStarDatabase() {
		return this.database;
	}

	/**
	 * Returns all facts
	 * @return
	 */
	public List<FactRecord> getFacts() {
		return this.facts;
	}
	
	/**
	 * Returns the facts which are matching with the filter criteria
	 * @param filter
	 * @return
	 */
	public List<FactRecord> getFacts(IFilter filter) {
		List<FactRecord> result = filter !=null ? filter.getValues():this.getFacts();
		return result;
	}

	public FactRecord getFactRecord(int index) {
		return this.facts.get(index);
	}

	public void clear() {
		facts.clear();
	}

	@Override
	public String toString() {
		return "Facts: " + facts;
	}

	public int size() {
		return this.facts.size();
	}

	/**
	 * Returns true if there are no facts
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return this.facts.isEmpty();
	}

	/**
	 * Returns the facts which are matching with the indicated search record. We
	 * consider only dimension values!
	 */
	public List<FactRecord> select(Map<String, Object> searchRecord) {
		List<FactRecord> result = null;
		boolean anyCriteria = false;
		if (searchRecord != null && !searchRecord.isEmpty()) {
			for (IDimension dim : database.getDimensions()) {
				Map<String, Object> search = dim.getDimensionSearchRecord(searchRecord);
				if (!search.isEmpty()) {
					anyCriteria = true;
					Collection<FactRecord> dimResult = dim.select(search);
					if (result == null) {
						result = new ArrayList<FactRecord>(dimResult);
					} else {
						result.retainAll(dimResult);
					}
				}
			}
		}
		// we return all facts if there is no restriction
		if (!anyCriteria) {
			result = this.getFacts();
		}
		return result;
	}

	/**
	 * Calculate using the visitor based on the fields provided in the search
	 * record
	 * 
	 * @param searchRecord
	 * @param visitor
	 * @throws StarDBException
	 */
	public ICalculationVisitor calculate(Map<String, Object> searchRecord, ICalculationVisitor visitor) throws StarDBException {
		visitor.clear();
		for (FactRecord fact : select(searchRecord)) {
			visitor.calculate(fact);
		}
		visitor.end();
		return visitor;
	}
	
	/**
	 * Provides the data as ITalbe
	 * @return
	 */
	public ITable toTable() {
		return new FactITable(this, facts);
	}

	public ITable toTable(List<FactRecord> records) {
		return new FactITable(this, records);
	}


}
