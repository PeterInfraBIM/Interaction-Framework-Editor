package nl.visi.interaction_framework.editor.v14;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.RoleTypeType;
import nl.visi.schemas._20140331.TransactionTypeType;

public class SequenceTable extends Control14 {
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

	private enum SequenceTableColumns {
		Type, Role, Id, Message, Transaction;

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
								return executor != null ? executor.getDescription() : null;
							} else {
								return initiator != null ? initiator.getDescription() : null;
							}
						} else {
							return initiator.getDescription() + " => " + executor.getDescription();
						}
					} else {
						if (selectedElement != null) {
							if (selectedElement.equals(executor)) {
								return initiator != null ? initiator.getDescription() : null;
							} else {
								return executor != null ? executor.getDescription() : null;
							}
						} else {
							return executor.getDescription() + " => " + initiator.getDescription();
						}
					}
				}
				break;
			case Transaction:
				TransactionTypeType transactionType = getTransaction(mitt);
				return transactionType != null ? transactionType.getDescription() : null;
			case Message:
				MessageTypeType messageType = getMessage(mitt);
				return messageType != null ? messageType.getDescription() : null;
			}

			return null;
		}

	}

	private void initSequenceTable() {
		sequenceTableModel = new SequenceTableModel();
		tbl_Sequences.setModel(sequenceTableModel);
		tbl_Sequences.setAutoCreateRowSorter(true);
		tbl_Sequences.setFillsViewportHeight(true);
		TableColumn typeColumn = tbl_Sequences.getColumnModel().getColumn(SequenceTableColumns.Type.ordinal());
		typeColumn.setMaxWidth(80);
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
						SequenceElementType seqType = SequenceElementType.valueOf(sequenceElementType);
						switch (seqType) {
						case Next:
							if (sequenceTableModel.elements.size() == 1) {
								SequenceRule conditionRule = sequenceTableModel.get(0);
								if (conditionRule.type == SequenceElementType.Stop) {
									sequenceTableModel.remove(0);
								}
							}
							break;
						case Previous:
							if (sequenceTableModel.elements.size() == 1) {
								SequenceRule conditionRule = sequenceTableModel.get(0);
								if (conditionRule.type == SequenceElementType.Start) {
									sequenceTableModel.remove(0);
								}
							}
							break;
						case SendAfter:
							break;
						case SendBefore:
							break;
						case Start:
							break;
						case Stop:
							break;
						default:
							break;
						}
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
			SequenceRule conditionRule = sequenceTableModel.get(selectedRow);
			
			int response = JOptionPane.showConfirmDialog(getPanel(),
					getBundle().getString("lbl_Remove") + ": " + conditionRule.getMitt().getId(),
					getBundle().getString("lbl_Remove"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.CANCEL_OPTION)
				return;
			
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

	public void clear() {
		sequenceTableModel.clear();
	}
}
