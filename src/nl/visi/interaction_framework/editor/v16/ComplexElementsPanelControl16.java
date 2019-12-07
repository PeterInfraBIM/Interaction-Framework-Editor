package nl.visi.interaction_framework.editor.v16;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.v16.PanelControl16;
import nl.visi.interaction_framework.editor.v16.Store16;
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType.ComplexElements;
import nl.visi.schemas._20160331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.OrganisationTypeType;
import nl.visi.schemas._20160331.PersonTypeType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType.UserDefinedType;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;
import nl.visi.schemas._20160331.UserDefinedTypeType;

public class ComplexElementsPanelControl16 extends PanelControl16<ComplexElementTypeType> {
	private static final String COMPLEX_ELEMENTS_PANEL = "nl/visi/interaction_framework/editor/swixml/ComplexElementsPanel.xml";

	private JPanel startDatePanel, endDatePanel;
	private JTable tbl_SubComplexElements, tbl_SimpleElements;
	private SubComplexElementsTableModel subComplexElementsTableModel;
	private SimpleElementsTableModel simpleElementsTableModel;
	private JComboBox<String> cbx_ComplexElements, cbx_SimpleElements;
	private JButton btn_AddComplexElement, btn_RemoveComplexElement, btn_AddSimpleElement, btn_RemoveSimpleElement;

