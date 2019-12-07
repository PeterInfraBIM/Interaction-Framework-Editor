package nl.visi.interaction_framework.editor.v16;

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
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.TransactionTypeType.Executor;
import nl.visi.schemas._20160331.TransactionTypeType.Initiator;

public class MessagesPanelControl16 extends PanelControl16<MessageTypeType> {
	private static final String MESSAGES_PANEL = "nl/visi/interaction_framework/editor/swixml/MessagesPanel.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_ComplexElements, tbl_Transactions;
	private ComplexElementsTableModel complexElementsTableModel;
	private TransactionsTableModel transactionsTableModel;
	private JComboBox<String> cbx_ComplexElements;
	private JButton btn_AddComplexElement, btn_RemoveComplexElement;

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

	public MessagesPanelControl16() throws Exception {
		super(MESSAGES_PANEL);
		initMessagesTable();
		initComplexElementsTable();
		initTransactionsTable();
		initStartDateField();
		initEndDateField();
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
						Editor16.getMainFrameControl().navigate(transactionType);
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
					Editor16.getMainFrameControl().navigate(complexElementTypeType);
				}
			}
		});
	}

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
		tbl_ComplexElements.setEnabled(rowSelected);
		tbl_Transactions.setEnabled(rowSelected);
		cbx_ComplexElements.setEnabled(rowSelected);
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
			List<ComplexElementTypeType> elements = Editor16.getStore16().getElements(ComplexElementTypeType.class);
			for (ComplexElementTypeType element : elements) {
				cbx_ComplexElements.addItem(element.getId());
			}

			transactionsTableModel.clear();
			List<MessageInTransactionTypeType> mittList = Editor16.getStore16()
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
		Store16 store = Editor16.getStore16();
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

	public void addComplexElement() {
		String ceId = (String) cbx_ComplexElements.getSelectedItem();
		ComplexElementTypeType element = Editor16.getStore16().getElement(ComplexElementTypeType.class, ceId);
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
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

}
