package com.github.n0ct.effectmanagerplugin.effects.effects.jump;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.PlayerMoveListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.PlayerToggleFlightListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public abstract class AbstractJumpEffect extends AbstractEffect {

	
	private final static String EFFECT_TYPE_NAME = "jump";
	
	private final static String EFFECT_DESCRIPTION = "Modify how the jump works.";
	
	
	public AbstractJumpEffect(Map<String,Object> map) {
		super(map);
	}
	
	public AbstractJumpEffect(String name) {
		super(name);
	}

	@Override
	public final boolean isCallable() {
		return false;
	}

	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}
	
	@Override
	public EffectParameters getDefaultParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 8134181231625273486L;

			{
				add(PlayerToggleFlightListener.class);
				add(PlayerMoveListener.class);
			}
		};
	}

	@Override
	protected final void on(Event event) {
		if (event instanceof PlayerToggleFlightEvent) {
			PlayerToggleFlightEvent playerToggleFlightEvent = (PlayerToggleFlightEvent) event;
			if (playerToggleFlightEvent.getPlayer().getName().equals(getPlayerName())) {
				onPlayerToogleFlightEvent(playerToggleFlightEvent);
			}
			
		}
		if (event instanceof PlayerMoveEvent) {
			PlayerMoveEvent playerMoveEvent = (PlayerMoveEvent) event;
			if (playerMoveEvent.getPlayer().getName().equals(getPlayerName())) {
				onPlayerMoveEvent(playerMoveEvent);
			}
		}
	}
	
	protected abstract void onPlayerMoveEvent(PlayerMoveEvent playerMoveEvent);

	protected abstract void onPlayerToogleFlightEvent(PlayerToggleFlightEvent playerToggleFlightEvent);

	@Override
	protected final String getHelp() {
		return "Modify how the jump works.";
	}

}
