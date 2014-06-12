package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

public class PlayerToggleSneakListener extends AbstractEventListener<PlayerToggleSneakEvent> {

	public PlayerToggleSneakListener() {
		super(PlayerToggleSneakEvent.class);
	}

	@EventHandler
	public void on(PlayerToggleSneakEvent event) {
		onEvent(event);
	}
	
	@Override
	public void unregister() {
		PlayerToggleSneakEvent.getHandlerList().unregister(this);
	}
}
