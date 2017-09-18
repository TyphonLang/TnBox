package info.iconmaster.tnbox.libs;

import java.util.Arrays;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.CorePackage;

public class CoreFunctions {
	private CoreFunctions() {}
	
	public static void register(TyphonInput tni1) {
		CorePackage core = tni1.corePackage;
		
		TnBoxFunction.registry.get(tni1).put(core.FUNC_PRINTLN, (tni, thiz, args)->{
			System.out.println(args.get(0).value);
			return Arrays.asList();
		});
		
		TnBoxFunction.registry.get(tni1).put(core.FUNC_PRINT, (tni, thiz, args)->{
			System.out.print(args.get(0).value);
			return Arrays.asList();
		});
	}
}
