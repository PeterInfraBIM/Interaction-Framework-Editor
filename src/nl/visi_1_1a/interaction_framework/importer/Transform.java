package nl.visi_1_1a.interaction_framework.importer;

import java.io.PrintStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_schema.ObjectFactory;
import nl.visi_1_1a.interaction_schema.AppendixTypeType;
import nl.visi_1_1a.interaction_schema.ComplexElementTypeType;
import nl.visi_1_1a.interaction_schema.ComplexElementTypeTypeRef;
import nl.visi_1_1a.interaction_schema.ElementConditionType;
import nl.visi_1_1a.interaction_schema.ElementType;
import nl.visi_1_1a.interaction_schema.GroupTypeType;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Group;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Message;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Previous;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.Transaction;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType.TransactionPhase;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeTypeRef;
import nl.visi_1_1a.interaction_schema.MessageTypeType;
import nl.visi_1_1a.interaction_schema.OrganisationTypeType;
import nl.visi_1_1a.interaction_schema.PersonTypeType;
import nl.visi_1_1a.interaction_schema.ProjectTypeType;
import nl.visi_1_1a.interaction_schema.RoleTypeType;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeType;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeType.UserDefinedType;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeTypeRef;
import nl.visi_1_1a.interaction_schema.TransactionPhaseTypeType;
import nl.visi_1_1a.interaction_schema.TransactionTypeType;
import nl.visi_1_1a.interaction_schema.TransactionTypeType.Executor;
import nl.visi_1_1a.interaction_schema.TransactionTypeType.Initiator;
import nl.visi_1_1a.interaction_schema.TransactionTypeTypeRef;
import nl.visi_1_1a.interaction_schema.UserDefinedTypeType;

public class Transform {
	protected static final GregorianCalendar gcal = new GregorianCalendar();

	private Transform() {
	}

	private static Transform singleton;

	public static Transform getTransform() {
		if (singleton == null) {
			singleton = new Transform();
		}
		return singleton;
	}

