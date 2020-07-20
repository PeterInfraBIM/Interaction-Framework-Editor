package nl.visi.interaction_framework.editor.v14;

import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.Control;
import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20140331.ElementConditionType;
import nl.visi.schemas._20140331.ElementConditionType.ComplexElement;
import nl.visi.schemas._20140331.ElementConditionType.MessageInTransaction;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType.SendAfter;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionType.SendBefore;
import nl.visi.schemas._20140331.MessageInTransactionTypeConditionTypeRef;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Conditions;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.TransactionPhase;
import nl.visi.schemas._20140331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.MessageTypeType.ComplexElements;
import nl.visi.schemas._20140331.ObjectFactory;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType.UserDefinedType;
import nl.visi.schemas._20140331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20140331.TransactionPhaseTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;
import nl.visi.schemas._20140331.TransactionTypeType.Executor;
import nl.visi.schemas._20140331.TransactionTypeType.Initiator;
import nl.visi.schemas._20140331.UserDefinedTypeType;

abstract class Control14 extends Control {
	static final java.text.DateFormat sdfDate = new SimpleDateFormat("d MMM yyyy");
	static final java.text.DateFormat sdfDateTime = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
	protected static final ObjectFactory objectFactory = new ObjectFactory();
	protected static final GregorianCalendar gcal = new GregorianCalendar();

