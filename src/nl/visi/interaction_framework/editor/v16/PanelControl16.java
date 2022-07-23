package nl.visi.interaction_framework.editor.v16;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import nl.visi.interaction_framework.editor.DateField;
import nl.visi.interaction_framework.editor.DocumentAdapter;
import nl.visi.interaction_framework.editor.InteractionFrameworkEditor;
import nl.visi.schemas._20160331.ComplexElementTypeType;
import nl.visi.schemas._20160331.ComplexElementTypeType.SimpleElements;
import nl.visi.schemas._20160331.ComplexElementTypeTypeRef;
import nl.visi.schemas._20160331.ObjectFactory;
import nl.visi.schemas._20160331.ElementConditionType;
import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.ElementTypeRef;
import nl.visi.schemas._20160331.ProjectTypeType;
import nl.visi.schemas._20160331.SimpleElementTypeTypeRef;

abstract class PanelControl16<E extends ElementType> extends Control16 {
	enum Fields {
		Id, Description, State, DateLaMu, UserLaMu, Language, Category, HelpInfo, Code;
	}

	protected JPanel panel;
	protected E selectedElement;
	protected int selectedRow;
	protected ElementsTableModel<E> elementsTableModel;
	protected JTable tbl_Elements;
	protected JButton btn_NewElement, btn_CopyElement, btn_DeleteElement;
	protected JTextField tfd_Filter, tfd_Id, tfd_Description, tfd_State, tfd_DateLamu, tfd_UserLamu, tfd_Language,
			tfd_Category, tfd_HelpInfo, tfd_Code;
	protected DateField startDateField, endDateField;
	protected boolean inSelection = false;

	@SuppressWarnings("serial")
	public class NavigatorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
		protected static final String EDIT = "edit";

		JButton button;

