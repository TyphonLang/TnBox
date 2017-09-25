package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;

public class TnBoxErrorDetails {
	public TnBoxThread thread;
	public List<TnBoxCall> callStack;
	public TnBoxObject thrown;
	
	public TnBoxErrorDetails(TnBoxThread thread, TnBoxObject thrown) {
		this.thread = thread;
		this.thrown = thrown;
		this.callStack = new ArrayList<>(thread.callStack);
	}
}
