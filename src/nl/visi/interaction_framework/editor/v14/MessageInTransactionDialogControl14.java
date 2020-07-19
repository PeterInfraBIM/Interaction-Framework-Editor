package nl.visi.interaction_framework.editor.v14;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import nl.visi.schemas._20140331.ComplexElementTypeType;
import nl.visi.schemas._20140331.MessageInTransactionTypeType;
import nl.visi.schemas._20140331.MessageTypeType;
import nl.visi.schemas._20140331.SimpleElementTypeType;

public class MessageInTransactionDialogControl14 extends Control14 {
	private static final String MESSAGE_IN_TRANSACTION_DIALOG = "nl/visi/interaction_framework/editor/swixml/MessageInTransactionDialog14.xml";

	private JDialog dialog;
	private JTree tree_Elements;
	private DefaultTreeModel treeModel;
	private DefaultMutableTreeNode root;

	public MessageInTransactionDialogControl14() throws Exception {
		super();
		dialog = (JDialog) render(MESSAGE_IN_TRANSACTION_DIALOG);
	}

	JDialog getDialog() {
		return dialog;
	}

	void fillTree(MessageInTransactionTypeType mitt) {
		MessageTypeType message = getMessage(mitt);
		root = new DefaultMutableTreeNode(message.getDescription());
		treeModel = new DefaultTreeModel(root);
		tree_Elements.setModel(treeModel);

		List<ComplexElementTypeType> parentComplexElements = getComplexElements(message);
		if (parentComplexElements != null) {
			for (ComplexElementTypeType pce : parentComplexElements) {
				String condition = getFinalCondition(mitt, pce, null);
				DefaultMutableTreeNode parentCe = new DefaultMutableTreeNode(
						pce.getDescription() + (condition != null ? " " + condition : ""));
				root.add(parentCe);
				List<SimpleElementTypeType> parentSimpleElements = getSimpleElements(pce);
				if (parentSimpleElements != null) {
					for (SimpleElementTypeType pse : parentSimpleElements) {
						addSimpleElement(mitt, pce, parentCe, pse);
					}
				}
				List<ComplexElementTypeType> childComplexElements = getComplexElements(pce);
				if (childComplexElements != null) {
					for (ComplexElementTypeType cce : childComplexElements) {
						condition = getFinalCondition(mitt, cce, null);
						DefaultMutableTreeNode childCe = new DefaultMutableTreeNode(
								cce.getDescription() + (condition != null ? " " + condition : ""));
						parentCe.add(childCe);
						List<SimpleElementTypeType> childSimpleElements = getSimpleElements(cce);
						for (SimpleElementTypeType cse : childSimpleElements) {
							addSimpleElement(mitt, cce, childCe, cse);
						}
					}
				}
			}
		}
	}

	private void addSimpleElement(MessageInTransactionTypeType mitt, ComplexElementTypeType ce,
			DefaultMutableTreeNode parentNode, SimpleElementTypeType se) {
		String condition = getFinalCondition(mitt, ce, se);
		DefaultMutableTreeNode seNode = new DefaultMutableTreeNode(
				se.getDescription() + (condition != null ? " " + condition : ""));
		parentNode.add(seNode);
	}
}
