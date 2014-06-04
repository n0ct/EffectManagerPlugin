package com.github.n0ct.effectmanagerplugin.effects.listener.generic;

import java.util.Observable;

import org.bukkit.event.Event;
import org.bukkit.event.Listener;

public abstract class AbstractEventListener<T extends Event> extends Observable implements Listener {
	
	private Class<T> clazz;
	
	public AbstractEventListener() {
	}
	
	protected AbstractEventListener(Class<T> clazz) {
		this.clazz = clazz;
	}

	protected void onEvent(T event) {
        setChanged(); // Positionne son indicateur de changement
        notifyObservers(event); // notification
	}
	
	public Class<? extends Event> getObservedEvent() {
		return clazz;
	}
}
