package nl.visi.interaction_framework.editor.v16;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.GroupTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Group;
import nl.visi.schemas._20160331.OrganisationTypeType;
import nl.visi.schemas._20160331.PersonTypeType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.TransactionPhaseTypeType;

public class MiscellaneousPanelControl16 extends PanelControl16<ElementType> {
	private static final String MISCELLANEOUS_PANEL = "nl/visi/interaction_framework/editor/swixml/MiscellaneousPanel.xml";
	private JTable tbl_ComplexElements;
	private JPanel startDatePanel, endDatePanel, relationsPanel;
	private JLabel lbl_Code, lbl_Namespace;
	private JComboBox<MiscellaneousTypes> cbx_ElementType;
	private JComboBox<String> cbx_ComplexElements;
	private ComplexElementsTableModel complexElementsTableModel;
	private JButton btn_AddComplexElement, btn_RemoveComplexElement;
	private JTextField tfd_Namespace;

	private enum MiscellaneousTypes {
		AppendixType, GroupType, OrganisationType, PersonType, ProjectType, TransactionPhaseType;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + this.name());
		}

	}

	private enum MiscellaneousTableColumns {
//		Id, ElementType, Description, StartDate, EndDate, State, DateLamu, UserLamu;
		Id, ElementType, Description;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	public class MiscellaneousTableModel extends ElementsTableModel<ElementType> {

		@Override
		public int getColumnCount() {
			return MiscellaneousTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return MiscellaneousTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ElementType element = get(rowIndex);
			switch (MiscellaneousTableColumns.values()[columnIndex]) {
			case Id:
				return element.getId();
			case ElementType:
				String className = element.getClass().getSimpleName();
				String label = "lbl_" + className.substring(0, className.length() - 4);
				return getBundle().getString(label);
			case Description:
				if (element instanceof ProjectTypeType)
					return ((ProjectTypeType) element).getDescription();
				if (element instanceof PersonTypeType)
					return ((PersonTypeType) element).getDescription();
				if (element instanceof OrganisationTypeType)
					return ((OrganisationTypeType) element).getDescription();
				if (element instanceof GroupTypeType)
					return ((GroupTypeType) element).getDescription();
				if (element instanceof AppendixTypeType)
					return ((AppendixTypeType) element).getDescription();
				if (element instanceof TransactionPhaseTypeType)
					return ((TransactionPhaseTypeType) element).getDescription();
				return null;
//			case StartDate:
//				if (element instanceof ProjectTypeType)
//					return getDate(((ProjectTypeType) element).getStartDate());
//				if (element instanceof PersonTypeType)
//					return getDate(((PersonTypeType) element).getStartDate());
//				if (element instanceof OrganisationTypeType)
//					return getDate(((OrganisationTypeType) element).getStartDate());
//				if (element instanceof GroupTypeType)
//					return getDate(((GroupTypeType) element).getStartDate());
//				if (element instanceof AppendixTypeType)
//					return getDate(((AppendixTypeType) element).getStartDate());
//				if (element instanceof TransactionPhaseTypeType)
//					return getDate(((TransactionPhaseTypeType) element).getStartDate());
//				return null;
//			case EndDate:
//				if (element instanceof ProjectTypeType)
//					return getDate(((ProjectTypeType) element).getEndDate());
//				if (element instanceof PersonTypeType)
//					return getDate(((PersonTypeType) element).getEndDate());
//				if (element instanceof OrganisationTypeType)
//					return getDate(((OrganisationTypeType) element).getEndDate());
//				if (element instanceof GroupTypeType)
//					return getDate(((GroupTypeType) element).getEndDate());
//				if (element instanceof AppendixTypeType)
//					return getDate(((AppendixTypeType) element).getEndDate());
//				if (element instanceof TransactionPhaseTypeType)
//					return getDate(((TransactionPhaseTypeType) element).getEndDate());
//				return null;
//			case State:
//				if (element instanceof ProjectTypeType)
//					return ((ProjectTypeType) element).getState();
//				if (element instanceof PersonTypeType)
//					return ((PersonTypeType) element).getState();
//				if (element instanceof OrganisationTypeType)
//					return ((OrganisationTypeType) element).getState();
//				if (element instanceof GroupTypeType)
//					return ((GroupTypeType) element).getState();
//				if (element instanceof AppendixTypeType)
//					return ((AppendixTypeType) element).getState();
//				if (element instanceof TransactionPhaseTypeType)
//					return ((TransactionPhaseTypeType) element).getState();
//				return null;
//			case DateLamu:
//				if (element instanceof ProjectTypeType)
//					return getDateTime(((ProjectTypeType) element).getDateLaMu());
//				if (element instanceof PersonTypeType)
//					return getDateTime(((PersonTypeType) element).getDateLaMu());
//				if (element instanceof OrganisationTypeType)
//					return getDateTime(((OrganisationTypeType) element).getDateLaMu());
//				if (element instanceof GroupTypeType)
//					return getDateTime(((GroupTypeType) element).getDateLaMu());
//				if (element instanceof AppendixTypeType)
//					return getDateTime(((AppendixTypeType) element).getDateLaMu());
//				if (element instanceof TransactionPhaseTypeType)
//					return getDateTime(((TransactionPhaseTypeType) element).getDateLaMu());
//				return null;
//			case UserLamu:
//				if (element instanceof ProjectTypeType)
//					return ((ProjectTypeType) element).getUserLaMu();
//				if (element instanceof PersonTypeType)
//					return ((PersonTypeType) element).getUserLaMu();
//				if (element instanceof OrganisationTypeType)
//					return ((OrganisationTypeType) element).getUserLaMu();
//				if (element instanceof GroupTypeType)
//					return ((GroupTypeType) element).getUserLaMu();
//				if (element instanceof AppendixTypeType)
//					return ((AppendixTypeType) element).getUserLaMu();
//				if (element instanceof TransactionPhaseTypeType)
//					return ((TransactionPhaseTypeType) element).getUserLaMu();
//				return null;
			default:
				return null;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

	}

	ComplexElementsTableModel getComplexElementsTableModel() {
		return complexElementsTableModel;
	}

	private enum ComplexElementsTableColumns {
		Id, Description, Navigate;

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
			default:
				break;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == ComplexElementsTableColumns.Navigate.ordinal();
		}

	}

	public MiscellaneousPanelControl16() throws Exception {
		super(MISCELLANEOUS_PANEL);
		initMiscellaneousTable();
		initComplexElementsTable();

		for (MiscellaneousTypes type : MiscellaneousTypes.values()) {
			cbx_ElementType.addItem(type);
		}
		initStartDateField();
		initEndDateField();
	}

	private void initEndDateField() {
		endDateField = new DateField(endDatePanel);
		endDateField.addPropertyChangeListener("date", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				try {
					Date endDate = endDateField.getDate();
					if (endDate != null) {
						gcal.setTime(endDate);
						XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
						if (selectedElement instanceof AppendixTypeType)
							((AppendixTypeType) selectedElement).setEndDate(xgcal);
						else if (selectedElement instanceof GroupTypeType)
							((GroupTypeType) selectedElement).setEndDate(xgcal);
						else if (selectedElement instanceof OrganisationTypeType)
							((OrganisationTypeType) selectedElement).setEndDate(xgcal);
						else if (selectedElement instanceof PersonTypeType)
							((PersonTypeType) selectedElement).setEndDate(xgcal);
						else if (selectedElement instanceof ProjectTypeType)
							((ProjectTypeType) selectedElement).setEndDate(xgcal);
						else if (selectedElement instanceof TransactionPhaseTypeType)
							((TransactionPhaseTypeType) selectedElement).setEndDate(xgcal);
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
						gcal.setTime(startDate);
						XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
						if (selectedElement instanceof AppendixTypeType)
							((AppendixTypeType) selectedElement).setStartDate(xgcal);
						else if (selectedElement instanceof GroupTypeType)
							((GroupTypeType) selectedElement).setStartDate(xgcal);
						else if (selectedElement instanceof OrganisationTypeType)
							((OrganisationTypeType) selectedElement).setStartDate(xgcal);
						else if (selectedElement instanceof PersonTypeType)
							((PersonTypeType) selectedElement).setStartDate(xgcal);
						else if (selectedElement instanceof ProjectTypeType)
							((ProjectTypeType) selectedElement).setStartDate(xgcal);
						else if (selectedElement instanceof TransactionPhaseTypeType)
							((TransactionPhaseTypeType) selectedElement).setStartDate(xgcal);
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
	private void initComplexElementsTable() {
		complexElementsTableModel = new ComplexElementsTableModel();
		complexElementsTableModel.setSorted(false);
		tbl_ComplexElements.setModel(complexElementsTableModel);
		tbl_ComplexElements.setAutoCreateRowSorter(true);
		tbl_ComplexElements.setFillsViewportHeight(true);
		tbl_ComplexElements.setDropMode(DropMode.INSERT_ROWS);
		tbl_ComplexElements
				.setTransferHandler(getTransferHandler(tbl_ComplexElements, complexElementsTableModel, true));
		tbl_ComplexElements.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = tbl_ComplexElements.getSelectedRow();
				btn_RemoveComplexElement.setEnabled(selectedRow >= 0);
			}
		});
		TableColumn navigateColumn = tbl_ComplexElements.getColumnModel()
				.getColumn(ComplexElementsTableColumns.Navigate.ordinal());
		navigateColumn.setMaxWidth(50);
		navigateColumn.setCellRenderer(getButtonTableCellRenderer());
		navigateColumn.setCellEditor(new NavigatorEditor() {
			@Override
			protected void navigate() {
				int row = tbl_ComplexElements.getSelectedRow();
				ComplexElementTypeType complexElementTypeType = complexElementsTableModel.get(row);
				if (complexElementTypeType != null) {
					InteractionFrameworkEditor.navigate(complexElementTypeType);
				}
			}
		});
	}

	private void initMiscellaneousTable() {
		elementsTableModel = new MiscellaneousTableModel();
		tbl_Elements.setModel(elementsTableModel);
		tbl_Elements.setAutoCreateRowSorter(true);
		TableRowSorter<ElementsTableModel<ElementType>> tableRowSorter = new TableRowSorter<>(elementsTableModel);
//		tableRowSorter.setComparator(MiscellaneousTableColumns.StartDate.ordinal(), dateComparator);
//		tableRowSorter.setComparator(MiscellaneousTableColumns.EndDate.ordinal(), dateComparator);
//		tableRowSorter.setComparator(MiscellaneousTableColumns.DateLamu.ordinal(), dateTimeComparator);
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

		tfd_Filter.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
//				String filterString = tfd_Filter.getText().toUpperCase();
//				if (filterString.isEmpty()) {
//					fillTable();
//				} else {
//					Store16 store = Editor16.getStore16();
//
//					elementsTableModel.clear();
//					List<ProjectTypeType> projects = store.getElements(ProjectTypeType.class);
//					for (ProjectTypeType project : projects) {
//						if (project.getDescription().toUpperCase().contains(filterString)
//								|| project.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(project);
//						}
//					}
//					List<PersonTypeType> persons = store.getElements(PersonTypeType.class);
//					for (PersonTypeType person : persons) {
//						if (person.getDescription().toUpperCase().contains(filterString)
//								|| person.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(person);
//						}
//					}
//					List<OrganisationTypeType> organisations = store.getElements(OrganisationTypeType.class);
//					for (OrganisationTypeType organisation : organisations) {
//						if (organisation.getDescription().toUpperCase().contains(filterString)
//								|| organisation.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(organisation);
//						}
//					}
//					List<GroupTypeType> groups = store.getElements(GroupTypeType.class);
//					for (GroupTypeType group : groups) {
//						if (group.getDescription().toUpperCase().contains(filterString)
//								|| group.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(group);
//						}
//					}
//					List<AppendixTypeType> appendices = store.getElements(AppendixTypeType.class);
//					for (AppendixTypeType appendix : appendices) {
//						if (appendix.getDescription().toUpperCase().contains(filterString)
//								|| appendix.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(appendix);
//						}
//					}
//					List<TransactionPhaseTypeType> transactionPhases = store
//							.getElements(TransactionPhaseTypeType.class);
//					for (TransactionPhaseTypeType transactionPhase : transactionPhases) {
//						if (transactionPhase.getDescription().toUpperCase().contains(filterString)
//								|| transactionPhase.getId().toUpperCase().contains(filterString)) {
//							elementsTableModel.add(transactionPhase);
//						}
//					}
//				}

				fillTable();
			}

		});

	}

	@Override
	public void fillTable() {
//		Store16 store = Editor16.getStore16();
//
//		elementsTableModel.clear();
//		List<ProjectTypeType> projects = store.getElements(ProjectTypeType.class);
//		for (ProjectTypeType project : projects) {
//			elementsTableModel.add(project);
//		}
//		List<PersonTypeType> persons = store.getElements(PersonTypeType.class);
//		for (PersonTypeType person : persons) {
//			elementsTableModel.add(person);
//		}
//		List<OrganisationTypeType> organisations = store.getElements(OrganisationTypeType.class);
//		for (OrganisationTypeType organisation : organisations) {
//			elementsTableModel.add(organisation);
//		}
//		List<GroupTypeType> groups = store.getElements(GroupTypeType.class);
//		for (GroupTypeType group : groups) {
//			elementsTableModel.add(group);
//		}
//		List<AppendixTypeType> appendices = store.getElements(AppendixTypeType.class);
//		for (AppendixTypeType appendix : appendices) {
//			elementsTableModel.add(appendix);
//		}
//		List<TransactionPhaseTypeType> transactionPhases = store.getElements(TransactionPhaseTypeType.class);
//		for (TransactionPhaseTypeType transactionPhase : transactionPhases) {
//			elementsTableModel.add(transactionPhase);
//		}

		String filterString = tfd_Filter.getText().toUpperCase();
		if (filterString.isEmpty()) {
			Store16 store = Editor16.getStore16();

			elementsTableModel.clear();
			List<ProjectTypeType> projects = store.getElements(ProjectTypeType.class);
			for (ProjectTypeType project : projects) {
				elementsTableModel.add(project);
			}
			List<PersonTypeType> persons = store.getElements(PersonTypeType.class);
			for (PersonTypeType person : persons) {
				elementsTableModel.add(person);
			}
			List<OrganisationTypeType> organisations = store.getElements(OrganisationTypeType.class);
			for (OrganisationTypeType organisation : organisations) {
				elementsTableModel.add(organisation);
			}
			List<GroupTypeType> groups = store.getElements(GroupTypeType.class);
			for (GroupTypeType group : groups) {
				elementsTableModel.add(group);
			}
			List<AppendixTypeType> appendices = store.getElements(AppendixTypeType.class);
			for (AppendixTypeType appendix : appendices) {
				elementsTableModel.add(appendix);
			}
			List<TransactionPhaseTypeType> transactionPhases = store.getElements(TransactionPhaseTypeType.class);
			for (TransactionPhaseTypeType transactionPhase : transactionPhases) {
				elementsTableModel.add(transactionPhase);
			}
		} else {
			Store16 store = Editor16.getStore16();

			elementsTableModel.clear();
			List<ProjectTypeType> projects = store.getElements(ProjectTypeType.class);
			for (ProjectTypeType project : projects) {
				if (project.getDescription().toUpperCase().contains(filterString)
						|| project.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(project);
				}
			}
			List<PersonTypeType> persons = store.getElements(PersonTypeType.class);
			for (PersonTypeType person : persons) {
				if (person.getDescription().toUpperCase().contains(filterString)
						|| person.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(person);
				}
			}
			List<OrganisationTypeType> organisations = store.getElements(OrganisationTypeType.class);
			for (OrganisationTypeType organisation : organisations) {
				if (organisation.getDescription().toUpperCase().contains(filterString)
						|| organisation.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(organisation);
				}
			}
			List<GroupTypeType> groups = store.getElements(GroupTypeType.class);
			for (GroupTypeType group : groups) {
				if (group.getDescription().toUpperCase().contains(filterString)
						|| group.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(group);
				}
			}
			List<AppendixTypeType> appendices = store.getElements(AppendixTypeType.class);
			for (AppendixTypeType appendix : appendices) {
				if (appendix.getDescription().toUpperCase().contains(filterString)
						|| appendix.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(appendix);
				}
			}
			List<TransactionPhaseTypeType> transactionPhases = store.getElements(TransactionPhaseTypeType.class);
			for (TransactionPhaseTypeType transactionPhase : transactionPhases) {
				if (transactionPhase.getDescription().toUpperCase().contains(filterString)
						|| transactionPhase.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(transactionPhase);
				}
			}
		}
	}

	public void newElement() {
		ElementType et = null;
		try {
			MiscellaneousTypes type = MiscellaneousTypes.values()[cbx_ElementType.getSelectedIndex()];
			switch (type) {
			case GroupType:
				et = objectFactory.createGroupTypeType();
				break;
			case OrganisationType:
				et = objectFactory.createOrganisationTypeType();
				break;
			case PersonType:
				et = objectFactory.createPersonTypeType();
				break;
			case ProjectType:
				et = objectFactory.createProjectTypeType();
				break;
			case AppendixType:
				et = objectFactory.createAppendixTypeType();
				break;
			case TransactionPhaseType:
				et = objectFactory.createTransactionPhaseTypeType();
				break;
			}

			newElement(et, type.name() + "_");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int row = elementsTableModel.add(et);
		row = tbl_Elements.convertRowIndexToView(row);
		tbl_Elements.getSelectionModel().setSelectionInterval(row, row);
	}

	public void copyElement() {
		Store16 store = Editor16.getStore16();
		int row = tbl_Elements.getSelectedRow();
		row = tbl_Elements.getRowSorter().convertRowIndexToModel(row);
		ElementType origElementType = elementsTableModel.get(row);
		ElementType copyElementType = null;
		String simpleClassName = origElementType.getClass().getSimpleName();
		try {
			switch (MiscellaneousTypes.valueOf(simpleClassName.substring(0, simpleClassName.lastIndexOf("Type")))) {
			case AppendixType:
				AppendixTypeType origAppendixType = (AppendixTypeType) origElementType;
				AppendixTypeType copyAppendixType = objectFactory.createAppendixTypeType();
				newElement(copyAppendixType, "AppendixType_");
				store.generateCopyId(copyAppendixType, origAppendixType);
				copyAppendixType.setCategory(origAppendixType.getCategory());
				copyAppendixType.setCode(origAppendixType.getCode());
				copyAppendixType.setComplexElements(origAppendixType.getComplexElements());
				copyAppendixType.setDescription(origAppendixType.getDescription());
				copyAppendixType.setEndDate(origAppendixType.getEndDate());
				copyAppendixType.setHelpInfo(origAppendixType.getHelpInfo());
				copyAppendixType.setLanguage(origAppendixType.getLanguage());
				copyAppendixType.setStartDate(origAppendixType.getStartDate());
				copyAppendixType.setState(origAppendixType.getState());
				copyElementType = copyAppendixType;
				break;
			case GroupType:
				GroupTypeType origGroupType = (GroupTypeType) origElementType;
				GroupTypeType copyGroupType = objectFactory.createGroupTypeType();
				newElement(copyGroupType, "GroupType_");
				store.generateCopyId(copyGroupType, origGroupType);
				copyGroupType.setCategory(origGroupType.getCategory());
				copyGroupType.setDescription(origGroupType.getDescription());
				copyGroupType.setEndDate(origGroupType.getEndDate());
				copyGroupType.setHelpInfo(origGroupType.getHelpInfo());
				copyGroupType.setLanguage(origGroupType.getLanguage());
				copyGroupType.setStartDate(origGroupType.getStartDate());
				copyGroupType.setState(origGroupType.getState());
				copyElementType = copyGroupType;
				break;
			case OrganisationType:
				OrganisationTypeType origOrganisationType = (OrganisationTypeType) origElementType;
				OrganisationTypeType copyOrganisationType = objectFactory.createOrganisationTypeType();
				newElement(copyOrganisationType, "OrganisationType_");
				store.generateCopyId(copyOrganisationType, origOrganisationType);
				copyOrganisationType.setCategory(origOrganisationType.getCategory());
				copyOrganisationType.setCode(origOrganisationType.getCode());
				copyOrganisationType.setComplexElements(origOrganisationType.getComplexElements());
				copyOrganisationType.setDescription(origOrganisationType.getDescription());
				copyOrganisationType.setEndDate(origOrganisationType.getEndDate());
				copyOrganisationType.setHelpInfo(origOrganisationType.getHelpInfo());
				copyOrganisationType.setLanguage(origOrganisationType.getLanguage());
				copyOrganisationType.setStartDate(origOrganisationType.getStartDate());
				copyOrganisationType.setState(origOrganisationType.getState());
				copyElementType = copyOrganisationType;
				break;
			case PersonType:
				PersonTypeType origPersonType = (PersonTypeType) origElementType;
				PersonTypeType copyPersonType = objectFactory.createPersonTypeType();
				newElement(copyPersonType, "PersonType_");
				store.generateCopyId(copyPersonType, origPersonType);
				copyPersonType.setCategory(origPersonType.getCategory());
				copyPersonType.setCode(origPersonType.getCode());
				copyPersonType.setComplexElements(origPersonType.getComplexElements());
				copyPersonType.setDescription(origPersonType.getDescription());
				copyPersonType.setEndDate(origPersonType.getEndDate());
				copyPersonType.setHelpInfo(origPersonType.getHelpInfo());
				copyPersonType.setLanguage(origPersonType.getLanguage());
				copyPersonType.setStartDate(origPersonType.getStartDate());
				copyPersonType.setState(origPersonType.getState());
				copyElementType = copyPersonType;
				break;
			case ProjectType:
				ProjectTypeType origProjectType = (ProjectTypeType) origElementType;
				ProjectTypeType copyProjectType = objectFactory.createProjectTypeType();
				newElement(copyProjectType, "ProjectType_");
				store.generateCopyId(copyProjectType, origProjectType);
				copyProjectType.setCategory(origProjectType.getCategory());
				copyProjectType.setCode(origProjectType.getCode());
				copyProjectType.setComplexElements(origProjectType.getComplexElements());
				copyProjectType.setDescription(origProjectType.getDescription());
				copyProjectType.setEndDate(origProjectType.getEndDate());
				copyProjectType.setHelpInfo(origProjectType.getHelpInfo());
				copyProjectType.setLanguage(origProjectType.getLanguage());
				copyProjectType.setNamespace(origProjectType.getNamespace());
				copyProjectType.setStartDate(origProjectType.getStartDate());
				copyProjectType.setState(origProjectType.getState());
				copyElementType = copyProjectType;
				break;
			case TransactionPhaseType:
				TransactionPhaseTypeType origTransactionPhaseType = (TransactionPhaseTypeType) origElementType;
				TransactionPhaseTypeType copyTransactionPhaseType = objectFactory.createTransactionPhaseTypeType();
				newElement(copyTransactionPhaseType, "TransactionPhaseType_");
				store.generateCopyId(copyTransactionPhaseType, origTransactionPhaseType);
				copyTransactionPhaseType.setCategory(origTransactionPhaseType.getCategory());
				copyTransactionPhaseType.setCode(origTransactionPhaseType.getCode());
				copyTransactionPhaseType.setDescription(origTransactionPhaseType.getDescription());
				copyTransactionPhaseType.setEndDate(origTransactionPhaseType.getEndDate());
				copyTransactionPhaseType.setHelpInfo(origTransactionPhaseType.getHelpInfo());
				copyTransactionPhaseType.setLanguage(origTransactionPhaseType.getLanguage());
				copyTransactionPhaseType.setStartDate(origTransactionPhaseType.getStartDate());
				copyTransactionPhaseType.setState(origTransactionPhaseType.getState());
				copyElementType = copyTransactionPhaseType;
				break;
			default:
				break;
			}
			store.put(copyElementType.getId(), origElementType);
			int copyrow = elementsTableModel.add(copyElementType);
			copyrow = tbl_Elements.convertRowIndexToView(copyrow);
			tbl_Elements.getSelectionModel().setSelectionInterval(copyrow, copyrow);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteElement() {
		int selectedMiscellaneousRow = tbl_Elements.getSelectedRow();
		selectedMiscellaneousRow = tbl_Elements.getRowSorter().convertRowIndexToModel(selectedMiscellaneousRow);
		ElementType elementType = elementsTableModel.get(selectedMiscellaneousRow);
		String className = elementType.getClass().getSimpleName();
		MiscellaneousTypes type = MiscellaneousTypes.valueOf(className.substring(0, className.length() - 4));
		List<Object> mittElements = null;
		switch (type) {
		case AppendixType:
			break;
		case GroupType:
			mittElements = Editor16.getStore16().getElements(MessageInTransactionTypeType.class);
			for (Object object : mittElements) {
				MessageInTransactionTypeType mittElement = (MessageInTransactionTypeType) object;
				Group group = mittElement.getGroup();
				if (group != null) {
					GroupTypeType groupType = group.getGroupType();
					if (groupType == null) {
						groupType = (GroupTypeType) group.getGroupTypeRef().getIdref();
					}
					if (groupType != null && groupType.equals(elementType)) {
						mittElement.setGroup(null);
					}
				}
			}
			break;
		case OrganisationType:
			break;
		case PersonType:
			break;
		case ProjectType:
			break;
		case TransactionPhaseType:
			mittElements = Editor16.getStore16().getElements(MessageInTransactionTypeType.class);
			for (Object object : mittElements) {
				MessageInTransactionTypeType mittElement = (MessageInTransactionTypeType) object;
				MessageInTransactionTypeType.TransactionPhase transactionPhase = mittElement.getTransactionPhase();
				if (transactionPhase != null) {
					TransactionPhaseTypeType transactionPhaseType = transactionPhase.getTransactionPhaseType();
					if (transactionPhaseType == null) {
						transactionPhaseType = (TransactionPhaseTypeType) transactionPhase.getTransactionPhaseTypeRef()
								.getIdref();
					}
					if (transactionPhaseType != null && transactionPhaseType.equals(elementType)) {
						mittElement.setTransactionPhase(null);
					}
				}
			}
			break;
		}
		elementsTableModel.remove(selectedMiscellaneousRow);
		Editor16.getStore16().remove(elementType.getId());
	}

	protected void updateSelectionArea(ListSelectionEvent e) {
		inSelection = true;

		selectedRow = tbl_Elements.getSelectedRow();
		tbl_Elements.scrollRectToVisible(tbl_Elements.getCellRect(selectedRow, 0, true));
		if (selectedRow >= 0) {
			selectedRow = tbl_Elements.getRowSorter().convertRowIndexToModel(selectedRow);
		}
		boolean rowSelected = selectedRow >= 0;
		selectedElement = rowSelected ? elementsTableModel.get(selectedRow) : null;
		boolean isGroupType = selectedElement instanceof GroupTypeType;
		boolean isProjectType = selectedElement instanceof ProjectTypeType;
		boolean isTransactionPhaseType = selectedElement instanceof TransactionPhaseTypeType;
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
		tfd_Code.setEnabled(rowSelected);
		lbl_Code.setVisible(!isGroupType);
		tfd_Code.setVisible(!isGroupType);
		tfd_Code.getParent().invalidate();
		tfd_Namespace.setEnabled(rowSelected);
		lbl_Namespace.setVisible(isProjectType);
		tfd_Namespace.setVisible(isProjectType);
		tfd_Namespace.getParent().invalidate();
		if (rowSelected && !(isGroupType || isTransactionPhaseType)) {
			tbl_ComplexElements.setEnabled(true);
			cbx_ComplexElements.setEnabled(true);
			relationsPanel.setVisible(true);
		} else {
			tbl_ComplexElements.setEnabled(false);
			cbx_ComplexElements.setEnabled(false);
			relationsPanel.setVisible(false);
		}
		relationsPanel.invalidate();
		if (rowSelected) {
			complexElementsTableModel.clear();
			tfd_Id.setText(selectedElement.getId());
			List<Object> ceList = null;
			if (selectedElement instanceof ProjectTypeType) {
				ProjectTypeType projectType = (ProjectTypeType) selectedElement;
				tfd_Description.setText(projectType.getDescription());
				XMLGregorianCalendar startDate = projectType.getStartDate();
				startDateField.setDate(startDate != null ? startDate.toGregorianCalendar().getTime() : null);
				XMLGregorianCalendar endDate = projectType.getEndDate();
				endDateField.setDate(endDate != null ? endDate.toGregorianCalendar().getTime() : null);
				tfd_State.setText(projectType.getState());
				tfd_DateLamu.setText(projectType.getDateLaMu() != null
						? sdfDateTime.format(projectType.getDateLaMu().toGregorianCalendar().getTime())
						: "");
				tfd_UserLamu.setText(projectType.getUserLaMu());
				tfd_Language.setText(projectType.getLanguage());
				tfd_Category.setText(projectType.getCategory());
				tfd_HelpInfo.setText(projectType.getHelpInfo());
				tfd_Code.setText(projectType.getCode());
				tfd_Namespace.setText(projectType.getNamespace());
				ProjectTypeType.ComplexElements complexElements = projectType.getComplexElements();
				if (complexElements != null) {
					ceList = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				}
			} else if (selectedElement instanceof PersonTypeType) {
				PersonTypeType personType = (PersonTypeType) selectedElement;
				tfd_Description.setText(personType.getDescription());
				XMLGregorianCalendar startDate = personType.getStartDate();
				startDateField.setDate(startDate != null ? startDate.toGregorianCalendar().getTime() : null);
				XMLGregorianCalendar endDate = personType.getEndDate();
				endDateField.setDate(endDate != null ? endDate.toGregorianCalendar().getTime() : null);
				tfd_State.setText(personType.getState());
				tfd_DateLamu.setText(personType.getDateLaMu() != null
						? sdfDateTime.format(personType.getDateLaMu().toGregorianCalendar().getTime())
						: "");
				tfd_UserLamu.setText(personType.getUserLaMu());
				tfd_Language.setText(personType.getLanguage());
				tfd_Category.setText(personType.getCategory());
				tfd_HelpInfo.setText(personType.getHelpInfo());
				tfd_Code.setText(personType.getCode());
				PersonTypeType.ComplexElements complexElements = personType.getComplexElements();
				if (complexElements != null) {
					ceList = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				}
			} else if (selectedElement instanceof OrganisationTypeType) {
				OrganisationTypeType organisationType = (OrganisationTypeType) selectedElement;
				tfd_Description.setText(organisationType.getDescription());
				XMLGregorianCalendar startDate = organisationType.getStartDate();
				startDateField.setDate(startDate != null ? startDate.toGregorianCalendar().getTime() : null);
				XMLGregorianCalendar endDate = organisationType.getEndDate();
				endDateField.setDate(endDate != null ? endDate.toGregorianCalendar().getTime() : null);
				tfd_State.setText(organisationType.getState());
				tfd_DateLamu.setText(organisationType.getDateLaMu() != null
						? sdfDateTime.format(organisationType.getDateLaMu().toGregorianCalendar().getTime())
						: "");
				tfd_UserLamu.setText(organisationType.getUserLaMu());
				tfd_Language.setText(organisationType.getLanguage());
				tfd_Category.setText(organisationType.getCategory());
				tfd_HelpInfo.setText(organisationType.getHelpInfo());
				tfd_Code.setText(organisationType.getCode());
				OrganisationTypeType.ComplexElements complexElements = organisationType.getComplexElements();
				if (complexElements != null) {
					ceList = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				}
			} else if (selectedElement instanceof GroupTypeType) {
				tfd_Description.setText(((GroupTypeType) selectedElement).getDescription());
				XMLGregorianCalendar startDate = ((GroupTypeType) selectedElement).getStartDate();
				startDateField.setDate(startDate != null ? startDate.toGregorianCalendar().getTime() : null);
				XMLGregorianCalendar endDate = ((GroupTypeType) selectedElement).getEndDate();
				endDateField.setDate(endDate != null ? endDate.toGregorianCalendar().getTime() : null);
				tfd_State.setText(((GroupTypeType) selectedElement).getState());
				tfd_DateLamu
						.setText(((GroupTypeType) selectedElement).getDateLaMu() != null
								? sdfDateTime.format(
										((GroupTypeType) selectedElement).getDateLaMu().toGregorianCalendar().getTime())
								: "");
				tfd_UserLamu.setText(((GroupTypeType) selectedElement).getUserLaMu());
				tfd_Language.setText(((GroupTypeType) selectedElement).getLanguage());
				tfd_Category.setText(((GroupTypeType) selectedElement).getCategory());
				tfd_HelpInfo.setText(((GroupTypeType) selectedElement).getHelpInfo());
			} else if (selectedElement instanceof AppendixTypeType) {
				AppendixTypeType appendixType = (AppendixTypeType) selectedElement;
				tfd_Description.setText(appendixType.getDescription());
				XMLGregorianCalendar startDate = appendixType.getStartDate();
				startDateField.setDate(startDate != null ? startDate.toGregorianCalendar().getTime() : null);
				XMLGregorianCalendar endDate = appendixType.getEndDate();
				endDateField.setDate(endDate != null ? endDate.toGregorianCalendar().getTime() : null);
				tfd_State.setText(appendixType.getState());
				tfd_DateLamu.setText(appendixType.getDateLaMu() != null
						? sdfDateTime.format(appendixType.getDateLaMu().toGregorianCalendar().getTime())
						: "");
				tfd_UserLamu.setText(appendixType.getUserLaMu());
				tfd_Language.setText(appendixType.getLanguage());
				tfd_Category.setText(appendixType.getCategory());
				tfd_HelpInfo.setText(appendixType.getHelpInfo());
				tfd_Code.setText(appendixType.getCode());
				AppendixTypeType.ComplexElements complexElements = appendixType.getComplexElements();
				if (complexElements != null) {
					ceList = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				}
			} else if (selectedElement instanceof TransactionPhaseTypeType) {
				TransactionPhaseTypeType transactionPhaseType = (TransactionPhaseTypeType) selectedElement;
				tfd_Description.setText(transactionPhaseType.getDescription());
				XMLGregorianCalendar startDate = transactionPhaseType.getStartDate();
				startDateField.setDate(startDate != null ? startDate.toGregorianCalendar().getTime() : null);
				XMLGregorianCalendar endDate = transactionPhaseType.getEndDate();
				endDateField.setDate(endDate != null ? endDate.toGregorianCalendar().getTime() : null);
				tfd_State.setText(transactionPhaseType.getState());
				tfd_DateLamu.setText(transactionPhaseType.getDateLaMu() != null
						? sdfDateTime.format(transactionPhaseType.getDateLaMu().toGregorianCalendar().getTime())
						: "");
				tfd_UserLamu.setText(transactionPhaseType.getUserLaMu());
				tfd_Language.setText(transactionPhaseType.getLanguage());
				tfd_Category.setText(transactionPhaseType.getCategory());
				tfd_HelpInfo.setText(transactionPhaseType.getHelpInfo());
				tfd_Code.setText(transactionPhaseType.getCode());
			}
			if (ceList != null) {
				for (Object object : ceList) {
					ComplexElementTypeType element = null;
					if (object instanceof ComplexElementTypeTypeRef) {
						element = (ComplexElementTypeType) ((ComplexElementTypeTypeRef) object).getIdref();
					} else {
						element = (ComplexElementTypeType) object;
					}
					complexElementsTableModel.add(element);
				}
			}

			cbx_ComplexElements.removeAllItems();
			cbx_ComplexElements.addItem(null);
			List<ComplexElementTypeType> elements = Editor16.getStore16().getElements(ComplexElementTypeType.class);
			for (ComplexElementTypeType element : elements) {
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
			tfd_Code.setText("");
			tfd_Namespace.setText("");
			complexElementsTableModel.clear();
			cbx_ComplexElements.removeAllItems();
		}

		inSelection = false;
	}

	public void selectComplexElement() {
		int selectedIndex = cbx_ComplexElements.getSelectedIndex();
		btn_AddComplexElement.setEnabled(selectedIndex > 0);
	}

	public void addComplexElement() {
		String complexElement = (String) cbx_ComplexElements.getSelectedItem();
		assert complexElement != null;
		String ceId = complexElement.substring(1, complexElement.indexOf("]"));

		ComplexElementTypeType element = Editor16.getStore16().getElement(ComplexElementTypeType.class, ceId);
		ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
		ref.setIdref(element);
		List<Object> list = null;
		if (selectedElement instanceof AppendixTypeType) {
			AppendixTypeType.ComplexElements complexElements = ((AppendixTypeType) selectedElement)
					.getComplexElements();
			if (complexElements == null) {
				complexElements = objectFactory.createAppendixTypeTypeComplexElements();
				((AppendixTypeType) selectedElement).setComplexElements(complexElements);
			}
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		} else if (selectedElement instanceof OrganisationTypeType) {
			OrganisationTypeType.ComplexElements complexElements = ((OrganisationTypeType) selectedElement)
					.getComplexElements();
			if (complexElements == null) {
				complexElements = objectFactory.createOrganisationTypeTypeComplexElements();
				((OrganisationTypeType) selectedElement).setComplexElements(complexElements);
			}
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		} else if (selectedElement instanceof PersonTypeType) {
			PersonTypeType.ComplexElements complexElements = ((PersonTypeType) selectedElement).getComplexElements();
			if (complexElements == null) {
				complexElements = objectFactory.createPersonTypeTypeComplexElements();
				((PersonTypeType) selectedElement).setComplexElements(complexElements);
			}
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		} else if (selectedElement instanceof ProjectTypeType) {
			ProjectTypeType.ComplexElements complexElements = ((ProjectTypeType) selectedElement).getComplexElements();
			if (complexElements == null) {
				complexElements = objectFactory.createProjectTypeTypeComplexElements();
				((ProjectTypeType) selectedElement).setComplexElements(complexElements);
			}
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		}
		list.add(ref);
		complexElementsTableModel.add(element);
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
		cbx_ComplexElements.setSelectedItem(null);
	}

	public void removeComplexElement() {
		int selectedRow = tbl_ComplexElements.getSelectedRow();
		List<Object> list = null;
		ComplexElementTypeType complexElement = complexElementsTableModel.remove(selectedRow);
		if (selectedElement instanceof AppendixTypeType) {
			AppendixTypeType.ComplexElements complexElements = ((AppendixTypeType) selectedElement)
					.getComplexElements();
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		} else if (selectedElement instanceof OrganisationTypeType) {
			OrganisationTypeType.ComplexElements complexElements = ((OrganisationTypeType) selectedElement)
					.getComplexElements();
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		} else if (selectedElement instanceof PersonTypeType) {
			PersonTypeType.ComplexElements complexElements = ((PersonTypeType) selectedElement).getComplexElements();
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		} else if (selectedElement instanceof ProjectTypeType) {
			ProjectTypeType.ComplexElements complexElements = ((ProjectTypeType) selectedElement).getComplexElements();
			list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
		}
		assert list != null;
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
			if (selectedElement instanceof AppendixTypeType) {
				((AppendixTypeType) selectedElement).setComplexElements(null);
			} else if (selectedElement instanceof OrganisationTypeType) {
				((OrganisationTypeType) selectedElement).setComplexElements(null);
			} else if (selectedElement instanceof PersonTypeType) {
				((PersonTypeType) selectedElement).setComplexElements(null);
			} else if (selectedElement instanceof ProjectTypeType) {
				((ProjectTypeType) selectedElement).setComplexElements(null);
			}
		}
		updateLaMu(selectedElement, user);
		elementsTableModel.update(selectedRow);
	}

}
