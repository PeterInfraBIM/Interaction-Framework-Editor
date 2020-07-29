package nl.visi.interaction_framework.editor;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;

import nl.visi.interaction_framework.editor.v14.Control14;
import nl.visi.interaction_framework.editor.v14.Editor14;
import nl.visi.interaction_framework.editor.v16.Editor16;
import nl.visi.schemas._20140331.AppendixTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ElementConditionType;
import nl.visi.schemas._20140331.GroupTypeType;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;
import nl.visi.schemas._20140331.UserDefinedTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementConditionType.ComplexElements;
import nl.visi.schemas._20160331.ElementConditionType.MessageInTransaction;
import nl.visi.schemas._20160331.ElementConditionType.SimpleElement;
import nl.visi.schemas._20160331.GroupTypeTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Group;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20160331.MessageTypeTypeRef;
import nl.visi.schemas._20160331.ObjectFactory;
import nl.visi.schemas._20160331.RoleTypeTypeRef;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20160331.TransactionTypeTypeRef;
import nl.visi.schemas._20160331.UserDefinedTypeTypeRef;

public class Translator14to16 extends Control14 {
	public Translator14to16() {
	}

	void translate() throws FileNotFoundException, Exception {
		ObjectFactory object16Factory = new ObjectFactory();

		translateAppendixTypeAttributes(object16Factory);
		translateComplexElementTypeAttributes(object16Factory);
		translateElementConditionAttributes(object16Factory);
		translateGroupTypeAttributes(object16Factory);
		translateMessageInTransactionTypeAttributes(object16Factory);
		translateMessageInTransactionTypeConditionAttributes(object16Factory);
		translateMessageTypeAttributes(object16Factory);
		translateRoleTypeAttributes(object16Factory);
		translateSimpleElementTypeAttributes(object16Factory);
		translateTransactionTypeAttributes(object16Factory);
		translateUserDefinedTypeAttributes(object16Factory);
		translateAppendixTypeLinks(object16Factory);
		translateComplexElementTypeLinks(object16Factory);
		translateElementConditionLinks(object16Factory);
		translateMessageInTransactionTypeConditionLinks(object16Factory);
		translateMessageInTransactionTypeLinks(object16Factory);
		translateMessageTypeLinks(object16Factory);
		translateSimpleElementTypeLinks(object16Factory);
		translateTransactionTypeLinks(object16Factory);

		Editor16.getLoader16().marshal(new PrintStream("test.xml"));
	}

	private void translateAppendixTypeAttributes(ObjectFactory object16Factory) {
		List<AppendixTypeType> appendix14Types = Editor14.getStore14().getElements(AppendixTypeType.class);
		for (AppendixTypeType appendix14Type : appendix14Types) {
			nl.visi.schemas._20160331.AppendixTypeType appendix16Type = object16Factory.createAppendixTypeType();
			appendix16Type.setId(appendix14Type.getId());
			Method[] declared14Methods = AppendixTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCategory":
						appendix16Type.setCategory(appendix14Type.getCategory());
						break;
					case "getCode":
						appendix16Type.setCode(appendix14Type.getCode());
						break;
					case "getComplexElements":
						break;
					case "getDescription":
						appendix16Type.setDescription(appendix14Type.getDescription());
						break;
					case "getDateLaMu":
						appendix16Type.setDateLaMu(appendix14Type.getDateLaMu());
						break;
					case "getEndDate":
						appendix16Type.setEndDate(appendix14Type.getEndDate());
						break;
					case "getHelpInfo":
						appendix16Type.setHelpInfo(appendix14Type.getHelpInfo());
						break;
					case "getLanguage":
						appendix16Type.setLanguage(appendix14Type.getLanguage());
						break;
					case "getStartDate":
						appendix16Type.setStartDate(appendix14Type.getStartDate());
						break;
					case "getState":
						appendix16Type.setState(appendix14Type.getState());
						break;
					case "getUserLaMu":
						appendix16Type.setUserLaMu(appendix14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(appendix16Type.getId(), appendix16Type);
		}
	}

	private void translateAppendixTypeLinks(ObjectFactory object16Factory) {
		List<AppendixTypeType> appendix14Types = Editor14.getStore14().getElements(AppendixTypeType.class);
		for (AppendixTypeType appendix14Type : appendix14Types) {
			nl.visi.schemas._20160331.AppendixTypeType appendix16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.AppendixTypeType.class, appendix14Type.getId());
			List<ComplexElementTypeType> complexElements = getComplexElements(appendix14Type);
			if (complexElements != null) {
				nl.visi.schemas._20160331.AppendixTypeType.ComplexElements appendixTypeComplexElements = object16Factory
						.createAppendixTypeTypeComplexElements();
				for (ComplexElementTypeType complexElement : complexElements) {
					nl.visi.schemas._20160331.ComplexElementTypeType ce = Editor16.getStore16()
							.getElement(nl.visi.schemas._20160331.ComplexElementTypeType.class, complexElement.getId());
					ComplexElementTypeTypeRef complexElementTypeTypeRef = object16Factory
							.createComplexElementTypeTypeRef();
					complexElementTypeTypeRef.setIdref(ce);
					appendixTypeComplexElements.getComplexElementTypeOrComplexElementTypeRef()
							.add(complexElementTypeTypeRef);
					appendix16Type.setComplexElements(appendixTypeComplexElements);
				}
			}
			Editor16.getStore16().put(appendix16Type.getId(), appendix16Type);
		}
	}

