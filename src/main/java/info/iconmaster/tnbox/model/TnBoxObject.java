package info.iconmaster.tnbox.model;

import info.iconmaster.typhon.types.TypeRef;

public class TnBoxObject {
	public Object value;
	public TypeRef type;
	
	public TnBoxObject(TypeRef type, Object value) {
		this.value = value;
		this.type = type;
	}
}
