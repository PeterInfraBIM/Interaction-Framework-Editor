package nl.visi.interaction_framework.editor.v14;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20140331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;
import nl.visi.schemas._20140331.TransactionTypeType.Executor;
import nl.visi.schemas._20140331.TransactionTypeType.Initiator;

public class MessagesPanelControl14 extends PanelControl14<MessageTypeType> {
	private static final String MESSAGES_PANEL = "nl/visi/interaction_framework/editor/swixml/MessagesPanel14.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_ComplexElements, tbl_Transactions;
//	private JTable tbl_Appendices;
	private ComplexElementsTableModel complexElementsTableModel;
//	private AppendicesTableModel appendicesTableModel;
	private TransactionsTableModel transactionsTableModel;
	private JComboBox<String> cbx_ComplexElements;
//	private JComboBox<String> cbx_Appendices;
	private JButton btn_AddComplexElement, btn_RemoveComplexElement;
//	private JButton btn_AddAppendix, btn_RemoveAppendix;
//	private JCheckBox chb_AppendixMandatory;

	private enum MessagesTableColumns {
		Id, Description, StartDate, EndDate, State, DateLamu, UserLamu;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class MessagesTableModel extends ElementsTableModel<MessageTypeType> {

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
			MessageTypeType message = get(rowIndex);
			switch (MessagesTableColumns.values()[columnIndex]) {
			case Id:
				return message.getId();
			case Description:
				return message.getDescription();
			case StartDate:
				return getDate(message.getStartDate());
			case EndDate:
				return getDate(message.getEndDate());
			case State:
				return message.getState();
			case DateLamu:
				return getDateTime(message.getDateLaMu());
			case UserLamu:
				return message.getUserLaMu();
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

	private enum ComplexElementsTableColumns {
		Id, Description, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class ComplexElementsTableModel extends ElementsTableModel<ComplexElementTypeType> {

		@Override
		public int getColumnCount() {
			return ComplexElementsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return ComplexElementsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ComplexElementTypeType complexElement = get(rowIndex);
			switch (ComplexElementsTableColumns.values()[columnIndex]) {
			case Id:
				return complexElement.getId();
			case Description:
				return complexElement.getDescription();
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == ComplexElementsTableColumns.Navigate.ordinal();
		}

	}

	public ComplexElementsTableModel getComplexElementsTableModel() {
		return complexElementsTableModel;
	}

	private enum TransactionsTableColumns {
		Id, Initiator, Executor, InitiatorToExecutor, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class TransactionsTableModel extends ElementsTableModel<MessageInTransactionTypeType> {

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
			MessageInTransactionTypeType mitt = get(rowIndex);

			TransactionTypeType transactionType = null;
			Transaction transaction = mitt.getTransaction();
			if (transaction != null) {
				transactionType = transaction.getTransactionType();
				if (transactionType == null) {
					transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
				}
			}

			switch (TransactionsTableColumns.values()[columnIndex]) {
			case Id:
				if (transactionType != null) {
					return transactionType.getId();
				}
				return null;
			case Initiator:
				if (transactionType != null) {
					Initiator initiator = transactionType.getInitiator();
					if (initiator != null) {
						RoleTypeType roleType = initiator.getRoleType();
						if (roleType == null) {
							roleType = (RoleTypeType) initiator.getRoleTypeRef().getIdref();
						}
						if (roleType != null) {
							return roleType.getId();
						}
					}
				}
				return null;
			case Executor:
				if (transactionType != null) {
					Executor executor = transactionType.getExecutor();
					if (executor != null) {
						RoleTypeType roleType = executor.getRoleType();
						if (roleType == null) {
							roleType = (RoleTypeType) executor.getRoleTypeRef().getIdref();
						}
						if (roleType != null) {
							return roleType.getId();
						}
					}
				}
				return null;
			case InitiatorToExecutor:
				return mitt.isInitiatorToExecutor();
			default:
				break;
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == TransactionsTableColumns.InitiatorToExecutor.ordinal())
				return Boolean.class;
			return Object.class;
		}

		@Override
		protected String getSortId(MessageInTransactionTypeType element) {
			Transaction transaction = element.getTransaction();
			if (transaction != null) {
				TransactionTypeType transactionType = transaction.getTransactionType();
				if (transactionType == null) {
					transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
				}
				if (transactionType != null) {
					return transactionType.getId();
				}
			}
			return super.getSortId(element);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == TransactionsTableColumns.Navigate.ordinal();
		}

	}

	public TransactionsTableModel getTransactionsTableModel() {
		return transactionsTableModel;
	}

//	private enum AppendicesTableColumns {
//		Id, Description, Navigate;
//
//		@Override
//		public String toString() {
//			return getBundle().getString("lbl_" + name());
//		}
//
//	}
//
//	@SuppressWarnings("serial")
//	public class AppendicesTableModel extends ElementsTableModel<AppendixTypeType> {
//
//		@Override
//		public int getColumnCount() {
//			return AppendicesTableColumns.values().length;
//		}
//
//		@Override
//		public String getColumnName(int columnIndex) {
//			return AppendicesTableColumns.values()[columnIndex].toString();
//		}
//
//		@Override
//		public Object getValueAt(int rowIndex, int columnIndex) {
//			AppendixTypeType appendixElement = get(rowIndex);
//			switch (AppendicesTableColumns.values()[columnIndex]) {
//			case Id:
//				return appendixElement.getId();
//			case Description:
//				return appendixElement.getDescription();
//			default:
//				break;
//			}
//			return null;
//		}
//
//		@Override
//		public boolean isCellEditable(int rowIndex, int columnIndex) {
//			return columnIndex == AppendicesTableColumns.Navigate.ordinal();
//		}
//
//	}

	public MessagesPanelControl14() throws Exception {
		super(MESSAGES_PANEL);
		initMessagesTable();
		initComplexElementsTable();
		initTransactionsTable();
//		initAppendicesTable();
		initStartDateField();
		initEndDateField();
	}

	private void initEndDateField() {
		endDateField = new DateField14(endDatePanel);
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
		startDateField = new DateField14(startDatePanel);
		startDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date startDate = startDateField.getDate();
					if (startDate != null) {
						gcal.setTime(startDate);
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

	private void initMessagesTable() {
		elementsTableModel = new MessagesTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setFillsViewportHeight(true);
		tbl_Elements.setAutoCreateRowSorter(true);
		TableRowSorter<ElementsTableModel<MessageTypeType>> tableRowSorter = new TableRowSorter<>(elementsTableModel);
		tableRowSorter.setComparator(MessagesTableColumns.StartDate.ordinal(), dateComparator);
		tableRowSorter.setComparator(MessagesTableColumns.EndDate.ordinal(), dateComparator);
		tableRowSorter.setComparator(MessagesTableColumns.DateLamu.ordinal(), dateTimeComparator);
		tbl_Elements.setRowSorter(tableRowSorter);
		tbl_Elements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updateSelectionArea(e);
			}
		});
	}

	@SuppressWarnings("serial")
	private void initTransactionsTable() {
		transactionsTableModel = new TransactionsTableModel();
		tbl_Transactions.setModel(transactionsTableModel);
		tbl_Transactions.setAutoCreateRowSorter(true);
		tbl_Transactions.setFillsViewportHeight(true);
		TableColumn navigateColumn = tbl_Transactions.getColumnModel()
				.getColumn(TransactionsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_Transactions.getSelectedRow();
				MessageInTransactionTypeType mitt = transactionsTableModel.get(row);
				Transaction transaction = mitt.getTransaction();
				if (transaction != null) {
					TransactionTypeType transactionType = transaction.getTransactionType();
					if (transactionType == null) {
						transactionType = (TransactionTypeType) transaction.getTransactionTypeRef().getIdref();
					}
					if (transactionType != null) {
						InteractionFrameworkEditor.navigate(transactionType);
					}
				}
			}
		});
	}

	@SuppressWarnings("serial")
	private void initComplexElementsTable() {
		complexElementsTableModel = new ComplexElementsTableModel();
		complexElementsTableModel.setSorted(false);
		tbl_ComplexElements.setModel(complexElementsTableModel);
		tbl_ComplexElements.setAutoCreateRowSorter(true);
		tbl_ComplexElements.setFillsViewportHeight(true);
		tbl_ComplexElements.setDropMode(DropMode.INSERT_ROWS);
		tbl_ComplexElements
				.setTransferHandler(getTransferHandler(tbl_ComplexElements, complexElementsTableModel, true));
		tbl_ComplexElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tbl_ComplexElements.getSelectedRow();
				btn_RemoveComplexElement.setEnabled(selectedRow >= 0);
			}
		});
		TableColumn navigateColumn = tbl_ComplexElements.getColumnModel()
				.getColumn(ComplexElementsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_ComplexElements.getSelectedRow();
				ComplexElementTypeType complexElementTypeType = complexElementsTableModel.get(row);
				if (complexElementTypeType != null) {
					InteractionFrameworkEditor.navigate(complexElementTypeType);
				}
			}
		});
	}

