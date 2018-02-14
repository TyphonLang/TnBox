package info.iconmaster.tnbox.libs;

import java.util.HashMap;
import java.util.Map;

import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.types.Type;

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
	 * The input this extension is mapped to.
	 */
	public TyphonInput tni;
	
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
		
		TYPE_LIST = new SystemTypeList(this);
		
		CoreFunctions.register(tni);
		OperatorFunctions.register(tni);
	}
}
