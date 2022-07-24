package nl.visi.interaction_framework.editor.v16;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType.ComplexElements;
import nl.visi.schemas._20160331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.MessageTypeType;
import nl.visi.schemas._20160331.ObjectFactory;
import nl.visi.schemas._20160331.SimpleElementTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;

@SuppressWarnings("serial")
public class ComplexElementTreeTransferHandler<E extends ElementType> extends TransferHandler {
	private final PanelControl16<E> panelControl;
	private final JTree tree_ComplexElements;
	private final ObjectFactory objectFactory;
	private final DefaultMutableTreeNode complexElementsRoot;
	private DefaultTreeModel complexElementsTreeModel;
	private JTree.DropLocation dropLocation;
	private MessageTypeType selectedMessage;
	private ComplexElementTypeType selectedComplexElement;
	private SimpleElementTypeType simpleElement;
	private ComplexElementTypeType complexElement;
	private ComplexElementTypeType dropElement;
	private DefaultMutableTreeNode dropNode;
	private DefaultMutableTreeNode movedNode;

	public ComplexElementTreeTransferHandler(PanelControl16<E> panelControl, JTree tree) {
		super();
		this.panelControl = panelControl;
		this.tree_ComplexElements = tree;
		this.complexElementsTreeModel = (DefaultTreeModel) this.tree_ComplexElements.getModel();
		this.complexElementsRoot = (DefaultMutableTreeNode) complexElementsTreeModel.getRoot();
		this.objectFactory = new ObjectFactory();
	}

	private E getSelectedElement() {
		E selectedElement = panelControl.selectedElement;
		if (selectedElement instanceof MessageTypeType) {
			selectedMessage = (MessageTypeType) selectedElement;
			selectedComplexElement = null;
		} else {
			selectedComplexElement = (ComplexElementTypeType) selectedElement;
			selectedMessage = null;
		}
		return selectedElement;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	@Override
	protected Transferable createTransferable(JComponent c) {
		TreePath[] paths = tree_ComplexElements.getSelectionPaths();
		if (paths != null && paths.length == 1) {
			movedNode = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
			Object userObject = movedNode.getUserObject();
			if (userObject instanceof SimpleElementTypeType) {
				return new StringSelection(((SimpleElementTypeType) userObject).getId());
			} else {
				return new StringSelection(((ComplexElementTypeType) userObject).getId());
			}
		}
		return null;
	}

	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action == MOVE) {
			Object parentObject = ((DefaultMutableTreeNode) movedNode.getParent()).getUserObject();
			Object dropObject = ((DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent())
					.getUserObject();
			if (parentObject instanceof ComplexElementTypeType && simpleElement != null) {
				ComplexElementTypeType parentCe = (ComplexElementTypeType) parentObject;
				int index = 0;
				int foundIndex = -1;
				boolean found = false;
				SimpleElements simpleElements = parentCe.getSimpleElements();
				if (simpleElements != null) {
					List<Object> refs = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
					for (Object ref : refs) {
						if (index == dropLocation.getChildIndex() && dropObject.equals(parentObject)) {
							index++;
							continue;
						}
						SimpleElementTypeType se = null;
						if (ref instanceof SimpleElementTypeType) {
							se = (SimpleElementTypeType) ref;
						} else {
							se = (SimpleElementTypeType) (((SimpleElementTypeTypeRef) ref).getIdref());

						}
						if (se != null) {
							if (se.getId().equals(simpleElement.getId())) {
								found = true;
								foundIndex = index;
								break;
							}
						}
						index++;
					}
					if (found) {
						refs.remove(foundIndex);
					}
				}
			} else {
				int index = 0;
				int foundIndex = -1;
				boolean found = false;
				List<Object> refs = null;
				E selectedElement = getSelectedElement();
				if (selectedElement instanceof MessageTypeType) {
					refs = parentObject instanceof String
							? selectedMessage.getComplexElements().getComplexElementTypeOrComplexElementTypeRef()
							: ((ComplexElementTypeType) parentObject).getComplexElements()
									.getComplexElementTypeOrComplexElementTypeRef();
				} else {
					refs = parentObject instanceof String
							? selectedComplexElement.getComplexElements().getComplexElementTypeOrComplexElementTypeRef()
							: ((ComplexElementTypeType) parentObject).getComplexElements()
									.getComplexElementTypeOrComplexElementTypeRef();
				}
				for (Object ref : refs) {
					if (index == dropLocation.getChildIndex()) {
						index++;
						continue;
					}
					ComplexElementTypeType ce = null;
					if (ref instanceof ComplexElementTypeType) {
						ce = (ComplexElementTypeType) ref;
					} else {
						ce = (ComplexElementTypeType) (((ComplexElementTypeTypeRef) ref).getIdref());

					}
					if (ce != null) {
						if (ce.getId().equals(complexElement.getId())) {
							found = true;
							foundIndex = index;
							break;
						}
					}
					index++;
				}
				if (found) {
					refs.remove(foundIndex);
				}
			}
			complexElementsTreeModel.removeNodeFromParent(movedNode);
		}
		movedNode = null;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				String transferData = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
				String id = transferData.split("\t")[0];
				simpleElement = Editor16.getStore16().getElement(SimpleElementTypeType.class, id);
				if (simpleElement != null) {
					return canImport(support, simpleElement);
				} else {
					complexElement = Editor16.getStore16().getElement(ComplexElementTypeType.class, id);
					if (complexElement != null) {
						return canImport(support, complexElement);
					}
				}
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean canImport(TransferSupport support, ComplexElementTypeType complexElement) {
		if (dropNode != null) {
			// Remove previous drop node selection if any
			tree_ComplexElements.removeSelectionPath(new TreePath(dropNode.getPath()));
		}
		// set current drop location
		dropLocation = (javax.swing.JTree.DropLocation) support.getDropLocation();

		// Check if the list of complex elements of the selected message type is null or
		// empty
		E selectedElement = getSelectedElement();
		if (selectedElement instanceof MessageTypeType) {
			if (selectedMessage.getComplexElements() == null
					|| selectedMessage.getComplexElements().getComplexElementTypeOrComplexElementTypeRef().isEmpty()) {
				// Add new complex type to empty message type
				return true;
			}
		}

		TreePath path = dropLocation.getPath();
		if (path != null) {
			dropNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (movedNode != null) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) movedNode.getParent();
				if (!(dropNode.getUserObject() instanceof ComplexElementTypeType)
						&& !(parentNode.getUserObject() instanceof ComplexElementTypeType)) {
					// Add complex type to message type if it is not a sub-complexelementtype
					return true;
				} else {
					if (parentNode.getUserObject() instanceof ComplexElementTypeType && dropNode.equals(parentNode)
							&& dropLocation.getChildIndex() >= 0
							&& dropLocation.getChildIndex() <= dropNode.getChildCount()) {

						List<ComplexElementTypeType> complexElements = Control16
								.getComplexElements((ComplexElementTypeType) parentNode.getUserObject());
						if (complexElements == null || complexElements.isEmpty()) {
							return true;
						} else if (dropLocation.getChildIndex() <= dropNode.getChildCount() - complexElements.size()) {
							return true;
						}
					}
				}
			} else {
				if (dropLocation.getChildIndex() == -1) {
					// Add complex type to complex type (as table)
					tree_ComplexElements.setSelectionPath(new TreePath(dropNode.getPath()));
					return true;
				} else if (!(dropNode.getUserObject() instanceof ComplexElementTypeType)) {
					// Add new complex type to selected message type.
					return true;
				}
			}
		}

