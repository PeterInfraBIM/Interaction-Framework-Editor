package nl.visi.interaction_framework.editor.v16;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.OrganisationTypeType;
import nl.visi.schemas._20160331.PersonTypeType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType.UserDefinedType;
import nl.visi.schemas._20160331.UserDefinedTypeType;
import nl.visi.schemas._20160331.UserDefinedTypeTypeRef;

public class SimpleElementsPanelControl16 extends PanelControl16<SimpleElementTypeType> {
	private static final String SIMPLE_ELEMENTS_PANEL = "nl/visi/interaction_framework/editor/swixml/SimpleElementsPanel.xml";

	protected static final Class<Object> UserDefinedTypeType = null;

	private JTextField tfd_InterfaceType, tfd_ValueList;
	private JComboBox<String> cbx_GlobalElementCondition, cbx_UserDefinedType;
	private JButton btn_NavigateUserDefinedType;
	private JTable tbl_UseElements;
	private UseElementsTableModel useElementsTableModel;

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

	
	private enum SimpleElementsTableColumns {
//		Id, Description, InterfaceType, State, DateLamu, UserLamu;
		Id, Description;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class SimpleElementsTableModel extends ElementsTableModel<SimpleElementTypeType> {

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
//			case InterfaceType:
//				return simpleElement.getInterfaceType();
//			case State:
//				return simpleElement.getState();
//			case DateLamu:
//				return getDateTime(simpleElement.getDateLaMu());
//			case UserLamu:
//				return simpleElement.getUserLaMu();
			default:
				return null;
			}
		}
	}

	@SuppressWarnings("serial")
	public SimpleElementsPanelControl16() throws Exception {
		super(SIMPLE_ELEMENTS_PANEL);
		elementsTableModel = new SimpleElementsTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setTransferHandler(Simple2ComplexTransferHandler.getInstance());
		tbl_Elements.setAutoCreateRowSorter(true);
		tbl_Elements.setFillsViewportHeight(true);
		TableRowSorter<ElementsTableModel<SimpleElementTypeType>> tableRowSorter = new TableRowSorter<>(
				elementsTableModel);
//		tableRowSorter.setComparator(SimpleElementsTableColumns.DateLamu.ordinal(), dateTimeComparator);
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
//				String filterString = tfd_Filter.getText().toUpperCase();
//				if (filterString.isEmpty()) {
//					fillTable(SimpleElementTypeType.class);
//				} else {
//					List<SimpleElementTypeType> elements = Editor16.getStore16()
//							.getElements(SimpleElementTypeType.class);
//					elementsTableModel.clear();
//					for (SimpleElementTypeType element : elements) {
//						if (element.getDescription().toUpperCase().contains(filterString)
//								|| element.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(element);
//						}
//					}
//				}
				fillTable();
			}
		});
		
		tfd_InterfaceType.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setInterfaceType(tfd_InterfaceType.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});

		tfd_ValueList.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setValueList(tfd_ValueList.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});

		for (String conditionValue : CONDITION_VALUES) {
			cbx_GlobalElementCondition.addItem(conditionValue);
		}
		cbx_GlobalElementCondition.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inSelection)
					return;
				String condition = (String) cbx_GlobalElementCondition.getSelectedItem();
				ElementConditionType elementConditionType = getElementConditionType(null, null, null, selectedElement);
				if (elementConditionType != null) {
					if (condition != null) {
						elementConditionType.setCondition(condition);
					} else {
						Editor16.getStore16().remove(elementConditionType);
					}
					updateLaMu(selectedElement, user);
				} else {
					if (condition != null) {
						String newId = Editor16.getStore16().getNewId("ec_");
						ElementConditionType newElementConditionType = objectFactory.createElementConditionType();
						Editor16.getStore16().put(newId, newElementConditionType);
						newElementConditionType.setId(newId);
						newElementConditionType.setDescription("Decription of " + newId);
						newElementConditionType.setCondition(condition);
						setElementConditionTypeSimpleElement(newElementConditionType, selectedElement);
						updateLaMu(selectedElement, user);
					}
				}
			}
		});
		
		
		cbx_UserDefinedType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (inSelection)
					return;
				String idref = (String) cbx_UserDefinedType.getSelectedItem();
				if (idref != null) {
					UserDefinedTypeType definedType = Editor16.getStore16().getElement(UserDefinedTypeType.class,
							idref);
					SimpleElementTypeType.UserDefinedType value = objectFactory
							.createSimpleElementTypeTypeUserDefinedType();
					UserDefinedTypeTypeRef userDefinedTypeTypeRef = objectFactory.createUserDefinedTypeTypeRef();
					userDefinedTypeTypeRef.setIdref(definedType);
					value.setUserDefinedTypeRef(userDefinedTypeTypeRef);
					selectedElement.setUserDefinedType(value);
					updateLaMu(selectedElement, user);
					elementsTableModel.update(selectedRow);
				} else {
					if (selectedElement != null) {
						UserDefinedType userDefinedType = selectedElement.getUserDefinedType();
						if (userDefinedType != null) {
							selectedElement.setUserDefinedType(null);
						}
					}
				}
			}
		});
		
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

	@Override
	public void fillTable() {
		String filterString = tfd_Filter.getText().toUpperCase();
		if (filterString.isEmpty()) {
			fillTable(SimpleElementTypeType.class);
		} else {
			List<SimpleElementTypeType> elements = Editor16.getStore16()
					.getElements(SimpleElementTypeType.class);
			elementsTableModel.clear();
			for (SimpleElementTypeType element : elements) {
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
		tfd_InterfaceType.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_DateLamu.setEnabled(rowSelected);
		tfd_UserLamu.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_ValueList.setEnabled(rowSelected);
		cbx_GlobalElementCondition.setEnabled(rowSelected);
		cbx_UserDefinedType.setEnabled(rowSelected);
		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			tfd_InterfaceType.setText(selectedElement.getInterfaceType());
			tfd_State.setText(selectedElement.getState());
			tfd_DateLamu.setText(selectedElement.getDateLaMu() != null
					? sdfDateTime.format(selectedElement.getDateLaMu().toGregorianCalendar().getTime())
					: "");
			tfd_UserLamu.setText(selectedElement.getUserLaMu());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_ValueList.setText(selectedElement.getValueList());

			ElementConditionType ec = getElementConditionType(null, null, null, selectedElement);
			cbx_GlobalElementCondition.setSelectedItem(ec != null ? ec.getCondition() : null);
			cbx_UserDefinedType.removeAllItems();
			cbx_UserDefinedType.addItem(null);
			List<UserDefinedTypeType> elements = Editor16.getStore16().getElements(UserDefinedTypeType.class);
			for (UserDefinedTypeType element : elements) {
				cbx_UserDefinedType.addItem(element.getId());
			}
			UserDefinedType userDefinedType = selectedElement.getUserDefinedType();
			btn_NavigateUserDefinedType.setEnabled(userDefinedType != null);
			if (userDefinedType != null) {
				UserDefinedTypeType userDefined = userDefinedType.getUserDefinedType();
				if (userDefined == null) {
					userDefined = (UserDefinedTypeType) userDefinedType.getUserDefinedTypeRef().getIdref();
				}
				cbx_UserDefinedType.setSelectedItem(userDefined.getId());
			}
			useElementsTableModel.clear();
			List<ElementType> useElements = getUseElements(selectedElement);
			if (useElements != null) {
				for (ElementType elementType : useElements) {
					useElementsTableModel.add(elementType);
				}
			}
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			tfd_InterfaceType.setText("");
			tfd_State.setText("");
			tfd_DateLamu.setText("");
			tfd_UserLamu.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			tfd_ValueList.setText("");
			cbx_UserDefinedType.removeAllItems();
			btn_NavigateUserDefinedType.setEnabled(false);
			useElementsTableModel.clear();
		}
		inSelection = false;
	}

	public void newElement() {
		try {
			SimpleElementTypeType newSimpleElementType = objectFactory.createSimpleElementTypeType();
			newElement(newSimpleElementType, "SimpleElement_");
			newSimpleElementType.setInterfaceType("");

			int row = elementsTableModel.add(newSimpleElementType);
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
		SimpleElementTypeType simpleElementType = elementsTableModel.get(row);

		try {
			SimpleElementTypeType copySimpleElementType = objectFactory.createSimpleElementTypeType();
			newElement(copySimpleElementType, "SimpleElement_");
			store.generateCopyId(copySimpleElementType, simpleElementType);
			copySimpleElementType.setCategory(simpleElementType.getCategory());
			copySimpleElementType.setDescription(simpleElementType.getDescription());
			copySimpleElementType.setHelpInfo(simpleElementType.getHelpInfo());
			copySimpleElementType.setInterfaceType(simpleElementType.getInterfaceType());
			copySimpleElementType.setLanguage(simpleElementType.getLanguage());
			copySimpleElementType.setState(simpleElementType.getState());
			copySimpleElementType.setUserDefinedType(simpleElementType.getUserDefinedType());
			copySimpleElementType.setValueList(simpleElementType.getValueList());
			store.put(copySimpleElementType.getId(), copySimpleElementType);
			int copyrow = elementsTableModel.add(copySimpleElementType);
			copyrow = tbl_Elements.convertRowIndexToView(copyrow);
			tbl_Elements.getSelectionModel().setSelectionInterval(copyrow, copyrow);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		SimpleElementTypeType simpleElementType = elementsTableModel.get(row);

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + simpleElementType.getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;
		
		List<ComplexElementTypeType> elements = store.getElements(ComplexElementTypeType.class);
		for (ComplexElementTypeType ceType : elements) {
			ComplexElementTypeType.SimpleElements simpleElements = ceType.getSimpleElements();
			if (simpleElements != null) {
				List<Object> list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
				for (Object object : list) {
					SimpleElementTypeType seType = (SimpleElementTypeType) getElementType(object);
					if (seType.equals(simpleElementType)) {
						list.remove(object);
						break;
					}
				}
			}
		}

		Editor16.getStore16().remove(simpleElementType.getId());
		elementsTableModel.remove(row);
	}

	public void navigateUserDefinedType() {
		String idref = (String) cbx_UserDefinedType.getSelectedItem();
		UserDefinedTypeType element = Editor16.getStore16().getElement(UserDefinedTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}
}
