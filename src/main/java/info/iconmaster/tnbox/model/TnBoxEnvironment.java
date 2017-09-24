package info.iconmaster.tnbox.model;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import info.iconmaster.typhon.model.Field;

public class TnBoxEnvironment {
	public PrintStream out = System.out;
	public PrintStream err = System.err;
	public InputStream in = System.in;
	
	public Map<Field, TnBoxObject> globalFields = new HashMap<>();
}
