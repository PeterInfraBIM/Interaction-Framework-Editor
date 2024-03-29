package nl.visi.interaction_framework.editor.v16;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
import nl.visi.interaction_framework.editor.SelectBox;
import nl.visi.interaction_framework.editor.v16.MainPanelControl16.Tabs;
import nl.visi.interaction_framework.editor.v16.TransactionsPanelControl16.TransactionTabs;
import nl.visi.schemas._20160331.AppendixTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.GroupTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType.Group;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.OrganisationTypeType;
import nl.visi.schemas._20160331.PersonTypeType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.TransactionPhaseTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;

public class MiscellaneousPanelControl16 extends PanelControl16<ElementType> {
	private static final String MISCELLANEOUS_PANEL = "nl/visi/interaction_framework/editor/swixml/MiscellaneousPanel.xml";
	private JTable tbl_ComplexElements;
	private JPanel startDatePanel, endDatePanel, cards, relationsPanel, elementConditionPanel, emptyPanel;
	private CardLayout cl;
	private JLabel lbl_StartDate, lbl_EndDate, lbl_State, lbl_DateLamu, lbl_UserLamu, lbl_Language, lbl_Category,
			lbl_Code, lbl_Namespace;
	private JComboBox<MiscellaneousTypes> cbx_ElementType;
	private JComboBox<String> cbx_ComplexElements, cbx_Condition;
	private ComplexElementsTableModel complexElementsTableModel;
	private JButton btn_AddComplexElement, btn_RemoveComplexElement, btn_NavigateComplexElementType1,
			btn_RemoveComplexElementType1, btn_NavigateComplexElementType2, btn_RemoveComplexElementType2,
			btn_NavigateSimpleElementType, btn_RemoveSimpleElementType, btn_NavigateMitt, btn_NavigateTransactionType,
			btn_NavigateMessageType, btn_RemoveMitt;
	private JTextField tfd_Namespace, tfd_ComplexElement1, tfd_ComplexElement2, tfd_SimpleElement, tfd_MittId,
			tfd_Transaction, tfd_Message;

