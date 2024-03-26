package ch.pschatzmann.starschema.engine;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

/**
 * If we create a large number of records in a short period of time we add the
 * values into a queue and push the data into the database from the queue via
 * multiple threads.
 * 
 * @author pschatzmann
 *
 */

public class FactDataPump {
	private final static Logger LOG = Logger.getLogger(FactDataPump.class);
	private boolean done = false;
	private StarDatabase db;
	private BlockingQueue<Map<String,Object>> queue = new ArrayBlockingQueue(500000);

	/** Inner class representing the Consumer side */
	class Consumer implements Runnable {
		private BlockingQueue<Map<String,Object>> queue;

		Consumer(BlockingQueue<Map<String,Object>> theQueue) {
			this.queue = theQueue;
		}

		@Override
		public void run() {
			try {
				while (true) {
					Map<String,Object> rec = queue.take();
					if (LOG.isDebugEnabled()) {
						int len = queue.size();
						LOG.debug("Queue size now " + len);
					}
					db.addRecord(rec);
					if (done) {
						return;
					}
				}
			} catch (Exception ex) {
				LOG.error("Error while adding fact to the database ", ex);
			}
		}
	}

	public FactDataPump(StarDatabase db, int numberOfConsumers) {
		this.db = db;
		for (int i = 0; i < numberOfConsumers; i++) {
			Thread t = new Thread(new Consumer(this.queue));
			t.setName("FactDataPump-" + i);
			t.start();
		}
	}

	public void addRecord(Map<String,Object> newObject) throws InterruptedException {
		queue.put(newObject);
	}

	public void addRecords(Collection<Map<String,Object>> newObjects) throws InterruptedException {
		queue.addAll(newObjects);
	}

	public void waitUntilQueueIsEmpty() throws InterruptedException {
		while (!this.queue.isEmpty()) {
			Thread.sleep(500);
		}
		done = true;
	}

}
