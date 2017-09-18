package info.iconmaster.tnbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.iconmaster.typhon.Typhon;
import info.iconmaster.typhon.TyphonInput;
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
	
	@TyphonPlugin.AddCommandLineOptions
	public static Object addOpts() {
		return OPTION_RUN;
	}
	
	@TyphonPlugin.OnCompilationComplete
	public static void onDone(CommandLineHelper claHelper, CommandLineHelper.Result result, TyphonInput tni) {
		if (result.optionalArguments.containsKey(OPTION_RUN)) {
			// TODO: run the code!
		}
	}
}
