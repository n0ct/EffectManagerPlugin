package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

public class EntityDamageByEntityListener extends AbstractEventListener<EntityDamageByEntityEvent> {
	public EntityDamageByEntityListener() {
		super(EntityDamageByEntityEvent.class);
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		onEvent(event);
	}
}
