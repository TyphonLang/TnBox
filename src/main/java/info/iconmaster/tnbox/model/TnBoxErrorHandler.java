package info.iconmaster.tnbox.model;

import info.iconmaster.typhon.compiler.CatchInfo;
import info.iconmaster.typhon.compiler.Label;

public class TnBoxErrorHandler {
	public Label tryId;
	public TnBoxCall call;
	public CatchInfo info;
	
	public TnBoxErrorHandler(TnBoxCall call, Label tryId, CatchInfo info) {
		super();
		this.tryId = tryId;
		this.call = call;
		this.info = info;
	}
}
