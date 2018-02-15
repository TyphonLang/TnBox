package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;

import info.iconmaster.typhon.util.SourceInfo;

public class TnBoxErrorDetails {
	public TnBoxThread thread;
	public List<StackTraceItem> stackTrace = new ArrayList<>();
	public TnBoxObject thrown;
	
	public abstract static class StackTraceItem {
		public TnBoxCall call;
		public SourceInfo source;
		
		public StackTraceItem(TnBoxUserCall call, SourceInfo source) {
			this.call = call;
			this.source = source;
		}
		
		@Override
		public abstract String toString();
	}
	
	public TnBoxErrorDetails(TnBoxThread thread, TnBoxObject thrown) {
		this.thread = thread;
		this.thrown = thrown;
		
		for (TnBoxCall call : thread.callStack) {
			StackTraceItem sti = call.asStackTraceItem();
			if (sti != null) stackTrace.add(0, sti);
		}
	}
}
