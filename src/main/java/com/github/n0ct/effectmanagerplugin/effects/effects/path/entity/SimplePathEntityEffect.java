package com.github.n0ct.effectmanagerplugin.effects.effects.path.entity;

import java.util.Map;

import org.bukkit.entity.EntityType;

import com.github.n0ct.effectmanagerplugin.effects.parameters.EntityTypeEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class SimplePathEntityEffect extends AbstractPathEntityEffect {

	
	public SimplePathEntityEffect(String name) {
		super(name);
	}

	public static SimplePathEntityEffect valueOf(Map<String,Object> map) {
		return new SimplePathEntityEffect(map);
	}
	
	public static SimplePathEntityEffect deserialize(Map<String,Object> map) {
		return new SimplePathEntityEffect(map);
	}
	
	public SimplePathEntityEffect(Map<String,Object> map) {
		super(map);
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("effect Parameters","params","Simple Path Entity Effect Parameters", false, " ", 3);
		EntityTypeEffectParameter entityType = new EntityTypeEffectParameter("entity","entityType","The type of the entity spawning behind player's steps.", true, EntityType.BAT);
		ret.addSubEffectParameter(entityType);
		IntegerEffectParameter amplifier = new IntegerEffectParameter("amplifier","amplifier","The number of entity spawned.", true, 2, 1, 20);
		ret.addSubEffectParameter(amplifier);
		return ret;
	}

	@Override
	protected String getHelp() {
		return "Spawn entity behind player's steps";
	}

	@Override
	protected String getDescription() {
		return "Spawn entity behind player's steps. The number of entities can be defined in the 'amplifier' parameter. The type of the entities spawning behind the player steps is defined in the 'entity' parameter.";
	}

}
