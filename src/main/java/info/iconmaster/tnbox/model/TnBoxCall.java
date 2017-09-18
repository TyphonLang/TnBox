package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import info.iconmaster.typhon.compiler.CodeBlock;
import info.iconmaster.typhon.compiler.Instruction;
import info.iconmaster.typhon.compiler.Variable;
import info.iconmaster.typhon.model.CorePackage;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.types.TypeRef;

public class TnBoxCall {
	public TnBoxThread thread;
	public CodeBlock code;
	public TnBoxScope scope = new TnBoxScope();
	
	public int pc;
	public List<TnBoxObject> retVal = new ArrayList<>();
	public boolean completed;
	
	public TnBoxCall(TnBoxThread thread, CodeBlock code) {
		this.thread = thread;
		this.code = code;
		
		for (Variable v : code.vars) {
			scope.newVar(v);
		}
	}
	
	public TnBoxCall(TnBoxThread thread, CodeBlock code, TnBoxObject thisObject) {
		this(thread, code);
		if (thisObject != null && code.instance != null) scope.setVar(code.instance, thisObject);
	}
	
	public TnBoxCall(TnBoxThread thread, CodeBlock code, Map<Variable, TnBoxObject> args) {
		this(thread, code);
		
		for (Entry<Variable, TnBoxObject> entry : args.entrySet()) {
			scope.setVar(entry.getKey(), entry.getValue());
		}
	}
	
	public void step() {
		if (completed || pc >= code.ops.size()) {
			completed = true;
			return;
		}
		
		CorePackage core = code.tni.corePackage;
		Instruction inst = code.ops.get(pc);
		
		switch (inst.op) {
		case MOV: {
			Variable dest = (Variable) inst.args[0];
			Variable src = (Variable) inst.args[1];
			
			scope.setVar(dest, scope.getVar(src).get());
			break;
		}
		case MOVINT: {
			Variable dest = (Variable) inst.args[0];
			TnBoxObject constant = new TnBoxObject(new TypeRef(core.TYPE_INT), inst.args[1]);
			
			scope.setVar(dest, constant);
			break;
		}
		case CALLSTATIC: {
			List<Variable> dest = (List<Variable>) inst.args[0];
			Function f = (Function) inst.args[1];
			List<Variable> src = (List<Variable>) inst.args[2];
			
			if (f.isLibrary()) {
				// TODO: handle system calls
			} else {
				Map<Variable, TnBoxObject> args = new HashMap<>();
				
				int i = 0;
				for (Variable v : src) {
					args.put(f.getParams().get(i).getVar(), scope.getVar(v).get());
					i++;
				}
				
				thread.callStack.push(new TnBoxCall(thread, f.getCode(), args));
				// TODO: handle ret vals
			}
			break;
		}
		case CALL: {
			List<Variable> dest = (List<Variable>) inst.args[0];
			Variable thisVar = (Variable) inst.args[1];
			List<Variable> src = (List<Variable>) inst.args[3];
			
			// TODO: find overload
			Function f = (Function) inst.args[2];
			
			if (f.isLibrary()) {
				// TODO: handle system calls
			} else {
				Map<Variable, TnBoxObject> args = new HashMap<>();
				
				int i = 0;
				for (Variable v : src) {
					args.put(f.getParams().get(i).getVar(), scope.getVar(v).get());
					i++;
				}
				args.put(thisVar, scope.getVar(thisVar).get());
				
				thread.callStack.push(new TnBoxCall(thread, f.getCode(), args));
				// TODO: handle ret vals
			}
			break;
		}
		}
		
		pc++;
	}
}
