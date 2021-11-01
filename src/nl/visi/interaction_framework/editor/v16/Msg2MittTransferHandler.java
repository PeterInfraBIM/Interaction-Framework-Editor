package nl.visi.interaction_framework.editor.v16;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
	private static Color saveSelectedBackground;
	private boolean initialized = false;
	private TransactionsPanelControl16 transactionsPC;
	private JTable tbl_Messages, tbl_TransMessages;
	private MessagesTableModel model;

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

		JTable table = (JTable) c;
		@SuppressWarnings("unchecked")
		ElementsTableModel<MessageTypeType> model = (ElementsTableModel<MessageTypeType>) table.getModel();
		int selectedRow = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
		MessageTypeType selectedElement = (MessageTypeType) model.get(selectedRow);
		return new StringSelection(selectedElement.getId() + '\t' + selectedElement.getDescription());
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		synchronized (this) {

			init();

			Component component = info.getComponent();

			// Check if MessagesTable is the target
			if (info.getComponent().equals(tbl_Messages)) {
				return false;
			}

			// Check for String flavor
			if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				return false;
			}

			Transferable t = info.getTransferable();

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
			MessageInTransactionTypeType newMitt = addMsg2Mitt(idDescr);
			newMitt.setInitiatorToExecutor(!selectedMitt.isInitiatorToExecutor());
			Control16.addPrevious(newMitt, selectedMitt);
			transactionsPC.fillMessageTable();
			transactionsPC.canvas16Plane.reset();
			transactionsPC.reset();
			return true;
		} else if (comp instanceof JPanel || comp instanceof JRootPane) {
			addMsg2Mitt(idDescr);
			return true;
		} else if (comp instanceof JTable) {
			if (dropLocation.isInsertRow()) {
				// between lines drop
				addMsg2Mitt(idDescr);
				return true;
			} else {
				// on line drop
				int row = dropLocation.getRow();
				MessageInTransactionTypeType selectedMitt = model.elements.get(row);
				MessageInTransactionTypeType newMitt = addMsg2Mitt(idDescr);
				newMitt.setInitiatorToExecutor(!selectedMitt.isInitiatorToExecutor());
				Control16.addPrevious(newMitt, selectedMitt);
				transactionsPC.fillMessageTable();
				transactionsPC.canvas16Plane.reset();
				transactionsPC.reset();
				return true;
			}
		}
		return false;
	}

	private MessageInTransactionTypeType addMsg2Mitt(String[] idDescr) {
		return transactionsPC.canvas16Plane.addMitt2Canvas("[" + idDescr[0] + "] " + idDescr[1]);
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
