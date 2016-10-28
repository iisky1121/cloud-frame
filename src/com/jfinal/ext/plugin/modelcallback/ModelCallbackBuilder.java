package com.jfinal.ext.plugin.modelcallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jfinal.ext.kit.ClassSearcher;
import com.jfinal.plugin.activerecord.Model;

@SuppressWarnings({"rawtypes"})
public class ModelCallbackBuilder {
	public enum EventType{
		save,update;
	}
	private static boolean isStart = false;
	private static Map<IModelCallback, Class> callbackMaps = new HashMap<IModelCallback, Class>();
	private static List<Class> callbackClasses = new ArrayList<Class>();
	
	public static void call(Model<?> model, EventType eventType){
		if(isStart && model != null){
			for(Entry<IModelCallback, Class> entry : callbackMaps.entrySet()){
				if(entry.getValue().getName().equals(model.getClass().getName())){
					execute(entry.getKey(), eventType, model);
					return;
				}
			}
		}
	}
	
	static void close(){
		isStart = false;
		callbackMaps.clear();
		callbackClasses.clear();
	}
	
	static void execute(IModelCallback callback, EventType eventType, Model<?> model){
		if(eventType.equals(EventType.save)){
			callback.save(model);
		}
		else if(eventType.equals(EventType.update)){
			callback.update(model);
		}
	}
	
	static void init(){
		List<Class<? extends IModelCallback>> callbackLists = ClassSearcher.of(IModelCallback.class).includeAllJarsInLib(false).search();
		Class modelClass;
		ModelCallback mcb;
		try {
			for(Class<? extends IModelCallback> callbackClass : callbackLists){
				mcb = (ModelCallback) callbackClass.getAnnotation(ModelCallback.class);
				if(mcb != null){
					modelClass = mcb.modelClass();
					if(callbackClasses.contains(callbackClass)){
						throw new IllegalArgumentException(String.format("Model:%s,监听已存在！", modelClass.getSimpleName()));
					}
					callbackClasses.add(callbackClass);
					callbackMaps.put(callbackClass.newInstance(), modelClass);
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		isStart = true;
	}
}
