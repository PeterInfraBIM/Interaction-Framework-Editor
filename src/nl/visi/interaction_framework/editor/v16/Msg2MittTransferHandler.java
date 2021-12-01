package nl.visi.interaction_framework.editor.v16;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import nl.visi.interaction_framework.editor.ui.RotatingButton;
import nl.visi.interaction_framework.editor.v16.Control16.ElementsTableModel;
import nl.visi.interaction_framework.editor.v16.TransactionsPanelControl16.MessagesTableModel;
import nl.visi.schemas._20160331.MessageInTransactionTypeType;
import nl.visi.schemas._20160331.MessageTypeType;

@SuppressWarnings("serial")
public class Msg2MittTransferHandler extends TransferHandler {

	private static Msg2MittTransferHandler instance;

	private static JTable.DropLocation dropLocation;
	private static String[] idDescr;
	private static RotatingButton selectedMessage;
	private static MessageInTransactionTypeType transMitt;
	private static Color saveSelectedBackground;
	private boolean initialized = false;
	private TransactionsPanelControl16 transactionsPC;
	private JTable tbl_Messages, tbl_TransMessages;
	private MessagesTableModel model;
	private int dropAction;

	public static Msg2MittTransferHandler getInstance() {
		if (instance == null) {
			instance = new Msg2MittTransferHandler();
		}
		return instance;
	}

	private Msg2MittTransferHandler() {
	}

	private void init() {
		if (!initialized) {
			tbl_Messages = MainPanelControl16.getMessagesPC().tbl_Elements;
			transactionsPC = MainPanelControl16.getTransactionsPC();
			tbl_TransMessages = transactionsPC.tbl_Messages;
			model = (MessagesTableModel) tbl_TransMessages.getModel();
			initialized = true;
		}
	}

	@Override
	public int getSourceActions(JComponent c) {
		init();

		return COPY | MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		init();

		if (c instanceof JTable) {
			JTable table = (JTable) c;
			@SuppressWarnings("unchecked")
			ElementsTableModel<MessageTypeType> model = (ElementsTableModel<MessageTypeType>) table.getModel();
			int selectedRow = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
			MessageTypeType selectedElement = (MessageTypeType) model.get(selectedRow);
			return new StringSelection(selectedElement.getId() + '\t' + selectedElement.getDescription());
		} else {
			RotatingButton btn = (RotatingButton) c;
			String[] words = btn.getToolTipText().split(" ");
			System.out.println("create transferable for MITT: " + words[0]);
			return new StringSelection(words[0]);
		}
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		synchronized (this) {

			init();

			// Check if MessagesTable is the target
			if (info.getComponent().equals(tbl_Messages)) {
				return false;
			}

			// Check for String flavor
			if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}

			Transferable t = info.getTransferable();
			dropAction = info.getDropAction();

			// Check for existing message type
			idDescr = getMessageIdPlusDescr(t);
			MessageTypeType messageType = Editor16.getStore16().getElement(MessageTypeType.class, idDescr[0]);
			if (messageType != null) {
				if (info.getComponent() instanceof JTable) {
					dropLocation = (javax.swing.JTable.DropLocation) info.getDropLocation();
					return true;
				} else if (info.getComponent() instanceof JComboBox) {
					return true;
				} else if (info.getComponent() instanceof JPanel || info.getComponent() instanceof JRootPane) {
					if (selectedMessage != null) {
						selectedMessage.setBackground(saveSelectedBackground);
						selectedMessage.setForeground(null);
						selectedMessage.repaint();
					}
					selectedMessage = null;
					saveSelectedBackground = null;
					return true;
				} else if (info.getComponent() instanceof RotatingButton) {
					RotatingButton candSelMsg = (RotatingButton) info.getComponent();
					String mittId = candSelMsg.getToolTipText();
					mittId = mittId.indexOf(' ') > 0 ? mittId.substring(0, mittId.indexOf(' ')) : mittId;
					MessageInTransactionTypeType selectedMitt = Editor16.getStore16()
							.getElement(MessageInTransactionTypeType.class, mittId);
					// Check if candidate message holds a mitt ID
					if (selectedMitt == null) {
						return false;
					}
					if (selectedMessage == null || !candSelMsg.equals(selectedMessage)) {
						if (selectedMessage != null) {
							selectedMessage.setBackground(saveSelectedBackground);
							selectedMessage.repaint();
						}
						selectedMessage = candSelMsg;
						saveSelectedBackground = candSelMsg.getBackground();
						selectedMessage.setBackground(Color.BLUE);
						selectedMessage.setForeground(Color.WHITE);
						selectedMessage.repaint();
					}
					return true;
				}
			} else {
				transMitt = Editor16.getStore16().getElement(MessageInTransactionTypeType.class, idDescr[0]);
				if (transMitt != null) {
					if (info.getComponent().equals(transactionsPC.canvas2Panel)) {
						// No background drop
						return false;
					}
					if (info.getComponent().equals(transactionsPC.canvas16Plane.initiator.getActiveLabel())) {
						// No initiator drop
						return false;
					}
					if (info.getComponent().equals(transactionsPC.canvas16Plane.executor.getActiveLabel())) {
						// No executor drop
						return false;
					}
					if (info.getComponent() instanceof RotatingButton) {
						RotatingButton target = (RotatingButton) info.getComponent();
						String targetId = target.getToolTipText().split(" ")[0];
						if (transMitt.getId().equals(targetId)) {
							// No drop on the same MITT
							return false;
						}
						MessageInTransactionTypeType targetMitt = Editor16.getStore16()
								.getElement(MessageInTransactionTypeType.class, targetId);
						if (transMitt.isInitiatorToExecutor() == targetMitt.isInitiatorToExecutor()) {
							// Not the same direction
							return false;
						}

						if (dropAction == MOVE) {
							List<MessageInTransactionTypeType> previous = Control16.getPrevious(transMitt);
							if (previous != null && previous.contains(targetMitt)) {
								// transfer mitt should not contain target mitt as a previous mitt
								return false;
							}
							return true;
						} else {
							List<MessageInTransactionTypeType> previous = Control16.getPrevious(targetMitt);
							if (previous != null && previous.contains(transMitt)) {
								// target mitt should not contain transfer mitt as a previous mitt
								return false;
							}
							return true;
						}
					}
				}
			}
			return false;
		}

	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		init();

