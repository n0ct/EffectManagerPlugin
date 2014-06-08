package com.github.n0ct.effectmanagerplugin.effects.effects.jump;

import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.n0ct.effectmanagerplugin.effects.parameters.DoubleEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class DoubleJumpEffect extends AbstractJumpEffect {

	public static DoubleJumpEffect valueOf(Map<String,Object> map) {
		return new DoubleJumpEffect(map);
	}
	
	public static DoubleJumpEffect deserialize(Map<String,Object> map) {
		return new DoubleJumpEffect(map);
	}
	
	public DoubleJumpEffect(Map<String,Object> map) {
		super(map);
	}
	
	public DoubleJumpEffect(String name) {
		super(name);
	}

	@Override
	protected void onPlayerToogleFlightEvent(PlayerToggleFlightEvent playerToggleFlightEvent) {
		Player p = playerToggleFlightEvent.getPlayer();
		if (p.getGameMode() == GameMode.CREATIVE)
			return;
		playerToggleFlightEvent.setCancelled(true);
		p.setAllowFlight(false);
		p.setFlying(false);
		
		p.setVelocity(p.getLocation().getDirection().multiply(getMultiplier()).setY(getJumpHeight()));
	}

	@Override
	protected void onPlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
		Player p = playerMoveEvent.getPlayer();
		if ((p.getGameMode() != GameMode.CREATIVE) && (p.getLocation().subtract(0,1,0).getBlock().getType() != Material.AIR) && (!p.isFlying())) {
			p.setAllowFlight(true);
		}
		
	}
	

	private double getMultiplier() {
		return ((DoubleEffectParameter)getParameters().getParameterWithUniqueName("velocityMultiplier", false)).getValue();
	}
	
	private double getJumpHeight() {
		return ((DoubleEffectParameter)getParameters().getParameterWithUniqueName("jumpHeight", false)).getValue();
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("Double Jump Effect Parameters","params","Double Jump Effect Parameters.", true, " ", 0);
		DoubleEffectParameter velocityMultiplier = new DoubleEffectParameter("velocityMultiplier","velocityMultiplier","Defines the multiplier that increase the horizontal velocity of the player upon double jumping.", true, 1.5, 1.0, 3.0);
		ret.addSubEffectParameter(velocityMultiplier);
		DoubleEffectParameter jumpHeight = new DoubleEffectParameter("jumpHeight","jumpHeight","Defines the height of the double jump.", true, 1.0, 0.5, 3.0);
		ret.addSubEffectParameter(jumpHeight);
		return ret;
	}
	

	@Override
	protected String getDescription() {
		return "Allow the player to double jump in survival gamemode.\n"
				+ "The first parameter ('velocityMultiplier') can contains a decimal number to increase the horizontal velocity of a player during the jump.\n"
				+"The second parameter ('jumpHeight') is  another decimal number used to define the height of the jump."; 
	}


}
