package nl.visi_1_1a.interaction_framework.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import nl.visi_1_1a.interaction_schema.AppendixTypeType;
import nl.visi_1_1a.interaction_schema.ComplexElementTypeType;
import nl.visi_1_1a.interaction_schema.ComplexElementTypeTypeRef;
import nl.visi_1_1a.interaction_schema.ElementType;
import nl.visi_1_1a.interaction_schema.GroupTypeType;
import nl.visi_1_1a.interaction_schema.GroupTypeTypeRef;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Group;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Previous;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Transaction;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.TransactionPhase;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeTypeRef;
import nl.visi_1_1a.interaction_schema.MessageTypeType;
import nl.visi_1_1a.interaction_schema.MessageTypeTypeRef;
import nl.visi_1_1a.interaction_schema.OrganisationTypeType;
import nl.visi_1_1a.interaction_schema.PersonTypeType;
import nl.visi_1_1a.interaction_schema.ProjectTypeType;
import nl.visi_1_1a.interaction_schema.RoleTypeType;
import nl.visi_1_1a.interaction_schema.RoleTypeTypeRef;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeType;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeType.UserDefinedType;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeTypeRef;
import nl.visi_1_1a.interaction_schema.TransactionPhaseTypeType;
import nl.visi_1_1a.interaction_schema.TransactionPhaseTypeTypeRef;
import nl.visi_1_1a.interaction_schema.TransactionTypeType;
import nl.visi_1_1a.interaction_schema.TransactionTypeType.Executor;
import nl.visi_1_1a.interaction_schema.TransactionTypeType.Initiator;
import nl.visi_1_1a.interaction_schema.TransactionTypeTypeRef;
import nl.visi_1_1a.interaction_schema.UserDefinedTypeType;
import nl.visi_1_1a.interaction_schema.UserDefinedTypeTypeRef;
import nl.visi_1_1a.interaction_schema.VisiXMLVISISystematics;

public class Loader {
	public void load(File interactionSchema, File interactionFramework) {
		Store store = Store.getStore();
		store.clear();

		VisiXMLVISISystematics visiXMLVISISystematics = null;
		try {
			visiXMLVISISystematics = unmarshal(VisiXMLVISISystematics.class, new FileInputStream(interactionFramework));
			List<ElementType> list = visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition();
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

	@SuppressWarnings("unchecked")
	public <T> T unmarshal(Class<T> docClass, InputStream inputStream) throws JAXBException {
		String packageName = docClass.getPackage().getName();
		JAXBContext jc = JAXBContext.newInstance(packageName);
		Unmarshaller u = jc.createUnmarshaller();
		T doc = (T) u.unmarshal(inputStream);
		return doc;
	}

	private void loadChildren(ElementType et) {
		Store.ElementTypeType type = Store.ElementTypeType.valueOf(et.getClass().getSimpleName());
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
			Store.getStore().put(id, tt);
			loadChildren((ElementType) tt);
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

	private void loadComplexElementType(ElementType et) {
		List<Object> list;
		ComplexElementTypeType complexElement = (ComplexElementTypeType) et;
		ComplexElementTypeType.ComplexElements complexElements = complexElement.getComplexElements();
		if (complexElements != null) {
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
		}
		ComplexElementTypeType.SimpleElements simpleElements = complexElement.getSimpleElements();
		if (simpleElements != null) {
			list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
			filter(list, SimpleElementTypeType.class, SimpleElementTypeTypeRef.class);
		}
	}

	private void loadElementConditionType(ElementType et) {
		// ElementConditionType elementCondition = (ElementConditionType) et;
		/*
		 * ElementConditionType.ComplexElement ecComplexElement =
		 * elementCondition.getComplexElement(); if (ecComplexElement != null) {
		 * ComplexElementTypeType complexElementTypeType =
		 * ecComplexElement.getComplexElementType(); ComplexElementTypeTypeRef
		 * complexElementTypeTypeRef =
		 * ecComplexElement.getComplexElementTypeRef();
		 * filter(complexElementTypeType != null ? complexElementTypeType :
		 * complexElementTypeTypeRef, ComplexElementTypeType.class,
		 * ComplexElementTypeTypeRef.class); }
		 */
		/*
		 * MessageInTransaction mitt =
		 * elementCondition.getMessageInTransaction(); if (mitt != null) {
		 * MessageInTransactionTypeType messageInTransactionType =
		 * mitt.getMessageInTransactionType(); MessageInTransactionTypeTypeRef
		 * messageInTransactionTypeRef = mitt.getMessageInTransactionTypeRef();
		 * filter(messageInTransactionType != null ? messageInTransactionType :
		 * messageInTransactionTypeRef, MessageInTransactionTypeType.class,
		 * MessageInTransactionTypeTypeRef.class); }
		 */
		/*
		 * SimpleElement se = elementCondition.getSimpleElement(); if (se !=
		 * null) { SimpleElementTypeType simpleElementTypeType =
		 * se.getSimpleElementType(); SimpleElementTypeTypeRef
		 * simpleElementTypeTypeRef = se.getSimpleElementTypeRef();
		 * filter(simpleElementTypeType != null ? simpleElementTypeType :
		 * simpleElementTypeTypeRef, SimpleElementTypeType.class,
		 * SimpleElementTypeTypeRef.class); }
		 */
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
		MessageInTransactionTypeType.Message mittMessage = messageInTransaction.getMessage();
		if (mittMessage != null) {
			MessageTypeType messageTypeType = mittMessage.getMessageType();
			MessageTypeTypeRef messageTypeTypeRef = mittMessage.getMessageTypeRef();
			filter(messageTypeType != null ? messageTypeType : messageTypeTypeRef, MessageTypeType.class,
					MessageTypeTypeRef.class);
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

	private void loadMessageType(ElementType et) {
		List<Object> list;
		MessageTypeType message = (MessageTypeType) et;
		MessageTypeType.ComplexElements messageComplexElements = message.getComplexElements();
		if (messageComplexElements != null) {
			list = messageComplexElements.getComplexElementTypeOrComplexElementTypeRef();
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

	private void loadPersonType(ElementType et) {
		List<Object> list;
		PersonTypeType person = (PersonTypeType) et;
		PersonTypeType.ComplexElements personComplexElements = person.getComplexElements();
		if (personComplexElements != null) {
			list = personComplexElements.getComplexElementTypeOrComplexElementTypeRef();
			filter(list, ComplexElementTypeType.class, ComplexElementTypeTypeRef.class);
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

	public static void main(String[] args) {
		Loader loader = new Loader();
		// loader.load(new File("lib" + File.separator + "_3_1_1_3.xsd"), new
		// File(
		// "raamwerk-van-N302-Beheer-en-Onderhoudsfase.xml"));
		loader.load(new File("lib" + File.separator + "_3_1_1_3.xsd"), new File("v2_2Example_framework2.xml"));
		try {
			PrintStream stream = new PrintStream(new File("another_test.xml"));
			Transform.getTransform().transform(stream);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