	private void translateComplexElementTypeAttributes(ObjectFactory object16Factory) {
		List<ComplexElementTypeType> complexElement14Types = Editor14.getStore14()
				.getElements(ComplexElementTypeType.class);
		for (ComplexElementTypeType complexElement14Type : complexElement14Types) {
			nl.visi.schemas._20160331.ComplexElementTypeType complexElement16Type = object16Factory
					.createComplexElementTypeType();
			complexElement16Type.setId(complexElement14Type.getId());
			Method[] declared14Methods = ComplexElementTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCategory":
						complexElement16Type.setCategory(complexElement14Type.getCategory());
						break;
					case "getComplexElements":
						break;
					case "getDescription":
						complexElement16Type.setDescription(complexElement14Type.getDescription());
						break;
					case "getDateLaMu":
						complexElement16Type.setDateLaMu(complexElement14Type.getDateLaMu());
						break;
					case "getEndDate":
						complexElement16Type.setEndDate(complexElement14Type.getEndDate());
						break;
					case "getHelpInfo":
						complexElement16Type.setHelpInfo(complexElement14Type.getHelpInfo());
						break;
					case "getLanguage":
						complexElement16Type.setLanguage(complexElement14Type.getLanguage());
						break;
					case "getSimpleElements":
						break;
					case "getStartDate":
						complexElement16Type.setStartDate(complexElement14Type.getStartDate());
						break;
					case "getState":
						complexElement16Type.setState(complexElement14Type.getState());
						break;
					case "getUserLaMu":
						complexElement16Type.setUserLaMu(complexElement14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(complexElement16Type.getId(), complexElement16Type);
		}
	}

	private void translateComplexElementTypeLinks(ObjectFactory object16Factory) {
		List<ComplexElementTypeType> complexElement14Types = Editor14.getStore14()
				.getElements(ComplexElementTypeType.class);
		for (ComplexElementTypeType complexElement14Type : complexElement14Types) {
			nl.visi.schemas._20160331.ComplexElementTypeType complexElement16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.ComplexElementTypeType.class, complexElement14Type.getId());
			List<ComplexElementTypeType> complexElements = getComplexElements(complexElement14Type);
			if (complexElements != null) {
				nl.visi.schemas._20160331.ComplexElementTypeType.ComplexElements complexElementTypeComplexElements = object16Factory
						.createComplexElementTypeTypeComplexElements();
				for (ComplexElementTypeType complexElement : complexElements) {
					nl.visi.schemas._20160331.ComplexElementTypeType ce = Editor16.getStore16()
							.getElement(nl.visi.schemas._20160331.ComplexElementTypeType.class, complexElement.getId());
					ComplexElementTypeTypeRef complexElementTypeTypeRef = object16Factory
							.createComplexElementTypeTypeRef();
					complexElementTypeTypeRef.setIdref(ce);
					complexElementTypeComplexElements.getComplexElementTypeOrComplexElementTypeRef()
							.add(complexElementTypeTypeRef);
					complexElement16Type.setComplexElements(complexElementTypeComplexElements);
				}
			}

			List<SimpleElementTypeType> simpleElements = getSimpleElements(complexElement14Type);
			if (simpleElements != null) {
				nl.visi.schemas._20160331.ComplexElementTypeType.SimpleElements complexElementTypeSimpleElements = object16Factory
						.createComplexElementTypeTypeSimpleElements();
				for (SimpleElementTypeType simpleElement : simpleElements) {
					nl.visi.schemas._20160331.SimpleElementTypeType ce = Editor16.getStore16()
							.getElement(nl.visi.schemas._20160331.SimpleElementTypeType.class, simpleElement.getId());
					SimpleElementTypeTypeRef simpleElementTypeTypeRef = object16Factory
							.createSimpleElementTypeTypeRef();
					simpleElementTypeTypeRef.setIdref(ce);
					complexElementTypeSimpleElements.getSimpleElementTypeOrSimpleElementTypeRef()
							.add(simpleElementTypeTypeRef);
					complexElement16Type.setSimpleElements(complexElementTypeSimpleElements);
				}
			}

			Editor16.getStore16().put(complexElement16Type.getId(), complexElement16Type);
		}
	}

