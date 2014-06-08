/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

/**
 * @author Benjamin
 *
 */
public class PlayerToggleFlightListener extends AbstractEventListener<PlayerToggleFlightEvent> {

	public PlayerToggleFlightListener() {
		super(PlayerToggleFlightEvent.class);
	}
	
	@EventHandler
	public void on(PlayerToggleFlightEvent event) {
		onEvent(event);
	}

	@Override
	public void unregister() {
		PlayerToggleFlightEvent.getHandlerList().unregister(this);
	}
}