//	@SuppressWarnings("serial")
//	private void initAppendicesTable() {
//		appendicesTableModel = new AppendicesTableModel();
//		appendicesTableModel.setSorted(false);
//		tbl_Appendices.setModel(appendicesTableModel);
//		tbl_Appendices.setAutoCreateRowSorter(true);
//		tbl_Appendices.setFillsViewportHeight(true);
//		tbl_Appendices.setDropMode(DropMode.INSERT_ROWS);
//		tbl_Appendices.setTransferHandler(getTransferHandler(tbl_Appendices, appendicesTableModel, true));
//		tbl_Appendices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				int selectedRow = tbl_Appendices.getSelectedRow();
//				btn_RemoveAppendix.setEnabled(selectedRow >= 0);
//			}
//		});
//		TableColumn navigateColumn = tbl_Appendices.getColumnModel()
//				.getColumn(AppendicesTableColumns.Navigate.ordinal());
//		navigateColumn.setMaxWidth(50);
//		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
//		navigateColumn.setCellEditor(new NavigatorEditor() {
//			@Override
//			protected void navigate() {
//				int row = tbl_Appendices.getSelectedRow();
//				AppendixTypeType appendixTypeType = appendicesTableModel.get(row);
//				if (appendixTypeType != null) {
//					Editor14.getMainFrameControl().navigate(appendixTypeType);
//				}
//			}
//		});
//	}

	@Override
	public void fillTable() {
		fillTable(MessageTypeType.class);
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
//		chb_AppendixMandatory.setEnabled(rowSelected);
		tbl_ComplexElements.setEnabled(rowSelected);
		tbl_Transactions.setEnabled(rowSelected);
//		tbl_Appendices.setEnabled(rowSelected);
		cbx_ComplexElements.setEnabled(rowSelected);
//		cbx_Appendices.setEnabled(rowSelected);
		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			XMLGregorianCalendar startDate = selectedElement.getStartDate();
			if (startDate != null) {
				startDateField.setDate(startDate.toGregorianCalendar().getTime());
			}
			XMLGregorianCalendar endDate = selectedElement.getEndDate();
			if (endDate != null) {
				endDateField.setDate(endDate.toGregorianCalendar().getTime());
			}
			tfd_State.setText(selectedElement.getState());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_Code.setText(selectedElement.getCode());
//			Boolean appendixMandatory = selectedElement.isAppendixMandatory();
//			chb_AppendixMandatory
//					.setSelected(appendixMandatory != null ? selectedElement.isAppendixMandatory() : false);

			complexElementsTableModel.clear();
			MessageTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
			if (complexElements != null) {
				List<Object> ceList = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				for (Object object : ceList) {
					ComplexElementTypeType element = null;
					if (object instanceof ComplexElementTypeTypeRef) {
						element = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object).getIdref();
					} else {
						element = (ComplexElementTypeType) object;
					}
					complexElementsTableModel.add(element);
				}
			}
			cbx_ComplexElements.removeAllItems();
			cbx_ComplexElements.addItem(null);
			List<ComplexElementTypeType> ceElements = Editor14.getStore14().getElements(ComplexElementTypeType.class);
			for (ComplexElementTypeType element : ceElements) {
				cbx_ComplexElements.addItem(element.getId());
			}

