//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.11.01 at 09:58:58 PM CET 
//


package nl.visi.interaction_schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ElementConditionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ElementConditionType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.visi.nl/schemas/20071218}elementType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="condition" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="helpInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="complexElement" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20071218}ComplexElementType"/>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20071218}ComplexElementTypeRef"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="simpleElement" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20071218}SimpleElementType"/>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20071218}SimpleElementTypeRef"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="messageInTransaction" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20071218}MessageInTransactionType"/>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20071218}MessageInTransactionTypeRef"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ElementConditionType", propOrder = {
    "description",
    "condition",
    "helpInfo",
    "complexElement",
    "simpleElement",
    "messageInTransaction"
})
public class ElementConditionType
    extends ElementType
{

    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String condition;
    protected String helpInfo;
    protected ElementConditionType.ComplexElement complexElement;
    protected ElementConditionType.SimpleElement simpleElement;
    protected ElementConditionType.MessageInTransaction messageInTransaction;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondition(String value) {
        this.condition = value;
    }

    /**
     * Gets the value of the helpInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelpInfo() {
        return helpInfo;
    }

    /**
     * Sets the value of the helpInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelpInfo(String value) {
        this.helpInfo = value;
    }

    /**
     * Gets the value of the complexElement property.
     * 
     * @return
     *     possible object is
     *     {@link ElementConditionType.ComplexElement }
     *     
     */
    public ElementConditionType.ComplexElement getComplexElement() {
        return complexElement;
    }

    /**
     * Sets the value of the complexElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementConditionType.ComplexElement }
     *     
     */
    public void setComplexElement(ElementConditionType.ComplexElement value) {
        this.complexElement = value;
    }

    /**
     * Gets the value of the simpleElement property.
     * 
     * @return
     *     possible object is
     *     {@link ElementConditionType.SimpleElement }
     *     
     */
    public ElementConditionType.SimpleElement getSimpleElement() {
        return simpleElement;
    }

    /**
     * Sets the value of the simpleElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementConditionType.SimpleElement }
     *     
     */
    public void setSimpleElement(ElementConditionType.SimpleElement value) {
        this.simpleElement = value;
    }

    /**
     * Gets the value of the messageInTransaction property.
     * 
     * @return
     *     possible object is
     *     {@link ElementConditionType.MessageInTransaction }
     *     
     */
    public ElementConditionType.MessageInTransaction getMessageInTransaction() {
        return messageInTransaction;
    }

    /**
     * Sets the value of the messageInTransaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link ElementConditionType.MessageInTransaction }
     *     
     */
    public void setMessageInTransaction(ElementConditionType.MessageInTransaction value) {
        this.messageInTransaction = value;
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
     *       &lt;choice>
     *         &lt;element ref="{http://www.visi.nl/schemas/20071218}ComplexElementType"/>
     *         &lt;element ref="{http://www.visi.nl/schemas/20071218}ComplexElementTypeRef"/>
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
        "complexElementType",
        "complexElementTypeRef"
    })
    public static class ComplexElement {

        @XmlElement(name = "ComplexElementType")
        protected ComplexElementTypeType complexElementType;
        @XmlElement(name = "ComplexElementTypeRef")
        protected ComplexElementTypeTypeRef complexElementTypeRef;

        /**
         * Gets the value of the complexElementType property.
         * 
         * @return
         *     possible object is
         *     {@link ComplexElementTypeType }
         *     
         */
        public ComplexElementTypeType getComplexElementType() {
            return complexElementType;
        }

        /**
         * Sets the value of the complexElementType property.
         * 
         * @param value
         *     allowed object is
         *     {@link ComplexElementTypeType }
         *     
         */
        public void setComplexElementType(ComplexElementTypeType value) {
            this.complexElementType = value;
        }

        /**
         * Gets the value of the complexElementTypeRef property.
         * 
         * @return
         *     possible object is
         *     {@link ComplexElementTypeTypeRef }
         *     
         */
        public ComplexElementTypeTypeRef getComplexElementTypeRef() {
            return complexElementTypeRef;
        }

        /**
         * Sets the value of the complexElementTypeRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link ComplexElementTypeTypeRef }
         *     
         */
        public void setComplexElementTypeRef(ComplexElementTypeTypeRef value) {
            this.complexElementTypeRef = value;
        }

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
     *       &lt;choice>
     *         &lt;element ref="{http://www.visi.nl/schemas/20071218}MessageInTransactionType"/>
     *         &lt;element ref="{http://www.visi.nl/schemas/20071218}MessageInTransactionTypeRef"/>
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
        "messageInTransactionType",
        "messageInTransactionTypeRef"
    })
    public static class MessageInTransaction {

        @XmlElement(name = "MessageInTransactionType")
        protected MessageInTransactionTypeType messageInTransactionType;
        @XmlElement(name = "MessageInTransactionTypeRef")
        protected MessageInTransactionTypeTypeRef messageInTransactionTypeRef;

        /**
         * Gets the value of the messageInTransactionType property.
         * 
         * @return
         *     possible object is
         *     {@link MessageInTransactionTypeType }
         *     
         */
        public MessageInTransactionTypeType getMessageInTransactionType() {
            return messageInTransactionType;
        }

        /**
         * Sets the value of the messageInTransactionType property.
         * 
         * @param value
         *     allowed object is
         *     {@link MessageInTransactionTypeType }
         *     
         */
        public void setMessageInTransactionType(MessageInTransactionTypeType value) {
            this.messageInTransactionType = value;
        }

        /**
         * Gets the value of the messageInTransactionTypeRef property.
         * 
         * @return
         *     possible object is
         *     {@link MessageInTransactionTypeTypeRef }
         *     
         */
        public MessageInTransactionTypeTypeRef getMessageInTransactionTypeRef() {
            return messageInTransactionTypeRef;
        }

        /**
         * Sets the value of the messageInTransactionTypeRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link MessageInTransactionTypeTypeRef }
         *     
         */
        public void setMessageInTransactionTypeRef(MessageInTransactionTypeTypeRef value) {
            this.messageInTransactionTypeRef = value;
        }

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
     *       &lt;choice>
     *         &lt;element ref="{http://www.visi.nl/schemas/20071218}SimpleElementType"/>
     *         &lt;element ref="{http://www.visi.nl/schemas/20071218}SimpleElementTypeRef"/>
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
        "simpleElementType",
        "simpleElementTypeRef"
    })
    public static class SimpleElement {

        @XmlElement(name = "SimpleElementType")
        protected SimpleElementTypeType simpleElementType;
        @XmlElement(name = "SimpleElementTypeRef")
        protected SimpleElementTypeTypeRef simpleElementTypeRef;

        /**
         * Gets the value of the simpleElementType property.
         * 
         * @return
         *     possible object is
         *     {@link SimpleElementTypeType }
         *     
         */
        public SimpleElementTypeType getSimpleElementType() {
            return simpleElementType;
        }

        /**
         * Sets the value of the simpleElementType property.
         * 
         * @param value
         *     allowed object is
         *     {@link SimpleElementTypeType }
         *     
         */
        public void setSimpleElementType(SimpleElementTypeType value) {
            this.simpleElementType = value;
        }

        /**
         * Gets the value of the simpleElementTypeRef property.
         * 
         * @return
         *     possible object is
         *     {@link SimpleElementTypeTypeRef }
         *     
         */
        public SimpleElementTypeTypeRef getSimpleElementTypeRef() {
            return simpleElementTypeRef;
        }

        /**
         * Sets the value of the simpleElementTypeRef property.
         * 
         * @param value
         *     allowed object is
         *     {@link SimpleElementTypeTypeRef }
         *     
         */
        public void setSimpleElementTypeRef(SimpleElementTypeTypeRef value) {
            this.simpleElementTypeRef = value;
        }

    }

}
