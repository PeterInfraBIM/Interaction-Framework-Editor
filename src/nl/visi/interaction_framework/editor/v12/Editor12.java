package nl.visi.interaction_framework.editor.v12;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.Silver;

public class Editor12 {
	public static String INTERACTION_SCHEMA_NAME = "_3.xsd";
	public static File INTERACTION_FRAMEWORK = new File("_7.xml");

	private static final Store12 store12 = new Store12();
	private static final Loader12 loader12 = new Loader12();
	private static MainFrameControl12 mainFrame;

	public static Store12 getStore12() {
		return store12;
	}

	public static Loader12 getLoader12() {
		return loader12;
	}

	public static MainFrameControl12 getMainFrameControl() {
		return mainFrame;
	}

	public Editor12() {
		try {
			PlasticLookAndFeel laf = new PlasticXPLookAndFeel();
			PlasticLookAndFeel.setCurrentTheme(new Silver());
			PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);
			UIManager.setLookAndFeel(laf);
			mainFrame = new MainFrameControl12();
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
		new Editor12();
	}

}
