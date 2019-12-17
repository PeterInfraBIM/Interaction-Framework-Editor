package nl.visi.interaction_framework.editor.v12;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import nl.visi.interaction_schema.RoleTypeType;
import nl.visi.interaction_schema.TransactionTypeType;
import nl.visi.interaction_schema.TransactionTypeType.Executor;
import nl.visi.interaction_schema.TransactionTypeType.Initiator;

public class RolesPanelControl12 extends PanelControl12<RoleTypeType> {
	private static final String ROLES_PANEL = "nl/visi/interaction_framework/editor/swixml/RolesPanel12.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_Transactions;
	private JTextField tfd_ResponsibilityScope, tfd_ResponsibilityTask, tfd_ResponsibilitySupportTask,
			tfd_ResponsibilityFeedback;
	private TransactionsTableModel transactionsTableModel;

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
				return getDateTime(role.getDateLamu());
			case UserLamu:
				return role.getUserLamu();
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

	public RolesPanelControl12() throws Exception {
		super(ROLES_PANEL);
		initRolesTable();
		initTransactionsTable();
		initStartDateField();
		initEndDateField();
		initResponsibilityScope();
		initResponsibilityTask();
		initResponsibilitySupportTask();
		initResponsibilityFeedback();
	}

	private void initResponsibilityFeedback() {
		tfd_ResponsibilityFeedback.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityFeedback(tfd_ResponsibilityFeedback.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilitySupportTask() {
		tfd_ResponsibilitySupportTask.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilitySupportTask(tfd_ResponsibilitySupportTask.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilityTask() {
		tfd_ResponsibilityTask.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityTask(tfd_ResponsibilityTask.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initResponsibilityScope() {
		tfd_ResponsibilityScope.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				selectedElement.setResponsibilityScope(tfd_ResponsibilityScope.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initEndDateField() {
		endDateField = new DateField12(endDatePanel);
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
							updateLamu(selectedElement, user);
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
		startDateField = new DateField12(startDatePanel);
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
							updateLamu(selectedElement, user);
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
				Editor12.getMainFrameControl().navigate(transactionsTableModel.get(row));
			}
		});
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
			startDateField.setDate(selectedElement.getStartDate().toGregorianCalendar().getTime());
			endDateField.setDate(selectedElement.getEndDate().toGregorianCalendar().getTime());
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
			List<TransactionTypeType> elements = Editor12.getStore12().getElements(TransactionTypeType.class);
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
		}
		inSelection = false;
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
		Store12 store = Editor12.getStore12();
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

		Editor12.getStore12().remove(roleType.getId());
		elementsTableModel.remove(row);
	}
}
