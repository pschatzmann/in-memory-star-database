package ch.pschatzmann.starschema.engine.io;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.FactTable;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.engine.dimension.IDimensionRecord;
import ch.pschatzmann.starschema.errors.SerializationException;

/**
 * Serializer which saves a star database to XML
 * 
 * @author philschatzmann
 * 
 */
public class XMLWriter implements IWriter {
	private StarDatabase db;

	/**
	 * Constructor which associates the writer with the star database.
	 * 
	 * @param db
	 */
	public XMLWriter(StarDatabase db) {
		this.db = db;
	}

	/**
	 * Saves the data as XML. If the file size is a concearn you can use a
	 * GZIPOutputStream.
	 * 
	 * @param out
	 * @throws SerializationException
	 */

	@Override
	public void write(OutputStream out) throws SerializationException {
		try {
			XMLOutputFactory output = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = output.createXMLStreamWriter(out);
			writer.writeStartDocument();
			writer.writeStartElement("Db");
			writer.writeStartElement("Dimensions");

			for (IDimension dim : db.getDimensions()) {
				writeXML(dim, writer);
			}
			writer.writeEndElement();
			writer.writeCharacters("\n");
			writeFactTableXML(db.getFactTable(), writer);
			writer.writeEndElement();
			writer.writeCharacters("\n");
			writer.writeEndDocument();
			writer.flush();
			writer.close();
			out.close();
		} catch (Exception ex) {
			throw new SerializationException(ex);
		}
	}

	/**
	 * Serialize the dimension information into XML
	 * 
	 * @param writer
	 * @throws XMLStreamException
	 */
	protected void writeXML(IDimension dim, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("Dimension");
		writer.writeAttribute("name", dim.getName());
		writer.writeStartElement("Attributes");
		for (String name : dim.getAttributeNames()) {
			writer.writeStartElement("Attribute");
			writer.writeAttribute("id", name);
			writer.writeEndElement();
			writer.writeCharacters("\n");
		}

		writer.writeEndElement();
		writer.writeCharacters("\n");

		for (IDimensionRecord rec : dim.getDimensionRecords()) {
			writer.writeStartElement("Record");
			writer.writeAttribute("id", String.valueOf(rec.getId()));
			for (String name : dim.getAttributeNames()) {
				writer.writeAttribute(name, rec.getData().get(name));
			}
			writer.writeEndElement();
			writer.writeCharacters("\n");
		}
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}

	protected void writeFactTableXML(FactTable factTable, XMLStreamWriter writer) throws XMLStreamException {
		writer.writeStartElement("Facts");
		writer.writeCharacters("\n");
		writer.writeStartElement("FactAttributes");
		writer.writeCharacters("\n");
		if (factTable != null) {
			for (String name : factTable.getAttributes()) {
				writer.writeStartElement("FactAttribute");
				writer.writeAttribute("id", name);
				writer.writeEndElement();
				writer.writeCharacters("\n");
			}
		}
		writer.writeEndElement();
		writer.writeCharacters("\n");

		for (FactRecord fact : factTable.getFacts()) {
			writer.writeStartElement("Fact");
			// write dimension record ids
			for (IDimension dim : factTable.getStarDatabase().getDimensions()) {
				writer.writeAttribute(dim.getName(), String.valueOf(fact.getDimension(dim).getId()));
			}

			for (String attribute : factTable.getAttributes()) {
				Number value = fact.getFactValue(attribute);
				writer.writeAttribute(attribute, String.valueOf(value));
			}

			writer.writeEndElement();
			writer.writeCharacters("\n");
		}
		writer.writeEndElement();
		writer.writeCharacters("\n");
	}

}