	private enum ComplexElementsTableColumns {
		Id, Description, StartDate, EndDate, State, DateLamu, UserLamu;

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
			case StartDate:
				return getDate(complexElement.getStartDate());
			case EndDate:
				return getDate(complexElement.getEndDate());
			case State:
				return complexElement.getState();
			case DateLamu:
				return getDateTime(complexElement.getDateLaMu());
			case UserLamu:
				return complexElement.getUserLaMu();
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
		Id, Description, UserDefinedType, Navigate;

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
				UserDefinedType userDefinedType = simpleElement.getUserDefinedType();
				if (userDefinedType != null) {
					UserDefinedTypeType type = userDefinedType.getUserDefinedType();
					if (type == null) {
						type = (UserDefinedTypeType) userDefinedType.getUserDefinedTypeRef().getIdref();
					}
					if (type != null) {
						return type.getId();
					}
				}
				return simpleElement.getInterfaceType();
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == SimpleElementsTableColumns.Navigate.ordinal();
		}

	}

	SimpleElementsTableModel getSimpleElementsTableModel() {
		return simpleElementsTableModel;
	}

	public ComplexElementsPanelControl16() throws Exception {
		super(COMPLEX_ELEMENTS_PANEL);
		initComplexElementsTable();
		initSubComplexElementsTable();
		initSimpleElementsTable();
		initStartDateField();
		initEndDateField();
	}

	private void initEndDateField() {
		endDateField = new DateField16(endDatePanel);
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
		startDateField = new DateField16(startDatePanel);
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

	@SuppressWarnings("serial")
	private void initSimpleElementsTable() {
		simpleElementsTableModel = new SimpleElementsTableModel();
		simpleElementsTableModel.setSorted(false);
		tbl_SimpleElements.setModel(simpleElementsTableModel);
		tbl_SimpleElements.setFillsViewportHeight(true);
		tbl_SimpleElements.setDropMode(DropMode.INSERT_ROWS);
		tbl_SimpleElements.setTransferHandler(getTransferHandler(tbl_SimpleElements, simpleElementsTableModel, false));
		tbl_SimpleElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tbl_SimpleElements.getSelectedRow();
				btn_RemoveSimpleElement.setEnabled(selectedRow >= 0);
			}
		});
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
					Editor16.getMainFrameControl().navigate(simpleElementTypeType);
				}
			}
		});
	}

	@SuppressWarnings("serial")
	private void initSubComplexElementsTable() {
		subComplexElementsTableModel = new SubComplexElementsTableModel();
		subComplexElementsTableModel.setSorted(false);
		tbl_SubComplexElements.setModel(subComplexElementsTableModel);
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
					Editor16.getMainFrameControl().navigate(complexElementTypeType);
				}
			}
		});
	}

	private void initComplexElementsTable() {
		elementsTableModel = new ComplexElementsTableModel();
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
		fillTable(ComplexElementTypeType.class);
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;
		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		boolean rowSelected = selectedRow >= 0;
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		startDateField.setEnabled(rowSelected);
		endDateField.setEnabled(rowSelected);
		tfd_State.setEnabled(rowSelected);
		tfd_Language.setEnabled(rowSelected);
		tfd_Category.setEnabled(rowSelected);
		tfd_HelpInfo.setEnabled(rowSelected);
		tbl_SubComplexElements.setEnabled(rowSelected);
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
			tfd_Language.setText(selectedElement.getLanguage());
			tfd_Category.setText(selectedElement.getCategory());
			tfd_HelpInfo.setText(selectedElement.getHelpInfo());

			subComplexElementsTableModel.clear();
			ComplexElementTypeType.ComplexElements complexElements = selectedElement.getComplexElements();
			if (complexElements != null) {
				List<Object> ceList = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				for (Object object : ceList) {
					ComplexElementTypeType element = null;
					if (object instanceof ComplexElementTypeTypeRef) {
						element = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object).getIdref();
					} else {
						element = (ComplexElementTypeType) object;
					}
					subComplexElementsTableModel.add(element);
				}
			}
			cbx_ComplexElements.removeAllItems();
			cbx_ComplexElements.addItem(null);
			List<ComplexElementTypeType> ceList = Editor16.getStore16().getElements(ComplexElementTypeType.class);
			for (ComplexElementTypeType element : ceList) {
				cbx_ComplexElements.addItem(element.getId());
			}

			simpleElementsTableModel.clear();
			SimpleElements simpleElements = selectedElement.getSimpleElements();
			if (simpleElements != null) {
				List<Object> list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
				for (Object object : list) {
					SimpleElementTypeType simpleElement = null;
					if (object instanceof SimpleElementTypeType) {
						simpleElement = (SimpleElementTypeType) object;
					} else {
						simpleElement = (SimpleElementTypeType) ((SimpleElementTypeTypeRef) object).getIdref();
					}
					simpleElementsTableModel.add(simpleElement);
				}
			}
			cbx_SimpleElements.removeAllItems();
			cbx_SimpleElements.addItem(null);
			List<SimpleElementTypeType> seList = Editor16.getStore16().getElements(SimpleElementTypeType.class);
			for (SimpleElementTypeType element : seList) {
				cbx_SimpleElements.addItem(element.getId());
			}
		} else {
			selectedElement = null;
			tfd_Id.setText("");
			tfd_Description.setText("");
			startDateField.setDate(null);
			endDateField.setDate(null);
			tfd_State.setText("");
			tfd_Language.setText("");
			tfd_Category.setText("");
			tfd_HelpInfo.setText("");
			subComplexElementsTableModel.clear();
			cbx_ComplexElements.removeAllItems();
			simpleElementsTableModel.clear();
			cbx_SimpleElements.removeAllItems();
		}
		inSelection = false;
	}

	public void newElement() {
		try {
			ComplexElementTypeType newComplexElementType = objectFactory.createComplexElementTypeType();
			newElement(newComplexElementType, "ComplexElement_");

			int row = elementsTableModel.add(newComplexElementType);
			tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		ComplexElementTypeType complexElementType = elementsTableModel.get(row);

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
		String ceId = (String) cbx_ComplexElements.getSelectedItem();
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
		subComplexElementsTableModel.add(element);
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

	public void removeComplexElement() {
		int selectedSubComplexElementsRow = tbl_SubComplexElements.getSelectedRow();

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
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

	public void selectSimpleElement() {
		int selectedIndex = cbx_SimpleElements.getSelectedIndex();
		btn_AddSimpleElement.setEnabled(selectedIndex > 0);
	}

	public void addSimpleElement() {
		String seId = (String) cbx_SimpleElements.getSelectedItem();
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
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

	public void removeSimpleElement() {
		int selectedSimpleElementsRow = tbl_SimpleElements.getSelectedRow();

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
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}
}
