package info.iconmaster.tnbox.model;

import java.util.Map;
import java.util.Stack;

import info.iconmaster.typhon.compiler.Variable;
import info.iconmaster.typhon.model.Function;

public class TnBoxThread {
	public Stack<TnBoxCall> callStack = new Stack<>();
	public TnBoxEnvironment environ;
	
	public TnBoxThread(TnBoxEnvironment environ) {
		this.environ = environ;
	}
	
	/**
	 * Create a new thread that executes the static function f when run.
	 * @param f
	 * @param args
	 */
	public TnBoxThread(TnBoxEnvironment environ, Function f, Map<Variable, TnBoxObject> args) {
		this.environ = environ;
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
