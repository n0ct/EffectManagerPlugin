package com.github.n0ct.effectmanagerplugin.effects.effects.path;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.listener.PlayerMoveListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;

public abstract class AbstractPathEffect extends AbstractEffect {

	protected static final int MIN_HEIGHT = 3;

	protected static final int MAX_HEIGHT = 253;
	
	public AbstractPathEffect(String name) {
		super(name);
	}

	public AbstractPathEffect(Map<String, Object> map) {
		super(map);
	}

	@Override
	public final ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 6619922397961321409L;
			{
				add(PlayerMoveListener.class);
			}
		};
	}
	

	@Override
	protected final void on(Event argEvent) {
		if (!(argEvent instanceof PlayerMoveEvent)) {
			return;
		}
		PlayerMoveEvent event = (PlayerMoveEvent)argEvent;
		//On ne fait rien si le joueur n'a pas l'effet
		if (!getPlayerUUID().equals(event.getPlayer().getUniqueId()))
		{
			return;
		}
		//On ne fait rien si le joueur ne change pas de block lors du deplacement
    	if (!isPlayerLeavingBlock(event)) {
    		return;
    	}
    	
    	//On ne fait rien si le joueur est au dessus ou au dessous de la limite des blocs
    	if (isPlayerTooHighOrLow(event)) {
    		return;
    	}
    	doPathModifications(event.getFrom(), event.getPlayer());
    	
	}

	protected abstract void doPathModifications(Location from, Player player);

	private final boolean isPlayerTooHighOrLow(PlayerMoveEvent event) {
		if (event.getFrom().getY()>MAX_HEIGHT || event.getFrom().getY()<MIN_HEIGHT) {
			return true;
		}
		return false;
	}

	private final boolean isPlayerLeavingBlock(PlayerMoveEvent event) {
		if(event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
			return false;
		}
		return true;
	}
	
	@Override
	public final boolean isCallable() {
		return false;
	}

}
