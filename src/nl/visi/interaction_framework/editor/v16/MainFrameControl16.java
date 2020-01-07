package nl.visi.interaction_framework.editor.v16;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import nl.visi.interaction_framework.editor.v16.TransactionsPanelControl16.Canvas;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi_1_1a.interaction_framework.importer.Transform;

public class MainFrameControl16 extends Control16 {
	private static final String MAIN_FRAME = "nl/visi/interaction_framework/editor/swixml/MainFrame.xml";
	private JFrame frame;
	private JPanel rolesPanel, transactionsPanel, messagesPanel, complexElementsPanel, simpleElementsPanel,
			userDefinedTypesPanel, miscellaneousPanel;
	private static RolesPanelControl16 rolesPC;
	private static TransactionsPanelControl16 transactionsPC;
	private static MessagesPanelControl16 messagesPC;
	private static ComplexElementsPanelControl16 complexElementsPC;
	private static SimpleElementsPanelControl16 simpleElementsPC;
	private static UserDefinedTypesPanelControl16 userDefinedTypesPC;
	private static MiscellaneousPanelControl16 miscellaneousPC;
	private ExcelReportGenerator16 excelReportGenerator;
	private JFileChooser fileChooser, reportChooser;
	JTabbedPane tabs;
	private JTextField tfd_User;
	private JButton btn_NavigateBackward, btn_NavigateForward, btn_XsdCheck, btn_Print, btn_Report, btn_NewFramework,
			btn_SaveFramework, btn_SaveAsFramework;
	private File frameworkFile, excelFile;
	private String version;

	public static TransactionsPanelControl16 getTransactionsPC() {
		return transactionsPC;
	}

	public static MessagesPanelControl16 getMessagesPC() {
		return messagesPC;
	}

	public static MiscellaneousPanelControl16 getMiscellaneousPC() {
		return miscellaneousPC;
	}

	public static ComplexElementsPanelControl16 getComplexElementsPC() {
		return complexElementsPC;
	}

	enum Tabs {
		Roles, Transactions, Messages, ComplexElements, SimpleElements, UserDefinedTypes, Miscellaneous;

