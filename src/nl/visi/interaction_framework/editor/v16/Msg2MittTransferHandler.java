package nl.visi.interaction_framework.editor.v16;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import nl.visi.schemas._20160331.MessageTypeType;

@SuppressWarnings("serial")
public class Msg2MittTransferHandler extends TransferHandler {

	@Override
	public boolean canImport(TransferHandler.TransferSupport info) {
		// Check for String flavor
		if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return false;
		}
		Transferable t = info.getTransferable();

		// Check for existing message type
		String[] idDescr = getMessageIdPlusDescr(t);
		MessageTypeType messageType = Editor16.getStore16().getElement(MessageTypeType.class, idDescr[0]);
		if (messageType != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		String[] idDescr = getMessageIdPlusDescr(t);
		if (comp instanceof JComboBox<?>) {
			JComboBox<?> msgComboBox = (JComboBox<?>) comp;
			msgComboBox.setSelectedItem("[" + idDescr[0] + "] " + idDescr[1]);
			return true;
		} else if (comp instanceof JPanel) {
			addMsg2Mitt(idDescr);
			return true;
		} else if (comp instanceof JTable) {
			JTable table = (JTable) comp;
			System.out.println(table.getDropMode());
			addMsg2Mitt(idDescr);
			return true;
		}
		return false;
	}

	private void addMsg2Mitt(String[] idDescr) {
		TransactionsPanelControl16 transactionsPC = MainPanelControl16.getTransactionsPC();
		transactionsPC.canvas16Plane.addMitt2Canvas("[" + idDescr[0] + "] " + idDescr[1]);
		transactionsPC.canvas16Plane.selectedTransaction = null;
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