		return false;
	}

	private boolean canImport(TransferSupport support, SimpleElementTypeType simpleElement) {
		dropLocation = (javax.swing.JTree.DropLocation) support.getDropLocation();
		TreePath path = dropLocation.getPath();
		if (path != null) {
			dropNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (dropNode.getUserObject() instanceof ComplexElementTypeType) {
				ComplexElementTypeType ce = (ComplexElementTypeType) dropNode.getUserObject();
				List<ComplexElementTypeType> complexElements = Control16.getComplexElements(ce);
				List<SimpleElementTypeType> simpleElements = Control16.getSimpleElements(ce);
				if (simpleElements != null && simpleElements.contains(simpleElement)) {
					if (dropLocation.getChildIndex() >= 0 && dropLocation.getChildIndex() <= dropNode.getChildCount()) {
						if (complexElements == null || complexElements.isEmpty()) {
							if (dropLocation.getChildIndex() >= 0) {
								return true;
							}
						} else {
							if (dropLocation.getChildIndex() >= complexElements.size()) {
								return true;
							}
						}
					}
				} else if (movedNode == null) {
					if (complexElements == null || complexElements.isEmpty()) {
						if (dropLocation.getChildIndex() >= 0) {
							return true;
						}
					} else {
						if (dropLocation.getChildIndex() >= complexElements.size()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean importData(TransferSupport support) {
		if (dropLocation.getPath() == null) {
			ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
			ref.setIdref(complexElement);
			E selectedElement = getSelectedElement();
			if (selectedElement instanceof MessageTypeType) {
				MessageTypeType.ComplexElements complexElements = selectedMessage.getComplexElements();
				if (complexElements == null) {
					complexElements = objectFactory.createMessageTypeTypeComplexElements();
					selectedMessage.setComplexElements(complexElements);
				}
				List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				list.add(0, ref);
			} else {
				ComplexElementTypeType.ComplexElements complexElements = ((ComplexElementTypeType) selectedElement)
						.getComplexElements();
				if (complexElements == null) {
					complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
					selectedComplexElement.setComplexElements(complexElements);
				}
				List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
				list.add(0, ref);
			}
			panelControl.updateLaMu(selectedElement, panelControl.getUser());
			DefaultMutableTreeNode complexNode = new DefaultMutableTreeNode(complexElement);
			complexElementsTreeModel.insertNodeInto(complexNode, complexElementsRoot, 0);
			tree_ComplexElements.expandPath(new TreePath(complexElementsRoot.getPath()));
			showComplexNode(complexNode);
			return true;
		} else {
			dropNode = (DefaultMutableTreeNode) dropLocation.getPath().getLastPathComponent();
			if (dropNode.getUserObject() instanceof ComplexElementTypeType) {
				dropElement = (ComplexElementTypeType) dropNode.getUserObject();
				if (simpleElement != null && dropLocation.getChildIndex() > -1) {
					SimpleElementTypeTypeRef ref = objectFactory.createSimpleElementTypeTypeRef();
					ref.setIdref(simpleElement);
					ComplexElementTypeType.SimpleElements simpleElements = dropElement.getSimpleElements();
					if (simpleElements == null) {
						simpleElements = objectFactory.createComplexElementTypeTypeSimpleElements();
						dropElement.setSimpleElements(simpleElements);
					}
					List<Object> list = simpleElements.getSimpleElementTypeOrSimpleElementTypeRef();
					List<ComplexElementTypeType> complexElements = Control16.getComplexElements(dropElement);
					if (complexElements == null || complexElements.isEmpty()) {
						list.add(dropLocation.getChildIndex(), ref);
					} else {
						list.add(dropLocation.getChildIndex() - complexElements.size(), ref);
					}
					panelControl.updateLaMu(dropElement, panelControl.getUser());
					complexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(simpleElement), dropNode,
							dropLocation.getChildIndex());
					return true;
				} else {
					ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
					ref.setIdref(complexElement);
					ComplexElements complexElements = dropElement.getComplexElements();
					if (complexElements == null) {
						complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
						dropElement.setComplexElements(complexElements);
					}
					List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
					list.add(ref);
					panelControl.updateLaMu(dropElement, panelControl.getUser());
					DefaultMutableTreeNode complexNode = new DefaultMutableTreeNode(complexElement);
					complexElementsTreeModel.insertNodeInto(complexNode, dropNode,
							dropLocation.getChildIndex() == -1 ? 0 : dropLocation.getChildIndex());
					tree_ComplexElements.expandPath(new TreePath(dropNode.getPath()));
					List<SimpleElementTypeType> simpleList = Control16.getSimpleElements(complexElement);
					if (simpleList != null) {
						int index = 0;
						for (SimpleElementTypeType se : simpleList) {
							DefaultMutableTreeNode simpleSubNode = new DefaultMutableTreeNode(se);
							complexElementsTreeModel.insertNodeInto(simpleSubNode, complexNode, index++);
							tree_ComplexElements.expandPath(new TreePath(simpleSubNode.getPath()));
						}
						tree_ComplexElements.expandPath(new TreePath(complexNode.getPath()));
					}
					return true;
				}
			} else {
				ComplexElementTypeTypeRef ref = objectFactory.createComplexElementTypeTypeRef();
				ref.setIdref(complexElement);
				E selectedElement = getSelectedElement();
				if (selectedElement instanceof MessageTypeType) {
					MessageTypeType.ComplexElements complexElements = selectedMessage.getComplexElements();
					if (complexElements == null) {
						complexElements = objectFactory.createMessageTypeTypeComplexElements();
						selectedMessage.setComplexElements(complexElements);
					}
					List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
					list.add(dropLocation.getChildIndex(), ref);
				} else {
					ComplexElementTypeType.ComplexElements complexElements = ((ComplexElementTypeType) selectedElement)
							.getComplexElements();
					if (complexElements == null) {
						complexElements = objectFactory.createComplexElementTypeTypeComplexElements();
						selectedComplexElement.setComplexElements(complexElements);
					}
					List<Object> list = complexElements.getComplexElementTypeOrComplexElementTypeRef();
					list.add(dropLocation.getChildIndex(), ref);
				}
				panelControl.updateLaMu(selectedElement, panelControl.getUser());
				DefaultMutableTreeNode complexNode = new DefaultMutableTreeNode(complexElement);
				complexElementsTreeModel.insertNodeInto(complexNode, complexElementsRoot, dropLocation.getChildIndex());
				showComplexNode(complexNode);
				return true;
			}
		}
	}

	void showComplexNode(DefaultMutableTreeNode complexNode) {
		int index = 0;
		List<SimpleElementTypeType> simpleList = Control16.getSimpleElements(complexElement);
		if (simpleList != null) {
			for (SimpleElementTypeType se : simpleList) {
				complexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(se), complexNode, index++);
			}
			tree_ComplexElements.expandPath(new TreePath(complexNode.getPath()));
		}
		List<ComplexElementTypeType> complexList = Control16.getComplexElements(complexElement);
		if (complexList != null) {
			for (ComplexElementTypeType ce : complexList) {
				DefaultMutableTreeNode complexSubNode = new DefaultMutableTreeNode(ce);
				complexElementsTreeModel.insertNodeInto(complexSubNode, complexNode, index++);
				List<SimpleElementTypeType> seList = Control16.getSimpleElements(ce);
				if (seList != null) {
					int index2 = 0;
					for (SimpleElementTypeType se : seList) {
						complexElementsTreeModel.insertNodeInto(new DefaultMutableTreeNode(se), complexSubNode,
								index2++);
					}
				}
				tree_ComplexElements.expandPath(new TreePath(complexSubNode.getPath()));
			}
			tree_ComplexElements.expandPath(new TreePath(complexNode.getPath()));
		}
	}

}
