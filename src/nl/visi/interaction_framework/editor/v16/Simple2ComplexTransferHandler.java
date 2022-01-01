package nl.visi.interaction_framework.editor.v16;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import nl.visi.interaction_framework.editor.v16.Control16.ElementsTableModel;
import nl.visi.schemas._20160331.SimpleElementTypeType;

@SuppressWarnings("serial")
public class Simple2ComplexTransferHandler extends TransferHandler {

	private static Simple2ComplexTransferHandler instance;

	public static Simple2ComplexTransferHandler getInstance() {
		if (instance == null) {
			instance = new Simple2ComplexTransferHandler();
		}
		return instance;
	}

	private Simple2ComplexTransferHandler() {
	}

	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		JTable tbl_SimpleElements = MainPanelControl16.getSimpleElementsPC().tbl_Elements;

		if (c instanceof JTable) {
			JTable table = (JTable) c;
			if (c.equals(tbl_SimpleElements)) {
				@SuppressWarnings("unchecked")
				ElementsTableModel<SimpleElementTypeType> model = (ElementsTableModel<SimpleElementTypeType>) table
						.getModel();
				int selectedRow = table.getRowSorter().convertRowIndexToModel(table.getSelectedRow());
				SimpleElementTypeType selectedElement = (SimpleElementTypeType) model.get(selectedRow);
				return new StringSelection(selectedElement.getId() + '\t' + selectedElement.getDescription());
			}
		}
		return null;
	}

}
