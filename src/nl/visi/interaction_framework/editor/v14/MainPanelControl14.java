package nl.visi.interaction_framework.editor.v14;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Stack;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import nl.visi.interaction_framework.editor.NewFrameworkDialogControl;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.ProjectTypeType;

public class MainPanelControl14 extends Control14 {
	private static final String MAIN_PANEL = "nl/visi/interaction_framework/editor/swixml/MainPanel16.xml";
	private JPanel mainPanel, rolesPanel, transactionsPanel, messagesPanel, complexElementsPanel, simpleElementsPanel,
			userDefinedTypesPanel, miscellaneousPanel;
	JTabbedPane tabs;
	private static ComplexElementsPanelControl14 complexElementsPC;
	private static MessagesPanelControl14 messagesPC;
	private static MiscellaneousPanelControl14 miscellaneousPC;
	private static RolesPanelControl14 rolesPC;
	private static SimpleElementsPanelControl14 simpleElementsPC;
	private static TransactionsPanelControl14 transactionsPC;
	private static UserDefinedTypesPanelControl14 userDefinedTypesPC;

	private Stack<ElementType> forwardStack = new Stack<ElementType>();
	private Stack<ElementType> backwardStack = new Stack<ElementType>();

	public enum Tabs {
		Roles, Transactions, Messages, ComplexElements, SimpleElements, UserDefinedTypes, Miscellaneous;

		PanelControl14<?> getPanelControl() {
			switch (this) {
			case ComplexElements:
				return complexElementsPC;
			case Messages:
				return messagesPC;
			case Roles:
				return rolesPC;
			case SimpleElements:
				return simpleElementsPC;
			case Transactions:
				return transactionsPC;
			case UserDefinedTypes:
				return userDefinedTypesPC;
			case Miscellaneous:
				return miscellaneousPC;
			}
			return null;
		}
	}

