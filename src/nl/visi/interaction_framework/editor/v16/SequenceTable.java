package nl.visi.interaction_framework.editor.v16;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nl.visi.interaction_framework.editor.v16.Control16;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.RoleTypeType;
import nl.visi.schemas._20160331.TransactionTypeType;

public class SequenceTable extends Control16 {
	private static final String SEQUENCE_PANEL = "nl/visi/interaction_framework/editor/swixml/SequencePanel.xml";
	private JPanel panel;
	private JTable tbl_Sequences;
	private SequenceTableModel sequenceTableModel;
	private JButton btn_RemoveSequenceElement, btn_AddSequenceElement;
	private RoleTypeType selectedElement;
	private String inOut;
	private MessageInTransactionTypeType parent;

	private enum SequenceElementType {
		Next, Previous, SendAfter, SendBefore, Start, Stop;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}
	}

	private class SequenceRule extends ElementType {
		private SequenceElementType type;
		private MessageInTransactionTypeType mitt;

		public SequenceRule() {
			super();
		}

		public SequenceRule(SequenceElementType type, MessageInTransactionTypeType mitt) {
			this();
			this.type = type;
			this.mitt = mitt;
		}

		public SequenceElementType getType() {
			return type;
		}

		public MessageInTransactionTypeType getMitt() {
			return mitt;
		}

		@Override
		public String getId() {
			return mitt != null ? mitt.getId() : null;
		}

		@Override
		public String toString() {
			return mitt != null ? mitt.getId() : null;
		}

	}

