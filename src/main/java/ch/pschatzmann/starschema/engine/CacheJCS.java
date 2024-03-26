package ch.pschatzmann.starschema.engine;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.log4j.Logger;

/**
 * Cache implementation which uses Apache JCS
 * 
 * @author philschatzmann
 * 
 */
public class CacheJCS implements ICache {
	private static final Logger LOG = Logger.getLogger(CacheJCS.class);
	private static JCS calculateCache = null;

	public CacheJCS() throws CacheException {
		calculateCache = JCS.getInstance("calculateCache");
	}

	@Override
	public Object get(Object key) {
		if (calculateCache == null) {
			return null;
		}
		return calculateCache.get(key);
	}
	
	
	@Override
	public void put(Object key, Object value)  {
		try {
			calculateCache.put(key, value);
		} catch (CacheException e) {
			LOG.warn("The entry could not be added to the cache:",e);
		}
	}

}
