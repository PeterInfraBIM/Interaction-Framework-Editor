package nl.visi.interaction_framework.editor.v16;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.table.AbstractTableModel;
import javax.xml.datatype.XMLGregorianCalendar;

import org.swixml.SwingEngine;

import nl.visi.schemas._20160331.ElementType;
import nl.visi.schemas._20160331.ObjectFactory;

abstract class Control16 {
	public static final String RESOURCE_BUNDLE = "nl.visi.interaction_framework.editor.locale.Editor";
	private static final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
	private static final java.text.DateFormat sdfDate = SimpleDateFormat.getDateInstance();
	private static final java.text.DateFormat sdfDateTime = SimpleDateFormat.getDateTimeInstance();
	private final SwingEngine swingEngine;
	protected static final ObjectFactory objectFactory = new ObjectFactory();
	protected static final GregorianCalendar gcal = new GregorianCalendar();
	protected PropertyChangeSupport propertyChangeSupport;
	protected static String user = "???";
	protected static Preferences userPrefs = Preferences.userNodeForPackage(Control16.class);

	@SuppressWarnings("serial")
	public abstract class ElementsTableModel<T extends ElementType> extends AbstractTableModel {

		public List<T> elements = new ArrayList<T>();
		private boolean sorted = true;

		public boolean isSorted() {
			return sorted;
		}

		public void setSorted(boolean sorted) {
			this.sorted = sorted;
		}

		@Override
		public int getRowCount() {
			return elements.size();
		}

		public void clear() {
			int rowCount = getRowCount();
			elements.clear();
			if (rowCount > 0) {
				fireTableRowsDeleted(0, rowCount - 1);
			}
		}

		public int add(T element) {
			int row = getRowCount();
			boolean inserted = false;
			if (sorted) {
				for (int index = 0; !inserted && index < elements.size(); index++) {
					String sortId1 = getSortId(elements.get(index));
					String sortId2 = getSortId(element);
					if (sortId1.compareToIgnoreCase(sortId2) > 0) {
						row = index;
						elements.add(index, element);
						inserted = true;
					}
				}
			}
			if (!inserted) {
				elements.add(element);
			}
			fireTableRowsInserted(row, row);
			return row;
		}

		public T remove(int row) {
			T element = elements.get(row);
			elements.remove(row);
			fireTableRowsDeleted(row, row);
			return element;
		}

		public void update(int row) {
			fireTableRowsUpdated(row, row);
		}

		public T get(int index) {
			return elements.get(index);
		}

		protected String getSortId(T element) {
			return element.getId();
		}

	}

	public Control16() {
		swingEngine = new SwingEngine(this);
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	protected static ResourceBundle getBundle() {
		return bundle;
	}

	protected Component render(String swiXmlResource) throws Exception {
		Component component = null;
		component = swingEngine.render(swiXmlResource);
		return component;
	}

	protected String getDate(XMLGregorianCalendar dateTime) {
		return dateTime != null ? sdfDate.format(dateTime.toGregorianCalendar().getTime()) : "";
	}

	protected String getDateTime(XMLGregorianCalendar dateTime) {
		return dateTime != null ? sdfDateTime.format(dateTime.toGregorianCalendar().getTime()) : "";
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

}
