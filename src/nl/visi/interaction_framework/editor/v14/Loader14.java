package nl.visi.interaction_framework.editor.v14;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import nl.visi.schemas._20140331.AppendixTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeType.ComplexElements;
import nl.visi.schemas._20140331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20140331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20140331.ElementConditionType;
import nl.visi.schemas._20140331.ElementConditionType.MessageInTransaction;
import nl.visi.schemas._20140331.ElementConditionType.SimpleElement;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.GroupTypeType;
import nl.visi.schemas._20140331.GroupTypeTypeRef;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType.SendAfter;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType.SendBefore;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionTypeRef;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Conditions;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Group;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.TransactionPhase;
import nl.visi.schemas._20140331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.MessageTypeTypeRef;
import nl.visi.schemas._20140331.ObjectFactory;
import nl.visi.schemas._20140331.OrganisationTypeType;
import nl.visi.schemas._20140331.PersonTypeType;
import nl.visi.schemas._20140331.ProjectTypeType;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.RoleTypeTypeRef;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType.UserDefinedType;
import nl.visi.schemas._20140331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20140331.TransactionPhaseTypeType;
import nl.visi.schemas._20140331.TransactionPhaseTypeTypeRef;
import nl.visi.schemas._20140331.TransactionTypeType;
import nl.visi.schemas._20140331.TransactionTypeType.Executor;
import nl.visi.schemas._20140331.TransactionTypeType.Initiator;
import nl.visi.schemas._20140331.TransactionTypeTypeRef;
import nl.visi.schemas._20140331.UserDefinedTypeType;
import nl.visi.schemas._20140331.UserDefinedTypeTypeRef;
import nl.visi.schemas._20140331.VisiXMLVISISystematics;

class Loader14 {
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

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

