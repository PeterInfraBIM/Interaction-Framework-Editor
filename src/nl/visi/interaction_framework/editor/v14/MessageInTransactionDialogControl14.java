package nl.visi.interaction_framework.editor.v14;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.visi.interaction_framework.editor.SelectBox;
import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ElementConditionType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;

public class MessageInTransactionDialogControl14 extends Control14 {
	private static final String MESSAGE_IN_TRANSACTION_DIALOG = "nl/visi/interaction_framework/editor/swixml/MessageInTransactionPanel14.xml";

	class SimpleElementTreeNode {
		private final MessageInTransactionTypeType mitt;
		private final ComplexElementTypeType ce;
		private final SimpleElementTypeType se;

		public SimpleElementTreeNode(MessageInTransactionTypeType mitt, ComplexElementTypeType ce,
				SimpleElementTypeType se) {
			this.mitt = mitt;
			this.ce = ce;
			this.se = se;
		}

		MessageInTransactionTypeType getMitt() {
			return this.mitt;
		}

		ComplexElementTypeType getCe() {
			return this.ce;
		}

		SimpleElementTypeType getSe() {
			return this.se;
		}

		public String getId() {
			return se.getId();
		}

		public String getDescription() {
			return se.getDescription();
		}

		public String getCondition() {
			return Control14.getCondition(mitt, ce, se);
		}

		public void setCondition(String condition) {
			if (getCondition() == null) {
				ElementConditionType newElementCondition = objectFactory.createElementConditionType();
				String newId = Editor14.getStore14().getNewId("ec_");
				newElementCondition.setId(newId);
				newElementCondition.setDescription("Description of " + newId);
				newElementCondition.setCondition(condition);
				setElementConditionTypeMessageInTransaction(newElementCondition, mitt);
				setElementConditionTypeComplexElement(newElementCondition, ce);
				setElementConditionTypeSimpleElement(newElementCondition, se);
				Editor14.getStore14().put(newId, newElementCondition);
			} else {
				ElementConditionType ec = getElementConditionType(mitt, ce, se);
				if (ec != null) {
					ec.setCondition(condition);
					Editor14.getStore14().put(ec.getId(), ec);
				}
			}
		}

		public String getFinalCondition() {
			if (isStartMessage(mitt)) {
				return "FREE";
			}
			if (isNewElement(mitt, ce)) {
				return "FREE";
			}
			return Control14.getFinalCondition(mitt, ce, se);
		}
	}

	class ComplexElementTreeNode {
		private final MessageInTransactionTypeType mitt;
		private final ComplexElementTypeType ce;

		public ComplexElementTreeNode(MessageInTransactionTypeType mitt, ComplexElementTypeType ce) {
			this.mitt = mitt;
			this.ce = ce;
		}

		public String getId() {
			return ce.getId();
		}

		public String getDescription() {
			return ce.getDescription();
		}

		MessageInTransactionTypeType getMitt() {
			return this.mitt;
		}

		ComplexElementTypeType getCe() {
			return this.ce;
		}

		public String getCondition() {
			return Control14.getCondition(mitt, ce, null);
		}

		public void setCondition(String condition) {
			if (getCondition() == null) {
				ElementConditionType newElementCondition = objectFactory.createElementConditionType();
				String newId = Editor14.getStore14().getNewId("ec_");
				newElementCondition.setId(newId);
				newElementCondition.setDescription("Description of " + newId);
				newElementCondition.setCondition(condition);
				setElementConditionTypeMessageInTransaction(newElementCondition, mitt);
				setElementConditionTypeComplexElement(newElementCondition, ce);
				Editor14.getStore14().put(newId, newElementCondition);
			} else {
				ElementConditionType ec = getElementConditionType(mitt, ce, null);
				if (ec != null) {
					ec.setCondition(condition);
					Editor14.getStore14().put(ec.getId(), ec);
				}
			}
		}