	private void translateElementConditionAttributes(ObjectFactory object16Factory) {
		List<ElementConditionType> elementCondition14Types = Editor14.getStore14()
				.getElements(ElementConditionType.class);
		for (ElementConditionType elementCondition14Type : elementCondition14Types) {
			nl.visi.schemas._20160331.ElementConditionType elementCondition16Type = object16Factory
					.createElementConditionType();
			elementCondition16Type.setId(elementCondition14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.ElementConditionType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCondition":
						elementCondition16Type.setCondition(elementCondition14Type.getCondition());
						break;
					case "getDescription":
						elementCondition16Type.setDescription(elementCondition14Type.getDescription());
						break;
					case "getHelpInfo":
						elementCondition16Type.setHelpInfo(elementCondition14Type.getHelpInfo());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(elementCondition16Type.getId(), elementCondition16Type);
		}
	}

	private void translateElementConditionLinks(ObjectFactory object16Factory) {
		List<ElementConditionType> elementCondition14Types = Editor14.getStore14()
				.getElements(ElementConditionType.class);
		for (ElementConditionType elementCondition14Type : elementCondition14Types) {
			nl.visi.schemas._20160331.ElementConditionType elementCondition16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.ElementConditionType.class, elementCondition14Type.getId());
			MessageInTransactionTypeType messageInTransactionType = getMessageInTransaction(elementCondition14Type);
			if (messageInTransactionType != null) {
				MessageInTransaction elementConditionTypeMessageInTransaction = object16Factory
						.createElementConditionTypeMessageInTransaction();
				nl.visi.schemas._20160331.MessageInTransactionTypeType simpleElement = Editor16.getStore16().getElement(
						nl.visi.schemas._20160331.MessageInTransactionTypeType.class, messageInTransactionType.getId());
				MessageInTransactionTypeTypeRef messageInTransactionTypeTypeRef = object16Factory
						.createMessageInTransactionTypeTypeRef();
				messageInTransactionTypeTypeRef.setIdref(simpleElement);
				elementConditionTypeMessageInTransaction
						.setMessageInTransactionTypeRef(messageInTransactionTypeTypeRef);
				elementCondition16Type.setMessageInTransaction(elementConditionTypeMessageInTransaction);
			}

			SimpleElementTypeType simpleElementType = getSimpleElement(elementCondition14Type);
			if (simpleElementType != null) {
				SimpleElement elementConditionTypeSimpleElement = object16Factory
						.createElementConditionTypeSimpleElement();
				nl.visi.schemas._20160331.SimpleElementTypeType simpleElement = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.SimpleElementTypeType.class, simpleElementType.getId());
				SimpleElementTypeTypeRef simpleElementTypeTypeRef = object16Factory.createSimpleElementTypeTypeRef();
				simpleElementTypeTypeRef.setIdref(simpleElement);
				elementConditionTypeSimpleElement.setSimpleElementTypeRef(simpleElementTypeTypeRef);
				elementCondition16Type.setSimpleElement(elementConditionTypeSimpleElement);
			}

			ComplexElementTypeType complexElement = getComplexElement(elementCondition14Type);
			if (complexElement != null) {
				ComplexElements complexElementTypeComplexElements = object16Factory
						.createElementConditionTypeComplexElements();
				nl.visi.schemas._20160331.ComplexElementTypeType ce = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.ComplexElementTypeType.class, complexElement.getId());
				ComplexElementTypeTypeRef complexElementTypeTypeRef = object16Factory.createComplexElementTypeTypeRef();
				complexElementTypeTypeRef.setIdref(ce);
				complexElementTypeComplexElements.getComplexElementTypeOrComplexElementTypeRef()
						.add(complexElementTypeTypeRef);
				elementCondition16Type.setComplexElements(complexElementTypeComplexElements);
			}

