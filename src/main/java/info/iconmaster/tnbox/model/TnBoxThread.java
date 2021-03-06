package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import info.iconmaster.tnbox.model.TnBoxErrorDetails.StackTraceItem;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.compiler.Variable;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.types.Type;

public class TnBoxThread {
	public Stack<TnBoxCall> callStack = new Stack<>();
	public TnBoxEnvironment environ;
	public List<TnBoxObject> retVal = new ArrayList<>();
	public Stack<TnBoxErrorHandler> errorHandlers = new Stack<>();
	public TnBoxErrorDetails error;
	
	public TnBoxThread(TnBoxEnvironment environ) {
		this.environ = environ;
		environ.initialize(this);
	}
	
	/**
	 * Create a new thread that executes the static function f when run.
	 * @param f
	 * @param args
	 */
	public TnBoxThread(TnBoxEnvironment environ, Function f, Map<Variable, TnBoxObject> args) {
		this.environ = environ;
		callStack.push(new TnBoxUserCall(this, f, f.getCode(), args));
		environ.initialize(this);
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
			if (error.thrown.type.canCastTo(potentialHandler.toCatch)) {
				handler = potentialHandler;
				break;
			}
		}
		
		if (handler == null) {
			// no handler found, print exception to stderr and exit
			this.error = error;
			environ.err.print("runtime error: ");
			environ.err.print(error.prettyPrint());
			return;
		}
		
		// unwind call stack
		while (!callStack.isEmpty()) {
			if (callStack.peek() == handler.call) break;
			callStack.pop();
		}
		
		handler.handleError(error);
	}
	
	public void throwError(Type type, String message, TnBoxObject cause) {
		TnBoxInstance instance = new TnBoxInstance();
		instance.fields.put(type.tni.corePackage.TYPE_ERROR.FIELD_MESSAGE, new TnBoxObject(type.tni.corePackage.TYPE_STRING, message));
		instance.fields.put(type.tni.corePackage.TYPE_ERROR.FIELD_CAUSE, cause);
		
		throwError(new TnBoxErrorDetails(this, new TnBoxObject(type, instance)));
	}
}
