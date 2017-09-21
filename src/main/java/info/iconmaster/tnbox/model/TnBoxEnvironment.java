package info.iconmaster.tnbox.model;

import java.io.InputStream;
import java.io.PrintStream;

public class TnBoxEnvironment {
	public PrintStream out = System.out;
	public PrintStream err = System.err;
	public InputStream in = System.in;
}
