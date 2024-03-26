package ch.pschatzmann.starschema.engine.io;

import ch.pschatzmann.starschema.engine.StarDatabase;

public class XMLSerialization implements ISerialization {
	private IReader reader;
	private IWriter writer;
	
	public XMLSerialization(StarDatabase db) {
		reader = new XMLReader(db);
		writer = new XMLWriter(db);
	}

	/**
	 * @return the reader
	 */
	@Override
	public IReader getReader() {
		return reader;
	}

	/**
	 * @return the writer
	 */
	@Override
	public IWriter getWriter() {
		return writer;
	}
}
