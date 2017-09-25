package info.iconmaster.tnbox.model;

import java.util.HashMap;
import java.util.Map;

import info.iconmaster.typhon.model.Field;

public class TnBoxInstance {
	public Map<Field, TnBoxObject> fields = new HashMap<>();
	
	public TnBoxInstance() {}

	public TnBoxInstance(Map<Field, TnBoxObject> fields) {
		this.fields.putAll(fields);
	}
}
