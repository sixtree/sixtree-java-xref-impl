package au.com.sixtree.xref;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import au.com.sixtree.xref.cache.CacheAccessor;
import au.com.sixtree.xref.model.EntityNotFoundException;
import au.com.sixtree.xref.model.Relation;
import au.com.sixtree.xref.model.RelationFactory;
import au.com.sixtree.xref.model.RelationMarshaller;

public class JDBCXrefOperationCacheTest extends TestCase {
	private EmbeddedDatabase db;
	private JDBCXrefOperation xrefOperation = new JDBCXrefOperation();
	private static final Logger log = Logger.getLogger(JDBCXrefOperationCacheTest.class);

	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("h2-ddl.sql").build();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(db);
		xrefOperation.setJdbcTemplate(jdbcTemplate);
	}

	@After
	public void tearDown() {
		db.shutdown();
	}
	
	@Test
	public void testFindRelationByEndpoint() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation.getReference().add(RelationFactory.createRelationReference("sap", "2222"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);

		//Cache miss
		long start = System.currentTimeMillis();
		relation = xrefOperation.findRelation("customer", "sixtree", "sap", "2222");
		log.info("Cache miss find took "+(System.currentTimeMillis() - start)+"ms");
		
		int numCalls = 1000;
		start = System.currentTimeMillis();
		for (int i = 0; i < numCalls; i++) {
			relation = xrefOperation.findRelation("customer", "sixtree", "sap", "2222");
		}
		log.info(numCalls+" calls hitting cache find took in total "+(System.currentTimeMillis() - start)+"ms");
	}
	
	@Test
	public void testFindRelationByCommonId() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation.getReference().add(RelationFactory.createRelationReference("sap", "2222"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);
		
		//Cache miss
		long start = System.currentTimeMillis();
		relation = xrefOperation.findRelationByCommonId(relation.getCommonID(), "customer", "sixtree");
		log.info("Cache miss find took "+(System.currentTimeMillis() - start)+"ms");
		
		int numCalls = 1000;
		start = System.currentTimeMillis();
		for (int i = 0; i < numCalls; i++) {
			relation = xrefOperation.findRelationByCommonId(relation.getCommonID(), "customer", "sixtree");
		}
		log.info(numCalls+" calls hitting cache find took in total "+(System.currentTimeMillis() - start)+"ms");
	}
}
