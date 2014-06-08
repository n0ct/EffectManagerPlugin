package com.github.n0ct.effectmanagerplugin.effects.effects.path.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.effects.parameters.EntityTypeEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class TemporaryPathEntityEffect extends AbstractPathEntityEffect {

	private int frequence = 0;
	
	public TemporaryPathEntityEffect(String name) {
		super(name);
	}

	public static TemporaryPathEntityEffect valueOf(Map<String,Object> map) {
		return new TemporaryPathEntityEffect(map);
	}
	
	public static TemporaryPathEntityEffect deserialize(Map<String,Object> map) {
		return new TemporaryPathEntityEffect(map);
	}
	
	public TemporaryPathEntityEffect(Map<String,Object> map) {
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
		final List<Entity> temporaryEntities = new ArrayList<Entity>();
		if (amplifier > halfMaxFreq) {
			temporaryEntities.add(world.spawnEntity(from,entityType));
		}
		if (frequence == halfMaxFreq) {
			temporaryEntities.add(world.spawnEntity(from,entityType));
		} else {
			frequence = frequence + amplifier;
			if (frequence>halfMaxFreq) {
				frequence = halfMaxFreq;
			}
		}
		for(final Entity e : temporaryEntities) {
			runTaskLater(new Runnable() {
				
				@Override
				public void run() {
					e.remove();
				}
			}, getTimeBeforeDespawn());
		}
		
	}
	
	private int getTimeBeforeDespawn() {
		return ((IntegerEffectParameter)getParameters().getParameterWithUniqueName("timeBeforeDespawn", false)).getValue();
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("effect Parameters","params","Simple Path Entity Effect Parameters", false, " ", 3);
		EntityTypeEffectParameter entityType = new EntityTypeEffectParameter("entity","entityType","The type of the entity spawning behind player's steps.", true, EntityType.BAT);
		ret.addSubEffectParameter(entityType);
		IntegerEffectParameter amplifier = new IntegerEffectParameter("amplifier","amplifier","The number of entity spawned.", true, 2, 1, 20);
		ret.addSubEffectParameter(amplifier);
		IntegerEffectParameter timeBeforeDespawn = new IntegerEffectParameter("timeBeforeDespawn","timeBeforeDespawn","The time between the spawn and the despawn of an entity.", true, 200, 1, 10000);
		ret.addSubEffectParameter(timeBeforeDespawn);
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
