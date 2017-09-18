package info.iconmaster.tnbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import info.iconmaster.tnbox.model.TnBoxThread;
import info.iconmaster.typhon.Typhon;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.model.Package;
import info.iconmaster.typhon.plugins.TyphonPlugin;
import info.iconmaster.typhon.util.CommandLineHelper;

/**
 * The plugin class for TnBox. Contains event handlers and a proxy main method.
 * 
 * @author iconmaster
 *
 */
@TyphonPlugin(name = "tnbox")
public class TnBox {
	/**
	 * The main function. We just run Typhon's main function, but with the run flag specified.
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> a = new ArrayList<>(Arrays.asList(args));
		a.add(0, "-r");
		Typhon.main(a.toArray(args));
	}
	
	public static final CommandLineHelper.Option OPTION_RUN = new CommandLineHelper.Option(new String[] {"tnbox-run"}, new String[] {"r"}, false, "Runs the generated code in TnBox if specified.");
	public static final CommandLineHelper.Option OPTION_MAIN = new CommandLineHelper.Option(new String[] {"tnbox-main"}, new String[] {}, false, "Specifies the main function.");
	
	@TyphonPlugin.AddCommandLineOptions
	public static Object addOpts() {
		return new CommandLineHelper.Option[] {OPTION_RUN, OPTION_MAIN};
	}
	
	private static List<Function> getMains(Package p) {
		List<Function> mains = new ArrayList<>();
		
		for (Function f : p.getFunctions()) {
			if (f.hasAnnot(p.tni.corePackage.ANNOT_MAIN)) {
				mains.add(f);
			}
		}
		
		for (Package q : p.getSubpackges()) {
			mains.addAll(getMains(q));
		}
		
		return mains;
	}
	
	@TyphonPlugin.OnCompilationComplete
	public static void onDone(CommandLineHelper claHelper, CommandLineHelper.Result result, TyphonInput tni) {
		if (result.optionalArguments.containsKey(OPTION_RUN)) {
			// find the main method
			Function main = null;
			
			if (result.optionalArguments.containsKey(OPTION_MAIN)) {
				// TODO: it was explicitly specified
			} else {
				// find methods annotated with @main
				List<Function> mains = new ArrayList<>();
				
				for (Package p : tni.inputPackages) {
					mains.addAll(getMains(p));
				}
				
				// error if none or more than 1 main
				if (mains.isEmpty()) {
					System.err.println("error: no main function found. Please annotate a function with @main or specify a main function with --tnbox-main.");
					return;
				}
				
				if (mains.size() > 1) {
					System.err.println("error: multiple main functions found. Please specify a main function with --tnbox-main.");
					return;
				}
				
				main = mains.get(0);
			}
			
			// execute main
			new TnBoxThread(main, new HashMap<>()).run();
		}
	}
}