		PanelControl16<?> getPanelControl() {
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

	DefaultHandler defaultHandler = new DefaultHandler() {

		@Override
		public void error(SAXParseException e) throws SAXException {
			JOptionPane.showMessageDialog(frame,
					"Line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() + "\n" + e.getMessage(),
					getBundle().getString("lbl_ValidationError"), JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void fatalError(SAXParseException e) throws SAXException {
			JOptionPane.showMessageDialog(frame,
					"Line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() + "\n" + e.getMessage(),
					getBundle().getString("lbl_ValidationError"), JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void warning(SAXParseException e) throws SAXException {
			JOptionPane.showMessageDialog(frame,
					"Line: " + e.getLineNumber() + ", column: " + e.getColumnNumber() + "\n" + e.getMessage(),
					getBundle().getString("lbl_ValidationWarning"), JOptionPane.WARNING_MESSAGE);
		}
	};

	public MainFrameControl16() throws Exception {
		super();
		frame = (JFrame) render(MAIN_FRAME);
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
//		buildTabs();

		user = userPrefs.get("User", "???");
		tfd_User.setText(user);
		tfd_User.getDocument().addDocumentListener(new DocumentAdapter16() {
			@Override
			protected void update(DocumentEvent e) {
				user = tfd_User.getText();
				userPrefs.put("User", user);
			}
		});
	}

	private void buildTabs() throws Exception {
		rolesPC = new RolesPanelControl16();
		rolesPanel.add(rolesPC.getPanel());
		transactionsPC = new TransactionsPanelControl16();
		transactionsPanel.add(transactionsPC.getPanel());
		messagesPC = new MessagesPanelControl16();
		messagesPanel.add(messagesPC.getPanel());
		complexElementsPC = new ComplexElementsPanelControl16();
		complexElementsPanel.add(complexElementsPC.getPanel());
		simpleElementsPC = new SimpleElementsPanelControl16();
		simpleElementsPanel.add(simpleElementsPC.getPanel());
		userDefinedTypesPC = new UserDefinedTypesPanelControl16();
		userDefinedTypesPanel.add(userDefinedTypesPC.getPanel());
		miscellaneousPC = new MiscellaneousPanelControl16();
		miscellaneousPanel.add(miscellaneousPC.getPanel());
	}

	private Runnable doRun = new Runnable() {
		@Override
		public void run() {
			frame.setVisible(true);
		}
	};

	public void show() {
		try {
			SwingUtilities.invokeAndWait(doRun);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public void newFramework() {
		btn_NewFramework.setEnabled(false);
		try {
			final NewFrameworkDialogControl newFrameworkDialogControl = new NewFrameworkDialogControl();
			newFrameworkDialogControl.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + ": " + evt.getNewValue());
					if (evt.getPropertyName().equals("btn_Create")) {
						if (newFrameworkDialogControl.getVersion().equals("1.6")) {
							ProjectTypeType projectType = objectFactory.createProjectTypeType();
							projectType.setDescription(newFrameworkDialogControl.getDescription());
							projectType.setNamespace(newFrameworkDialogControl.getNamespace());
							try {
								Editor16.getStore16().clear();
								frameworkFile = null;
								btn_SaveFramework.setEnabled(true);
								btn_SaveAsFramework.setEnabled(true);
								btn_XsdCheck.setEnabled(false);
								btn_Print.setEnabled(true);
								btn_Report.setEnabled(true);
								buildTabs();
								getMiscellaneousPC().newElement(projectType, "ProjectType_");
								Tabs.values()[tabs.getSelectedIndex()].getPanelControl().fillTable();
								tabs.repaint();
								MainFrameControl16.this.setMainframeText(projectType.getId());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					btn_NewFramework.setEnabled(true);
				}
			});
			newFrameworkDialogControl.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openFramework() throws IOException {
		if (fileChooser == null) {
			fileChooser = new JFileChooser();
		}
		fileChooser.setDialogTitle(getBundle().getString("lbl_OpenFramework"));
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
		fileChooser.setFileFilter(filter);
		String defaultFilePath = userPrefs.get("FrameworkFile", "");
		if (!defaultFilePath.equals("")) {
			fileChooser.setSelectedFile(new File(defaultFilePath));
		}
		int returnVal = fileChooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			frameworkFile = fileChooser.getSelectedFile();

			checkOnVersion(frameworkFile);
			if (version.equals("1.6")) {
				userPrefs.put("FrameworkFile", frameworkFile.getAbsolutePath());
				InputSource schema = new InputSource(new FileInputStream("_3_16.xsd"));
				btn_SaveFramework.setEnabled(true);
				btn_SaveAsFramework.setEnabled(true);
				btn_XsdCheck.setEnabled(true);
				btn_Print.setEnabled(true);
				btn_Report.setEnabled(true);
				try {
					Editor16.getLoader16().validate(schema, frameworkFile, defaultHandler);
					Editor16.getLoader16().load(schema, frameworkFile);
					buildTabs();
					Tabs.values()[tabs.getSelectedIndex()].getPanelControl().fillTable();
					tabs.repaint();
				} catch (SAXParseException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (version.equals("1.2")) {
				userPrefs.put("FrameworkFile", frameworkFile.getAbsolutePath());
				InputSource schema = new InputSource(new FileInputStream("_3_12.xsd"));
				btn_SaveFramework.setEnabled(true);
				btn_SaveAsFramework.setEnabled(true);
				btn_XsdCheck.setEnabled(true);
				btn_Print.setEnabled(true);
				btn_Report.setEnabled(true);
				try {
					Editor16.getLoader16().validate(schema, frameworkFile, defaultHandler);
					Editor16.getLoader16().load(schema, frameworkFile);
				} catch (SAXParseException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Tabs.values()[tabs.getSelectedIndex()].getPanelControl().fillTable();
			}
			setMainframeText(frameworkFile.getName());
		}

	}

	private String checkOnVersion(final File frameworkFile) {
		version = "?";
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(frameworkFile, new DefaultHandler() {
				@Override
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {
					if (qName.equals("visiXML_VISI_Systematics")) {
						for (int index = 0; index < attributes.getLength(); index++) {
							String attrName = attributes.getLocalName(index);
							if (attrName.equals("xmlns")) {
								String attrValue = attributes.getValue(index);
								if (attrValue.equals("http://www.visi.nl/schemas/20160331")) {
									// 1.6 framework
									version = "1.6";
								} else if (attrValue.equals("http://www.visi.nl/schemas/20071218")) {
									// 1.2 framework
									version = "1.2";
								} else if (attrValue.equals("http://www.visi.nl/schemas/20070406")
										|| attrValue.equals("http://www.visi.nl/schemas/20060807")) {
									// 1.1 framework
									version = "1.1";
									String warningMessage = getBundle().getString("lbl_WarningMessage");
									String olderVersion = getBundle().getString("msg_OlderVersion");
									int confirmDialog = JOptionPane.showConfirmDialog(frame, olderVersion,
											warningMessage, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
									if (confirmDialog == JOptionPane.YES_OPTION) {
										saveAsFramework();
										nl.visi_1_1a.interaction_framework.importer.Loader loader = new nl.visi_1_1a.interaction_framework.importer.Loader();
										loader.load(new File("_3_1_1_3.xsd"), frameworkFile);
										try {
											PrintStream stream = new PrintStream(MainFrameControl16.this.frameworkFile);
											Transform.getTransform().transform(stream);
											version = "1.2";
										} catch (JAXBException e) {
											e.printStackTrace();
										} catch (FileNotFoundException e) {
											e.printStackTrace();
										}
									}
								} else {
									// unrecognized framework
									version = "?";
									String errorMessage = getBundle().getString("lbl_ErrorMessage");
									String unknownVersion = getBundle().getString("msg_UnknownVersion");
									JOptionPane.showMessageDialog(frame, unknownVersion, errorMessage,
											JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					} else {
						super.startElement(uri, localName, qName, attributes);
					}
				}
			});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return version;
	}

	public void saveFramework() {
		try {
			if (frameworkFile == null) {
				if (fileChooser == null) {
					fileChooser = new JFileChooser();
				}
				fileChooser.setDialogTitle(getBundle().getString("lbl_SaveFramework"));
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
				fileChooser.setFileFilter(filter);
				String defaultFilePath = userPrefs.get("FrameworkFile", "");
				if (!defaultFilePath.equals("")) {
					fileChooser.setSelectedFile(new File(defaultFilePath));
				}
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					frameworkFile = fileChooser.getSelectedFile();
				}
			}
			if (frameworkFile != null) {
				Editor16.getLoader16().marshal(new PrintStream(frameworkFile));
				userPrefs.put("FrameworkFile", frameworkFile.getAbsolutePath());
				setMainframeText(frameworkFile.getName());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(fileChooser, e.getMessage(), "Exception handling", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public void saveAsFramework() {
		frameworkFile = null;
		saveFramework();
	}

	public void report() {
		if (excelReportGenerator == null) {
			excelReportGenerator = new ExcelReportGenerator16(this);
		}
		if (reportChooser == null) {
			reportChooser = new JFileChooser();
		}
		reportChooser.setDialogTitle(getBundle().getString("lbl_SaveExcelReport"));
		reportChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xls");
		reportChooser.setFileFilter(filter);
		String defaultExcelPath = userPrefs.get("ExcelFile", "");
		if (!defaultExcelPath.equals("")) {
			excelFile = new File(defaultExcelPath);
			reportChooser.setSelectedFile(excelFile);
		}
		int returnVal = reportChooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			excelFile = reportChooser.getSelectedFile();
			userPrefs.put("ExcelFile", excelFile.getAbsolutePath());
		}

		try {
			excelReportGenerator.writeReport(excelFile);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new PrintRolesDiagram());
		boolean doPrint = job.printDialog();
		if (doPrint) {
			try {
				job.print();
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
	}

	class PrintTransactionsDiagram implements Printable {

		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			// We have only one page, and 'page'
			// is zero-based
			tabs.setSelectedIndex(Tabs.Transactions.ordinal());
			int rowCount = transactionsPC.tbl_Elements.getRowCount();
			if (pageIndex > rowCount - 1) {
				return NO_SUCH_PAGE;
			}
			transactionsPC.tbl_Elements.getSelectionModel().setSelectionInterval(pageIndex, pageIndex);

			// User (0,0) is typically outside the
			// imageable area, so we must translate
			// by the X and Y values in the PageFormat
			// to avoid clipping.
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			g2d.scale(0.8, 0.8);

			// Now we perform our rendering
			// graphics.drawString("Hello world!", 100, 100);
			nl.visi.interaction_framework.editor.v16.TransactionsPanelControl16.Canvas transactionsDrawingPlane = transactionsPC
					.getDrawingPlane();
			transactionsDrawingPlane.paintComponent(graphics);

			// tell the caller that this page is part
			// of the printed document
			return PAGE_EXISTS;
		}
	}

	class PrintRolesDiagram implements Printable {
		private static final int LINES_PER_PAGE = 50;
		private List<PrintPage> pageList;

		class PrintPage {
			private final int elementIndex;
			private final int startLine;

			public PrintPage(int elementIndex, int startLine) {
				this.elementIndex = elementIndex;
				this.startLine = startLine;
			}
		}

		public PrintRolesDiagram() {
			this.pageList = new ArrayList<>();
			tabs.setSelectedIndex(Tabs.Roles.ordinal());
			int elementCount = rolesPC.tbl_Elements.getRowCount();
			for (int elementIndex = 0; elementIndex < elementCount; elementIndex++) {
				int messageCount = getMessageCount(elementIndex);
				int pagesPerElement = (int) (Math.floor(messageCount / LINES_PER_PAGE) + 1);
				int startLine = 0;
				for (int page = 0; page < pagesPerElement; page++) {
					pageList.add(new PrintPage(elementIndex, startLine));
					startLine += LINES_PER_PAGE;
				}
			}
		}

		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex > pageList.size() - 1) {
				System.out.println("NO_SUCH_PAGE");
				return NO_SUCH_PAGE;
			}

			nl.visi.interaction_framework.editor.v16.RolesPanelControl16.Canvas rolesDrawingPlane = rolesPC
					.getDrawingPlane();
			rolesDrawingPlane.setSize((int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			g2d.scale(0.8, 0.8);
			getMessageCount(pageList.get(pageIndex).elementIndex);
			rolesDrawingPlane.setCurrentRole(null);
			rolesDrawingPlane.print(graphics, pageList.get(pageIndex).startLine, LINES_PER_PAGE);

			System.out.println("PAGE_EXISTS");
			return PAGE_EXISTS;
		}

		private int getMessageCount(int elementIndex) {
			rolesPC.tbl_Elements.getSelectionModel().setSelectionInterval(elementIndex, elementIndex);
			rolesPC.selectedElement = rolesPC.elementsTableModel.get(elementIndex);
			rolesPC.fillMessagesTable();
			return rolesPC.tbl_Messages.getRowCount();
		}
	}

	public void xsdCheck() {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			InputSource schema = new InputSource(classLoader.getResourceAsStream("_3.xsd"));
			Editor16.getLoader16().validate(schema, frameworkFile, defaultHandler);
		} catch (SAXParseException e) {
		}
	}

	private Stack<ElementType> forwardStack = new Stack<ElementType>();
	private Stack<ElementType> backwardStack = new Stack<ElementType>();

	public void navigateForward() {
		int tabIndex = tabs.getSelectedIndex();
		PanelControl16<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		backwardStack.push(panelControl.selectedElement);
		ElementType et = forwardStack.pop();
		navExec(et);
	}

	public void navigateBackward() {
		int tabIndex = tabs.getSelectedIndex();
		PanelControl16<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		forwardStack.push(panelControl.selectedElement);
		ElementType et = backwardStack.pop();
		navExec(et);
	}

	public void navigate(ElementType element) {
		int tabIndex = tabs.getSelectedIndex();
		PanelControl16<?> panelControl = Tabs.values()[tabIndex].getPanelControl();
		backwardStack.push(panelControl.selectedElement);
		forwardStack.clear();
		navExec(element);
	}

	private void navExec(ElementType element) {
		System.out.println(element.getId());
		PanelControl16<?> panelControl = null;
		switch (Store16.ElementTypeType.valueOf(element.getClass().getSimpleName())) {
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
			btn_NavigateBackward.setEnabled(!backwardStack.isEmpty());
			btn_NavigateForward.setEnabled(!forwardStack.isEmpty());
		}
	}

	private void setMainframeText(String text) {
		String prefix = getBundle().getString("lbl_InteractionFrameworkEditor");
		frame.setTitle(prefix + " - " + text);
	}
}
