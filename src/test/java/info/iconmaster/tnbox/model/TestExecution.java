package info.iconmaster.tnbox.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.runners.Parameterized;

import info.iconmaster.tnbox.TnBox;
import info.iconmaster.tnbox.TyphonTest;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.compiler.TyphonCompiler;
import info.iconmaster.typhon.linker.TyphonLinker;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.model.Package;
import info.iconmaster.typhon.model.TyphonModelReader;
import info.iconmaster.typhon.plugins.PluginLoader;
import info.iconmaster.typhon.plugins.TyphonPlugin;
import info.iconmaster.typhon.types.TyphonTypeResolver;

/**
 * Tests the ANTLR grammar.
 * 
 * @author iconmaster
 *
 */
public class TestExecution extends TyphonTest {
	@Parameterized.Parameters
    public static Collection<Object[]> data() {
		return TyphonTest.makeData(
			new CaseValid("@main void f() {print(1);}", "1"),
			new CaseValid("@main void f() {print(1); print(2);}", "12"),
			new CaseValid("@main void f() {println(1); print(2);}", "1\n2"),
			new CaseValid("@main void f() {println(1); println(2);}", "1\n2\n"),
			new CaseValid("@main void f() {}", "")
		);
	}
    
    private static class CaseValid implements Runnable {
    	String input, outputExpected, outputGot;
    	
		public CaseValid(String input, String output) {
			this.input = input;
			this.outputExpected = output;
			outputGot = "";
		}
		
		@Override
		public void run() {
			if (PluginLoader.plugins.isEmpty()) {
				PluginLoader.plugins.add(TnBox.class);
				PluginLoader.runHook(TyphonPlugin.OnLoad.class);
			}
			
			TyphonInput tni = new TyphonInput();
			Package p = TyphonModelReader.parseString(tni, input);
			TyphonLinker.link(p);
			TyphonTypeResolver.resolve(p);
			TyphonCompiler.compile(p);
			
			Assert.assertTrue("Errors present in compilation of '"+input+"': "+tni.errors, tni.errors.isEmpty());
			
			Function f = p.getFunctions().stream().filter(ff->ff.hasAnnot(tni.corePackage.ANNOT_MAIN)).findFirst().get();
			
			TnBoxEnvironment environ = new TnBoxEnvironment();
			environ.out = new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
					outputGot += (char) b;
				}
			});
			environ.err = new PrintStream(new OutputStream() {
				@Override public void write(int b) throws IOException {}
			});
			
			new TnBoxThread(environ, f, new HashMap<>()).run();
			
			Assert.assertTrue("Test '"+input+"' failed: expected '"+outputExpected+"', got '"+outputGot+"'", outputGot.replaceAll("\r\n", "\n").equals(outputExpected));
		}
    }
}