		public String getFinalCondition() {
			if (isStartMessage(mitt)) {
				return "FREE";
			}
			if (isNewElement(mitt, ce)) {
				return "FREE";
			}
			return Control14.getFinalCondition(mitt, ce, null);
		}

	}

	private JFrame owner;
	private JDialog dialog;
	private JPanel elementsTreePanel;
	private JTree tree_Elements;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;
	private JPopupMenu popupMenu;
	private JRadioButtonMenuItem rbt_EmptyItem, rbt_FixedItem, rbt_FreeItem;
	private JMenuItem mit_Remove, mit_CollapseAll, mit_ExpandAll;
	private DefaultMutableTreeNode selectedNode;
	private ElementConditionTable elementConditionTable;
	private MessageInTransactionTypeType currentMitt;
	private JTable tbl_Prev, tbl_Next, tbl_SendBefore, tbl_SendAfter;
	private PrevNextTableModel prevTableModel, nextTableModel, sendBeforeTableModel, sendAfterTableModel;
	private JButton btn_RemovePrevious, btn_RemoveNext, btn_RemoveSendBefore, btn_RemoveSendAfter;
	private JCheckBox chb_FirstMessage, chb_Direction, chb_OpenSecondaryTransactionsAllowed;

	private enum PrevNextTableColumns {
		Id, Description, Transaction;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	@SuppressWarnings("serial")
	private class PrevNextTableModel extends AbstractTableModel {
		public List<MessageInTransactionTypeType> elements = new ArrayList<>();

		@Override
		public int getRowCount() {
			return elements.size();
		}

		@Override
		public int getColumnCount() {
			return PrevNextTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return PrevNextTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			MessageInTransactionTypeType mittElement = elements.get(rowIndex);
			switch (PrevNextTableColumns.values()[columnIndex]) {
			case Id:
				return mittElement.getId();
			case Description:
				MessageTypeType message = getMessage(mittElement);
				return message.getDescription();
			case Transaction:
				TransactionTypeType transaction = getTransaction(mittElement);
				return transaction.getDescription();
			}
			return null;
		}
	}

	public MessageInTransactionDialogControl14(TransactionsPanelControl14 transactionsPanelControl) throws Exception {
		super();
		owner = (JFrame) SwingUtilities.windowForComponent(transactionsPanelControl.getPanel());
		dialog = new JDialog(owner);
		initialize(transactionsPanelControl);
	}

	public MessageInTransactionDialogControl14(TransactionsPanelControl14 transactionsPanelControl, Window owner)
			throws Exception {
		super();
		dialog = new JDialog(owner);
		initialize(transactionsPanelControl);
	}

	private void initialize(TransactionsPanelControl14 transactionsPanelControl) throws Exception {
		JTabbedPane tabbedPane = (JTabbedPane) render(MESSAGE_IN_TRANSACTION_DIALOG);
		dialog.getContentPane().add(tabbedPane);
		dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dialog.setSize(480, 480);
		this.elementConditionTable = transactionsPanelControl.elementConditionTable;
		initTreeElements();
	}

	JDialog getDialog() {
		return dialog;
	}

	JPanel getPanel() {
		return elementsTreePanel;
	}

