package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;

import info.iconmaster.typhon.compiler.Variable;

public abstract class TnBoxCall {
	public TnBoxThread thread;
	public boolean completed;
	
	public List<TnBoxObject> retVal = new ArrayList<>();
	
	public TnBoxCall(TnBoxThread thread) {
		this.thread = thread;
	}
	
	public abstract void step();
	
	public TnBoxErrorDetails.StackTraceItem asStackTraceItem() {
		return null;
	}
}