			Editor16.getStore16().put(elementCondition16Type.getId(), elementCondition16Type);
		}
	}

	private void translateGroupTypeAttributes(ObjectFactory object16Factory) {
		List<GroupTypeType> group14Types = Editor14.getStore14().getElements(GroupTypeType.class);
		for (GroupTypeType group14Type : group14Types) {
			nl.visi.schemas._20160331.GroupTypeType group16Type = object16Factory.createGroupTypeType();
			group16Type.setId(group14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.GroupTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCategory":
						group16Type.setCategory(group14Type.getCategory());
						break;
					case "getDescription":
						group16Type.setDescription(group14Type.getDescription());
						break;
					case "getDateLaMu":
						group16Type.setDateLaMu(group14Type.getDateLaMu());
						break;
					case "getEndDate":
						group16Type.setEndDate(group14Type.getEndDate());
						break;
					case "getHelpInfo":
						group16Type.setHelpInfo(group14Type.getHelpInfo());
						break;
					case "getLanguage":
						group16Type.setLanguage(group14Type.getLanguage());
						break;
					case "getSimpleElements":
						break;
					case "getStartDate":
						group16Type.setStartDate(group14Type.getStartDate());
						break;
					case "getState":
						group16Type.setState(group14Type.getState());
						break;
					case "getUserLaMu":
						group16Type.setUserLaMu(group14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(group16Type.getId(), group16Type);
		}
	}

	private void translateMessageInTransactionTypeAttributes(ObjectFactory object16Factory) {
		List<MessageInTransactionTypeType> messageInTransaction14Types = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType messageInTransaction14Type : messageInTransaction14Types) {
			nl.visi.schemas._20160331.MessageInTransactionTypeType messageInTransaction16Type = object16Factory
					.createMessageInTransactionTypeType();
			messageInTransaction16Type.setId(messageInTransaction14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.MessageInTransactionTypeType.class
					.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getDateLaMu":
						messageInTransaction16Type.setDateLaMu(messageInTransaction14Type.getDateLaMu());
						break;
					case "isFirstMessage":
						messageInTransaction16Type.setFirstMessage(messageInTransaction14Type.isFirstMessage());
						break;
					case "isInitiatorToExecutor":
						messageInTransaction16Type
								.setInitiatorToExecutor(messageInTransaction14Type.isInitiatorToExecutor());
						break;
					case "isOpenSecondaryTransactionsAllowed":
						messageInTransaction16Type.setOpenSecondaryTransactionsAllowed(
								messageInTransaction14Type.isOpenSecondaryTransactionsAllowed());
						break;
					case "isReceived":
						messageInTransaction16Type.setReceived(messageInTransaction14Type.isReceived());
						break;
					case "isSend":
						messageInTransaction16Type.setSend(messageInTransaction14Type.isSend());
						break;
					case "getRequiredNotify":
						messageInTransaction16Type.setRequiredNotify(messageInTransaction14Type.getRequiredNotify());
						break;
					case "getState":
						messageInTransaction16Type.setState(messageInTransaction14Type.getState());
						break;
					case "getUserLaMu":
						messageInTransaction16Type.setUserLaMu(messageInTransaction14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(messageInTransaction16Type.getId(), messageInTransaction16Type);
		}
	}

	private void translateMessageInTransactionTypeLinks(ObjectFactory object16Factory) {
		List<MessageInTransactionTypeType> messageInTransaction14Types = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType messageInTransaction14Type : messageInTransaction14Types) {
			nl.visi.schemas._20160331.MessageInTransactionTypeType messageInTransaction16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.MessageInTransactionTypeType.class,
							messageInTransaction14Type.getId());
			List<MessageInTransactionTypeType> previousList = getPrevious(messageInTransaction14Type);
			if (previousList != null) {
				Previous messageInTransactionTypeTypePrevious = object16Factory
						.createMessageInTransactionTypeTypePrevious();
				for (MessageInTransactionTypeType previous : previousList) {
					nl.visi.schemas._20160331.MessageInTransactionTypeType previous16 = Editor16.getStore16()
							.getElement(nl.visi.schemas._20160331.MessageInTransactionTypeType.class, previous.getId());
					MessageInTransactionTypeTypeRef messageInTransactionTypeTypeRef = object16Factory
							.createMessageInTransactionTypeTypeRef();
					messageInTransactionTypeTypeRef.setIdref(previous16);
					messageInTransactionTypeTypePrevious.getMessageInTransactionTypeOrMessageInTransactionTypeRef()
							.add(messageInTransactionTypeTypeRef);
				}
				messageInTransaction16Type.setPrevious(messageInTransactionTypeTypePrevious);
			}

			TransactionTypeType transactionType = getTransaction(messageInTransaction14Type);
			if (transactionType != null) {
				Transaction messageInTransactionTypeTypeTransaction = object16Factory
						.createMessageInTransactionTypeTypeTransaction();
				nl.visi.schemas._20160331.TransactionTypeType transaction = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.TransactionTypeType.class, transactionType.getId());
				TransactionTypeTypeRef transactionTypeTypeRef = object16Factory.createTransactionTypeTypeRef();
				transactionTypeTypeRef.setIdref(transaction);
				messageInTransactionTypeTypeTransaction.setTransactionTypeRef(transactionTypeTypeRef);
				messageInTransaction16Type.setTransaction(messageInTransactionTypeTypeTransaction);
			}

			MessageTypeType messageType = getMessage(messageInTransaction14Type);
			if (messageType != null) {
				Message messageTypeTypeGroup = object16Factory.createMessageInTransactionTypeTypeMessage();
				nl.visi.schemas._20160331.MessageTypeType message = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.MessageTypeType.class, messageType.getId());
				MessageTypeTypeRef messageTypeTypeRef = object16Factory.createMessageTypeTypeRef();
				messageTypeTypeRef.setIdref(message);
				messageTypeTypeGroup.setMessageTypeRef(messageTypeTypeRef);
				messageInTransaction16Type.setMessage(messageTypeTypeGroup);
			}

			GroupTypeType groupType = getGroup(messageInTransaction14Type);
			if (groupType != null) {
				Group messageInTransactionTypeTypeGroup = object16Factory.createMessageInTransactionTypeTypeGroup();
				nl.visi.schemas._20160331.GroupTypeType group = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.GroupTypeType.class, groupType.getId());
				GroupTypeTypeRef simpleElementTypeTypeRef = object16Factory.createGroupTypeTypeRef();
				simpleElementTypeTypeRef.setIdref(group);
				messageInTransactionTypeTypeGroup.setGroupTypeRef(simpleElementTypeTypeRef);
				messageInTransaction16Type.setGroup(messageInTransactionTypeTypeGroup);
			}

			Editor16.getStore16().put(messageInTransaction16Type.getId(), messageInTransaction16Type);
		}
	}

	private void translateMessageInTransactionTypeConditionAttributes(ObjectFactory object16Factory) {
		List<MessageInTransactionTypeConditionType> messageInTransactionCondition14Types = Editor14.getStore14()
				.getElements(MessageInTransactionTypeConditionType.class);
		for (MessageInTransactionTypeConditionType messageInTransactionCondition14Type : messageInTransactionCondition14Types) {
			nl.visi.schemas._20160331.MessageInTransactionTypeConditionType messageInTransactionCondition16Type = object16Factory
					.createMessageInTransactionTypeConditionType();
			messageInTransactionCondition16Type.setId(messageInTransactionCondition14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.class
					.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getDateLaMu":
						messageInTransactionCondition16Type
								.setDateLaMu(messageInTransactionCondition14Type.getDateLaMu());
						break;
					case "getHelpInfo":
						messageInTransactionCondition16Type
								.setHelpInfo(messageInTransactionCondition14Type.getHelpInfo());
						break;
					case "getSimpleElements":
						break;
					case "getState":
						messageInTransactionCondition16Type.setState(messageInTransactionCondition14Type.getState());
						break;
					case "getUserLaMu":
						messageInTransactionCondition16Type
								.setUserLaMu(messageInTransactionCondition14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(messageInTransactionCondition16Type.getId(), messageInTransactionCondition16Type);
		}
	}

	private void translateMessageInTransactionTypeConditionLinks(ObjectFactory object16Factory) {
		List<MessageInTransactionTypeConditionType> messageInTransactionCondition14Types = Editor14.getStore14()
				.getElements(MessageInTransactionTypeConditionType.class);
		for (MessageInTransactionTypeConditionType messageInTransactionCondition14Type : messageInTransactionCondition14Types) {
			nl.visi.schemas._20160331.MessageInTransactionTypeConditionType messageInTransactionCondition16Type = Editor16
					.getStore16().getElement(nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.class,
							messageInTransactionCondition14Type.getId());
			List<MessageInTransactionTypeType> sendAfters14 = getSendAfters(messageInTransactionCondition14Type);
			if (sendAfters14 != null) {
				nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.SendAfter messageInTransactionTypeConditionTypeSendAfter = object16Factory
						.createMessageInTransactionTypeConditionTypeSendAfter();
				for (MessageInTransactionTypeType sendAfter14 : sendAfters14) {
					nl.visi.schemas._20160331.MessageInTransactionTypeType mitt = Editor16.getStore16().getElement(
							nl.visi.schemas._20160331.MessageInTransactionTypeType.class, sendAfter14.getId());
					MessageInTransactionTypeTypeRef messageInTransactionTypeTypeRef = object16Factory
							.createMessageInTransactionTypeTypeRef();
					messageInTransactionTypeTypeRef.setIdref(mitt);
					messageInTransactionTypeConditionTypeSendAfter
							.getMessageInTransactionTypeOrMessageInTransactionTypeRef()
							.add(messageInTransactionTypeTypeRef);
					messageInTransactionCondition16Type.setSendAfter(messageInTransactionTypeConditionTypeSendAfter);

				}
			}

			List<MessageInTransactionTypeType> sendBefores14 = getSendBefores(messageInTransactionCondition14Type);
			if (sendBefores14 != null) {
				nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.SendBefore messageInTransactionTypeConditionTypeSendBefore = object16Factory
						.createMessageInTransactionTypeConditionTypeSendBefore();
				for (MessageInTransactionTypeType sendBefore14 : sendBefores14) {
					nl.visi.schemas._20160331.MessageInTransactionTypeType mitt = Editor16.getStore16().getElement(
							nl.visi.schemas._20160331.MessageInTransactionTypeType.class, sendBefore14.getId());
					MessageInTransactionTypeTypeRef messageInTransactionTypeTypeRef = object16Factory
							.createMessageInTransactionTypeTypeRef();
					messageInTransactionTypeTypeRef.setIdref(mitt);
					messageInTransactionTypeConditionTypeSendBefore
							.getMessageInTransactionTypeOrMessageInTransactionTypeRef()
							.add(messageInTransactionTypeTypeRef);
					messageInTransactionCondition16Type.setSendBefore(messageInTransactionTypeConditionTypeSendBefore);
				}
			}
		}
	}

	private void translateMessageTypeAttributes(ObjectFactory object16Factory) {
		List<MessageTypeType> message14Types = Editor14.getStore14().getElements(MessageTypeType.class);
		for (MessageTypeType message14Type : message14Types) {
			nl.visi.schemas._20160331.MessageTypeType message16Type = object16Factory.createMessageTypeType();
			message16Type.setId(message14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.MessageTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "setCategory":
						message16Type.setCategory(message14Type.getCategory());
						break;
					case "setCode":
						message16Type.setCode(message14Type.getCode());
						break;
					case "setDateLaMu":
						message16Type.setDateLaMu(message14Type.getDateLaMu());
						break;
					case "setDescription":
						message16Type.setDescription(message14Type.getDescription());
						break;
					case "setEndDate":
						message16Type.setEndDate(message14Type.getEndDate());
						break;
					case "setHelpInfo":
						message16Type.setHelpInfo(message14Type.getHelpInfo());
						break;
					case "setLanguage":
						message16Type.setLanguage(message14Type.getLanguage());
						break;
					case "setStartDate":
						message16Type.setStartDate(message14Type.getStartDate());
						break;
					case "setState":
						message16Type.setState(message14Type.getState());
						break;
					case "setUserLaMu":
						message16Type.setUserLaMu(message14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(message16Type.getId(), message16Type);
		}
	}

	private void translateMessageTypeLinks(ObjectFactory object16Factory) {
		List<MessageTypeType> message14Types = Editor14.getStore14().getElements(MessageTypeType.class);
		for (MessageTypeType message14Type : message14Types) {
			nl.visi.schemas._20160331.MessageTypeType complexElement16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.MessageTypeType.class, message14Type.getId());
			List<ComplexElementTypeType> complexElements = getComplexElements(message14Type);
			if (complexElements != null) {
				nl.visi.schemas._20160331.MessageTypeType.ComplexElements messageTypeComplexElements = object16Factory
						.createMessageTypeTypeComplexElements();
				for (ComplexElementTypeType complexElement : complexElements) {
					nl.visi.schemas._20160331.ComplexElementTypeType ce = Editor16.getStore16()
							.getElement(nl.visi.schemas._20160331.ComplexElementTypeType.class, complexElement.getId());
					ComplexElementTypeTypeRef complexElementTypeTypeRef = object16Factory
							.createComplexElementTypeTypeRef();
					complexElementTypeTypeRef.setIdref(ce);
					messageTypeComplexElements.getComplexElementTypeOrComplexElementTypeRef()
							.add(complexElementTypeTypeRef);
					complexElement16Type.setComplexElements(messageTypeComplexElements);
				}
			}

			Editor16.getStore16().put(complexElement16Type.getId(), complexElement16Type);
		}
	}

	private void translateRoleTypeAttributes(ObjectFactory object16Factory) {
		List<RoleTypeType> role14Types = Editor14.getStore14().getElements(RoleTypeType.class);
		for (RoleTypeType role14Type : role14Types) {
			nl.visi.schemas._20160331.RoleTypeType role16Type = object16Factory.createRoleTypeType();
			role16Type.setId(role14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.RoleTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCategory":
						role16Type.setCategory(role14Type.getCategory());
						break;
					case "getCode":
						role16Type.setCode(role14Type.getCode());
						break;
					case "getDescription":
						role16Type.setDescription(role14Type.getDescription());
						break;
					case "getDateLaMu":
						role16Type.setDateLaMu(role14Type.getDateLaMu());
						break;
					case "getEndDate":
						role16Type.setEndDate(role14Type.getEndDate());
						break;
					case "getHelpInfo":
						role16Type.setHelpInfo(role14Type.getHelpInfo());
						break;
					case "getLanguage":
						role16Type.setLanguage(role14Type.getLanguage());
						break;
					case "getResponsibilityFeedback":
						role16Type.setResponsibilityFeedback(role14Type.getResponsibilityFeedback());
						break;
					case "getResponsibilityScope":
						role16Type.setResponsibilityScope(role14Type.getResponsibilityScope());
						break;
					case "getResponsibilitySupportTask":
						role16Type.setResponsibilitySupportTask(role14Type.getResponsibilitySupportTask());
						break;
					case "getResponsibilityTask":
						role16Type.setResponsibilityTask(role14Type.getResponsibilityTask());
						break;
					case "getStartDate":
						role16Type.setStartDate(role14Type.getStartDate());
						break;
					case "getState":
						role16Type.setState(role14Type.getState());
						break;
					case "getUserLaMu":
						role16Type.setUserLaMu(role14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(role16Type.getId(), role16Type);
		}
	}

	private void translateSimpleElementTypeAttributes(ObjectFactory object16Factory) {
		List<SimpleElementTypeType> simpleElement14Types = Editor14.getStore14()
				.getElements(SimpleElementTypeType.class);
		for (SimpleElementTypeType simpleElement14Type : simpleElement14Types) {
			nl.visi.schemas._20160331.SimpleElementTypeType simpleElement16Type = object16Factory
					.createSimpleElementTypeType();
			simpleElement16Type.setId(simpleElement14Type.getId());
			Method[] declared14Methods = SimpleElementTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCategory":
						simpleElement16Type.setCategory(simpleElement14Type.getCategory());
						break;
					case "getDescription":
						simpleElement16Type.setDescription(simpleElement14Type.getDescription());
						break;
					case "getDateLaMu":
						simpleElement16Type.setDateLaMu(simpleElement14Type.getDateLaMu());
						break;
					case "getHelpInfo":
						simpleElement16Type.setHelpInfo(simpleElement14Type.getHelpInfo());
						break;
					case "getInterfaceType":
						simpleElement16Type.setInterfaceType(simpleElement14Type.getInterfaceType());
						break;
					case "getLanguage":
						simpleElement16Type.setLanguage(simpleElement14Type.getLanguage());
						break;
					case "getState":
						simpleElement16Type.setState(simpleElement14Type.getState());
						break;
					case "getUserLaMu":
						simpleElement16Type.setUserLaMu(simpleElement14Type.getUserLaMu());
						break;
					case "getValueList":
						simpleElement16Type.setValueList(simpleElement14Type.getValueList());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(simpleElement16Type.getId(), simpleElement16Type);
		}
	}

	private void translateSimpleElementTypeLinks(ObjectFactory object16Factory) {
		List<SimpleElementTypeType> simpleElement14Types = Editor14.getStore14()
				.getElements(SimpleElementTypeType.class);
		for (SimpleElementTypeType simpleElement14Type : simpleElement14Types) {
			nl.visi.schemas._20160331.SimpleElementTypeType simpleElement16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.SimpleElementTypeType.class, simpleElement14Type.getId());
			UserDefinedTypeType userDefinedType = getUserDefinedType(simpleElement14Type);
			if (userDefinedType != null) {
				nl.visi.schemas._20160331.SimpleElementTypeType.UserDefinedType simpleElementTypeUserDefinedType = object16Factory
						.createSimpleElementTypeTypeUserDefinedType();
				nl.visi.schemas._20160331.UserDefinedTypeType userDefined = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.UserDefinedTypeType.class, userDefinedType.getId());
				UserDefinedTypeTypeRef userDefinedTypeTypeRef = object16Factory.createUserDefinedTypeTypeRef();
				userDefinedTypeTypeRef.setIdref(userDefined);
				simpleElementTypeUserDefinedType.setUserDefinedTypeRef(userDefinedTypeTypeRef);
				simpleElement16Type.setUserDefinedType(simpleElementTypeUserDefinedType);
			}
			Editor16.getStore16().put(simpleElement16Type.getId(), simpleElement16Type);
		}
	}

	private void translateTransactionTypeAttributes(ObjectFactory object16Factory) {
		List<TransactionTypeType> transaction14Types = Editor14.getStore14().getElements(TransactionTypeType.class);
		for (TransactionTypeType transaction14Type : transaction14Types) {
			nl.visi.schemas._20160331.TransactionTypeType transaction16Type = object16Factory
					.createTransactionTypeType();
			transaction16Type.setId(transaction14Type.getId());
			Method[] declared14Methods = nl.visi.schemas._20160331.TransactionTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getCategory":
						transaction16Type.setCategory(transaction14Type.getCategory());
						break;
					case "getCode":
						transaction16Type.setCode(transaction14Type.getCode());
						break;
					case "getDescription":
						transaction16Type.setDescription(transaction14Type.getDescription());
						break;
					case "getDateLaMu":
						transaction16Type.setDateLaMu(transaction14Type.getDateLaMu());
						break;
					case "getEndDate":
						transaction16Type.setEndDate(transaction14Type.getEndDate());
						break;
					case "getHelpInfo":
						transaction16Type.setHelpInfo(transaction14Type.getHelpInfo());
						break;
					case "getLanguage":
						transaction16Type.setLanguage(transaction14Type.getLanguage());
						break;
					case "getStartDate":
						transaction16Type.setStartDate(transaction14Type.getStartDate());
						break;
					case "getResult":
						transaction16Type.setResult(transaction14Type.getResult());
						break;
					case "getState":
						transaction16Type.setState(transaction14Type.getState());
						break;
					case "getUserLaMu":
						transaction16Type.setUserLaMu(transaction14Type.getUserLaMu());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(transaction16Type.getId(), transaction16Type);
		}
	}

	private void translateTransactionTypeLinks(ObjectFactory object16Factory) {
		List<TransactionTypeType> transaction14Types = Editor14.getStore14().getElements(TransactionTypeType.class);
		for (TransactionTypeType transaction14Type : transaction14Types) {
			nl.visi.schemas._20160331.TransactionTypeType transaction16Type = Editor16.getStore16()
					.getElement(nl.visi.schemas._20160331.TransactionTypeType.class, transaction14Type.getId());

			RoleTypeType initiatorType = getInitiator(transaction14Type);
			if (initiatorType != null) {
				nl.visi.schemas._20160331.TransactionTypeType.Initiator transactionTypeInitiatorType = object16Factory
						.createTransactionTypeTypeInitiator();
				nl.visi.schemas._20160331.RoleTypeType initiator = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.RoleTypeType.class, initiatorType.getId());
				RoleTypeTypeRef roleTypeTypeRef = object16Factory.createRoleTypeTypeRef();
				roleTypeTypeRef.setIdref(initiator);
				transactionTypeInitiatorType.setRoleTypeRef(roleTypeTypeRef);
				transaction16Type.setInitiator(transactionTypeInitiatorType);
			}

			RoleTypeType executorType = getExecutor(transaction14Type);
			if (executorType != null) {
				nl.visi.schemas._20160331.TransactionTypeType.Executor transactionTypeExecutorType = object16Factory
						.createTransactionTypeTypeExecutor();
				nl.visi.schemas._20160331.RoleTypeType executor = Editor16.getStore16()
						.getElement(nl.visi.schemas._20160331.RoleTypeType.class, executorType.getId());
				RoleTypeTypeRef roleTypeTypeRef = object16Factory.createRoleTypeTypeRef();
				roleTypeTypeRef.setIdref(executor);
				transactionTypeExecutorType.setRoleTypeRef(roleTypeTypeRef);
				transaction16Type.setExecutor(transactionTypeExecutorType);
			}

			Editor16.getStore16().put(transaction16Type.getId(), transaction16Type);
		}
	}

	private void translateUserDefinedTypeAttributes(ObjectFactory object16Factory) {
		List<UserDefinedTypeType> userDefined14Types = Editor14.getStore14().getElements(UserDefinedTypeType.class);
		for (UserDefinedTypeType userDefined14Type : userDefined14Types) {
			nl.visi.schemas._20160331.UserDefinedTypeType userDefined16Type = object16Factory
					.createUserDefinedTypeType();
			userDefined16Type.setId(userDefined14Type.getId());
			Method[] declared14Methods = UserDefinedTypeType.class.getDeclaredMethods();
			try {
				for (Method method : declared14Methods) {
					switch (method.getName()) {
					case "getBaseType":
						userDefined16Type.setBaseType(userDefined14Type.getBaseType());
						break;
					case "getDescription":
						userDefined16Type.setDescription(userDefined14Type.getDescription());
						break;
					case "getDateLaMu":
						userDefined16Type.setDateLaMu(userDefined14Type.getDateLaMu());
						break;
					case "getHelpInfo":
						userDefined16Type.setHelpInfo(userDefined14Type.getHelpInfo());
						break;
					case "getLanguage":
						userDefined16Type.setLanguage(userDefined14Type.getLanguage());
						break;
					case "getSimpleElements":
						break;
					case "getState":
						userDefined16Type.setState(userDefined14Type.getState());
						break;
					case "getUserLaMu":
						userDefined16Type.setUserLaMu(userDefined14Type.getUserLaMu());
						break;
					case "getXsdRestriction":
						userDefined16Type.setXsdRestriction(userDefined14Type.getXsdRestriction());
						break;
					default:
						System.out.println(method.getName());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Editor16.getStore16().put(userDefined16Type.getId(), userDefined16Type);
		}
	}
}