//			appendicesTableModel.clear();
//			AppendixTypes appendixTypes = selectedElement.getAppendixTypes();
//			if (appendixTypes != null) {
//				List<Object> appendixList = appendixTypes.getAppendixTypeOrAppendixTypeRef();
//				for (Object object : appendixList) {
//					AppendixTypeType element = null;
//					if (object instanceof AppendixTypeTypeRef) {
//						element = (AppendixTypeType) ((AppendixTypeTypeRef) object).getIdref();
//					} else {
//						element = (AppendixTypeType) object;
//					}
//					appendicesTableModel.add(element);
//				}
//			}
//			cbx_Appendices.removeAllItems();
//			cbx_Appendices.addItem(null);
//			List<AppendixTypeType> apElements = Editor14.getStore14().getElements(AppendixTypeType.class);
//			for (AppendixTypeType element : apElements) {
//				cbx_Appendices.addItem(element.getId());
//			}

			transactionsTableModel.clear();
			List<MessageInTransactionTypeType> mittList = Editor14.getStore14()
					.getElements(MessageInTransactionTypeType.class);
			if (mittList != null) {
				for (MessageInTransactionTypeType mitt : mittList) {
					Message message = mitt.getMessage();
					if (message != null) {
						MessageTypeType msg = message.getMessageType();
						if (msg == null) {
							msg = (MessageTypeType) message.getMessageTypeRef().getIdref();
						}
						if (selectedElement.equals(msg)) {
							transactionsTableModel.add(mitt);
						}
					}
				}
			}
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
//			chb_AppendixMandatory.setSelected(false);
			transactionsTableModel.clear();
			complexElementsTableModel.clear();
		}
		inSelection = false;
	}

	public void newElement() {
		try {
			MessageTypeType newMessageType = objectFactory.createMessageTypeType();
			newElement(newMessageType, "Message_");

			int row = elementsTableModel.add(newMessageType);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store14 store = Editor14.getStore14();
		int row = tbl_Elements.getSelectedRow();
		MessageTypeType messageType = elementsTableModel.get(row);

		List<MessageInTransactionTypeType> elements = store.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType element : elements) {
			Message message = element.getMessage();
			if (message != null) {
				MessageTypeType msgType = message.getMessageType();
				if (msgType == null) {
					msgType = (MessageTypeType) message.getMessageTypeRef().getIdref();
				}
				if (msgType != null && msgType.equals(messageType)) {
					for (MessageInTransactionTypeType subElement : elements) {
						Previous previous = subElement.getPrevious();
						if (previous != null) {
							List<Object> prevList = previous.getMessageInTransactionTypeOrMessageInTransactionTypeRef();
							for (Object prev : prevList) {
								MessageInTransactionTypeType prevMitt = (MessageInTransactionTypeType) getElementType(
										prev);
								if (prevMitt.equals(element)) {
									prevList.remove(prevMitt);
									break;
								}
							}
						}
					}
					store.remove(element.getId());
				}
			}
		}

		store.remove(messageType.getId());
		elementsTableModel.remove(row);
	}

	public void selectComplexElement() {
		int selectedIndex = cbx_ComplexElements.getSelectedIndex();
		btn_AddComplexElement.setEnabled(selectedIndex > 0);
	}

