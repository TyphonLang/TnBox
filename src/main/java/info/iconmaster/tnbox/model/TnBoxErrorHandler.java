package info.iconmaster.tnbox.model;

import info.iconmaster.typhon.types.TypeRef;

public abstract class TnBoxErrorHandler {
	public TnBoxCall call;
	public TypeRef toCatch;
	
	public TnBoxErrorHandler(TnBoxCall call, TypeRef toCatch) {
		this.call = call;
		this.toCatch = toCatch;
	}
	
	public abstract void handleError(TnBoxErrorDetails error);
}