	private enum MiscellaneousTypes {
		AppendixType, ElementCondition, GroupType, OrganisationType, PersonType, ProjectType, TransactionPhaseType;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + this.name());
		}

	}

	private enum MiscellaneousTableColumns {
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
				if (element instanceof ElementConditionType)
					return ((ElementConditionType) element).getDescription();
				return null;
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

		cl = (CardLayout) (cards.getLayout());
		cards.add(relationsPanel, "relationsPanel");
		cards.add(elementConditionPanel, "elementConditionPanel");
		cards.add(emptyPanel, "emptyPanel");
		cl.show(cards, "emptyPanel");

		cbx_Condition.addItem(null);
		cbx_Condition.addItem("EMPTY");
		cbx_Condition.addItem("FIXED");
		cbx_Condition.addItem("FREE");
		
		initNamespaceField();
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

	private void initNamespaceField() {
		tfd_Namespace.setEditable(true);
		tfd_Namespace.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected synchronized void update(DocumentEvent e) {
				if (inSelection)
					return;
				((ProjectTypeType) selectedElement).setNamespace(tfd_Namespace.getText());
				updateLaMu(selectedElement, getUser());
			}
		});
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
				fillTable();
			}
		});

	}

	@Override
	public void fillTable() {
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
			List<ElementConditionType> elementConditions = store.getElements(ElementConditionType.class);
			for (ElementConditionType elementCondition : elementConditions) {
				elementsTableModel.add(elementCondition);
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
			List<ElementConditionType> elementConditions = store.getElements(ElementConditionType.class);
			for (ElementConditionType elementCondition : elementConditions) {
				if (elementCondition.getDescription().toUpperCase().contains(filterString)
						|| elementCondition.getId().toUpperCase().contains(filterString)) {
					elementsTableModel.add(elementCondition);
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
			case ElementCondition:
				et = objectFactory.createElementConditionType();
				((ElementConditionType) et).setCondition("FIXED");
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
				copyComplexElements(origAppendixType, copyAppendixType, simpleClassName);
				copyAppendixType.setDescription(origAppendixType.getDescription());
				copyAppendixType.setEndDate(origAppendixType.getEndDate());
				copyAppendixType.setHelpInfo(origAppendixType.getHelpInfo());
				copyAppendixType.setLanguage(origAppendixType.getLanguage());
				copyAppendixType.setStartDate(origAppendixType.getStartDate());
				copyAppendixType.setState(origAppendixType.getState());
				copyElementType = copyAppendixType;
				break;
			case ElementCondition:
				ElementConditionType origElementConditionType = (ElementConditionType) origElementType;
				ElementConditionType copyElementConditionType = objectFactory.createElementConditionType();
				newElement(copyElementConditionType, "ElementConditionType_");
				store.generateCopyId(copyElementConditionType, origElementConditionType);
				copyElementConditionType.setCondition(origElementConditionType.getCondition());
				copyElementConditionType.setMessageInTransaction(origElementConditionType.getMessageInTransaction());
				copyComplexElements(origElementConditionType, copyElementConditionType, simpleClassName);
				copySimpleElement(origElementConditionType, copyElementConditionType);
				copyElementConditionType.setSimpleElement(origElementConditionType.getSimpleElement());
				copyElementConditionType.setDescription(origElementConditionType.getDescription());
				copyElementConditionType.setHelpInfo(origElementConditionType.getHelpInfo());
				copyElementType = copyElementConditionType;
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
				copyComplexElements(origOrganisationType, copyOrganisationType, simpleClassName);
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
				copyComplexElements(origPersonType, copyPersonType, simpleClassName);
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
				copyComplexElements(origProjectType, copyProjectType, simpleClassName);
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

	private void copyComplexElements(ElementType origComplexType, ElementType copyComplexType, String simpleClassName) {
		switch (MiscellaneousTypes.valueOf(simpleClassName.substring(0, simpleClassName.lastIndexOf("Type")))) {
		case AppendixType:
			AppendixTypeType.ComplexElements appendixComplexElements = ((AppendixTypeType) origComplexType)
					.getComplexElements();
			if (appendixComplexElements != null) {
				List<Object> refs = appendixComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				if (refs != null) {
					AppendixTypeType.ComplexElements copyComplexElements = objectFactory
							.createAppendixTypeTypeComplexElements();
					List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
					for (Object item : refs) {
						copyRefs.add(item);
					}
					((AppendixTypeType) copyComplexType).setComplexElements(copyComplexElements);
				}
			}
			break;
		case ElementCondition:
			ElementConditionType.ComplexElements elementConditionComplexElements = ((ElementConditionType) origComplexType)
					.getComplexElements();
			if (elementConditionComplexElements != null) {
				List<Object> refs = elementConditionComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				if (refs != null) {
					ElementConditionType.ComplexElements copyComplexElements = objectFactory
							.createElementConditionTypeComplexElements();
					List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
					for (Object item : refs) {
						copyRefs.add(item);
					}
					((ElementConditionType) copyComplexType).setComplexElements(copyComplexElements);
				}
			}
			break;
		case GroupType:
			break;
		case OrganisationType:
			OrganisationTypeType.ComplexElements organisationComplexElements = ((OrganisationTypeType) origComplexType)
					.getComplexElements();
			if (organisationComplexElements != null) {
				List<Object> refs = organisationComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				if (refs != null) {
					OrganisationTypeType.ComplexElements copyComplexElements = objectFactory
							.createOrganisationTypeTypeComplexElements();
					List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
					for (Object item : refs) {
						copyRefs.add(item);
					}
					((OrganisationTypeType) copyComplexType).setComplexElements(copyComplexElements);
				}
			}
			break;
		case PersonType:
			PersonTypeType.ComplexElements personComplexElements = ((PersonTypeType) origComplexType)
					.getComplexElements();
			if (personComplexElements != null) {
				List<Object> refs = personComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				if (refs != null) {
					PersonTypeType.ComplexElements copyComplexElements = objectFactory
							.createPersonTypeTypeComplexElements();
					List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
					for (Object item : refs) {
						copyRefs.add(item);
					}
					((PersonTypeType) copyComplexType).setComplexElements(copyComplexElements);
				}
			}
			break;
		case ProjectType:
			ProjectTypeType.ComplexElements projectComplexElements = ((ProjectTypeType) origComplexType)
					.getComplexElements();
			if (projectComplexElements != null) {
				List<Object> refs = projectComplexElements.getComplexElementTypeOrComplexElementTypeRef();
				if (refs != null) {
					ProjectTypeType.ComplexElements copyComplexElements = objectFactory
							.createProjectTypeTypeComplexElements();
					List<Object> copyRefs = copyComplexElements.getComplexElementTypeOrComplexElementTypeRef();
					for (Object item : refs) {
						copyRefs.add(item);
					}
					((ProjectTypeType) copyComplexType).setComplexElements(copyComplexElements);
				}
			}
			break;
		case TransactionPhaseType:
			break;
		default:
			break;
		}
	}

	private void copySimpleElement(ElementConditionType origElementCondition,
			ElementConditionType copyElementCondition) {
		ElementConditionType.SimpleElement simpleElement = origElementCondition.getSimpleElement();
		if (simpleElement != null) {
			SimpleElementTypeType simpleElementType = simpleElement.getSimpleElementType();
			if (simpleElementType == null) {
				simpleElementType = (SimpleElementTypeType) simpleElement.getSimpleElementTypeRef().getIdref();
			}
			if (simpleElementType != null) {
				ElementConditionType.SimpleElement copySimpleElement = objectFactory
						.createElementConditionTypeSimpleElement();
				copySimpleElement.setSimpleElementType(simpleElementType);
			}
		}
	}

	public void deleteElement() {
		int selectedMiscellaneousRow = tbl_Elements.getSelectedRow();
		selectedMiscellaneousRow = tbl_Elements.getRowSorter().convertRowIndexToModel(selectedMiscellaneousRow);
		ElementType elementType = elementsTableModel.get(selectedMiscellaneousRow);

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + elementType.getId(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

		String className = elementType.getClass().getSimpleName();
		MiscellaneousTypes type = MiscellaneousTypes.valueOf(className.substring(0, className.length() - 4));
		List<Object> mittElements = null;
		switch (type) {
		case AppendixType:
			break;
		case ElementCondition:
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
		boolean isElementConditionType = selectedElement instanceof ElementConditionType;
		btn_CopyElement.setEnabled(rowSelected);
		btn_DeleteElement.setEnabled(rowSelected);
		tfd_Id.setEnabled(rowSelected);
		tfd_Description.setEnabled(rowSelected);
		if (rowSelected && isElementConditionType) {
			startDateField.setDate(null);
			endDateField.setDate(null);
			tfd_State.setText(null);
			tfd_DateLamu.setText(null);
			tfd_UserLamu.setText(null);
			tfd_Language.setText(null);
			tfd_Category.setText(null);
		}
		lbl_StartDate.setEnabled(rowSelected && !isElementConditionType);
		startDateField.setEnabled(rowSelected && !isElementConditionType);
		lbl_EndDate.setEnabled(rowSelected && !isElementConditionType);
		endDateField.setEnabled(rowSelected && !isElementConditionType);
		lbl_State.setEnabled(rowSelected && !isElementConditionType);
		tfd_State.setEnabled(rowSelected && !isElementConditionType);
		lbl_DateLamu.setEnabled(rowSelected && !isElementConditionType);
		tfd_DateLamu.setEnabled(rowSelected && !isElementConditionType);
		lbl_UserLamu.setEnabled(rowSelected && !isElementConditionType);
		tfd_UserLamu.setEnabled(rowSelected && !isElementConditionType);
		lbl_Language.setEnabled(rowSelected && !isElementConditionType);
		tfd_Language.setEnabled(rowSelected && !isElementConditionType);
		lbl_Category.setEnabled(rowSelected && !isElementConditionType);
		tfd_Category.setEnabled(rowSelected && !isElementConditionType);
		tfd_HelpInfo.setEnabled(rowSelected);
		tfd_Code.setEnabled(rowSelected && !isElementConditionType);
		lbl_Code.setVisible(!isGroupType && !isElementConditionType);
		tfd_Code.setVisible(!isGroupType && !isElementConditionType);
		tfd_Code.getParent().invalidate();
		tfd_Namespace.setEnabled(rowSelected);
		lbl_Namespace.setVisible(isProjectType);
		tfd_Namespace.setVisible(isProjectType);
		tfd_Namespace.getParent().invalidate();
		if (rowSelected && !(isGroupType || isTransactionPhaseType || isElementConditionType)) {
			cl.show(cards, "relationsPanel");
		} else if (rowSelected && isElementConditionType) {
			cl.show(cards, "elementConditionPanel");
		} else {
			cl.show(cards, "emptyPanel");
		}
		if (rowSelected) {
			complexElementsTableModel.clear();
			tfd_Id.setText(selectedElement.getId());
			List<Object> ceList = null;
			if (selectedElement instanceof ElementConditionType) {
				ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
				tfd_Description.setText(elementConditionType.getDescription());
				tfd_HelpInfo.setText(elementConditionType.getHelpInfo());
				cbx_Condition.setSelectedItem(elementConditionType.getCondition());
				List<ComplexElementTypeType> complexElements2 = getComplexElements(elementConditionType);
				tfd_ComplexElement1.setText(
						complexElements2 != null ? ((ComplexElementTypeType) complexElements2.get(0)).getDescription()
								: null);
				tfd_ComplexElement1.setToolTipText(
						complexElements2 != null ? ((ComplexElementTypeType) complexElements2.get(0)).getId() : null);
				btn_RemoveComplexElementType1.setEnabled(complexElements2 != null);
				btn_NavigateComplexElementType1.setEnabled(complexElements2 != null);
				tfd_ComplexElement2.setText(complexElements2 != null && complexElements2.size() > 1
						? ((ComplexElementTypeType) complexElements2.get(1)).getDescription()
						: null);
				tfd_ComplexElement2.setToolTipText(complexElements2 != null && complexElements2.size() > 1
						? ((ComplexElementTypeType) complexElements2.get(1)).getId()
						: null);
				btn_NavigateComplexElementType2.setEnabled(complexElements2 != null && complexElements2.size() > 1);
				btn_RemoveComplexElementType2.setEnabled(complexElements2 != null && complexElements2.size() > 1);
				SimpleElementTypeType simpleElement = getSimpleElement(elementConditionType);
				tfd_SimpleElement.setText(simpleElement != null ? simpleElement.getDescription() : null);
				tfd_SimpleElement.setToolTipText(simpleElement != null ? simpleElement.getId() : null);
				btn_NavigateSimpleElementType.setEnabled(simpleElement != null);
				btn_RemoveSimpleElementType.setEnabled(simpleElement != null);
				MessageInTransactionTypeType mitt = getMessageInTransaction(elementConditionType);
				tfd_MittId.setText(mitt != null ? mitt.getId() : null);
				btn_NavigateMitt.setEnabled(mitt != null);
				btn_NavigateTransactionType.setEnabled(mitt != null);
				btn_NavigateMessageType.setEnabled(mitt != null);
				btn_RemoveMitt.setEnabled(mitt != null);
				if (mitt != null) {
					TransactionTypeType transaction = getTransaction(mitt);
					tfd_Transaction.setText(transaction.getDescription());
					tfd_Transaction.setToolTipText(transaction.getId());
					MessageTypeType message = getMessage(mitt);
					tfd_Message.setText(message.getDescription());
					tfd_Message.setToolTipText(message.getId());
				} else {
					tfd_Transaction.setText(null);
					tfd_Transaction.setToolTipText(null);
					tfd_Message.setText(null);
					tfd_Message.setToolTipText(null);
				}
			} else if (selectedElement instanceof ProjectTypeType) {
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

	public void setComplexElementType1() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		List<ComplexElementTypeType> elements = null;
		List<String> items = new ArrayList<>();
		MessageInTransactionTypeType mitt = getMessageInTransaction(elementConditionType);
		if (mitt != null) {
			MessageTypeType message = getMessage(mitt);
			List<ComplexElementTypeType> complexElements1 = getComplexElements(message);
			if (complexElements1 != null) {
				for (ComplexElementTypeType pce : complexElements1) {
					if (elements == null) {
						elements = new ArrayList<>();
					}
					if (!elements.contains(pce)) {
						elements.add(pce);
					}
					List<ComplexElementTypeType> complexElements2 = getComplexElements(pce);
					if (complexElements2 != null) {
						for (ComplexElementTypeType cce : complexElements2) {
							if (!elements.contains(cce)) {
								elements.add(cce);
							}
						}
					}
				}
			}
		} else {
			elements = Editor16.getStore16().getElements(ComplexElementTypeType.class);
		}
		if (elements != null) {
			for (ComplexElementTypeType element : elements) {
				items.add(element.getDescription() + " [" + element.getId() + "]");
			}
			Collections.sort(items);
		}
		SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(getPanel())),
				getBundle().getString("lbl_ComplexElement1"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String elementId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				ComplexElementTypeType ce = Editor16.getStore16().getElement(ComplexElementTypeType.class, elementId);
				setElementConditionTypeComplexElement1(elementConditionType, ce);
				updateSelectionArea(null);
			}
		});
	}

	public void removeComplexElementType1() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		ComplexElementTypeType complexElement1 = Control16.getElementConditionTypeComplexElement1(elementConditionType);
		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + complexElement1.getDescription(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response != JOptionPane.CANCEL_OPTION) {
			setElementConditionTypeComplexElement1(elementConditionType, null);
			updateSelectionArea(null);
		}
	}

	public void navigateComplexElementType1() {
		String idref = tfd_ComplexElement1.getToolTipText();
		ComplexElementTypeType element = Editor16.getStore16().getElement(ComplexElementTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}

	public void setComplexElementType2() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		List<String> items = new ArrayList<>();
		ComplexElementTypeType complexElement1 = getElementConditionTypeComplexElement1(elementConditionType);
		List<ComplexElementTypeType> elements = getComplexElements(complexElement1);
		if (elements != null) {
			for (ComplexElementTypeType element : elements) {
				items.add(element.getDescription() + " [" + element.getId() + "]");
			}
			Collections.sort(items);
		}
		SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(getPanel())),
				getBundle().getString("lbl_ComplexElement2"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String elementId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				ComplexElementTypeType ce = Editor16.getStore16().getElement(ComplexElementTypeType.class, elementId);
				setElementConditionTypeComplexElement2(elementConditionType, ce);
				updateSelectionArea(null);
			}
		});
	}

	public void removeComplexElementType2() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		ComplexElementTypeType complexElement2 = Control16.getElementConditionTypeComplexElement2(elementConditionType);
		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + complexElement2.getDescription(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response != JOptionPane.CANCEL_OPTION) {
			Control16.setElementConditionTypeComplexElement2(elementConditionType, null);
			updateSelectionArea(null);
		}
	}

	public void navigateComplexElementType2() {
		String idref = tfd_ComplexElement2.getToolTipText();
		ComplexElementTypeType element = Editor16.getStore16().getElement(ComplexElementTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}

	public void setSimpleElementType() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		List<String> items = new ArrayList<>();
		List<SimpleElementTypeType> elements = new ArrayList<>();

		ComplexElementTypeType complexElement1 = getElementConditionTypeComplexElement1(elementConditionType);
		if (complexElement1 != null) {
			elements = getSimpleElements(complexElement1);
			ComplexElementTypeType complexElement2 = getElementConditionTypeComplexElement2(elementConditionType);
			if (complexElement2 != null) {
				List<SimpleElementTypeType> simpleElements2 = getSimpleElements(complexElement2);
				if (simpleElements2 != null) {
					for (SimpleElementTypeType simpleElement : simpleElements2) {
						if (elements == null)
							elements = new ArrayList<>();
						elements.add(simpleElement);
					}
				}
			} else {
				List<ComplexElementTypeType> complexElements = getComplexElements(complexElement1);
				if (complexElements != null) {
					for (ComplexElementTypeType ce : complexElements) {
						List<SimpleElementTypeType> simpleElements = getSimpleElements(ce);
						for (SimpleElementTypeType simpleElement : simpleElements) {
							if (elements == null)
								elements = new ArrayList<>();
							elements.add(simpleElement);
						}
					}
				}
			}
		} else {
			elements = Editor16.getStore16().getElements(SimpleElementTypeType.class);
		}
		if (elements != null) {
			for (SimpleElementTypeType element : elements) {
				items.add(element.getDescription() + " [" + element.getId() + "]");
			}
			Collections.sort(items);
		}
		SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(getPanel())),
				getBundle().getString("lbl_SimpleElement"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String elementId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				SimpleElementTypeType se = Editor16.getStore16().getElement(SimpleElementTypeType.class, elementId);
				Control16.setElementConditionTypeSimpleElement(elementConditionType, se);
				updateSelectionArea(null);
			}
		});
	}

	public void removeSimpleElementType() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		SimpleElementTypeType simpleElement = Control16.getElementConditionTypeSimpleElement(elementConditionType);
		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + simpleElement.getDescription(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response != JOptionPane.CANCEL_OPTION) {
			Control16.setElementConditionTypeSimpleElement(elementConditionType, null);
			updateSelectionArea(null);
		}
	}

	public void navigateSimpleElementType() {
		String idref = tfd_SimpleElement.getToolTipText();
		SimpleElementTypeType element = Editor16.getStore16().getElement(SimpleElementTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}

	public void navigateTransactionType() {
		String idref = tfd_Transaction.getToolTipText();
		TransactionTypeType element = Editor16.getStore16().getElement(TransactionTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
		String mittId = tfd_MittId.getText();
		MessageInTransactionTypeType mitt = Editor16.getStore16().getElement(MessageInTransactionTypeType.class,
				mittId);
		TransactionsPanelControl16 panelControl = (TransactionsPanelControl16) Tabs.Transactions.getPanelControl();
		int index = panelControl.getMessagesTableModel().elements.indexOf(mitt);
		panelControl.tbl_Messages.getSelectionModel().setSelectionInterval(index, index);
		panelControl.tbl_Messages.scrollRectToVisible(panelControl.tbl_Messages.getCellRect(selectedRow, 0, true));
		panelControl.transactionTabs.setSelectedIndex(TransactionTabs.Messages.ordinal());
	}

	public void navigateMessageType() {
		String idref = tfd_Message.getToolTipText();
		MessageTypeType element = Editor16.getStore16().getElement(MessageTypeType.class, idref);
		InteractionFrameworkEditor.navigate(element);
	}

	public void setMitt() {
		final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		List<String> items = new ArrayList<>();
		ComplexElementTypeType complexElement1 = getElementConditionTypeComplexElement1(elementConditionType);
		SimpleElementTypeType simpleElement = getElementConditionTypeSimpleElement(elementConditionType);
		List<MessageInTransactionTypeType> elements = Editor16.getStore16()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType element : elements) {
			TransactionTypeType transaction = getTransaction(element);
			MessageTypeType message = getMessage(element);
			if (complexElement1 != null) {
				List<ComplexElementTypeType> complexElements = getComplexElements(message);
				if (complexElements != null && complexElements.contains(complexElement1)) {
					items.add(transaction.getDescription() + " : " + message.getDescription() + " [" + element.getId()
							+ "]");
				}
			} else if (simpleElement != null) {
				List<ElementType> useElements = getUseElements(simpleElement);
				if (useElements != null) {
					for (ElementType useElement : useElements) {
						if (useElement instanceof ComplexElementTypeType) {
							List<ComplexElementTypeType> complexElements = getComplexElements(message);
							if (complexElements != null && complexElements.contains(useElement)) {
								items.add(transaction.getDescription() + " : " + message.getDescription() + " ["
										+ element.getId() + "]");
							}
						}
					}
				}
			} else {
				items.add(
						transaction.getDescription() + " : " + message.getDescription() + " [" + element.getId() + "]");
			}
		}
		Collections.sort(items);
		SelectBox selectBox = new SelectBox(((JFrame) SwingUtilities.getRoot(getPanel())),
				getBundle().getString("lbl_Mitt"), items);
		selectBox.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String result = (String) evt.getNewValue();
				int lastOpenBracket = result.lastIndexOf('[');
				int lastCloseBracket = result.lastIndexOf(']');
				String elementId = result.substring(lastOpenBracket + 1, lastCloseBracket);
				MessageInTransactionTypeType mitt = Editor16.getStore16().getElement(MessageInTransactionTypeType.class,
						elementId);
				setElementConditionTypeMessageInTransaction(elementConditionType, mitt);
				updateSelectionArea(null);
			}
		});
	}

	public void removeMitt() {
		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": " + tfd_MittId.getText(), getBundle().getString("lbl_Remove"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response != JOptionPane.CANCEL_OPTION) {
			final ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
			setElementConditionTypeMessageInTransaction(elementConditionType, null);
			updateSelectionArea(null);
		}
	}

	public void selectCondition() {
		ElementConditionType elementConditionType = (ElementConditionType) selectedElement;
		String selectedItem = (String) cbx_Condition.getSelectedItem();
		if (selectedItem != null)
			elementConditionType.setCondition(selectedItem);
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
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
		cbx_ComplexElements.setSelectedItem(null);
	}

	public void removeComplexElement() {
		int selectedRow = tbl_ComplexElements.getSelectedRow();

		int response = JOptionPane.showConfirmDialog(getPanel(),
				getBundle().getString("lbl_Remove") + ": "
						+ complexElementsTableModel.elements.get(selectedRow).getId(),
				getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
		if (response == JOptionPane.CANCEL_OPTION)
			return;

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
		updateLaMu(selectedElement, getUser());
		elementsTableModel.update(selectedRow);
	}

}
