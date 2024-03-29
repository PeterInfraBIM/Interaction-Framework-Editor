package nl.visi.interaction_framework.editor;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceRoyale;

import nl.visi.interaction_framework.editor.v14.MainPanelControl14;
import nl.visi.interaction_framework.editor.v16.MainPanelControl16;

public class InteractionFrameworkEditor extends Control {
	private static final String TOP_LEVEL = "nl/visi/interaction_framework/editor/swixml/Toplevel.xml";
	private static InteractionFrameworkEditor instance;
	private File frameworkFile;
	private String version;
	private MainPanelControl14 mainPanelControl14;
	private MainPanelControl16 mainPanelControl16;

	private JFrame frame;
	private JButton btn_NewFramework, btn_SaveFramework, btn_SaveAsFramework, btn_Translate14to16, btn_XsdCheck,
			btn_Print, btn_Report, btn_NavigateBackward, btn_NavigateForward;
	private JTextField tfd_User, tfd_Version;
	private JPanel mainPanel;

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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new InteractionFrameworkEditor();
			}
		});

	}

	static void renderSplashFrame(Graphics2D g) {
//		final String version = "Release candidate: 2.16 - release date: 2020-12-12";
//		final String version = "Release: 2.0 - release date: 2020-12-16";
//		final String version = "Release: 2.0.1 - release date: 2021-01-31";
//		final String version = "Release: 2.0.2 - release date: 2021-08-17";
//		final String version = "Release: 2.0.3 - release date: 2021-08-24";
//		final String version = "Release: 2.1.1 - release date: 2022-07-31";
		final String version = "Release: 2.1.2 - release date: 2023-09-27";
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(120, 140, 200, 40);
		g.setPaintMode();
		g.setColor(Color.BLACK);
		g.drawString("Loading " + version + "...", 120, 350);
	}

	public InteractionFrameworkEditor() {
		super();

		instance = this;

		try {
			final SplashScreen splash = SplashScreen.getSplashScreen();
			if (splash == null) {
				System.out.println("SplashScreen.getSplashScreen() returned null");
			} else {
				Graphics2D g = splash.createGraphics();
				if (g == null) {
					System.out.println("g is null");
					return;
				}
				renderSplashFrame(g);
				splash.update();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				splash.close();
			}
			PlasticXPLookAndFeel laf = new PlasticXPLookAndFeel();
			PlasticXPLookAndFeel.setCurrentTheme(new ExperienceRoyale());
			PlasticXPLookAndFeel.setTabStyle(PlasticXPLookAndFeel.TAB_STYLE_METAL_VALUE);
			PlasticXPLookAndFeel.set3DEnabled(true);
			UIManager.setLookAndFeel(laf);
			// FontUIResource font = new FontUIResource("Verdana", Font.PLAIN, 12);
			FontUIResource font = new FontUIResource("Sans serif", Font.PLAIN, 12);
			UIManager.put("Table.font", font);

			frame = (JFrame) render(TOP_LEVEL);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					if (mainPanel.getComponentCount() != 0) {
						int selectedOption = JOptionPane.showConfirmDialog(frame,
								getBundle().getString("lbl_SaveBeforeClosing"));
						if (selectedOption == JOptionPane.YES_OPTION) {
							System.out.println("YES");
							saveFramework();
							// frame.dispose();
							closeAllWindows();
						} else if (selectedOption == JOptionPane.NO_OPTION) {
							System.out.println("NO");
							// frame.dispose();
							closeAllWindows();
						} else if (selectedOption == JOptionPane.CANCEL_OPTION) {
							System.out.println("CANCEL");
						}
					} else {
						frame.dispose();
					}
				}

				private void closeAllWindows() {
					java.awt.Window win[] = java.awt.Window.getWindows();
					for (int i = 0; i < win.length; i++) {
						win[i].dispose();
						win[i] = null;
					}
				}
			});
			frame.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		user = userPrefs.get("User", "???");
		tfd_User.setText(getUser());
		tfd_User.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				user = tfd_User.getText();
				userPrefs.put("User", getUser());
			}
		});
	}

	public void newFramework() {
		if (frameworkFile != null) {
			int selectedOption = JOptionPane.showConfirmDialog(frame, getBundle().getString("lbl_SaveBeforeOpening"));
			switch (selectedOption) {
			case JOptionPane.YES_OPTION:
				saveFramework();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
				return;
			}
		}
		btn_NewFramework.setEnabled(false);
		try {
			final NewFrameworkDialogControl newFrameworkDialogControl = new NewFrameworkDialogControl();
			newFrameworkDialogControl.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getPropertyName().equals("btn_Create")) {
						version = newFrameworkDialogControl.getVersion();
						tfd_Version.setText(version);
						if (version.equals("1.6")) {
							try {
								mainPanel.removeAll();
								mainPanelControl16 = new MainPanelControl16();
								mainPanel.add(mainPanelControl16.getMainPanel());
								mainPanel.revalidate();
								String newProjectId = mainPanelControl16.newFramework(newFrameworkDialogControl);
								btn_SaveFramework.setEnabled(true);
								btn_SaveAsFramework.setEnabled(true);
								btn_Translate14to16.setEnabled(false);
								btn_XsdCheck.setEnabled(false);
								btn_Print.setEnabled(true);
								btn_Report.setEnabled(true);
								setWindowTitle(newProjectId);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(mainPanel, e.getMessage(),
										getBundle().getString("lbl_ErrorMessage"), JOptionPane.ERROR_MESSAGE);
							}
						} else if (newFrameworkDialogControl.getVersion().equals("1.4")) {
							try {
								mainPanel.removeAll();
								mainPanelControl14 = new MainPanelControl14();
								mainPanel.add(mainPanelControl14.getMainPanel());
								mainPanel.revalidate();
								String newProjectId = mainPanelControl14.newFramework(newFrameworkDialogControl);
								btn_SaveFramework.setEnabled(true);
								btn_SaveAsFramework.setEnabled(true);
								btn_Translate14to16.setEnabled(true);
								btn_XsdCheck.setEnabled(false);
								btn_Print.setEnabled(true);
								btn_Report.setEnabled(true);
								setWindowTitle(newProjectId);
							} catch (Exception e) {
								JOptionPane.showMessageDialog(mainPanel, e.getMessage(),
										getBundle().getString("lbl_ErrorMessage"), JOptionPane.ERROR_MESSAGE);
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

	public void openFramework() throws Exception {
		if (frameworkFile != null) {
			int selectedOption = JOptionPane.showConfirmDialog(frame, getBundle().getString("lbl_SaveBeforeOpening"));
			switch (selectedOption) {
			case JOptionPane.YES_OPTION:
				saveFramework();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
				return;
			}
		}
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(getBundle().getString("lbl_OpenFramework"));
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
		fileChooser.setFileFilter(filter);
		fileChooser.setMultiSelectionEnabled(true);
		String defaultFilePath = userPrefs.get("FrameworkFile", "");
		if (!defaultFilePath.equals("")) {
			fileChooser.setSelectedFile(new File(defaultFilePath));
		}
		int returnVal = fileChooser.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			frameworkFile = fileChooser.getSelectedFile();
			checkOnVersion(frameworkFile);
			tfd_Version.setText(version);
			if (version.equals("1.6")) {
				setWindowTitle(frameworkFile.getName());
				userPrefs.put("FrameworkFile", frameworkFile.getAbsolutePath());
				mainPanelControl16 = new MainPanelControl16();
				mainPanel.removeAll();
				mainPanel.add(mainPanelControl16.getMainPanel());
				mainPanel.revalidate();
				mainPanelControl16.openFramework(frameworkFile, defaultHandler);
				btn_SaveFramework.setEnabled(true);
				btn_SaveAsFramework.setEnabled(true);
				btn_Translate14to16.setEnabled(false);
				btn_XsdCheck.setEnabled(true);
				btn_Print.setEnabled(true);
				btn_Report.setEnabled(true);
			} else if (version.equals("1.4")) {
				setWindowTitle(frameworkFile.getName());
				userPrefs.put("FrameworkFile", frameworkFile.getAbsolutePath());
				mainPanelControl14 = new MainPanelControl14();
				mainPanel.removeAll();
				mainPanel.add(mainPanelControl14.getMainPanel());
				mainPanel.revalidate();
				mainPanelControl14.openFramework(frameworkFile, defaultHandler);
				btn_SaveFramework.setEnabled(true);
				btn_SaveAsFramework.setEnabled(true);
				btn_Translate14to16.setEnabled(true);
				btn_XsdCheck.setEnabled(true);
				btn_Print.setEnabled(true);
				btn_Report.setEnabled(true);
			}
		}
		frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
								} else if (attrValue.equals("http://www.visi.nl/schemas/20140331")) {
									// 1.4 framework
									version = "1.4";
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
		JFileChooser fileChooser = new JFileChooser();
		try {
			if (frameworkFile == null) {
				fileChooser.setDialogTitle(getBundle().getString("lbl_SaveFramework"));
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("XML", "xml");
				fileChooser.setFileFilter(filter);
				String defaultFilePath = userPrefs.get("FrameworkFile", "");
				if (!defaultFilePath.equals("")) {
					fileChooser.setSelectedFile(new File(defaultFilePath));
				}
				int returnVal = fileChooser.showSaveDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					frameworkFile = fileChooser.getSelectedFile();
				}
			}
			if (frameworkFile != null) {
				if (version.equals("1.6")) {
					mainPanelControl16.saveFramework(frameworkFile);
				} else if (version.equals("1.4")) {
					mainPanelControl14.saveFramework(frameworkFile);
				}
				userPrefs.put("FrameworkFile", frameworkFile.getAbsolutePath());
				setWindowTitle(frameworkFile.getName());
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

	public void translate14To16() {
		Translator14to16 translator14to16 = new Translator14to16();
		try {
			translator14to16.translate();
			frameworkFile = null;
			version = "1.6";
			tfd_Version.setText(version);

			mainPanelControl16 = new MainPanelControl16();

			saveFramework();

			mainPanel.removeAll();
			mainPanel.add(mainPanelControl16.getMainPanel());
			mainPanel.revalidate();
			mainPanelControl16.openFramework(frameworkFile, defaultHandler);
			btn_SaveFramework.setEnabled(true);
			btn_SaveAsFramework.setEnabled(true);
			btn_Translate14to16.setEnabled(false);
			btn_XsdCheck.setEnabled(true);
			btn_Print.setEnabled(true);
			btn_Report.setEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print() {
		if (version.equals("1.6")) {
			mainPanelControl16.print();
		} else if (version.equals("1.4")) {
			mainPanelControl14.print();
		}
	}

	public void report() {
		File excelFile = null;
		JFileChooser reportChooser = new JFileChooser();
		reportChooser.setDialogTitle(getBundle().getString("lbl_SaveExcelReport"));
		reportChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel", "xls");
		reportChooser.setFileFilter(filter);
		String defaultExcelPath = userPrefs.get("ExcelFile", "");
		if (!defaultExcelPath.equals("")) {
			excelFile = new File(defaultExcelPath);
			reportChooser.setSelectedFile(excelFile);
		}
		int returnVal = reportChooser.showSaveDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			excelFile = reportChooser.getSelectedFile();
			userPrefs.put("ExcelFile", excelFile.getAbsolutePath());
		}

		if (version.equals("1.6")) {
			try {
				mainPanelControl16.report(excelFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else if (version.equals("1.4")) {
			try {
				mainPanelControl14.report(excelFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	public void navigateForward() {
		if (version.equals("1.6")) {
			mainPanelControl16.navigateForward();
			btn_NavigateBackward.setEnabled(!mainPanelControl16.isBackwardStackEmpty());
			btn_NavigateForward.setEnabled(!mainPanelControl16.isForwardStackEmpty());
		} else if (version.equals("1.4")) {
			mainPanelControl14.navigateForward();
			btn_NavigateBackward.setEnabled(!mainPanelControl14.isBackwardStackEmpty());
			btn_NavigateForward.setEnabled(!mainPanelControl14.isForwardStackEmpty());
		}
	}

	public void navigateBackward() {
		if (version.equals("1.6")) {
			mainPanelControl16.navigateBackward();
			btn_NavigateBackward.setEnabled(!mainPanelControl16.isBackwardStackEmpty());
			btn_NavigateForward.setEnabled(!mainPanelControl16.isForwardStackEmpty());
		} else if (version.equals("1.4")) {
			mainPanelControl14.navigateBackward();
			btn_NavigateBackward.setEnabled(!mainPanelControl14.isBackwardStackEmpty());
			btn_NavigateForward.setEnabled(!mainPanelControl14.isForwardStackEmpty());
		}
	}

	public void xsdCheck() {
		if (version.equals("1.6")) {
			try {
				mainPanelControl16.xsdCheck(frameworkFile, defaultHandler);
				JOptionPane.showMessageDialog(frame, getBundle().getString("lbl_FrameworkValidated"),
						getBundle().getString("lbl_Information"), JOptionPane.INFORMATION_MESSAGE);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage(), getBundle().getString("lbl_ErrorMessage"),
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		} else if (version.equals("1.4")) {
			try {
				mainPanelControl14.xsdCheck(frameworkFile, defaultHandler);
				JOptionPane.showMessageDialog(frame, getBundle().getString("lbl_FrameworkValidated"),
						getBundle().getString("lbl_Information"), JOptionPane.INFORMATION_MESSAGE);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				JOptionPane.showMessageDialog(frame, e.getMessage(), getBundle().getString("lbl_ErrorMessage"),
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	private void setWindowTitle(String text) {
		String prefix = getBundle().getString("lbl_InteractionFrameworkEditor");
		frame.setTitle(prefix + " - " + text);
	}

	public static MainPanelControl16 getMainPanelControl16() {
		return instance.mainPanelControl16;
	}

	public static MainPanelControl14 getMainPanelControl14() {
		return instance.mainPanelControl14;
	}

	public static void navigate(Object element) {
		if (element instanceof nl.visi.schemas._20160331.ElementType) {
			instance.mainPanelControl16.navigate((nl.visi.schemas._20160331.ElementType) element);
			instance.btn_NavigateBackward.setEnabled(!instance.mainPanelControl16.isBackwardStackEmpty());
			instance.btn_NavigateForward.setEnabled(!instance.mainPanelControl16.isForwardStackEmpty());
		} else if (element instanceof nl.visi.schemas._20140331.ElementType) {
			instance.mainPanelControl14.navigate((nl.visi.schemas._20140331.ElementType) element);
			instance.btn_NavigateBackward.setEnabled(!instance.mainPanelControl14.isBackwardStackEmpty());
			instance.btn_NavigateForward.setEnabled(!instance.mainPanelControl14.isForwardStackEmpty());
		}
	}

	public static JFrame getApplicationFrame() {
		return instance.frame;
	}
}
