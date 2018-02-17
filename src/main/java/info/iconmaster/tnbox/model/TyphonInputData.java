package info.iconmaster.tnbox.model;

import java.util.HashMap;
import java.util.Map;

import info.iconmaster.tnbox.libs.CoreFunctions;
import info.iconmaster.tnbox.libs.OperatorFunctions;
import info.iconmaster.tnbox.libs.SystemTypeList;
import info.iconmaster.tnbox.libs.SystemTypeMap;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Field;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.types.Type;
import info.iconmaster.typhon.types.TypeRef;

/**
 * This class represents extension data to a given TyphonInput- Information TnBox needs on a per-TyphonInput basis.
 * @author iconmaster
 *
 */
public class TyphonInputData {
	/**
	 * A map of TyphonInputs to thier TnBox extensions.
	 */
	public static final Map<TyphonInput, TyphonInputData> registry = new HashMap<>();
	
	/**
	 * Internal type definitions.
	 */
	public SystemTypeList TYPE_LIST;
	
	/**
	 * Internal type definitions.
	 */
	public SystemTypeMap TYPE_MAP;
	
	/**
	 * The input this extension is mapped to.
	 */
	public TyphonInput tni;
	
	/**
	 * This field is a dummy field used to store TnBoxErrorDetails for Error objects.
	 */
	public Field FIELD_ERROR_DETAILS;
	
	/**
	 * Handlers for executing system functions.
	 */
	public Map<Function, TnBoxFunction> functionHandlers = new HashMap<>();
	
	/**
	 * Handlers for allocating system objects.
	 */
	public Map<Type, java.util.function.Function<TyphonInput,Object>> allocHandlers = new HashMap<>();

	public TyphonInputData(TyphonInput tni) {
		this.tni = tni;
		TyphonInputData.registry.put(tni, this);
		
		FIELD_ERROR_DETAILS = new Field("%errorDetails", TypeRef.var(tni));
		
		TYPE_LIST = new SystemTypeList(this);
		TYPE_MAP = new SystemTypeMap(this);
		
		CoreFunctions.register(tni);
		OperatorFunctions.register(tni);
	}
}
