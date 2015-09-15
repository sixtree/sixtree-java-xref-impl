package au.com.sixtree.xref.cache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import au.com.sixtree.xref.model.Relation;

public class CacheAccessor {
	
	private CacheManager manager;
	private static final Logger log = Logger.getLogger(CacheAccessor.class);
	
	public Relation getRelationByEndpoint(String tenant, String entitySet, String endpoint, String endpointId) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		Serializable endpointKey = createEndpointKey(endpoint, endpointId);
		Element element = cache.get(endpointKey);
		if(element != null) {
			log.trace("Cache hit for Endpoint: "+endpointKey);
			return (Relation)element.getObjectValue();
		}
		log.trace("Cache miss for Endpoint: "+endpointKey);
		return null;
	}
	public void putRelationByEndpoint(String tenant, String entitySet, String endpoint, String endpointId, Relation relation) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		cache.put(new Element(createEndpointKey(endpoint, endpointId), relation));
	}

	private Cache getOrAddCache(String cacheKey) {
		if(manager == null) {
			manager = CacheManager.create();
		}
		if(!manager.cacheExists(cacheKey)) {
			manager.addCache(cacheKey);
		}
		return manager.getCache(cacheKey);
	}
	
	private String createCacheKey(String tenant, String cacheName) {
		return tenant + ":" + cacheName;
	}
	
	private Serializable createEndpointKey(String endpoint, String endpointId) {
		return endpoint + ":" + endpointId;
	}

	public CacheManager getManager() {
		return manager;
	}

	public void setManager(CacheManager manager) {
		this.manager = manager;
	}
	
	
}
