package nl.visi.interaction_framework.editor.v14;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
	int tabsIndex = 0;
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

		JFrame tearOffFrame;

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

		JFrame getTearOffFrame() {
			if (tearOffFrame == null) {
				tearOffFrame = new JFrame(getBundle().getString("lbl_" + name()));
			}
			return tearOffFrame;
		}
	}

	public MainPanelControl14() throws Exception {
		mainPanel = (JPanel) render(MAIN_PANEL);
		tabs.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				tabsIndex = tabs.getSelectedIndex();
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

		tabs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					tabs.setEnabledAt(tabs.getSelectedIndex(), false);
					((JComponent) tabs.getSelectedComponent()).removeAll();
					tabs.getSelectedComponent().repaint();
//					JFrame frame = new JFrame();
					JFrame frame = Tabs.values()[tabs.getSelectedIndex()].getTearOffFrame();
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					tearOff(frame, Tabs.values()[tabs.getSelectedIndex()]);
					frame.pack();
					frame.setVisible(true);
				}
			}

			private void tearOff(JFrame frame, final Tabs tab) {
				frame.setTitle(getBundle().getString("lbl_" + tab.name()));
				frame.add(tab.getPanelControl().getPanel());
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						super.windowClosing(e);
						JComponent tabComponent = (JComponent) tabs.getComponentAt(tab.ordinal());
						tabComponent.add(tab.getPanelControl().getPanel());
						tabs.setEnabledAt(tab.ordinal(), true);
						tab.tearOffFrame = null;
					}
				});
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
			return miscellaneousPC.newElement(projectType, "ProjectType_");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void openFramework(File frameworkFile, DefaultHandler defaultHandler) {
		try {
//			InputSource schema = new InputSource(new FileInputStream("_3_14.xsd"));
			InputSource schema = new InputSource(
					getClass().getClassLoader().getResourceAsStream("main/resource/20140331.xsd"));
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

	class PrintDiagrams implements Printable {
		private static final int LINES_PER_PAGE = 50;
		private List<PrintPage> pageList;
		private int transactionElementCount = 0;

		class PrintPage {
			private final int elementIndex;
			private final int startLine;

			public PrintPage(int elementIndex, int startLine) {
				this.elementIndex = elementIndex;
				this.startLine = startLine;
			}
		}

		public PrintDiagrams() {
			this.pageList = new ArrayList<>();
			tabs.setSelectedIndex(Tabs.Roles.ordinal());
			int roleElementCount = rolesPC.tbl_Elements.getRowCount();
			for (int elementIndex = 0; elementIndex < roleElementCount; elementIndex++) {
				int messageCount = getMessageCount(elementIndex);
				int pagesPerElement = (int) (Math.floor(messageCount / LINES_PER_PAGE) + 1);
				int startLine = 0;
				for (int page = 0; page < pagesPerElement; page++) {
					pageList.add(new PrintPage(elementIndex, startLine));
					startLine += LINES_PER_PAGE;
				}
			}
			tabs.setSelectedIndex(Tabs.Transactions.ordinal());
			transactionElementCount = transactionsPC.tbl_Elements.getRowCount();
		}

		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex > pageList.size() + transactionElementCount - 1) {
				return NO_SUCH_PAGE;
			}

			if (pageIndex < pageList.size()) {
				RolesPanelControl14.Canvas rolesDrawingPlane = rolesPC.getDrawingPlane();
				rolesDrawingPlane.setSize((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
				Graphics2D g2d = (Graphics2D) graphics;
				g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
				g2d.scale(0.8, 0.8);
				getMessageCount(pageList.get(pageIndex).elementIndex);
				rolesDrawingPlane.setCurrentRole(null);
				rolesDrawingPlane.print(graphics, pageList.get(pageIndex).startLine, LINES_PER_PAGE);

				return PAGE_EXISTS;
			} else {
				transactionsPC.tbl_Elements.getSelectionModel().setSelectionInterval(pageIndex - pageList.size(),
						pageIndex - pageList.size());

				// User (0,0) is typically outside the
				// imageable area, so we must translate
				// by the X and Y values in the PageFormat
				// to avoid clipping.
				Graphics2D g2d = (Graphics2D) graphics;
				g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
				g2d.scale(0.8, 0.8);

				// Now we perform our rendering
				// graphics.drawString("Hello world!", 100, 100);
				TransactionsPanelControl14.Canvas transactionsDrawingPlane = transactionsPC.getDrawingPlane();
				transactionsDrawingPlane.print(graphics);

				// tell the caller that this page is part
				// of the printed document
				return PAGE_EXISTS;
			}
		}

		private int getMessageCount(int elementIndex) {
			rolesPC.tbl_Elements.getSelectionModel().setSelectionInterval(elementIndex, elementIndex);
			rolesPC.selectedElement = rolesPC.elementsTableModel.get(elementIndex);
			rolesPC.fillMessagesTable();
			return rolesPC.tbl_Messages.getRowCount();
		}

	}

	public void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new PrintDiagrams());
		boolean doPrint = job.printDialog();
		if (doPrint) {
			try {
				job.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
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
		PanelControl14<?> panelControl = null;
		switch (Store14.ElementTypeType.valueOf(element.getClass().getSimpleName())) {
		case AppendixTypeType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case ComplexElementTypeType:
			tabsIndex = Tabs.ComplexElements.ordinal();
			if (Tabs.ComplexElements.tearOffFrame != null)
				Tabs.ComplexElements.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.ComplexElements.getPanelControl();
			break;
		case ElementConditionType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case GroupTypeType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case MessageInTransactionTypeConditionType:
			break;
		case MessageInTransactionTypeType:
			break;
		case MessageTypeType:
			tabsIndex = Tabs.Messages.ordinal();
			if (Tabs.Messages.tearOffFrame != null)
				Tabs.Messages.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Messages.getPanelControl();
			break;
		case OrganisationTypeType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case PersonTypeType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case ProjectTypeType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case RoleTypeType:
			tabsIndex = Tabs.Roles.ordinal();
			if (Tabs.Roles.tearOffFrame != null)
				Tabs.Roles.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Roles.getPanelControl();
			break;
		case SimpleElementTypeType:
			tabsIndex = Tabs.SimpleElements.ordinal();
			if (Tabs.SimpleElements.tearOffFrame != null)
				Tabs.SimpleElements.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.SimpleElements.getPanelControl();
			break;
		case TransactionPhaseTypeType:
			tabsIndex = Tabs.Miscellaneous.ordinal();
			if (Tabs.Miscellaneous.tearOffFrame != null)
				Tabs.Miscellaneous.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.Miscellaneous.getPanelControl();
			break;
		case TransactionTypeType:
			tabsIndex = Tabs.Transactions.ordinal();
			if (Tabs.Transactions.tearOffFrame != null) {
				Tabs.Transactions.tearOffFrame.toFront();
			} else {
				tabs.setSelectedIndex(tabsIndex);
			}
			panelControl = Tabs.Transactions.getPanelControl();
			break;
		case UserDefinedTypeType:
			tabsIndex = Tabs.UserDefinedTypes.ordinal();
			if (Tabs.UserDefinedTypes.tearOffFrame != null)
				Tabs.UserDefinedTypes.tearOffFrame.toFront();
			else
				tabs.setSelectedIndex(tabsIndex);
			panelControl = Tabs.UserDefinedTypes.getPanelControl();
			break;
		default:
			break;
		}
		if (panelControl != null) {
			int index = panelControl.elementsTableModel.elements.indexOf(element);
			if (index >= 0) {
				index = panelControl.tbl_Elements.convertRowIndexToView(index);
				panelControl.tbl_Elements.getSelectionModel().setSelectionInterval(index, index);
			}
		} else {
			JOptionPane.showMessageDialog(mainPanel, "No tab available for this navigation link.", "No tab available",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public void navigateForward() {
//		int tabIndex = tabs.getSelectedIndex();
//		PanelControl14<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		PanelControl14<?> panelControl = Tabs.values()[tabsIndex].getPanelControl();
		if (panelControl.selectedElement != null) {
			backwardStack.push(panelControl.selectedElement);
		}
		ElementType et = forwardStack.pop();
		navExec(et);
	}

	public void navigateBackward() {
//		int tabIndex = tabs.getSelectedIndex();
//		PanelControl14<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		PanelControl14<?> panelControl = Tabs.values()[tabsIndex].getPanelControl();
		if (panelControl.selectedElement != null) {
			forwardStack.push(panelControl.selectedElement);
		}
		ElementType et = backwardStack.pop();
		navExec(et);
	}

	public void xsdCheck(File frameworkFile, DefaultHandler defaultHandler)
			throws ParserConfigurationException, SAXException, IOException {
//		InputSource schema = new InputSource(new FileInputStream("_3_14.xsd"));
		InputSource schema = new InputSource(
				getClass().getClassLoader().getResourceAsStream("main/resource/20140331.xsd"));
		Editor14.getLoader14().validate(schema, frameworkFile, defaultHandler);
	}
}
