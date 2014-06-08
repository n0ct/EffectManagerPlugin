package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

public class EntityDamageListener extends AbstractEventListener<EntityDamageEvent> {

	public EntityDamageListener() {
		super(EntityDamageEvent.class);
	}

	@EventHandler
	public void on(EntityDamageEvent event) {
		onEvent(event);
	}
	
	@Override
	public void unregister() {
		EntityDamageEvent.getHandlerList().unregister(this);
	}
}
