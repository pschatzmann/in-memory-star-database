package ch.pschatzmann.test;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.io.XMLReader;
import ch.pschatzmann.starschema.errors.SerializationException;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.calculation.TotalForOneFact;


public class TestStarReader {
	private static Logger LOG = Logger.getLogger(TestStarReader.class);
	private static StarDatabase star;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testReadXMLGZ() throws StarDBException, XMLStreamException, IOException, InterruptedException, SerializationException {
		star = new StarDatabase();
		InputStream is = new BufferedInputStream(new GZIPInputStream(getClass().getResourceAsStream("/test.xml.gz")));
		new XMLReader(star).read(is);
		is.close();
	}	
}