	public void transform(PrintStream out) throws JAXBException {
		nl.visi.interaction_schema.ObjectFactory objectFactory_12 = new nl.visi.interaction_schema.ObjectFactory();
		nl.visi.interaction_schema.VisiXMLVISISystematics visiXMLVISISystematics = objectFactory_12
				.createVisiXMLVISISystematics();
		Store store = Store.getStore();
		Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable = new Hashtable<String, nl.visi.interaction_schema.ElementType>();
		for (Store.ElementTypeType et : Store.ElementTypeType.values()) {
			List<?> elements = store.getElements(et.getElementClass());
			for (Object object : elements) {
				nl.visi.interaction_schema.ElementType elementType = transformAttributes(objectFactory_12,
						visiXMLVISISystematics, object);
				mappingTable.put(elementType.getId(), elementType);
			}
		}
		for (Store.ElementTypeType et : Store.ElementTypeType.values()) {
			List<?> elements = store.getElements(et.getElementClass());
			for (Object object : elements) {
				transformRelations(objectFactory_12, object, mappingTable);
			}
		}
		JAXBContext jc = JAXBContext.newInstance("nl.visi.interaction_schema");
		Marshaller m = jc.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);
		m.marshal(visiXMLVISISystematics, out);
		out.close();
	}

	private void transformRelations(nl.visi.interaction_schema.ObjectFactory objectFactory_12, Object object,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable) {
		String id = ((ElementType) object).getId();
		nl.visi.interaction_schema.ElementType elementType = mappingTable.get(id);
		String simpleName = object.getClass().getSimpleName();
		System.out.println(simpleName);
		switch (Store.ElementTypeType.valueOf(simpleName)) {
		case AppendixTypeType:
			AppendixTypeType appendixType_11 = (AppendixTypeType) object;
			transformAppendixType(objectFactory_12, mappingTable, elementType, appendixType_11);
			break;
		case ComplexElementTypeType:
			ComplexElementTypeType complexElementType_11 = (ComplexElementTypeType) object;
			transformComplexElementType(objectFactory_12, mappingTable, elementType, complexElementType_11);
			break;
		case ElementConditionType:
			ElementConditionType elementCondition_11 = (ElementConditionType) object;
			transformElementCondition(objectFactory_12, mappingTable, elementType, elementCondition_11);
			break;
		case GroupTypeType:
			break;
		case MessageInTransactionTypeType:
			MessageInTransactionTypeType mitt_11 = (MessageInTransactionTypeType) object;
			transformMessageInTransactionType(objectFactory_12, mappingTable, elementType, mitt_11);
			break;
		case MessageTypeType:
			MessageTypeType messageType_11 = (MessageTypeType) object;
			transformMessageType(objectFactory_12, mappingTable, elementType, messageType_11);
			break;
		case OrganisationTypeType:
			OrganisationTypeType organisationType_11 = (OrganisationTypeType) object;
			transformOrganisationType(objectFactory_12, mappingTable, elementType, organisationType_11);
			break;
		case PersonTypeType:
			PersonTypeType personType_11 = (PersonTypeType) object;
			transformPersonType(objectFactory_12, mappingTable, elementType, personType_11);
			break;
		case ProjectTypeType:
			ProjectTypeType projectType_11 = (ProjectTypeType) object;
			transformProjectType(objectFactory_12, mappingTable, elementType, projectType_11);
			break;
		case RoleTypeType:
			break;
		case SimpleElementTypeType:
			SimpleElementTypeType simpleElementType_11 = (SimpleElementTypeType) object;
			transformSimpleElementType(objectFactory_12, mappingTable, elementType, simpleElementType_11);
			break;
		case TransactionPhaseTypeType:
			break;
		case TransactionTypeType:
			TransactionTypeType transactionType_11 = (TransactionTypeType) object;
			transformTransaction(objectFactory_12, mappingTable, elementType, transactionType_11);
			break;
		case UserDefinedTypeType:
			break;
		}
	}

	private void transformElementCondition(ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, ElementConditionType elementCondition_11) {
		ElementConditionType.Element element_11 = elementCondition_11.getElement();
		if (element_11 != null) {
			SimpleElementTypeType simpleElementType_11 = element_11.getSimpleElementType();
			if (simpleElementType_11 == null) {
				simpleElementType_11 = (SimpleElementTypeType) element_11.getSimpleElementTypeRef().getIdref();
			}
			if (simpleElementType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(simpleElementType_11.getId());
				nl.visi.interaction_schema.ElementConditionType.SimpleElement simpleElementType_12 = objectFactory_12
						.createElementConditionTypeSimpleElement();
				nl.visi.interaction_schema.SimpleElementTypeTypeRef ref_12 = objectFactory_12
						.createSimpleElementTypeTypeRef();
				ref_12.setIdref(elementType_12);
				simpleElementType_12.setSimpleElementTypeRef(ref_12);
				((nl.visi.interaction_schema.ElementConditionType) elementType).setSimpleElement(simpleElementType_12);
			}
		}
	}

	private void transformMessageType(ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, MessageTypeType messageType_11) {
		MessageTypeType.ComplexElements complexElements_11 = messageType_11.getComplexElements();
		if (complexElements_11 != null) {
			List<Object> ceList_11 = complexElements_11.getComplexElementTypeOrComplexElementTypeRef();
			for (Object ceObject_11 : ceList_11) {
				ComplexElementTypeType complexElement_11 = null;
				if (ceObject_11 instanceof ComplexElementTypeType) {
					complexElement_11 = (ComplexElementTypeType) ceObject_11;
				} else if (ceObject_11 instanceof ComplexElementTypeTypeRef) {
					complexElement_11 = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ceObject_11).getIdref();
				}
				if (complexElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(complexElement_11.getId());
					nl.visi.interaction_schema.MessageTypeType.ComplexElements complexElements_12 = ((nl.visi.interaction_schema.MessageTypeType) elementType)
							.getComplexElements();
					if (complexElements_12 == null) {
						complexElements_12 = objectFactory_12.createMessageTypeTypeComplexElements();
						((nl.visi.interaction_schema.MessageTypeType) elementType)
								.setComplexElements(complexElements_12);
					}
					nl.visi.interaction_schema.ComplexElementTypeTypeRef ref_12 = objectFactory_12
							.createComplexElementTypeTypeRef();
					ref_12.setIdref(elementType_12);
					complexElements_12.getComplexElementTypeOrComplexElementTypeRef().add(ref_12);
				}
			}
		}
	}

	private void transformComplexElementType(ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, ComplexElementTypeType complexElementType_11) {
		ComplexElementTypeType.ComplexElements complexElements_11 = complexElementType_11.getComplexElements();
		if (complexElements_11 != null) {
			List<Object> ceList_11 = complexElements_11.getComplexElementTypeOrComplexElementTypeRef();
			for (Object ceObject_11 : ceList_11) {
				ComplexElementTypeType complexElement_11 = null;
				if (ceObject_11 instanceof ComplexElementTypeType) {
					complexElement_11 = (ComplexElementTypeType) ceObject_11;
				} else if (ceObject_11 instanceof ComplexElementTypeTypeRef) {
					complexElement_11 = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ceObject_11).getIdref();
				}
				if (complexElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(complexElement_11.getId());
					nl.visi.interaction_schema.ComplexElementTypeType.ComplexElements complexElements_12 = ((nl.visi.interaction_schema.ComplexElementTypeType) elementType)
							.getComplexElements();
					if (complexElements_12 == null) {
						complexElements_12 = objectFactory_12.createComplexElementTypeTypeComplexElements();
						((nl.visi.interaction_schema.ComplexElementTypeType) elementType)
								.setComplexElements(complexElements_12);
					}
					nl.visi.interaction_schema.ComplexElementTypeTypeRef ref_12 = objectFactory_12
							.createComplexElementTypeTypeRef();
					ref_12.setIdref(elementType_12);
					complexElements_12.getComplexElementTypeOrComplexElementTypeRef().add(ref_12);
				}
			}
		}

		ComplexElementTypeType.SimpleElements simpleElements_11 = complexElementType_11.getSimpleElements();
		if (simpleElements_11 != null) {
			List<Object> seList_11 = simpleElements_11.getSimpleElementTypeOrSimpleElementTypeRef();
			for (Object seObject_11 : seList_11) {
				SimpleElementTypeType simpleElement_11 = null;
				if (seObject_11 instanceof SimpleElementTypeType) {
					simpleElement_11 = (SimpleElementTypeType) seObject_11;
				} else if (seObject_11 instanceof SimpleElementTypeTypeRef) {
					simpleElement_11 = (SimpleElementTypeType) ((SimpleElementTypeTypeRef) seObject_11).getIdref();
				}
				if (simpleElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(simpleElement_11.getId());
					nl.visi.interaction_schema.ComplexElementTypeType.SimpleElements simpleElements_12 = ((nl.visi.interaction_schema.ComplexElementTypeType) elementType)
							.getSimpleElements();
					if (simpleElements_12 == null) {
						simpleElements_12 = objectFactory_12.createComplexElementTypeTypeSimpleElements();
						((nl.visi.interaction_schema.ComplexElementTypeType) elementType)
								.setSimpleElements(simpleElements_12);
					}
					nl.visi.interaction_schema.SimpleElementTypeTypeRef ref_12 = objectFactory_12
							.createSimpleElementTypeTypeRef();
					ref_12.setIdref(elementType_12);
					simpleElements_12.getSimpleElementTypeOrSimpleElementTypeRef().add(ref_12);
				}
			}
		}

	}

	private void transformAppendixType(ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, AppendixTypeType appendixType_11) {
		AppendixTypeType.ComplexElements complexElements_11 = appendixType_11.getComplexElements();
		if (complexElements_11 != null) {
			List<Object> ceList_11 = complexElements_11.getComplexElementTypeOrComplexElementTypeRef();
			for (Object ceObject_11 : ceList_11) {
				ComplexElementTypeType complexElement_11 = null;
				if (ceObject_11 instanceof ComplexElementTypeType) {
					complexElement_11 = (ComplexElementTypeType) ceObject_11;
				} else if (ceObject_11 instanceof ComplexElementTypeTypeRef) {
					complexElement_11 = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ceObject_11).getIdref();
				}
				if (complexElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(complexElement_11.getId());
					nl.visi.interaction_schema.AppendixTypeType.ComplexElements complexElements_12 = ((nl.visi.interaction_schema.AppendixTypeType) elementType)
							.getComplexElements();
					if (complexElements_12 == null) {
						complexElements_12 = objectFactory_12.createAppendixTypeTypeComplexElements();
						((nl.visi.interaction_schema.AppendixTypeType) elementType)
								.setComplexElements(complexElements_12);
					}
					nl.visi.interaction_schema.ComplexElementTypeTypeRef ref_12 = objectFactory_12
							.createComplexElementTypeTypeRef();
					ref_12.setIdref(elementType_12);
					complexElements_12.getComplexElementTypeOrComplexElementTypeRef().add(ref_12);
				}
			}
		}
	}

	private void transformOrganisationType(ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, OrganisationTypeType organisationType_11) {
		OrganisationTypeType.ComplexElements complexElements_11 = organisationType_11.getComplexElements();
		if (complexElements_11 != null) {
			List<Object> ceList_11 = complexElements_11.getComplexElementTypeOrComplexElementTypeRef();
			for (Object ceObject_11 : ceList_11) {
				ComplexElementTypeType complexElement_11 = null;
				if (ceObject_11 instanceof ComplexElementTypeType) {
					complexElement_11 = (ComplexElementTypeType) ceObject_11;
				} else if (ceObject_11 instanceof ComplexElementTypeTypeRef) {
					complexElement_11 = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ceObject_11).getIdref();
				}
				if (complexElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(complexElement_11.getId());
					nl.visi.interaction_schema.OrganisationTypeType.ComplexElements complexElements_12 = ((nl.visi.interaction_schema.OrganisationTypeType) elementType)
							.getComplexElements();
					if (complexElements_12 == null) {
						complexElements_12 = objectFactory_12.createOrganisationTypeTypeComplexElements();
						((nl.visi.interaction_schema.OrganisationTypeType) elementType)
								.setComplexElements(complexElements_12);
					}
					nl.visi.interaction_schema.ComplexElementTypeTypeRef ref_12 = objectFactory_12
							.createComplexElementTypeTypeRef();
					ref_12.setIdref(elementType_12);
					complexElements_12.getComplexElementTypeOrComplexElementTypeRef().add(ref_12);
				}
			}
		}
	}

	private void transformPersonType(ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, PersonTypeType personType_11) {
		PersonTypeType.ComplexElements complexElements_11 = personType_11.getComplexElements();
		if (complexElements_11 != null) {
			List<Object> ceList_11 = complexElements_11.getComplexElementTypeOrComplexElementTypeRef();
			for (Object ceObject_11 : ceList_11) {
				ComplexElementTypeType complexElement_11 = null;
				if (ceObject_11 instanceof ComplexElementTypeType) {
					complexElement_11 = (ComplexElementTypeType) ceObject_11;
				} else if (ceObject_11 instanceof ComplexElementTypeTypeRef) {
					complexElement_11 = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ceObject_11).getIdref();
				}
				if (complexElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(complexElement_11.getId());
					nl.visi.interaction_schema.PersonTypeType.ComplexElements complexElements_12 = ((nl.visi.interaction_schema.PersonTypeType) elementType)
							.getComplexElements();
					if (complexElements_12 == null) {
						complexElements_12 = objectFactory_12.createPersonTypeTypeComplexElements();
						((nl.visi.interaction_schema.PersonTypeType) elementType)
								.setComplexElements(complexElements_12);
					}
					nl.visi.interaction_schema.ComplexElementTypeTypeRef ref_12 = objectFactory_12
							.createComplexElementTypeTypeRef();
					ref_12.setIdref(elementType_12);
					complexElements_12.getComplexElementTypeOrComplexElementTypeRef().add(ref_12);
				}
			}
		}
	}

	private void transformProjectType(nl.visi.interaction_schema.ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, ProjectTypeType projectType_11) {
		ProjectTypeType.ComplexElements complexElements_11 = projectType_11.getComplexElements();
		if (complexElements_11 != null) {
			List<Object> ceList_11 = complexElements_11.getComplexElementTypeOrComplexElementTypeRef();
			for (Object ceObject_11 : ceList_11) {
				ComplexElementTypeType complexElement_11 = null;
				if (ceObject_11 instanceof ComplexElementTypeType) {
					complexElement_11 = (ComplexElementTypeType) ceObject_11;
				} else if (ceObject_11 instanceof ComplexElementTypeTypeRef) {
					complexElement_11 = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ceObject_11).getIdref();
				}
				if (complexElement_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(complexElement_11.getId());
					nl.visi.interaction_schema.ProjectTypeType.ComplexElements complexElements_12 = ((nl.visi.interaction_schema.ProjectTypeType) elementType)
							.getComplexElements();
					if (complexElements_12 == null) {
						complexElements_12 = objectFactory_12.createProjectTypeTypeComplexElements();
						((nl.visi.interaction_schema.ProjectTypeType) elementType)
								.setComplexElements(complexElements_12);
					}
					nl.visi.interaction_schema.OrganisationTypeTypeRef ref_12 = objectFactory_12
							.createOrganisationTypeTypeRef();
					ref_12.setIdref(elementType_12);
					complexElements_12.getComplexElementTypeOrComplexElementTypeRef().add(ref_12);
				}
			}
		}
	}

	private void transformTransaction(nl.visi.interaction_schema.ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, TransactionTypeType transactionType_11) {
		Initiator initiator_11 = transactionType_11.getInitiator();
		if (initiator_11 != null) {
			RoleTypeType roleType_11 = initiator_11.getRoleType();
			if (roleType_11 == null) {
				roleType_11 = (RoleTypeType) initiator_11.getRoleTypeRef().getIdref();
			}
			if (roleType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(roleType_11.getId());
				nl.visi.interaction_schema.TransactionTypeType.Initiator initiator_12 = objectFactory_12
						.createTransactionTypeTypeInitiator();
				nl.visi.interaction_schema.RoleTypeTypeRef ref_12 = objectFactory_12.createRoleTypeTypeRef();
				ref_12.setIdref(elementType_12);
				initiator_12.setRoleTypeRef(ref_12);
				((nl.visi.interaction_schema.TransactionTypeType) elementType).setInitiator(initiator_12);
			}
		}
		Executor executor_11 = transactionType_11.getExecutor();
		if (executor_11 != null) {
			RoleTypeType roleType_11 = executor_11.getRoleType();
			if (roleType_11 == null) {
				roleType_11 = (RoleTypeType) executor_11.getRoleTypeRef().getIdref();
			}
			if (roleType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(roleType_11.getId());
				nl.visi.interaction_schema.TransactionTypeType.Executor executor_12 = objectFactory_12
						.createTransactionTypeTypeExecutor();
				nl.visi.interaction_schema.RoleTypeTypeRef ref_12 = objectFactory_12.createRoleTypeTypeRef();
				ref_12.setIdref(elementType_12);
				executor_12.setRoleTypeRef(ref_12);
				((nl.visi.interaction_schema.TransactionTypeType) elementType).setExecutor(executor_12);
			}
		}

		// Transform subtransactions
		TransactionTypeType.SubTransactions subtransactions_11 = transactionType_11.getSubTransactions();
		if (subtransactions_11 != null) {
			List<Object> subtransactionsList_11 = subtransactions_11.getTransactionTypeOrTransactionTypeRef();
			for (Object transaction_11 : subtransactionsList_11) {
				TransactionTypeType subtransaction_11 = null;
				if (transaction_11 instanceof TransactionTypeType) {
					subtransaction_11 = (TransactionTypeType) transaction_11;
				} else if (transaction_11 instanceof TransactionTypeTypeRef) {
					subtransaction_11 = (TransactionTypeType) ((TransactionTypeTypeRef) transaction_11).getIdref();
				}
				if (subtransaction_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(subtransaction_11.getId());
					nl.visi.interaction_schema.TransactionTypeType.SubTransactions subtransactionsList_12 = ((nl.visi.interaction_schema.TransactionTypeType) elementType)
							.getSubTransactions();
					if (subtransactionsList_12 == null) {
						subtransactionsList_12 = objectFactory_12.createTransactionTypeTypeSubTransactions();
						((nl.visi.interaction_schema.TransactionTypeType) elementType)
								.setSubTransactions(subtransactionsList_12);
					}
					nl.visi.interaction_schema.TransactionTypeTypeRef ref_12 = objectFactory_12
							.createTransactionTypeTypeRef();
					ref_12.setIdref(elementType_12);

					subtransactionsList_12.getTransactionTypeOrTransactionTypeRef().add(ref_12);
				}
			}
		}

	}

	private void transformSimpleElementType(nl.visi.interaction_schema.ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, SimpleElementTypeType simpleElementType_11) {
		UserDefinedType userDefinedType_11 = simpleElementType_11.getUserDefinedType();
		if (userDefinedType_11 != null) {
			UserDefinedTypeType userDefinedTypeType_11 = userDefinedType_11.getUserDefinedType();
			if (userDefinedTypeType_11 == null) {
				userDefinedTypeType_11 = (UserDefinedTypeType) userDefinedType_11.getUserDefinedTypeRef().getIdref();
			}
			if (userDefinedTypeType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable
						.get(userDefinedTypeType_11.getId());
				nl.visi.interaction_schema.SimpleElementTypeType.UserDefinedType userDefinedType_12 = objectFactory_12
						.createSimpleElementTypeTypeUserDefinedType();
				nl.visi.interaction_schema.UserDefinedTypeTypeRef ref_12 = objectFactory_12
						.createUserDefinedTypeTypeRef();
				ref_12.setIdref(elementType_12);
				userDefinedType_12.setUserDefinedTypeRef(ref_12);
				((nl.visi.interaction_schema.SimpleElementTypeType) elementType).setUserDefinedType(userDefinedType_12);
			}
		}
	}

	private void transformMessageInTransactionType(nl.visi.interaction_schema.ObjectFactory objectFactory_12,
			Hashtable<String, nl.visi.interaction_schema.ElementType> mappingTable,
			nl.visi.interaction_schema.ElementType elementType, MessageInTransactionTypeType mitt_11) {

		// Transform message
		Message message_11 = mitt_11.getMessage();
		if (message_11 != null) {
			MessageTypeType messageType_11 = message_11.getMessageType();
			if (messageType_11 == null) {
				messageType_11 = (MessageTypeType) message_11.getMessageTypeRef().getIdref();
			}
			if (messageType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(messageType_11.getId());
				nl.visi.interaction_schema.MessageInTransactionTypeType.Message message_12 = objectFactory_12
						.createMessageInTransactionTypeTypeMessage();
				nl.visi.interaction_schema.MessageTypeTypeRef ref_12 = objectFactory_12.createMessageTypeTypeRef();
				ref_12.setIdref(elementType_12);
				message_12.setMessageTypeRef(ref_12);
				((nl.visi.interaction_schema.MessageInTransactionTypeType) elementType).setMessage(message_12);
			}
		}

		// Transform previous
		Previous previous_11 = mitt_11.getPrevious();
		if (previous_11 != null) {
			List<Object> previousList_11 = previous_11.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			for (Object previousObject_11 : previousList_11) {
				MessageInTransactionTypeType previousMitt_11 = null;
				if (previousObject_11 instanceof MessageInTransactionTypeType) {
					previousMitt_11 = (MessageInTransactionTypeType) previousObject_11;
				} else if (previousObject_11 instanceof MessageInTransactionTypeTypeRef) {
					previousMitt_11 = (MessageInTransactionTypeType) ((MessageInTransactionTypeTypeRef) previousObject_11)
							.getIdref();
				}
				if (previousMitt_11 != null) {
					nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(previousMitt_11.getId());
					nl.visi.interaction_schema.MessageInTransactionTypeType.Previous previous_12 = ((nl.visi.interaction_schema.MessageInTransactionTypeType) elementType)
							.getPrevious();
					if (previous_12 == null) {
						previous_12 = objectFactory_12.createMessageInTransactionTypeTypePrevious();
						((nl.visi.interaction_schema.MessageInTransactionTypeType) elementType)
								.setPrevious(previous_12);
					}
					nl.visi.interaction_schema.MessageInTransactionTypeTypeRef ref_12 = objectFactory_12
							.createMessageInTransactionTypeTypeRef();
					ref_12.setIdref(elementType_12);

					previous_12.getMessageInTransactionTypeOrMessageInTransactionTypeRef().add(ref_12);
				}
			}
		}

		// Transform transaction phase
		TransactionPhase transactionPhase_11 = mitt_11.getTransactionPhase();
		if (transactionPhase_11 != null) {
			TransactionPhaseTypeType transactionPhaseType_11 = transactionPhase_11.getTransactionPhaseType();
			if (transactionPhaseType_11 == null) {
				transactionPhaseType_11 = (TransactionPhaseTypeType) transactionPhase_11.getTransactionPhaseTypeRef()
						.getIdref();
			}
			if (transactionPhaseType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(transactionPhaseType_11
						.getId());
				nl.visi.interaction_schema.MessageInTransactionTypeType.TransactionPhase transactionPhase_12 = objectFactory_12
						.createMessageInTransactionTypeTypeTransactionPhase();
				nl.visi.interaction_schema.TransactionPhaseTypeTypeRef ref_12 = objectFactory_12
						.createTransactionPhaseTypeTypeRef();
				ref_12.setIdref(elementType_12);
				transactionPhase_12.setTransactionPhaseTypeRef(ref_12);
				((nl.visi.interaction_schema.MessageInTransactionTypeType) elementType)
						.setTransactionPhase(transactionPhase_12);
			}
		}

		// Transform transaction
		Transaction transaction_11 = mitt_11.getTransaction();
		if (transaction_11 != null) {
			TransactionTypeType transactionType_11 = transaction_11.getTransactionType();
			if (transactionType_11 == null) {
				transactionType_11 = (TransactionTypeType) transaction_11.getTransactionTypeRef().getIdref();
			}
			if (transactionType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(transactionType_11.getId());
				nl.visi.interaction_schema.MessageInTransactionTypeType.Transaction transaction_12 = objectFactory_12
						.createMessageInTransactionTypeTypeTransaction();
				nl.visi.interaction_schema.TransactionTypeTypeRef ref_12 = objectFactory_12
						.createTransactionTypeTypeRef();
				ref_12.setIdref(elementType_12);
				transaction_12.setTransactionTypeRef(ref_12);
				((nl.visi.interaction_schema.MessageInTransactionTypeType) elementType).setTransaction(transaction_12);
			}
		}

		// Transform group
		Group group_11 = mitt_11.getGroup();
		if (group_11 != null) {
			GroupTypeType groupType_11 = group_11.getGroupType();
			if (groupType_11 == null) {
				groupType_11 = (GroupTypeType) group_11.getGroupTypeRef().getIdref();
			}
			if (groupType_11 != null) {
				nl.visi.interaction_schema.ElementType elementType_12 = mappingTable.get(groupType_11.getId());
				nl.visi.interaction_schema.MessageInTransactionTypeType.Group group_12 = objectFactory_12
						.createMessageInTransactionTypeTypeGroup();
				nl.visi.interaction_schema.GroupTypeTypeRef ref_12 = objectFactory_12.createGroupTypeTypeRef();
				ref_12.setIdref(elementType_12);
				group_12.setGroupTypeRef(ref_12);
				((nl.visi.interaction_schema.MessageInTransactionTypeType) elementType).setGroup(group_12);
			}
		}

	}

	private nl.visi.interaction_schema.ElementType transformAttributes(
			nl.visi.interaction_schema.ObjectFactory objectFactory_12,
			nl.visi.interaction_schema.VisiXMLVISISystematics visiXMLVISISystematics, Object object) {
		nl.visi.interaction_schema.ElementType elementType = null;
		String simpleName = object.getClass().getSimpleName();
		System.out.println(simpleName);
		switch (Store.ElementTypeType.valueOf(simpleName)) {
		case AppendixTypeType:
			AppendixTypeType appendixType_11 = (AppendixTypeType) object;
			nl.visi.interaction_schema.AppendixTypeType appendixType_12 = objectFactory_12.createAppendixTypeType();

			appendixType_12.setCategory(appendixType_11.getCategory());
			appendixType_12.setCode(appendixType_11.getCode());
			appendixType_12.setDateLamu(getDate(appendixType_11.getDateLamu()));
			appendixType_12.setDescription(appendixType_11.getDescription());
			appendixType_12.setEndDate(getDate(appendixType_11.getEndDate()));
			appendixType_12.setHelpInfo(appendixType_11.getHelpInfo());
			appendixType_12.setId(appendixType_11.getId());
			appendixType_12.setLanguage(appendixType_11.getLanguage());
			appendixType_12.setStartDate(getDate(appendixType_11.getStartDate()));
			appendixType_12.setState(appendixType_11.getState());
			appendixType_12.setUserLamu(appendixType_11.getUserLamu());

			elementType = appendixType_12;
			break;
		case ComplexElementTypeType:
			ComplexElementTypeType complexElementType_11 = (ComplexElementTypeType) object;
			nl.visi.interaction_schema.ComplexElementTypeType complexElementType_12 = objectFactory_12
					.createComplexElementTypeType();

			complexElementType_12.setCategory(complexElementType_11.getCategory());
			complexElementType_12.setDateLamu(getDate(complexElementType_11.getDateLamu()));
			complexElementType_12.setDescription(complexElementType_11.getDescription());
			complexElementType_12.setEndDate(getDate(complexElementType_11.getEndDate()));
			complexElementType_12.setHelpInfo(complexElementType_11.getHelpInfo());
			complexElementType_12.setId(complexElementType_11.getId());
			complexElementType_12.setLanguage(complexElementType_11.getLanguage());
			complexElementType_12.setStartDate(getDate(complexElementType_11.getStartDate()));
			complexElementType_12.setState(complexElementType_11.getState());
			complexElementType_12.setUserLamu(complexElementType_11.getUserLamu());

			elementType = complexElementType_12;
			break;
		case ElementConditionType:
			ElementConditionType elementConditionType_11 = (ElementConditionType) object;
			nl.visi.interaction_schema.GroupTypeType elementConditionType_12 = objectFactory_12.createGroupTypeType();

			elementConditionType_12.setDescription(elementConditionType_11.getDescription());
			elementConditionType_12.setHelpInfo(elementConditionType_11.getHelpInfo());
			elementConditionType_12.setId(elementConditionType_11.getId());

			elementType = elementConditionType_12;
			break;
		case GroupTypeType:
			GroupTypeType groupType_11 = (GroupTypeType) object;
			nl.visi.interaction_schema.GroupTypeType groupType_12 = objectFactory_12.createGroupTypeType();

			groupType_12.setCategory(groupType_11.getCategory());
			groupType_12.setDateLamu(getDate(groupType_11.getDateLamu()));
			groupType_12.setDescription(groupType_11.getDescription());
			groupType_12.setEndDate(getDate(groupType_11.getEndDate()));
			groupType_12.setHelpInfo(groupType_11.getHelpInfo());
			groupType_12.setId(groupType_11.getId());
			groupType_12.setLanguage(groupType_11.getLanguage());
			groupType_12.setStartDate(getDate(groupType_11.getStartDate()));
			groupType_12.setState(groupType_11.getState());
			groupType_12.setUserLamu(groupType_11.getUserLamu());

			elementType = groupType_12;
			break;
		case MessageInTransactionTypeType:
			MessageInTransactionTypeType mitt_11 = (MessageInTransactionTypeType) object;
			nl.visi.interaction_schema.MessageInTransactionTypeType mitt_12 = objectFactory_12
					.createMessageInTransactionTypeType();

			mitt_12.setDateLamu(getDate(mitt_11.getDateLamu()));
			mitt_12.setId(mitt_11.getId());
			mitt_12.setInitiatorToExecutor(mitt_11.isInitiatorToExecutor());
			mitt_12.setReceived(mitt_11.isReceived());
			mitt_12.setRequiredNotify(mitt_11.getRequiredNotify());
			mitt_12.setSend(mitt_11.isSend());
			mitt_12.setState(mitt_11.getState());
			mitt_12.setUserLamu(mitt_11.getUserLamu());

			elementType = mitt_12;
			break;
		case MessageTypeType:
			MessageTypeType messageType_11 = (MessageTypeType) object;
			nl.visi.interaction_schema.MessageTypeType messageType_12 = objectFactory_12.createMessageTypeType();

			messageType_12.setCategory(messageType_11.getCategory());
			messageType_12.setCode(messageType_11.getCode());
			messageType_12.setDateLamu(getDate(messageType_11.getDateLamu()));
			messageType_12.setDescription(messageType_11.getDescription());
			messageType_12.setEndDate(getDate(messageType_11.getEndDate()));
			messageType_12.setHelpInfo(messageType_11.getHelpInfo());
			messageType_12.setId(messageType_11.getId());
			messageType_12.setLanguage(messageType_11.getLanguage());
			messageType_12.setStartDate(getDate(messageType_11.getStartDate()));
			messageType_12.setState(messageType_11.getState());
			messageType_12.setUserLamu(messageType_11.getUserLamu());

			elementType = messageType_12;
			break;
		case OrganisationTypeType:
			OrganisationTypeType organisationType_11 = (OrganisationTypeType) object;
			nl.visi.interaction_schema.OrganisationTypeType organisationType_12 = objectFactory_12
					.createOrganisationTypeType();

			organisationType_12.setCategory(organisationType_11.getCategory());
			organisationType_12.setCode(organisationType_11.getCode());
			organisationType_12.setDateLamu(getDate(organisationType_11.getDateLamu()));
			organisationType_12.setDescription(organisationType_11.getDescription());
			organisationType_12.setEndDate(getDate(organisationType_11.getEndDate()));
			organisationType_12.setHelpInfo(organisationType_11.getHelpInfo());
			organisationType_12.setId(organisationType_11.getId());
			organisationType_12.setLanguage(organisationType_11.getLanguage());
			organisationType_12.setStartDate(getDate(organisationType_11.getStartDate()));
			organisationType_12.setState(organisationType_11.getState());
			organisationType_12.setUserLamu(organisationType_11.getUserLamu());

			elementType = organisationType_12;
			break;
		case PersonTypeType:
			PersonTypeType personType_11 = (PersonTypeType) object;
			nl.visi.interaction_schema.PersonTypeType personType_12 = objectFactory_12.createPersonTypeType();

			personType_12.setCategory(personType_11.getCategory());
			personType_12.setCode(personType_11.getCode());
			personType_12.setDateLamu(getDate(personType_11.getDateLamu()));
			personType_12.setDescription(personType_11.getDescription());
			personType_12.setEndDate(getDate(personType_11.getEndDate()));
			personType_12.setHelpInfo(personType_11.getHelpInfo());
			personType_12.setId(personType_11.getId());
			personType_12.setLanguage(personType_11.getLanguage());
			personType_12.setStartDate(getDate(personType_11.getStartDate()));
			personType_12.setState(personType_11.getState());
			personType_12.setUserLamu(personType_11.getUserLamu());

			elementType = personType_12;
			break;
		case ProjectTypeType:
			ProjectTypeType projectType_11 = (ProjectTypeType) object;
			nl.visi.interaction_schema.ProjectTypeType projectType_12 = objectFactory_12.createProjectTypeType();

			projectType_12.setCategory(projectType_11.getCategory());
			projectType_12.setCode(projectType_11.getCode());
			projectType_12.setDateLamu(getDate(projectType_11.getDateLamu()));
			projectType_12.setDescription(projectType_11.getDescription());
			projectType_12.setEndDate(getDate(projectType_11.getEndDate()));
			projectType_12.setHelpInfo(projectType_11.getHelpInfo());
			projectType_12.setId(projectType_11.getId());
			projectType_12.setLanguage(projectType_11.getLanguage());
			projectType_12.setStartDate(getDate(projectType_11.getStartDate()));
			projectType_12.setState(projectType_11.getState());
			projectType_12.setUserLamu(projectType_11.getUserLamu());

			elementType = projectType_12;
			break;
		case RoleTypeType:
			RoleTypeType roleType_11 = (RoleTypeType) object;
			nl.visi.interaction_schema.RoleTypeType roleType_12 = objectFactory_12.createRoleTypeType();

			roleType_12.setCategory(roleType_11.getCategory());
			roleType_12.setCode(roleType_11.getCode());
			roleType_12.setDateLamu(getDate(roleType_11.getDateLamu()));
			roleType_12.setDescription(roleType_11.getDescription());
			roleType_12.setEndDate(getDate(roleType_11.getEndDate()));
			roleType_12.setHelpInfo(roleType_11.getHelpInfo());
			roleType_12.setId(roleType_11.getId());
			roleType_12.setLanguage(roleType_11.getLanguage());
			roleType_12.setResponsibilityScope(roleType_11.getResponsibilityScope());
			roleType_12.setResponsibilityTask(roleType_11.getResponsibilityTask());
			roleType_12.setResponsibilitySupportTask(roleType_11.getResponsibilitySupportTask());
			roleType_12.setResponsibilityFeedback(roleType_11.getResponsibilityFeedback());
			roleType_12.setStartDate(getDate(roleType_11.getStartDate()));
			roleType_12.setState(roleType_11.getState());
			roleType_12.setUserLamu(roleType_11.getUserLamu());

			elementType = roleType_12;
			break;
		case SimpleElementTypeType:
			SimpleElementTypeType simpleElementType_11 = (SimpleElementTypeType) object;
			nl.visi.interaction_schema.SimpleElementTypeType simpleElementType_12 = objectFactory_12
					.createSimpleElementTypeType();

			simpleElementType_12.setCategory(simpleElementType_11.getCategory());
			simpleElementType_12.setDateLamu(getDate(simpleElementType_11.getDateLamu()));
			simpleElementType_12.setDescription(simpleElementType_11.getDescription());
			simpleElementType_12.setHelpInfo(simpleElementType_11.getHelpInfo());
			simpleElementType_12.setId(simpleElementType_11.getId());
			simpleElementType_12.setInterfaceType(simpleElementType_11.getInterfaceType());
			simpleElementType_12.setLanguage(simpleElementType_11.getLanguage());
			simpleElementType_12.setState(simpleElementType_11.getState());
			simpleElementType_12.setUserLamu(simpleElementType_11.getUserLamu());
			simpleElementType_12.setValueList(simpleElementType_11.getValueList());

			elementType = simpleElementType_12;
			break;
		case TransactionPhaseTypeType:
			TransactionPhaseTypeType transactionPhaseType_11 = (TransactionPhaseTypeType) object;
			nl.visi.interaction_schema.TransactionPhaseTypeType transactionPhaseType_12 = objectFactory_12
					.createTransactionPhaseTypeType();

			transactionPhaseType_12.setCategory(transactionPhaseType_11.getCategory());
			transactionPhaseType_12.setCode(transactionPhaseType_11.getCode());
			transactionPhaseType_12.setDateLamu(getDate(transactionPhaseType_11.getDateLamu()));
			transactionPhaseType_12.setDescription(transactionPhaseType_11.getDescription());
			transactionPhaseType_12.setEndDate(getDate(transactionPhaseType_11.getEndDate()));
			transactionPhaseType_12.setHelpInfo(transactionPhaseType_11.getHelpInfo());
			transactionPhaseType_12.setId(transactionPhaseType_11.getId());
			transactionPhaseType_12.setLanguage(transactionPhaseType_11.getLanguage());
			transactionPhaseType_12.setStartDate(getDate(transactionPhaseType_11.getStartDate()));
			transactionPhaseType_12.setState(transactionPhaseType_11.getState());
			transactionPhaseType_12.setUserLamu(transactionPhaseType_11.getUserLamu());

			elementType = transactionPhaseType_12;
			break;
		case TransactionTypeType:
			TransactionTypeType transactionType_11 = (TransactionTypeType) object;
			nl.visi.interaction_schema.TransactionTypeType transactionType_12 = objectFactory_12
					.createTransactionTypeType();

			transactionType_12.setBasePoint(transactionType_11.getBasePoint());
			transactionType_12.setCategory(transactionType_11.getCategory());
			transactionType_12.setCode(transactionType_11.getCode());
			transactionType_12.setDateLamu(getDate(transactionType_11.getDateLamu()));
			transactionType_12.setDescription(transactionType_11.getDescription());
			transactionType_12.setEndDate(getDate(transactionType_11.getEndDate()));
			transactionType_12.setHelpInfo(transactionType_11.getHelpInfo());
			transactionType_12.setId(transactionType_11.getId());
			transactionType_12.setLanguage(transactionType_11.getLanguage());
			transactionType_12.setResult(transactionType_11.getResult());
			transactionType_12.setStartDate(getDate(transactionType_11.getStartDate()));
			transactionType_12.setState(transactionType_11.getState());
			transactionType_12.setUserLamu(transactionType_11.getUserLamu());

			elementType = transactionType_12;
			break;
		case UserDefinedTypeType:
			UserDefinedTypeType userDefinedType_11 = (UserDefinedTypeType) object;
			nl.visi.interaction_schema.UserDefinedTypeType userDefinedType_12 = objectFactory_12
					.createUserDefinedTypeType();

			userDefinedType_12.setBaseType(userDefinedType_11.getBaseType());
			userDefinedType_12.setDateLamu(getDate(userDefinedType_11.getDateLamu()));
			userDefinedType_12.setDescription(userDefinedType_11.getDescription());
			userDefinedType_12.setHelpInfo(userDefinedType_11.getHelpInfo());
			userDefinedType_12.setId(userDefinedType_11.getId());
			userDefinedType_12.setLanguage(userDefinedType_11.getLanguage());
			userDefinedType_12.setState(userDefinedType_11.getState());
			userDefinedType_12.setUserLamu(userDefinedType_11.getUserLamu());
			userDefinedType_12.setXsdRestriction(userDefinedType_11.getXsdRestriction());

			elementType = userDefinedType_12;
			break;
		}
		if (elementType != null) {
			visiXMLVISISystematics.getAppendixTypeOrComplexElementTypeOrElementCondition().add(elementType);
		} else {
			System.out.println("Unkown element: " + object);
		}
		return elementType;
	}

	private XMLGregorianCalendar getDate(XMLGregorianCalendar date) {
		try {
			Date dateTime = date.toGregorianCalendar().getTime();
			gcal.setTime(dateTime);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
