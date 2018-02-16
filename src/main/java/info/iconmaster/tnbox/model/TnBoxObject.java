package info.iconmaster.tnbox.model;

import java.util.Iterator;

import info.iconmaster.typhon.model.Field;
import info.iconmaster.typhon.types.Type;
import info.iconmaster.typhon.types.TypeRef;

public class TnBoxObject {
	public static class FieldSetterCall extends TnBoxCall {
		TnBoxInstance inst;
		TnBoxObject ob;
		Iterator<Field> iter;
		Field field;
		
		public FieldSetterCall(TnBoxThread thread, TnBoxInstance inst, TnBoxObject ob, Iterator<Field> iter, Field field) {
			super(thread);
			this.inst = inst;
			this.ob = ob;
			this.iter = iter;
			this.field = field;
		}
		
		@Override
		public void step() {
			if (thread.retVal.size() != 1) {
				thread.throwError(field.tni.corePackage.TYPE_ERROR_INTERNAL, "Returns for field initilization was not a single value", null);
				return;
			}
			
			TnBoxObject value = thread.retVal.get(0);
			inst.fields.put(field, value);
			
			thread.callStack.pop();
			
			while (iter.hasNext()) {
				Field f = iter.next();
				
				if (f.getValue() != null) {
					thread.callStack.push(new FieldSetterCall(thread, inst, ob, iter, f));
					thread.callStack.push(new TnBoxUserCall(thread, f, f.getValue(), ob));
					return;
				}
			}
		}
	}
	
	public static TnBoxObject alloc(TnBoxThread thread, TypeRef type) {
		TnBoxInstance inst = new TnBoxInstance();
		TnBoxObject ob = new TnBoxObject(type, inst);
		
		// for each field that can be initalized, create a new thread to run the init code for that field
		// this is done via a chain of FieldSetterCalls interlaced with TnBoxUserCalls
		Iterator<Field> iter = type.getType().getAllFields().iterator();
		while (iter.hasNext()) {
			Field f = iter.next();
			
			if (f.getValue() != null) {
				thread.callStack.push(new FieldSetterCall(thread, inst, ob, iter, f));
				thread.callStack.push(new TnBoxUserCall(thread, f, f.getValue(), ob));
				break;
			}
		}
		
		return ob;
	}
	
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
