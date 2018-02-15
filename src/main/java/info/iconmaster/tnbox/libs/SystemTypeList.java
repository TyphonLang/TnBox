package info.iconmaster.tnbox.libs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import info.iconmaster.tnbox.model.TnBoxObject;
import info.iconmaster.tnbox.model.TyphonInputData;
import info.iconmaster.typhon.TyphonInput;
import info.iconmaster.typhon.model.Function;
import info.iconmaster.typhon.model.Parameter;
import info.iconmaster.typhon.model.TemplateArgument;
import info.iconmaster.typhon.types.TemplateType;
import info.iconmaster.typhon.types.Type;
import info.iconmaster.typhon.types.TypeRef;
import info.iconmaster.typhon.types.UserType;

/**
 * This is an instance of the concrete type used for lists generated by Typhon list constants and List's factory default constructor.
 * 
 * @author iconmaster
 *
 */
public class SystemTypeList extends UserType {
	public static class Iterator extends UserType {
		public static class Value {
			ArrayList<TnBoxObject> list;
			int index;
			
			public Value(ArrayList<TnBoxObject> list) {
				this.list = list;
				this.index = 0;
			}
		}
		
		public TemplateType T;
		
		public Function FUNC_NEXT, FUNC_DONE;
		
		public Iterator(TyphonInputData tniData) {
			super(tniData.tni, "%SystemListIterator"); markAsLibrary();
			TyphonInput input = tniData.tni;
			
			// add templates
			getTemplates().add(T = new TemplateType("T", input.corePackage.TYPE_ANY, null));
			
			// add parents
			getParentTypes().add(new TypeRef(input.corePackage.TYPE_ITERATOR, new TemplateArgument(T)));
			
			// add method overrides
			getTypePackage().addFunction(FUNC_NEXT = new Function(tni, "next", new TemplateType[] {
					
			}, new Parameter[] {
					
			}, new Type[] {
					T
			}));
			Function.setOverride(input.corePackage.TYPE_ITERATOR.FUNC_NEXT, FUNC_NEXT);
			
			getTypePackage().addFunction(FUNC_DONE = new Function(tni, "done", new TemplateType[] {
					
			}, new Parameter[] {
					
			}, new Type[] {
					tni.corePackage.TYPE_BOOL
			}));
			Function.setOverride(input.corePackage.TYPE_ITERATOR.FUNC_DONE, FUNC_DONE);
			
			// add TnBox implementation
			tniData.functionHandlers.put(FUNC_NEXT, (thread, tni1, thiz, args)->{
				Value v = (Value) thiz.value;
				TnBoxObject ret = v.list.get(v.index);
				v.index++;
				return Arrays.asList(ret);
			});
			
			tniData.functionHandlers.put(FUNC_DONE, (thread, tni1, thiz, args)->{
				Value v = (Value) thiz.value;
				return Arrays.asList(new TnBoxObject(tni.corePackage.TYPE_BOOL, v.index >= v.list.size()));
			});
		}
	}
	
	public TemplateType T;
	public Iterator TYPE_ITERATOR;
	
	public Function FUNC_GET, FUNC_SET, FUNC_SIZE, FUNC_ITERATOR;
	
	public SystemTypeList(TyphonInputData tniData) {
		super(tniData.tni, "%SystemList"); markAsLibrary();
		TyphonInput input = tniData.tni;
		
		// add templates
		getTemplates().add(T = new TemplateType("T", input.corePackage.TYPE_ANY, null));
		
		// add parents
		getParentTypes().add(new TypeRef(input.corePackage.TYPE_LIST, new TemplateArgument(T)));
		
		// add subtypes
		TYPE_ITERATOR = new Iterator(tniData);
		
		// add method overrides
		getTypePackage().addFunction(FUNC_GET = new Function(tni, "get", new TemplateType[] {
		
		}, new Parameter[] {
				new Parameter(tni, "i", tni.corePackage.TYPE_INT, false)
		}, new Type[] {
				T
		}));
		Function.setOverride(input.corePackage.TYPE_LIST.FUNC_GET, FUNC_GET);
		
		getTypePackage().addFunction(FUNC_SET = new Function(tni, "set", new TemplateType[] {
				
		}, new Parameter[] {
				new Parameter(tni, "v", T, false),
				new Parameter(tni, "i", tni.corePackage.TYPE_INT, false)
		}, new Type[] {
				
		}));
		Function.setOverride(input.corePackage.TYPE_LIST.FUNC_SET, FUNC_SET);
		
		getTypePackage().addFunction(FUNC_SIZE = new Function(tni, "size", new TemplateType[] {
				
		}, new Parameter[] {
				
		}, new Type[] {
				tni.corePackage.TYPE_INT
		}));
		Function.setOverride(input.corePackage.TYPE_LIST.FUNC_SIZE, FUNC_SIZE);
		
		getTypePackage().addFunction(FUNC_ITERATOR = new Function(tni, "iterator", new TemplateType[] {
				
		}, new Parameter[] {
				
		}, new TypeRef[] {
				new TypeRef(TYPE_ITERATOR, new TemplateArgument(T))
		}));
		Function.setOverride(tni.corePackage.TYPE_ITERABLE.FUNC_ITERATOR, FUNC_ITERATOR);
		
		// add TnBox implementation
		tniData.allocHandlers.put(this, (tni1)->new ArrayList<TnBoxObject>());
		
		tniData.functionHandlers.put(FUNC_GET, (thread, tni1, thiz, args)->{
			ArrayList<TnBoxObject> a = (ArrayList<TnBoxObject>) thiz.value;
			int i = (Integer) args.get(0).value;
			return Arrays.asList(a.get(i));
		});
		
		tniData.functionHandlers.put(FUNC_SET, (thread, tni1, thiz, args)->{
			ArrayList<TnBoxObject> a = (ArrayList<TnBoxObject>) thiz.value;
			TnBoxObject v = args.get(0);
			int i = (Integer) args.get(1).value;
			
			a.set(i, v);
			return Arrays.asList();
		});
		
		tniData.functionHandlers.put(FUNC_SIZE, (thread, tni1, thiz, args)->{
			ArrayList<TnBoxObject> a = (ArrayList<TnBoxObject>) thiz.value;
			return Arrays.asList(new TnBoxObject(tni.corePackage.TYPE_INT, a.size()));
		});
		
		tniData.functionHandlers.put(FUNC_ITERATOR, (thread, tni1, thiz, args)->{
			ArrayList<TnBoxObject> a = (ArrayList<TnBoxObject>) thiz.value;
			return Arrays.asList(new TnBoxObject(TYPE_ITERATOR, new Iterator.Value(a)));
		});
	}
}
