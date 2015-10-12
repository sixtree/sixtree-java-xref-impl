package au.com.sixtree.xref;

import javax.xml.bind.JAXBException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import au.com.sixtree.xref.model.EntityNotFoundException;
import au.com.sixtree.xref.model.Relation;
import au.com.sixtree.xref.model.RelationFactory;
import au.com.sixtree.xref.model.RelationMarshaller;

public class JDBCXrefOperationTest extends TestCase {
	private EmbeddedDatabase db;
	private JDBCXrefOperation xrefOperation = new JDBCXrefOperation();

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
	public void testAddOrUpdateReference() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);
		relation = xrefOperation.addOrUpdateReference("3333", "siebel", relation.getCommonID(), "customer", "sixtree");
		System.out.println(RelationMarshaller.marshall(relation));
	}
	
	@Test
    public void testCreateRelation() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
        relation = xrefOperation.createRelation("customer", "sixtree", relation);
        assertNotNull(relation.getCommonID());
        assertEquals(1, relation.getReference().size());
        assertEquals("salesforce", relation.getReference().get(0).getEndpoint());
        assertEquals("1111", relation.getReference().get(0).getEndpointId());
        System.out.println(RelationMarshaller.marshall(relation));
    }
	
	@Test
	public void testDeleteReference() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation.getReference().add(RelationFactory.createRelationReference("sap", "2222"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);
		assertNotNull(relation.getCommonID());
		assertEquals(2, relation.getReference().size());
		assertEquals("salesforce", relation.getReference().get(0).getEndpoint());
		assertEquals("1111", relation.getReference().get(0).getEndpointId());
		relation = xrefOperation.deleteReference(relation.getCommonID(), "customer", "sixtree", "salesforce", "1111");
		assertNotNull(relation.getCommonID());
		assertEquals(1, relation.getReference().size());
		
		xrefOperation.findRelationByCommonId(relation.getCommonID(), "customer", "sixtree");
		assertNotNull(relation.getCommonID());
		assertEquals(1, relation.getReference().size());
		assertEquals("sap", relation.getReference().get(0).getEndpoint());
		assertEquals("2222", relation.getReference().get(0).getEndpointId());
		
		System.out.println(RelationMarshaller.marshall(relation));
	}
	
	@Test
	public void testFindRelation() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation.getReference().add(RelationFactory.createRelationReference("sap", "2222"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);
		
		relation = xrefOperation.findRelation("customer", "sixtree", "sap", "2222");
		assertNotNull(relation.getCommonID());
		assertEquals(2, relation.getReference().size());
		assertEquals("sap", relation.getReference().get(1).getEndpoint());
		assertEquals("2222", relation.getReference().get(1).getEndpointId());
		
		System.out.println(RelationMarshaller.marshall(relation));
	}
	
	@Test
	public void testFindRelationByCommonID() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation.getReference().add(RelationFactory.createRelationReference("sap", "2222"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);
		
		relation = xrefOperation.findRelationByCommonId(relation.getCommonID(), "customer", "sixtree");
		assertNotNull(relation.getCommonID());
		assertEquals(2, relation.getReference().size());
		assertEquals("salesforce", relation.getReference().get(0).getEndpoint());
		assertEquals("1111", relation.getReference().get(0).getEndpointId());
		assertEquals("sap", relation.getReference().get(1).getEndpoint());
		assertEquals("2222", relation.getReference().get(1).getEndpointId());
		
		System.out.println(RelationMarshaller.marshall(relation));
	}

	@Test
	public void testUpdateRelation() throws JAXBException, EntityNotFoundException {
		Relation relation = RelationFactory.createRelation();
		relation.getReference().add(RelationFactory.createRelationReference("salesforce", "1111"));
		relation = xrefOperation.createRelation("customer", "sixtree", relation);
		relation.getReference().get(0).setEndpointId("2222");
		relation = xrefOperation.updateRelation("customer", "sixtree", relation);
		assertNotNull(relation.getCommonID());
		assertEquals(1, relation.getReference().size());
		assertEquals("salesforce", relation.getReference().get(0).getEndpoint());
		assertEquals("2222", relation.getReference().get(0).getEndpointId());
		System.out.println(RelationMarshaller.marshall(relation));
	}
}
