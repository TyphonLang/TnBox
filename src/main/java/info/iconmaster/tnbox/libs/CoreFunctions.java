package info.iconmaster.tnbox.libs;

import java.math.BigDecimal;
import java.util.Arrays;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.libs.CorePackage;
import info.iconmaster.typhon.model.libs.CoreTypeNumber;

public class CoreFunctions {
	private CoreFunctions() {}
	
	public static void register(TyphonInput tni1) {
		CorePackage core = tni1.corePackage;
		
		TnBoxFunction.functionHandlers.get(tni1).put(core.FUNC_PRINTLN, (thread, tni, thiz, args)->{
			thread.environ.out.println(args.get(0) == null ? null : args.get(0).value);
			return Arrays.asList();
		});
		
		TnBoxFunction.functionHandlers.get(tni1).put(core.FUNC_PRINT, (thread, tni, thiz, args)->{
			thread.environ.out.print(args.get(0) == null ? null : args.get(0).value);
			return Arrays.asList();
		});
		
		tni1.corePackage.getTypes().stream().filter(t->t instanceof CoreTypeNumber).map(t->(CoreTypeNumber) t).forEach(t->{
			TnBoxFunction.allocHandlers.get(tni1).put(t, (tni)->{
				return null;
			});
			
			TnBoxFunction.functionHandlers.get(tni1).put(t.FUNC_NEW_FROM_NUM, (thread, tni, thiz, args)->{
				BigDecimal arg = new BigDecimal(args.get(0).value.toString());
				
				if (t == core.TYPE_BYTE || t == core.TYPE_UBYTE) {
					thiz.value = arg.byteValue();
				} else if (t == core.TYPE_SHORT || t == core.TYPE_USHORT) {
					thiz.value = arg.shortValue();
				} else if (t == core.TYPE_INT || t == core.TYPE_UINT) {
					thiz.value = arg.intValue();
				} else if (t == core.TYPE_LONG || t == core.TYPE_ULONG) {
					thiz.value = arg.longValue();
				} else if (t == core.TYPE_FLOAT) {
					thiz.value = arg.floatValue();
				} else if (t == core.TYPE_DOUBLE) {
					thiz.value = arg.doubleValue();
				} else {
					throw new IllegalArgumentException("Unknown result type "+t.getName());
				}
				
				return Arrays.asList();
			});
			
			TnBoxFunction.functionHandlers.get(tni1).put(t.FUNC_NEW_FROM_STRING, (thread, tni, thiz, args)->{
				BigDecimal arg = new BigDecimal((String) args.get(0).value);
				
				if (t == core.TYPE_BYTE || t == core.TYPE_UBYTE) {
					thiz.value = arg.byteValue();
				} else if (t == core.TYPE_SHORT || t == core.TYPE_USHORT) {
					thiz.value = arg.shortValue();
				} else if (t == core.TYPE_INT || t == core.TYPE_UINT) {
					thiz.value = arg.intValue();
				} else if (t == core.TYPE_LONG || t == core.TYPE_ULONG) {
					thiz.value = arg.longValue();
				} else if (t == core.TYPE_FLOAT) {
					thiz.value = arg.floatValue();
				} else if (t == core.TYPE_DOUBLE) {
					thiz.value = arg.doubleValue();
				} else {
					throw new IllegalArgumentException("Unknown result type "+t.getName());
				}
				
				return Arrays.asList();
			});
		});
	}
}
