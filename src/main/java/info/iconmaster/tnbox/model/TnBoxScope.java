package info.iconmaster.tnbox.model;

import java.util.HashMap;
import java.util.Map;

import info.iconmaster.typhon.compiler.Variable;

public class TnBoxScope {
	public Map<Variable, TnBoxVar> vars = new HashMap<>();
	TnBoxScope parent;
	
	public TnBoxScope() {
		
	}
	
	public TnBoxScope(TnBoxScope parent) {
		this.parent = parent;
	}
	
	public TnBoxVar newVar(Variable v) {
		TnBoxVar var = new TnBoxVar(v);
		vars.put(v, var);
		return var;
	}
	
	public TnBoxVar getVar(Variable v) {
		if (vars.containsKey(v)) {
			return vars.get(v);
		} else if (parent != null) {
			return parent.getVar(v);
		} else {
			return null;
		}
	}
	
	public void setVar(Variable v, TnBoxObject o) {
		TnBoxVar var = getVar(v);
		if (var != null) {
			var.set(o);
		} else {
			newVar(v).set(o);
		}
	}
}
