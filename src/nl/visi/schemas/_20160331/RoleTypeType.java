//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.12.05 at 01:30:30 PM CET 
//


package nl.visi.schemas._20160331;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RoleTypeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RoleTypeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.visi.nl/schemas/20160331}elementType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="state" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateLaMu" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="userLaMu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="language" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="category" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="helpInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsibilityScope" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsibilityTask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsibilitySupportTask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="responsibilityFeedback" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RoleTypeType", propOrder = {
    "description",
    "startDate",
    "endDate",
    "state",
    "dateLaMu",
    "userLaMu",
    "language",
    "category",
    "helpInfo",
    "code",
    "responsibilityScope",
    "responsibilityTask",
    "responsibilitySupportTask",
    "responsibilityFeedback"
})
public class RoleTypeType
    extends ElementType
{

    @XmlElement(required = true)
    protected String description;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    protected String state;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateLaMu;
    protected String userLaMu;
    protected String language;
    protected String category;
    protected String helpInfo;
    protected String code;
    protected String responsibilityScope;
    protected String responsibilityTask;
    protected String responsibilitySupportTask;
    protected String responsibilityFeedback;

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
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the dateLaMu property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateLaMu() {
        return dateLaMu;
    }

    /**
     * Sets the value of the dateLaMu property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateLaMu(XMLGregorianCalendar value) {
        this.dateLaMu = value;
    }

    /**
     * Gets the value of the userLaMu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserLaMu() {
        return userLaMu;
    }

    /**
     * Sets the value of the userLaMu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserLaMu(String value) {
        this.userLaMu = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategory(String value) {
        this.category = value;
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
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the responsibilityScope property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsibilityScope() {
        return responsibilityScope;
    }

    /**
     * Sets the value of the responsibilityScope property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsibilityScope(String value) {
        this.responsibilityScope = value;
    }

    /**
     * Gets the value of the responsibilityTask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsibilityTask() {
        return responsibilityTask;
    }

    /**
     * Sets the value of the responsibilityTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsibilityTask(String value) {
        this.responsibilityTask = value;
    }

    /**
     * Gets the value of the responsibilitySupportTask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsibilitySupportTask() {
        return responsibilitySupportTask;
    }

    /**
     * Sets the value of the responsibilitySupportTask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsibilitySupportTask(String value) {
        this.responsibilitySupportTask = value;
    }

    /**
     * Gets the value of the responsibilityFeedback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsibilityFeedback() {
        return responsibilityFeedback;
    }

    /**
     * Sets the value of the responsibilityFeedback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsibilityFeedback(String value) {
        this.responsibilityFeedback = value;
    }

}
