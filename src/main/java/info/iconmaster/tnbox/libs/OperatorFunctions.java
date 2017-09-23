package info.iconmaster.tnbox.libs;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

import info.iconmaster.tnbox.model.TnBoxObject;
import info.iconmaster.tnbox.model.TnBoxThread;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.AnnotationDefinition;
import info.iconmaster.typhon.model.CorePackage;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.types.Type;

public class OperatorFunctions {
	private OperatorFunctions() {}
	
	public static class BinOpFunc implements TnBoxFunction {
		Function f;
		public java.util.function.BiFunction<BigDecimal, BigDecimal, BigDecimal> op;
		
		public BinOpFunc(Function f, java.util.function.BiFunction<BigDecimal, BigDecimal, BigDecimal> op) {
			this.f = f;
			this.op = op;
		}
		
		@Override
		public List<TnBoxObject> execute(TnBoxThread thread, TyphonInput tni, TnBoxObject thiz, List<TnBoxObject> args) {
			CorePackage core = tni.corePackage;
			
			BigDecimal a = new BigDecimal(thiz.value.toString());
			BigDecimal b = new BigDecimal(args.get(0).value.toString());
			
			TnBoxObject result = new TnBoxObject(f.getRetType().get(0), null);
			
			Type t = f.getRetType().get(0).getType();
			if (t == core.TYPE_BYTE || t == core.TYPE_UBYTE) {
				result.value = op.apply(a, b).byteValue();
			} else if (t == core.TYPE_SHORT || t == core.TYPE_USHORT) {
				result.value = op.apply(a, b).shortValue();
			} else if (t == core.TYPE_INT || t == core.TYPE_UINT || t == core.TYPE_NUMBER) {
				result.value = op.apply(a, b).intValue();
			} else if (t == core.TYPE_LONG || t == core.TYPE_ULONG) {
				result.value = op.apply(a, b).longValue();
			} else if (t == core.TYPE_FLOAT) {
				result.value = op.apply(a, b).floatValue();
			} else if (t == core.TYPE_DOUBLE) {
				result.value = op.apply(a, b).doubleValue();
			}
			
			return Arrays.asList(result);
		}
	}
	
	public static void register(TyphonInput tni) {
		CorePackage core = tni.corePackage;
		
		for (AnnotationDefinition annot : core.LIB_OPS.OP_FUNCS.keySet()) {
			List<Function> fs = core.LIB_OPS.OP_FUNCS.get(annot);
			
			for (Function f : fs) {
				if (annot == core.LIB_OPS.ANNOT_ADD) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->a.add(b, MathContext.DECIMAL128)));
				} else if (annot == core.LIB_OPS.ANNOT_SUB) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->a.subtract(b, MathContext.DECIMAL128)));
				} else if (annot == core.LIB_OPS.ANNOT_MUL) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->a.multiply(b, MathContext.DECIMAL128)));
				} else if (annot == core.LIB_OPS.ANNOT_DIV) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->a.divide(b, MathContext.DECIMAL128)));
				} else if (annot == core.LIB_OPS.ANNOT_MOD) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->a.remainder(b, MathContext.DECIMAL128).abs()));
				} else if (annot == core.LIB_OPS.ANNOT_BAND) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->new BigDecimal(a.toBigInteger().and(b.toBigInteger()))));
				} else if (annot == core.LIB_OPS.ANNOT_BOR) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->new BigDecimal(a.toBigInteger().or(b.toBigInteger()))));
				} else if (annot == core.LIB_OPS.ANNOT_XOR) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->new BigDecimal(a.toBigInteger().xor(b.toBigInteger()))));
				} else if (annot == core.LIB_OPS.ANNOT_SHL) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f, (a,b)->new BigDecimal(a.toBigInteger().shiftLeft(b.intValue()))));
				} else if (annot == core.LIB_OPS.ANNOT_SHR) {
					TnBoxFunction.registry.get(tni).put(f, new BinOpFunc(f,(a,b)->new BigDecimal(a.toBigInteger().shiftRight(b.intValue()))));
				}
			}
		}
	}
}
