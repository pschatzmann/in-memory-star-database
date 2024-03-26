package ch.pschatzmann.starschema.engine.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.Utils;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Loads the data from a CSV delimited input stream
 * 
 * @author pschatzmann
 *
 */
public class CSVReader implements IReader {
	private final static Logger LOG = Logger.getLogger(CSVReader.class);
	private StarDatabase db;
	private String delimiter=",";
	
	public CSVReader(StarDatabase db) {
		this.db = db;
	}

	public CSVReader(StarDatabase db, String delimiter) {
		this.db = db;
		this.delimiter = delimiter;
	}
	
	@Override
	public void read(InputStream is) throws IOException, StarDBException   {
	    if (db.getDimensions().isEmpty()) {
	    		throw new StarDBException("The database dimensions are not defined");
	    }
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    String line  = br.readLine();
	    List<String> header = Utils.csvToList(line, delimiter);
	    while ((line = br.readLine()) != null) {
		    List<String> record = Utils.csvToList(line, delimiter);
		    db.addRecord(getMap(header,record));
	    }
		
	}

	private Map<String, Object> getMap(List<String> header, List<String> record) {
		Map<String,Object> result = new HashMap();
		int min = Math.min(header.size(), record.size());
		for (int j =0;j<min;j++){
			result.put(header.get(j), record.get(j));
		}
		return result;
	}

}
