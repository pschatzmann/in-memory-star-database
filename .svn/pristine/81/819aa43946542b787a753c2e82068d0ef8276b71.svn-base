package ch.pschatzmann.starschema.views.calculation;

import java.util.Map;

import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.ITableView;

/**
 * Interface which is used to provide the calculation results in a map
 * 
 * @author philschatzmann
 *
 */
public interface ICalculator {
	Map<Map<String,String>,ICalculationVisitor> getResult(ITableView view) throws StarDBException;
	Map<String, String> getKeyMap(String colValue, String rowValue);
		
}
