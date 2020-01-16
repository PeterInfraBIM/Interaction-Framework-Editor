package nl.visi.interaction_framework.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.swixml.SwingEngine;

public class Control {
	public static final String RESOURCE_BUNDLE = "nl.visi.interaction_framework.editor.locale.Editor";
	private static final ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE);
	protected static Preferences userPrefs = Preferences.userNodeForPackage(Control.class);
	protected static String user = "???";
	private final SwingEngine swingEngine;
	protected PropertyChangeSupport propertyChangeSupport;

	public Control() {
		swingEngine = new SwingEngine(this);
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	protected Component render(String swiXmlResource) throws Exception {
		Component component = null;
		component = swingEngine.render(swiXmlResource);
		return component;
	}

	protected void renderCheck(String swiXmlResource) throws Exception {
		Component component = swingEngine.render(swiXmlResource);
		JFrame frame = new JFrame("Render Check");
		frame.add(component, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
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

	protected static ResourceBundle getBundle() {
		return bundle;
	}
}
