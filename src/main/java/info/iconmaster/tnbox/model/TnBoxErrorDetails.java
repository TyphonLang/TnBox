package info.iconmaster.tnbox.model;

import java.util.ArrayList;
import java.util.List;

import info.iconmaster.tnbox.model.TnBoxErrorDetails.StackTraceItem;
import info.iconmaster.typhon.TyphonInput;
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
		
		public abstract String prettyPrint();
	}
	
	public TnBoxErrorDetails(TnBoxThread thread, TnBoxObject thrown) {
		this.thread = thread;
		this.thrown = thrown;
		
		for (TnBoxCall call : thread.callStack) {
			StackTraceItem sti = call.asStackTraceItem();
			if (sti != null) stackTrace.add(0, sti);
		}
	}
	
	public String prettyPrint() {
		TyphonInput tni = thrown.type.tni;
		TnBoxInstance instance = (TnBoxInstance) thrown.value;
		
		StringBuilder sb = new StringBuilder();
		sb.append(thrown.type.prettyPrint());
		
		if (instance.fields.containsKey(tni.corePackage.TYPE_ERROR.FIELD_MESSAGE)) {
			sb.append(": ");
			sb.append(instance.fields.get(tni.corePackage.TYPE_ERROR.FIELD_MESSAGE));
		}
		
		sb.append('\n');
		
		for (StackTraceItem item : stackTrace) {
			sb.append("\tat ");
			sb.append(item.prettyPrint());
			sb.append('\n');
		}
		
		if (instance.fields.containsKey(tni.corePackage.TYPE_ERROR.FIELD_CAUSE) && instance.fields.get(tni.corePackage.TYPE_ERROR.FIELD_CAUSE) != null) {
			TnBoxObject cause = instance.fields.get(tni.corePackage.TYPE_ERROR.FIELD_CAUSE);
			while (cause != null) {
				TnBoxInstance errorInst = (TnBoxInstance) cause.value;
				TnBoxObject errorOb = errorInst.fields.get(TyphonInputData.registry.get(thread.environ.tni).FIELD_ERROR_DETAILS);
				TnBoxErrorDetails error = errorOb == null ? null : (TnBoxErrorDetails) errorOb.value;
				
				sb.append("caused by ");
				
				if (error == null) {
					sb.append(cause.type.prettyPrint());
					
					if (instance.fields.containsKey(tni.corePackage.TYPE_ERROR.FIELD_MESSAGE)) {
						sb.append(": ");
						sb.append(instance.fields.get(tni.corePackage.TYPE_ERROR.FIELD_MESSAGE));
					}
					
					sb.append('\n');
					
					cause = errorInst.fields.get(TyphonInputData.registry.get(thread.environ.tni).FIELD_ERROR_DETAILS);
				} else {
					sb.append(error.prettyPrint());
					break;
				}
			}
		}
		
		return sb.toString();
	}
}
