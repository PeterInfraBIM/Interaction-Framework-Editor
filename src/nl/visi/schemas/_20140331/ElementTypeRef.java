//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.01.13 at 02:17:24 PM CET 
//


package nl.visi.schemas._20140331;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for elementTypeRef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="elementTypeRef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="idref" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "elementTypeRef")
@XmlSeeAlso({
    ElementConditionTypeRef.class,
    GroupTypeTypeRef.class,
    AppendixTypeTypeRef.class,
    TransactionPhaseTypeTypeRef.class,
    TransactionTypeTypeRef.class,
    MessageTypeTypeRef.class,
    MessageInTransactionTypeTypeRef.class,
    RoleTypeTypeRef.class,
    PersonTypeTypeRef.class,
    UserDefinedTypeTypeRef.class,
    MessageInTransactionTypeConditionTypeRef.class,
    ProjectTypeTypeRef.class,
    ComplexElementTypeTypeRef.class,
    OrganisationTypeTypeRef.class,
    SimpleElementTypeTypeRef.class
})
public class ElementTypeRef {

    @XmlAttribute(name = "idref", required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object idref;

    /**
     * Gets the value of the idref property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getIdref() {
        return idref;
    }

    /**
     * Sets the value of the idref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setIdref(Object value) {
        this.idref = value;
    }

}
