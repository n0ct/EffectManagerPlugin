package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

/**
 * 
 * @author Benjamin
 *
 */
public class PlayerInteractListener extends AbstractEventListener<PlayerInteractEvent> {

	public PlayerInteractListener() {
		super(PlayerInteractEvent.class);
	}
	
	@EventHandler
	public void on(PlayerInteractEvent event) {
		onEvent(event);
	}

	@Override
	public void unregister() {
		PlayerInteractEvent.getHandlerList().unregister(this);
	}

}
