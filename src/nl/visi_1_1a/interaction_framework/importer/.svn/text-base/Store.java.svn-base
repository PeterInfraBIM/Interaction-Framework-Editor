package nl.visi_1_1a.interaction_framework.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.visi_1_1a.interaction_schema.AppendixTypeType;
import nl.visi_1_1a.interaction_schema.ComplexElementTypeType;
import nl.visi_1_1a.interaction_schema.ElementConditionType;
import nl.visi_1_1a.interaction_schema.ElementType;
import nl.visi_1_1a.interaction_schema.GroupTypeType;
import nl.visi_1_1a.interaction_schema.MessageInTransactionTypeType;
import nl.visi_1_1a.interaction_schema.MessageTypeType;
import nl.visi_1_1a.interaction_schema.OrganisationTypeType;
import nl.visi_1_1a.interaction_schema.PersonTypeType;
import nl.visi_1_1a.interaction_schema.ProjectTypeType;
import nl.visi_1_1a.interaction_schema.RoleTypeType;
import nl.visi_1_1a.interaction_schema.SimpleElementTypeType;
import nl.visi_1_1a.interaction_schema.TransactionPhaseTypeType;
import nl.visi_1_1a.interaction_schema.TransactionTypeType;
import nl.visi_1_1a.interaction_schema.UserDefinedTypeType;

public class Store {
	enum ElementTypeType {
		AppendixTypeType, ComplexElementTypeType, ElementConditionType, GroupTypeType, MessageInTransactionTypeType, MessageTypeType, OrganisationTypeType, PersonTypeType, ProjectTypeType, RoleTypeType, SimpleElementTypeType, TransactionPhaseTypeType, TransactionTypeType, UserDefinedTypeType;

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

	private Store() {
		store = new HashMap<String, Object>();
	}

	private static Store singleton;

	public static Store getStore() {
		if (singleton == null) {
			singleton = new Store();
		}
		return singleton;
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

	public void put(String id, Object element) {
		store.put(id, element);
	}

	public void clear() {
		store.clear();
	}

}
