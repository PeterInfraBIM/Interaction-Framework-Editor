//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.12.03 at 01:18:23 PM CET 
//


package nl.visi_1_1a.interaction_schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
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
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}AppendixType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}ComplexElementType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}ElementCondition"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}GroupType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}MessageInTransactionType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}MessageType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}OrganisationType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}PersonType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}ProjectType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}RoleType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}SimpleElementType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}TransactionPhaseType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}TransactionType"/>
 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}UserDefinedType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "appendixTypeOrComplexElementTypeOrElementCondition"
})
@XmlRootElement(name = "visiXML_VISI_Systematics")
public class VisiXMLVISISystematics {

    @XmlElements({
        @XmlElement(name = "TransactionPhaseType", type = TransactionPhaseTypeType.class),
        @XmlElement(name = "MessageInTransactionType", type = MessageInTransactionTypeType.class),
        @XmlElement(name = "ComplexElementType", type = ComplexElementTypeType.class),
        @XmlElement(name = "SimpleElementType", type = SimpleElementTypeType.class),
        @XmlElement(name = "RoleType", type = RoleTypeType.class),
        @XmlElement(name = "ProjectType", type = ProjectTypeType.class),
        @XmlElement(name = "UserDefinedType", type = UserDefinedTypeType.class),
        @XmlElement(name = "TransactionType", type = TransactionTypeType.class),
        @XmlElement(name = "OrganisationType", type = OrganisationTypeType.class),
        @XmlElement(name = "GroupType", type = GroupTypeType.class),
        @XmlElement(name = "ElementCondition", type = ElementConditionType.class),
        @XmlElement(name = "MessageType", type = MessageTypeType.class),
        @XmlElement(name = "PersonType", type = PersonTypeType.class),
        @XmlElement(name = "AppendixType", type = AppendixTypeType.class)
    })
    protected List<ElementType> appendixTypeOrComplexElementTypeOrElementCondition;

    /**
     * Gets the value of the appendixTypeOrComplexElementTypeOrElementCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the appendixTypeOrComplexElementTypeOrElementCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAppendixTypeOrComplexElementTypeOrElementCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionPhaseTypeType }
     * {@link MessageInTransactionTypeType }
     * {@link ComplexElementTypeType }
     * {@link SimpleElementTypeType }
     * {@link RoleTypeType }
     * {@link ProjectTypeType }
     * {@link UserDefinedTypeType }
     * {@link TransactionTypeType }
     * {@link OrganisationTypeType }
     * {@link GroupTypeType }
     * {@link ElementConditionType }
     * {@link MessageTypeType }
     * {@link PersonTypeType }
     * {@link AppendixTypeType }
     * 
     * 
     */
    public List<ElementType> getAppendixTypeOrComplexElementTypeOrElementCondition() {
        if (appendixTypeOrComplexElementTypeOrElementCondition == null) {
            appendixTypeOrComplexElementTypeOrElementCondition = new ArrayList<ElementType>();
        }
        return this.appendixTypeOrComplexElementTypeOrElementCondition;
    }

}
