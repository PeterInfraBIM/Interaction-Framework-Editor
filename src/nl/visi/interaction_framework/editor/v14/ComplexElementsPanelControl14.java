package nl.visi.interaction_framework.editor.v14;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.DateField;
import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.schemas._20140331.AppendixTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.ComplexElementTypeType.ComplexElements;
import nl.visi.schemas._20140331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20140331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20140331.ElementConditionType;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.OrganisationTypeType;
import nl.visi.schemas._20140331.PersonTypeType;
import nl.visi.schemas._20140331.ProjectTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20140331.UserDefinedTypeType;

public class ComplexElementsPanelControl14 extends PanelControl14<ComplexElementTypeType> {
	private static final String COMPLEX_ELEMENTS_PANEL = "nl/visi/interaction_framework/editor/swixml/ComplexElementsPanel14.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_SubComplexElements, tbl_SimpleElements, tbl_UseElements;
	private SubComplexElementsTableModel subComplexElementsTableModel;
	private SimpleElementsTableModel simpleElementsTableModel;
	private UseElementsTableModel useElementsTableModel;
	private JComboBox<String> cbx_GlobalElementCondition, cbx_Conditions, cbx_ComplexElements, cbx_SimpleElements;
	private JButton btn_AddComplexElement, btn_RemoveComplexElement, btn_AddSimpleElement, btn_RemoveSimpleElement;

	private enum ComplexElementsTableColumns {
//		Id, Description, StartDate, EndDate, State, DateLamu, UserLamu;
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

	SubComplexElementsTableModel getSubComplexElementsTableModel() {
		return subComplexElementsTableModel;
	}

