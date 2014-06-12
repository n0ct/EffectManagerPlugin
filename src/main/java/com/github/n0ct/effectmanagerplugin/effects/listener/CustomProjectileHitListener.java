/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.stirante.MoreProjectiles.event.CustomProjectileHitEvent;

/**
 * @author Benjamin
 *
 */
public class CustomProjectileHitListener extends AbstractEventListener<CustomProjectileHitEvent> {

	public CustomProjectileHitListener() {
		super(CustomProjectileHitEvent.class);
	}
	
	@EventHandler
	public void on(CustomProjectileHitEvent event) {
		onEvent(event);
	}

	@Override
	public void unregister() {
		CustomProjectileHitEvent.getHandlerList().unregister(this);
	}
}
