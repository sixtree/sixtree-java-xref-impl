package au.com.sixtree.xref.model;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class RelationMarshaller {

	private static final JAXBContext jaxbContext;
	
	static {
		try {
			jaxbContext = JAXBContext.newInstance(Relation.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String marshall(Relation relation) throws JAXBException {
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		jaxbMarshaller.marshal(relation, baos);
		return new String(baos.toByteArray());
	}

	public static Relation unmarshall(String xml) throws JAXBException {
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Relation) jaxbUnmarshaller.unmarshal(new StringReader(xml));
	}
}
