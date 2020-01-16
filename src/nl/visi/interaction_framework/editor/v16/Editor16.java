package nl.visi.interaction_framework.editor.v16;

public class Editor16 {
//	public static String INTERACTION_SCHEMA_NAME = "_3.xsd";
//	public static File INTERACTION_FRAMEWORK = new File("_7.xml");

	private static final Store16 store16 = new Store16();
	private static final Loader16 loader16 = new Loader16();

	public static Store16 getStore16() {
		return store16;
	}

	public static Loader16 getLoader16() {
		return loader16;
	}

}
