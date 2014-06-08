package com.github.n0ct.effectmanagerplugin.effects.effects.path.entity;

import java.util.Map;

import org.bukkit.entity.EntityType;

import com.github.n0ct.effectmanagerplugin.effects.effects.path.AbstractPathEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.parameters.EntityTypeEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;

public abstract class AbstractPathEntityEffect extends AbstractPathEffect {

	private static final String EFFECT_TYPE_NAME = "spawnEntityOnPath";
	
	private static final String EFFECT_DESCRIPTION = "spawn entity behind the player's steps.";
	
	
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
