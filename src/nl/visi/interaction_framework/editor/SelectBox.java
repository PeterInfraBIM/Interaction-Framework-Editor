package nl.visi.interaction_framework.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceRoyale;

@SuppressWarnings("serial")
public class SelectBox extends JDialog {
	final ResourceBundle bundle = ResourceBundle.getBundle(Control.RESOURCE_BUNDLE);
	private PropertyChangeSupport propertyChangeSupport;
	private JList<String> itemList;
	private DefaultListModel<String> listModel;
	private final List<String> items;
	private JButton selectButton;
	private Integer selectedIndex;

	public SelectBox(JFrame owner, String title, List<String> items) throws UnsupportedLookAndFeelException {
		super((JFrame) owner, title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		propertyChangeSupport = new PropertyChangeSupport(this);
		this.items = items;

		PlasticXPLookAndFeel laf = new PlasticXPLookAndFeel();
		PlasticXPLookAndFeel.setCurrentTheme(new ExperienceRoyale());
		PlasticXPLookAndFeel.setTabStyle(PlasticXPLookAndFeel.TAB_STYLE_METAL_VALUE);
		PlasticXPLookAndFeel.set3DEnabled(true);
		UIManager.setLookAndFeel(laf);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(mainPanel);

		// Panel north
		final JTextField filter = new JTextField();
		filter.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				filter(filter.getText());
			}
		});
		mainPanel.add(filter, BorderLayout.NORTH);

		// Panel south
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		JButton cancelButton = new JButton(new AbstractAction(bundle.getString("lbl_Cancel")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				SelectBox.this.dispose();
			}
		});
		buttonPanel.add(cancelButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);
		selectButton = new JButton(new AbstractAction(bundle.getString("lbl_Select")) {
			@Override
			public void actionPerformed(ActionEvent e) {
				propertyChangeSupport.firePropertyChange("SelectedItem", null, listModel.get(selectedIndex));
				SelectBox.this.dispose();
			}
		});
		selectButton.setEnabled(false);
		buttonPanel.add(selectButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 0, 0, 0), 0, 0));
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		// Panel center
		itemList = new JList<>();
		listModel = new DefaultListModel<>();
		itemList.setModel(listModel);
		listModel.addAll(items);
		itemList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				selectedIndex = itemList.getSelectedIndex();
				selectButton.setEnabled(selectedIndex >= 0);
			}
		});
		mainPanel.add(new JScrollPane(itemList), BorderLayout.CENTER);

		setPreferredSize(new Dimension(400, 400));
		pack();
		setVisible(true);
	}

	private void filter(String filterText) {
		String filterTextUc = filterText.toUpperCase();
		if (filterText.equals("")) {
			listModel.clear();
			listModel.addAll(SelectBox.this.items);
		} else {
			listModel.clear();
			for (String item : SelectBox.this.items) {
				String itemUc = item.toUpperCase();
				if (itemUc.contains(filterTextUc)) {
					listModel.addElement(item);
				}
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	private static final String[] TEST_ITEMS = { "Aanleveren rapportage", "Aanleveren hernieuwde rapportage",
			"Aanvaarding (deel)Oplevering/ Prestatieverklaring", "Aanvaarding rapportage conform SLA",
			"Aanvaarding rapportage NIET conform SLA", "Aanvaarding uitgevoerde wijziging",
			"Aanvaarding uitgevoerde wijziging", "Aanvraagbevestiging Derde", "Hernieuwde Aanvraagbevestiging Derde",
			"Advies offerte", "Advies (deel)Oplevering", "Advies rapportage" };

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				List<String> testItems = new ArrayList<>();
				for (String item : TEST_ITEMS) {
					testItems.add(item);
				}
				try {
					new SelectBox((JFrame) null, "Test", testItems);
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
