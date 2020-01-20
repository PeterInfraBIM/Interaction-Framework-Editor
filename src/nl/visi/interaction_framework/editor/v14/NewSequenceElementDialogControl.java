package nl.visi.interaction_framework.editor.v14;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;

public class NewSequenceElementDialogControl extends Control14 {
	private static final String NEW_SEQUENCE_ELEMENT_DIALOG = "nl/visi/interaction_framework/editor/swixml/NewSequenceElementDialog.xml";
	private static final String[] IN_SEQUENCE_ELEMENT_TYPES = { "Next" };
	private static final String[] OUT_SEQUENCE_ELEMENT_TYPES = { "Previous", "SendAfter", "SendBefore" };

	private JDialog dialog;
	private JButton btn_Cancel, btn_Create;
	private JComboBox<String> cbx_SequenceElementType, cbx_Message, cbx_MessageInTransaction, cbx_Transaction;
	private ElementsModel<TransactionTypeType> transactionsModel;
	private ElementsModel<MessageTypeType> messagesModel;
	private ElementsModel<MessageInTransactionTypeType> mittsModel;

	@SuppressWarnings("serial")
	private class ElementsModel<T> extends AbstractListModel<String> implements ComboBoxModel<String> {
		private List<T> elements;
		private Object selectedItem;

		public ElementsModel() {
			elements = new ArrayList<T>();
		}

		@Override
		public int getSize() {
			return elements.size();
		}

		@Override
		public String getElementAt(int index) {
			T element = elements.get(index);
			return getStringValue(element);
		}

		public void addElement(T element) {
			elements.add(element);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			selectedItem = anItem;
		}

		@Override
		public Object getSelectedItem() {
			return selectedItem;
		}

		public T getSelectedElement() {
			if (selectedItem != null) {
				for (T element : elements) {
					if (getStringValue(element).contentEquals((String) selectedItem)) {
						return element;
					}
				}
			}
			return null;
		}

		public void setSelectedElement(T selectedElement) {
			setSelectedItem(getStringValue(selectedElement));
		}

		private String getStringValue(T element) {
			if (element instanceof TransactionTypeType) {
				return ((TransactionTypeType) element).getDescription();
			}
			if (element instanceof MessageTypeType) {
				return ((MessageTypeType) element).getDescription();
			}
			if (element instanceof MessageInTransactionTypeType) {
				return ((MessageInTransactionTypeType) element).getId();
			}
			return null;
		}

	}

	public NewSequenceElementDialogControl(String inOut) throws Exception {
		super();
		dialog = (JDialog) render(NEW_SEQUENCE_ELEMENT_DIALOG);
		JRootPane rootPane = SwingUtilities.getRootPane(btn_Create);
		rootPane.setDefaultButton(btn_Create);
		DefaultComboBoxModel<String> typesModel = inOut.equals("in")
				? new DefaultComboBoxModel<>(IN_SEQUENCE_ELEMENT_TYPES)
				: new DefaultComboBoxModel<>(OUT_SEQUENCE_ELEMENT_TYPES);
		cbx_SequenceElementType.setModel(typesModel);

		mittsModel = new ElementsModel<>();
		List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : mitts) {
			mittsModel.addElement(mitt);
		}
		cbx_MessageInTransaction.setModel(mittsModel);
		cbx_MessageInTransaction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MessageInTransactionTypeType selectedMitt = mittsModel.getSelectedElement();
				TransactionTypeType transaction = getTransaction(selectedMitt);
				transactionsModel.setSelectedElement(transaction);
				cbx_Transaction.repaint();
				fillMessageComboBox(transaction.getId());
				cbx_Message.setEnabled(true);
				MessageTypeType message = getMessage(selectedMitt);
				messagesModel.setSelectedElement(message);
				cbx_Message.repaint();
				enableCreateButton();
			}
		});

		transactionsModel = new ElementsModel<>();
		List<TransactionTypeType> transactions = Editor14.getStore14().getElements(TransactionTypeType.class);
		for (TransactionTypeType transaction : transactions) {
			transactionsModel.addElement(transaction);
		}
		cbx_Transaction.setModel(transactionsModel);
		cbx_Transaction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				messagesModel.setSelectedItem(null);
				messagesModel.elements.clear();
				mittsModel.setSelectedItem(null);
				cbx_MessageInTransaction.repaint();
				TransactionTypeType selectedTransaction = transactionsModel.getSelectedElement();
				String selectedTransactionId = selectedTransaction.getId();
				fillMessageComboBox(selectedTransactionId);
				cbx_Message.setEnabled(true);
				enableCreateButton();
			}
		});

		messagesModel = new ElementsModel<>();
		cbx_Message.setModel(messagesModel);
		cbx_Message.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mittsModel.setSelectedElement(null);
				cbx_MessageInTransaction.repaint();
				TransactionTypeType selectedTransaction = transactionsModel.getSelectedElement();
				MessageTypeType selectedMessage = messagesModel.getSelectedElement();
				if (selectedTransaction != null && selectedMessage != null) {
					List<MessageInTransactionTypeType> mittElements = Editor14.getStore14()
							.getElements(MessageInTransactionTypeType.class);
					for (MessageInTransactionTypeType mitt : mittElements) {
						TransactionTypeType transaction = getTransaction(mitt);
						if (transaction.getId().equals(selectedTransaction.getId())) {
							MessageTypeType message = getMessage(mitt);
							if (message.getId().equals(selectedMessage.getId())) {
								mittsModel.setSelectedElement(mitt);
								cbx_MessageInTransaction.repaint();
								enableCreateButton();
								return;
							}
						}
					}
				}
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

	private void fillMessageComboBox(String selectedTransactionId) {
		messagesModel.elements.clear();
		List<MessageInTransactionTypeType> mitts = Editor14.getStore14()
				.getElements(MessageInTransactionTypeType.class);
		for (MessageInTransactionTypeType mitt : mitts) {
			TransactionTypeType transaction = getTransaction(mitt);
			if (transaction.getId().equals(selectedTransactionId)) {
				messagesModel.addElement(getMessage(mitt));
			}
		}
	}

	private void enableCreateButton() {
		btn_Create.setEnabled(mittsModel.getSelectedElement() != null);
	}

	public void clear() {
		transactionsModel.setSelectedElement(null);
		cbx_Transaction.repaint();
		messagesModel.setSelectedElement(null);
		cbx_Message.repaint();
		mittsModel.setSelectedElement(null);
		cbx_MessageInTransaction.repaint();
	}

	public void setVisible(boolean visible) {
		dialog.setVisible(visible);
	}

	public String getConditionType() {
		return (String) cbx_SequenceElementType.getSelectedItem();
	}

	public MessageInTransactionTypeType getMitt() {
		return mittsModel.getSelectedElement();
	}
}
