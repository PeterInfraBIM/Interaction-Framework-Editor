package nl.visi.interaction_framework.editor.v14;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.Silver;

public class Editor14 {
	public static String INTERACTION_SCHEMA_NAME = "_3.xsd";
	public static File INTERACTION_FRAMEWORK = new File("_7.xml");

	private static final Store14 store14 = new Store14();
	private static final Loader14 loader14 = new Loader14();
	private static MainFrameControl14 mainFrame;

	public static Store14 getStore14() {
		return store14;
	}
	public static Loader14 getLoader14() {
		return loader14;
	}

	public static MainFrameControl14 getMainFrameControl() {
		return mainFrame;
	}

	public Editor14() {
		try {
			PlasticLookAndFeel laf = new PlasticXPLookAndFeel();
			PlasticLookAndFeel.setCurrentTheme(new Silver());
			PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
			UIManager.setLookAndFeel(laf);
			mainFrame = new MainFrameControl14();
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
		new Editor14();
	}

}
