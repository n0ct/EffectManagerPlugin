package com.github.n0ct.effectmanagerplugin.effects.effects.path.entity;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.effects.effects.path.AbstractPathEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.parameters.EntityTypeEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;

public abstract class AbstractPathEntityEffect extends AbstractPathEffect {

	private static final String EFFECT_TYPE_NAME = "spawnEntityOnPath";
	
	private static final String EFFECT_DESCRIPTION = "spawn entity behind the player's steps.";
	
	private int frequence = 0;
	
	@Override
	public final EffectType getType() {
			return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}
	
	public AbstractPathEntityEffect(String name) {
		super(name);
	}
	
	public AbstractPathEntityEffect(Map<String, Object> map) {
		super(map);
		
	}


	@Override
	protected void doPathModifications(Location from, Player player) {
		World world = player.getWorld();
		int maxAmplifier = ((IntegerEffectParameter)getParameters().getParameterWithUniqueName("amplifier", false)).getMax();
		int amplifier = getAmplifier();
		EntityType entityType = getEntityType();
		// From that amplifier value we must create one entity each times the player moves.
		int halfMaxFreq = maxAmplifier / 2;
		if (amplifier > halfMaxFreq) {
			world.spawnEntity(from,entityType);
		}
		if (frequence == halfMaxFreq) {
			world.spawnEntity(from,entityType);
		} else {
			frequence = frequence + amplifier;
			if (frequence>halfMaxFreq) {
				frequence = halfMaxFreq;
			}
		}
	}
	

	/**
	 * @return the amplifier
	 */
	public int getAmplifier() {
		return ((IntegerEffectParameter)getParameters().getParameterWithUniqueName("amplifier", false)).getValue();
	}

	/**
	 * @return the entityType
	 */
	public EntityType getEntityType() {
		return ((EntityTypeEffectParameter)getParameters().getParameterWithUniqueName("entityType", false)).getValue();
	}

}