//	public void selectAppendix() {
//		int selectedIndex = cbx_Appendices.getSelectedIndex();
//		btn_AddAppendix.setEnabled(selectedIndex > 0);
//	}

//	public void setAppendixMandatory() {
//		selectedElement.setAppendixMandatory(chb_AppendixMandatory.isSelected());
//	}

	public void addComplexElement() {
		String ceId = (String) cbx_ComplexElements.getSelectedItem();
		ComplexElementTypeType element = Editor14.getStore14().getElement(ComplexElementTypeType.class, ceId);
		ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
		ref.setIdref(element);
		MessageTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
		if (complexElements == null) {
			complexElements = objectFactory.createMessageTypeTypeComplexElements();
			selectedElement.setComplexElements(complexElements);
		}
		List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		list.add(ref);
		complexElementsTableModel.add(element);
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
		cbx_ComplexElements.setSelectedItem(null);
	}

	public void removeComplexElement() {
		int selectedRow = tbl_ComplexElements.getSelectedRow();

		ComplexElementTypeType complexElement = complexElementsTableModel.remove(selectedRow);
		MessageTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
		List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		for (Object object : list) {
			ComplexElementTypeType element = null;
			if (object instanceof ComplexElementTypeTypeRef) {
				element = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object).getIdref();
			} else if (object instanceof ComplexElementTypeType) {
				element = (ComplexElementTypeType) object;
			}
			if (element != null) {
				if (complexElement.equals(element)) {
					list.remove(object);
					break;
				}
			}
		}
		if (list.isEmpty()) {
			selectedElement.setComplexElements(null);
		}
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

//	public void addAppendix() {
//		String appId = (String) cbx_Appendices.getSelectedItem();
//		AppendixTypeType element = Editor14.getStore14().getElement(AppendixTypeType.class, appId);
//		AppendixTypeTypeRef ref = objectFactory.createAppendixTypeTypeRef();
//		ref.setIdref(element);
//		AppendixTypes appendixTypes = selectedElement.getAppendixTypes();
//		if (appendixTypes == null) {
//			appendixTypes = objectFactory.createMessageTypeTypeAppendixTypes();
//			selectedElement.setAppendixTypes(appendixTypes);
//		}
//		List<Object> list = appendixTypes.getAppendixTypeOrAppendixTypeRef();
//		list.add(ref);
//		appendicesTableModel.add(element);
//		updateLaMu(selectedElement, user);
//		elementsTableModel.update(selectedRow);
//		cbx_Appendices.setSelectedItem(null);
//	}

//	public void removeAppendix() {
//		int selectedRow = tbl_Appendices.getSelectedRow();
//
//		AppendixTypeType appendices = appendicesTableModel.remove(selectedRow);
//		AppendixTypes appendixTypes = selectedElement.getAppendixTypes();
//		List<Object> list = appendixTypes.getAppendixTypeOrAppendixTypeRef();
//		for (Object object : list) {
//			AppendixTypeType element = null;
//			if (object instanceof AppendixTypeTypeRef) {
//				element = (AppendixTypeType) ((AppendixTypeTypeRef) object).getIdref();
//			} else if (object instanceof AppendixTypeType) {
//				element = (AppendixTypeType) object;
//			}
//			if (element != null) {
//				if (appendices.equals(element)) {
//					list.remove(object);
//					break;
//				}
//			}
//		}
//		if (list.isEmpty()) {
//			selectedElement.setAppendixTypes(null);
//		}
//		updateLaMu(selectedElement, user);
//		elementsTableModel.update(selectedRow);
//	}
}
