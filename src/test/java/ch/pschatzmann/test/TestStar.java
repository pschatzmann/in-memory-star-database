package ch.pschatzmann.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.engine.io.XMLWriter;
import ch.pschatzmann.starschema.errors.SerializationException;
import ch.pschatzmann.starschema.errors.StarDBException;


public class TestStar {
	private static Logger LOG = Logger.getLogger(TestStar.class);
	static StarDatabase star = new StarDatabase();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		star.addDimension(new Dimension("Dim1", Arrays.asList("fld1")));
		star.addDimension(new Dimension("Dim2", Arrays.asList("fld2")));
		star.addDimension(new Dimension("Dim3", Arrays.asList("fld3")));
		star.setFactAttributes(Arrays.asList("value"));
		for (int j = 0; j < 10; j++) {
			Map<String, Object> record = new HashMap();
			record.put("fld1", "a");
			record.put("fld2", "a" + j);
			record.put("fld3", "z");
			record.put("value", (double)j);
			star.addRecord(record);
		}

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
	public void listDimensions() {
		for (IDimension dim : star.getDimensions()) {
			LOG.info("Dimension '" + dim.getName() + "': " + dim.getDimensionRecords().size());
			Assert.assertFalse(dim.getAttributeNames().isEmpty());
		}
		Assert.assertFalse(star.getDimensions().isEmpty());
	}
	
	@Test
	public void addAttribute() {
		star.getDimension("Dim2").addAttribute("fld22", map -> map.get("fld2"));
		Assert.assertFalse(star.getDimensions().isEmpty());
	}


	@Test
	public void testWriteXML() throws StarDBException,  SerializationException, IOException {
		OutputStream os = new BufferedOutputStream(new FileOutputStream("target/test.xml"));
		new XMLWriter(star).write(os);
		os.close();
	}

	@Test
	public void testWriteXMLGZ() throws StarDBException, XMLStreamException, IOException, SerializationException {
		OutputStream os = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream("target/test.xml.gz")));
		new XMLWriter(star).write(os);
		os.close();
	}


}
