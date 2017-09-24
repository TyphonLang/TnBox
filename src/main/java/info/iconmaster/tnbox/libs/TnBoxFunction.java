package info.iconmaster.tnbox.libs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.iconmaster.tnbox.model.TnBoxObject;
import info.iconmaster.tnbox.model.TnBoxThread;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.types.Type;

public interface TnBoxFunction {
	public static final Map<TyphonInput, Map<Function, TnBoxFunction>> functionHandlers = new HashMap<>();
	public static final Map<TyphonInput, Map<Type, java.util.function.Function<TyphonInput,Object>>> allocHandlers = new HashMap<>();
	
	public List<TnBoxObject> execute(TnBoxThread thread, TyphonInput tni, TnBoxObject thisArg, List<TnBoxObject> args);
} 
