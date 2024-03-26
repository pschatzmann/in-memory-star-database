package ch.pschatzmann.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.dimension.IDimension;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.calculation.TotalForOneFact;

public class TestCalculation {
	private static Logger LOG = Logger.getLogger(TestStar.class);
	private static StarDatabase star = new StarDatabase();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		star.addDimension(new Dimension("Dim1", new ArrayList(Arrays.asList("fld1"))));
		star.addDimension(new Dimension("Dim2", new ArrayList(Arrays.asList("fld2"))));
		star.addDimension(new Dimension("Dim3", new ArrayList(Arrays.asList("fld3"))));
		star.setFactAttributes(Arrays.asList("value"));

		for (int j = 0; j < 10; j++) {
			Map<String, Object> record = new HashMap();
			record.put("fld1", "a");
			record.put("fld2", "a" + j);
			record.put("fld3", "z");
			record.put("value", 1.0);
			star.addRecord(record);
		}
	}

	@Test
	public void testCalculateAllRecords() throws StarDBException {
		TotalForOneFact total = new  TotalForOneFact("value");
		star.getFactTable().calculate(null, total);
		Assert.assertEquals(10.0, total.getCount(),0.001);
		Assert.assertEquals(10.0, total.getTotal(),0.001);
		Assert.assertEquals(1.0, total.getMin(),0.001);
		Assert.assertEquals(1.0, total.getMax(),0.001);
		Assert.assertEquals(1.0, total.getAvg(),0.001);
	
	}
	
	@Test
	public void testCalculate1Record() throws StarDBException {
		TotalForOneFact total = new  TotalForOneFact("value");
		Map<String,Object> search = new HashMap();
		search.put("fld2", "a2");
		star.getFactTable().calculate(search, total);
		Assert.assertEquals(1.0, total.getCount(),0.001);
		Assert.assertEquals(1.0, total.getTotal(),0.001);
		Assert.assertEquals(1.0, total.getMin(),0.001);
		Assert.assertEquals(1.0, total.getMax(),0.001);
		Assert.assertEquals(1.0, total.getAvg(),0.001);	
	}
	
	
}