	protected Comparator<String> dateComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if (o1 != null && o2 != null && !o1.isEmpty() && !o2.isEmpty()) {
				try {
					Date o1Date = sdfDate.parse(o1);
					Date o2Date = sdfDate.parse(o2);
					return o1Date.compareTo(o2Date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return 0;
		}
	};

	protected Comparator<String> dateTimeComparator = new Comparator<String>() {
		@Override
		public int compare(String o1, String o2) {
			if (o1 != null && o2 != null && !o1.isEmpty() && !o2.isEmpty()) {
				try {
					Date o1Date = sdfDateTime.parse(o1);
					Date o2Date = sdfDateTime.parse(o2);
					return o1Date.compareTo(o2Date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			return 0;
		}
	};

	@SuppressWarnings("serial")
	public abstract class ElementsTableModel<T extends ElementType> extends AbstractTableModel {

		public List<T> elements = new ArrayList<T>();
		private boolean sorted = true;

		public boolean isSorted() {
			return sorted;
		}

		public void setSorted(boolean sorted) {
			this.sorted = sorted;
		}

		@Override
		public int getRowCount() {
			return elements.size();
		}

		public void clear() {
			int rowCount = getRowCount();
			elements.clear();
			if (rowCount > 0) {
				fireTableRowsDeleted(0, rowCount - 1);
			}
		}

		public int add(T element) {
			int row = getRowCount();
			boolean inserted = false;
			if (sorted) {
				for (int index = 0; !inserted && index < elements.size(); index++) {
					String sortId1 = getSortId(elements.get(index));
					String sortId2 = getSortId(element);
					if (sortId1.compareToIgnoreCase(sortId2) > 0) {
						row = index;
						elements.add(index, element);
						inserted = true;
					}
				}
			}
			if (!inserted) {
				elements.add(element);
			}
			fireTableRowsInserted(row, row);
			return row;
		}

		public T remove(int row) {
			T element = elements.get(row);
			elements.remove(row);
			fireTableRowsDeleted(row, row);
			return element;
		}

		public void update(int row) {
			fireTableRowsUpdated(row, row);
		}

		public T get(int index) {
			return elements.get(index);
		}

		protected String getSortId(T element) {
			return element.getId();
		}

	}

	public Control14() {
		super();
	}

	protected String getDate(XMLGregorianCalendar dateTime) {
		return dateTime != null ? sdfDate.format(dateTime.toGregorianCalendar().getTime()) : "";
	}

	protected String getDateTime(XMLGregorianCalendar dateTime) {
		return dateTime != null ? sdfDateTime.format(dateTime.toGregorianCalendar().getTime()) : "";
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	protected static TransactionTypeType getTransaction(MessageInTransactionTypeType mitt) {
		if (mitt != null) {
			Transaction transactionValue = mitt.getTransaction();
			if (transactionValue != null) {
				TransactionTypeType transactionType = transactionValue.getTransactionType();
				if (transactionType == null) {
					transactionType = (TransactionTypeType) transactionValue.getTransactionTypeRef().getIdref();
				}
				return transactionType;
			}
		}
		return null;
	}

	protected static TransactionPhaseTypeType getTransactionPhase(MessageInTransactionTypeType mitt) {
		if (mitt != null) {
			TransactionPhase transactionPhase = mitt.getTransactionPhase();
			if (transactionPhase != null) {
				TransactionPhaseTypeType transactionPhaseType = transactionPhase.getTransactionPhaseType();
				if (transactionPhaseType == null) {
					transactionPhaseType = (TransactionPhaseTypeType) transactionPhase.getTransactionPhaseTypeRef()
							.getIdref();
				}
				return transactionPhaseType;
			}
		}
		return null;
	}

	protected static MessageInTransactionTypeType getMessageInTransaction(ElementConditionType elementConditionType) {
		if (elementConditionType != null) {
			MessageInTransaction messageInTransaction = elementConditionType.getMessageInTransaction();
			if (messageInTransaction != null) {
				MessageInTransactionTypeType messageInTransactionType = messageInTransaction
						.getMessageInTransactionType();
				if (messageInTransactionType == null) {
					messageInTransactionType = (MessageInTransactionTypeType) messageInTransaction
							.getMessageInTransactionTypeRef().getIdref();
				}
				return messageInTransactionType;
			}
		}
		return null;
	}

	protected static MessageTypeType getMessage(MessageInTransactionTypeType mitt) {
		if (mitt != null) {
			Message messageValue = mitt.getMessage();
			if (messageValue != null) {
				MessageTypeType messageType = messageValue.getMessageType();
				if (messageType == null) {
					messageType = (MessageTypeType) messageValue.getMessageTypeRef().getIdref();
				}
				return messageType;
			}
		}
		return null;
	}

	protected static RoleTypeType getExecutor(MessageInTransactionTypeType mitt) {
		TransactionTypeType transactionType = getTransaction(mitt);
		return getExecutor(transactionType);

	}

	protected static RoleTypeType getExecutor(TransactionTypeType transactionType) {
		if (transactionType != null) {
			Executor executorValue = transactionType.getExecutor();
			if (executorValue != null) {
				RoleTypeType roleType = executorValue.getRoleType();
				if (roleType == null) {
					roleType = (RoleTypeType) executorValue.getRoleTypeRef().getIdref();
				}
				return roleType;
			}
		}
		return null;
	}

	protected static RoleTypeType getInitiator(TransactionTypeType transactionType) {
		if (transactionType != null) {
			Initiator initiatorValue = transactionType.getInitiator();
			if (initiatorValue != null) {
				RoleTypeType roleType = initiatorValue.getRoleType();
				if (roleType == null) {
					roleType = (RoleTypeType) initiatorValue.getRoleTypeRef().getIdref();
				}
				return roleType;
			}
		}
		return null;
	}

	protected static RoleTypeType getInitiator(MessageInTransactionTypeType mitt) {
		TransactionTypeType transactionType = getTransaction(mitt);
		return getInitiator(transactionType);
	}

	protected static List<MessageInTransactionTypeType> getNext(MessageInTransactionTypeType mitt) {
		if (mitt != null) {
			List<MessageInTransactionTypeType> next = null;

			List<MessageInTransactionTypeType> allMitts = Editor14.getStore14()
					.getElements(MessageInTransactionTypeType.class);
			for (MessageInTransactionTypeType mittElement : allMitts) {
				List<MessageInTransactionTypeType> previous = getPrevious(mittElement);
				if (previous != null) {
					for (MessageInTransactionTypeType prev : previous) {
						if (prev.getId().equals(mitt.getId())) {
							if (next == null) {
								next = new ArrayList<>();
							}
							next.add(mittElement);
						}
					}
				}
			}
			return next;
		}
		return null;
	}

	protected static List<MessageInTransactionTypeType> getPrevious(MessageInTransactionTypeType mitt) {
		if (mitt != null) {
			Previous previous = mitt.getPrevious();
			if (previous != null) {
				List<MessageInTransactionTypeType> prevs = new ArrayList<>();
				List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
				for (Object object : previousList) {
					MessageInTransactionTypeType prev = null;
					if (object instanceof MessageInTransactionTypeType) {
						prev = (MessageInTransactionTypeType) object;
					} else {
						prev = (MessageInTransactionTypeType) ((MessageInTransactionTypeTypeRef) object).getIdref();
					}
					prevs.add(prev);
				}
				return prevs;
			}
		}
		return null;
	}

	protected static void addPrevious(MessageInTransactionTypeType mitt, MessageInTransactionTypeType previousMitt) {
		Previous previous = mitt.getPrevious();
		if (previous == null) {
			previous = objectFactory.createMessageInTransactionTypeTypePrevious();
			mitt.setPrevious(previous);
		}
		MessageInTransactionTypeTypeRef mittRef = objectFactory.createMessageInTransactionTypeTypeRef();
		mittRef.setIdref(previousMitt);
		previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef().add(mittRef);
	}

	protected static int removePrevious(MessageInTransactionTypeType mitt, MessageInTransactionTypeType previousMitt) {
		int size = 0;
		Previous previous = mitt.getPrevious();
		if (previous != null) {
			List<Object> previousList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
			for (Object object : previousList) {
				MessageInTransactionTypeType prev = null;
				if (object instanceof MessageInTransactionTypeType) {
					prev = (MessageInTransactionTypeType) object;
				} else {
					prev = (MessageInTransactionTypeType) ((MessageInTransactionTypeTypeRef) object).getIdref();
				}
				if (prev.equals(previousMitt)) {
					previousList.remove(object);
					break;
				}
			}
			size = previousList.size();
			if (size == 0) {
				mitt.setPrevious(null);
			}
		}
		return size;
	}

	protected static List<ComplexElementTypeType> getComplexElements(ComplexElementTypeType complexElementParentType) {
		if (complexElementParentType != null) {
			ComplexElementTypeType.ComplexElements complexElements = complexElementParentType.getComplexElements();
			if (complexElements != null) {
				List<ComplexElementTypeType> complexElementTypeList = new ArrayList<>();
				ComplexElementTypeType complexElementType = null;
				List<Object> complexElementObjects = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				for (Object complexElementObject : complexElementObjects) {
					if (complexElementObject instanceof ComplexElementTypeType) {
						complexElementType = (ComplexElementTypeType) complexElementObject;
					} else {
						complexElementType = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) complexElementObject)
								.getIdref();
					}
					complexElementTypeList.add(complexElementType);
				}
				return complexElementTypeList;
			}
		}
		return null;
	}

	protected static List<SimpleElementTypeType> getSimpleElements(ComplexElementTypeType complexElementParentType) {
		if (complexElementParentType != null) {
			ComplexElementTypeType.SimpleElements simpleElements = complexElementParentType.getSimpleElements();
			if (simpleElements != null) {
				List<SimpleElementTypeType> simpleElementTypeList = new ArrayList<>();
				SimpleElementTypeType simpleElementType = null;
				List<Object> simpleElementObjects = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
				for (Object simpleElementObject : simpleElementObjects) {
					if (simpleElementObject instanceof SimpleElementTypeType) {
						simpleElementType = (SimpleElementTypeType) simpleElementObject;
					} else {
						simpleElementType = (SimpleElementTypeType) ((SimpleElementTypeTypeRef) simpleElementObject)
								.getIdref();
					}
					simpleElementTypeList.add(simpleElementType);
				}
				return simpleElementTypeList;
			}
		}
		return null;
	}

	protected static SimpleElementTypeType getSimpleElement(ElementConditionType elementCondition) {
		if (elementCondition != null) {
			ElementConditionType.SimpleElement simpleElement = elementCondition.getSimpleElement();
			if (simpleElement != null) {
				SimpleElementTypeType simpleElementType = simpleElement.getSimpleElementType();
				if (simpleElementType == null) {
					simpleElementType = (SimpleElementTypeType) simpleElement.getSimpleElementTypeRef().getIdref();
				}
				return simpleElementType;
			}
		}
		return null;
	}

	protected static UserDefinedTypeType getUserDefinedType(SimpleElementTypeType simpleElement) {
		if (simpleElement != null) {
			UserDefinedType userDefined = simpleElement.getUserDefinedType();
			if (userDefined != null) {
				UserDefinedTypeType userDefinedType = userDefined.getUserDefinedType();
				if (userDefinedType == null) {
					userDefinedType = (UserDefinedTypeType) userDefined.getUserDefinedTypeRef().getIdref();
				}
				return userDefinedType;
			}
		}
		return null;
	}

	protected static ComplexElementTypeType getComplexElement(ElementConditionType elementConditionType) {
		if (elementConditionType != null) {
			ComplexElement complexElement = elementConditionType.getComplexElement();
			if (complexElement != null) {
				ComplexElementTypeType complexElementType = complexElement.getComplexElementType();
				if (complexElementType == null) {
					complexElementType = (ComplexElementTypeType) complexElement.getComplexElementTypeRef().getIdref();
				}
				return complexElementType;
			}
		}
		return null;
	}

	protected static List<ComplexElementTypeType> getComplexElements(MessageTypeType messageType) {
		if (messageType != null) {
			ComplexElements complexElements = messageType.getComplexElements();
			if (complexElements != null) {
				List<ComplexElementTypeType> complexElementTypeList = new ArrayList<>();
				ComplexElementTypeType complexElementType = null;
				List<Object> complexElementObjects = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				for (Object complexElementObject : complexElementObjects) {
					if (complexElementObject instanceof ComplexElementTypeType) {
						complexElementType = (ComplexElementTypeType) complexElementObject;
					} else {
						complexElementType = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) complexElementObject)
								.getIdref();
					}
					complexElementTypeList.add(complexElementType);
				}
				return complexElementTypeList;
			}
		}
		return null;
	}

	protected static List<MessageInTransactionTypeConditionType> getConditions(MessageInTransactionTypeType mitt) {
		if (mitt != null) {
			Conditions conditions = mitt.getConditions();
			if (conditions != null) {
				List<MessageInTransactionTypeConditionType> conds = new ArrayList<>();
				List<Object> conditionsList = conditions
						.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
				for (Object object : conditionsList) {
					MessageInTransactionTypeConditionType cond = null;
					if (object instanceof MessageInTransactionTypeConditionType) {
						cond = (MessageInTransactionTypeConditionType) object;
					} else {
						cond = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) object)
								.getIdref();
					}
					conds.add(cond);
				}
				return conds;
			}
		}
		return null;
	}

	protected static List<MessageInTransactionTypeType> getSendAfters(MessageInTransactionTypeType mitt) {
		List<MessageInTransactionTypeConditionType> conditions = getConditions(mitt);
		if (conditions != null) {
			for (MessageInTransactionTypeConditionType condition : conditions) {
				SendAfter sendAfterValue = condition.getSendAfter();
				if (sendAfterValue != null) {
					List<MessageInTransactionTypeType> sendAfters = new ArrayList<>();
					List<Object> sendAftersList = sendAfterValue
							.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object sendAfterObject : sendAftersList) {
						MessageInTransactionTypeType sendAfter = null;
						if (sendAfterObject instanceof MessageInTransactionTypeType) {
							sendAfter = (MessageInTransactionTypeType) sendAfterObject;
						} else {
							sendAfter = (MessageInTransactionTypeType) ((MessageInTransactionTypeTypeRef) sendAfterObject)
									.getIdref();
						}
						sendAfters.add(sendAfter);
					}
					return sendAfters;
				}
			}
		}
		return null;
	}

	protected static void addSendAfter(MessageInTransactionTypeType mitt, MessageInTransactionTypeType sendAfterMitt) {
		Conditions conditions = mitt.getConditions();
		if (conditions == null) {
			conditions = objectFactory.createMessageInTransactionTypeTypeConditions();
			mitt.setConditions(conditions);
		}
		List<Object> conditionRefs = conditions
				.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
		if (conditionRefs.isEmpty()) {
			MessageInTransactionTypeConditionType condition = objectFactory
					.createMessageInTransactionTypeConditionType();
			condition.setId(Editor14.getStore14().getNewId("Condition"));
			conditionRefs.add(condition);
		}
		Object conditionObject = conditionRefs.get(0);
		MessageInTransactionTypeConditionType condition = null;
		if (conditionObject instanceof MessageInTransactionTypeConditionType) {
			condition = (MessageInTransactionTypeConditionType) conditionObject;
		} else {
			condition = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) conditionObject)
					.getIdref();
		}
		SendAfter sendAfters = condition.getSendAfter();
		if (sendAfters == null) {
			sendAfters = objectFactory.createMessageInTransactionTypeConditionTypeSendAfter();
			condition.setSendAfter(sendAfters);
		}
		MessageInTransactionTypeTypeRef mittRef = objectFactory.createMessageInTransactionTypeTypeRef();
		mittRef.setIdref(sendAfterMitt);
		sendAfters.getMessageInTransactionTypeOrMessageInTransactionTypeRef().add(mittRef);
	}

	protected static void removeSendAfter(MessageInTransactionTypeType mitt,
			MessageInTransactionTypeType sendAfterMitt) {
		Conditions conditions = mitt.getConditions();
		if (conditions != null) {
			List<Object> conditionRefs = conditions
					.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
			boolean found = false;
			for (Object conditionRef : conditionRefs) {
				MessageInTransactionTypeConditionType condition = null;
				if (conditionRef instanceof MessageInTransactionTypeConditionType) {
					condition = (MessageInTransactionTypeConditionType) conditionRef;
				} else {
					condition = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) conditionRef)
							.getIdref();
				}
				SendAfter sendAfter = condition.getSendAfter();
				if (sendAfter != null) {
					List<Object> sendAfterRefs = sendAfter.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object sendAfterRef : sendAfterRefs) {
						MessageInTransactionTypeType saMitt = null;
						if (sendAfterRef instanceof MessageInTransactionTypeType) {
							saMitt = (MessageInTransactionTypeType) sendAfterRef;
						} else {
							saMitt = (MessageInTransactionTypeType) (((MessageInTransactionTypeTypeRef) sendAfterRef)
									.getIdref());
						}
						if (saMitt.equals(sendAfterMitt)) {
							found = true;
							sendAfterRefs.remove(sendAfterRef);
							break;
						}
					}
					if (sendAfterRefs.size() == 0) {
						condition.setSendAfter(null);
					}

				}
				if (found) {
					return;
				}
			}
		}
	}

	protected static List<MessageInTransactionTypeType> getSendBefores(MessageInTransactionTypeType mitt) {
		List<MessageInTransactionTypeConditionType> conditions = getConditions(mitt);
		if (conditions != null) {
			for (MessageInTransactionTypeConditionType condition : conditions) {
				SendBefore sendBeforeValue = condition.getSendBefore();
				if (sendBeforeValue != null) {
					List<MessageInTransactionTypeType> sendBefores = new ArrayList<>();
					List<Object> sendBeforesList = sendBeforeValue
							.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object sendBeforeObject : sendBeforesList) {
						MessageInTransactionTypeType sendBefore = null;
						if (sendBeforeObject instanceof MessageInTransactionTypeType) {
							sendBefore = (MessageInTransactionTypeType) sendBeforeObject;
						} else {
							sendBefore = (MessageInTransactionTypeType) ((MessageInTransactionTypeTypeRef) sendBeforeObject)
									.getIdref();
						}
						sendBefores.add(sendBefore);
					}
					return sendBefores;
				}
			}
		}
		return null;
	}

	protected static void addSendBefore(MessageInTransactionTypeType mitt,
			MessageInTransactionTypeType sendBeforeMitt) {
		Conditions conditions = mitt.getConditions();
		if (conditions == null) {
			conditions = objectFactory.createMessageInTransactionTypeTypeConditions();
			mitt.setConditions(conditions);
		}
		List<Object> conditionRefs = conditions
				.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
		if (conditionRefs.isEmpty()) {
			MessageInTransactionTypeConditionType condition = objectFactory
					.createMessageInTransactionTypeConditionType();
			condition.setId(Editor14.getStore14().getNewId("Condition"));
			conditionRefs.add(condition);
		}
		Object conditionObject = conditionRefs.get(0);
		MessageInTransactionTypeConditionType condition = null;
		if (conditionObject instanceof MessageInTransactionTypeConditionType) {
			condition = (MessageInTransactionTypeConditionType) conditionObject;
		} else {
			condition = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) conditionObject)
					.getIdref();
		}
		SendBefore sendBefores = condition.getSendBefore();
		if (sendBefores == null) {
			sendBefores = objectFactory.createMessageInTransactionTypeConditionTypeSendBefore();
			condition.setSendBefore(sendBefores);
		}
		MessageInTransactionTypeTypeRef mittRef = objectFactory.createMessageInTransactionTypeTypeRef();
		mittRef.setIdref(sendBeforeMitt);
		sendBefores.getMessageInTransactionTypeOrMessageInTransactionTypeRef().add(mittRef);
	}

	protected static void removeSendBefore(MessageInTransactionTypeType mitt,
			MessageInTransactionTypeType sendBeforeMitt) {
		Conditions conditions = mitt.getConditions();
		if (conditions != null) {
			List<Object> conditionRefs = conditions
					.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
			boolean found = false;
			for (Object conditionRef : conditionRefs) {
				MessageInTransactionTypeConditionType condition = null;
				if (conditionRef instanceof MessageInTransactionTypeConditionType) {
					condition = (MessageInTransactionTypeConditionType) conditionRef;
				} else {
					condition = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) conditionRef)
							.getIdref();
				}
				SendBefore sendBefore = condition.getSendBefore();
				if (sendBefore != null) {
					List<Object> sendBeforeRefs = sendBefore.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object sendBeforeRef : sendBeforeRefs) {
						MessageInTransactionTypeType sbMitt = null;
						if (sendBeforeRef instanceof MessageInTransactionTypeType) {
							sbMitt = (MessageInTransactionTypeType) sendBeforeRef;
						} else {
							sbMitt = (MessageInTransactionTypeType) (((MessageInTransactionTypeTypeRef) sendBeforeRef)
									.getIdref());
						}
						if (sbMitt.equals(sendBeforeMitt)) {
							found = true;
							sendBeforeRefs.remove(sendBeforeRef);
							break;
						}
					}
					if (sendBeforeRefs.size() == 0) {
						condition.setSendBefore(null);
					}

				}
				if (found) {
					return;
				}
			}
		}
	}

	protected static String getCondition(MessageInTransactionTypeType mitt, ComplexElementTypeType ce,
			SimpleElementTypeType se) {
		List<ElementConditionType> elementConditions = Editor14.getStore14().getElements(ElementConditionType.class);
		for (ElementConditionType ec : elementConditions) {
			MessageInTransactionTypeType messageInTransaction = getMessageInTransaction(ec);
			ComplexElementTypeType complexElement = getComplexElement(ec);
			SimpleElementTypeType simpleElement = getSimpleElement(ec);
			String condition = ec.getCondition();
			if (mitt != null) {
				if (messageInTransaction != null && messageInTransaction.getId().equals(mitt.getId())) {
					if (ce != null) {
						if (complexElement != null) {
							if (complexElement.getId().equals(ce.getId())) {
								if (se != null) {
									if (simpleElement != null && simpleElement.getId().equals(se.getId())) {
										return condition;
									}
								} else {
									if (simpleElement == null) {
										return condition;
									}
								}
							} else {
								List<ComplexElementTypeType> parents = getParents(ce);
								if (parents != null) {
									for (ComplexElementTypeType parent : parents) {
										if (parent.getId().equals(complexElement.getId())) {
											if (se != null) {
												if (simpleElement != null && simpleElement.getId().equals(se.getId())) {
													return condition;
												}
											} else {
												if (simpleElement == null) {
													return condition;
												}
											}
										}
									}
								}
							}
						}
					} else {
						if (complexElement == null) {
							if (se != null) {
								if (simpleElement != null && simpleElement.getId().equals(se.getId())) {
									return condition;
								}
							} else {
								if (simpleElement == null) {
									return condition;
								}
							}
						}
					}
				}
			} else {
				if (messageInTransaction == null) {
					if (ce != null) {
						if (complexElement != null && complexElement.getId().equals(ce.getId())) {
							if (se != null) {
								if (simpleElement != null && simpleElement.getId().equals(se.getId())) {
									return condition;
								}
							} else {
								if (simpleElement == null) {
									return condition;
								}
							}
						}
					} else {
						if (complexElement == null) {
							if (se != null) {
								if (simpleElement != null && simpleElement.getId().equals(se.getId())) {
									return condition;
								}
							} else {
								if (simpleElement == null) {
									return condition;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	protected static String getFinalCondition(MessageInTransactionTypeType mitt, ComplexElementTypeType ce,
			SimpleElementTypeType se) {
		String condition = getCondition(mitt, ce, se);
		if (condition == null) {
			condition = getCondition(mitt, ce, null);
			if (condition == null) {
				condition = getCondition(mitt, null, se);
				if (condition == null) {
					condition = getCondition(mitt, null, null);
					if (condition == null) {
						condition = getCondition(null, ce, se);
						if (condition == null) {
							condition = getCondition(null, ce, null);
							if (condition == null) {
								condition = getCondition(null, null, se);
								if (condition == null) {
									condition = getCondition(null, null, null);
								}
							}
						}
					}
				}
			}
		}
		return condition;
	}

	protected static List<ComplexElementTypeType> getParents(ComplexElementTypeType ce) {
		List<ComplexElementTypeType> parents = null;
		List<ComplexElementTypeType> elements = Editor14.getStore14().getElements(ComplexElementTypeType.class);
		if (elements != null) {
			for (ComplexElementTypeType element : elements) {
				List<ComplexElementTypeType> childElements = getComplexElements(element);
				if (childElements != null) {
					if (childElements.contains(ce)) {
						if (parents == null) {
							parents = new ArrayList<>();
						}
						parents.add(element);
					}
				}
			}
		}
		return parents;
	}

	protected static void setElementConditionTypeMessageInTransaction(ElementConditionType elementConditionType,
			MessageInTransactionTypeType mitt) {
		ElementConditionType.MessageInTransaction messageInTransaction = objectFactory
				.createElementConditionTypeMessageInTransaction();
		MessageInTransactionTypeTypeRef messageInTransactionTypeTypeRef = objectFactory
				.createMessageInTransactionTypeTypeRef();
		messageInTransactionTypeTypeRef.setIdref(mitt);
		messageInTransaction.setMessageInTransactionTypeRef(messageInTransactionTypeTypeRef);
		elementConditionType.setMessageInTransaction(messageInTransaction);
	}

	protected static void setElementConditionTypeComplexElement(ElementConditionType elementConditionType,
			ComplexElementTypeType ce) {
		ElementConditionType.ComplexElement complexElement = objectFactory.createElementConditionTypeComplexElement();
		ComplexElementTypeTypeRef complexElementTypeTypeRef = objectFactory.createComplexElementTypeTypeRef();
		complexElementTypeTypeRef.setIdref(ce);
		complexElement.setComplexElementTypeRef(complexElementTypeTypeRef);
		elementConditionType.setComplexElement(complexElement);
	}

	protected static void setElementConditionTypeSimpleElement(ElementConditionType elementConditionType,
			SimpleElementTypeType se) {
		ElementConditionType.SimpleElement simpleElement = objectFactory.createElementConditionTypeSimpleElement();
		SimpleElementTypeTypeRef simpleElementTypeTypeRef = objectFactory.createSimpleElementTypeTypeRef();
		simpleElementTypeTypeRef.setIdref(se);
		simpleElement.setSimpleElementTypeRef(simpleElementTypeTypeRef);
		elementConditionType.setSimpleElement(simpleElement);
	}

	protected static ElementConditionType getElementConditionType(MessageInTransactionTypeType mitt,
			ComplexElementTypeType ce, SimpleElementTypeType se) {
		List<ElementConditionType> ecs = Editor14.getStore14().getElements(ElementConditionType.class);
		for (ElementConditionType ec : ecs) {
			MessageInTransactionTypeType messageInTransaction = getMessageInTransaction(ec);
			if ((messageInTransaction != null && messageInTransaction.getId().equals(mitt.getId()))
					|| (messageInTransaction == null && mitt == null)) {
				ComplexElementTypeType complexElement = getComplexElement(ec);
				if ((complexElement != null && complexElement.getId().equals(ce.getId()))
						|| (complexElement == null && ce == null)) {
					SimpleElementTypeType simpleElement = getSimpleElement(ec);
					if ((simpleElement != null && se != null && simpleElement.getId().equals(se.getId()))
							|| (simpleElement == null && se == null)) {
						return ec;
					}
				} else {
					SimpleElementTypeType simpleElement = getSimpleElement(ec);
					List<ComplexElementTypeType> parents = getParents(ce);
					if (parents != null) {
						for (ComplexElementTypeType parent : parents) {
							if (parent.getId().equals(complexElement.getId())) {
								if (se != null) {
									if ((simpleElement != null && se != null
											&& simpleElement.getId().equals(se.getId()))
											|| (simpleElement == null && se == null)) {
										return ec;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
}
