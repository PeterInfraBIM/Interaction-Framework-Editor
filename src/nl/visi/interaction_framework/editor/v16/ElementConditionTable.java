package nl.visi.interaction_framework.editor.v16;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementConditionType.ComplexElements;
import nl.visi.schemas._20160331.ElementConditionType.SimpleElement;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeTypeRef;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;

public class ElementConditionTable extends Control16 {
	private static final String ELEMENT_CONDITION_PANEL = "nl/visi/interaction_framework/editor/swixml/ElementConditionPanel.xml";
	private JPanel panel;
	private JTable tbl_ElementConditions, tbl_Messages;
	private ElementConditionsTableModel elementConditionsTableModel;
	private TransactionsPanelControl16.MessagesTableModel messagesTableModel;
	private JComboBox<String> cbx_Conditions, cbx_ComplexElements1, cbx_ComplexElements2, cbx_SimpleElements;
	private JButton btn_NewElementCondition, btn_RemoveElementCondition;

	private enum ElementConditionsTableColumns {
		Id, Description, Condition, ComplexElement1, ComplexElement2, SimpleElement, Global;

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
			case ComplexElement1:
				ComplexElements complexElement1s = elementConditionType.getComplexElements();
				if (complexElement1s != null) {
					List<Object> objects = complexElement1s.getComplexElementTypeOrComplexElementTypeRef();
					if (objects != null && objects.size() > 0) {
						Object object = objects.get(0);
						ComplexElementTypeType complexElementType = null;
						if (object != null && object instanceof ComplexElementTypeTypeRef) {
							complexElementType = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object)
									.getIdref();
						}
						if (object != null && object instanceof ComplexElementTypeType) {
							complexElementType = (ComplexElementTypeType) object;
						}
						return complexElementType.getId();
					}
				}
				break;
			case ComplexElement2:
				ComplexElements complexElement2s = elementConditionType.getComplexElements();
				if (complexElement2s != null) {
					List<Object> objects = complexElement2s.getComplexElementTypeOrComplexElementTypeRef();
					if (objects != null && objects.size() > 1) {
						Object object = objects.get(1);
						if (object != null && object instanceof ComplexElementTypeTypeRef) {
							ComplexElementTypeType complexElementType = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object)
									.getIdref();
							return complexElementType.getId();
						}
						if (object != null && object instanceof ComplexElementTypeType) {
							ComplexElementTypeType complexElementType = (ComplexElementTypeType) object;
							return complexElementType.getId();
						}
					}
				}
				break;
			case Condition:
				return elementConditionType.getCondition();
			case Description:
				return elementConditionType.getDescription();
			case Id:
				return elementConditionType.getId();
			case SimpleElement:
				ElementConditionType.SimpleElement simpleElement = elementConditionType.getSimpleElement();
				if (simpleElement != null) {
					SimpleElementTypeType simpleElementType = simpleElement.getSimpleElementType();
					if (simpleElementType == null) {
						simpleElementType = (SimpleElementTypeType) simpleElement.getSimpleElementTypeRef().getIdref();
					}
					if (simpleElementType != null) {
						return simpleElementType.getId();
					}
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
			case ComplexElement1:
				return true;
			case ComplexElement2:
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
			case ComplexElement1:
				if (value == null) {
					elementConditionType.setComplexElements(null);
				} else {
					ComplexElements complexElement1s = elementConditionType.getComplexElements();
					if (complexElement1s == null) {
						complexElement1s = objectFactory.createElementConditionTypeComplexElements();
					}
					String idref = (String) value;
					ComplexElementTypeType ce = Editor16.getStore16().getElement(ComplexElementTypeType.class, idref);
					ComplexElementTypeTypeRef ceRef = objectFactory.createComplexElementTypeTypeRef();
					ceRef.setIdref(ce);
					List<Object> complexElementObjects = complexElement1s
							.getComplexElementTypeOrComplexElementTypeRef();
					if (complexElementObjects.size() > 0) {
						complexElementObjects.set(0, ceRef);
					} else {
						complexElementObjects.add(0, ceRef);
					}
					elementConditionType.setComplexElements(complexElement1s);
					elementConditionTableListener.valueChanged(null);
				}
				break;
			case ComplexElement2:
				if (value == null) {
					List<ComplexElementTypeType> complexElementTypes = getComplexElements(elementConditionType);
					if (complexElementTypes != null) {
						ComplexElements complexElements = elementConditionType.getComplexElements();
						List<Object> complexElementObjects = complexElements
								.getComplexElementTypeOrComplexElementTypeRef();
						if (complexElementObjects.size() > 1) {
							complexElementObjects.remove(1);
						}
					}
				} else {
					ComplexElements complexElement1s = elementConditionType.getComplexElements();
					if (complexElement1s == null) {
						break;
					}
					String idref = (String) value;
					ComplexElementTypeType ce = Editor16.getStore16().getElement(ComplexElementTypeType.class, idref);
					ComplexElementTypeTypeRef ceRef = objectFactory.createComplexElementTypeTypeRef();
					ceRef.setIdref(ce);
					List<Object> complexElementObjects = complexElement1s
							.getComplexElementTypeOrComplexElementTypeRef();
					if (complexElementObjects.size() == 1) {
						complexElementObjects.add(1, ceRef);
					} else {
						complexElementObjects.set(1, ceRef);
					}
					elementConditionType.setComplexElements(complexElement1s);
					elementConditionTableListener.valueChanged(null);
				}
				break;
			case SimpleElement:
				if (value == null) {
					elementConditionType.setSimpleElement(null);
				} else {
					String idref = (String) value;
					SimpleElementTypeType se = Editor16.getStore16().getElement(SimpleElementTypeType.class, idref);
					SimpleElementTypeTypeRef seRef = objectFactory.createSimpleElementTypeTypeRef();
					seRef.setIdref(se);
					ElementConditionType.SimpleElement set = objectFactory.createElementConditionTypeSimpleElement();
					set.setSimpleElementTypeRef(seRef);
					elementConditionType.setSimpleElement(set);
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
			case ComplexElement1:
				return ElementType.class;
			case ComplexElement2:
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

		cbx_ComplexElements1 = new JComboBox<>(new DefaultComboBoxModel<String>());
		TableColumn complexElement1Column = tbl_ElementConditions.getColumnModel()
				.getColumn(ElementConditionsTableColumns.ComplexElement1.ordinal());
		complexElement1Column.setCellEditor(new DefaultCellEditor(cbx_ComplexElements1));
		cbx_ComplexElements2 = new JComboBox<>(new DefaultComboBoxModel<String>());
		cbx_ComplexElements2.addItem(null);
		TableColumn complexElement2Column = tbl_ElementConditions.getColumnModel()
				.getColumn(ElementConditionsTableColumns.ComplexElement2.ordinal());
		complexElement2Column.setCellEditor(new DefaultCellEditor(cbx_ComplexElements2));

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
				List<ComplexElementTypeType> complexElements = getComplexElements(ec);

				cbx_ComplexElements1.removeAllItems();
				cbx_ComplexElements1.addItem(null);
				fillComplexTypeCbx1(selectedMessage);
				if (complexElements != null && complexElements.size() > 0) {
					cbx_ComplexElements1.setSelectedItem(complexElements.get(0).getId());
				} else {
					cbx_ComplexElements1.setSelectedItem(null);
				}
				cbx_ComplexElements2.removeAllItems();
				cbx_ComplexElements2.addItem(null);
				fillComplexTypeCbx2((String) cbx_ComplexElements1.getSelectedItem());
				if (complexElements != null && complexElements.size() > 1) {
					cbx_ComplexElements2.setSelectedItem(complexElements.get(1).getId());
				} else {
					cbx_ComplexElements2.setSelectedItem(null);
				}
				cbx_SimpleElements.removeAllItems();
				cbx_SimpleElements.addItem(null);
				fillSimpleTypeCbx((String) cbx_ComplexElements1.getSelectedItem(),
						(String) cbx_ComplexElements2.getSelectedItem());
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

	private void fillComplexTypeCbx1(MessageTypeType message) {
		List<ComplexElementTypeType> complexElements = getComplexElements(message);
		if (complexElements != null) {
			for (ComplexElementTypeType ce : complexElements) {
				cbx_ComplexElements1.addItem(ce.getId());
			}
		}
	}

	private void fillComplexTypeCbx2(String complexElementId) {
		if (complexElementId != null) {
			ComplexElementTypeType parentCE = Editor16.getStore16().getElement(ComplexElementTypeType.class,
					complexElementId);
			List<ComplexElementTypeType> childCEs = getComplexElements(parentCE);
			if (childCEs != null) {
				for (ComplexElementTypeType ce : childCEs) {
					cbx_ComplexElements2.addItem(ce.getId());
				}
			}
		}
	}

	private void fillSimpleTypeCbx(String complexElement1Id, String complexElement2Id) {
		if (complexElement1Id != null) {
			ComplexElementTypeType parentCE = Editor16.getStore16().getElement(ComplexElementTypeType.class,
					complexElement1Id);
			List<SimpleElementTypeType> simpleElements1 = getSimpleElements(parentCE);
			if (simpleElements1 != null) {
				for (SimpleElementTypeType se : simpleElements1) {
					cbx_SimpleElements.addItem(se.getId());
				}
			}
			if (complexElement2Id != null) {
				ComplexElementTypeType childCE = Editor16.getStore16().getElement(ComplexElementTypeType.class,
						complexElement2Id);
				List<SimpleElementTypeType> simpleElements2 = getSimpleElements(childCE);
				if (simpleElements2 != null) {
					for (SimpleElementTypeType se : simpleElements2) {
						if (((DefaultComboBoxModel<String>) cbx_SimpleElements.getModel()).getIndexOf(se.getId()) < 0) {
							cbx_SimpleElements.addItem(se.getId());
						}
					}

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
		List<ElementConditionType> elements = Editor16.getStore16().getElements(ElementConditionType.class);

		for (ElementConditionType ec : elements) {
			MessageInTransactionTypeType ecMitt = getMessageInTransaction(ec);
			if (ecMitt != null) {
				if (ecMitt.equals(mitt)) {
					elementConditionsTableModel.add(ec);
				}
			} else {
				List<ComplexElementTypeType> msgComplexElements = getComplexElements(message);
				List<ComplexElementTypeType> ecComplexElements = getComplexElements(ec);

				// Complex elements
				if (msgComplexElements != null && ecComplexElements != null) {
					if (msgComplexElements.contains(ecComplexElements.get(0)) || (ecComplexElements.size() > 1
							&& msgComplexElements.contains(ecComplexElements.get(1)))) {
						elementConditionsTableModel.add(ec);
					}
				} else if (ecComplexElements == null) {
					// Simple elements
					SimpleElement ecSimpleElement = ec.getSimpleElement();
					if (msgComplexElements != null && msgComplexElements.size() > 0 && ecSimpleElement != null) {
						for (ComplexElementTypeType ce : msgComplexElements) {
							SimpleElements msgSimpleElements = ce.getSimpleElements();
							if (msgSimpleElements != null) {
								Set<SimpleElementTypeType> seElementSet = new HashSet<SimpleElementTypeType>();
								List<Object> seObjects = msgSimpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
								SimpleElementTypeType simpleElementType = null;
								for (Object seObject : seObjects) {
									if (seObject instanceof SimpleElementTypeType) {
										simpleElementType = (SimpleElementTypeType) seObject;
									} else {
										simpleElementType = (SimpleElementTypeType) ((SimpleElementTypeTypeRef) seObject)
												.getIdref();
									}
									seElementSet.add(simpleElementType);
								}
								SimpleElementTypeType ecSimpleElementType = ecSimpleElement.getSimpleElementType();
								if (ecSimpleElementType == null) {
									ecSimpleElementType = (SimpleElementTypeType) ecSimpleElement
											.getSimpleElementTypeRef().getIdref();
								}
								if (seElementSet.contains(ecSimpleElementType)) {
									elementConditionsTableModel.add(ec);
								}
							}
						}
					}
				}
			}
		}
	}

	public void newElementCondition() {
		try {
			String newId = Editor16.getStore16().getNewId("ElementCondition_");
			ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
			Editor16.getStore16().put(newId, newElementConditionType);
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
		Editor16.getStore16().remove(elementConditionType.getId());
		elementConditionsTableModel.remove(row);
	}

	private void setElementConditionTypeMessageInTransaction(ElementConditionType elementConditionType,
			MessageInTransactionTypeType mitt) {
		ElementConditionType.MessageInTransaction messageInTransaction = objectFactory
				.createElementConditionTypeMessageInTransaction();
		MessageInTransactionTypeTypeRef messageInTransactionTypeTypeRef = objectFactory
				.createMessageInTransactionTypeTypeRef();
		messageInTransactionTypeTypeRef.setIdref(mitt);
		messageInTransaction.setMessageInTransactionTypeRef(messageInTransactionTypeTypeRef);
		elementConditionType.setMessageInTransaction(messageInTransaction);
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
