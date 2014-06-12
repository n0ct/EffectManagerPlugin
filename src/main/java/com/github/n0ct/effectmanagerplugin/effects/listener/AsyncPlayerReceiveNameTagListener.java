package com.github.n0ct.effectmanagerplugin.effects.listener;

import org.bukkit.event.EventHandler;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

public class AsyncPlayerReceiveNameTagListener extends AbstractEventListener<AsyncPlayerReceiveNameTagEvent> {

	public AsyncPlayerReceiveNameTagListener() {
		super(AsyncPlayerReceiveNameTagEvent.class);
	}

	@EventHandler
	public void on(AsyncPlayerReceiveNameTagEvent event) {
		onEvent(event);
	}
	
	@Override
	public void unregister() {
		AsyncPlayerReceiveNameTagEvent.getHandlerList().unregister(this);
	}
}
