package ch.pschatzmann.starschema.views.calculation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import ch.pschatzmann.starschema.engine.FactRecord;
import ch.pschatzmann.starschema.engine.StarDatabase;
import ch.pschatzmann.starschema.errors.StarDBException;
import ch.pschatzmann.starschema.views.ITableView;

/**
 * We split up the fact table into n buckets and process these in parallel. In
 * order to avoid the need for any synchronization we do this for each thread
 * separately.
 * 
 * In the end we merge the result.
 * 
 * @author philschatzmann
 * 
 */

public class CalculatorWithThreads implements ICalculator {
	private int BATCH_SIZE = 100000;
	private final static Logger LOG = Logger.getLogger(CalculatorWithThreads.class);
	private ITableView view;
	private Map<Map<String,String>, ICalculationVisitor> calculationResultMap = new HashMap(5000);
	private Calculator keyMapCalculator;
	private CalculationResultMerger merger;

	/**
	 * Constructor to associate the view with the calculator
	 */
	public CalculatorWithThreads() {
		this.merger = new CalculationResultMerger(calculationResultMap);
	}

	/**
	 * Returns a map wiht the total records.
	 */
	@Override
	public Map<Map<String,String>, ICalculationVisitor> getResult(ITableView view) throws StarDBException {
		try {
			this.keyMapCalculator = new Calculator(view);
			this.view = view;
			List<Callable<Map>> callables = new ArrayList<Callable<Map>>();
			StarDatabase star = view.getStarDB();
			int index = 0;
			Collection<FactRecord> facts = getFacts(index);

			ExecutorService executorService = Executors.newFixedThreadPool(50);
			List<Future<Map>> resultList = new ArrayList();
			while (!facts.isEmpty()) {
				index++;
				resultList.add(executorService.submit(new CalculationCallable(facts)));
				facts = getFacts(index);
			}

			mergeResultList(resultList);
			executorService.shutdown();

			return calculationResultMap;
		} catch (Exception e) {
			throw new StarDBException(e);
		}
	}

	/**
	 * Merges the list of results to the final totals
	 * 
	 * @param result
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void mergeResultList(List<Future<Map>> result) throws InterruptedException, ExecutionException {
		LOG.info("mergeResultList with entries " + result.size());
		for (Future<Map> future : result) {
			Map<Map, TotalForOneFact> resultForThread = future.get();
			LOG.info("-Procssing Map with entries " + resultForThread.size());
			for (Entry entry : resultForThread.entrySet()) {
				mergeResultEntry(entry);
			}
		}
	}

	/**
	 * Merges the result record with the final total result
	 * 
	 * @param entry
	 */
	private void mergeResultEntry(Entry<Map, TotalForOneFact> entry) {
		merger.mergeResult(entry.getKey(), entry.getValue());
	}

	/**
	 * Provides a map with the key field names and values which can be used as
	 * key for looking up the summarization results.
	 */
	@Override
	public Map<String, String> getKeyMap(String colValue, String rowValue) {
		return keyMapCalculator.getKeyMap(colValue, rowValue);
	}

	/**
	 * Callable with provides the result for a subset of the fact table
	 * 
	 * @author philschatzmann
	 * 
	 * @param <Map>
	 */
	protected class CalculationCallable<Map> implements Callable {
		private Calculator calculator;
		private Collection<FactRecord> facts;;

		CalculationCallable(Collection<FactRecord> facts) {
			this.facts = facts;
			calculator = new Calculator(view);
		}

		@Override
		public Map call() throws Exception {
			return (Map) calculator.calculateTotals(facts);
		}
	}

	/**
	 * Returns fixed number of records. If there is no data we return an empty
	 * collection.
	 * 
	 * @param index
	 * @return
	 * @throws StarDBException 
	 */
	public synchronized List<FactRecord> getFacts(int index) throws StarDBException {
		List<FactRecord> facts = this.view.getFacts();
		if (index * BATCH_SIZE > facts.size()) {
			return new ArrayList();
		}
		return facts.subList(index * BATCH_SIZE, Math.min((index + 1) * BATCH_SIZE, facts.size()));
	}

}