	@SuppressWarnings("serial")
	private void initTreeElements() {
		ToolTipManager.sharedInstance().registerComponent(tree_Elements);
		tree_Elements.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					rbt_EmptyItem.setSelected(false);
					rbt_FixedItem.setSelected(false);
					rbt_FreeItem.setSelected(false);

					int row = tree_Elements.getClosestRowForLocation(e.getX(), e.getY());
					tree_Elements.setSelectionRow(row);
					selectedNode = (DefaultMutableTreeNode) tree_Elements.getSelectionPath().getLastPathComponent();
					Object userObject = selectedNode.getUserObject();
					mit_CollapseAll.setEnabled(true);
					mit_ExpandAll.setEnabled(true);
					if (selectedNode.getUserObject() instanceof SimpleElementTreeNode) {
						SimpleElementTreeNode seNode = (SimpleElementTreeNode) userObject;
						String finalCondition = seNode.getFinalCondition();
						if (finalCondition != null) {
							switch (finalCondition) {
							case "FREE":
								rbt_FreeItem.setSelected(true);
								break;
							case "FIXED":
								rbt_FixedItem.setSelected(true);
								break;
							case "EMPTY":
								rbt_EmptyItem.setSelected(true);
								break;
							}
						}
						mit_Remove.setEnabled(seNode.getCondition() != null);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					} else if (selectedNode.getUserObject() instanceof ComplexElementTreeNode) {
						ComplexElementTreeNode ceNode = (ComplexElementTreeNode) userObject;
						String finalCondition = ceNode.getFinalCondition();
						if (finalCondition != null) {
							switch (finalCondition) {
							case "FREE":
								rbt_FreeItem.setSelected(true);
								break;
							case "FIXED":
								rbt_FixedItem.setSelected(true);
								break;
							case "EMPTY":
								rbt_EmptyItem.setSelected(true);
								break;
							}
						}
						mit_Remove.setEnabled(ceNode.getCondition() != null);
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});

