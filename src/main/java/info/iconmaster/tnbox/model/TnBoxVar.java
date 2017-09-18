package info.iconmaster.tnbox.model;

import info.iconmaster.typhon.compiler.Variable;

public class TnBoxVar {
	private Variable var;
	private TnBoxObject value;
	
	public TnBoxVar(Variable var) {
		this.var = var;
	}
	
	public TnBoxVar(Variable var, TnBoxObject value) {
		this.var = var;
		this.value = value;
	}
	
	public TnBoxObject get() {
		return value;
	}
	
	public void set(TnBoxObject value) {
		this.value = value;
	}
	
	public Variable var() {
		return var;
	}
}
