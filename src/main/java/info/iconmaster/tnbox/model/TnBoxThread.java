package info.iconmaster.tnbox.model;

import java.util.Map;
import java.util.Stack;

import info.iconmaster.typhon.compiler.Variable;
import info.iconmaster.typhon.model.Function;

public class TnBoxThread {
	public Stack<TnBoxCall> callStack = new Stack<>();
	
	public TnBoxThread() {
		
	}
	
	/**
	 * Create a new thread that executes the static function f when run.
	 * @param f
	 * @param args
	 */
	public TnBoxThread(Function f, Map<Variable, TnBoxObject> args) {
		callStack.push(new TnBoxCall(this, f.getCode(), args));
	}
	
	public void step() {
		while (!callStack.isEmpty() && callStack.peek().completed) {
			callStack.pop();
		}
		
		if (callStack.isEmpty()) {
			return;
		}
		
		callStack.peek().step();
	}
	
	public boolean completed() {
		return callStack.isEmpty();
	}
	
	public void run() {
		while (!completed()) {
			step();
		}
	}
}
