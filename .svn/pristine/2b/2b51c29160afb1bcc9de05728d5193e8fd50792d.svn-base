package ch.pschatzmann.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.pschatzmann.common.table.FormatException;
import ch.pschatzmann.common.table.TableFormatterCSV;
import ch.pschatzmann.common.table.TableFormatterHtml;
import ch.pschatzmann.common.table.TableFormatterJson;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.Utils;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.ITableView;
import ch.pschatzmann.starschema.views.TableView;
import ch.pschatzmann.starschema.views.TopNTableView;
import ch.pschatzmann.starschema.views.calculation.Operations;
import ch.pschatzmann.starschema.views.output.Formatter;

public class TestView {
	private static final Logger LOG = Logger.getLogger(TestView.class);
	private static StarDatabase star = new StarDatabase();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		star.addDimension(new Dimension( "Dim1", Arrays.asList("fld1")));
		star.addDimension(new Dimension( "Dim2", Arrays.asList("fld2")));
		star.addDimension(new Dimension( "Dim3", Arrays.asList("fld3")));
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
	public void testView() throws StarDBException {
		ITableView tv = star.createTableView("fld3", "fld1", "value", Operations.Sum);
		Object value = tv.getValue("z", "a");
		LOG.info(tv);
		Assert.assertEquals(1, tv.getColumns().size());
		Assert.assertEquals(1, tv.getRows().size());
		Assert.assertEquals(10.0, Utils.todouble(value), 0.1);
	}

	@Test
	public void testView1() throws StarDBException {
		ITableView tv = new TableView(star, "fld3", "fld1", "value", Operations.Sum);
		Object value = tv.getValue("z", "a");
		Assert.assertEquals(1, tv.getColumns().size());
		Assert.assertEquals(1, tv.getRows().size());
		Assert.assertEquals(10.0, Utils.todouble(value), 0.1);
	}

	@Test
	public void testViewMultiple() throws StarDBException {
		ITableView tv = new TableView(star, "fld2", "fld1", "value", Operations.Sum);
		Object value = tv.getValue("a1", "a");
		Assert.assertEquals(10, tv.getColumns().size());
		Assert.assertEquals(1, tv.getRows().size());
		Assert.assertEquals(1.0, Utils.todouble(value), 0.1);
	}
	
	@Test
	public void testCSV() throws StarDBException, FormatException {
		ITableView tv = new TableView(star, "fld1", "fld2", "value", Operations.Sum);
		System.out.println(new Formatter().format(tv, new TableFormatterCSV()));
	}
	
	@Test
	public void testHtml() throws StarDBException, FormatException {
		ITableView tv = new TableView(star, "fld1", "fld2", "value", Operations.Sum);
		System.out.println(new Formatter().format(tv, new TableFormatterHtml()));
	}

	@Test
	public void testJson() throws StarDBException, FormatException {
		ITableView tv = new TableView(star, "fld1", "fld2", "value", Operations.Sum);
		System.out.println(new Formatter().format(tv, new TableFormatterJson()));
	}

	@Test
	public void testViewTopN() throws StarDBException {
		TableView i = new TableView(star, "fld1", "fld2", "value", Operations.Sum);
		ITableView tv = new TopNTableView(i, 5);
		Assert.assertEquals(1, tv.getColumns().size());
		Assert.assertEquals(5 + 1, tv.getRows().size());

		double total = 0;
		for (String col : tv.getColumns()) {
			for (String row : tv.getRows()) {
				Number num = tv.getValue(col, row);
				LOG.info(row + "/" + col + "->" + num);
				total += Utils.todouble(num);
			}
		}
		Assert.assertEquals(10.0, total, 0.1);

	}

	@Test
	public void testViewLarge() throws StarDBException {
		StarDatabase starBig = new StarDatabase();
		starBig.addDimension(new Dimension( "Dim1", Arrays.asList("fld1")));
		starBig.addDimension(new Dimension("Dim2", Arrays.asList("fld2")));
		starBig.addDimension(new Dimension( "Dim3", Arrays.asList("fld3")));
		starBig.setFactAttributes(Arrays.asList("value"));

		Map<String, Object> record = new HashMap();
		record.put("fld1", "a");
		record.put("fld2", "a");
		record.put("fld3", "z");
		record.put("value", 1.0);
		for (int j = 0; j < 1000000; j++) {
			starBig.addRecord(record);
		}
		LOG.info("data loaded");

		TableView tv = new TableView(starBig, "fld1", "fld3", "value", Operations.Sum);
		Assert.assertEquals(1, tv.getColumns().size());
		Assert.assertEquals(1, tv.getRows().size());
		Object value = tv.getValue("a", "z");
		LOG.info("data calculated!");

		Assert.assertEquals(1000000.0, Utils.todouble(value), 0.1);

	}

}
