package com.github.n0ct.effectmanagerplugin.effects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.IllegalClassException;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

public class EventListenerManager {

	PlayerEffectManager playerEffectManager;
	
	private Map<String, AbstractEventListener<?>> eventListeners;

	public EventListenerManager() {
		eventListeners = new TreeMap<String,AbstractEventListener<?>>();
		this.playerEffectManager = EffectManagerPlugin.getPlugin(EffectManagerPlugin.class).getPlayerEffectManager();
	}
	
	public AbstractEventListener<?> getEventListener(Class<? extends AbstractEventListener<?>> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
		for (String currentClassName : eventListeners.keySet()) {
			if (currentClassName == clazz.getName()) {
				return eventListeners.get(currentClassName);
			}
		}
		
		Constructor<?>[] constructors = clazz.getConstructors();
		AbstractEventListener<?> eventListener = null;
		if (constructors.length != 1) throw new IllegalClassException("An effect class must contain 1 and only 1 constructor");
		try {
			eventListener = ((AbstractEventListener<?>) constructors[0].newInstance());
		} catch(Exception e) {
			e.printStackTrace();
		}
		if (eventListener == null) {
			throw new IllegalArgumentException("[INTERNAL ERROR] error during the event listeners initialization.");
		}
		playerEffectManager.registerEvents(eventListener);
		eventListeners.put(clazz.getName(), eventListener);
		
		return eventListener;
	}
}
