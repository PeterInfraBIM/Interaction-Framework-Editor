package nl.visi.interaction_framework.editor.v14;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

public class TransactionCopyDialogControl extends Control14 {
	private static final String TRANSACTION_COPY_DIALOG = "nl/visi/interaction_framework/editor/swixml/TransactionCopyDialog.xml";

	private JDialog dialog;
	private JButton btn_Cancel, btn_Copy;
	private JRadioButton rdb_ShallowCopy, rdb_DeepInternalOnlyCopy, rdb_DeepCopy;
	private JCheckBox chb_IncludeElementConditions;

	public TransactionCopyDialogControl() throws Exception {
		super();
		dialog = (JDialog) render(TRANSACTION_COPY_DIALOG);
		JRootPane rootPane = SwingUtilities.getRootPane(btn_Copy);
		rootPane.setDefaultButton(btn_Copy);
		rdb_ShallowCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chb_IncludeElementConditions.setEnabled(!rdb_ShallowCopy.isSelected());
				chb_IncludeElementConditions.setSelected(!rdb_ShallowCopy.isSelected());
			}
		});
		rdb_DeepInternalOnlyCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chb_IncludeElementConditions.setEnabled(rdb_DeepInternalOnlyCopy.isSelected());
			}
		});
		btn_Copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				if (rdb_ShallowCopy.isSelected()) {
					propertyChangeSupport.firePropertyChange("btn_Copy", false, "shallow_copy");
				} else if (rdb_DeepInternalOnlyCopy.isSelected()) {
					propertyChangeSupport.firePropertyChange("btn_Copy", chb_IncludeElementConditions.isSelected(),
							"deep_internal_only_copy");
				} else if (rdb_DeepCopy.isSelected()) {
					propertyChangeSupport.firePropertyChange("btn_Copy", chb_IncludeElementConditions.isSelected(),
							"deep_copy");
				}
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
	}

	public void setVisible(boolean visible) {
		dialog.setVisible(visible);
	}
}