	public MainPanelControl14() throws Exception {
		mainPanel = (JPanel) render(MAIN_PANEL);
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				switch (Tabs.values()[tabs.getSelectedIndex()]) {
				case Roles:
					rolesPC.fillTable();
					break;
				case Transactions:
					transactionsPC.fillTable();
					break;
				case Messages:
					messagesPC.fillTable();
					break;
				case ComplexElements:
					complexElementsPC.fillTable();
					break;
				case SimpleElements:
					simpleElementsPC.fillTable();
					break;
				case UserDefinedTypes:
					userDefinedTypesPC.fillTable();
					break;
				case Miscellaneous:
					miscellaneousPC.fillTable();
					break;
				}
			}
		});
	}

	public JPanel getMainPanel() throws Exception {
		buildTabs();
		return mainPanel;
	}

	private void buildTabs() throws Exception {
		rolesPC = new RolesPanelControl14();
		rolesPanel.removeAll();
		rolesPanel.add(rolesPC.getPanel());
		transactionsPC = new TransactionsPanelControl14();
		transactionsPanel.removeAll();
		transactionsPanel.add(transactionsPC.getPanel());
		messagesPC = new MessagesPanelControl14();
		messagesPanel.removeAll();
		messagesPanel.add(messagesPC.getPanel());
		complexElementsPC = new ComplexElementsPanelControl14();
		complexElementsPanel.removeAll();
		complexElementsPanel.add(complexElementsPC.getPanel());
		simpleElementsPC = new SimpleElementsPanelControl14();
		simpleElementsPanel.removeAll();
		simpleElementsPanel.add(simpleElementsPC.getPanel());
		userDefinedTypesPC = new UserDefinedTypesPanelControl14();
		userDefinedTypesPanel.removeAll();
		userDefinedTypesPanel.add(userDefinedTypesPC.getPanel());
		miscellaneousPC = new MiscellaneousPanelControl14();
		miscellaneousPanel.removeAll();
		miscellaneousPanel.add(miscellaneousPC.getPanel());
		tabs.revalidate();
	}

	public String newFramework(NewFrameworkDialogControl newFrameworkDialogControl) {
		ProjectTypeType projectType = objectFactory.createProjectTypeType();
		projectType.setDescription(newFrameworkDialogControl.getDescription());
		projectType.setNamespace(newFrameworkDialogControl.getNamespace());
		try {
			Editor14.getStore14().clear();
//			frameworkFile = null;
//			buildTabs();
			return miscellaneousPC.newElement(projectType, "ProjectType_");
//			Tabs.values()[tabs.getSelectedIndex()].getPanelControl().fillTable();
//			tabs.repaint();
//			MainFrameControl16.this.setMainframeText(projectType.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void openFramework(File frameworkFile, DefaultHandler defaultHandler) {
		try {
			InputSource schema = new InputSource(new FileInputStream("_3_14.xsd"));
			Editor14.getLoader14().validate(schema, frameworkFile, defaultHandler);
			Editor14.getLoader14().load(schema, frameworkFile);
			Tabs.values()[tabs.getSelectedIndex()].getPanelControl().fillTable();
		} catch (SAXParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveFramework(File frameworkFile) throws FileNotFoundException, Exception {
		Editor14.getLoader14().marshal(new PrintStream(frameworkFile));
	}

	public void report(File excelFile) throws IOException {
		ExcelReportGenerator14 excelReportGenerator = new ExcelReportGenerator14(this);
		excelReportGenerator.writeReport(excelFile);
	}

	public static TransactionsPanelControl14 getTransactionsPC() {
		return transactionsPC;
	}

	public static MessagesPanelControl14 getMessagesPC() {
		return messagesPC;
	}

	public static ComplexElementsPanelControl14 getComplexElementsPC() {
		return complexElementsPC;
	}

	public static MiscellaneousPanelControl14 getMiscellaneousPC() {
		return miscellaneousPC;
	}

	public void navigate(ElementType element) {
		int tabIndex = tabs.getSelectedIndex();
		PanelControl14<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		backwardStack.push(panelControl.selectedElement);
		forwardStack.clear();
		navExec(element);
	}

	public boolean isBackwardStackEmpty() {
		return backwardStack.isEmpty();
	}

	public boolean isForwardStackEmpty() {
		return forwardStack.isEmpty();
	}

	private void navExec(ElementType element) {
		System.out.println(element.getId());
		PanelControl14<?> panelControl = null;
		switch (Store14.ElementTypeType.valueOf(element.getClass().getSimpleName())) {
		case AppendixTypeType:
			tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case ComplexElementTypeType:
			tabs.setSelectedIndex(Tabs.ComplexElements.ordinal());
			panelControl = Tabs.ComplexElements.getPanelControl();
			break;
		case ElementConditionType:
			break;
		case GroupTypeType:
			tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case MessageInTransactionTypeConditionType:
			break;
		case MessageInTransactionTypeType:
			break;
		case MessageTypeType:
			tabs.setSelectedIndex(Tabs.Messages.ordinal());
			panelControl = Tabs.Messages.getPanelControl();
			break;
		case OrganisationTypeType:
			tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case PersonTypeType:
			tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case ProjectTypeType:
			tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case RoleTypeType:
			tabs.setSelectedIndex(Tabs.Roles.ordinal());
			panelControl = Tabs.Roles.getPanelControl();
			break;
		case SimpleElementTypeType:
			tabs.setSelectedIndex(Tabs.SimpleElements.ordinal());
			panelControl = Tabs.SimpleElements.getPanelControl();
			break;
		case TransactionPhaseTypeType:
			tabs.setSelectedIndex(Tabs.Miscellaneous.ordinal());
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case TransactionTypeType:
			tabs.setSelectedIndex(Tabs.Transactions.ordinal());
			panelControl = Tabs.Transactions.getPanelControl();
			break;
		case UserDefinedTypeType:
			tabs.setSelectedIndex(Tabs.UserDefinedTypes.ordinal());
			panelControl = Tabs.UserDefinedTypes.getPanelControl();
			break;
		default:
			break;
		}
		if (panelControl != null) {
			int index = panelControl.elementsTableModel.elements.indexOf(element);
			if (index >= 0) {
				panelControl.tbl_Elements.getSelectionModel().setSelectionInterval(index, index);
			}
		}
	}

	public void navigateForward() {
		int tabIndex = tabs.getSelectedIndex();
		PanelControl14<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		backwardStack.push(panelControl.selectedElement);
		ElementType et = forwardStack.pop();
		navExec(et);
	}

	public void navigateBackward() {
		int tabIndex = tabs.getSelectedIndex();
		PanelControl14<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		forwardStack.push(panelControl.selectedElement);
		ElementType et = backwardStack.pop();
		navExec(et);
	}

	public void xsdCheck(File frameworkFile, DefaultHandler defaultHandler)
			throws ParserConfigurationException, SAXException, IOException {
		InputSource schema = new InputSource(new FileInputStream("_3_14.xsd"));
		Editor14.getLoader14().validate(schema, frameworkFile, defaultHandler);
	}
}