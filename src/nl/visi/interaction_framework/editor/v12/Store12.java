package nl.visi.interaction_framework.editor.v12;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.visi.interaction_schema.AppendixTypeType;
import nl.visi.interaction_schema.ComplexElementTypeType;
import nl.visi.interaction_schema.ElementConditionType;
import nl.visi.interaction_schema.ElementType;
import nl.visi.interaction_schema.GroupTypeType;
import nl.visi.interaction_schema.MessageInTransactionTypeType;
import nl.visi.interaction_schema.MessageInTransactionTypeType.Previous;
import nl.visi.interaction_schema.MessageInTransactionTypeType.Transaction;
import nl.visi.interaction_schema.MessageInTransactionTypeTypeRef;
import nl.visi.interaction_schema.MessageTypeType;
import nl.visi.interaction_schema.OrganisationTypeType;
import nl.visi.interaction_schema.PersonTypeType;
import nl.visi.interaction_schema.ProjectTypeType;
import nl.visi.interaction_schema.RoleTypeType;
import nl.visi.interaction_schema.SimpleElementTypeType;
import nl.visi.interaction_schema.TransactionPhaseTypeType;
import nl.visi.interaction_schema.TransactionTypeType;
import nl.visi.interaction_schema.UserDefinedTypeType;

class Store12 {
	public enum ElementTypeType {
		AppendixTypeType, ComplexElementTypeType, ElementConditionType, GroupTypeType, MessageInTransactionTypeType,
		MessageTypeType, OrganisationTypeType, PersonTypeType, ProjectTypeType, RoleTypeType, SimpleElementTypeType,
		TransactionPhaseTypeType, TransactionTypeType, UserDefinedTypeType;

		public Class<?> getElementClass() {
			switch (this) {
			case AppendixTypeType:
				return AppendixTypeType.class;
			case ComplexElementTypeType:
				return ComplexElementTypeType.class;
			case ElementConditionType:
				return ElementConditionType.class;
			case GroupTypeType:
				return GroupTypeType.class;
			case MessageInTransactionTypeType:
				return MessageInTransactionTypeType.class;
			case MessageTypeType:
				return MessageTypeType.class;
			case OrganisationTypeType:
				return OrganisationTypeType.class;
			case PersonTypeType:
				return PersonTypeType.class;
			case ProjectTypeType:
				return ProjectTypeType.class;
			case RoleTypeType:
				return RoleTypeType.class;
			case SimpleElementTypeType:
				return SimpleElementTypeType.class;
			case TransactionPhaseTypeType:
				return TransactionPhaseTypeType.class;
			case TransactionTypeType:
				return TransactionTypeType.class;
			case UserDefinedTypeType:
				return UserDefinedTypeType.class;
			}
			return null;
		}
	}

	private final Map<String, Object> store;

	public Store12() {
		store = new HashMap<String, Object>();
	}

	public void put(String id, Object element) {
		store.put(id, element);
	}

	public Object get(String idref) {
		return store.get(idref);
	}

	public void remove(String idref) {
		store.remove(idref);
	}

	public void remove(ElementType element) {
		if (element instanceof MessageInTransactionTypeType) {
			List<Object> elements = getElements(MessageInTransactionTypeType.class);
			for (Object mittObj : elements) {
				MessageInTransactionTypeType mitt = (MessageInTransactionTypeType) mittObj;
				Previous previous = mitt.getPrevious();
				if (previous != null) {
					List<Object> prevMitts = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
					for (Object prevMittObj : prevMitts) {
						MessageInTransactionTypeType prevMitt = null;
						if (prevMittObj instanceof MessageInTransactionTypeType) {
							prevMitt = (MessageInTransactionTypeType) prevMittObj;
						} else {
							MessageInTransactionTypeTypeRef prevMittRef = (MessageInTransactionTypeTypeRef) prevMittObj;
							prevMitt = (MessageInTransactionTypeType) prevMittRef.getIdref();
						}
						if (mitt.equals(prevMitt)) {
							prevMitts.remove(mitt);
							break;
						}
					}
				}
			}
		} else if (element instanceof TransactionTypeType) {
			List<Object> elements = getElements(MessageInTransactionTypeType.class);
			for (Object mittObj : elements) {
				MessageInTransactionTypeType mitt = (MessageInTransactionTypeType) mittObj;
				Transaction transaction = mitt.getTransaction();
				if (transaction != null) {
					TransactionTypeType transactionType = transaction.getTransactionType();
					if (transactionType == null) {
						transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
					}
					if (((TransactionTypeType) element).equals(transactionType)) {
						store.remove(mitt.getId());
					}
				}
			}
		}
		store.remove(element.getId());
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getElements(Class<?> classType) {
		List<ElementType> list = new ArrayList<ElementType>();
		Iterator<Object> iterator = store.values().iterator();
		while (iterator.hasNext()) {
			ElementType element = (ElementType) iterator.next();
			if (element.getClass().equals(classType)) {
				boolean inserted = false;
				for (int index = 0; !inserted && index < list.size(); index++) {
					ElementType et = list.get(index);
					if (et.getId().compareToIgnoreCase(((ElementType) element).getId()) > 0) {
						list.add(index, element);
						inserted = true;
					}
				}
				if (!inserted) {
					list.add(element);
				}
			}
		}
		return (List<T>) list;
	}

	@SuppressWarnings("unchecked")
	public <T> T getElement(Class<T> elementClass, String idref) {
		Object element = store.get(idref);
		if (element != null) {
			if ((elementClass != null && elementClass.equals(ElementType.class))
					|| element.getClass().equals(elementClass)) {
				return (T) element;
			}
		}
		return null;
	}

	public String getNewId(String prefix) {
		int index = 1;
		while (store.containsKey(prefix + index)) {
			index++;
		}
		return prefix + index;
	}

	public void renameId(String oldId, String newId) throws Exception {
		if (oldId.equals(newId))
			return;
		if (store.containsKey(newId))
			throw new Exception("Id is not unique");
		if (newId.equals(""))
			throw new Exception("Id is empty");
		Object elementType = store.remove(oldId);
		store.put(newId, elementType);
	}

	public void clear() {
		store.clear();
	}
}