		tree_Elements.setCellRenderer(new DefaultTreeCellRenderer() {

			@Override
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row,
						hasFocus);

				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
				Object userObject = node.getUserObject();
				if (userObject != null && userObject instanceof SimpleElementTreeNode) {
					SimpleElementTreeNode seNode = (SimpleElementTreeNode) userObject;
					String condition = seNode.getFinalCondition();
					label.setText(seNode.getDescription() + ": " + (condition != null ? condition : ""));
					label.setToolTipText(seNode.getId());
				} else if (userObject != null && userObject instanceof ComplexElementTreeNode) {
					ComplexElementTreeNode ceNode = (ComplexElementTreeNode) userObject;
					String condition = ceNode.getFinalCondition();
					label.setText(ceNode.getDescription() + ": " + (condition != null ? condition : ""));
					label.setToolTipText(ceNode.getId());
				} else if (userObject != null && userObject instanceof MessageTypeType) {
					MessageTypeType messageNode = (MessageTypeType) userObject;
					label.setText(messageNode.getDescription());
					label.setToolTipText(messageNode.getId());
				}
				return label;
			}
		});
	}

	void initSequenceElements() {
		prevTableModel = new PrevNextTableModel();
		tbl_Prev.setModel(prevTableModel);
		tbl_Prev.setFillsViewportHeight(true);
		tbl_Prev.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				btn_RemovePrevious.setEnabled(tbl_Prev.getSelectedRow() >= 0);
			}
		});
		nextTableModel = new PrevNextTableModel();
		tbl_Next.setModel(nextTableModel);
		tbl_Next.setFillsViewportHeight(true);
		tbl_Next.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				btn_RemoveNext.setEnabled(tbl_Next.getSelectedRow() >= 0);
			}
		});
		sendBeforeTableModel = new PrevNextTableModel();
		tbl_SendBefore.setModel(sendBeforeTableModel);
		tbl_SendBefore.setFillsViewportHeight(true);
		tbl_SendBefore.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				btn_RemoveSendBefore.setEnabled(tbl_SendBefore.getSelectedRow() >= 0);
			}
		});
		sendAfterTableModel = new PrevNextTableModel();
		tbl_SendAfter.setModel(sendAfterTableModel);
		tbl_SendAfter.setFillsViewportHeight(true);
		tbl_SendAfter.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				btn_RemoveSendAfter.setEnabled(tbl_SendAfter.getSelectedRow() >= 0);
			}
		});
	}

	void fillSequenceElements(MessageInTransactionTypeType mitt) {
		List<MessageInTransactionTypeType> prevList = getPrevious(mitt);
		if (prevList != null) {
			prevTableModel.elements.addAll(prevList);
			prevTableModel.fireTableDataChanged();
		}
		List<MessageInTransactionTypeType> nextList = getNext(mitt);
		if (nextList != null) {
			nextTableModel.elements.addAll(nextList);
			nextTableModel.fireTableDataChanged();
		}
		List<MessageInTransactionTypeType> sendBeforeList = getSendBefores(mitt);
		if (sendBeforeList != null) {
			sendBeforeTableModel.elements.addAll(sendBeforeList);
			sendBeforeTableModel.fireTableDataChanged();
		}
		List<MessageInTransactionTypeType> sendAfterList = getSendAfters(mitt);
		if (sendAfterList != null) {
			sendAfterTableModel.elements.addAll(sendAfterList);
			sendAfterTableModel.fireTableDataChanged();
		}
		chb_FirstMessage.setSelected(mitt.isFirstMessage() != null ? mitt.isFirstMessage() : false);
		chb_Direction.setSelected(mitt.isInitiatorToExecutor() != null ? mitt.isInitiatorToExecutor() : false);
		chb_OpenSecondaryTransactionsAllowed.setSelected(
				mitt.isOpenSecondaryTransactionsAllowed() != null ? mitt.isOpenSecondaryTransactionsAllowed() : false);
	}

	private Map<String, DefaultMutableTreeNode> treeMap;

	private DefaultMutableTreeNode getTreeMap(String key, Object value, DefaultMutableTreeNode parent) {
		DefaultMutableTreeNode node = treeMap.get(key);
		if (node == null) {
			node = new DefaultMutableTreeNode(value);
			treeMap.put(key, node);
			if (parent != null) {
				parent.add(node);
			} else {
				treeModel = new DefaultTreeModel(node);
				tree_Elements.setModel(treeModel);
			}
		}
		return node;
	}

	void fillTree(MessageInTransactionTypeType mitt) {
		if (treeMap == null) {
			treeMap = new Hashtable<>();
		}
		if (mitt != currentMitt) {
			treeMap.clear();
			currentMitt = mitt;
		}

		MessageTypeType message = getMessage(mitt);
		root = getTreeMap(message.getId(), message, null);

		List<ComplexElementTypeType> parentComplexElements = getComplexElements(message);
		if (parentComplexElements != null) {
			for (ComplexElementTypeType pce : parentComplexElements) {
				DefaultMutableTreeNode parentCe = getTreeMap(pce.getId(), new ComplexElementTreeNode(mitt, pce), root);
				List<SimpleElementTypeType> parentSimpleElements = getSimpleElements(pce);
				if (parentSimpleElements != null) {
					for (SimpleElementTypeType pse : parentSimpleElements) {
						addSimpleElement(mitt, pce, parentCe, pse);
					}
				}
				List<ComplexElementTypeType> childComplexElements = getComplexElements(pce);
				if (childComplexElements != null) {
					for (ComplexElementTypeType cce : childComplexElements) {
						DefaultMutableTreeNode childCe = getTreeMap(cce.getId(), new ComplexElementTreeNode(mitt, cce),
								parentCe);
						List<SimpleElementTypeType> childSimpleElements = getSimpleElements(cce);
						for (SimpleElementTypeType cse : childSimpleElements) {
							addSimpleElement(mitt, cce, childCe, cse);
						}
					}
				}
			}
		}
		tree_Elements.expandPath(new TreePath(root));
		expandAll();
	}

	void clearTree() {
		if (treeMap != null)
			treeMap.clear();
		tree_Elements.setModel(null);
	}

	private void addSimpleElement(MessageInTransactionTypeType mitt, ComplexElementTypeType ce,
			DefaultMutableTreeNode parentNode, SimpleElementTypeType se) {
		getTreeMap(ce.getId() + se.getId(), new SimpleElementTreeNode(mitt, ce, se), parentNode);
	}

	public void setEmpty() {
		setCondition("EMPTY");
	}

	public void setFixed() {
		setCondition("FIXED");
	}

	public void setFree() {
		setCondition("FREE");
	}

	private void setCondition(String condition) {
		Object userObject = selectedNode.getUserObject();
		if (userObject != null) {
			if (userObject instanceof SimpleElementTreeNode) {
				SimpleElementTreeNode seNode = (SimpleElementTreeNode) userObject;
				seNode.setCondition(condition);
				fillTree(seNode.getMitt());
				elementConditionTable.fillElementConditionsTable(seNode.getMitt());
				tree_Elements.expandPath(new TreePath(selectedNode.getPath()).getParentPath());
				treeModel.nodeChanged(selectedNode);
			} else if (userObject instanceof ComplexElementTreeNode) {
				ComplexElementTreeNode ceNode = (ComplexElementTreeNode) userObject;
				ceNode.setCondition(condition);
				fillTree(ceNode.getMitt());
				elementConditionTable.fillElementConditionsTable(ceNode.getMitt());
				tree_Elements.expandPath(new TreePath(selectedNode.getPath()).getParentPath());
				treeModel.nodeChanged(selectedNode);
				Enumeration<TreeNode> children = selectedNode.children();
				while (children.hasMoreElements()) {
					treeModel.nodeChanged(children.nextElement());
				}
			}
		}
	}

	public void removeElementCondition() {
		Object userObject = selectedNode.getUserObject();
		if (userObject != null) {
			int confirm = JOptionPane.showConfirmDialog(getDialog(),
					getBundle().getString("lbl_Remove") + " " + getBundle().getString("lbl_ElementCondition"),
					getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (confirm == JOptionPane.OK_OPTION) {
				if (userObject instanceof SimpleElementTreeNode) {
					SimpleElementTreeNode seNode = (SimpleElementTreeNode) userObject;
					ElementConditionType ec = getElementConditionType(seNode.getMitt(), seNode.getCe(), seNode.getSe());
					Editor14.getStore14().remove(ec.getId());
					fillTree(seNode.getMitt());
					elementConditionTable.fillElementConditionsTable(seNode.getMitt());
					TreePath parentPath = new TreePath(selectedNode.getPath()).getParentPath();
					tree_Elements.expandPath(parentPath);
					treeModel.nodeChanged(selectedNode);
				} else if (userObject instanceof ComplexElementTreeNode) {
					ComplexElementTreeNode ceNode = (ComplexElementTreeNode) userObject;
					ElementConditionType ec = getElementConditionType(ceNode.getMitt(), ceNode.getCe(), null);
					Editor14.getStore14().remove(ec.getId());
					fillTree(ceNode.getMitt());
					elementConditionTable.fillElementConditionsTable(ceNode.getMitt());
					TreePath nodePath = new TreePath(selectedNode.getPath());
					tree_Elements.expandPath(nodePath);
					treeModel.nodeChanged(selectedNode);
					Enumeration<TreeNode> children = selectedNode.children();
					while (children.hasMoreElements()) {
						treeModel.nodeChanged(children.nextElement());
					}
				}
			}
		}
	}

	public void collapseAll() {
		TreeNode root = (TreeNode) tree_Elements.getModel().getRoot();
		expandAll(tree_Elements, new TreePath(root), false);
		tree_Elements.expandPath(new TreePath(root));
	}

	public void expandAll() {
		TreeNode root = (TreeNode) tree_Elements.getModel().getRoot();
		expandAll(tree_Elements, new TreePath(root), true);
	}

	private void expandAll(JTree tree, TreePath path, boolean expand) {
		TreeNode node = (TreeNode) path.getLastPathComponent();

		if (node.getChildCount() >= 0) {
			Enumeration<? extends TreeNode> enumeration = node.children();
			while (enumeration.hasMoreElements()) {
				TreeNode n = (TreeNode) enumeration.nextElement();
				TreePath p = path.pathByAddingChild(n);

				expandAll(tree, p, expand);
			}
		}

		if (expand) {
			tree.expandPath(path);
		} else {
			tree.collapsePath(path);
		}
	}

	private boolean isStartMessage(MessageInTransactionTypeType mitt) {
		TransactionTypeType transaction = getTransaction(mitt);
		List<MessageInTransactionTypeType> previous = getPrevious(mitt);
		if (previous != null) {
			for (MessageInTransactionTypeType prevMitt : previous) {
				TransactionTypeType prevMittTransaction = getTransaction(prevMitt);
				if (prevMittTransaction.getId().equals(transaction.getId())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean isNewElement(MessageInTransactionTypeType mitt, ComplexElementTypeType ce) {
		TransactionTypeType selectedTransaction = getTransaction(mitt);
		List<MessageInTransactionTypeType> previous = getPrevious(mitt);
		if (previous != null) {
			for (MessageInTransactionTypeType prevMitt : previous) {
				TransactionTypeType prevMittTransaction = getTransaction(prevMitt);
				if (prevMittTransaction.getId().equals(selectedTransaction.getId())) {
					MessageTypeType prevMessage = getMessage(prevMitt);
					List<ComplexElementTypeType> prevPElements = getComplexElements(prevMessage);
					if (prevPElements != null) {
						for (ComplexElementTypeType prevPElement : prevPElements) {
							if (prevPElement.getId().equals(ce.getId())) {
								return false;
							}
							List<ComplexElementTypeType> prevCElements = getComplexElements(prevPElement);
							if (prevCElements != null) {
								for (ComplexElementTypeType prevCElement : prevCElements) {
									if (prevCElement.getId().equals(ce.getId())) {
										return false;
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	public void addPrevious() {
		// System.out.println("addPrevious");
		List<String> items = new ArrayList<>();
		List<MessageInTransactionTypeType> allMitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : allMitts) {
			MessageTypeType message = getMessage(mitt);
			items.add(message.getDescription() + " [" + mitt.getId() + "]");
		}
		SelectBox selectBox = new SelectBox(owner, getBundle().getString("lbl_Add"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				MessageInTransactionTypeType prevMitt = Editor14.getStore14()
						.getElement(MessageInTransactionTypeType.class, mittId);
				Control14.addPrevious(currentMitt, prevMitt);
				int selectedRow = prevTableModel.getRowCount();
				prevTableModel.elements.add(prevMitt);
				prevTableModel.fireTableRowsInserted(selectedRow, selectedRow);
				propertyChangeSupport.firePropertyChange("Previous added", currentMitt, prevMitt);
			}
		});
	}

	public void addNext() {
		// System.out.println("addNext");
		List<String> items = new ArrayList<>();
		List<MessageInTransactionTypeType> allMitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : allMitts) {
			MessageTypeType message = getMessage(mitt);
			items.add(message.getDescription() + " [" + mitt.getId() + "]");
		}
		SelectBox selectBox = new SelectBox(owner, getBundle().getString("lbl_Add"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				MessageInTransactionTypeType nextMitt = Editor14.getStore14()
						.getElement(MessageInTransactionTypeType.class, mittId);
				Control14.addPrevious(nextMitt, currentMitt);
				int selectedRow = nextTableModel.getRowCount();
				nextTableModel.elements.add(nextMitt);
				nextTableModel.fireTableRowsInserted(selectedRow, selectedRow);
				propertyChangeSupport.firePropertyChange("Next added", nextMitt, currentMitt);
			}
		});
	}

	public void addSendBefore() {
		// System.out.println("addSendBefore");
		List<String> items = new ArrayList<>();
		List<MessageInTransactionTypeType> allMitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : allMitts) {
			MessageTypeType message = getMessage(mitt);
			items.add(message.getDescription() + " [" + mitt.getId() + "]");
		}
		SelectBox selectBox = new SelectBox(owner, getBundle().getString("lbl_Add"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				MessageInTransactionTypeType sendBeforeMitt = Editor14.getStore14()
						.getElement(MessageInTransactionTypeType.class, mittId);
				Control14.addSendBefore(currentMitt, sendBeforeMitt);
				int selectedRow = sendBeforeTableModel.getRowCount();
				sendBeforeTableModel.elements.add(sendBeforeMitt);
				sendBeforeTableModel.fireTableRowsInserted(selectedRow, selectedRow);
			}
		});
	}

	public void addSendAfter() {
		// System.out.println("addSendAfter");
		List<String> items = new ArrayList<>();
		List<MessageInTransactionTypeType> allMitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : allMitts) {
			MessageTypeType message = getMessage(mitt);
			items.add(message.getDescription() + " [" + mitt.getId() + "]");
		}
		SelectBox selectBox = new SelectBox(owner, getBundle().getString("lbl_Add"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// System.out.println(evt.getPropertyName() + " is " + evt.getNewValue());
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String mittId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				MessageInTransactionTypeType sendAfterMitt = Editor14.getStore14()
						.getElement(MessageInTransactionTypeType.class, mittId);
				Control14.addSendAfter(currentMitt, sendAfterMitt);
				int selectedRow = sendAfterTableModel.getRowCount();
				sendAfterTableModel.elements.add(sendAfterMitt);
				sendAfterTableModel.fireTableRowsInserted(selectedRow, selectedRow);
			}
		});
	}

	public void removePrevious() {
		int selectedRow = tbl_Prev.getSelectedRow();
		MessageInTransactionTypeType prevMitt = prevTableModel.elements.get(selectedRow);
		int confirm = JOptionPane.showConfirmDialog(getDialog(),
				getBundle().getString("lbl_Remove") + " " + prevMitt.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.OK_OPTION) {
			// System.out.println("Remove Previous action: " + prevMitt.getId());
			removePrevious(currentMitt, prevMitt);
			prevTableModel.elements.remove(selectedRow);
			prevTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
			propertyChangeSupport.firePropertyChange("Previous removed", currentMitt, prevMitt);
		}
	}

	public void removeNext() {
		int selectedRow = tbl_Next.getSelectedRow();
		MessageInTransactionTypeType nextMitt = nextTableModel.elements.get(selectedRow);
		int confirm = JOptionPane.showConfirmDialog(getDialog(),
				getBundle().getString("lbl_Remove") + " " + nextMitt.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.OK_OPTION) {
			// System.out.println("Remove Next action: " + nextMitt.getId());
			removePrevious(nextMitt, currentMitt);
			nextTableModel.elements.remove(selectedRow);
			nextTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
			propertyChangeSupport.firePropertyChange("Next removed", nextMitt, currentMitt);
		}
	}

	public void removeSendBefore() {
		int selectedRow = tbl_SendBefore.getSelectedRow();
		MessageInTransactionTypeType sendBeforeMitt = sendBeforeTableModel.elements.get(selectedRow);
		int confirm = JOptionPane.showConfirmDialog(getDialog(),
				getBundle().getString("lbl_Remove") + " " + sendBeforeMitt.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.OK_OPTION) {
			// System.out.println("Remove SendBefore action: " + sendBeforeMitt.getId());
			removeSendBefore(currentMitt, sendBeforeMitt);
			sendBeforeTableModel.elements.remove(selectedRow);
			sendBeforeTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
		}
	}

	public void removeSendAfter() {
		int selectedRow = tbl_SendAfter.getSelectedRow();
		MessageInTransactionTypeType sendAfterMitt = sendAfterTableModel.elements.get(selectedRow);
		int confirm = JOptionPane.showConfirmDialog(getDialog(),
				getBundle().getString("lbl_Remove") + " " + sendAfterMitt.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm == JOptionPane.OK_OPTION) {
			// System.out.println("Remove SendAfter action: " + sendAfterMitt.getId());
			removeSendAfter(currentMitt, sendAfterMitt);
			sendAfterTableModel.elements.remove(selectedRow);
			sendAfterTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
		}
	}

	public void changeDirection() {
		// System.out.println("Change direction action: " + currentMitt.getId() + "=" + chb_Direction.isSelected());
		currentMitt.setInitiatorToExecutor(chb_Direction.isSelected());
		propertyChangeSupport.firePropertyChange("Direction changed", currentMitt, chb_Direction.isSelected());
	}

}