//	private class SequenceMitt {
//		private MessageInTransactionTypeType mitt;
//
//		@SuppressWarnings("unused")
//		public SequenceMitt() {
//		}
//
//		public SequenceMitt(MessageInTransactionTypeType mitt) {
//			this.mitt = mitt;
//		}
//
//		public List<MessageInTransactionTypeType> getPreviousMitts() {
//			return getPrevious(mitt);
//		}
//
//		public List<MessageInTransactionTypeType> getSendAfterMitts() {
//			return Control16.getSendAfters(mitt);
//		}
//
//		public List<MessageInTransactionTypeType> getSendBeforeMitts() {
//			return Control16.getSendBefores(mitt);
//		}
//
//		public List<MessageInTransactionTypeType> getNextMitts() {
//			if (mitt != null) {
//				List<MessageInTransactionTypeType> actions = null;
//
//				List<MessageInTransactionTypeType> allMitts = Editor16.getStore16()
//						.getElements(MessageInTransactionTypeType.class);
//				for (MessageInTransactionTypeType mittElement : allMitts) {
//					List<MessageInTransactionTypeType> previous = getPrevious(mittElement);
//					if (previous != null) {
//						for (MessageInTransactionTypeType prev : previous) {
//							if (prev.getId().equals(mitt.getId())) {
//								if (actions == null) {
//									actions = new ArrayList<>();
//								}
//								actions.add(mittElement);
//							}
//						}
//					}
//				}
//				return actions;
//			}
//			return null;
//		}
//	}

	private enum SequenceTableColumns {
		Type, Id, Role, Transaction, Message;

		@Override
		public String toString() {
			return getBundle().getString("lbl_" + name());
		}

	}

	@SuppressWarnings("serial")
	private class SequenceTableModel extends ElementsTableModel<SequenceRule> {

		@Override
		public int getColumnCount() {
			return SequenceTableColumns.values().length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return SequenceTableColumns.values()[columnIndex].toString();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			SequenceRule conditionRule = get(rowIndex);
			MessageInTransactionTypeType mitt = conditionRule.getMitt();
			switch (SequenceTableColumns.values()[columnIndex]) {
			case Type:
				return conditionRule.getType().name();
			case Id:
				return conditionRule.getId();
			case Role:
				if (mitt != null) {
					RoleTypeType initiator = getInitiator(mitt);
					RoleTypeType executor = getExecutor(mitt);
					Boolean initiatorToExecutor = mitt.isInitiatorToExecutor();
					if (initiatorToExecutor == null) {
						initiatorToExecutor = false;
					}
					if (initiatorToExecutor) {
						if (selectedElement != null) {
							if (selectedElement.equals(initiator)) {
								return executor != null ? executor.getId() : null;
							} else {
								return initiator != null ? initiator.getId() : null;
							}
						} else {
							return initiator.getId() + " => " + executor.getId();
						}
					} else {
						if (selectedElement != null) {
							if (selectedElement.equals(executor)) {
								return initiator != null ? initiator.getId() : null;
							} else {
								return executor != null ? executor.getId() : null;
							}
						} else {
							return executor.getId() + " => " + initiator.getId();
						}
					}
				}
				break;
			case Transaction:
				TransactionTypeType transactionType = getTransaction(mitt);
				return transactionType != null ? transactionType.getId() : null;
			case Message:
				MessageTypeType messageType = getMessage(mitt);
				return messageType != null ? messageType.getId() : null;
			}

			return null;
		}

	}

	private void initSequenceTable() {
		sequenceTableModel = new SequenceTableModel();
		tbl_Sequences.setModel(sequenceTableModel);
		tbl_Sequences.setAutoCreateRowSorter(true);
		tbl_Sequences.setFillsViewportHeight(true);
		tbl_Sequences.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting())
					return;
				int selectedRow = tbl_Sequences.getSelectedRow();
				if (selectedRow < 0) {
					btn_RemoveSequenceElement.setEnabled(false);
				} else {
					SequenceRule sequenceRule = sequenceTableModel
							.get(tbl_Sequences.getRowSorter().convertRowIndexToModel(selectedRow));
					boolean isStopCondition = sequenceRule.getType().equals(SequenceElementType.Stop);
					boolean isStartCondition = sequenceRule.getType().equals(SequenceElementType.Start);
					btn_RemoveSequenceElement.setEnabled(!isStartCondition && !isStopCondition);
				}
			}
		});
	}

	public void fillSequenceTable(RoleTypeType selectedElement, String inOut, MessageInTransactionTypeType mitt) {
		this.selectedElement = selectedElement;
		this.parent = mitt;
		this.inOut = inOut;
		boolean enabled = mitt != null;
		tbl_Sequences.setEnabled(enabled);
		btn_AddSequenceElement.setEnabled(enabled);
		sequenceTableModel.clear();
		if (enabled) {
//			SequenceMitt sequenceMitt = new SequenceMitt(mitt);
			if (inOut.contentEquals("out") || inOut.contentEquals("inOut")) {
				List<MessageInTransactionTypeType> sendAfters = getSendAfters(mitt);
				if (sendAfters != null) {
					for (MessageInTransactionTypeType sendAfter : sendAfters) {
						sequenceTableModel.add(new SequenceRule(SequenceElementType.SendAfter, sendAfter));
					}
				}
				List<MessageInTransactionTypeType> sendBefores = getSendBefores(mitt);
				if (sendBefores != null) {
					for (MessageInTransactionTypeType sendBefore : sendBefores) {
						sequenceTableModel.add(new SequenceRule(SequenceElementType.SendBefore, sendBefore));
					}
				}
				List<MessageInTransactionTypeType> triggers = getPrevious(mitt);
				if (triggers != null) {
					for (MessageInTransactionTypeType trigger : triggers) {
						sequenceTableModel.add(new SequenceRule(SequenceElementType.Previous, trigger));
					}
				} else {
					if (sendAfters == null && sendBefores == null && !inOut.contentEquals("inOut")) {
						sequenceTableModel.add(new SequenceRule(SequenceElementType.Start, null));
					}
				}
			}
			if (inOut.contentEquals("in") || inOut.contentEquals("inOut")) {
				List<MessageInTransactionTypeType> actions = getNext(mitt);
				if (actions != null) {
					for (MessageInTransactionTypeType action : actions) {
						sequenceTableModel.add(new SequenceRule(SequenceElementType.Next, action));
					}
				} else if (!inOut.contentEquals("inOut")) {
					sequenceTableModel.add(new SequenceRule(SequenceElementType.Stop, null));
				}
			}
		}
	}

	public void addSequenceElement() {
		btn_AddSequenceElement.setEnabled(false);

		try {
//			int selectedRow = tbl_Messages.getSelectedRow();
//			final int selectedRowIndex = tbl_Messages.getRowSorter().convertRowIndexToModel(selectedRow);
//			String inOut = (String) messagesTableModel.getValueAt(selectedRowIndex,
//					MessagesTableColumns.Type.ordinal());
			final NewSequenceElementDialogControl newSequenceElementDialogControl = new NewSequenceElementDialogControl(
					inOut);
			newSequenceElementDialogControl.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + ": " + evt.getNewValue());
					if (evt.getPropertyName().equals("btn_Create")) {
						String sequenceElementType = newSequenceElementDialogControl.getConditionType();
						MessageInTransactionTypeType value = newSequenceElementDialogControl.getMitt();
						addSequenceElement(sequenceElementType, parent, value);
						sequenceTableModel
								.add(new SequenceRule(SequenceElementType.valueOf(sequenceElementType), value));
					}
				}

			});
			newSequenceElementDialogControl.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		btn_AddSequenceElement.setEnabled(true);
	}

	private void addSequenceElement(String sequenceElementType, MessageInTransactionTypeType mitt,
			MessageInTransactionTypeType value) {
		switch (sequenceElementType) {
		case "Next":
			addPrevious(value, mitt);
			break;
		case "Previous":
			addPrevious(mitt, value);
			break;
		case "SendAfter":
			addSendAfter(mitt, value);
			break;
		case "SendBefore":
			addSendBefore(mitt, value);
			break;
		default:
			break;
		}
	}

	public void removeSequenceElement() {
		int selectedRow = tbl_Sequences.getSelectedRow() > -1
				? tbl_Sequences.getRowSorter().convertRowIndexToModel(tbl_Sequences.getSelectedRow())
				: -1;
		if (selectedRow > -1) {
//		int selectedMessageTableRow = tbl_Messages.getRowSorter()
//				.convertRowIndexToModel(tbl_Messages.getSelectedRow());
//		MessageInTransactionTypeType parent = messagesTableModel.get(selectedMessageTableRow);
			SequenceRule conditionRule = sequenceTableModel.get(selectedRow);
			switch (conditionRule.getType()) {
			case Next:
				removePrevious(conditionRule.getMitt(), parent);
				break;
			case Previous:
				removePrevious(parent, conditionRule.getMitt());
				break;
			case SendAfter:
				removeSendAfter(parent, conditionRule.getMitt());
				break;
			case SendBefore:
				removeSendBefore(parent, conditionRule.getMitt());
				break;
			case Start:
				// Should not occur
				return;
			case Stop:
				// Should not occur
				return;
			default:
				break;
			}
			sequenceTableModel.elements.remove(selectedRow);
			sequenceTableModel.fireTableRowsDeleted(selectedRow, selectedRow);
//			messageTableSelectionListener.valueChanged(null);
		}
	}

	public SequenceTable() throws Exception {
		super();
		panel = (JPanel) render(SEQUENCE_PANEL);
		initSequenceTable();
	}

	public JPanel getPanel() {
		return panel;
	}

	public static void main(String[] arg) throws Exception {
		SequenceTable sequenceTable = new SequenceTable();
		sequenceTable.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println(evt.getPropertyName() + ": " + evt.getNewValue());
				if (evt.getPropertyName().equals("btn_Create")) {
					System.out.println(evt.getPropertyName());
//					String sequenceElementType = newSequenceElementDialogControl.getConditionType();
//					MessageInTransactionTypeType value = newSequenceElementDialogControl.getMitt();
//					String mittId = (String) messagesTableModel.getValueAt(selectedRowIndex,
//							MessagesTableColumns.Id.ordinal());
//					MessageInTransactionTypeType mitt = Editor16.getStore16()
//							.getElement(MessageInTransactionTypeType.class, mittId);
//					addSequenceElement(sequenceElementType, mitt, value);
//					sequenceTableModel
//							.add(new SequenceRule(SequenceElementType.valueOf(sequenceElementType), value));
				}
			}
		});
		MessageInTransactionTypeType mitt = objectFactory.createMessageInTransactionTypeType();
		mitt.setId("BerichtInTransactie10");
		// MessageInTransactionTypeType mitt =
		// Editor16.getStore16().getElement(MessageInTransactionTypeType.class,
		// "BerichtInTransactie10");
		JFrame frame = new JFrame("Test");
		Component panel = sequenceTable.render(SEQUENCE_PANEL);
		frame.add(panel);
		// sequenceTable.initSequenceTable();
		// sequenceTable.fillSequenceTable("in", mitt);
		frame.pack();
		frame.setVisible(true);
	}

	public void clear() {
		sequenceTableModel.clear();
	}
}
