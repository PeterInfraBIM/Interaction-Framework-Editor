package nl.visi.interaction_framework.editor.v12;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.visi.interaction_schema.ComplexElementTypeType;
import nl.visi.interaction_schema.SimpleElementTypeType;
import nl.visi.interaction_schema.SimpleElementTypeType.UserDefinedType;
import nl.visi.interaction_schema.UserDefinedTypeType;
import nl.visi.interaction_schema.UserDefinedTypeTypeRef;

public class SimpleElementsPanelControl12 extends PanelControl12<SimpleElementTypeType> {
	private static final String SIMPLE_ELEMENTS_PANEL = "nl/visi/interaction_framework/editor/swixml/SimpleElementsPanel.xml";

	private JTextField tfd_InterfaceType, tfd_ValueList;
	private JComboBox<String> cbx_UserDefinedType;
	private JButton btn_NavigateUserDefinedType;

	private enum SimpleElementsTableColumns {
		Id, Description, InterfaceType, State, DateLamu, UserLamu;

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
			case InterfaceType:
				return simpleElement.getInterfaceType();
			case State:
				return simpleElement.getState();
			case DateLamu:
				return getDateTime(simpleElement.getDateLamu());
			case UserLamu:
				return simpleElement.getUserLamu();
			default:
				return null;
			}
		}
	}

	public SimpleElementsPanelControl12() throws Exception {
		super(SIMPLE_ELEMENTS_PANEL);
		elementsTableModel = new SimpleElementsTableModel();
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

		tfd_InterfaceType.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setInterfaceType(tfd_InterfaceType.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});

		tfd_ValueList.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setValueList(tfd_ValueList.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	@Override
	public void fillTable() {
		fillTable(SimpleElementTypeType.class);
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;
		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		boolean rowSelected = selectedRow >= 0;
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		tfd_InterfaceType.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_ValueList.setEnabled(rowSelected);
		cbx_UserDefinedType.setEnabled(rowSelected);
		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			tfd_InterfaceType.setText(selectedElement.getInterfaceType());
			tfd_State.setText(selectedElement.getState());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_ValueList.setText(selectedElement.getValueList());

			cbx_UserDefinedType.removeAllItems();
			cbx_UserDefinedType.addItem(null);
			List<UserDefinedTypeType> elements = Editor12.getStore12().getElements(UserDefinedTypeType.class);
			for (UserDefinedTypeType element : elements) {
				cbx_UserDefinedType.addItem(element.getId());
			}
			UserDefinedType userDefinedType = selectedElement.getUserDefinedType();
			btn_NavigateUserDefinedType.setEnabled(userDefinedType != null);
			if (userDefinedType != null) {
				UserDefinedTypeType userDefined = userDefinedType.getUserDefinedType();
				if (userDefined == null) {
					userDefined = (UserDefinedTypeType) userDefinedType.getUserDefinedTypeRef().getIdref();
					cbx_UserDefinedType.setSelectedItem(userDefined);
				}
				cbx_UserDefinedType.setSelectedItem(userDefined.getId());
			}
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			tfd_InterfaceType.setText("");
			tfd_State.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			tfd_ValueList.setText("");
			cbx_UserDefinedType.removeAllItems();
			btn_NavigateUserDefinedType.setEnabled(false);
		}
		inSelection = false;
	}

	public void newElement() {
		try {
			SimpleElementTypeType newSimpleElementType = objectFactory.createSimpleElementTypeType();
			newElement(newSimpleElementType, "SimpleElement_");
			newSimpleElementType.setInterfaceType("");

			int row = elementsTableModel.add(newSimpleElementType);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store12 store = Editor12.getStore12();
		int row = tbl_Elements.getSelectedRow();
		SimpleElementTypeType simpleElementType = elementsTableModel.get(row);

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

		Editor12.getStore12().remove(simpleElementType.getId());
		elementsTableModel.remove(row);
	}

	public void setUserDefinedType() {
		String idref = (String) cbx_UserDefinedType.getSelectedItem();
		if (idref != null) {
			UserDefinedTypeType definedType = Editor12.getStore12().getElement(UserDefinedTypeType.class, idref);
			SimpleElementTypeType.UserDefinedType value = objectFactory.createSimpleElementTypeTypeUserDefinedType();
			UserDefinedTypeTypeRef userDefinedTypeTypeRef = objectFactory.createUserDefinedTypeTypeRef();
			userDefinedTypeTypeRef.setIdref(definedType);
			value.setUserDefinedTypeRef(userDefinedTypeTypeRef);
			selectedElement.setUserDefinedType(value);
			updateLamu(selectedElement, user);
			elementsTableModel.update(selectedRow);
		}
	}

	public void navigateUserDefinedType() {
		String idref = (String) cbx_UserDefinedType.getSelectedItem();
		UserDefinedTypeType element = Editor12.getStore12().getElement(UserDefinedTypeType.class, idref);
		Editor12.getMainFrameControl().navigate(element);
	}
}
