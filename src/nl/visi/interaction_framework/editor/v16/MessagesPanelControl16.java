package nl.visi.interaction_framework.editor.v16;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.DateField;
import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.AppendixTypeTypeRef;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Message;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Previous;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Transaction;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.MessageTypeType.AppendixTypes;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20160331.TransactionTypeType;
import nl.visi.schemas._20160331.TransactionTypeType.Executor;
import nl.visi.schemas._20160331.TransactionTypeType.Initiator;
import nl.visi.schemas._20160331.UserDefinedTypeType;

public class MessagesPanelControl16 extends PanelControl16<MessageTypeType> {
	private static final String MESSAGES_PANEL = "nl/visi/interaction_framework/editor/swixml/MessagesPanel16.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_Transactions, tbl_Appendices;
	private JTree tree_ComplexElements;
	private DefaultMutableTreeNode complexElementsRoot;
	private ComplexElementsTreeModel complexElementsTreeModel;
	private DefaultMutableTreeNode selectedNode;
	private AppendicesTableModel appendicesTableModel;
	private TransactionsTableModel transactionsTableModel;
	private JComboBox<String> cbx_Appendices;
	private JButton btn_AddAppendix, btn_RemoveAppendix;
	private JCheckBox chb_AppendixMandatory;
	private JPopupMenu popupMenu;

	private enum MessagesTableColumns {
		Id, Description;

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
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

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

	@SuppressWarnings("serial")
	class ComplexElementsTreeCellRenderer extends DefaultTreeCellRenderer {
		private final ImageIcon icon;