		public NavigatorEditor() {
			ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(getClass().getResource("/" + getBundle().getString("img_ForwardNav"))));
			button = new JButton(icon);
			button.setActionCommand(EDIT);
			button.addActionListener(this);
			button.setBorderPainted(false);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			return button;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (EDIT.equals(e.getActionCommand())) {
				navigate();
				fireEditingCanceled(); // Make the renderer reappear.
			}
		}

		protected void navigate() {
			InteractionFrameworkEditor.navigate(selectedElement);
		}
	}

	@SuppressWarnings("serial")
	private class ButtonTableCellRenderer extends JButton implements TableCellRenderer {

		public ButtonTableCellRenderer() {
			ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
					.getImage(getClass().getResource("/" + getBundle().getString("img_ForwardNav"))));
			setIcon(icon);
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (isSelected) {
				setForeground(table.getSelectionForeground());
				setBackground(table.getSelectionBackground());
			} else {
				setForeground(table.getForeground());
				setBackground(UIManager.getColor("Button.background"));
			}
			return this;
		}
	}

	private ButtonTableCellRenderer buttonTableCellRenderer;

	protected ButtonTableCellRenderer getButtonTableCellRenderer() {
		if (buttonTableCellRenderer == null) {
			buttonTableCellRenderer = new ButtonTableCellRenderer();
		}
		return buttonTableCellRenderer;
	}

	protected PanelControl16() {
		super();
	}

	protected PanelControl16(String swixml) throws Exception {
		panel = (JPanel) render(swixml);

		tfd_Id.getDocument().addDocumentListener(new DocumentAdapter() {
			@Override
			protected void update(DocumentEvent e) {
				setField(Fields.Id);
			}
		});
		if (tfd_Description != null) {
			tfd_Description.getDocument().addDocumentListener(new DocumentAdapter() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Description);
				}
			});
		}
		if (tfd_State != null) {
			tfd_State.getDocument().addDocumentListener(new DocumentAdapter() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.State);
				}
			});
		}
		if (tfd_Language != null) {
			tfd_Language.getDocument().addDocumentListener(new DocumentAdapter() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Language);
				}
			});
		}
		if (tfd_Category != null) {
			tfd_Category.getDocument().addDocumentListener(new DocumentAdapter() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Category);
				}
			});
		}
		if (tfd_HelpInfo != null) {
			tfd_HelpInfo.getDocument().addDocumentListener(new DocumentAdapter() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.HelpInfo);
				}
			});
		}
		if (tfd_Code != null) {
			tfd_Code.getDocument().addDocumentListener(new DocumentAdapter() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Code);
				}
			});
		}
	}

	protected void setField(Fields field) {
		if (inSelection)
			return;

		Store16 store = Editor16.getStore16();

		if (field == Fields.Id) {
			String oldId = selectedElement.getId();
			String newId = tfd_Id.getText();
			try {
				store.renameId(oldId, newId);
			} catch (Exception e) {
				tfd_Id.setForeground(Color.RED);
				return;
			}
			tfd_Id.setForeground(Color.BLACK);
		}

		if (selectedElement == null)
			return;

		try {
			switch (field) {
			case Category:
				Method setCategory = selectedElement.getClass().getMethod("setCategory",
						new Class<?>[] { String.class });
				setCategory.invoke(selectedElement, tfd_Category.getText());
				break;
			case Code:
				Method setCode = selectedElement.getClass().getMethod("setCode", new Class<?>[] { String.class });
				setCode.invoke(selectedElement, tfd_Code.getText());
				break;
			case Description:
				Method setDescription = selectedElement.getClass().getMethod("setDescription",
						new Class<?>[] { String.class });
				setDescription.invoke(selectedElement, tfd_Description.getText());
				break;
			case HelpInfo:
				Method setHelpInfo = selectedElement.getClass().getMethod("setHelpInfo",
						new Class<?>[] { String.class });
				setHelpInfo.invoke(selectedElement, tfd_HelpInfo.getText());
				break;
			case Id:
				selectedElement.setId(tfd_Id.getText());
				break;
			case Language:
				Method setLanguage = selectedElement.getClass().getMethod("setLanguage",
						new Class<?>[] { String.class });
				setLanguage.invoke(selectedElement, tfd_Language.getText());
				break;
			case State:
				Method setState = selectedElement.getClass().getMethod("setState", new Class<?>[] { String.class });
				setState.invoke(selectedElement, tfd_State.getText());
				break;
			default:
				break;
			}
		} catch (Exception e) {
		}

		try {
			updateLaMu(selectedElement, getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
	}

	public JPanel getPanel() {
		return panel;
	}

	protected void fillTable(Class<E> T) {
		List<E> elements = Editor16.getStore16().getElements(T);
		elementsTableModel.clear();
		for (E role : elements) {
			elementsTableModel.add(role);
		}
	}

	public abstract void fillTable();

	protected ElementType getElementType(Object object) {
		ElementType elementType = null;
		if (object instanceof ElementType) {
			elementType = (ElementType) object;
		} else if (object instanceof ElementTypeRef) {
			elementType = (ElementType) ((ElementTypeRef) object).getIdref();
		}
		return elementType;
	}

	protected void updateLaMu(ElementType element, String user) {
		if (inSelection)
			return;
		if (element instanceof ElementConditionType)
			return;

		try {
			gcal.setTime(new Date());
			XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

			Method setDateLaMu = element.getClass().getMethod("setDateLaMu",
					new Class[] { XMLGregorianCalendar.class });
			setDateLaMu.invoke(element, xgcal);
			Method setUserLaMu = element.getClass().getMethod("setUserLaMu", new Class[] { String.class });
			setUserLaMu.invoke(element, user);
			tfd_DateLamu.setText(sdfDateTime.format(xgcal.toGregorianCalendar().getTime()));
			tfd_UserLamu.setText(user);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	protected String newElement(ElementType newElement, String prefix) throws Exception {
		String newId = null;
		if (prefix.startsWith("Standard")) {
			newId = prefix;
		} else {
			newId = Editor16.getStore16().getNewId(prefix);
		}
		newElement.setId(newId);
		if (newElement instanceof ProjectTypeType) {
			ProjectTypeType newProjectElement = (ProjectTypeType) newElement;
			String namespace = newProjectElement.getNamespace();
			if (namespace == null || namespace.isEmpty()) {
				newProjectElement.setNamespace("http://www.visi.nl/schemas/20160331/NewProject");
			}
			String description = newProjectElement.getDescription();
			if (description == null || description.isEmpty()) {
				newProjectElement.setDescription(getBundle().getString("lbl_DescriptionOf") + " " + newId);
			}
		} else {
			if (newElement.getClass().getDeclaredField("description") != null) {
				Method setDescriptionMethod = newElement.getClass().getDeclaredMethod("setDescription",
						new Class[] { String.class });
				setDescriptionMethod.invoke(newElement, getBundle().getString("lbl_DescriptionOf") + " " + newId);
			}
		}
		Editor16.getStore16().put(newId, newElement);
		updateLaMu(newElement, getUser());
		return newId;
	}

	public <T extends ElementType> TransferHandler getTransferHandler(final JTable table,
			final ElementsTableModel<T> tablemodel, final boolean complex) {
		return new TableTransferHandler<T>(table, tablemodel, complex);
	}

	private static int tableTransferSourceRow = -1;

	@SuppressWarnings("serial")
	public class TableTransferHandler<T extends ElementType> extends TransferHandler {
		JTable table;
		ElementsTableModel<T> tablemodel;
		boolean complex;

		@SuppressWarnings("unchecked")
		public <U extends ElementType> TableTransferHandler(JTable table, ElementsTableModel<U> tablemodel,
				boolean complex) {
			this.table = table;
			this.tablemodel = (ElementsTableModel<T>) tablemodel;
			this.complex = complex;
		}

		@Override
		public int getSourceActions(JComponent component) {
			return MOVE;
		}

		@Override
		protected Transferable createTransferable(JComponent c) {
			tableTransferSourceRow = table.getSelectedRow();
			ElementType et = tablemodel.get(tableTransferSourceRow);
			Transferable transferable = new StringSelection(et.getId());
			return transferable;
		}

		@Override
		public boolean canImport(TransferSupport transferSupport) {
			if (!transferSupport.isDrop()) {
				// No drop support
				return false;
			}

			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(TransferSupport support) {
			try {
				String transferData = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
				if (transferData != null) {
					int index = transferData.indexOf('\t');
					transferData = transferData.substring(0, index != -1 ? index : transferData.length());
				}
				ElementType element = Editor16.getStore16().getElement(ElementType.class, transferData);
				JTable.DropLocation loc = (JTable.DropLocation) support.getDropLocation();
				int targetRow = loc.getRow();
				if (tableTransferSourceRow >= 0) {
					tablemodel.remove(tableTransferSourceRow);
					tablemodel.fireTableRowsDeleted(tableTransferSourceRow, tableTransferSourceRow);
					int row = targetRow > tableTransferSourceRow ? targetRow - 1 : targetRow;
					tablemodel.elements.add(row, (T) element);
					tablemodel.fireTableRowsInserted(row, row);
				} else {
					tablemodel.elements.add(targetRow, (T) element);
					tablemodel.fireTableRowsInserted(targetRow, targetRow);
				}

				if (complex) {
					Method getComplexElements = selectedElement.getClass().getMethod("getComplexElements",
							(Class<?>[]) null);
					Object ceObject = getComplexElements.invoke(selectedElement, (Object[]) null);
					if (ceObject != null) {
						Method getComplexElementTypeOrComplexElementTypeRef = ceObject.getClass()
								.getMethod("getComplexElementTypeOrComplexElementTypeRef", (Class<?>[]) null);
						List<Object> list = (List<Object>) getComplexElementTypeOrComplexElementTypeRef.invoke(ceObject,
								(Object[]) null);
						if (tableTransferSourceRow >= 0) {
							Object object = list.remove(tableTransferSourceRow);
							list.add(targetRow > tableTransferSourceRow ? targetRow - 1 : targetRow, object);
						} else {
							ComplexElementTypeTypeRef seTypeRef = new ObjectFactory().createComplexElementTypeTypeRef();
							seTypeRef.setIdref(element);
							list.add(targetRow, seTypeRef);
						}
					}
				} else {
					Method getSimpleElements = selectedElement.getClass().getMethod("getSimpleElements",
							(Class<?>[]) null);
					Object seObject = getSimpleElements.invoke(selectedElement, (Object[]) null);
					if (seObject == null) {
						seObject = new ObjectFactory().createComplexElementTypeTypeSimpleElements();
						Method setSimpleElements = selectedElement.getClass().getMethod("setSimpleElements",
								SimpleElements.class);
						setSimpleElements.invoke(selectedElement, seObject);
					}
					if (seObject != null) {
						Method getSimpleElementTypeOrSimpleElementTypeRef = seObject.getClass()
								.getMethod("getSimpleElementTypeOrSimpleElementTypeRef", (Class<?>[]) null);
						List<Object> list = (List<Object>) getSimpleElementTypeOrSimpleElementTypeRef.invoke(seObject,
								(Object[]) null);
						if (tableTransferSourceRow >= 0) {
							Object object = list.remove(tableTransferSourceRow);
							list.add(targetRow > tableTransferSourceRow ? targetRow - 1 : targetRow, object);
						} else {
							SimpleElementTypeTypeRef seTypeRef = new ObjectFactory().createSimpleElementTypeTypeRef();
							seTypeRef.setIdref(element);
							list.add(targetRow, seTypeRef);
							
						}
					}
				}
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (SecurityException e) {
				e.printStackTrace();
				return false;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return false;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void exportDone(JComponent source, Transferable data, int action) {
			tableTransferSourceRow = -1;
		}

	}
}