	public void validate(InputSource schema, File xml, DefaultHandler defaultHandler) throws SAXParseException {
		if (xml != null) {
			// build an XSD-aware SchemaFactory
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// hook up org.xml.sax.ErrorHandler implementation.
			schemaFactory.setErrorHandler(errorHandler);
			// get the custom xsd schema describing the required format for my XML
			// files.
			// Schema schemaXSD = schemaFactory.newSchema(schema);
			// Create a Validator capable of validating XML files according to my
			// custom schema.
			// Validator validator = schemaXSD.newValidator();
			try {
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				saxParserFactory.setNamespaceAware(true);
				saxParserFactory.setValidating(true);
				SAXParser saxParser = saxParserFactory.newSAXParser();

				saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
				saxParser.setProperty(JAXP_SCHEMA_SOURCE, schema);
				saxParser.parse(xml, defaultHandler);
			} catch (SAXNotRecognizedException e) {
				e.getStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void marshal(PrintStream out) throws Exception {
		ObjectFactory objectFactory = new ObjectFactory();
		VisiXMLVISISystematics visiXMLVISISystematics = objectFactory.createVisiXMLVISISystematics();
		Store14 store = Editor14.getStore14();
		for (Store14.ElementTypeType et : Store14.ElementTypeType.values()) {
			List<?> elements = store.getElements(et.getElementClass());
			for (Object object : elements) {
				visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition()
						.add((ElementType) object);
			}
		}
		JAXBContext jc = JAXBContext.newInstance("nl.visi.schemas._20160331");
		Marshaller m = jc.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		m.marshal(visiXMLVISISystematics, out);
		out.close();
	}

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		T doc = (T) u.unmarshal(inputStream);
		return doc;
	}

	public void load(InputSource interactionSchema, File interactionFramework) {
		Store14 store = Editor14.getStore14();
		store.clear();

		VisiXMLVISISystematics xmlvisiSystematics;
		try {
			xmlvisiSystematics = unmarshal(VisiXMLVISISystematics.class, new FileInputStream(interactionFramework));
			List<ElementType> list = xmlvisiSystematics.getAppendixTypeOrComplexElementTypeOrElementCondition();
			for (ElementType et : list) {
				loadChildren(et);
				store.put(et.getId(), et);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void loadChildren(ElementType et) {
		Store14.ElementTypeType type = Store14.ElementTypeType.valueOf(et.getClass().getSimpleName());
		switch (type) {
		case AppendixTypeType:
			loadAppendixType(et);
			break;
		case ComplexElementTypeType:
			loadComplexElementType(et);
			break;
		case ElementConditionType:
			loadElementConditionType(et);
			break;
		case GroupTypeType:
			break;
		case MessageInTransactionTypeConditionType:
			loadMessageInTransactionTypeConditionType(et);
			break;
		case MessageInTransactionTypeType:
			loadMessageInTransactionType(et);
			break;
		case MessageTypeType:
			loadMessageType(et);
			break;
		case OrganisationTypeType:
			loadOrganisationType(et);
			break;
		case PersonTypeType:
			loadPersonType(et);
			break;
		case ProjectTypeType:
			loadProjectType(et);
			break;
		case RoleTypeType:
			break;
		case SimpleElementTypeType:
			loadSimpleElementType(et);
			break;
		case TransactionPhaseTypeType:
			break;
		case TransactionTypeType:
			loadTransactionType(et);
			break;
		case UserDefinedTypeType:
			break;
		default:
			System.err.println("Unknown type: " + type);
		}
	}

	private void loadTransactionType(ElementType et) {
		TransactionTypeType transaction = (TransactionTypeType) et;
		Initiator initiator = transaction.getInitiator();
		if (initiator != null) {
			RoleTypeType roleTypeType = initiator.getRoleType();
			RoleTypeTypeRef roleTypeTypeRef = initiator.getRoleTypeRef();
			filter(roleTypeType != null ? roleTypeType : roleTypeTypeRef, RoleTypeType.class, RoleTypeTypeRef.class);
		}
		Executor executor = transaction.getExecutor();
		if (executor != null) {
			RoleTypeType roleTypeType = executor.getRoleType();
			RoleTypeTypeRef roleTypeTypeRef = executor.getRoleTypeRef();
			filter(roleTypeType != null ? roleTypeType : roleTypeTypeRef, RoleTypeType.class, RoleTypeTypeRef.class);
		}
	}

	private void loadSimpleElementType(ElementType et) {
		SimpleElementTypeType simpleElement = (SimpleElementTypeType) et;
		UserDefinedType userDefinedType = simpleElement.getUserDefinedType();
		if (userDefinedType != null) {
			UserDefinedTypeType userDefinedTypeType = userDefinedType.getUserDefinedType();
			UserDefinedTypeTypeRef userDefinedTypeTypeRef = userDefinedType.getUserDefinedTypeRef();
			filter(userDefinedTypeType != null ? userDefinedTypeType : userDefinedTypeTypeRef,
					UserDefinedTypeType.class, UserDefinedTypeTypeRef.class);
		}
	}

	private void loadProjectType(ElementType et) {
		List<Object> list;
		ProjectTypeType project = (ProjectTypeType) et;
		ProjectTypeType.ComplexElements projectComplexElements = project.getComplexElements();
		if (projectComplexElements != null) {
			list = projectComplexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
	}

	private void loadPersonType(ElementType et) {
		List<Object> list;
		PersonTypeType person = (PersonTypeType) et;
		PersonTypeType.ComplexElements personComplexElements = person.getComplexElements();
		if (personComplexElements != null) {
			list = personComplexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
	}

	private void loadOrganisationType(ElementType et) {
		List<Object> list;
		OrganisationTypeType organisation = (OrganisationTypeType) et;
		OrganisationTypeType.ComplexElements organisationComplexElements = organisation.getComplexElements();
		if (organisationComplexElements != null) {
			list = organisationComplexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
	}

	private void loadMessageType(ElementType et) {
		List<Object> list;
		MessageTypeType message = (MessageTypeType) et;
		MessageTypeType.ComplexElements messageComplexElements = message.getComplexElements();
		if (messageComplexElements != null) {
			list = messageComplexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
	}

	private void loadMessageInTransactionTypeConditionType(ElementType et) {
		List<Object> list;
		MessageInTransactionTypeConditionType messageInTransactionCondition = (MessageInTransactionTypeConditionType) et;
		SendAfter sendAfter = messageInTransactionCondition.getSendAfter();
		if (sendAfter != null) {
			list = sendAfter.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			filter(list, MessageInTransactionTypeConditionType.class, MessageInTransactionTypeConditionTypeRef.class);
		}
		SendBefore sendBefore = messageInTransactionCondition.getSendBefore();
		if (sendBefore != null) {
			list = sendBefore.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			filter(list, MessageInTransactionTypeConditionType.class, MessageInTransactionTypeConditionTypeRef.class);
		}
	}

	private void loadMessageInTransactionType(ElementType et) {
		List<Object> list;
		MessageInTransactionTypeType messageInTransaction = (MessageInTransactionTypeType) et;
		Group mittGroup = messageInTransaction.getGroup();
		if (mittGroup != null) {
			GroupTypeType groupTypeType = mittGroup.getGroupType();
			GroupTypeTypeRef groupTypeTypeRef = mittGroup.getGroupTypeRef();
			filter(groupTypeType != null ? groupTypeType : groupTypeTypeRef, GroupTypeType.class,
					GroupTypeTypeRef.class);
		}
		Message mittMessage = messageInTransaction.getMessage();
		if (mittMessage != null) {
			MessageTypeType messageTypeType = mittMessage.getMessageType();
			MessageTypeTypeRef messageTypeTypeRef = mittMessage.getMessageTypeRef();
			filter(messageTypeType != null ? messageTypeType : messageTypeTypeRef, MessageTypeType.class,
					MessageTypeTypeRef.class);
		}
		Conditions conditions = messageInTransaction.getConditions();
		if (conditions != null) {
			list = conditions.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
			filter(list, MessageInTransactionTypeConditionType.class, MessageInTransactionTypeTypeRef.class);
		}
		Previous previous = messageInTransaction.getPrevious();
		if (previous != null) {
			list = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			filter(list, MessageInTransactionTypeType.class, MessageInTransactionTypeTypeRef.class);
		}
		Transaction mittTransaction = messageInTransaction.getTransaction();
		if (mittTransaction != null) {
			TransactionTypeType transactionTypeType = mittTransaction.getTransactionType();
			TransactionTypeTypeRef transactionTypeTypeRef = mittTransaction.getTransactionTypeRef();
			filter(transactionTypeType != null ? transactionTypeType : transactionTypeTypeRef,
					TransactionTypeType.class, TransactionTypeTypeRef.class);
		}
		TransactionPhase mittTransactionPhase = messageInTransaction.getTransactionPhase();
		if (mittTransactionPhase != null) {
			TransactionPhaseTypeType transactionPhaseTypeType = mittTransactionPhase.getTransactionPhaseType();
			TransactionPhaseTypeTypeRef transactionPhaseTypeTypeRef = mittTransactionPhase.getTransactionPhaseTypeRef();
			filter(transactionPhaseTypeType != null ? transactionPhaseTypeType : transactionPhaseTypeTypeRef,
					TransactionPhaseTypeType.class, TransactionPhaseTypeTypeRef.class);
		}
	}

	private void loadElementConditionType(ElementType et) {
		ElementConditionType elementCondition = (ElementConditionType) et;
		ElementConditionType.ComplexElement ecComplexElement = elementCondition.getComplexElement();
		if (ecComplexElement != null) {
			ComplexElementTypeType complexElementType = ecComplexElement.getComplexElementType();
			ComplexElementTypeTypeRef complexElementTypeRef = ecComplexElement.getComplexElementTypeRef();
			filter(complexElementType != null ? complexElementType : complexElementTypeRef,
					ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
		MessageInTransaction mitt = elementCondition.getMessageInTransaction();
		if (mitt != null) {
			MessageInTransactionTypeType messageInTransactionType = mitt.getMessageInTransactionType();
			MessageInTransactionTypeTypeRef messageInTransactionTypeRef = mitt.getMessageInTransactionTypeRef();
			filter(messageInTransactionType != null ? messageInTransactionType : messageInTransactionTypeRef,
					MessageInTransactionTypeType.class, MessageInTransactionTypeTypeRef.class);
		}
		SimpleElement se = elementCondition.getSimpleElement();
		if (se != null) {
			SimpleElementTypeType simpleElementTypeType = se.getSimpleElementType();
			SimpleElementTypeTypeRef simpleElementTypeTypeRef = se.getSimpleElementTypeRef();
			filter(simpleElementTypeType != null ? simpleElementTypeType : simpleElementTypeTypeRef,
					SimpleElementTypeType.class, SimpleElementTypeTypeRef.class);
		}
	}

	private void loadComplexElementType(ElementType et) {
		List<Object> list;
		ComplexElementTypeType complexElement = (ComplexElementTypeType) et;
		ComplexElements complexElements = complexElement.getComplexElements();
		if (complexElements != null) {
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
		SimpleElements simpleElements = complexElement.getSimpleElements();
		if (simpleElements != null) {
			list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
			filter(list, SimpleElementTypeType.class, SimpleElementTypeTypeRef.class);
		}
	}

	private void loadAppendixType(ElementType et) {
		List<Object> list;
		AppendixTypeType appendix = (AppendixTypeType) et;
		AppendixTypeType.ComplexElements appendixComplexElements = appendix.getComplexElements();
		if (appendixComplexElements != null) {
			list = appendixComplexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
	}

	private <T, R> void filter(List<Object> list, Class<T> typeType, Class<R> typeTypeRef) {
		for (Object object : list) {
			filter(object, typeType, typeTypeRef);
		}
	}

	@SuppressWarnings("unchecked")
	private <T, R> void filter(Object object, Class<T> typeType, Class<R> typeTypeRef) {
		if (object.getClass().equals(typeTypeRef)) {
		} else if (object.getClass().equals(typeType)) {
			T tt = (T) object;
			String id = ((ElementType) tt).getId();
			Editor14.getStore14().put(id, tt);
			loadChildren((ElementType) tt);
		}
	}
}
