package nl.visi.interaction_framework.editor.v16;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;
import java.util.EventObject;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.DateField;
import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.OrganisationTypeType;
import nl.visi.schemas._20160331.PersonTypeType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20160331.UserDefinedTypeType;

public class ComplexElementsPanelControl16 extends PanelControl16<ComplexElementTypeType> {
	private static final String COMPLEX_ELEMENTS_PANEL = "nl/visi/interaction_framework/editor/swixml/ComplexElementsPanel16.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_SimpleElements, tbl_UseElements;
	private JTree tree_SubComplexElements;
	private SubComplexElementsTreeModel subComplexElementsTreeModel;
	private DefaultMutableTreeNode subComplexElementsRoot;
	private SimpleElementsTableModel simpleElementsTableModel;
	private UseElementsTableModel useElementsTableModel;
	private JComboBox<String> cbx_GlobalElementCondition, cbx_SimpleElementConditions, cbx_ComplexElementConditions,
			cbx_ComplexElements, cbx_SimpleElements;
	private JButton btn_AddComplexElement, btn_RemoveTreeElement, btn_AddSimpleElement, btn_RemoveSimpleElement;
	private JTextField tfd_MinOccurs, tfd_MaxOccurs;
	private JPopupMenu popupMenu;

	private enum ComplexElementsTableColumns {
		Id, Description;

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
//			case StartDate:
//				return getDate(complexElement.getStartDate());
//			case EndDate:
//				return getDate(complexElement.getEndDate());
//			case State:
//				return complexElement.getState();
//			case DateLamu:
//				return getDateTime(complexElement.getDateLaMu());
//			case UserLamu:
//				return complexElement.getUserLaMu();
			default:
				return null;
			}
		}
	}

