package nl.visi.interaction_framework.editor.v14;

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

import nl.visi.interaction_framework.editor.v14.DateField14;
import nl.visi.interaction_framework.editor.v14.Editor14;
import nl.visi.interaction_framework.editor.v14.Control14;
import nl.visi.interaction_framework.editor.v14.Store14;
import nl.visi.schemas._20140331.ElementType;
import nl.visi.schemas._20140331.ElementTypeRef;
import nl.visi.schemas._20140331.ProjectTypeType;

abstract class PanelControl14<E extends ElementType> extends Control14 {
	enum Fields {
		Id, Description, State, Language, Category, HelpInfo, Code;
	}

	protected JPanel panel;
	protected E selectedElement;
	protected int selectedRow;
	protected ElementsTableModel<E> elementsTableModel;
	protected JTable tbl_Elements;
	protected JButton btn_NewElement, btn_DeleteElement;
	protected JTextField tfd_Id, tfd_Description, tfd_State, tfd_Language, tfd_Category, tfd_HelpInfo, tfd_Code;
	protected DateField14 startDateField, endDateField;
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
			Editor14.getMainFrameControl().navigate(selectedElement);
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

	protected PanelControl14() {
		super();
	}

	protected PanelControl14(String swixml) throws Exception {
		panel = (JPanel) render(swixml);

		tfd_Id.getDocument().addDocumentListener(new DocumentAdapter14() {
			@Override
			protected void update(DocumentEvent e) {
				setField(Fields.Id);
			}
		});
		if (tfd_Description != null) {
			tfd_Description.getDocument().addDocumentListener(new DocumentAdapter14() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Description);
				}
			});
		}
		if (tfd_State != null) {
			tfd_State.getDocument().addDocumentListener(new DocumentAdapter14() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.State);
				}
			});
		}
		if (tfd_Language != null) {
			tfd_Language.getDocument().addDocumentListener(new DocumentAdapter14() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Language);
				}
			});
		}
		if (tfd_Category != null) {
			tfd_Category.getDocument().addDocumentListener(new DocumentAdapter14() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.Category);
				}
			});
		}
		if (tfd_HelpInfo != null) {
			tfd_HelpInfo.getDocument().addDocumentListener(new DocumentAdapter14() {
				@Override
				protected void update(DocumentEvent e) {
					setField(Fields.HelpInfo);
				}
			});
		}
		if (tfd_Code != null) {
			tfd_Code.getDocument().addDocumentListener(new DocumentAdapter14() {
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

		Store14 store = Editor14.getStore14();

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
			}
		} catch (Exception e) {
		}

		try {
			updateLaMu(selectedElement, user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		elementsTableModel.fireTableRowsUpdated(selectedRow, selectedRow);
	}

	public JPanel getPanel() {
		return panel;
	}

	protected void fillTable(Class<E> T) {
		List<E> elements = Editor14.getStore14().getElements(T);
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

		try {
			gcal.setTime(new Date());
			XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);

			Method setDateLaMu = element.getClass().getMethod("setDateLaMu",
					new Class[] { XMLGregorianCalendar.class });
			setDateLaMu.invoke(element, xgcal);
			Method setUserLaMu = element.getClass().getMethod("setUserLaMu", new Class[] { String.class });
			setUserLaMu.invoke(element, user);
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

	protected void newElement(ElementType newElement, String prefix) throws Exception {
		String newId = null;
		if (prefix.startsWith("Standard")) {
			newId = prefix;
		} else {
			newId = Editor14.getStore14().getNewId(prefix);
		}
		newElement.setId(newId);
		if (newElement.getClass().getDeclaredField("description") != null) {
			Method setDescriptionMethod = newElement.getClass().getDeclaredMethod("setDescription",
					new Class[] { String.class });
			setDescriptionMethod.invoke(newElement, getBundle().getString("lbl_DescriptionOf") + " " + newId);
		}
		if (newElement instanceof ProjectTypeType) {
			ProjectTypeType newProjectElement = (ProjectTypeType) newElement;
			newProjectElement.setNamespace("http://www.visi.nl/schemas/20160331/NewProject");
		}
//		if (!(newElement instanceof ElementConditionType)) {
//			Method setStateMethod = newElement.getClass().getDeclaredMethod("setState", new Class[] { String.class });
//			setStateMethod.invoke(newElement, "active");
//			updateLaMu(newElement, user);
//			try {
//				Method setStartDateMethod = newElement.getClass().getDeclaredMethod("setStartDate",
//						new Class[] { XMLGregorianCalendar.class });
//				Method setEndDateMethod = newElement.getClass().getDeclaredMethod("setEndDate",
//						new Class[] { XMLGregorianCalendar.class });
//				try {
//					gcal.setTime(new Date());
//					GregorianCalendar endDate = new GregorianCalendar();
//					long yearInMillis = Math.round(1000 * 60 * 60 * 24 * 365.2425);
//					endDate.setTimeInMillis(gcal.getTimeInMillis() + yearInMillis);
//					XMLGregorianCalendar xEndDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(endDate);
//					XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
//					setStartDateMethod.invoke(newElement, xgcal);
//					setEndDateMethod.invoke(newElement, xEndDate);
//				} catch (DatatypeConfigurationException e) {
//					e.printStackTrace();
//				}
//			} catch (NoSuchMethodException e) {
//			}
//		}
		Editor14.getStore14().put(newId, newElement);
		updateLaMu(newElement, MainFrameControl14.user);
	}

	@SuppressWarnings("serial")
	public <T extends ElementType> TransferHandler getTransferHandler(final JTable table,
			final ElementsTableModel<T> tablemodel, final boolean complex) {
		return new TransferHandler() {
			int sourceRow = -1;

			@Override
			public int getSourceActions(JComponent component) {
				return MOVE;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				sourceRow = table.getSelectedRow();
				ElementType et = tablemodel.get(sourceRow);
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
					ElementType element = Editor14.getStore14().getElement(ElementType.class, transferData);
					JTable.DropLocation loc = (JTable.DropLocation) support.getDropLocation();
					int targetRow = loc.getRow();
					tablemodel.remove(sourceRow);
					tablemodel.elements.add(targetRow > sourceRow ? targetRow - 1 : targetRow, (T) element);

					if (complex) {
						Method getComplexElements = selectedElement.getClass().getMethod("getComplexElements",
								(Class<?>[]) null);
						Object ceObject = getComplexElements.invoke(selectedElement, (Object[]) null);
						if (ceObject != null) {
							Method getComplexElementTypeOrComplexElementTypeRef = ceObject.getClass()
									.getMethod("getComplexElementTypeOrComplexElementTypeRef", (Class<?>[]) null);
							List<Object> list = (List<Object>) getComplexElementTypeOrComplexElementTypeRef
									.invoke(ceObject, (Object[]) null);
							Object object = list.remove(sourceRow);
							list.add(targetRow > sourceRow ? targetRow - 1 : targetRow, object);
						}
					} else {
						Method getSimpleElements = selectedElement.getClass().getMethod("getSimpleElements",
								(Class<?>[]) null);
						Object seObject = getSimpleElements.invoke(selectedElement, (Object[]) null);
						if (seObject != null) {
							Method getSimpleElementTypeOrSimpleElementTypeRef = seObject.getClass()
									.getMethod("getSimpleElementTypeOrSimpleElementTypeRef", (Class<?>[]) null);
							List<Object> list = (List<Object>) getSimpleElementTypeOrSimpleElementTypeRef
									.invoke(seObject, (Object[]) null);
							Object object = list.remove(sourceRow);
							list.add(targetRow > sourceRow ? targetRow - 1 : targetRow, object);
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
		};
	}
}
