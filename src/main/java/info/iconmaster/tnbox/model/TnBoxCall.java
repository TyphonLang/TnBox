package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import info.iconmaster.typhon.compiler.CodeBlock;
import info.iconmaster.typhon.compiler.Instruction;
import info.iconmaster.typhon.compiler.Variable;

public class TnBoxCall {
	public CodeBlock code;
	public TnBoxScope scope = new TnBoxScope();
	
	public int pc;
	public List<TnBoxObject> retVal = new ArrayList<>();
	public boolean completed;
	
	public TnBoxCall(CodeBlock code) {
		this.code = code;
		
		for (Variable v : code.vars) {
			scope.newVar(v);
		}
	}
	
	public TnBoxCall(CodeBlock code, TnBoxObject thisObject) {
		this(code);
		if (thisObject != null && code.instance != null) scope.setVar(code.instance, thisObject);
	}
	
	public TnBoxCall(CodeBlock code, Map<Variable, TnBoxObject> args) {
		this(code);
		
		for (Entry<Variable, TnBoxObject> entry : args.entrySet()) {
			scope.setVar(entry.getKey(), entry.getValue());
		}
	}
	
	public void step() {
		if (completed || pc >= code.ops.size()) {
			completed = true;
			return;
		}
		
		Instruction inst = code.ops.get(pc);
		
		switch (inst.op) {
			// TODO
		}
		
		pc++;
	}
}
