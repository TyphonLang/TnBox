package info.iconmaster.tnbox.libs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.iconmaster.tnbox.model.TnBoxObject;
import info.iconmaster.tnbox.model.TnBoxThread;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Function;

public interface TnBoxFunction {
	public static final Map<TyphonInput, Map<Function, TnBoxFunction>> registry = new HashMap<>();
	
	public List<TnBoxObject> execute(TnBoxThread thread, TyphonInput tni, TnBoxObject thisArg, List<TnBoxObject> args);
} 