	private enum SubComplexElementsTableColumns {
		Id, Description, Navigate;

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
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == SubComplexElementsTableColumns.Navigate.ordinal();
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
					return userDefinedType.getId();
				}
				break;
			case Condition:
				ElementConditionType ec = getElementConditionType(null, selectedElement, simpleElement);
				return ec != null ? ec.getCondition() : "";
			default:
				break;
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
				ElementConditionType ec = getElementConditionType(null, selectedElement, simpleElement);
				if (ec != null) {
					if (aValue != null) {
						ec.setCondition((String) aValue);
					} else {
						Editor14.getStore14().remove(ec);
					}
				} else {
					if (aValue != null) {
						String newId = Editor14.getStore14().getNewId("ec_");
						ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
						Editor14.getStore14().put(newId, newElementConditionType);
						newElementConditionType.setId(newId);
						newElementConditionType.setDescription("Decription of " + newId);
						newElementConditionType.setCondition((String) aValue);
						setElementConditionTypeComplexElement(newElementConditionType, selectedElement);
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
	
	public ComplexElementsPanelControl14() throws Exception {
		super(COMPLEX_ELEMENTS_PANEL);

		initComplexElementsTable();
		initSubComplexElementsTable();
		initSimpleElementsTable();
		initUseElementsTable();
		initStartDateField();
		initEndDateField();
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
				ElementConditionType elementConditionType = getElementConditionType(null, selectedElement, null);
				if (elementConditionType != null) {
					if (condition != null) {
						elementConditionType.setCondition(condition);
					} else {
						Editor14.getStore14().remove(elementConditionType);
					}
					updateLaMu(selectedElement, user);
				} else {
					if (condition != null) {
						String newId = Editor14.getStore14().getNewId("ec_");
						ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
						Editor14.getStore14().put(newId, newElementConditionType);
						newElementConditionType.setId(newId);
						newElementConditionType.setDescription("Decription of " + newId);
						newElementConditionType.setCondition(condition);
						setElementConditionTypeComplexElement(newElementConditionType, selectedElement);
						updateLaMu(selectedElement, user);
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
					cbx_Conditions.setSelectedItem(conditionValue == "" ? null : conditionValue);
				} else {
					btn_RemoveSimpleElement.setEnabled(false);
				}
			}
		});
		cbx_Conditions = new JComboBox<>(new DefaultComboBoxModel<String>());
		for (String conditionValue : CONDITION_VALUES) {
			cbx_Conditions.addItem(conditionValue);
		}
		TableColumn conditionColumn = tbl_SimpleElements.getColumnModel()
				.getColumn(SimpleElementsTableColumns.Condition.ordinal());
		conditionColumn.setCellEditor(new DefaultCellEditor(cbx_Conditions));

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

	@SuppressWarnings("serial")
	private void initSubComplexElementsTable() {
		subComplexElementsTableModel = new SubComplexElementsTableModel();
		subComplexElementsTableModel.setSorted(false);
		tbl_SubComplexElements.setModel(subComplexElementsTableModel);
//		tbl_SubComplexElements.setAutoCreateRowSorter(true);
		tbl_SubComplexElements.setFillsViewportHeight(true);
		tbl_SubComplexElements.setDropMode(DropMode.INSERT_ROWS);
		tbl_SubComplexElements
				.setTransferHandler(getTransferHandler(tbl_SubComplexElements, subComplexElementsTableModel, true));
		tbl_SubComplexElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tbl_SubComplexElements.getSelectedRow();
				btn_RemoveComplexElement.setEnabled(selectedRow >= 0);
			}
		});
		TableColumn navigateColumn = tbl_SubComplexElements.getColumnModel()
				.getColumn(SubComplexElementsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_SubComplexElements.getSelectedRow();
				ComplexElementTypeType complexElementTypeType = subComplexElementsTableModel.get(row);
				if (complexElementTypeType != null) {
					InteractionFrameworkEditor.navigate(complexElementTypeType);
				}
			}
		});
	}

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
			List<ComplexElementTypeType> elements = Editor14.getStore14().getElements(ComplexElementTypeType.class);
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
		cbx_GlobalElementCondition.setEnabled(rowSelected);
		cbx_ComplexElements.setEnabled(rowSelected);
		tbl_SubComplexElements.setEnabled(rowSelected);
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
			ElementConditionType ec = getElementConditionType(null, selectedElement, null);
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
			List<SimpleElementTypeType> seList = Editor14.getStore14().getElements(SimpleElementTypeType.class);
			for (SimpleElementTypeType element : seList) {
				cbx_SimpleElements.addItem("[" + element.getId() + "] " + element.getDescription());
			}

			subComplexElementsTableModel.clear();
			List<ComplexElementTypeType> complexElements = getComplexElements(selectedElement);
			if (complexElements != null) {
				for (ComplexElementTypeType complexElement : complexElements) {
					subComplexElementsTableModel.add(complexElement);
					List<SimpleElementTypeType> subSimpleElements = getSimpleElements(complexElement);
					if (subSimpleElements != null) {
						for (SimpleElementTypeType subSimpleElement : subSimpleElements) {
							simpleElementsTableModel.add(subSimpleElement);
						}
					}
				}
			}
			useElementsTableModel.clear();
			List<ElementType> useElements = getUseElements(selectedElement);
			if (useElements != null) {
				for (ElementType elementType : useElements) {
					useElementsTableModel.add(elementType);
				}
			}
			cbx_ComplexElements.removeAllItems();
			cbx_ComplexElements.addItem(null);
			List<ComplexElementTypeType> ceList = Editor14.getStore14().getElements(ComplexElementTypeType.class);
			for (ComplexElementTypeType element : ceList) {
				cbx_ComplexElements.addItem("[" + element.getId() + "] " + element.getDescription());
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
			cbx_GlobalElementCondition.setSelectedIndex(0);
			cbx_ComplexElements.removeAllItems();
			subComplexElementsTableModel.clear();
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
		Store14 store = Editor14.getStore14();
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
		Store14 store = Editor14.getStore14();
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

		Editor14.getStore14().remove(complexElementType.getId());
		elementsTableModel.remove(row);
	}

	public void selectComplexElement() {
		int selectedIndex = cbx_ComplexElements.getSelectedIndex();
		btn_AddComplexElement.setEnabled(selectedIndex > 0);
	}

	public void addComplexElement() {
		String complexElement = (String) cbx_ComplexElements.getSelectedItem();
		String ceId = complexElement.substring(1, complexElement.indexOf("]"));
		ComplexElementTypeType element = Editor14.getStore14().getElement(ComplexElementTypeType.class, ceId);
		ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
		ref.setIdref(element);
		ComplexElementTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
		if (complexElements == null) {
			complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
			selectedElement.setComplexElements(complexElements);
		}
		List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		list.add(ref);
		subComplexElementsTableModel.add(element);
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

	public void removeComplexElement() {
		int selectedSubComplexElementsRow = tbl_SubComplexElements.getSelectedRow();

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": "
						+ subComplexElementsTableModel.elements.get(selectedSubComplexElementsRow).getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		ComplexElementTypeType complexElement = subComplexElementsTableModel.remove(selectedSubComplexElementsRow);
		ComplexElements complexElements = selectedElement.getComplexElements();
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

	public void selectSimpleElement() {
		int selectedIndex = cbx_SimpleElements.getSelectedIndex();
		btn_AddSimpleElement.setEnabled(selectedIndex > 0);
	}

	public void addSimpleElement() {
		String simpleElement = (String) cbx_SimpleElements.getSelectedItem();
		String seId = simpleElement.substring(1, simpleElement.indexOf("]"));
		SimpleElementTypeType element = Editor14.getStore14().getElement(SimpleElementTypeType.class, seId);
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
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
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
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}
}
