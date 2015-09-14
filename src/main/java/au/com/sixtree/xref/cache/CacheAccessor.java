package au.com.sixtree.xref.cache;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import au.com.sixtree.xref.model.Relation;

public class CacheAccessor {
	
	private CacheManager manager;
	private String xrefCacheName = "xref";
	
	public Relation getRelationByEndpoint(String endpoint, String endpointId) {
		if(!manager.cacheExists(xrefCacheName)) {
			manager.addCache(xrefCacheName);
		}
		Cache cache = manager.getCache(xrefCacheName);
		Element element = cache.get(createEndpointKey(endpoint, endpointId));
		if(element != null) {
			return (Relation)element.getObjectValue();
		}
		return null;
	}
	
	public void putRelationByEndpoint(String endpoint, String endpointId, Relation relation) {
		Cache cache = manager.getCache(xrefCacheName);
		cache.put(new Element(createEndpointKey(endpoint, endpointId), relation));
	}

	private Serializable createEndpointKey(String endpoint, String endpointId) {
		return endpoint + ":" + endpointId;
	}

	public String getXrefCacheName() {
		return xrefCacheName;
	}

	public void setXrefCacheName(String xrefCacheName) {
		this.xrefCacheName = xrefCacheName;
	}

	public CacheManager getManager() {
		return manager;
	}

	public void setManager(CacheManager manager) {
		this.manager = manager;
	}
	
	
}
