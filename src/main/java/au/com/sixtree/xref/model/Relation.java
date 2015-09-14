//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.09.14 at 03:40:52 PM NZST 
//


package au.com.sixtree.xref.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CommonID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Reference" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Endpoint" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "commonID",
    "reference"
})
@XmlRootElement(name = "Relation")
public class Relation {

	@XmlTransient
	private Integer id;
	
    @XmlElement(name = "CommonID", required = true)
    protected String commonID;
    @XmlElement(name = "Reference")
    protected List<Relation.Reference> reference;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
     * Gets the value of the commonID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommonID() {
        return commonID;
    }

    /**
     * Sets the value of the commonID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommonID(String value) {
        this.commonID = value;
    }

    /**
     * Gets the value of the reference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Relation.Reference }
     * 
     * 
     */
    public List<Relation.Reference> getReference() {
        if (reference == null) {
            reference = new ArrayList<Relation.Reference>();
        }
        return this.reference;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Endpoint" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "endpoint",
        "endpointId"
    })
    public static class Reference {

    	@XmlTransient
    	private Integer id;
    	
        @XmlElement(name = "Endpoint", required = true)
        protected String endpoint;
        @XmlElement(name = "Id")
        protected String endpointId;

        
        public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		/**
         * Gets the value of the endpoint property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEndpoint() {
            return endpoint;
        }

        /**
         * Sets the value of the endpoint property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEndpoint(String value) {
            this.endpoint = value;
        }

        /**
         * Gets the value of the id property.
         * 
         */
        public String getEndpointId() {
            return endpointId;
        }

        /**
         * Sets the value of the id property.
         * 
         */
        public void setEndpointId(String value) {
            this.endpointId = value;
        }

    }

}
