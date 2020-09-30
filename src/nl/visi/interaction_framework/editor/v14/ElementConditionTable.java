package nl.visi.interaction_framework.editor.v14;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ElementConditionType;
import nl.visi.schemas._20140331.ElementConditionType.SimpleElement;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeTypeRef;

public class ElementConditionTable extends Control14 {
	private static final String ELEMENT_CONDITION_PANEL = "nl/visi/interaction_framework/editor/swixml/ElementConditionPanel.xml";
	private JPanel panel;
	private JTable tbl_ElementConditions, tbl_Messages;
	private ElementConditionsTableModel elementConditionsTableModel;
	private TransactionsPanelControl14.MessagesTableModel messagesTableModel;
	private JComboBox<String> cbx_Conditions, cbx_ComplexElements, cbx_SimpleElements;
	private JButton btn_NewElementCondition, btn_RemoveElementCondition;

	private enum ElementConditionsTableColumns {
		Id, Description, Condition, ComplexElement, SimpleElement, Global;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	@SuppressWarnings("serial")
	private class ElementConditionsTableModel extends ElementsTableModel<ElementConditionType> {

		@Override
		public int getColumnCount() {
			return ElementConditionsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return ElementConditionsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ElementConditionType elementConditionType = get(rowIndex);

			switch (ElementConditionsTableColumns.values()[columnIndex]) {
			case ComplexElement:
				ComplexElementTypeType complexElement = getComplexElement(elementConditionType);
				if (complexElement != null) {
					return complexElement.getId();
				}
				break;
			case Condition:
				return elementConditionType.getCondition();
			case Description:
				return elementConditionType.getDescription();
			case Id:
				return elementConditionType.getId();
			case SimpleElement:
				SimpleElementTypeType simpleElement = getSimpleElement(elementConditionType);
				if (simpleElement != null) {
					return simpleElement.getId();
				}
				break;
			case Global:
				return elementConditionType.getMessageInTransaction() == null;
			default:
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			switch (ElementConditionsTableColumns.values()[columnIndex]) {
			case ComplexElement:
				return true;
			case Condition:
				return true;
			case Description:
				return true;
			case Id:
				break;
			case SimpleElement:
				return true;
			case Global:
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (inValueChangeElementConditionTable)
				return;
			ElementConditionType elementConditionType = elementConditionsTableModel.get(rowIndex);

			switch (ElementConditionsTableColumns.values()[columnIndex]) {
			case Description:
				elementConditionType.setDescription((String) value);
				break;
			case Condition:
				elementConditionType.setCondition((String) value);
				break;
			case ComplexElement:
				if (value == null) {
					elementConditionType.setComplexElement(null);
				} else {
					String idref = (String) value;
					ComplexElementTypeType ce = Editor14.getStore14().getElement(ComplexElementTypeType.class, idref);
					setElementConditionTypeComplexElement(elementConditionType, ce);
					elementConditionTableListener.valueChanged(null);
				}
				break;
			case SimpleElement:
				if (value == null) {
					elementConditionType.setSimpleElement(null);
				} else {
					String idref = (String) value;
					SimpleElementTypeType se = Editor14.getStore14().getElement(SimpleElementTypeType.class, idref);
					setElementConditionTypeSimpleElement(elementConditionType, se);
				}
				break;
			case Global:
				if ((Boolean) value) {
					elementConditionType.setMessageInTransaction(null);
				} else {
					int selectedMessageRow = tbl_Messages.getSelectedRow();
					MessageInTransactionTypeType mitt = messagesTableModel.get(selectedMessageRow);
					setElementConditionTypeMessageInTransaction(elementConditionType, mitt);
				}
				break;
			default:
				break;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (ElementConditionsTableColumns.values()[columnIndex]) {
			case ComplexElement:
				return ElementType.class;
			case Condition:
				return String.class;
			case Description:
				return String.class;
			case Id:
				return String.class;
			case SimpleElement:
				return ElementType.class;
			case Global:
				return Boolean.class;
			}
			return Object.class;
		}
	}

	private boolean inValueChangeElementConditionTable = false;

	private void initElementConditionsTable() {
		elementConditionsTableModel = new ElementConditionsTableModel();
		tbl_ElementConditions.setModel(elementConditionsTableModel);
		tbl_ElementConditions.setAutoCreateRowSorter(true);
		tbl_ElementConditions.setFillsViewportHeight(true);

		cbx_Conditions = new JComboBox<>(new DefaultComboBoxModel<String>());
		cbx_Conditions.addItem("FIXED");
		cbx_Conditions.addItem("FREE");
		cbx_Conditions.addItem("EMPTY");
		TableColumn conditionColumn = tbl_ElementConditions.getColumnModel()
				.getColumn(ElementConditionsTableColumns.Condition.ordinal());
		conditionColumn.setCellEditor(new DefaultCellEditor(cbx_Conditions));

		cbx_ComplexElements = new JComboBox<>(new DefaultComboBoxModel<String>());
		TableColumn complexElementColumn = tbl_ElementConditions.getColumnModel()
				.getColumn(ElementConditionsTableColumns.ComplexElement.ordinal());
		complexElementColumn.setCellEditor(new DefaultCellEditor(cbx_ComplexElements));

		cbx_SimpleElements = new JComboBox<>(new DefaultComboBoxModel<String>());
		TableColumn simpleElementColumn = tbl_ElementConditions.getColumnModel()
				.getColumn(ElementConditionsTableColumns.SimpleElement.ordinal());
		simpleElementColumn.setCellEditor(new DefaultCellEditor(cbx_SimpleElements));

		tbl_ElementConditions.getSelectionModel().addListSelectionListener(elementConditionTableListener);
	}

	private ListSelectionListener elementConditionTableListener = new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			inValueChangeElementConditionTable = true;

			int selectedElementConditionRow = tbl_ElementConditions.getSelectedRow();
			boolean rowSelected = selectedElementConditionRow >= 0;
			btn_RemoveElementCondition.setEnabled(rowSelected);
			if (rowSelected) {
				selectedElementConditionRow = tbl_ElementConditions.getRowSorter()
						.convertRowIndexToModel(selectedElementConditionRow);
				ElementConditionType ec = elementConditionsTableModel.get(selectedElementConditionRow);
				ComplexElementTypeType complexElement = getComplexElement(ec);

				cbx_ComplexElements.removeAllItems();
				cbx_ComplexElements.addItem(null);
				fillComplexTypeCbx(selectedMessage);
				if (complexElement != null) {
					cbx_ComplexElements.setSelectedItem(complexElement.getId());
				} else {
					cbx_ComplexElements.setSelectedItem(null);
				}
				cbx_SimpleElements.removeAllItems();
				cbx_SimpleElements.addItem(null);
				fillSimpleTypeCbx((String) cbx_ComplexElements.getSelectedItem());
				SimpleElementTypeType simpleElement = getSimpleElement(ec);
				if (simpleElement != null) {
					int indexOf = ((DefaultComboBoxModel<String>) (cbx_SimpleElements.getModel()))
							.getIndexOf(simpleElement.getId());
					cbx_SimpleElements.setSelectedItem(indexOf > 0 ? simpleElement.getId() : null);
					if (indexOf > 0) {
						SimpleElement simpleElementObject = ec.getSimpleElement();
						SimpleElementTypeTypeRef simpleElementTypeTypeRef = objectFactory
								.createSimpleElementTypeTypeRef();
						simpleElementTypeTypeRef.setIdref(simpleElement);
						simpleElementObject.setSimpleElementTypeRef(simpleElementTypeTypeRef);
					} else {
						ec.setSimpleElement(null);
					}
				} else {
					cbx_SimpleElements.setSelectedItem(null);
					ec.setSimpleElement(null);
				}
				elementConditionsTableModel.fireTableCellUpdated(selectedElementConditionRow,
						ElementConditionsTableColumns.SimpleElement.ordinal());
			}

			inValueChangeElementConditionTable = false;
		}

	};

