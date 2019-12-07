package nl.visi.interaction_framework.editor.v16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.GroupTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.OrganisationTypeType;
import nl.visi.schemas._20160331.PersonTypeType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.TransactionPhaseTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.UserDefinedTypeType;

class Store16 {
	enum ElementTypeType {
		AppendixTypeType, //
		ComplexElementTypeType, //
		ElementConditionType, //
		GroupTypeType, //
		MessageInTransactionTypeConditionType, //
		MessageInTransactionTypeType, //
		MessageTypeType, //
		OrganisationTypeType, //
		PersonTypeType, //
		ProjectTypeType, //
		RoleTypeType, //
		SimpleElementTypeType, //
		TransactionPhaseTypeType, //
		TransactionTypeType, //
		UserDefinedTypeType;

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
			case MessageInTransactionTypeConditionType:
				return MessageInTransactionTypeConditionType.class;
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

	public Store16() {
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
