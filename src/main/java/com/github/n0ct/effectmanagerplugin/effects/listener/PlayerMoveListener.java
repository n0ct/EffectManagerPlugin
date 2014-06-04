/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

/**
 * @author Benjamin
 *
 */
public class PlayerMoveListener extends AbstractEventListener<PlayerMoveEvent> {

	public PlayerMoveListener() {
		super(PlayerMoveEvent.class);
	}
	
	@EventHandler
	public void on(PlayerMoveEvent event) {
		onEvent(event);
	}
}