	private void fillComplexTypeCbx(MessageTypeType message) {
		List<ComplexElementTypeType> complexElements = getComplexElements(message);
		if (complexElements != null) {
			for (ComplexElementTypeType ce : complexElements) {
				cbx_ComplexElements.addItem(ce.getId());
			}
		}
	}

	private void fillSimpleTypeCbx(String complexElementId) {
		if (complexElementId != null) {
			ComplexElementTypeType parentCE = Editor14.getStore14().getElement(ComplexElementTypeType.class,
					complexElementId);
			List<SimpleElementTypeType> simpleElements = getSimpleElements(parentCE);
			if (simpleElements != null) {
				for (SimpleElementTypeType se : simpleElements) {
					cbx_SimpleElements.addItem(se.getId());
				}
			}
		} else {
			List<ComplexElementTypeType> parentComplexElements = getComplexElements(selectedMessage);
			if (parentComplexElements != null) {
				for (ComplexElementTypeType parentCE : parentComplexElements) {
					List<SimpleElementTypeType> parentSimpleElements = getSimpleElements(parentCE);
					if (parentSimpleElements != null) {
						for (SimpleElementTypeType se : parentSimpleElements) {
							if (((DefaultComboBoxModel<String>) cbx_SimpleElements.getModel())
									.getIndexOf(se.getId()) < 0) {
								cbx_SimpleElements.addItem(se.getId());
							}
						}
					}
					List<ComplexElementTypeType> childComplexElements = getComplexElements(parentCE);
					if (childComplexElements != null) {
						for (ComplexElementTypeType childCe : childComplexElements) {
							List<SimpleElementTypeType> childSimpleElements = getSimpleElements(childCe);
							if (childSimpleElements != null) {
								for (SimpleElementTypeType se : childSimpleElements) {
									if (((DefaultComboBoxModel<String>) cbx_SimpleElements.getModel())
											.getIndexOf(se.getId()) < 0) {
										cbx_SimpleElements.addItem(se.getId());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	void fillElementConditionsTable(MessageInTransactionTypeType mitt) {
		MessageTypeType message = getMessage(mitt);

		elementConditionsTableModel.clear();

		ElementConditionType universalCondition = getElementConditionType(null, null, null);
		if (universalCondition != null) {
			elementConditionsTableModel.add(universalCondition);
		}
		List<ElementConditionType> elements = Editor14.getStore14().getElements(ElementConditionType.class);

		for (ElementConditionType ec : elements) {
			MessageInTransactionTypeType ecMitt = getMessageInTransaction(ec);
			if (ecMitt != null) {
				// Always add element conditions for this specific MITT
				if (ecMitt.equals(mitt)) {
					elementConditionsTableModel.add(ec);
				}
			} else {
				List<ComplexElementTypeType> msgComplexElements = getComplexElements(message);
				ComplexElementTypeType ecComplexElement = getComplexElement(ec);

				// Complex elements
				if (msgComplexElements != null && ecComplexElement != null) {
					if (msgComplexElements.contains(ecComplexElement)) {
						elementConditionsTableModel.add(ec);
					}
				} else if (ecComplexElement == null) {
					// Simple elements
					SimpleElementTypeType ecSimpleElement = getSimpleElement(ec);
					if (msgComplexElements != null && msgComplexElements.size() > 0 && ecSimpleElement != null) {
						Set<SimpleElementTypeType> seElementSet = new HashSet<SimpleElementTypeType>();
						for (ComplexElementTypeType ce : msgComplexElements) {
							List<SimpleElementTypeType> simpleElements = getSimpleElements(ce);
							if (simpleElements != null) {
								for (SimpleElementTypeType simpleElement : simpleElements) {
									seElementSet.add(simpleElement);
								}
							}
						}
						if (seElementSet.contains(ecSimpleElement)) {
							elementConditionsTableModel.add(ec);
						}
					}
				}
			}
		}
	}

	public void newElementCondition() {
		try {
			String newId = Editor14.getStore14().getNewId("ec_");
			ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
			Editor14.getStore14().put(newId, newElementConditionType);
			newElementConditionType.setId(newId);
			newElementConditionType.setDescription("Decription of " + newId);
			newElementConditionType.setCondition("FREE");
			setElementConditionTypeMessageInTransaction(newElementConditionType, selectedMitt);
			int row = elementConditionsTableModel.add(newElementConditionType);
			tbl_ElementConditions.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeElementCondition() {
		int row = tbl_ElementConditions.getSelectedRow();
		ElementConditionType elementConditionType = elementConditionsTableModel.get(row);
		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + elementConditionType.getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		Editor14.getStore14().remove(elementConditionType.getId());
		elementConditionsTableModel.remove(row);
	}

	public ElementConditionTable(JTable tbl_Messages) throws Exception {
		super();
		panel = (JPanel) render(ELEMENT_CONDITION_PANEL);
		this.tbl_Messages = tbl_Messages;
		initElementConditionsTable();
	}

	public JPanel getPanel() {
		return panel;
	}

	public void clear() {
		elementConditionsTableModel.clear();
	}

	private MessageInTransactionTypeType selectedMitt;
	private MessageTypeType selectedMessage;

	public void setSelectedMitt(MessageInTransactionTypeType mitt) {
		selectedMitt = mitt;
		selectedMessage = getMessage(mitt);
		btn_NewElementCondition.setEnabled(mitt != null);
		elementConditionTableListener.valueChanged(null);
	}
}
