package nl.visi.interaction_framework.editor.v16;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.SendAfter;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionType.SendBefore;
import nl.visi.schemas._20160331.MessageInTransactionTypeConditionTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Conditions;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.TransactionTypeType.Executor;
import nl.visi.schemas._20160331.TransactionTypeType.Initiator;

class RolesPanelControl16 extends PanelControl16<RoleTypeType> {
	private static final String ROLES_PANEL = "nl/visi/interaction_framework/editor/swixml/RolesPanel16.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_Transactions, tbl_Messages, tbl_Conditions;
	private JTextField tfd_ResponsibilityScope, tfd_ResponsibilityTask, tfd_ResponsibilitySupportTask,
			tfd_ResponsibilityFeedback;
	private TransactionsTableModel transactionsTableModel;
	private MessagesTableModel messagesTableModel;
	private ConditionsTableModel conditionsTableModel;

	private enum RoleTableColumns {
		Id, Description, StartDate, EndDate, State, DateLamu, UserLamu;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class RolesTableModel extends ElementsTableModel<RoleTypeType> {

		@Override
		public int getColumnCount() {
			return RoleTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return RoleTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			RoleTypeType role = get(rowIndex);
			switch (RoleTableColumns.values()[columnIndex]) {
			case Id:
				return role.getId();
			case Description:
				return role.getDescription();
			case StartDate:
				return getDate(role.getStartDate());
			case EndDate:
				return getDate(role.getEndDate());
			case State:
				return role.getState();
			case DateLamu:
				return getDateTime(role.getDateLaMu());
			case UserLamu:
				return role.getUserLaMu();
			default:
				return null;
			}
		}
	}

	private enum TransactionsTableColumns {
		Id, Description, Initiator, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class TransactionsTableModel extends ElementsTableModel<TransactionTypeType> {

		@Override
		public int getColumnCount() {
			return TransactionsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return TransactionsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			TransactionTypeType transaction = get(rowIndex);
			switch (TransactionsTableColumns.values()[columnIndex]) {
			case Id:
				return transaction.getId();
			case Description:
				return transaction.getDescription();
			case Initiator:
				Initiator initiator = transaction.getInitiator();
				if (initiator != null) {
					RoleTypeType roleType = initiator.getRoleType();
					if (roleType == null) {
						roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
					}
					if (selectedElement.equals(roleType)) {
						return true;
					} else {
						return false;
					}
				}
				return null;
			default:
				break;
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == TransactionsTableColumns.Initiator.ordinal())
				return Boolean.class;
			else
				return Object.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == TransactionsTableColumns.Navigate.ordinal()) ? true : false;
		}

	}

	private enum ConditionRuleType {
		Action, Trigger, SendAfter, SendBefore, Start, Stop;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	private class ConditionRule extends ElementType {
		private ConditionRuleType type;
		private MessageInTransactionTypeType mitt;

		public ConditionRule() {
			super();
		}

		public ConditionRule(ConditionRuleType type, MessageInTransactionTypeType mitt) {
			this();
			this.type = type;
			this.mitt = mitt;
		}

		public ConditionRuleType getType() {
			return type;
		}

		public MessageInTransactionTypeType getMitt() {
			return mitt;
		}

		@Override
		public String getId() {
			return mitt != null ? mitt.getId() : null;
		}

		@Override
		public String toString() {
			return mitt != null ? mitt.getId() : null;
		}

	}

	private class Condition {
		private MessageInTransactionTypeType mitt;

		@SuppressWarnings("unused")
		public Condition() {
		}

		public Condition(MessageInTransactionTypeType mitt) {
			this.mitt = mitt;
		}

