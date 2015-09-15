package au.com.sixtree.xref.cache;

import java.util.UUID;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;

import org.junit.Test;

import au.com.sixtree.xref.model.Relation;
import au.com.sixtree.xref.model.RelationFactory;
import au.com.sixtree.xref.model.RelationMarshaller;

public class CacheAccessorTest extends TestCase {

	@Test
	public void testRoundTrip() throws JAXBException {
		String tenant = "sixtree";
		String entitySet = "customers";
		String endpoint = "salesforce";
		String endpointId = "1111";
		
		Relation relation = RelationFactory.createRelation();
		relation.setCommonID(UUID.randomUUID().toString());
		relation.getReference().add(RelationFactory.createRelationReference(endpoint, endpointId));
		
		CacheAccessor cacheAccessor = new CacheAccessor();
		cacheAccessor.setManager(CacheManager.create());
		Relation cacheCheckA = cacheAccessor.getRelationByEndpoint(tenant, entitySet, endpoint, endpointId);
		assertNull(cacheCheckA);
		
		cacheAccessor.putRelationByEndpoint(tenant, entitySet, endpoint, endpointId, relation);
		Relation cacheCheckB = cacheAccessor.getRelationByEndpoint(tenant, entitySet, endpoint, endpointId);
		assertNotNull(cacheCheckB);
		assertEquals(endpoint, cacheCheckB.getReference().get(0).getEndpoint());
		
		System.out.println(RelationMarshaller.marshall(cacheCheckB));
	}

}