		if (comp instanceof JComboBox<?>) {
			JComboBox<?> msgComboBox = (JComboBox<?>) comp;
			msgComboBox.setSelectedItem("[" + idDescr[0] + "] " + idDescr[1]);
			return true;
		} else if (comp instanceof RotatingButton) {
			String mittId = ((RotatingButton) comp).getToolTipText();
			MessageInTransactionTypeType selectedMitt = Editor16.getStore16()
					.getElement(MessageInTransactionTypeType.class, mittId);
			dropBeforeOrAfter(selectedMitt);
			return true;
		} else if (comp instanceof JPanel || comp instanceof JRootPane) {
			addMsg2Mitt(idDescr, true);
			return true;
		} else if (comp instanceof JTable) {
			if (dropLocation.isInsertRow()) {
				// between lines drop
				addMsg2Mitt(idDescr, true);
				return true;
			} else {
				// on line drop
				int row = dropLocation.getRow();
				MessageInTransactionTypeType selectedMitt = model.elements.get(row);
				dropBeforeOrAfter(selectedMitt);
				return true;
			}
		}
		return false;
	}

	void dropBeforeOrAfter(MessageInTransactionTypeType selectedMitt) {
		if (transMitt == null) {
			MessageInTransactionTypeType newMitt = addMsg2Mitt(idDescr, false);
			newMitt.setInitiatorToExecutor(!selectedMitt.isInitiatorToExecutor());
			if (dropAction == MOVE) {
				Control16.addPrevious(newMitt, selectedMitt);
				transactionsPC.fillMessageTable();
				transactionsPC.canvas16Plane.selectMessage(selectedMitt);
			} else {
				Control16.addPrevious(selectedMitt, newMitt);
				transactionsPC.fillMessageTable();
				transactionsPC.canvas16Plane.selectMessage(newMitt);
			}
		} else {
			if (dropAction == MOVE) {
				Control16.addPrevious(transMitt, selectedMitt);
				transactionsPC.fillMessageTable();
				transactionsPC.canvas16Plane.selectMessage(selectedMitt);
			} else {
				Control16.addPrevious(selectedMitt, transMitt);
				transactionsPC.fillMessageTable();
				transactionsPC.canvas16Plane.selectMessage(transMitt);
			}
		}
		// Next two statements prevent a reset of the dynamic sequence diagram
		// if this window wasn't shown earlier.
		transactionsPC.canvas16Plane.selectedTransaction = transactionsPC.selectedElement;
		transactionsPC.canvas16Plane.currentTransaction = transactionsPC.selectedElement;
	}

	private MessageInTransactionTypeType addMsg2Mitt(String[] idDescr, boolean resetDiagrams) {
		return transactionsPC.canvas16Plane.addMitt2Canvas("[" + idDescr[0] + "] " + idDescr[1], resetDiagrams);
	}

	private String[] getMessageIdPlusDescr(Transferable t) {
		try {
			String data = (String) t.getTransferData(DataFlavor.stringFlavor);
			return data.split("\t");
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
