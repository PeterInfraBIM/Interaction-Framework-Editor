package nl.visi.interaction_framework.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;

public class NewFrameworkDialogControl extends Control {
	private static final String NEW_FRAMEWORK_DIALOG = "nl/visi/interaction_framework/editor/swixml/NewFrameworkDialog.xml";

	private JDialog dialog;
	private JTextField tfd_Description, tfd_Namespace;
	private JButton btn_Cancel, btn_Create;
	private JRadioButton rdb_V14, rdb_V16;
	private boolean discriptionEmpty = false, namespaceEmpty = false;

	public NewFrameworkDialogControl() throws Exception {
		super();
		dialog = (JDialog) render(NEW_FRAMEWORK_DIALOG);
		JRootPane rootPane = SwingUtilities.getRootPane(btn_Create);
		rootPane.setDefaultButton(btn_Create);
		tfd_Description.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				discriptionEmpty = e.getDocument().getLength() == 0;
				enableCreateButton();
			}
		});
		tfd_Namespace.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				namespaceEmpty = e.getDocument().getLength() == 0;
				enableCreateButton();
			}
		});
		btn_Create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				propertyChangeSupport.firePropertyChange("btn_Create", "unclicked", "clicked");
			}
		});
		btn_Cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				propertyChangeSupport.firePropertyChange("btn_Cancel", "unclicked", "clicked");
			}
		});
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				propertyChangeSupport.firePropertyChange("btn_CloseWindow", "unclicked", "clicked");
				super.windowClosing(e);
			}
		});
		clear();
		enableCreateButton();
	}

	private void enableCreateButton() {
		btn_Create.setEnabled(!discriptionEmpty && !namespaceEmpty);
	}

	public String getDescription() {
		return tfd_Description.getText();
	}

	public String getNamespace() {
		return tfd_Namespace.getText();
	}

	public String getVersion() {
		if (rdb_V14.isSelected())
			return "1.4";
		if (rdb_V16.isSelected())
			return "1.6";
		return null;
	}

	public void clear() {
		tfd_Description.setText("Description of new framework");
		tfd_Namespace.setText("http://www.visi.nl/schemas/20160331/NewFramework");
	}

	public void setVisible(boolean visible) {
		dialog.setVisible(visible);
	}
}
