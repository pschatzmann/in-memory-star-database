package ch.pschatzmann.starschema.engine.io;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.dimension.DimensionRecord;
import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.engine.dimension.IDimensionRecord;
import ch.pschatzmann.starschema.errors.SerializationException;
import ch.pschatzmann.starschema.errors.StarDBException;

/**
 * Load database from xml input stream
 * 
 * @author philschatzmann
 * 
 */
public class XMLReader implements IReader {
	private final static Logger LOG = Logger.getLogger(XMLReader.class);
	private StarDatabase db;

	/**
	 * Constructor which associates the reader with the star db.
	 * 
	 * @param db
	 */
	public XMLReader(StarDatabase db) {
		this.db = db;
	}

	/**
	 * Reads in a database from XML
	 * 
	 * @param is
	 * @throws SerializationException
	 */
	public void read(InputStream is) throws SerializationException {
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader streamReader = factory.createXMLStreamReader(is);

			Map<String, DimensionRecord> dimensionMap = new HashMap<String, DimensionRecord>();
			String dimensionName = null;
			Collection<String> attributes = null;
			Collection<IDimension> dimensions = null;
			List<String> factAttributes = null;
			Dimension dimension = null;

			while (streamReader.hasNext()) {
				int eventType = streamReader.next();

				if (eventType == XMLStreamConstants.START_ELEMENT) {
					String tagName = streamReader.getLocalName();
					if (tagName.equals("Db")) {
						dimensions = db.getDimensions();
					} else if (tagName.equals("Dimension")) {
						LOG.info("Importing Dimension");
						attributes = new ArrayList<String>();
						dimensionName = streamReader.getAttributeValue("", "name");
						dimension = new Dimension(dimensionName, attributes);
						dimensions.add(dimension);
					} else if (tagName.equals("Attribute")) {
						LOG.info("Importing Attribute");
						String attributeName = streamReader.getAttributeValue("", "id");
						attributes.add(attributeName);
					} else if (tagName.equals("Record")) {
						processReadXMLRecord(streamReader, dimension, dimensionMap, dimensionName);
					} else if (tagName.equals("FactAttributes")) {
						LOG.info("Importing FactAttributes");
						factAttributes = new ArrayList<String>();
						if (!db.isFactAttributesDefined()) {
							db.setFactAttributes(factAttributes);
						}
					} else if (tagName.equals("FactAttribute")) {
						factAttributes.add(streamReader.getAttributeValue("", "id"));
					} else if (tagName.equals("Fact")) {
						processReadXMLFact(streamReader, dimensionMap);
					}
				}
			}
		} catch (Exception ex) {
			throw new SerializationException(ex);
		}

	}

	protected void processReadXMLRecord(XMLStreamReader streamReader, Dimension dimension,
			Map<String, DimensionRecord> dimensionMap, String dimensionName) {
		String id = null;
		Map<String, Object> dimensionRecordMap = new HashMap<String, Object>();
		dimensionRecordMap = new HashMap<String, Object>();
		for (int j = 0; j < streamReader.getAttributeCount(); j++) {
			String name = streamReader.getAttributeLocalName(j);
			String value = streamReader.getAttributeValue(j);
			if (name.equals("id")) {
				id = value;
			} else {
				dimensionRecordMap.put(name, value);
			}
		}
		DimensionRecord dimensionRecord = new DimensionRecord(dimension, dimensionRecordMap);
		dimension.addRecord(dimensionRecordMap, dimensionRecord);
		dimensionMap.put(dimensionName + id, dimensionRecord);
	}

	protected void processReadXMLFact(XMLStreamReader streamReader, Map<String, DimensionRecord> dimensionMap)
			throws StarDBException {
		Collection<IDimensionRecord> dimensionRecords = new ArrayList<IDimensionRecord>();
		Map<String, Number> factMap = new HashMap<String, Number>();
		for (int j = 0; j < streamReader.getAttributeCount(); j++) {
			String name = streamReader.getAttributeLocalName(j);
			String value = streamReader.getAttributeValue(j);
			if (db.isDimensionValid(name)) {
				DimensionRecord dimRec = dimensionMap.get(name + value);
				dimensionRecords.add(dimRec);
			} else {
				factMap.put(name, Double.valueOf(value));
			}
		}
		FactRecord factRecord = new FactRecord(db.getFactTable(), factMap, dimensionRecords);
		db.getFactTable().add(factRecord);
	}
}