		public List<MessageInTransactionTypeType> getTriggers() {
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

		public List<MessageInTransactionTypeType> getSendAfters() {
			if (mitt != null) {
				Conditions conditions = mitt.getConditions();
				if (conditions != null) {
					List<Object> conditionsList = conditions
							.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
					for (Object conditionObject : conditionsList) {
						MessageInTransactionTypeConditionType condition = null;
						if (conditionObject instanceof MessageInTransactionTypeConditionType) {
							condition = (MessageInTransactionTypeConditionType) conditionObject;
						} else {
							condition = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) conditionObject)
									.getIdref();
						}
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
			}
			return null;
		}

		public List<MessageInTransactionTypeType> getSendBefores() {
			if (mitt != null) {
				Conditions conditions = mitt.getConditions();
				if (conditions != null) {
					List<Object> conditionsList = conditions
							.getMessageInTransactionTypeConditionOrMessageInTransactionTypeConditionRef();
					for (Object conditionObject : conditionsList) {
						MessageInTransactionTypeConditionType condition = null;
						if (conditionObject instanceof MessageInTransactionTypeConditionType) {
							condition = (MessageInTransactionTypeConditionType) conditionObject;
						} else {
							condition = (MessageInTransactionTypeConditionType) ((MessageInTransactionTypeConditionTypeRef) conditionObject)
									.getIdref();
						}
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
			}
			return null;
		}

		public List<MessageInTransactionTypeType> getActions() {
			if (mitt != null) {
				List<MessageInTransactionTypeType> actions = null;

				List<MessageInTransactionTypeType> allMitts = Editor16.getStore16()
						.getElements(MessageInTransactionTypeType.class);
				for (MessageInTransactionTypeType mittElement : allMitts) {
					Previous previousValue = mittElement.getPrevious();
					if (previousValue != null) {
						List<Object> mittObjects = previousValue
								.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
						for (Object mittObject : mittObjects) {
							MessageInTransactionTypeType prev = null;
							if (mittObject instanceof MessageInTransactionTypeType) {
								prev = (MessageInTransactionTypeType) mittObject;
							} else {
								prev = (MessageInTransactionTypeType) ((MessageInTransactionTypeTypeRef) mittObject)
										.getIdref();
							}
							if (prev.getId().equals(mitt.getId())) {
								if (actions == null) {
									actions = new ArrayList<>();
								}
								actions.add(mittElement);
							}
						}
					}
				}
				return actions;
			}
			return null;
		}
	}

	private enum MessagesTableColumns {
		Id, Type, Transaction, Message;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class MessagesTableModel extends ElementsTableModel<MessageInTransactionTypeType> {

		@Override
		public int getColumnCount() {
			return MessagesTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return MessagesTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			MessageInTransactionTypeType mitt = get(rowIndex);
			switch (MessagesTableColumns.values()[columnIndex]) {
			case Id:
				return mitt.getId();
			case Type:
				Boolean initiatorToExecutor = mitt.isInitiatorToExecutor();
				if (initiatorToExecutor == null) {
					initiatorToExecutor = false;
				}
				RoleTypeType initiator = getInitiator(mitt);
				if (initiator != null) {
					if (selectedElement.equals(initiator)) {
						return initiatorToExecutor ? "out" : "in";
					} else {
						RoleTypeType executor = getExecutor(mitt);
						if (executor != null) {
							if (selectedElement.equals(executor)) {
								return initiatorToExecutor ? "in" : "out";
							}
						}
					}
				}
				return null;
			case Transaction:
				Transaction transaction = mitt.getTransaction();
				TransactionTypeType transactionType = transaction.getTransactionType();
				if (transactionType == null) {
					transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
				}
				return transactionType != null ? transactionType.getId() : null;
			case Message:
				Message message = mitt.getMessage();
				MessageTypeType messageType = message.getMessageType();
				if (messageType == null) {
					messageType = (MessageTypeType) message.getMessageTypeRef().getIdref();
				}
				return messageType != null ? messageType.getId() : null;
			default:
				break;
			}
			return null;
		}
	}

	private TransactionTypeType getTransaction(MessageInTransactionTypeType mitt) {
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

	private RoleTypeType getInitiator(MessageInTransactionTypeType mitt) {
		TransactionTypeType transactionType = getTransaction(mitt);
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

	private RoleTypeType getExecutor(MessageInTransactionTypeType mitt) {
		TransactionTypeType transactionType = getTransaction(mitt);
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

	private enum ConditionsTableColumns {
		Type, Id, Transaction, Message;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class ConditionsTableModel extends ElementsTableModel<ConditionRule> {

		@Override
		public int getColumnCount() {
			return ConditionsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return ConditionsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ConditionRule conditionRule = get(rowIndex);
			switch (ConditionsTableColumns.values()[columnIndex]) {
			case Type:
				return conditionRule.getType().name();
			case Id:
				return conditionRule.getId();
			case Transaction:
				if (conditionRule.getMitt() != null) {
					Transaction transaction = conditionRule.getMitt().getTransaction();
					if (transaction != null) {
						TransactionTypeType transactionType = transaction.getTransactionType();
						if (transactionType == null) {
							transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
						}
						return transactionType.getId();
					}
				}
				break;
			case Message:
				if (conditionRule.getMitt() != null) {
					Message message = conditionRule.getMitt().getMessage();
					if (message != null) {
						MessageTypeType messageType = message.getMessageType();
						if (messageType == null) {
							messageType = (MessageTypeType) message.getMessageTypeRef().getIdref();
						}
						return messageType.getId();
					}
				}
				break;
			}

			return null;
		}
	}

	public RolesPanelControl16() throws Exception {
		super(ROLES_PANEL);
		initRolesTable();
		initTransactionsTable();
		initMessagesTable();
		initConditionsTable();
		initStartDateField();
		initEndDateField();
		initResponsibilityScope();
		initResponsibilityTask();
		initResponsibilitySupportTask();
		initResponsibilityFeedback();
	}

	private void initResponsibilityFeedback() {
		tfd_ResponsibilityFeedback.getDocument().addDocumentListener(new DocumentAdapter16() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityFeedback(tfd_ResponsibilityFeedback.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilitySupportTask() {
		tfd_ResponsibilitySupportTask.getDocument().addDocumentListener(new DocumentAdapter16() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilitySupportTask(tfd_ResponsibilitySupportTask.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilityTask() {
		tfd_ResponsibilityTask.getDocument().addDocumentListener(new DocumentAdapter16() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityTask(tfd_ResponsibilityTask.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilityScope() {
		tfd_ResponsibilityScope.getDocument().addDocumentListener(new DocumentAdapter16() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityScope(tfd_ResponsibilityScope.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initEndDateField() {
		endDateField = new DateField16(endDatePanel);
		endDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date endDate = endDateField.getDate();
					if (endDate != null) {
						gcal.setTime(endDate);
						XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
						selectedElement.setEndDate(xgcal);
						elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
						if (!inSelection) {
							updateLaMu(selectedElement, user);
							elementsTableModel.update(selectedRow);
						}
					}
				} catch (DatatypeConfigurationException e1) {
					e1.printStackTrace();
				}
			}
		});
		endDateField.setEnabled(false);
	}

	private void initStartDateField() {
		startDateField = new DateField16(startDatePanel);
		startDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date startDate = startDateField.getDate();
					if (startDate != null) {
						gcal.setTime(startDateField.getDate());
						XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
						selectedElement.setStartDate(xgcal);
						elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
						if (!inSelection) {
							updateLaMu(selectedElement, user);
							elementsTableModel.update(selectedRow);
						}
					}
				} catch (DatatypeConfigurationException e1) {
					e1.printStackTrace();
				}
			}
		});
		startDateField.setEnabled(false);
	}

	@SuppressWarnings("serial")
	private void initTransactionsTable() {
		transactionsTableModel = new TransactionsTableModel();
		tbl_Transactions.setModel(transactionsTableModel);
		tbl_Transactions.setFillsViewportHeight(true);
		tbl_Transactions.getColumnModel().getColumn(TransactionsTableColumns.Initiator.ordinal()).setMaxWidth(50);
		TableColumn navigateColumn = tbl_Transactions.getColumnModel()
				.getColumn(TransactionsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_Transactions.getSelectedRow();
				Editor16.getMainFrameControl().navigate(transactionsTableModel.get(row));
			}
		});
	}

	private void initMessagesTable() {
		messagesTableModel = new MessagesTableModel();
		tbl_Messages.setModel(messagesTableModel);
		tbl_Messages.setFillsViewportHeight(true);
//		tbl_Messages.getColumnModel().getColumn(MessagesTableColumns.Id.ordinal()).setMaxWidth(50);
//		TableColumn navigateColumn = tbl_Messages.getColumnModel()
//				.getColumn(MessagesTableColumns.Navigate.ordinal());
//		navigateColumn.setMaxWidth(50);
//		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
//		navigateColumn.setCellEditor(new NavigatorEditor() {
//			@Override
//			protected void navigate() {
//				int row = tbl_Transactions.getSelectedRow();
//				Editor16.getMainFrameControl().navigate(transactionsTableModel.get(row));
//			}
//		});
		tbl_Messages.getSelectionModel().addListSelectionListener(messageTableSelectionListener);
	}

	private ListSelectionListener messageTableSelectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedRow = tbl_Messages.getSelectedRow();
			boolean selectedMessage = selectedRow >= 0;
			conditionsTableModel.clear();
			tbl_Conditions.setEnabled(selectedMessage);
			if (selectedMessage) {
				String inOut = (String) messagesTableModel.getValueAt(selectedRow, MessagesTableColumns.Type.ordinal());
				MessageInTransactionTypeType mitt = messagesTableModel.get(selectedRow);
				Condition condition = new Condition(mitt);
				if (inOut.contentEquals("out")) {
					List<MessageInTransactionTypeType> triggers = condition.getTriggers();
					if (triggers != null) {
						for (MessageInTransactionTypeType trigger : triggers) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.Trigger, trigger));
						}
					} else {
						conditionsTableModel.add(new ConditionRule(ConditionRuleType.Start, null));
					}
					List<MessageInTransactionTypeType> sendAfters = condition.getSendAfters();
					if (sendAfters != null) {
						for (MessageInTransactionTypeType sendAfter : sendAfters) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.SendAfter, sendAfter));
						}
					}
					List<MessageInTransactionTypeType> sendBefores = condition.getSendBefores();
					if (sendBefores != null) {
						for (MessageInTransactionTypeType sendBefore : sendBefores) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.SendBefore, sendBefore));
						}
					}
				} else {
					List<MessageInTransactionTypeType> actions = condition.getActions();
					if (actions != null) {
						for (MessageInTransactionTypeType action : actions) {
							conditionsTableModel.add(new ConditionRule(ConditionRuleType.Action, action));
						}
					} else {
						conditionsTableModel.add(new ConditionRule(ConditionRuleType.Stop, null));
					}
				}
			}
		}
	};