		public ComplexElementsTreeCellRenderer() {
			this.icon = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(getClass().getResource("/" + getBundle().getString("img_ForwardNav"))));
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, final Object value, boolean sel, boolean expanded,
				boolean leaf, final int row, boolean hasFocus) {
			Component cell = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			JPanel panel = new JPanel();
			JButton navigateBtn = new JButton();
			JButton selectionBtn = new JButton();
			selectionBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					tree_ComplexElements.setSelectionPath(new TreePath(((DefaultMutableTreeNode) value).getPath()));
					tree_ComplexElements.cancelEditing();
				}
			});
			panel.setBackground(null);
			panel.add(selectionBtn);
			panel.add(navigateBtn);
			if (cell instanceof JLabel) {
				if (value instanceof DefaultMutableTreeNode) {
					navigateBtn.setIcon(icon);
					navigateBtn.setOpaque(false);
					navigateBtn.setMargin(new Insets(0, 0, 0, 0));
					navigateBtn.setBorder(null);
					navigateBtn.setBorderPainted(false);

					selectionBtn.setIcon(((JLabel) cell).getIcon());
					selectionBtn.setOpaque(true);
					selectionBtn.setMargin(new Insets(0, 2, 0, 2));
					selectionBtn.setBorder(null);
					selectionBtn.setBorderPainted(false);
					selectionBtn.setBackground(sel ? backgroundSelectionColor : backgroundNonSelectionColor);
					selectionBtn.setForeground(sel ? textSelectionColor : textNonSelectionColor);
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
					Object userObject = node.getUserObject();
					if (userObject instanceof ComplexElementTypeType) {
						final ComplexElementTypeType ce = (ComplexElementTypeType) userObject;
						selectionBtn.setText(ce.getDescription());
						panel.setToolTipText(ce.getId());
						navigateBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								tree_ComplexElements.cancelEditing();
								InteractionFrameworkEditor.navigate(ce);
							}
						});
					} else if (userObject instanceof SimpleElementTypeType) {
						final SimpleElementTypeType se = (SimpleElementTypeType) userObject;
						UserDefinedTypeType userDefinedType = Control16.getUserDefinedType(se);
						if (se.getDescription().length() > 40) {
							selectionBtn.setText(se.getDescription().substring(0, 37) + "..." + " ["
									+ userDefinedType.getDescription() + "]");
						} else {
							selectionBtn.setText(se.getDescription() + " [" + userDefinedType.getDescription() + "]");
						}
						panel.setToolTipText(se.getId());
						navigateBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								tree_ComplexElements.cancelEditing();
								InteractionFrameworkEditor.navigate(se);
							}
						});
					}
				}
			}
			return panel;
		}

	}

	class ComplexElementsTreeCellEditor implements TreeCellEditor {
		private ComplexElementsTreeCellRenderer treeCellRenderer;

		public ComplexElementsTreeCellEditor(ComplexElementsTreeCellRenderer treeCellRenderer) {
			this.treeCellRenderer = treeCellRenderer;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			return true;
		}

		@Override
		public void cancelCellEditing() {
		}

		@Override
		public void addCellEditorListener(CellEditorListener l) {
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
				boolean leaf, int row) {
			Component treeCellRendererComponent = treeCellRenderer.getTreeCellRendererComponent(tree, value, isSelected,
					expanded, leaf, row, isSelected);
			return treeCellRendererComponent;
		}

	}

	@SuppressWarnings("serial")
	public class ComplexElementsTreeModel extends DefaultTreeModel {

		public ComplexElementsTreeModel(TreeNode root) {
			super(root);
		}
	}

	public TransactionsTableModel getTransactionsTableModel() {
		return transactionsTableModel;
	}

	private enum AppendicesTableColumns {
		Id, Description, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class AppendicesTableModel extends ElementsTableModel<AppendixTypeType> {

		@Override
		public int getColumnCount() {
			return AppendicesTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return AppendicesTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			AppendixTypeType appendixElement = get(rowIndex);
			switch (AppendicesTableColumns.values()[columnIndex]) {
			case Id:
				return appendixElement.getId();
			case Description:
				return appendixElement.getDescription();
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == AppendicesTableColumns.Navigate.ordinal();
		}

	}

	public MessagesPanelControl16() throws Exception {
		super(MESSAGES_PANEL);

		initMessagesTable();
		initComplexElementsTree();
		initTransactionsTable();
		initAppendicesTable();
		initStartDateField();
		initEndDateField();
	}

	private void initEndDateField() {
		endDateField = new DateField(endDatePanel);
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
							updateLaMu(selectedElement, getUser());
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
		startDateField = new DateField(startDatePanel);
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
							updateLaMu(selectedElement, getUser());
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
		tbl_Elements.setTransferHandler(Msg2MittTransferHandler.getInstance());
		tbl_Elements.setFillsViewportHeight(true);
		tbl_Elements.setAutoCreateRowSorter(true);
		TableRowSorter<ElementsTableModel<MessageTypeType>> tableRowSorter = new TableRowSorter<>(elementsTableModel);
		tbl_Elements.setRowSorter(tableRowSorter);
		tbl_Elements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updateSelectionArea(e);
			}
		});

		tfd_Filter.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				fillTable();
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

	private void initComplexElementsTree() {
		complexElementsRoot = new DefaultMutableTreeNode();
		complexElementsTreeModel = new ComplexElementsTreeModel(complexElementsRoot);
		tree_ComplexElements.setModel(complexElementsTreeModel);
		ComplexElementsTreeCellRenderer treeCellRenderer = new ComplexElementsTreeCellRenderer();
		tree_ComplexElements.setCellRenderer(treeCellRenderer);
		tree_ComplexElements.setCellEditor(new ComplexElementsTreeCellEditor(treeCellRenderer));
		tree_ComplexElements.setEditable(true);
		tree_ComplexElements.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = tree_ComplexElements.getClosestRowForLocation(e.getX(), e.getY());
				tree_ComplexElements.setSelectionRow(row);
				TreePath selectionPath = tree_ComplexElements.getSelectionPath();
				if (selectionPath != null) {
					selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
					if (SwingUtilities.isRightMouseButton(e)) {
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		tree_ComplexElements.setDropMode(DropMode.INSERT);
		tree_ComplexElements
				.setTransferHandler(new ComplexElementTreeTransferHandler<MessageTypeType>(this, tree_ComplexElements));
		tree_ComplexElements.setRootVisible(false);
		tree_ComplexElements.setShowsRootHandles(false);
		tree_ComplexElements.setRowHeight(20);
		ToolTipManager.sharedInstance().registerComponent(tree_ComplexElements);
	}

	@SuppressWarnings("serial")
	private void initAppendicesTable() {
		appendicesTableModel = new AppendicesTableModel();
		appendicesTableModel.setSorted(false);
		tbl_Appendices.setModel(appendicesTableModel);
		tbl_Appendices.setFillsViewportHeight(true);
		tbl_Appendices.setDropMode(DropMode.INSERT_ROWS);
		tbl_Appendices.setTransferHandler(getTransferHandler(tbl_Appendices, appendicesTableModel, true));
		tbl_Appendices.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tbl_Appendices.getSelectedRow();
				btn_RemoveAppendix.setEnabled(selectedRow >= 0);
			}
		});
		TableColumn navigateColumn = tbl_Appendices.getColumnModel()
				.getColumn(AppendicesTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_Appendices.getSelectedRow();
				AppendixTypeType appendixTypeType = appendicesTableModel.get(row);
				if (appendixTypeType != null) {
					InteractionFrameworkEditor.navigate(appendixTypeType);
				}
			}
		});
	}

	@Override
	public void fillTable() {
		String filterString = tfd_Filter.getText().toUpperCase();
		if (filterString.isEmpty()) {
			fillTable(MessageTypeType.class);
		} else {
			List<MessageTypeType> elements = Editor16.getStore16().getElements(MessageTypeType.class);
			elementsTableModel.clear();
			for (MessageTypeType element : elements) {
				if (element.getDescription().toUpperCase().contains(filterString)
						|| element.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(element);
				}
			}
		}
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;
		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		if (selectedRow >= 0) {
			selectedRow = tbl_Elements.getRowSorter().convertRowIndexToModel(selectedRow);
		}
		boolean rowSelected = selectedRow >= 0;
		btn_CopyElement.setEnabled(rowSelected);
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		startDateField.setEnabled(rowSelected);
		endDateField.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_DateLamu.setEnabled(rowSelected);
		tfd_UserLamu.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_Code.setEnabled(rowSelected);
		chb_AppendixMandatory.setEnabled(rowSelected);
		tbl_Transactions.setEnabled(rowSelected);
		tbl_Appendices.setEnabled(rowSelected);
		cbx_Appendices.setEnabled(rowSelected);
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
			tfd_DateLamu.setText(selectedElement.getDateLaMu() != null
					? sdfDateTime.format(selectedElement.getDateLaMu().toGregorianCalendar().getTime())
					: "");
			tfd_UserLamu.setText(selectedElement.getUserLaMu());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_Code.setText(selectedElement.getCode());
			Boolean appendixMandatory = selectedElement.isAppendixMandatory();
			chb_AppendixMandatory
					.setSelected(appendixMandatory != null ? selectedElement.isAppendixMandatory() : false);

			ComplexElementsTreeModel treeModel = (ComplexElementsTreeModel) tree_ComplexElements.getModel();
			complexElementsRoot.setUserObject(selectedElement.getDescription());
			complexElementsRoot.removeAllChildren();
			treeModel.nodeStructureChanged(complexElementsRoot);
			List<ComplexElementTypeType> complexTreeElements = getComplexElements(selectedElement);
			if (complexTreeElements != null) {
				int parentIndex = 0;
				for (ComplexElementTypeType complexElement : complexTreeElements) {
					DefaultMutableTreeNode complexTreeElement = new DefaultMutableTreeNode(complexElement);
					treeModel.insertNodeInto(complexTreeElement, complexElementsRoot, parentIndex);
					tree_ComplexElements.expandPath(new TreePath(complexElementsRoot.getPath()));
					parentIndex++;
					int childIndex = 0;
					List<ComplexElementTypeType> complexElementcomplexElements = getComplexElements(complexElement);
					if (complexElementcomplexElements != null) {
						for (ComplexElementTypeType complexElementcomplexElement : complexElementcomplexElements) {
							DefaultMutableTreeNode subComplexTreeElement = new DefaultMutableTreeNode(
									complexElementcomplexElement);
							treeModel.insertNodeInto(subComplexTreeElement, complexTreeElement, childIndex);
							tree_ComplexElements.expandPath(new TreePath(complexTreeElement.getPath()));
							childIndex++;
							insertSimpleElements(treeModel, complexElementcomplexElement, subComplexTreeElement, 0);
						}
					}
					insertSimpleElements(treeModel, complexElement, complexTreeElement, childIndex);
				}
			}

			appendicesTableModel.clear();
			AppendixTypes appendixTypes = selectedElement.getAppendixTypes();
			if (appendixTypes != null) {
				List<Object> appendixList = appendixTypes.getAppendixTypeOrAppendixTypeRef();
				for (Object object : appendixList) {
					AppendixTypeType element = null;
					if (object instanceof AppendixTypeTypeRef) {
						element = (AppendixTypeType) ((AppendixTypeTypeRef) object).getIdref();
					} else {
						element = (AppendixTypeType) object;
					}
					appendicesTableModel.add(element);
				}
			}
			cbx_Appendices.removeAllItems();
			cbx_Appendices.addItem(null);
			List<AppendixTypeType> apElements = Editor16.getStore16().getElements(AppendixTypeType.class);
			for (AppendixTypeType element : apElements) {
				cbx_Appendices.addItem("[" + element.getId() + "] " + element.getDescription());
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
			tfd_DateLamu.setText("");
			tfd_UserLamu.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			tfd_Code.setText("");
			chb_AppendixMandatory.setSelected(false);
			transactionsTableModel.clear();
			complexElementsRoot.removeAllChildren();
			complexElementsTreeModel.nodeStructureChanged(complexElementsRoot);
		}
		inSelection = false;
	}

	void insertSimpleElements(ComplexElementsTreeModel treeModel, ComplexElementTypeType complexElement,
			DefaultMutableTreeNode complexTreeElement, int childIndex) {
		List<SimpleElementTypeType> complexElementsimpleElements = getSimpleElements(complexElement);
		if (complexElementsimpleElements != null) {
			for (SimpleElementTypeType complexElementsimpleElement : complexElementsimpleElements) {
				DefaultMutableTreeNode simpleTreeElement = new DefaultMutableTreeNode(complexElementsimpleElement);
				treeModel.insertNodeInto(simpleTreeElement, complexTreeElement, childIndex);
				tree_ComplexElements.expandPath(new TreePath(complexTreeElement.getPath()));
				childIndex++;
			}
		}
	}

	public void newElement() {
		try {
			MessageTypeType newMessageType = objectFactory.createMessageTypeType();
			newElement(newMessageType, "Message_");

			int row = elementsTableModel.add(newMessageType);
			row = tbl_Elements.convertRowIndexToView(row);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void copyElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		MessageTypeType origMessageType = elementsTableModel.get(row);

		try {
			MessageTypeType copyMessageType = objectFactory.createMessageTypeType();
			newElement(copyMessageType, "Message_");
			store.generateCopyId(copyMessageType, origMessageType);
			copyMessageType.setAppendixMandatory(origMessageType.isAppendixMandatory());
			copyAppendices(origMessageType, copyMessageType);
			copyMessageType.setCategory(origMessageType.getCategory());
			copyMessageType.setCode(origMessageType.getCode());
			copyComplexElements(origMessageType, copyMessageType);
			copyMessageType.setDescription(origMessageType.getDescription());
			copyMessageType.setEndDate(origMessageType.getEndDate());
			copyMessageType.setHelpInfo(origMessageType.getHelpInfo());
			copyMessageType.setLanguage(origMessageType.getLanguage());
			copyMessageType.setStartDate(origMessageType.getStartDate());
			copyMessageType.setState(origMessageType.getState());
			store.put(copyMessageType.getId(), copyMessageType);
			int copyrow = elementsTableModel.add(copyMessageType);
			copyrow = tbl_Elements.convertRowIndexToView(copyrow);
			tbl_Elements.getSelectionModel().setSelectionInterval(copyrow, copyrow);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void copyAppendices(MessageTypeType messageType, MessageTypeType copyMessageType) {
		MessageTypeType.AppendixTypes appendixTypes = messageType.getAppendixTypes();
		if (appendixTypes != null) {
			List<Object> refs = appendixTypes.getAppendixTypeOrAppendixTypeRef();
			if (refs != null) {
				MessageTypeType.AppendixTypes copyAppendixTypes = objectFactory.createMessageTypeTypeAppendixTypes();
				List<Object> copyRefs = copyAppendixTypes.getAppendixTypeOrAppendixTypeRef();
				for (Object item : refs) {
					copyRefs.add(item);
				}
				copyMessageType.setAppendixTypes(copyAppendixTypes);
			}
		}
	}

	private void copyComplexElements(MessageTypeType messageType, MessageTypeType copyMessageType) {
		MessageTypeType.ComplexElements complexElements = messageType.getComplexElements();
		if (complexElements != null) {
			List<Object> refs = complexElements.getComplexElementTypeOrComplexElementTypeRef();
			if (refs != null) {
				MessageTypeType.ComplexElements copyComplexElements = objectFactory
						.createMessageTypeTypeComplexElements();
				List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				for (Object item : refs) {
					copyRefs.add(item);
				}
				copyMessageType.setComplexElements(copyComplexElements);
			}
		}
	}

	public void deleteElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		MessageTypeType messageType = elementsTableModel.get(row);

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + messageType.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

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

	public void selectAppendix() {
		int selectedIndex = cbx_Appendices.getSelectedIndex();
		btn_AddAppendix.setEnabled(selectedIndex > 0);
	}

	public void setAppendixMandatory() {
		selectedElement.setAppendixMandatory(chb_AppendixMandatory.isSelected());
	}

	public void removeElement() {
		Object userObject = selectedNode.getUserObject();
		Object parentUserObject = ((DefaultMutableTreeNode) (selectedNode.getParent())).getUserObject();
		if (userObject instanceof SimpleElementTypeType) {
			SimpleElementTypeType se = (SimpleElementTypeType) userObject;
			int selectedOption = JOptionPane.showConfirmDialog(tree_ComplexElements,
					getBundle().getString("lbl_Remove") + " \"" + se.getDescription() + "\"?",
					getBundle().getString("lbl_Remove") + " element", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (selectedOption == JOptionPane.OK_OPTION) {
				ComplexElementTypeType parentCe = (ComplexElementTypeType) parentUserObject;
				ComplexElementTypeType.SimpleElements simpleElements = parentCe.getSimpleElements();
				List<Object> refs = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
				Object removeRef = null;
				for (Object ref : refs) {
					SimpleElementTypeType simpleElement = null;
					if (ref instanceof SimpleElementTypeType) {
						simpleElement = (SimpleElementTypeType) ref;
					} else if (ref instanceof SimpleElementTypeTypeRef) {
						simpleElement = (SimpleElementTypeType) ((SimpleElementTypeTypeRef) ref).getIdref();
					}
					if (simpleElement.getId().equals(se.getId())) {
						removeRef = ref;
						break;
					}
				}
				refs.remove(removeRef);
				updateLaMu(parentCe, getUser());
				Store16 store = Editor16.getStore16();
				store.put(parentCe.getId(), parentCe);

				complexElementsTreeModel.removeNodeFromParent(selectedNode);
			}
		} else {
			ComplexElementTypeType ce = (ComplexElementTypeType) userObject;
			int selectedOption = JOptionPane.showConfirmDialog(tree_ComplexElements,
					getBundle().getString("lbl_Remove") + " \"" + ce.getDescription() + "\"?",
					getBundle().getString("lbl_Remove") + " element", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (selectedOption == JOptionPane.OK_OPTION) {
				List<Object> refs = null;
				if (parentUserObject instanceof ComplexElementTypeType) {
					ComplexElementTypeType parent = (ComplexElementTypeType) parentUserObject;
					ComplexElementTypeType.ComplexElements complexElements = parent.getComplexElements();
					refs = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				} else {
					MessageTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
					refs = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				}
				Object removeRef = null;
				for (Object ref : refs) {
					ComplexElementTypeType complexElement = null;
					if (ref instanceof ComplexElementTypeType) {
						complexElement = (ComplexElementTypeType) ref;
					} else if (ref instanceof ComplexElementTypeTypeRef) {
						complexElement = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) ref).getIdref();
					}
					if (complexElement.getId().equals(ce.getId())) {
						removeRef = ref;
						break;
					}
				}
				refs.remove(removeRef);
				updateLaMu(selectedElement, getUser());
				Store16 store = Editor16.getStore16();
				store.put(selectedElement.getId(), selectedElement);

				complexElementsTreeModel.removeNodeFromParent(selectedNode);
			}
		}
	}

	public void addAppendix() {
		String content = (String) cbx_Appendices.getSelectedItem();
		String appId = content.substring(1, content.indexOf("]"));
		AppendixTypeType element = Editor16.getStore16().getElement(AppendixTypeType.class, appId);
		AppendixTypeTypeRef ref = objectFactory.createAppendixTypeTypeRef();
		ref.setIdref(element);
		AppendixTypes appendixTypes = selectedElement.getAppendixTypes();
		if (appendixTypes == null) {
			appendixTypes = objectFactory.createMessageTypeTypeAppendixTypes();
			selectedElement.setAppendixTypes(appendixTypes);
		}
		List<Object> list = appendixTypes.getAppendixTypeOrAppendixTypeRef();
		list.add(ref);
		appendicesTableModel.add(element);
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
		cbx_Appendices.setSelectedItem(null);
	}

	public void removeAppendix() {
		int selectedRow = tbl_Appendices.getSelectedRow();

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + appendicesTableModel.elements.get(selectedRow).getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		AppendixTypeType appendices = appendicesTableModel.remove(selectedRow);
		AppendixTypes appendixTypes = selectedElement.getAppendixTypes();
		List<Object> list = appendixTypes.getAppendixTypeOrAppendixTypeRef();
		for (Object object : list) {
			AppendixTypeType element = null;
			if (object instanceof AppendixTypeTypeRef) {
				element = (AppendixTypeType) ((AppendixTypeTypeRef) object).getIdref();
			} else if (object instanceof AppendixTypeType) {
				element = (AppendixTypeType) object;
			}
			if (element != null) {
				if (appendices.equals(element)) {
					list.remove(object);
					break;
				}
			}
		}
		if (list.isEmpty()) {
			selectedElement.setAppendixTypes(null);
		}
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
	}
}
