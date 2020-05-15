package nl.visi.interaction_framework.editor.v14;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.schemas._20140331.SimpleElementTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType.UserDefinedType;
import nl.visi.schemas._20140331.UserDefinedTypeType;

public class UserDefinedTypesPanelControl14 extends PanelControl14<UserDefinedTypeType> {
	private static final String USER_DEFINED_TYPES_PANEL = "nl/visi/interaction_framework/editor/swixml/UserDefinedTypesPanel16.xml";

	private JTextField tfd_XsdRestriction;
	private JComboBox<String> cbx_BaseType;
	private JTable tbl_XsdEnumerations;
	private XsdEnumerationsTableModel xsdEnumerationsTableModel;
	private JTextField tfd_ItemText;
	private JButton btn_ItemAdd, btn_ItemRemove, btn_Paste, btn_Alpha, btn_ItemUp, btn_ItemDown;
	private JPopupMenu popupMenu;
	private JMenuItem alphaMenuItem, addMenuItem, removeMenuItem, pasteMenuItem, moveUpMenuItem, moveDownMenuItem;

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
				return getDateTime(userDefinedType.getDateLaMu());
			case UserLamu:
				return userDefinedType.getUserLaMu();
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
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

		public void removeItem(int index) {
			items.remove(index);
		}

	}

	public UserDefinedTypesPanelControl14() throws Exception {
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
		ActionMap map = tbl_XsdEnumerations.getActionMap();
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
		tbl_XsdEnumerations.add(popupMenu);
		tbl_XsdEnumerations.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				boolean elementSelected = selectedElement != null;
				alphaMenuItem.setEnabled(elementSelected);
				pasteMenuItem.setEnabled(elementSelected);
				if (e.isPopupTrigger()) {
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		tbl_XsdEnumerations.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				updateEnumerationButtons();
			}
		});
		tfd_ItemText.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				boolean notEmpty = tfd_ItemText.getText().length() > 0;
				btn_ItemAdd.setEnabled(notEmpty);
				addMenuItem.setEnabled(notEmpty);
			}
		});
	}

	public void pasteAction() {
		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = c.getContents(this);
		try {
			String content = (String) t.getTransferData(DataFlavor.stringFlavor);
			String[] items = content.split("\n");
			for (String item : items) {
				item = "<xs:enumeration value=\"" + item + "\"/>";
				int selectedRow = tbl_XsdEnumerations.getSelectedRow();
				if (selectedRow == -1) {
					selectedRow = tbl_XsdEnumerations.getRowCount() - 1;
				}
				boolean success = insertEnumerationElement(selectedRow + 1, item);
				if (success) {
					initXsdRestriction();
					xsdEnumerationsTableModel.clear();
					fillEnumerationTable();
					tbl_XsdEnumerations.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
					tbl_XsdEnumerations.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow + 1, 0, true));
					tfd_ItemText.setText(null);
				}
			}
		} catch (UnsupportedFlavorException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void alphabetizeAction() {
		String restrictionString = tfd_XsdRestriction.getText();
		List<String> items = new ArrayList<>();
		int index = -1;
		int posStart = 0;
		try {
			while (posStart >= 0) {
				posStart = findBeginIndexEnumerationElement(restrictionString, ++index);
				if (posStart >= 0) {
					int posEnd = restrictionString.indexOf("/>", posStart);
					int startLabel = restrictionString.indexOf("\"", posStart);
					String item = restrictionString.substring(startLabel + 1, posEnd - 1);
					items.add(item);
				}
			}
			Collections.sort(items);

			StringBuffer buffer = new StringBuffer();
			for (String item : items) {
				buffer.append("<xs:enumeration value=\"" + item + "\"/>");
			}
			tfd_XsdRestriction.setText(buffer.toString());
			xsdEnumerationsTableModel.clear();
			fillEnumerationTable();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	private void updateEnumerationButtons() {
		int selectedRow = tbl_XsdEnumerations.getSelectedRow();
		boolean rowSelected = selectedRow >= 0;
		if (rowSelected) {
			btn_ItemRemove.setEnabled(rowSelected);
			removeMenuItem.setEnabled(rowSelected);
			boolean topSelected = selectedRow == 0;
			btn_ItemUp.setEnabled(!topSelected);
			moveUpMenuItem.setEnabled(!topSelected);
			boolean bottomSelected = selectedRow == xsdEnumerationsTableModel.getRowCount() - 1;
			btn_ItemDown.setEnabled(!bottomSelected);
			moveDownMenuItem.setEnabled(!bottomSelected);
		} else {
			btn_ItemRemove.setEnabled(false);
			removeMenuItem.setEnabled(false);
			btn_ItemUp.setEnabled(false);
			moveUpMenuItem.setEnabled(false);
			btn_ItemDown.setEnabled(false);
			moveDownMenuItem.setEnabled(false);
		}
	}


	public void itemAdd() throws BadLocationException {
		String newItem = "<xs:enumeration value=\"" + tfd_ItemText.getText() + "\"/>";
		int selectedRow = tbl_XsdEnumerations.getSelectedRow();
		boolean success = insertEnumerationElement(selectedRow + 1, newItem);
		if (success) {
			initXsdRestriction();
			xsdEnumerationsTableModel.clear();
			fillEnumerationTable();
			tbl_XsdEnumerations.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
			tbl_XsdEnumerations.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow + 1, 0, true));
			tfd_ItemText.setText(null);
		}
	}

	public void itemDown() throws BadLocationException {
		int selectedRow = tbl_XsdEnumerations.getSelectedRow();
		String removedItem = removeEnumerationElement(selectedRow);
		if (removedItem != null) {
			boolean success = insertEnumerationElement(selectedRow + 1, removedItem);
			if (success) {
				initXsdRestriction();
				xsdEnumerationsTableModel.clear();
				fillEnumerationTable();
				tbl_XsdEnumerations.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
				tbl_XsdEnumerations.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow + 1, 0, true));
			}
		}
	}

	public void itemUp() throws BadLocationException {
		int selectedRow = tbl_XsdEnumerations.getSelectedRow();
		String removedItem = removeEnumerationElement(selectedRow);
		if (removedItem != null) {
			boolean success = insertEnumerationElement(selectedRow - 1, removedItem);
			if (success) {
				initXsdRestriction();
				xsdEnumerationsTableModel.clear();
				fillEnumerationTable();
				tbl_XsdEnumerations.setRowSelectionInterval(selectedRow, selectedRow - 1);
				tbl_XsdEnumerations.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow - 1, 0, true));
			}
		}
	}

	public void itemRemove() throws BadLocationException {
		int selectedRow = tbl_XsdEnumerations.getSelectedRow();
		String removedItem = removeEnumerationElement(selectedRow);
		if (removedItem != null) {
			initXsdRestriction();
			xsdEnumerationsTableModel.removeItem(selectedRow);
			xsdEnumerationsTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
		}
	}

	private String removeEnumerationElement(int removeIndex) throws BadLocationException {
		Document document = tfd_XsdRestriction.getDocument();
		String text = document.getText(0, document.getLength());
		int beginIndex = findBeginIndexEnumerationElement(text, removeIndex);
		if (beginIndex == -1)
			return null;
		int endIndex = text.indexOf("\"/>", beginIndex);
		if (endIndex == -1)
			return null;
		String removedItem = document.getText(beginIndex, endIndex + 3 - beginIndex);
		document.remove(beginIndex, endIndex + 3 - beginIndex);
		tfd_XsdRestriction.setDocument(document);
		return removedItem;
	}

	private boolean insertEnumerationElement(int insertIndex, String insertItem) throws BadLocationException {
		Document document = tfd_XsdRestriction.getDocument();
		String text = document.getText(0, document.getLength());
		int beginIndex = findBeginIndexEnumerationElement(text, insertIndex);
		if (beginIndex == -1) {
			document.insertString(document.getLength(), insertItem, null);
		} else {
			document.insertString(beginIndex, insertItem, null);
		}
		return true;

	}

	private int findBeginIndexEnumerationElement(String text, int findIndex) throws BadLocationException {
		int beginIndex = 0;
		int endIndex = 0;
		for (int itemIndex = 0; itemIndex <= findIndex; itemIndex++) {
			beginIndex = text.indexOf("<xs:enumeration", endIndex);
			if (beginIndex < 0)
				return -1;
			endIndex = text.indexOf("\"/>", beginIndex);
			if (endIndex < 0)
				return -1;
			if (itemIndex < findIndex) {
				beginIndex = endIndex;
			}
		}
		return beginIndex;
	}
	
	private void initXsdRestriction() {
		tfd_XsdRestriction.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				if (inSelection)
					return;
				selectedElement.setXsdRestriction(tfd_XsdRestriction.getText());
				updateLaMu(selectedElement, user);
				elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
			}
		});
	}

	private void initBaseType() {
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		String[] baseTypes = new String[] { null, "ANYURI", "BOOLEAN", "DATE", "DATETIME", "DECIMAL", "INTEGER",
				"STRING", "TIME" };
		for (String baseType : baseTypes) {
			model.addElement(baseType);
		}
		cbx_BaseType.setModel(model);
		cbx_BaseType.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cbx = (JComboBox<String>) e.getSource();
				if (selectedElement != null) {
					selectedElement.setBaseType((String) cbx.getSelectedItem());
				}
			}
		});

	}

	private void initElementsTable() {
		elementsTableModel = new UserDefinedTypesTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setAutoCreateRowSorter(true);
		TableRowSorter<ElementsTableModel<UserDefinedTypeType>> tableRowSorter = new TableRowSorter<>(
				elementsTableModel);
		tableRowSorter.setComparator(UserDefinedTypesTableColumns.DateLamu.ordinal(), dateTimeComparator);
		tbl_Elements.setRowSorter(tableRowSorter);
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
		
		updateEnumerationButtons();
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
		cbx_BaseType.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_XsdRestriction.setEnabled(rowSelected);
		tbl_XsdEnumerations.setEnabled(rowSelected);
		tfd_ItemText.setEnabled(rowSelected);
		btn_Paste.setEnabled(rowSelected);
		btn_Alpha.setEnabled(rowSelected);
		if (rowSelected) {
			selectedElement = elementsTableModel.get(selectedRow);
			tfd_Id.setText(selectedElement.getId());
			tfd_Description.setText(selectedElement.getDescription());
			cbx_BaseType.setSelectedItem(selectedElement.getBaseType());
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
			cbx_BaseType.setSelectedItem(null);
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
			newUserDefinedType.setBaseType("STRING");
			int row = elementsTableModel.add(newUserDefinedType);
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
		UserDefinedTypeType userDefinedType = elementsTableModel.get(row);

		try {
			UserDefinedTypeType copyUserDefinedType = objectFactory.createUserDefinedTypeType();
			newElement(copyUserDefinedType, "UserDefinedType_");
			store.generateCopyId(copyUserDefinedType, userDefinedType);
			copyUserDefinedType.setBaseType(userDefinedType.getBaseType());
			copyUserDefinedType.setDescription(userDefinedType.getDescription());
			copyUserDefinedType.setHelpInfo(userDefinedType.getHelpInfo());
			copyUserDefinedType.setLanguage(userDefinedType.getLanguage());
			copyUserDefinedType.setState(userDefinedType.getState());
			copyUserDefinedType.setXsdRestriction(userDefinedType.getXsdRestriction());
			store.put(copyUserDefinedType.getId(), copyUserDefinedType);
			int copyrow = elementsTableModel.add(copyUserDefinedType);
			copyrow = tbl_Elements.convertRowIndexToView(copyrow);
			tbl_Elements.getSelectionModel().setSelectionInterval(copyrow, copyrow);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		Store14 store = Editor14.getStore14();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
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

		Editor14.getStore14().remove(userDefinedType.getId());
		elementsTableModel.remove(row);
	}
}