//	SubComplexElementsTableModel getSubComplexElementsTableModel() {
//		return subComplexElementsTableModel;
//	}

	private enum SubComplexElementsTableColumns {
		Id, Description, Condition, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class SubComplexElementsTableModel extends ElementsTableModel<ComplexElementTypeType> {

		@Override
		public int getColumnCount() {
			return SubComplexElementsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return SubComplexElementsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ComplexElementTypeType complexElement = get(rowIndex);
			switch (SubComplexElementsTableColumns.values()[columnIndex]) {
			case Id:
				return complexElement != null ? complexElement.getId() : null;
			case Description:
				return complexElement != null ? complexElement.getDescription() : null;
			case Condition:
				ElementConditionType ec = getElementConditionType(null, selectedElement, complexElement, null);
				return ec != null ? ec.getCondition() : "";
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (SubComplexElementsTableColumns.values()[columnIndex]) {
			case Condition:
				return true;
			case Navigate:
				return true;
			default:
				return false;
			}
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			ComplexElementTypeType complexElement = get(rowIndex);

			switch (SubComplexElementsTableColumns.values()[columnIndex]) {
			case Condition:
				ElementConditionType ec = getElementConditionType(null, selectedElement, complexElement, null);
				if (ec != null) {
					if (aValue != null) {
						ec.setCondition((String) aValue);
					} else {
						Editor16.getStore16().remove(ec);
					}
				} else {
					if (aValue != null) {
						String newId = Editor16.getStore16().getNewId("ec_");
						ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
						Editor16.getStore16().put(newId, newElementConditionType);
						newElementConditionType.setId(newId);
						newElementConditionType.setDescription("Decription of " + newId);
						newElementConditionType.setCondition((String) aValue);
						setElementConditionTypeComplexElement1(newElementConditionType, selectedElement);
						setElementConditionTypeComplexElement2(newElementConditionType, complexElement);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	private enum SimpleElementsTableColumns {
		Id, Description, UserDefinedType, Condition, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class SimpleElementsTableModel extends ElementsTableModel<SimpleElementTypeType> {

		@Override
		public int getColumnCount() {
			return SimpleElementsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return SimpleElementsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SimpleElementTypeType simpleElement = get(rowIndex);

			switch (SimpleElementsTableColumns.values()[columnIndex]) {
			case Id:
				return simpleElement.getId();
			case Description:
				return simpleElement.getDescription();
			case UserDefinedType:
				UserDefinedTypeType userDefinedType = getUserDefinedType(simpleElement);
				if (userDefinedType != null) {
					return userDefinedType.getDescription();
				}
				break;
			case Condition:
				ElementConditionType ec = getElementCondition(simpleElement);
				return ec != null ? ec.getCondition() : "";
			default:
				break;
			}
			return null;
		}

		private ElementConditionType getElementCondition(SimpleElementTypeType simpleElement) {
			ElementConditionType ec = null;
			List<SimpleElementTypeType> simpleElements = getSimpleElements(selectedElement);
			if (simpleElements != null && simpleElements.contains(simpleElement)) {
				ec = getElementConditionType(null, selectedElement, null, simpleElement);
			} else {
				ComplexElementTypeType subComplexElement = getSubComplexElement(simpleElement);
				if (subComplexElement != null) {
					ec = getElementConditionType(null, selectedElement, subComplexElement, simpleElement);
				}
			}
			return ec;
		}

		private ComplexElementTypeType getSubComplexElement(SimpleElementTypeType simpleElement) {
			List<ComplexElementTypeType> complexElements = getComplexElements(selectedElement);
			if (complexElements != null) {
				for (ComplexElementTypeType ce : complexElements) {
					List<SimpleElementTypeType> subSimpleElements = getSimpleElements(ce);
					if (subSimpleElements != null && subSimpleElements.contains(simpleElement)) {
						return ce;
					}
				}
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (SimpleElementsTableColumns.values()[columnIndex]) {
			case Condition:
				return true;
			case Navigate:
				return true;
			default:
				return false;
			}
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			SimpleElementTypeType simpleElement = get(rowIndex);

			switch (SimpleElementsTableColumns.values()[columnIndex]) {
			case Condition:
				ElementConditionType ec = getElementCondition(simpleElement);
				if (ec != null) {
					if (aValue != null) {
						ec.setCondition((String) aValue);
					} else {
						Editor16.getStore16().remove(ec);
					}
				} else {
					if (aValue != null) {
						String newId = Editor16.getStore16().getNewId("ec_");
						ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
						Editor16.getStore16().put(newId, newElementConditionType);
						newElementConditionType.setId(newId);
						newElementConditionType.setDescription("Decription of " + newId);
						newElementConditionType.setCondition((String) aValue);
						setElementConditionTypeComplexElement1(newElementConditionType, selectedElement);
						ComplexElementTypeType subComplexElement = getSubComplexElement(simpleElement);
						if (subComplexElement != null) {
							setElementConditionTypeComplexElement2(newElementConditionType, subComplexElement);
						}
						setElementConditionTypeSimpleElement(newElementConditionType, simpleElement);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	SimpleElementsTableModel getSimpleElementsTableModel() {
		return simpleElementsTableModel;
	}

	private enum UseElementsTableColumns {
		Id, Description, Type, Navigate;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	@SuppressWarnings("serial")
	public class UseElementsTableModel extends ElementsTableModel<ElementType> {

		@Override
		public int getColumnCount() {
			return UseElementsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return UseElementsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ElementType useElementType = get(rowIndex);

			switch (UseElementsTableColumns.values()[columnIndex]) {
			case Id:
				return useElementType.getId();
			case Description:
				switch (useElementType.getClass().getSimpleName()) {
				case "AppendixTypeType":
					return ((AppendixTypeType) useElementType).getDescription();
				case "ComplexElementTypeType":
					return ((ComplexElementTypeType) useElementType).getDescription();
				case "ElementConditionType":
					return ((ElementConditionType) useElementType).getDescription();
				case "MessageTypeType":
					return ((MessageTypeType) useElementType).getDescription();
				case "OrganisationTypeType":
					return ((OrganisationTypeType) useElementType).getDescription();
				case "PersonTypeType":
					return ((PersonTypeType) useElementType).getDescription();
				case "ProjectTypeType":
					return ((ProjectTypeType) useElementType).getDescription();
				}
				return null;
			case Type:
				String simpleName = useElementType.getClass().getSimpleName();
				int lastIndexOfType = simpleName.lastIndexOf("Type");
				return simpleName.substring(0, lastIndexOfType);
			default:
				break;
			}

			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (UseElementsTableColumns.values()[columnIndex]) {
			case Navigate:
				return true;
			default:
				return false;
			}
		}
	}

	@SuppressWarnings("serial")
	class SubComplexElementsTreeCellRenderer extends DefaultTreeCellRenderer {
		private final ImageIcon icon;
		private JButton selectionBtn;
		private JButton navigateBtn;

		public SubComplexElementsTreeCellRenderer() {
			this.icon = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(getClass().getResource("/" + getBundle().getString("img_ForwardNav"))));
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			Component cell = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (cell instanceof JLabel) {
				if (value instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
					Object userObject = node.getUserObject();
					JPanel pane = new JPanel();
					pane.setBackground(null);
					pane.setBorder(null);
					selectionBtn = new JButton(((JLabel) cell).getIcon());
					selectionBtn.setOpaque(true);
					selectionBtn.setMargin(new Insets(0, 0, 0, 0));
					selectionBtn.setBorder(null);
					selectionBtn.setBorderPainted(false);
					selectionBtn.setBackground(sel ? backgroundSelectionColor : backgroundNonSelectionColor);
					selectionBtn.setForeground(sel ? textSelectionColor : textNonSelectionColor);
					pane.add(selectionBtn);
					navigateBtn = new JButton(icon);
					navigateBtn.setOpaque(false);
					navigateBtn.setMargin(new Insets(0, 0, 0, 0));
					navigateBtn.setBorder(null);
					navigateBtn.setBorderPainted(false);
					pane.add(navigateBtn);
					if (userObject instanceof ComplexElementTypeType) {
						ComplexElementTypeType ce = (ComplexElementTypeType) userObject;
						((JLabel) cell).setText(ce.getDescription());
						((JLabel) cell).setToolTipText(ce.getId());
						selectionBtn.setText(((JLabel) cell).getText());
						pane.setToolTipText(((JLabel) cell).getToolTipText());
						return pane;
					} else if (userObject instanceof SimpleElementTypeType) {
						SimpleElementTypeType se = (SimpleElementTypeType) userObject;
						UserDefinedTypeType userDefinedType = Control16.getUserDefinedType(se);
						((JLabel) cell).setText(se.getDescription() + " [" + userDefinedType.getDescription() + "]");
						((JLabel) cell).setToolTipText(se.getId());
						selectionBtn.setText(((JLabel) cell).getText());
						pane.setToolTipText(((JLabel) cell).getToolTipText());
						return pane;
					}
				}
			}
			return cell;
		}

		private JButton getNavigateButton() {
			return navigateBtn;
		}

	}

	class SubComplexTreeCellEditor extends DefaultTreeCellEditor {
		private DefaultTreeCellRenderer treeCellRenderer;

		public SubComplexTreeCellEditor(JTree tree, DefaultTreeCellRenderer treeCellRenderer) {
			super(tree, treeCellRenderer);
			this.treeCellRenderer = treeCellRenderer;
		}

		@Override
		public boolean isCellEditable(EventObject event) {
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			return true;
		}

		@Override
		public Component getTreeCellEditorComponent(JTree tree, final Object value, boolean isSelected,
				boolean expanded, boolean leaf, int row) {
			Component rendererComponent = treeCellRenderer.getTreeCellRendererComponent(tree, value, isSelected,
					expanded, leaf, row, leaf);
			JButton navigateButton = ((SubComplexElementsTreeCellRenderer) this.treeCellRenderer).getNavigateButton();
			navigateButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
					if (userObject instanceof SimpleElementTypeType) {
						InteractionFrameworkEditor.navigate((SimpleElementTypeType) userObject);
					} else {
						InteractionFrameworkEditor.navigate((ComplexElementTypeType) userObject);
					}
				}
			});

			return rendererComponent;
		}

	}

	@SuppressWarnings("serial")
	public class SubComplexElementsTreeModel extends DefaultTreeModel {

		public SubComplexElementsTreeModel(TreeNode root) {
			super(root);
		}
	}

	public ComplexElementsPanelControl16() throws Exception {
		super(COMPLEX_ELEMENTS_PANEL);

		initComplexElementsTable();
//		initSubComplexElementsTable();
		initSimpleElementsTable();
		initUseElementsTable();
		initSubComplexElementsTree();
		initStartDateField();
		initEndDateField();
		initMinOccurs();
		initMaxOccurs();
		initGlobalElementCondition();
	}

	private void initEndDateField() {
		endDateField = new DateField(endDatePanel);
		endDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date endDate = endDateField.getDate();
					if (endDate != null) {
						gcal.setTime(endDateField.getDate());
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
						gcal.setTime(startDateField.getDate());
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

	private void initMinOccurs() {
		tfd_MinOccurs.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;

				if (tfd_MinOccurs.getText().equals("")) {
					selectedElement.setMinOccurs(null);
				} else {
					try {
						int intValue = Integer.parseInt(tfd_MinOccurs.getText());
						selectedElement.setMinOccurs(BigInteger.valueOf(intValue));
						updateLaMu(selectedElement, getUser());
						elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
					} catch (NumberFormatException exception) {
						JOptionPane.showMessageDialog(panel, exception.getMessage(),
								getBundle().getString("lbl_ValidationError"), JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}

	private void initMaxOccurs() {
		tfd_MaxOccurs.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;
				if (tfd_MaxOccurs.getText().equals("")) {
					selectedElement.setMaxOccurs(null);
				} else {
					try {
						int intValue = Integer.parseInt(tfd_MaxOccurs.getText());
						selectedElement.setMaxOccurs(BigInteger.valueOf(intValue));
						updateLaMu(selectedElement, getUser());
						elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
					} catch (NumberFormatException exception) {
						JOptionPane.showMessageDialog(panel, exception.getMessage(),
								getBundle().getString("lbl_ValidationError"), JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}

	private void initGlobalElementCondition() {
		for (String conditionValue : CONDITION_VALUES) {
			cbx_GlobalElementCondition.addItem(conditionValue);
		}
		cbx_GlobalElementCondition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inSelection)
					return;
				String condition = (String) cbx_GlobalElementCondition.getSelectedItem();
				ElementConditionType elementConditionType = getElementConditionType(null, selectedElement, null, null);
				if (elementConditionType != null) {
					if (condition != null) {
						elementConditionType.setCondition(condition);
					} else {
						Editor16.getStore16().remove(elementConditionType);
					}
					updateLaMu(selectedElement, getUser());
				} else {
					if (condition != null) {
						String newId = Editor16.getStore16().getNewId("ec_");
						ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
						Editor16.getStore16().put(newId, newElementConditionType);
						newElementConditionType.setId(newId);
						newElementConditionType.setDescription("Decription of " + newId);
						newElementConditionType.setCondition(condition);
						setElementConditionTypeComplexElement1(newElementConditionType, selectedElement);
						updateLaMu(selectedElement, getUser());
					}
				}
			}
		});
	}

	@SuppressWarnings("serial")
	private void initSimpleElementsTable() {
		simpleElementsTableModel = new SimpleElementsTableModel();
		simpleElementsTableModel.setSorted(false);
		tbl_SimpleElements.setModel(simpleElementsTableModel);
//		tbl_SimpleElements.setAutoCreateRowSorter(true);
		tbl_SimpleElements.setFillsViewportHeight(true);
		tbl_SimpleElements.setDropMode(DropMode.INSERT_ROWS);
		tbl_SimpleElements.setTransferHandler(getTransferHandler(tbl_SimpleElements, simpleElementsTableModel, false));
		tbl_SimpleElements.getColumnModel().getColumn(SimpleElementsTableColumns.Id.ordinal())
				.setCellRenderer(new DefaultTableCellRenderer() {

					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
								row, column);
						if (selectedElement != null) {
							boolean found = false;
							List<SimpleElementTypeType> simpleElements = Control16.getSimpleElements(selectedElement);
							if (simpleElements != null) {
								for (SimpleElementTypeType simpleElement : simpleElements) {
									if (simpleElement.getId().equals(value.toString())) {
										found = true;
										break;
									}
								}
							}
							if (!found || found && !(row < simpleElements.size())) {
								Font newLabelFont = new Font(label.getFont().getName(), Font.ITALIC,
										label.getFont().getSize());
								label.setFont(newLabelFont);
							}
						}
						return label;
					}
				});
		tbl_SimpleElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tbl_SimpleElements.getSelectedRow();
				if (selectedRow >= 0) {
					List<SimpleElementTypeType> simpleElements = getSimpleElements(selectedElement);
					SimpleElementTypeType se = simpleElementsTableModel.elements.get(selectedRow);
					btn_RemoveSimpleElement.setEnabled(simpleElements != null && simpleElements.contains(se));
					Object conditionValue = tbl_SimpleElements.getValueAt(selectedRow,
							SimpleElementsTableColumns.Condition.ordinal());
					cbx_SimpleElementConditions.setSelectedItem(conditionValue == "" ? null : conditionValue);
				} else {
					btn_RemoveSimpleElement.setEnabled(false);
				}
			}
		});
		cbx_SimpleElementConditions = new JComboBox<>(new DefaultComboBoxModel<String>());
		for (String conditionValue : CONDITION_VALUES) {
			cbx_SimpleElementConditions.addItem(conditionValue);
		}
		TableColumn conditionColumn = tbl_SimpleElements.getColumnModel()
				.getColumn(SimpleElementsTableColumns.Condition.ordinal());
		conditionColumn.setCellEditor(new DefaultCellEditor(cbx_SimpleElementConditions));

		TableColumn navigateColumn = tbl_SimpleElements.getColumnModel()
				.getColumn(SimpleElementsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_SimpleElements.getSelectedRow();
				SimpleElementTypeType simpleElementTypeType = simpleElementsTableModel.get(row);
				if (simpleElementTypeType != null) {
					InteractionFrameworkEditor.navigate(simpleElementTypeType);
				}
			}
		});
	}

//	@SuppressWarnings("serial")
//	private void initSubComplexElementsTable() {
//		subComplexElementsTableModel = new SubComplexElementsTableModel();
//		subComplexElementsTableModel.setSorted(false);
//		tbl_SubComplexElements.setModel(subComplexElementsTableModel);
////		tbl_SubComplexElements.setAutoCreateRowSorter(true);
//		tbl_SubComplexElements.setFillsViewportHeight(true);
//		tbl_SubComplexElements.setDropMode(DropMode.INSERT_ROWS);
//		tbl_SubComplexElements
//				.setTransferHandler(getTransferHandler(tbl_SubComplexElements, subComplexElementsTableModel, true));
//		tbl_SubComplexElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				int selectedRow = tbl_SubComplexElements.getSelectedRow();
//				if (selectedRow >= 0) {
//					List<ComplexElementTypeType> complexElements = getComplexElements(selectedElement);
//					ComplexElementTypeType ce = subComplexElementsTableModel.elements.get(selectedRow);
//					btn_RemoveComplexElement.setEnabled(complexElements != null && complexElements.contains(ce));
//					Object conditionValue = tbl_SubComplexElements.getValueAt(selectedRow,
//							SubComplexElementsTableColumns.Condition.ordinal());
//					cbx_ComplexElementConditions.setSelectedItem(conditionValue == "" ? null : conditionValue);
//				} else {
//					btn_RemoveComplexElement.setEnabled(false);
//				}
//			}
//		});
//		cbx_ComplexElementConditions = new JComboBox<>(new DefaultComboBoxModel<String>());
//		for (String conditionValue : CONDITION_VALUES) {
//			cbx_ComplexElementConditions.addItem(conditionValue);
//		}
//		TableColumn conditionColumn = tbl_SubComplexElements.getColumnModel()
//				.getColumn(SubComplexElementsTableColumns.Condition.ordinal());
//		conditionColumn.setCellEditor(new DefaultCellEditor(cbx_ComplexElementConditions));
//
//		TableColumn navigateColumn = tbl_SubComplexElements.getColumnModel()
//				.getColumn(SubComplexElementsTableColumns.Navigate.ordinal());
//		navigateColumn.setMaxWidth(50);
//		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
//		navigateColumn.setCellEditor(new NavigatorEditor() {
//			@Override
//			protected void navigate() {
//				int row = tbl_SubComplexElements.getSelectedRow();
//				ComplexElementTypeType complexElementTypeType = subComplexElementsTableModel.get(row);
//				if (complexElementTypeType != null) {
//					InteractionFrameworkEditor.navigate(complexElementTypeType);
//				}
//			}
//		});
//	}

	@SuppressWarnings("serial")
	private void initUseElementsTable() {
		useElementsTableModel = new UseElementsTableModel();
		tbl_UseElements.setModel(useElementsTableModel);
		tbl_UseElements.setFillsViewportHeight(true);
		tbl_UseElements.setAutoCreateRowSorter(true);
		TableColumn navigateColumn = tbl_UseElements.getColumnModel()
				.getColumn(UseElementsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_UseElements.getSelectedRow();
				row = tbl_UseElements.getRowSorter().convertRowIndexToModel(row);
				ElementType useElementType = useElementsTableModel.get(row);
				if (useElementType != null) {
					InteractionFrameworkEditor.navigate(useElementType);
				}
			}
		});
	}

	private void initSubComplexElementsTree() {
		subComplexElementsRoot = new DefaultMutableTreeNode();
		subComplexElementsTreeModel = new SubComplexElementsTreeModel(subComplexElementsRoot);
		tree_SubComplexElements.setModel(subComplexElementsTreeModel);
		SubComplexElementsTreeCellRenderer treeCellRenderer = new SubComplexElementsTreeCellRenderer();
		tree_SubComplexElements.setCellRenderer(treeCellRenderer);
		SubComplexTreeCellEditor treeCellEditor = new SubComplexTreeCellEditor(tree_SubComplexElements,
				treeCellRenderer);
		tree_SubComplexElements.setCellEditor(treeCellEditor);
		tree_SubComplexElements.setEditable(true);
		tree_SubComplexElements.setRootVisible(false);
		tree_SubComplexElements.setShowsRootHandles(false);
		ToolTipManager.sharedInstance().registerComponent(tree_SubComplexElements);
		tree_SubComplexElements.setDropMode(DropMode.INSERT);
		tree_SubComplexElements.setTransferHandler(
				new ComplexElementTreeTransferHandler<ComplexElementTypeType>(this, tree_SubComplexElements));
		tree_SubComplexElements.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = tree_SubComplexElements.getClosestRowForLocation(e.getX(), e.getY());
				tree_SubComplexElements.setSelectionRow(row);
				TreePath selectionPath = tree_SubComplexElements.getSelectionPath();
				btn_RemoveTreeElement.setEnabled(selectionPath != null);
				if (selectionPath != null) {
					if (SwingUtilities.isRightMouseButton(e)) {
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});

//		tree_SubComplexElements.setTransferHandler(new TransferHandler() {
//			private DefaultMutableTreeNode movedNode;
//			private SimpleElementTypeType simpleElement;
//			private ComplexElementTypeType complexElement;
//			private JTree.DropLocation dropLocation;
//			private DefaultMutableTreeNode dropNode;
//			private ComplexElementTypeType dropElement;
//
//			@Override
//			public int getSourceActions(JComponent c) {
//				return MOVE;
//			}
//			
//			@Override
//			protected Transferable createTransferable(JComponent c) {
//				TreePath[] paths = tree_SubComplexElements.getSelectionPaths();
//				if (paths != null && paths.length == 1) {
//					movedNode = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
//					Object userObject = movedNode.getUserObject();
//					if (userObject instanceof SimpleElementTypeType) {
//						return new StringSelection(((SimpleElementTypeType) userObject).getId());
//					} else {
//						return new StringSelection(((ComplexElementTypeType) userObject).getId());
//					}
//				}
//				return null;
//			}
//			
//			@Override
//			protected void exportDone(JComponent source, Transferable data, int action) {
//				if (action == MOVE) {
//					Object parentObject = ((DefaultMutableTreeNode) movedNode.getParent()).getUserObject();
//					Object dropObject = ((DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent())
//							.getUserObject();
//					if (parentObject instanceof ComplexElementTypeType && simpleElement != null) {
//						ComplexElementTypeType parentCe = (ComplexElementTypeType) parentObject;
//						int index = 0;
//						int foundIndex = -1;
//						boolean found = false;
//						SimpleElements simpleElements = parentCe.getSimpleElements();
//						if (simpleElements != null) {
//							List<Object> refs = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
//							for (Object ref : refs) {
//								if (index == dropLocation.getChildIndex() && dropObject.equals(parentObject)) {
//									index++;
//									continue;
//								}
//								SimpleElementTypeType se = null;
//								if (ref instanceof SimpleElementTypeType) {
//									se = (SimpleElementTypeType) ref;
//								} else {
//									se = (SimpleElementTypeType) (((SimpleElementTypeTypeRef) ref).getIdref());
//
//								}
//								if (se != null) {
//									if (se.getId().equals(simpleElement.getId())) {
//										found = true;
//										foundIndex = index;
//										break;
//									}
//								}
//								index++;
//							}
//							if (found) {
//								refs.remove(foundIndex);
//							}
//						}
//					} else {
//						int index = 0;
//						int foundIndex = -1;
//						boolean found = false;
//						List<Object> refs = parentObject instanceof String
//								? selectedElement.getComplexElements().getComplexElementTypeOrComplexElementTypeRef()
//								: ((ComplexElementTypeType) parentObject).getComplexElements()
//										.getComplexElementTypeOrComplexElementTypeRef();
//						for (Object ref : refs) {
//							if (index == dropLocation.getChildIndex()) {
//								index++;
//								continue;
//							}
//							ComplexElementTypeType ce = null;
//							if (ref instanceof ComplexElementTypeType) {
//								ce = (ComplexElementTypeType) ref;
//							} else {
//								ce = (ComplexElementTypeType) (((ComplexElementTypeTypeRef) ref).getIdref());
//
//							}
//							if (ce != null) {
//								if (ce.getId().equals(complexElement.getId())) {
//									found = true;
//									foundIndex = index;
//									break;
//								}
//							}
//							index++;
//						}
//						if (found) {
//							refs.remove(foundIndex);
//						}
//					}
//					subComplexElementsTreeModel.removeNodeFromParent(movedNode);
//				}
//				movedNode = null;
//			}
//
//			@Override
//			public boolean canImport(TransferSupport support) {
//				if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
//					try {
//						String transferData = (String) support.getTransferable()
//								.getTransferData(DataFlavor.stringFlavor);
//						String id = transferData.split("\t")[0];
//						simpleElement = Editor16.getStore16().getElement(SimpleElementTypeType.class, id);
//						if (simpleElement != null) {
//							dropLocation = (javax.swing.JTree.DropLocation) support.getDropLocation();
//							dropNode = (DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
//							System.out.println(dropNode.getUserObject());
//							if (dropNode.getUserObject() instanceof ComplexElementTypeType) {
//								System.out.println(dropLocation.getChildIndex());
//								if (dropLocation.getChildIndex() >= 0) {
//									return true;
//								}
//							}
//							return false;
//						} else {
//							complexElement = Editor16.getStore16().getElement(ComplexElementTypeType.class, id);
//							if (complexElement != null) {
//								if (dropNode != null) {
//									tree_SubComplexElements.removeSelectionPath(new TreePath(dropNode.getPath()));
//								}
//								dropLocation = (javax.swing.JTree.DropLocation) support.getDropLocation();
//								if (selectedElement.getComplexElements() == null || selectedElement.getComplexElements()
//										.getComplexElementTypeOrComplexElementTypeRef().isEmpty()) {
//									// Add complex type to empty message type
//									return true;
//								}
//								dropNode = (DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
//								if (!(dropNode.getUserObject() instanceof ComplexElementTypeType)) {
//									// Add complex type to message type
//									return true;
//								} else {
//									if (dropLocation.getChildIndex() == -1) {
//										// Add complex type to complex type (as table)
//										tree_SubComplexElements.setSelectionPath(new TreePath(dropNode.getPath()));
//										return true;
//									}
//								}
//								return false;
//							}
//						}
//					} catch (UnsupportedFlavorException | IOException e) {
//						e.printStackTrace();
//					}
//				}
//				return false;
//			}
//			
//			@Override
//			public boolean importData(TransferSupport support) {
//				if (dropLocation.getPath() == null) {
//					ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
//					ref.setIdref(complexElement);
//					ComplexElementTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
//					if (complexElements == null) {
//						complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
//						selectedElement.setComplexElements(complexElements);
//					}
//					List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
//					list.add(0, ref);
//					updateLaMu(selectedElement, getUser());
//					DefaultMutableTreeNode complexNode = new DefaultMutableTreeNode(complexElement);
//					subComplexElementsTreeModel.insertNodeInto(complexNode, subComplexElementsRoot, 0);
//					tree_SubComplexElements.expandPath(new TreePath(subComplexElementsRoot.getPath()));
//					showComplexNode(complexNode);
//					return true;
//				} else {
//					dropNode = (DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
//					if (dropNode.getUserObject() instanceof ComplexElementTypeType) {
//						dropElement = (ComplexElementTypeType) dropNode.getUserObject();
//						if (dropLocation.getChildIndex() > -1) {
//							SimpleElementTypeTypeRef ref = objectFactory.createSimpleElementTypeTypeRef();
//							ref.setIdref(simpleElement);
//							ComplexElementTypeType.SimpleElements simpleElements = dropElement.getSimpleElements();
//							if (simpleElements == null) {
//								simpleElements = objectFactory.createComplexElementTypeTypeSimpleElements();
//								dropElement.setSimpleElements(simpleElements);
//							}
//							List<Object> list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
//							list.add(dropLocation.getChildIndex(), ref);
//							updateLaMu(dropElement, getUser());
//							subComplexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(simpleElement), dropNode,
//									dropLocation.getChildIndex());
//							return true;
//						} else {
//							ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
//							ref.setIdref(complexElement);
//							ComplexElements complexElements = dropElement.getComplexElements();
//							if (complexElements == null) {
//								complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
//								dropElement.setComplexElements(complexElements);
//							}
//							List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
//							list.add(ref);
//							updateLaMu(dropElement, getUser());
//							DefaultMutableTreeNode complexNode = new DefaultMutableTreeNode(complexElement);
//							subComplexElementsTreeModel.insertNodeInto(complexNode, dropNode, 0);
//							tree_SubComplexElements.expandPath(new TreePath(dropNode.getPath()));
//							List<SimpleElementTypeType> simpleList = Control16.getSimpleElements(complexElement);
//							if (simpleList != null) {
//								int index = 0;
//								for (SimpleElementTypeType se : simpleList) {
//									DefaultMutableTreeNode simpleSubNode = new DefaultMutableTreeNode(se);
//									subComplexElementsTreeModel.insertNodeInto(simpleSubNode, complexNode, index++);
//									tree_SubComplexElements.expandPath(new TreePath(simpleSubNode.getPath()));
//								}
//								tree_SubComplexElements.expandPath(new TreePath(complexNode.getPath()));
//							}
//							return true;
//						}
//					} else {
//						ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
//						ref.setIdref(complexElement);
//						ComplexElementTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
//						if (complexElements == null) {
//							complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
//							selectedElement.setComplexElements(complexElements);
//						}
//						List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
//						list.add(dropLocation.getChildIndex(), ref);
//						updateLaMu(selectedElement, getUser());
//						DefaultMutableTreeNode complexNode = new DefaultMutableTreeNode(complexElement);
//						subComplexElementsTreeModel.insertNodeInto(complexNode, subComplexElementsRoot,
//								dropLocation.getChildIndex());
//						showComplexNode(complexNode);
//						return true;
//					}
//				}
//			}
//			
//			void showComplexNode(DefaultMutableTreeNode complexNode) {
//				int index = 0;
//				List<SimpleElementTypeType> simpleList = Control16.getSimpleElements(complexElement);
//				if (simpleList != null) {
//					for (SimpleElementTypeType se : simpleList) {
//						subComplexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(se), complexNode, index++);
//					}
//					tree_SubComplexElements.expandPath(new TreePath(complexNode.getPath()));
//				}
//				List<ComplexElementTypeType> complexList = Control16.getComplexElements(complexElement);
//				if (complexList != null) {
//					for (ComplexElementTypeType ce : complexList) {
//						DefaultMutableTreeNode complexSubNode = new DefaultMutableTreeNode(ce);
//						subComplexElementsTreeModel.insertNodeInto(complexSubNode, complexNode, index++);
//						List<SimpleElementTypeType> seList = Control16.getSimpleElements(ce);
//						if (seList != null) {
//							int index2 = 0;
//							for (SimpleElementTypeType se : seList) {
//								subComplexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(se), complexSubNode,
//										index2++);
//							}
//						}
//						tree_SubComplexElements.expandPath(new TreePath(complexSubNode.getPath()));
//					}
//					tree_SubComplexElements.expandPath(new TreePath(complexNode.getPath()));
//				}
//			}
//		});
		tree_SubComplexElements.setRowHeight(20);
		ToolTipManager.sharedInstance().registerComponent(tree_SubComplexElements);
	}

	private void initComplexElementsTable() {
		elementsTableModel = new ComplexElementsTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setAutoCreateRowSorter(true);
		tbl_Elements.setFillsViewportHeight(true);
		TableRowSorter<ElementsTableModel<ComplexElementTypeType>> tableRowSorter = new TableRowSorter<>(
				elementsTableModel);
//		tableRowSorter.setComparator(ComplexElementsTableColumns.StartDate.ordinal(), dateComparator);
//		tableRowSorter.setComparator(ComplexElementsTableColumns.EndDate.ordinal(), dateComparator);
//		tableRowSorter.setComparator(ComplexElementsTableColumns.DateLamu.ordinal(), dateTimeComparator);
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

	@Override
	public void fillTable() {
		String filterString = tfd_Filter.getText().toUpperCase();
		if (filterString.isEmpty()) {
			fillTable(ComplexElementTypeType.class);
		} else {
			List<ComplexElementTypeType> elements = Editor16.getStore16().getElements(ComplexElementTypeType.class);
			elementsTableModel.clear();
			for (ComplexElementTypeType element : elements) {
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
		tfd_MaxOccurs.setEnabled(rowSelected);
		tfd_MinOccurs.setEnabled(rowSelected);
		cbx_GlobalElementCondition.setEnabled(rowSelected);
		cbx_ComplexElements.setEnabled(rowSelected);
//		tbl_SubComplexElements.setEnabled(rowSelected);
		cbx_ComplexElements.setEnabled(rowSelected);
		tbl_SimpleElements.setEnabled(rowSelected);
		cbx_SimpleElements.setEnabled(rowSelected);
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
			tfd_DateLamu.setText(selectedElement.getDateLaMu() != null
					? sdfDateTime.format(selectedElement.getDateLaMu().toGregorianCalendar().getTime())
					: "");
			tfd_UserLamu.setText(selectedElement.getUserLaMu());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_MaxOccurs
					.setText(selectedElement.getMaxOccurs() != null ? selectedElement.getMaxOccurs().toString() : null);
			tfd_MinOccurs
					.setText(selectedElement.getMinOccurs() != null ? selectedElement.getMinOccurs().toString() : null);
			ElementConditionType ec = getElementConditionType(null, selectedElement, null, null);
			cbx_GlobalElementCondition.setSelectedItem(ec != null ? ec.getCondition() : null);

			simpleElementsTableModel.clear();
			List<SimpleElementTypeType> simpleElements = getSimpleElements(selectedElement);
			if (simpleElements != null) {
				for (SimpleElementTypeType simpleElement : simpleElements) {
					simpleElementsTableModel.add(simpleElement);
				}
			}
			cbx_SimpleElements.removeAllItems();
			cbx_SimpleElements.addItem(null);
			List<SimpleElementTypeType> seList = Editor16.getStore16().getElements(SimpleElementTypeType.class);
			for (SimpleElementTypeType element : seList) {
				cbx_SimpleElements.addItem("[" + element.getId() + "] " + element.getDescription());
			}

			SubComplexElementsTreeModel treeModel = (SubComplexElementsTreeModel) tree_SubComplexElements.getModel();
			subComplexElementsRoot.setUserObject(selectedElement.getDescription());
			subComplexElementsRoot.removeAllChildren();
			treeModel.nodeStructureChanged(subComplexElementsRoot);
			List<ComplexElementTypeType> complexTreeElements = getComplexElements(selectedElement);
			if (complexTreeElements != null) {
				int parentIndex = 0;
				for (ComplexElementTypeType complexElement : complexTreeElements) {
					DefaultMutableTreeNode complexTreeElement = new DefaultMutableTreeNode(complexElement);
					treeModel.insertNodeInto(complexTreeElement, subComplexElementsRoot, parentIndex);
					tree_SubComplexElements.expandPath(new TreePath(subComplexElementsRoot.getPath()));
					parentIndex++;
					int childIndex = 0;
					List<SimpleElementTypeType> complexElementsimpleElements = getSimpleElements(complexElement);
					if (complexElementsimpleElements != null) {
						for (SimpleElementTypeType complexElementsimpleElement : complexElementsimpleElements) {
							DefaultMutableTreeNode simpleTreeElement = new DefaultMutableTreeNode(
									complexElementsimpleElement);
							treeModel.insertNodeInto(simpleTreeElement, complexTreeElement, childIndex);
							tree_SubComplexElements.expandPath(new TreePath(complexTreeElement.getPath()));
							childIndex++;
						}
					}
				}
			}

//			subComplexElementsTableModel.clear();
//			List<ComplexElementTypeType> complexElements = getComplexElements(selectedElement);
//			if (complexElements != null) {
//				for (ComplexElementTypeType complexElement : complexElements) {
//					subComplexElementsTableModel.add(complexElement);
//					List<SimpleElementTypeType> subSimpleElements = getSimpleElements(complexElement);
//					if (subSimpleElements != null) {
//						for (SimpleElementTypeType subSimpleElement : subSimpleElements) {
//							simpleElementsTableModel.add(subSimpleElement);
//						}
//					}
//				}
//			}
			useElementsTableModel.clear();
			List<ElementType> useElements = getUseElements(selectedElement);
			if (useElements != null) {
				for (ElementType elementType : useElements) {
					useElementsTableModel.add(elementType);
				}
			}
			cbx_ComplexElements.removeAllItems();
			cbx_ComplexElements.addItem(null);
			List<ComplexElementTypeType> ceList = Editor16.getStore16().getElements(ComplexElementTypeType.class);
			ceList.sort(new Comparator<ComplexElementTypeType>() {
				@Override
				public int compare(ComplexElementTypeType o1, ComplexElementTypeType o2) {
					return o1.getDescription().compareTo(o2.getDescription());
				}
			});
			for (ComplexElementTypeType element : ceList) {
				cbx_ComplexElements.addItem(element.getDescription() + " [" + element.getId() + "]");
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
			tfd_MaxOccurs.setText("");
			tfd_MinOccurs.setText("");
			cbx_GlobalElementCondition.setSelectedIndex(0);
			cbx_ComplexElements.removeAllItems();
//			subComplexElementsTableModel.clear();
			simpleElementsTableModel.clear();
			useElementsTableModel.clear();
			cbx_SimpleElements.removeAllItems();
		}
		inSelection = false;
	}

	public void newElement() {
		try {
			ComplexElementTypeType newComplexElementType = objectFactory.createComplexElementTypeType();
			newElement(newComplexElementType, "ComplexElement_");

			int row = elementsTableModel.add(newComplexElementType);
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
		ComplexElementTypeType origComplexElementType = elementsTableModel.get(row);

		try {
			ComplexElementTypeType copyComplexElementType = objectFactory.createComplexElementTypeType();
			newElement(copyComplexElementType, "ComplexElement_");
			store.generateCopyId(copyComplexElementType, origComplexElementType);
			copyComplexElementType.setCategory(origComplexElementType.getCategory());
			copyComplexElements(origComplexElementType, copyComplexElementType);
			copyComplexElementType.setDescription(origComplexElementType.getDescription());
			copyComplexElementType.setEndDate(origComplexElementType.getEndDate());
			copyComplexElementType.setHelpInfo(origComplexElementType.getHelpInfo());
			copyComplexElementType.setLanguage(origComplexElementType.getLanguage());
			copyComplexElementType.setMaxOccurs(origComplexElementType.getMaxOccurs());
			copyComplexElementType.setMinOccurs(origComplexElementType.getMinOccurs());
			copySimpleElements(origComplexElementType, copyComplexElementType);
			copyComplexElementType.setStartDate(origComplexElementType.getStartDate());
			copyComplexElementType.setState(origComplexElementType.getState());
			store.put(copyComplexElementType.getId(), copyComplexElementType);
			int copyrow = elementsTableModel.add(copyComplexElementType);
			copyrow = tbl_Elements.convertRowIndexToView(copyrow);
			tbl_Elements.getSelectionModel().setSelectionInterval(copyrow, copyrow);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copyComplexElements(ComplexElementTypeType origComplexType, ComplexElementTypeType copyComplexType) {
		ComplexElementTypeType.ComplexElements complexElements = origComplexType.getComplexElements();
		if (complexElements != null) {
			List<Object> refs = complexElements.getComplexElementTypeOrComplexElementTypeRef();
			if (refs != null) {
				ComplexElementTypeType.ComplexElements copyComplexElements = objectFactory
						.createComplexElementTypeTypeComplexElements();
				List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				for (Object item : refs) {
					copyRefs.add(item);
				}
				copyComplexType.setComplexElements(copyComplexElements);
			}
		}
	}

	private void copySimpleElements(ComplexElementTypeType origComplexType, ComplexElementTypeType copyComplexType) {
		ComplexElementTypeType.SimpleElements simpleElements = origComplexType.getSimpleElements();
		if (simpleElements != null) {
			List<Object> refs = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
			if (refs != null) {
				ComplexElementTypeType.SimpleElements copySimpleElements = objectFactory
						.createComplexElementTypeTypeSimpleElements();
				List<Object> copyRefs = copySimpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
				for (Object item : refs) {
					copyRefs.add(item);
				}
				copyComplexType.setSimpleElements(copySimpleElements);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		ComplexElementTypeType complexElementType = elementsTableModel.get(row);

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + complexElementType.getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		try {
			Class<?>[] classesWithComplexElements = { ComplexElementTypeType.class, ProjectTypeType.class,
					PersonTypeType.class, OrganisationTypeType.class, AppendixTypeType.class, MessageTypeType.class };
			for (Class<?> classType : classesWithComplexElements) {
				List<ElementType> elements = store.getElements(classType);
				for (ElementType elementType : elements) {
					Method getComplexElements = elementType.getClass().getMethod("getComplexElements",
							new Class<?>[] {});
					Object complexElements = getComplexElements.invoke(elementType);
					if (complexElements != null) {
						Method getComplexElementTypeOrComplexElementTypeRef = complexElements.getClass()
								.getMethod("getComplexElementTypeOrComplexElementTypeRef", new Class<?>[] {});
						List<Object> list = (List<Object>) getComplexElementTypeOrComplexElementTypeRef
								.invoke(complexElements);
						for (Object object : list) {
							ComplexElementTypeType ceType = (ComplexElementTypeType) getElementType(object);
							if (ceType.equals(complexElementType)) {
								list.remove(object);
								break;
							}
						}
						if (list.isEmpty()) {
							Class<?>[] cArg = new Class[1];
							switch (classType.getSimpleName()) {
							case "ComplexElementTypeType":
								cArg[0] = ComplexElementTypeType.ComplexElements.class;
								break;
							case "ProjectTypeType":
								cArg[0] = ProjectTypeType.ComplexElements.class;
								break;
							case "PersonTypeType":
								cArg[0] = PersonTypeType.ComplexElements.class;
								break;
							case "OrganisationTypeType":
								cArg[0] = OrganisationTypeType.ComplexElements.class;
								break;
							case "AppendixTypeType":
								cArg[0] = AppendixTypeType.ComplexElements.class;
								break;
							case "MessageTypeType":
								cArg[0] = MessageTypeType.ComplexElements.class;
								break;
							}
							Method setComplexElements = elementType.getClass().getMethod("setComplexElements", cArg);
							setComplexElements.invoke(elementType, (Object) null);
						}
					}
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		Editor16.getStore16().remove(complexElementType.getId());
		elementsTableModel.remove(row);
	}

	public void selectComplexElement() {
		int selectedIndex = cbx_ComplexElements.getSelectedIndex();
		btn_AddComplexElement.setEnabled(selectedIndex > 0);
	}

	public void addComplexElement() {
		String label = (String) cbx_ComplexElements.getSelectedItem();
		String ceId = label.substring(label.lastIndexOf('[') + 1, label.lastIndexOf("]"));
		ComplexElementTypeType element = Editor16.getStore16().getElement(ComplexElementTypeType.class, ceId);
		ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
		ref.setIdref(element);
		ComplexElementTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
		if (complexElements == null) {
			complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
			selectedElement.setComplexElements(complexElements);
		}
		List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		list.add(ref);
//		subComplexElementsTableModel.add(element);
		DefaultMutableTreeNode elementNode = new DefaultMutableTreeNode(element);
		subComplexElementsTreeModel.insertNodeInto(elementNode, subComplexElementsRoot,
				subComplexElementsRoot.getChildCount());
		List<SimpleElementTypeType> simpleElements = Control16.getSimpleElements(element);
		if (simpleElements != null) {
			for (SimpleElementTypeType se : simpleElements) {
				subComplexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(se), elementNode,
						elementNode.getChildCount());
			}
			tree_SubComplexElements.expandPath(new TreePath(elementNode.getPath()));
		}
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
	}

//	public void removeComplexElement() {
//		int selectedSubComplexElementsRow = tbl_SubComplexElements.getSelectedRow();
//
//		int response = JOptionPane.showConfirmDialog(getPanel(),
//				getBundle().getString("lbl_Remove") + ": "
//						+ subComplexElementsTableModel.elements.get(selectedSubComplexElementsRow).getId(),
//				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
//		if (response == JOptionPane.CANCEL_OPTION)
//			return;
//
//		ComplexElementTypeType complexElement = subComplexElementsTableModel.remove(selectedSubComplexElementsRow);
//		ComplexElements complexElements = selectedElement.getComplexElements();
//		List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
//		for (Object object : list) {
//			ComplexElementTypeType element = null;
//			if (object instanceof ComplexElementTypeTypeRef) {
//				element = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object).getIdref();
//			} else if (object instanceof ComplexElementTypeType) {
//				element = (ComplexElementTypeType) object;
//			}
//			if (element != null) {
//				if (complexElement.equals(element)) {
//					list.remove(object);
//					break;
//				}
//			}
//		}
//		if (list.isEmpty()) {
//			selectedElement.setComplexElements(null);
//		}
//		updateLaMu(selectedElement, getUser());
//		elementsTableModel.update(selectedRow);
//	}

	public void selectSimpleElement() {
		int selectedIndex = cbx_SimpleElements.getSelectedIndex();
		btn_AddSimpleElement.setEnabled(selectedIndex > 0);
	}

	public void addSimpleElement() {
		String simpleElement = (String) cbx_SimpleElements.getSelectedItem();
		String seId = simpleElement.substring(1, simpleElement.indexOf("]"));
		SimpleElementTypeType element = Editor16.getStore16().getElement(SimpleElementTypeType.class, seId);
		SimpleElementTypeTypeRef ref = objectFactory.createSimpleElementTypeTypeRef();
		ref.setIdref(element);
		ComplexElementTypeType.SimpleElements simpleElements = selectedElement.getSimpleElements();
		if (simpleElements == null) {
			simpleElements = objectFactory.createComplexElementTypeTypeSimpleElements();
			selectedElement.setSimpleElements(simpleElements);
		}
		List<Object> list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
		list.add(ref);
		simpleElementsTableModel.add(element);
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
	}

	public void removeTreeElement() {
		System.out.println("removeTreeElement");
		TreePath selectionPath = tree_SubComplexElements.getSelectionPath();
		DefaultMutableTreeNode node = ((DefaultMutableTreeNode) selectionPath.getLastPathComponent());
		Object nodeObject = node.getUserObject();
		if (nodeObject instanceof SimpleElementTypeType) {
			SimpleElementTypeType se = (SimpleElementTypeType) nodeObject;
			int selectedOption = JOptionPane.showConfirmDialog(tree_SubComplexElements,
					getBundle().getString("lbl_Remove") + " \"" + se.getDescription() + "\"?",
					getBundle().getString("lbl_Remove") + " element", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (selectedOption == JOptionPane.OK_OPTION) {
				Object parentObject = ((DefaultMutableTreeNode) (node.getParent())).getUserObject();
				ComplexElementTypeType parentCe = (ComplexElementTypeType) parentObject;
				List<Object> refList = parentCe.getSimpleElements().getSimpleElementTypeOrSimpleElementTypeRef();
				for (Object ref : refList) {
					SimpleElementTypeType seRef = null;
					if (ref instanceof SimpleElementTypeType) {
						seRef = (SimpleElementTypeType) ref;
					} else if (ref instanceof SimpleElementTypeTypeRef) {
						seRef = (SimpleElementTypeType) (((SimpleElementTypeTypeRef) ref).getIdref());
					}
					if (seRef.getId().equals(se.getId())) {
						refList.remove(ref);
						updateLaMu(parentCe, getUser());
						subComplexElementsTreeModel.removeNodeFromParent(node);
						btn_RemoveTreeElement.setEnabled(false);
						break;
					}
				}
			}
		} else if (nodeObject instanceof ComplexElementTypeType) {
			ComplexElementTypeType ce = (ComplexElementTypeType) nodeObject;
			int selectedOption = JOptionPane.showConfirmDialog(tree_SubComplexElements,
					getBundle().getString("lbl_Remove") + " \"" + ce.getDescription() + "\"?",
					getBundle().getString("lbl_Remove") + " element", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (selectedOption == JOptionPane.OK_OPTION) {
				List<Object> refList = selectedElement.getComplexElements()
						.getComplexElementTypeOrComplexElementTypeRef();
				for (Object ref : refList) {
					ComplexElementTypeType ceRef = null;
					if (ref instanceof ComplexElementTypeType) {
						ceRef = (ComplexElementTypeType) ref;
					} else if (ref instanceof ComplexElementTypeTypeRef) {
						ceRef = (ComplexElementTypeType) (((ComplexElementTypeTypeRef) ref).getIdref());
					}
					if (ceRef.getId().equals(ce.getId())) {
						refList.remove(ref);
						updateLaMu(selectedElement, getUser());
						subComplexElementsTreeModel.removeNodeFromParent(node);
						btn_RemoveTreeElement.setEnabled(false);
						break;
					}
				}
			}
		}

	}

	public void removeSimpleElement() {
		int selectedSimpleElementsRow = tbl_SimpleElements.getSelectedRow();

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": "
						+ simpleElementsTableModel.elements.get(selectedSimpleElementsRow).getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		SimpleElementTypeType simpleElement = simpleElementsTableModel.remove(selectedSimpleElementsRow);
		SimpleElements simpleElements = selectedElement.getSimpleElements();
		List<Object> list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
		for (Object object : list) {
			SimpleElementTypeType element = null;
			if (object instanceof SimpleElementTypeTypeRef) {
				element = (SimpleElementTypeType) ((SimpleElementTypeTypeRef) object).getIdref();
			} else if (object instanceof SimpleElementTypeType) {
				element = (SimpleElementTypeType) object;
			}
			if (element != null) {
				if (simpleElement.equals(element)) {
					list.remove(object);
					break;
				}
			}
		}
		if (list.isEmpty()) {
			selectedElement.setSimpleElements(null);
		}
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
	}

}