	private void initConditionsTable() {
		conditionsTableModel = new ConditionsTableModel();
		tbl_Conditions.setModel(conditionsTableModel);
		tbl_Conditions.setFillsViewportHeight(true);
	}

	private void initRolesTable() {
		elementsTableModel = new RolesTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setFillsViewportHeight(true);
		tbl_Elements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updateSelectionArea(e);
			}
		});
	}

	public void fillTable() {
		fillTable(RoleTypeType.class);
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;

		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		boolean rowSelected = selectedRow >= 0;
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		startDateField.setEnabled(rowSelected);
		endDateField.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_Code.setEnabled(rowSelected);
		tfd_ResponsibilityScope.setEnabled(rowSelected);
		tfd_ResponsibilityTask.setEnabled(rowSelected);
		tfd_ResponsibilitySupportTask.setEnabled(rowSelected);
		tfd_ResponsibilityFeedback.setEnabled(rowSelected);

		tbl_Transactions.setEnabled(rowSelected);
		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			XMLGregorianCalendar startDate = selectedElement.getStartDate();
			if (startDate != null) {
				startDateField.setDate(selectedElement.getStartDate().toGregorianCalendar().getTime());
			}
			XMLGregorianCalendar endDate = selectedElement.getEndDate();
			if (endDate != null) {
				endDateField.setDate(selectedElement.getEndDate().toGregorianCalendar().getTime());
			}
			tfd_State.setText(selectedElement.getState());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_Code.setText(selectedElement.getCode());
			tfd_ResponsibilityScope.setText(selectedElement.getResponsibilityScope());
			tfd_ResponsibilityTask.setText(selectedElement.getResponsibilityTask());
			tfd_ResponsibilitySupportTask.setText(selectedElement.getResponsibilitySupportTask());
			tfd_ResponsibilityFeedback.setText(selectedElement.getResponsibilityFeedback());

			transactionsTableModel.clear();
			List<TransactionTypeType> elements = Editor16.getStore16().getElements(TransactionTypeType.class);
			for (TransactionTypeType transaction : elements) {
				Initiator initiator = transaction.getInitiator();
				if (initiator != null) {
					RoleTypeType roleType = initiator.getRoleType();
					if (roleType == null) {
						roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
					}
					if (selectedElement.equals(roleType)) {
						transactionsTableModel.add(transaction);
						continue;
					}
				}
				Executor executor = transaction.getExecutor();
				if (executor != null) {
					RoleTypeType roleType = executor.getRoleType();
					if (roleType == null) {
						roleType = (RoleTypeType) executor.getRoleTypeRef().getIdref();
					}
					if (selectedElement.equals(roleType)) {
						transactionsTableModel.add(transaction);
						continue;
					}
				}
			}
			fillMessagesTable();
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			startDateField.setDate(null);
			endDateField.setDate(null);
			tfd_State.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			tfd_Code.setText("");
			tfd_ResponsibilityScope.setText("");
			tfd_ResponsibilityTask.setText("");
			tfd_ResponsibilitySupportTask.setText("");
			tfd_ResponsibilityFeedback.setText("");
			transactionsTableModel.clear();
			messagesTableModel.clear();
			conditionsTableModel.clear();
		}
		inSelection = false;
	}

	private void fillMessagesTable() {
		messagesTableModel.clear();
		List<MessageInTransactionTypeType> mitts = Editor16.getStore16()
				.getElements(MessageInTransactionTypeType.class);
		if (mitts != null) {
			for (MessageInTransactionTypeType mitt : mitts) {
				Transaction transaction = mitt.getTransaction();
				if (transaction != null) {
					TransactionTypeType transactionType = transaction.getTransactionType();
					if (transactionType == null) {
						transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
						Initiator initiator = transactionType.getInitiator();
						if (initiator != null) {
							RoleTypeType roleType = initiator.getRoleType();
							if (roleType == null) {
								roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
							}
							if (selectedElement.equals(roleType)) {
								messagesTableModel.add(mitt);
							}
						}
						Executor executor = transactionType.getExecutor();
						if (executor != null) {
							RoleTypeType roleType = executor.getRoleType();
							if (roleType == null) {
								roleType = (RoleTypeType) executor.getRoleTypeRef().getIdref();
							}
							if (selectedElement.equals(roleType)) {
								messagesTableModel.add(mitt);
							}
						}
					}
				}
			}
		}
	}

	public void newElement() {
		try {
			RoleTypeType newRoleType = objectFactory.createRoleTypeType();
			newElement(newRoleType, "Role_");
			int row = elementsTableModel.add(newRoleType);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		RoleTypeType roleType = elementsTableModel.get(row);

		List<TransactionTypeType> elements = store.getElements(TransactionTypeType.class);
		for (TransactionTypeType element : elements) {
			Initiator initiator = element.getInitiator();
			if (initiator != null) {
				RoleTypeType role = initiator.getRoleType();
				if (role == null) {
					role = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
				}
				if (role != null && role.equals(roleType)) {
					element.setInitiator(null);
				}
			}

			Executor executor = element.getExecutor();
			if (executor != null) {
				RoleTypeType role = executor.getRoleType();
				if (role == null) {
					role = (RoleTypeType) executor.getRoleTypeRef().getIdref();
				}
				if (role != null && role.equals(roleType)) {
					element.setExecutor(null);
				}
			}
		}

		Editor16.getStore16().remove(roleType.getId());
		elementsTableModel.remove(row);
	}
}
