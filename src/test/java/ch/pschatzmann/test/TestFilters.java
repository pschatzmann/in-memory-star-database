package ch.pschatzmann.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.engine.Utils;
import ch.pschatzmann.starschema.engine.dimension.Dimension;
import ch.pschatzmann.starschema.engine.filter.Filter;
import ch.pschatzmann.starschema.engine.filter.FilterCriteria;
import ch.pschatzmann.starschema.engine.filter.PriorityFilter;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.ITableView;
import ch.pschatzmann.starschema.views.TableView;
import ch.pschatzmann.starschema.views.calculation.Operations;

public class TestFilters {
	@Test
	public void testFilter() throws StarDBException {
		StarDatabase star = new StarDatabase();
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
		ITableView tv = new TableView(star, "fld1", "fld2", "value", Operations.Sum);
		tv.addFilter(new Filter(Arrays.asList(new FilterCriteria(star.getDimension("Dim2"),"fld2",Arrays.asList("a2")))));
		//new ViewCSVRenderer(tv).write(System.out);

		Object value = tv.getValue("a", "a2");
		Assert.assertEquals(1, tv.getColumns().size());
		Assert.assertEquals(1, tv.getRows().size());
		Assert.assertEquals(2.0, Utils.todouble(value), 0.1);
	}
	
	@Test
	public void testPriorityFilter() throws StarDBException {
		StarDatabase star = new StarDatabase();
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
		ITableView tv = new TableView(star, "fld1", "fld2", "value", Operations.Sum);
		tv.addFilter(new PriorityFilter(new FilterCriteria(star.getDimension("Dim2"),"fld2",Arrays.asList("a2","a1")),star.getDimension("Dim3"),"fld3"));
		//new ViewCSVRenderer(tv).write(System.out);

		Object value = tv.getValue("a", "a2");
		Assert.assertEquals(1, tv.getColumns().size());
		Assert.assertEquals(1, tv.getRows().size());
		Assert.assertEquals(2.0, Utils.todouble(value), 0.1);
	}
}

