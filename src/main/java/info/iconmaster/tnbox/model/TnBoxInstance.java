package info.iconmaster.tnbox.model;

import java.util.HashMap;
import java.util.Map;

import info.iconmaster.typhon.model.Field;
import info.iconmaster.typhon.types.TypeRef;

public class TnBoxInstance {
	public TypeRef type;
	public Map<Field, TnBoxObject> fields = new HashMap<>();
	
	public TnBoxInstance(TypeRef type) {
		this.type = type;
	}

	public TnBoxInstance(TypeRef type, Map<Field, TnBoxObject> fields) {
		this.type = type;
		this.fields.putAll(fields);
	}
}
