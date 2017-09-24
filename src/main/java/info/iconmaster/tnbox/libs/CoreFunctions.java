package info.iconmaster.tnbox.libs;

import java.util.Arrays;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.libs.CorePackage;

public class CoreFunctions {
	private CoreFunctions() {}
	
	public static void register(TyphonInput tni1) {
		CorePackage core = tni1.corePackage;
		
		TnBoxFunction.functionHandlers.get(tni1).put(core.FUNC_PRINTLN, (thread, tni, thiz, args)->{
			thread.environ.out.println(args.get(0).value);
			return Arrays.asList();
		});
		
		TnBoxFunction.functionHandlers.get(tni1).put(core.FUNC_PRINT, (thread, tni, thiz, args)->{
			thread.environ.out.print(args.get(0).value);
			return Arrays.asList();
		});
	}
}
