package au.com.sixtree.xref.model;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBException;

import org.junit.Test;

public class RelationMarshallerTest {

	@Test
	public void testMarshal() throws JAXBException {
		Relation relation = RelationMarshaller.unmarshall(
		"<Relation>"+
	    "  <Reference>"+
	    "    <Endpoint>SAP</Endpoint>"+
	    "    <Id>111111</Id>"+
	    "  </Reference>"+
	    "</Relation>");
		
		assertNotNull(relation);
		assertEquals("111111", relation.getReference().get(0).getEndpointId());
	}
	
	@Test
	public void testUnmarshall() throws JAXBException {
		Relation relation = RelationFactory.createRelation();
		relation.setCommonID("1111");
		relation.getReference().add(RelationFactory.createRelationReference("SAP", "111111"));
		String xml = RelationMarshaller.marshall(relation);
		System.out.println(xml);
	}

}
