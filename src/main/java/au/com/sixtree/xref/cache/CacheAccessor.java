package au.com.sixtree.xref.cache;

import java.io.Serializable;

import javax.xml.bind.JAXBException;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;

import au.com.sixtree.xref.model.Relation;
import au.com.sixtree.xref.model.RelationMarshaller;

public class CacheAccessor {
	
	private CacheManager manager;
	private static final Logger log = Logger.getLogger(CacheAccessor.class);
	private static final String EHCACHECONFIGFILEPATH = "EHCACHECONFIGFILEPATH";
	private static final String EHCACHEDEFAULTCACHEKEY = "EHCACHEDEFAULTCACHEKEY";
	
	public CacheAccessor() {
		String defaultCacheKey = System.getProperty(EHCACHEDEFAULTCACHEKEY);
		if(defaultCacheKey != null) {
			warmUpCache(defaultCacheKey);
		}
	}
	
	private void warmUpCache(String cacheKey) {
		getOrAddCache(cacheKey);
	}

	public Relation getRelationByEndpoint(String tenant, String entitySet, String endpoint, String endpointId) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		Serializable endpointKey = createEndpointKey(endpoint, endpointId);
		try {
			Element element = cache.get(endpointKey);
			if(element != null) {
				log.trace("Cache hit for Endpoint: "+endpointKey);
				return RelationMarshaller.unmarshall((String)element.getObjectValue());
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		log.trace("Cache miss for Endpoint: "+endpointKey);
		return null;
	}

	public Relation getRelationByCommonId(String tenant, String entitySet, String commonId) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		try {
			Element element = cache.get(commonId);
			if(element != null) {
				log.trace("Cache hit for CommonId: "+commonId);
				return RelationMarshaller.unmarshall((String)element.getObjectValue());
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		log.trace("Cache miss for CommonId: "+commonId);
		return null;
	}
	
	public void putRelationByEndpoint(String tenant, String entitySet, String endpoint, String endpointId, Relation relation) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		try {
			cache.put(new Element(createEndpointKey(endpoint, endpointId), RelationMarshaller.marshall(relation)));
		} catch(Exception e) {
			log.error("Could not insert into cache: "+e.getMessage(), e);
		}
	}
	
	public void putRelationByCommonId(String tenant, String entitySet, String commonId, Relation relation) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		try {
			cache.put(new Element(commonId, RelationMarshaller.marshall(relation)));
		} catch(Exception e) {
			log.error("Could not insert into cache: "+e.getMessage(), e);
		}
	}	
	
	public void deleteRelationByEndpoint(String tenant, String entitySet, String endpoint, String endpointId) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		try {
			cache.remove(createEndpointKey(endpoint, endpointId));
		} catch(Exception e) {
			log.error("Could not delete from cache: "+e.getMessage(), e);
		}
	}
	
	public void deleteRelationByCommonId(String tenant, String entitySet, String commonId, Relation relation) {
		String cacheKey = createCacheKey(tenant, entitySet);
		Cache cache = getOrAddCache(cacheKey);
		try {
			cache.remove(commonId);
		} catch(Exception e) {
			log.error("Could not delete from cache: "+e.getMessage(), e);
		}
	}	

	private Cache getOrAddCache(String cacheKey) {
		if(manager == null) {
			String configPath = System.getenv(EHCACHECONFIGFILEPATH);
			if(configPath != null) {
				log.info("Instantiating EHCache from config file at path "+configPath);
				manager = CacheManager.create(configPath);
			} else {
				log.info("Instantiating EHCache using simple cache. No config file Environment variable was found. Use "+EHCACHECONFIGFILEPATH);
				manager = CacheManager.create();
			}
			System.setProperty(CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "true");
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
