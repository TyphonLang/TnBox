package info.iconmaster.tnbox.libs;

import java.util.List;

import info.iconmaster.tnbox.model.TnBoxObject;
import info.iconmaster.tnbox.model.TnBoxThread;
import info.iconmaster.typhon.TyphonInput;

public interface TnBoxFunction {
	public List<TnBoxObject> execute(TnBoxThread thread, TyphonInput tni, TnBoxObject thisArg, List<TnBoxObject> args);
} 
