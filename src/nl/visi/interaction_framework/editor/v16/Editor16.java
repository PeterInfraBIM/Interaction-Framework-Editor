package nl.visi.interaction_framework.editor.v16;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.Silver;

public class Editor16 {
	public static String INTERACTION_SCHEMA_NAME = "_3.xsd";
	public static File INTERACTION_FRAMEWORK = new File("_7.xml");

	private static final Store16 store16 = new Store16();
	private static final Loader16 loader16 = new Loader16();
	private static MainFrameControl16 mainFrame;

	public static Store16 getStore16() {
		return store16;
	}
	public static Loader16 getLoader16() {
		return loader16;
	}

	public static MainFrameControl16 getMainFrameControl() {
		return mainFrame;
	}

	public Editor16() {
		try {
			PlasticLookAndFeel laf = new PlasticXPLookAndFeel();
			PlasticLookAndFeel.setCurrentTheme(new Silver());
			PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
			UIManager.setLookAndFeel(laf);
			mainFrame = new MainFrameControl16();
			mainFrame.show();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Editor16();
	}

}
