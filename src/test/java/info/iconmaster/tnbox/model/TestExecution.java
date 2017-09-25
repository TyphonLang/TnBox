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
			new CaseValid("@main void f() {if true {print(1);}}", "1"),
			new CaseValid("@main void f() {if false {print(1);}}", ""),
			new CaseValid("@main void f() {if true {print(1);} else {print(2);}}", "1"),
			new CaseValid("@main void f() {if false {print(1);} else {print(2);}}", "2"),
			new CaseValid("@main void f() {if false {print(1);} elseif false {print(2);} elseif true {print(3);} else {print(4);}}", "3"),
			new CaseValid("@main void f() {print(1 ?? 2);}", "1"),
			new CaseValid("@main void f() {print(null ?? 2);}", "2"),
			new CaseValid("@main void f() {print(true && true);}", "true"),
			new CaseValid("@main void f() {print(true && false);}", "false"),
			new CaseValid("@main void f() {print(false && true);}", "false"),
			new CaseValid("@main void f() {print(false && false);}", "false"),
			new CaseValid("@main void f() {print(true || true);}", "true"),
			new CaseValid("@main void f() {print(true || false);}", "true"),
			new CaseValid("@main void f() {print(false || true);}", "true"),
			new CaseValid("@main void f() {print(false || false);}", "false"),
			new CaseValid("@main void f() {print([]);}", "[]"),
			new CaseValid("@main void f() {print([1]);}", "[1]"),
			new CaseValid("@main void f() {print([1,2]);}", "[1, 2]"),
			new CaseValid("@main void f() {print({});}", "{}"),
			new CaseValid("@main void f() {print({1:2});}", "{1=2}"),
			new CaseValid("@main void f() {print(if true: 1 else: 0);}", "1"),
			new CaseValid("@main void f() {print(if false: 1 else: 0);}", "0"),
			new CaseValid("@main void f() {print(if false: 1 elseif true: 2 else: 3);}", "2"),
			new CaseValid("@main void f() {print(if false: 1 elseif false: 2 else: 3);}", "3"),
			new CaseValid("@main void f() {print(if false: 1 elseif false: 2 elseif false: 3 else: 4);}", "4"),
			new CaseValid("@main void f() {print(if false: 1 elseif true: 2 elseif false: 3 else: 4);}", "2"),
			new CaseValid("@main void f() {print(if false: 1 elseif false: 2 elseif true: 3 else: 4);}", "3"),
			new CaseValid("@main void f() {print(1+1);}", "2"),
			new CaseValid("@main void f() {print((1 as byte)+(1 as double));}", "2.0"),
			new CaseValid("@main void f() {print(4-5*6/4+2*(5/7)-1);}", "-4"),
			new CaseValid("@main void f() {print(4.0-5*6/4+2*(5/7)-1);}", "-4.0"),
			new CaseValid("@main void f() {print(4.0-5.0*6/4+2*(5/7)-1);}", "-4.5"),
			new CaseValid("@main void f() {print(1<2);}", "true"),
			new CaseValid("@main void f() {print(1.0<2);}", "true"),
			new CaseValid("@main void f() {print(1<1);}", "false"),
			new CaseValid("@main void f() {print(1<=1);}", "true"),
			new CaseValid("@main void f() {print(2>1);}", "true"),
			new CaseValid("@main void f() {print(2.0>=1);}", "true"),
			new CaseValid("@main void f() {print(1.0>2 && 1>2.0);}", "false"),
			new CaseValid("@main void f() {print(-1);}", "-1"),
			new CaseValid("@main void f() {print(-2.6);}", "-2.6"),
			new CaseValid("@main void f() {print(+1);}", "1"),
			new CaseValid("@main void f() {print(+1.0);}", "1.0"),
			new CaseValid("@main void f() {print(1 == 1);}", "true"),
			new CaseValid("@main void f() {print(1 == 2);}", "false"),
			new CaseValid("@main void f() {print(1 != 1);}", "false"),
			new CaseValid("@main void f() {print(1 != 2);}", "true"),
			new CaseValid("@main void f() {print(1.0 == 1.0 && 1.0 != 2.0);}", "true"),
			new CaseValid("@main void f() {switch 0 {case 0 {print('z');} case 1 {print('o');} case 2 {print('t');}}}", "z"),
			new CaseValid("@main void f() {switch 1 {case 0 {print('z');} case 1 {print('o');} case 2 {print('t');}}}", "o"),
			new CaseValid("@main void f() {switch 2 {case 0 {print('z');} case 1 {print('o');} case 2 {print('t');}}}", "t"),
			new CaseValid("@main void f() {switch 3 {case 0 {print('z');} case 1 {print('o');} case 2 {print('t');}}}", ""),
			new CaseValid("@main void f() {switch 3 {case 0 {print('z');} case 1,2 {print('n');} default {print('d');}}}", "d"),
			new CaseValid("class a {} @main void f() {a a = new a();}", ""),
			new CaseValid("class a {new() {print('A');}} @main void f() {a a = new a();}", "A"),
			new CaseValid("class a {int x;} @main void f() {a a = new a(); a.x = 5; print(a.x);}", "5"),
			new CaseValid("@main void f() {print(new int(2.0));}", "2"),
			new CaseValid("class a {void g() {print('a');}} class b : a {@override void g() {print('b');}} @main void f() {b b = new b(); b.g();}", "b"),
			new CaseValid("int g() {return 6;} @main void f() {print(g());}", "6"),
			new CaseValid("@main void f() {try {} catch error e {}}", ""),
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
