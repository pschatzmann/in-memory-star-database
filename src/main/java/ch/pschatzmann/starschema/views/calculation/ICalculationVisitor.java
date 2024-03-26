package ch.pschatzmann.starschema.views.calculation;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * We calculate totals by visiting all relevant facts
 * 
 * @author pschatzmann
 *
 */
public interface ICalculationVisitor {

	void calculate(FactRecord record) throws StarDBException;

	void end();

	void clear();

}
