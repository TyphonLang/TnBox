package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import info.iconmaster.tnbox.model.TnBoxErrorDetails.StackTraceItem;
import info.iconmaster.typhon.compiler.CatchInfo;
import info.iconmaster.typhon.compiler.CodeBlock;
import info.iconmaster.typhon.compiler.Instruction;
import info.iconmaster.typhon.compiler.Instruction.OpCode;
import info.iconmaster.typhon.compiler.Label;
import info.iconmaster.typhon.compiler.Variable;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.model.TemplateArgument;
import info.iconmaster.typhon.model.TyphonModelEntity;
import info.iconmaster.typhon.model.libs.CorePackage;
import info.iconmaster.typhon.types.TypeRef;

public class TnBoxUserCall extends TnBoxCall {
	public class UserStackTraceItem extends TnBoxErrorDetails.StackTraceItem {
		public UserStackTraceItem() {
			super(TnBoxUserCall.this, code.ops.get(pc).source);
		}
		
		@Override
		public String prettyPrint() {
			return TnBoxUserCall.this.source.prettyPrint()+" ["+source+"]";
		}
	}
	
	public class UserErrorHandler extends TnBoxErrorHandler {
		public Label tryId;
		public CatchInfo info;
		
		public UserErrorHandler(Label tryId, CatchInfo info) {
			super(TnBoxUserCall.this, info.toCatch);
			this.tryId = tryId;
			this.info = info;
		}
		
		@Override
		public void handleError(TnBoxErrorDetails error) {
			// move execution to catch label
			int i = 0;
			for (Instruction inst : code.ops) {
				if (inst.op == OpCode.LABEL && inst.args[0] == info.label) {
					pc = i;
					break;
				}
				i++;
			}
			
			// supply error as variable
			scope.getVar(info.var).set(error.thrown);
			
			// remove all other handlers of this tryId
			while (!thread.errorHandlers.isEmpty()) {
				if (thread.errorHandlers.peek().call != call || ((UserErrorHandler)thread.errorHandlers.peek()).tryId != tryId) break;
				thread.errorHandlers.pop();
			}
		}
	}
	
	public CodeBlock code;
	public TnBoxScope scope = new TnBoxScope();
	public TyphonModelEntity source;
	
	public int pc;
	public List<Variable> waitingforRet;
	
	public TnBoxUserCall(TnBoxThread thread, TyphonModelEntity source, CodeBlock code) {
		super(thread);
		this.code = code;
		this.source = source;
		
		for (Variable v : code.vars) {
			scope.newVar(v);
		}
	}
	
	public TnBoxUserCall(TnBoxThread thread, TyphonModelEntity source, CodeBlock code, TnBoxObject thisObject) {
		this(thread, source, code);
		if (thisObject != null && code.instance != null) scope.setVar(code.instance, thisObject);
	}
	
