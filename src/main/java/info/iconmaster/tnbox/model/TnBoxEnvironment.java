package info.iconmaster.tnbox.model;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Field;
import info.iconmaster.typhon.model.StaticInitBlock;

public class TnBoxEnvironment {
	public TyphonInput tni;
	
	public PrintStream out = System.out;
	public PrintStream err = System.err;
	public InputStream in = System.in;
	
	public Map<Field, TnBoxObject> globals = new HashMap<>();
	boolean initialized;
	
	public TnBoxEnvironment(TyphonInput tni) {
		this.tni = tni;
	}
	
	public static class GlobalSetterCall extends TnBoxCall {
		Iterator<Field> iter;
		Field field;
		
		public GlobalSetterCall(TnBoxThread thread, Iterator<Field> iter, Field field) {
			super(thread);
			this.iter = iter;
			this.field = field;
		}
		
		@Override
		public void step() {
			if (thread.retVal.size() != 1) {
				thread.throwError(field.tni.corePackage.TYPE_ERROR_INTERNAL, "Returns for field initilization was not a single value", null);
				return;
			}
			
			TnBoxObject value = thread.retVal.get(0);
			thread.environ.globals.put(field, value);
			
			thread.callStack.pop();
			
			while (iter.hasNext()) {
				Field f = iter.next();
				
				if (f.getValue() != null) {
					thread.callStack.push(new GlobalSetterCall(thread, iter, f));
					thread.callStack.push(new TnBoxUserCall(thread, f, f.getValue()));
					return;
				}
			}
		}
	}
	
	public static class StaticInitBlockCall extends TnBoxCall {
		Iterator<StaticInitBlock> iter;
		StaticInitBlock block;
		
		public StaticInitBlockCall(TnBoxThread thread, Iterator<StaticInitBlock> iter, StaticInitBlock block) {
			super(thread);
			this.iter = iter;
			this.block = block;
		}
		
		@Override
		public void step() {
			thread.callStack.pop();
			
			if (iter.hasNext()) {
				StaticInitBlock b = iter.next();
				
				thread.callStack.push(new StaticInitBlockCall(thread, iter, b));
				thread.callStack.push(new TnBoxUserCall(thread, b, b.getCode()));
			}
		}
	}
	
	public void initialize(TnBoxThread thread) {
		if (initialized) return;
		initialized = true;
		
		// set the initial value of all globals; see TnBoxObject.alloc for details
		Iterator<Field> iter = tni.corePackage.getAllGlobals().iterator();
		while (iter.hasNext()) {
			Field f = iter.next();
			
			if (f.getValue() != null) {
				thread.callStack.push(new GlobalSetterCall(thread, iter, f));
				thread.callStack.push(new TnBoxUserCall(thread, f, f.getValue()));
				return;
			}
		}
		
		// run all static init blocks; see TnBoxObject.alloc for details
		Iterator<StaticInitBlock> iter2 = tni.corePackage.getAllStaticInitBlocks().iterator();
		if (iter2.hasNext()) {
			StaticInitBlock b = iter2.next();
			
			thread.callStack.push(new StaticInitBlockCall(thread, iter2, b));
			thread.callStack.push(new TnBoxUserCall(thread, b, b.getCode()));
		}
	}
}
