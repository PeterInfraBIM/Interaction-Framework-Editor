package nl.visi.interaction_framework.editor.v16;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

class DateField16 extends Control16 implements PropertyChangeListener {
	private JPanel parentPanel;
	private JDateChooser dateChooser;

	public DateField16(JPanel parentPanel) {
		super();
		this.parentPanel = parentPanel;
		dateChooser = new JDateChooser();
		dateChooser.addPropertyChangeListener(this);
		this.parentPanel.add(dateChooser, BorderLayout.CENTER);
	}

	public Date getDate() {
		return dateChooser.getDate();
	}

	public void setDate(Date date) {
		if (date != null && date.getTime() == 0L)
			date = null;
		dateChooser.setDate(date);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String propName = e.getPropertyName();
		if (propName.equals("date")) {
			propertyChangeSupport.firePropertyChange("date", e.getOldValue(), e.getNewValue());
		}
	}

	public boolean isEnabled() {
		return dateChooser.isEnabled();
	}

	public void setEnabled(boolean enabled) {
		dateChooser.setEnabled(enabled);
	}
}
