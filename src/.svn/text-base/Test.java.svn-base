import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import nl.visi.interaction_schema.ComplexElementTypeType;
import nl.visi.interaction_schema.ComplexElementTypeTypeRef;
import nl.visi.interaction_schema.ElementType;
import nl.visi.interaction_schema.ProjectTypeType;
import nl.visi.interaction_schema.SimpleElementTypeType;
import nl.visi.interaction_schema.SimpleElementTypeTypeRef;
import nl.visi.interaction_schema.UserDefinedTypeType;
import nl.visi.interaction_schema.VisiXMLVISISystematics;
import nl.visi.interaction_schema.ComplexElementTypeType.SimpleElements;
import nl.visi.interaction_schema.SimpleElementTypeType.UserDefinedType;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class Test {
	private static final java.text.DateFormat sdf = SimpleDateFormat.getDateTimeInstance();
	private ErrorHandler errorHandler = new ErrorHandler() {

		@Override
		public void error(SAXParseException exception) throws SAXException {
			System.err.println("error " + exception.getLineNumber() + " " + exception.getColumnNumber());
			System.err.println(exception.getMessage());
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			System.err.println("fatal error " + exception.getLineNumber() + " " + exception.getColumnNumber());
			System.err.println(exception.getMessage());
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			System.err.println("warning" + exception.getLineNumber() + " " + exception.getColumnNumber());
			System.err.println(exception.getMessage());
		}
	};

	public Test() throws SAXException, ParserConfigurationException, IOException, JAXBException {
		// build an XSD-aware SchemaFactory
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// hook up org.xml.sax.ErrorHandler implementation.
		schemaFactory.setErrorHandler(errorHandler);
		// get the custom xsd schema describing the required format for my XML
		// files.
		Schema schemaXSD = schemaFactory.newSchema(new File("_3.xsd"));
		// Create a Validator capable of validating XML files according to my
		// custom schema.
		Validator validator = schemaXSD.newValidator();
		// Get a parser capable of parsing vanilla XML into a DOM tree
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder parser = documentBuilderFactory.newDocumentBuilder();
		// parse the XML purely as XML and get a DOM tree representation.
		Document document = parser.parse(new File("_7.xml"));
		// parse the XML DOM tree against the stricter XSD schema
		validator.validate(new DOMSource(document));

		VisiXMLVISISystematics xmlvisiSystematics = unmarshal(VisiXMLVISISystematics.class, new FileInputStream(
				new File("_7.xml")));
		List<ElementType> list = xmlvisiSystematics.getAppendixTypeOrComplexElementTypeOrElementCondition();
		for (ElementType et : list) {
			System.out.println(et.getId() + " " + et.getClass().getName());
			if (et.getClass() == ProjectTypeType.class) {
				ProjectTypeType projectTT = (ProjectTypeType) et;
				System.out.println("\tCategory: " + projectTT.getCategory());
				System.out.println("\tCode: " + projectTT.getCode());
				System.out.println("\tComplexElements: ");
				getProjectComplexElements(projectTT.getComplexElements());
				System.out.println("\tDateLamu: " + getDateTime(projectTT.getDateLamu()));
				System.out.println("\tDescription: " + projectTT.getDescription());
				System.out.println("\tEndDate: " + getDateTime(projectTT.getEndDate()));
				System.out.println("\tHelpInfo: " + projectTT.getHelpInfo());
				System.out.println("\tId: " + projectTT.getId());
				System.out.println("\tLanguage: " + projectTT.getLanguage());
				System.out.println("\tStartDate: " + getDateTime(projectTT.getStartDate()));
				System.out.println("\tState: " + projectTT.getState());
				System.out.println("\tUserLamu: " + projectTT.getUserLamu());
			}
		}

	}

	public String getDateTime(XMLGregorianCalendar dateTime) {
		return sdf.format(dateTime.toGregorianCalendar().getTime());
	}

	public void getProjectComplexElements(nl.visi.interaction_schema.ProjectTypeType.ComplexElements complexElements) {
		List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		for (Object ce : list) {
			if (ce instanceof ComplexElementTypeTypeRef) {
				Object object = ((ComplexElementTypeTypeRef) ce).getIdref();
				if (object instanceof ComplexElementTypeType) {
					ComplexElementTypeType complexElementTT = (ComplexElementTypeType) object;
					System.out.println("\t\tId: " + complexElementTT.getId());
					SimpleElements simpleElements = complexElementTT.getSimpleElements();
					List<Object> list2 = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
					System.out.println("\t\tSimpleElements: ");
					for (Object se : list2) {
						if (se instanceof SimpleElementTypeTypeRef) {
							Object object2 = ((SimpleElementTypeTypeRef) se).getIdref();
							if (object2 instanceof SimpleElementTypeType) {
								SimpleElementTypeType seTT = (SimpleElementTypeType) object2;
								System.out.print("\t\t\tId: " + seTT.getId());
								UserDefinedType userDefinedType = seTT.getUserDefinedType();
								Object object3 = userDefinedType.getUserDefinedTypeRef().getIdref();
								if (object3 instanceof UserDefinedTypeType) {
									UserDefinedTypeType userDefinedTT = (UserDefinedTypeType) object3;
									System.out.println(" (" + userDefinedTT.getBaseType() + ")");
								}
							}
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		// JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(inputStream);
		T doc = (T) u.unmarshal(inputStream);
		return doc;
	}

	public static void main(String[] args) {
		try {
			new Test();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
