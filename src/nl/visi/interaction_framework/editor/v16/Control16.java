package nl.visi.interaction_framework.editor.v16;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.table.AbstractTableModel;
import javax.xml.datatype.XMLGregorianCalendar;

import org.swixml.SwingEngine;

import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.ObjectFactory;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.SendAfter;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.SendBefore;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Conditions;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.TransactionTypeType.Executor;
import nl.visi.schemas._20160331.TransactionTypeType.Initiator;

abstract class Control16 {
	public static final String RESOURCE_BUNDLE = "nl.visi.interaction_framework.editor.locale.Editor";
	private static final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
	// static final java.text.DateFormat sdfDate = SimpleDateFormat.getDateInstance();
	static final java.text.DateFormat sdfDate = new SimpleDateFormat("d MMM yyyy");
	// private static final java.text.DateFormat sdfDateTime = SimpleDateFormat.getDateTimeInstance();
	static final java.text.DateFormat sdfDateTime = new SimpleDateFormat("d MMM yyyy HH:mm:ss");	
	private final SwingEngine swingEngine;
	protected static final ObjectFactory objectFactory = new ObjectFactory();
	protected static final GregorianCalendar gcal = new GregorianCalendar();
	protected PropertyChangeSupport propertyChangeSupport;
	protected static String user = "???";
	protected static Preferences userPrefs = Preferences.userNodeForPackage(Control16.class);

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

	public Control16() {
		swingEngine = new SwingEngine(this);
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	protected static ResourceBundle getBundle() {
		return bundle;
	}

	protected Component render(String swiXmlResource) throws Exception {
		Component component = null;
		component = swingEngine.render(swiXmlResource);
		return component;
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

}