	public TnBoxUserCall(TnBoxThread thread, TyphonModelEntity source, CodeBlock code, Map<Variable, TnBoxObject> args) {
		this(thread, source, code);
		
		for (Entry<Variable, TnBoxObject> entry : args.entrySet()) {
			scope.setVar(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public void step() {
		if (completed || pc >= code.ops.size()) {
			completed = true;
			return;
		}
		
		if (waitingforRet != null) {
			int i = 0;
			for (Variable v : waitingforRet) {
				if (i >= thread.retVal.size()) break;
				TnBoxObject ob = thread.retVal.get(i);
				scope.getVar(v).set(ob);
				
				i++;
			}
			
			thread.retVal.clear();
			waitingforRet = null;
		}
		
		CorePackage core = code.tni.corePackage;
		Instruction inst = code.ops.get(pc);
		
		opSwitch:
		switch (inst.op) {
		case MOV: {
			Variable dest = (Variable) inst.args[0];
			Variable src = (Variable) inst.args[1];
			
			scope.setVar(dest, scope.getVar(src).get());
			break;
		}
		
		case MOVBYTE: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_BYTE), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVSHORT: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_SHORT), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVINT: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_INT), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVLONG: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_LONG), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVFLOAT: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_FLOAT), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVDOUBLE: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_DOUBLE), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVSTR: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_STRING), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVCHAR: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_CHAR), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVTRUE: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_BOOL), true);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVFALSE: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_BOOL), false);
			
			scope.setVar(dest, constant);
			break;
		}
		
		case MOVNULL: {
			Variable dest = (Variable) inst.args[0];
			
			scope.setVar(dest, null);
			break;
		}
		
		case CALLSTATIC: {
			List<Variable> dest = (List<Variable>) inst.args[0];
			Function f = (Function) inst.args[1];
			List<Variable> src = (List<Variable>) inst.args[2];
			
			if (f.isLibrary()) {
				TnBoxFunction handler = TyphonInputData.registry.get(code.tni).functionHandlers.get(f);
				if (handler == null) {
					thread.throwError(f.tni.corePackage.TYPE_ERROR_INTERNAL, "no handler for function "+f.prettyPrint(), null);
					break opSwitch;
				}
				
				List<TnBoxObject> argValues = src.stream().map(v->scope.getVar(v).get()).collect(Collectors.toList());
				
				int i = 0;
				for (TnBoxObject arg : argValues) {
					Variable v = src.get(i);
					if (arg != null && arg.value != null && !arg.type.canCastTo(v.type)) {
						thread.throwError(f.tni.corePackage.TYPE_ERROR_CAST, "cannot cast "+arg.type.prettyPrint()+" to "+v.type.prettyPrint(), null);
						break opSwitch;
					}
					i++;
				}
				
				List<TnBoxObject> retVals = handler.execute(thread, code.tni, null, argValues);
				
				i = 0;
				for (Variable v : dest) {
					if (i < retVals.size()) {
						scope.setVar(v, retVals.get(i));
					} else {
						break;
					}
					
					i++;
				}
			} else {
				waitingforRet = dest;
				Map<Variable, TnBoxObject> args = new HashMap<>();
				
				int i = 0;
				for (Variable v : src) {
					TnBoxObject arg = scope.getVar(v).get();
					
					if (arg != null && arg.value != null && !arg.type.canCastTo(v.type)) {
						thread.throwError(f.tni.corePackage.TYPE_ERROR_CAST, "cannot cast "+arg.type.prettyPrint()+" to "+v.type.prettyPrint(), null);
						break opSwitch;
					}
					
					args.put(f.getParams().get(i).getVar(), arg);
					i++;
				}
				
				thread.callStack.push(new TnBoxUserCall(thread, f, f.getCode(), args));
			}
			break;
		}
		
		case CALL: {
			List<Variable> dest = (List<Variable>) inst.args[0];
			List<Variable> src = (List<Variable>) inst.args[3];
			
			Variable thisVar = (Variable) inst.args[1];
			TnBoxObject thiz = scope.getVar(thisVar).get();
			
			if (thiz == null || thiz.value == null) {
				thread.throwError(inst.tni.corePackage.TYPE_ERROR_NULL, "callee of method "+((Function) inst.args[2]).prettyPrint()+" was null", null);
				break opSwitch;
			}
			
			// find the correct override of f
			Function f = ((Function) inst.args[2]).getVirtualOverride(thiz.type.getType());
			
			// call it
			if (f.isLibrary()) {
				TnBoxFunction handler = TyphonInputData.registry.get(code.tni).functionHandlers.get(f);
				if (handler == null) {
					thread.throwError(f.tni.corePackage.TYPE_ERROR_INTERNAL, "no handler for function "+f.prettyPrint(), null);
					break opSwitch;
				}
				
				List<TnBoxObject> argValues = src.stream().map(v->scope.getVar(v).get()).collect(Collectors.toList());
				
				int i = 0;
				for (TnBoxObject arg : argValues) {
					Variable v = src.get(i);
					if (arg != null && arg.value != null && !arg.type.canCastTo(v.type)) {
						thread.throwError(f.tni.corePackage.TYPE_ERROR_CAST, "cannot cast "+arg.type.prettyPrint()+" to "+v.type.prettyPrint(), null);
						break opSwitch;
					}
					i++;
				}
				
				List<TnBoxObject> retVals = handler.execute(thread, code.tni, thiz, argValues);
				
				i = 0;
				for (Variable v : dest) {
					if (i < retVals.size()) {
						scope.setVar(v, retVals.get(i));
					} else {
						break;
					}
					
					i++;
				}
			} else {
				waitingforRet = dest;
				Map<Variable, TnBoxObject> args = new HashMap<>();
				
				int i = 0;
				for (Variable v : src) {
					TnBoxObject arg = scope.getVar(v).get();
					
					if (arg != null && arg.value != null && !arg.type.canCastTo(v.type)) {
						thread.throwError(f.tni.corePackage.TYPE_ERROR_CAST, "cannot cast "+arg.type.prettyPrint()+" to "+v.type.prettyPrint(), null);
						break opSwitch;
					}
					
					args.put(f.getParams().get(i).getVar(), arg);
					i++;
				}
				args.put(f.getCode().instance, thiz);
				
				thread.callStack.push(new TnBoxUserCall(thread, f, f.getCode(), args));
			}
			break;
		}
		
		case INSTANCEOF: {
			Variable dest = (Variable) inst.args[0];
			Variable src = (Variable) inst.args[1];
			TypeRef expectedType = (TypeRef) inst.args[2];
			
			TnBoxObject ob = scope.getVar(src).get();
			
			if (ob == null || ob.value == null) {
				scope.setVar(dest, new TnBoxObject(core.TYPE_BOOL, false));
			} else {
				TnBoxObject result = new TnBoxObject(core.TYPE_BOOL, ob.type.canCastTo(expectedType));
				scope.setVar(dest, result);
			}
			break;
		}
		
		case ISNULL: {
			Variable dest = (Variable) inst.args[0];
			Variable src = (Variable) inst.args[1];
			
			TnBoxVar var = scope.getVar(src);
			
			TnBoxObject result = new TnBoxObject(new TypeRef(core.TYPE_BOOL), var == null || var.get() == null);
			scope.setVar(dest, result);
			break;
		}
		
		case JUMP: {
			boolean found = false;
			int i = 0;
			for (Instruction inst2 : code.ops) {
				if (inst2.op == OpCode.LABEL && inst.args[0] == inst2.args[0]) {
					found = true;
					pc = i-1;
					break;
				}
				i++;
			}
			
			if (!found) {
				thread.throwError(inst.tni.corePackage.TYPE_ERROR_INTERNAL, "label not found", null);
				break opSwitch;
			}
			
			break;
		}
		
		case JUMPTRUE: {
			Variable src = (Variable) inst.args[0];
			
			if ((Boolean) scope.getVar(src).get().value) {
				boolean found = false;
				int i = 0;
				for (Instruction inst2 : code.ops) {
					if (inst2.op == OpCode.LABEL && inst.args[1] == inst2.args[0]) {
						found = true;
						pc = i-1;
						break;
					}
					i++;
				}
				
				if (!found) {
					thread.throwError(inst.tni.corePackage.TYPE_ERROR_INTERNAL, "label not found", null);
					break opSwitch;
				}
			}
			
			break;
		}
		
		case JUMPFALSE: {
			Variable src = (Variable) inst.args[0];
			
			if (!(Boolean) scope.getVar(src).get().value) {
				boolean found = false;
				int i = 0;
				for (Instruction inst2 : code.ops) {
					if (inst2.op == OpCode.LABEL && inst.args[1] == inst2.args[0]) {
						found = true;
						pc = i-1;
						break;
					}
					i++;
				}
				
				if (!found) {
					thread.throwError(inst.tni.corePackage.TYPE_ERROR_INTERNAL, "label not found", null);
					break opSwitch;
				}
			}
			
			break;
		}
		
		case LABEL: {
			// do nothing
			break;
		}
		
		case NOT: {
			Variable dest = (Variable) inst.args[0];
			Variable src = (Variable) inst.args[1];
			
			TnBoxObject ob = scope.getVar(src).get();
			
			if (ob == null || ob.value == null) {
				thread.throwError(inst.tni.corePackage.TYPE_ERROR_NULL, "cannot find boolean NOT of null", null);
				break opSwitch;
			} else {
				scope.setVar(dest, new TnBoxObject(new TypeRef(core.TYPE_BOOL), !((Boolean) (ob.value))));
			}
			break;
		}
		
		case RAWEQ: {
			// TODO: better == behavior
			
			Variable dest = (Variable) inst.args[0];
			Variable a = (Variable) inst.args[1];
			Variable b = (Variable) inst.args[2];
			
			TnBoxObject oa = scope.getVar(a).get();
			TnBoxObject ob = scope.getVar(b).get();
			
			scope.setVar(dest, new TnBoxObject(new TypeRef(core.TYPE_BOOL), oa == null ? ob == null : oa.equals(ob)));
			break;
		}
		
		case RET: {
			for (Variable v : (List<Variable>) inst.args[0]) {
				retVal.add(scope.getVar(v).get());
			}
			completed = true;
			break;
		}
		
		case MOVLIST: {
			List<TnBoxObject> list = new ArrayList<>();
			
			Variable dest = (Variable) inst.args[0];
			List<Variable> src = (List<Variable>) inst.args[1];
			
			for (Variable v : src) {
				list.add(scope.getVar(v).get());
			}
			
			TypeRef elemType = new TypeRef(core.TYPE_ANY);
			if (dest.type.getType() == core.TYPE_LIST && dest.type.getTemplateArgs().size() == 1) {
				elemType = dest.type.getTemplateArgs().get(0).getValue();
			}
			
			scope.setVar(dest, new TnBoxObject(new TypeRef(TyphonInputData.registry.get(core.tni).TYPE_LIST, new TemplateArgument(elemType)), list));
			break;
		}
		
		case MOVMAP: {
			Map<TnBoxObject, TnBoxObject> list = new HashMap<>();
			
			Variable dest = (Variable) inst.args[0];
			Map<Variable,Variable> src = (Map<Variable,Variable>) inst.args[1];
			
			for (Entry<Variable,Variable> entry : src.entrySet()) {
				list.put(scope.getVar(entry.getKey()).get(), scope.getVar(entry.getValue()).get());
			}
			
			TypeRef keyType = new TypeRef(core.TYPE_ANY);
			TypeRef valueType = new TypeRef(core.TYPE_ANY);
			if (dest.type.getType() == core.TYPE_MAP && dest.type.getTemplateArgs().size() == 2) {
				keyType = dest.type.getTemplateArgs().get(0).getValue();
				valueType = dest.type.getTemplateArgs().get(1).getValue();
			}
			
			scope.setVar(dest, new TnBoxObject(new TypeRef(TyphonInputData.registry.get(core.tni).TYPE_MAP, new TemplateArgument(keyType), new TemplateArgument(valueType)), list));
			break;
		}
		
		case MOVTYPE: {
			Variable dest = (Variable) inst.args[0];
			TypeRef src = (TypeRef) inst.args[1];
			
			scope.setVar(dest, new TnBoxObject(new TypeRef(core.LIB_REFLECT.TYPE_TYPE), src));
			break;
		}
		
		case ALLOC: {
			Variable dest = (Variable) inst.args[0];
			TypeRef src = (TypeRef) inst.args[1];
			
			TnBoxObject ob;
			if (TyphonInputData.registry.get(src.tni).allocHandlers.containsKey(src.getType())) {
				ob = new TnBoxObject(src, TyphonInputData.registry.get(src.tni).allocHandlers.get(src.getType()).apply(src.tni));
			} else {
				ob = TnBoxObject.alloc(thread, src);
			}
			
			scope.setVar(dest, ob);
			break;
		}
		
		case THROW: {
			Variable errorVar = (Variable) inst.args[0];
			
			TnBoxObject ob = scope.getVar(errorVar).get();
			
			if (ob == null || ob.value == null) {
				thread.throwError(inst.tni.corePackage.TYPE_ERROR_NULL, "attempt to throw null", null);
				break opSwitch;
			}
			
			TnBoxErrorDetails error = new TnBoxErrorDetails(thread, ob);
			
			thread.throwError(error);
			break;
		}
		
		case TRY: {
			Label tryId = (Label) inst.args[0];
			List<CatchInfo> catches = (List<CatchInfo>) inst.args[1];
			
			for (CatchInfo info : catches) {
				thread.errorHandlers.push(new UserErrorHandler(tryId, info));
			}
			
			break;
		}
		
		case ENDTRY: {
			Label tryId = (Label) inst.args[0];
			
			while (!thread.errorHandlers.isEmpty()) {
				if (thread.errorHandlers.peek().call != this || ((UserErrorHandler)thread.errorHandlers.peek()).tryId != tryId) break;
				thread.errorHandlers.pop();
			}
			break;
		}
		
		default: {
			throw new IllegalArgumentException("Unknown opcode "+inst.op);
		}
		}
		
		pc++;
	}
	
	@Override
	public StackTraceItem asStackTraceItem() {
		return new UserStackTraceItem();
	}
}
