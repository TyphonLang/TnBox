package info.iconmaster.tnbox.model;

import java.util.List;

import info.iconmaster.typhon.TyphonInput;

public interface TnBoxFunction {
	public List<TnBoxObject> execute(TnBoxThread thread, TyphonInput tni, TnBoxObject thisArg, List<TnBoxObject> args);
} 
