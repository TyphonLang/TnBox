package info.iconmaster.tnbox.model;

import info.iconmaster.typhon.types.Type;
import info.iconmaster.typhon.types.TypeRef;

public class TnBoxObject {
	public Object value;
	public TypeRef type;
	
	public TnBoxObject(TypeRef type, Object value) {
		this.value = value;
		this.type = type;
	}
	
	public TnBoxObject(Type type, Object value) {
		this.value = value;
		this.type = new TypeRef(type);
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TnBoxObject other = (TnBoxObject) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
