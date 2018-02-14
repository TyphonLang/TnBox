package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;

import info.iconmaster.typhon.util.SourceInfo;

public class TnBoxErrorDetails {
	public TnBoxThread thread;
	public List<StackTraceItem> stackTrace = new ArrayList<>();
	public TnBoxObject thrown;
	
	public static class StackTraceItem {
		public TnBoxCall call;
		public SourceInfo source;
		
		public StackTraceItem(TnBoxCall call, SourceInfo source) {
			this.call = call;
			this.source = source;
		}
		
		@Override
		public String toString() {
			return call.source.prettyPrint()+" ["+source+"]";
		}
	}
	
	public TnBoxErrorDetails(TnBoxThread thread, TnBoxObject thrown) {
		this.thread = thread;
		this.thrown = thrown;
		
		for (TnBoxCall call : thread.callStack) {
			stackTrace.add(0, new StackTraceItem(call, call.code.ops.get(call.pc-1).source));
		}
	}
}
