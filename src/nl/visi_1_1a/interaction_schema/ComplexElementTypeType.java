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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for ComplexElementTypeType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="ComplexElementTypeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.visi.nl/schemas/20070406}elementType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dateLamu" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="userLamu" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="helpInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="complexElements" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded">
 *                   &lt;element ref="{http://www.visi.nl/schemas/20070406}ComplexElementType"/>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20070406}ComplexElementTypeRef"/>
 *                 &lt;/choice>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="simpleElements" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded">
 *                   &lt;element ref="{http://www.visi.nl/schemas/20070406}SimpleElementType"/>
 *                   &lt;element ref="{http://www.visi.nl/schemas/20070406}SimpleElementTypeRef"/>
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
@XmlType(name = "ComplexElementTypeType", propOrder = { "description", "startDate", "endDate", "state", "dateLamu",
		"userLamu", "language", "category", "helpInfo", "complexElements", "simpleElements" })
public class ComplexElementTypeType extends ElementType {

	@XmlElement(required = true)
	protected String description;
	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar startDate;
	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar endDate;
	@XmlElement(required = true)
	protected String state;
	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar dateLamu;
	@XmlElement(required = true)
	protected String userLamu;
	protected String language;
	protected String category;
	protected String helpInfo;
	protected ComplexElementTypeType.ComplexElements complexElements;
	protected ComplexElementTypeType.SimpleElements simpleElements;

	/**
	 * Gets the value of the description property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the value of the description property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescription(String value) {
		this.description = value;
	}

	/**
	 * Gets the value of the startDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getStartDate() {
		return startDate;
	}

	/**
	 * Sets the value of the startDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setStartDate(XMLGregorianCalendar value) {
		this.startDate = value;
	}

	/**
	 * Gets the value of the endDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getEndDate() {
		return endDate;
	}

	/**
	 * Sets the value of the endDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setEndDate(XMLGregorianCalendar value) {
		this.endDate = value;
	}

	/**
	 * Gets the value of the state property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the value of the state property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setState(String value) {
		this.state = value;
	}

	/**
	 * Gets the value of the dateLamu property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDateLamu() {
		return dateLamu;
	}

	/**
	 * Sets the value of the dateLamu property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDateLamu(XMLGregorianCalendar value) {
		this.dateLamu = value;
	}

	/**
	 * Gets the value of the userLamu property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUserLamu() {
		return userLamu;
	}

	/**
	 * Sets the value of the userLamu property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUserLamu(String value) {
		this.userLamu = value;
	}

	/**
	 * Gets the value of the language property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the value of the language property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLanguage(String value) {
		this.language = value;
	}

	/**
	 * Gets the value of the category property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Sets the value of the category property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCategory(String value) {
		this.category = value;
	}

	/**
	 * Gets the value of the helpInfo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getHelpInfo() {
		return helpInfo;
	}

	/**
	 * Sets the value of the helpInfo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setHelpInfo(String value) {
		this.helpInfo = value;
	}

	/**
	 * Gets the value of the complexElements property.
	 * 
	 * @return possible object is {@link ComplexElementTypeType.ComplexElements }
	 * 
	 */
	public ComplexElementTypeType.ComplexElements getComplexElements() {
		return complexElements;
	}

	/**
	 * Sets the value of the complexElements property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link ComplexElementTypeType.ComplexElements }
	 * 
	 */
	public void setComplexElements(ComplexElementTypeType.ComplexElements value) {
		this.complexElements = value;
	}

	/**
	 * Gets the value of the simpleElements property.
	 * 
	 * @return possible object is {@link ComplexElementTypeType.SimpleElements }
	 * 
	 */
	public ComplexElementTypeType.SimpleElements getSimpleElements() {
		return simpleElements;
	}

	/**
	 * Sets the value of the simpleElements property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link ComplexElementTypeType.SimpleElements }
	 * 
	 */
	public void setSimpleElements(ComplexElementTypeType.SimpleElements value) {
		this.simpleElements = value;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;choice maxOccurs="unbounded">
	 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}ComplexElementType"/>
	 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}ComplexElementTypeRef"/>
	 *       &lt;/choice>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "complexElementTypeOrComplexElementTypeRef" })
	public static class ComplexElements {

		@XmlElements({ @XmlElement(name = "ComplexElementType", type = ComplexElementTypeType.class),
				@XmlElement(name = "ComplexElementTypeRef", type = ComplexElementTypeTypeRef.class) })
		protected List<Object> complexElementTypeOrComplexElementTypeRef;

		/**
		 * Gets the value of the complexElementTypeOrComplexElementTypeRef
		 * property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the
		 * complexElementTypeOrComplexElementTypeRef property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getComplexElementTypeOrComplexElementTypeRef().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link ComplexElementTypeType } {@link ComplexElementTypeTypeRef }
		 * 
		 * 
		 */
		public List<Object> getComplexElementTypeOrComplexElementTypeRef() {
			if (complexElementTypeOrComplexElementTypeRef == null) {
				complexElementTypeOrComplexElementTypeRef = new ArrayList<Object>();
			}
			return this.complexElementTypeOrComplexElementTypeRef;
		}

	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;choice maxOccurs="unbounded">
	 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}SimpleElementType"/>
	 *         &lt;element ref="{http://www.visi.nl/schemas/20070406}SimpleElementTypeRef"/>
	 *       &lt;/choice>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "simpleElementTypeOrSimpleElementTypeRef" })
	public static class SimpleElements {

		@XmlElements({ @XmlElement(name = "SimpleElementType", type = SimpleElementTypeType.class),
				@XmlElement(name = "SimpleElementTypeRef", type = SimpleElementTypeTypeRef.class) })
		protected List<Object> simpleElementTypeOrSimpleElementTypeRef;

		/**
		 * Gets the value of the simpleElementTypeOrSimpleElementTypeRef
		 * property.
		 * 
		 * <p>
		 * This accessor method returns a reference to the live list, not a
		 * snapshot. Therefore any modification you make to the returned list
		 * will be present inside the JAXB object. This is why there is not a
		 * <CODE>set</CODE> method for the
		 * simpleElementTypeOrSimpleElementTypeRef property.
		 * 
		 * <p>
		 * For example, to add a new item, do as follows:
		 * 
		 * <pre>
		 * getSimpleElementTypeOrSimpleElementTypeRef().add(newItem);
		 * </pre>
		 * 
		 * 
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link SimpleElementTypeType } {@link SimpleElementTypeTypeRef }
		 * 
		 * 
		 */
		public List<Object> getSimpleElementTypeOrSimpleElementTypeRef() {
			if (simpleElementTypeOrSimpleElementTypeRef == null) {
				simpleElementTypeOrSimpleElementTypeRef = new ArrayList<Object>();
			}
			return this.simpleElementTypeOrSimpleElementTypeRef;
		}

	}

}
