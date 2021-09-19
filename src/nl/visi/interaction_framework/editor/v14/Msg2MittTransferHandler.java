package nl.visi.interaction_framework.editor.v14;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nl.visi.schemas._20140331.MessageTypeType;

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
		MessageTypeType messageType = Editor14.getStore14().getElement(MessageTypeType.class, idDescr[0]);
		if (messageType != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		@SuppressWarnings("unchecked")
		JComboBox<String> msgComboBox = (JComboBox<String>) comp;
		if (msgComboBox != null) {
			String[] idDescr = getMessageIdPlusDescr(t);
			msgComboBox.setSelectedItem("[" + idDescr[0] + "] " + idDescr[1]);
		}
		return false;
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
