package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import info.iconmaster.typhon.compiler.Instruction;
import info.iconmaster.typhon.compiler.Variable;
import info.iconmaster.typhon.compiler.Instruction.OpCode;
import info.iconmaster.typhon.model.Function;

public class TnBoxThread {
	public Stack<TnBoxCall> callStack = new Stack<>();
	public TnBoxEnvironment environ;
	public List<TnBoxObject> retVal = new ArrayList<>();
	public Stack<TnBoxErrorHandler> errorHandlers = new Stack<>();
	public TnBoxErrorDetails error;
	
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
		if (completed()) {
			return;
		}
		
		while (!callStack.isEmpty() && callStack.peek().completed) {
			TnBoxCall call = callStack.pop();
			retVal.clear();
			retVal.addAll(call.retVal);
		}
		
		if (callStack.isEmpty()) {
			return;
		}
		
		callStack.peek().step();
	}
	
	public boolean completed() {
		return error != null || callStack.isEmpty();
	}
	
	public void run() {
		while (!completed()) {
			step();
		}
	}
	
	public void throwError(TnBoxErrorDetails error) {
		// find the handler to unwind to
		TnBoxErrorHandler handler = null;
		while (!errorHandlers.isEmpty()) {
			TnBoxErrorHandler potentialHandler = errorHandlers.pop();
			if (error.thrown.type.canCastTo(potentialHandler.info.toCatch)) {
				handler = potentialHandler;
				break;
			}
		}
		
		if (handler == null) {
			// no handler found, print exception to stderr and exit
			this.error = error;
			environ.err.println("runtime error: "+error.thrown.type.getName());
			// TODO: provide stack trace
			return;
		}
		
		// unwind call stack
		while (!callStack.isEmpty()) {
			if (callStack.peek() == handler.call) break;
			callStack.pop();
		}
		
		// move execution to catch label
		int i = 0;
		for (Instruction inst : handler.call.code.ops) {
			if (inst.op == OpCode.LABEL && inst.args[0] == handler.info.label) {
				handler.call.pc = i;
				break;
			}
			i++;
		}
		
		// supply error as variable
		handler.call.scope.getVar(handler.info.var).set(error.thrown);
		
		// remove all other handlers of this tryId
		while (!errorHandlers.isEmpty()) {
			if (errorHandlers.peek().call == handler.call && errorHandlers.peek().tryId != handler.tryId) break;
			errorHandlers.pop();
		}
	}
}
