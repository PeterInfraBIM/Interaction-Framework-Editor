package nl.visi.interaction_framework.editor.v12;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import nl.visi.interaction_schema.SimpleElementTypeType;
import nl.visi.interaction_schema.SimpleElementTypeType.UserDefinedType;
import nl.visi.interaction_schema.UserDefinedTypeType;

public class UserDefinedTypesPanelControl12 extends PanelControl12<UserDefinedTypeType> {
	private static final String USER_DEFINED_TYPES_PANEL = "nl/visi/interaction_framework/editor/swixml/UserDefinedTypesPanel12.xml";

	private JTextField tfd_BaseType, tfd_XsdRestriction;
	private JTable tbl_XsdEnumerations;
	private XsdEnumerationsTableModel xsdEnumerationsTableModel;

	private enum UserDefinedTypesTableColumns {
		Id, Description, State, BaseType, DateLamu, UserLamu;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class UserDefinedTypesTableModel extends ElementsTableModel<UserDefinedTypeType> {

		@Override
		public int getColumnCount() {
			return UserDefinedTypesTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return UserDefinedTypesTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			UserDefinedTypeType userDefinedType = get(rowIndex);
			switch (UserDefinedTypesTableColumns.values()[columnIndex]) {
			case Id:
				return userDefinedType.getId();
			case Description:
				return userDefinedType.getDescription();
			case State:
				return userDefinedType.getState();
			case BaseType:
				return userDefinedType.getBaseType();
			case DateLamu:
				return getDateTime(userDefinedType.getDateLamu());
			case UserLamu:
				return userDefinedType.getUserLamu();
			default:
				return null;
			}
		}
	}

	private enum XsdEnumerationsTableColumns {
		Item;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class XsdEnumerationsTableModel extends AbstractTableModel {
		List<String> items;

		public XsdEnumerationsTableModel() {
			items = new ArrayList<String>();
		}

		@Override
		public int getColumnCount() {
			return XsdEnumerationsTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return XsdEnumerationsTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			String item = items.get(rowIndex);
			switch (XsdEnumerationsTableColumns.values()[columnIndex]) {
			case Item:
				return item;
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == XsdEnumerationsTableColumns.Item.ordinal())
				return String.class;
			else
				return Object.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == XsdEnumerationsTableColumns.Item.ordinal()) ? true : false;
		}

		@Override
		public int getRowCount() {
			return items.size();
		}

		public void clear() {
			items.clear();
			fireTableDataChanged();
		}

		public void addItem(int index, String item) {
			items.add(0, item);
		}

	}

	public UserDefinedTypesPanelControl12() throws Exception {
		super(USER_DEFINED_TYPES_PANEL);
		initElementsTable();
		initBaseType();
		initXsdRestriction();
		initXsdEnumerationTable();
	}

	private void initXsdEnumerationTable() {
		xsdEnumerationsTableModel = new XsdEnumerationsTableModel();
		tbl_XsdEnumerations.setModel(xsdEnumerationsTableModel);
		tbl_XsdEnumerations.setFillsViewportHeight(true);
	}

	private void initXsdRestriction() {
		tfd_XsdRestriction.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setXsdRestriction(tfd_XsdRestriction.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initBaseType() {
		tfd_BaseType.getDocument().addDocumentListener(new DocumentAdapter12() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setBaseType(tfd_BaseType.getText());
				updateLamu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initElementsTable() {
		elementsTableModel = new UserDefinedTypesTableModel();
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

	@Override
	public void fillTable() {
		fillTable(UserDefinedTypeType.class);
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;
		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		boolean rowSelected = selectedRow >= 0;
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		tfd_BaseType.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_XsdRestriction.setEnabled(rowSelected);
		tbl_XsdEnumerations.setEnabled(rowSelected);
		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			tfd_BaseType.setText(selectedElement.getBaseType());
			tfd_State.setText(selectedElement.getState());
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());
			tfd_XsdRestriction.setText(selectedElement.getXsdRestriction());
			xsdEnumerationsTableModel.clear();
			fillEnumerationTable();
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			tfd_BaseType.setText("");
			tfd_State.setText("");
			tfd_Language.setText("");
			tfd_HelpInfo.setText("");
			tfd_XsdRestriction.setText("");
			xsdEnumerationsTableModel.clear();
		}
		inSelection = false;
	}

	private void fillEnumerationTable() {
		String xsdRestriction = selectedElement.getXsdRestriction();
		if (xsdRestriction != null && xsdRestriction.length() > 0) {
			int lastIndexOf = xsdRestriction.lastIndexOf("<xs:enumeration");
			while (lastIndexOf >= 0) {
				String item = xsdRestriction.substring(lastIndexOf);
				xsdRestriction = xsdRestriction.substring(0, lastIndexOf);
				item = item.substring(item.indexOf('"') + 1, item.lastIndexOf('"'));
				xsdEnumerationsTableModel.addItem(0, item);
				lastIndexOf = xsdRestriction.lastIndexOf("<xs:enumeration");
			}
		}
	}

	public void newElement() {
		try {
			UserDefinedTypeType newUserDefinedType = objectFactory.createUserDefinedTypeType();
			newElement(newUserDefinedType, "UserDefinedType_");

			int row = elementsTableModel.add(newUserDefinedType);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store12 store = Editor12.getStore12();
		int row = tbl_Elements.getSelectedRow();
		UserDefinedTypeType userDefinedType = elementsTableModel.get(row);

		List<SimpleElementTypeType> elements = store.getElements(SimpleElementTypeType.class);
		for (SimpleElementTypeType element : elements) {
			UserDefinedType dataType = element.getUserDefinedType();
			if (dataType != null) {
				UserDefinedTypeType dataTypeType = dataType.getUserDefinedType();
				if (dataTypeType == null) {
					dataTypeType = (UserDefinedTypeType) dataType.getUserDefinedTypeRef().getIdref();
				}
				if (dataTypeType != null && dataTypeType.equals(userDefinedType)) {
					element.setUserDefinedType(null);
				}
			}
		}

		Editor12.getStore12().remove(userDefinedType.getId());
		elementsTableModel.remove(row);
	}
}
