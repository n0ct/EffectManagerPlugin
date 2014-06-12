/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.reflections.Reflections;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectMap;

/**
 * @author Benjamin
 *
 */
public class EffectManager implements ConfigurationSerializable {

	private EffectMap effects;
	
	private EffectFactory effectFactory;
	
	private Map<String, Class<? extends AbstractEffect>> effectClasses;

	private EffectManagerPlugin plugin;
	

	@Override
	public Map<String, Object> serialize() {
		return effects.serialize();
	}
	
	public EffectManager(Map<String,Object> map) {
		this(EffectManagerPlugin.getPlugin(EffectManagerPlugin.class));
		this.effects = EffectMap.deserialize(map);
	}
	
	public EffectManager(EffectManagerPlugin plugin) {
		this.plugin = plugin;
		this.effects = new EffectMap();
		effectFactory = new EffectFactory(this);
		//stores the effectClasses
		effectClasses = new TreeMap<String, Class<? extends AbstractEffect>>();
		Reflections reflections = new Reflections("com.github.n0ct.effectmanagerplugin.effects.effects");
		for (Class<? extends AbstractEffect> effectClass : reflections.getSubTypesOf(AbstractEffect.class)) {
			if (!Modifier.isAbstract(effectClass.getModifiers())) {
				effectClasses.put(effectClass.getName().substring(0, effectClass.getName().indexOf("Effect",effectClass.getName().length()-7)), effectClass);
			}
		}
	}
	

	public Map<String, AbstractEffect> getEffects() {
		return effects;
	}

	public Map<String, Class<? extends AbstractEffect>> getEffectClasses() {
		return effectClasses;
	}
	
	public Class<? extends AbstractEffect> getEffectClass(String effectClassName) {
		for (String currentClassName : getEffectClassesNames()) {
			String[] classNameArray = currentClassName.split("\\.");
			if (classNameArray[classNameArray.length-1].equals(effectClassName)) {
				return effectClasses.get(currentClassName);
			}
		}
		return null;
	}
	
	public List<String> getEffectClassesNames() {
		return new ArrayList<String>(this.effectClasses.keySet());
	}
	
	public boolean effectClassExists(String effectClassName) {
		for (String currentClassName : getEffectClassesNames()) {
			String[] classNameArray = currentClassName.split("\\.");
			if (classNameArray[classNameArray.length-1].equals(effectClassName)) {
				return true;
			}
		}
		return false;
	}

	private void add(AbstractEffect effect) {
		if (this.effects.containsKey(effect.getName())) {
			throw new IllegalArgumentException("An effect named " + effect.getName() +" already exists.");
		}
		this.effects.put(effect.getName(),effect);
	}
	
	public List<String> getAll() {
		return new ArrayList<String>(effects.keySet());
	}
	
	public AbstractEffect get(String effectName) {
		if (!this.effects.containsKey(effectName)) return null;
		return this.effects.get(effectName);
	}
	
	public void remove(String effectName) {
		if (!this.effects.containsKey(effectName)) {
			throw new IllegalArgumentException("The effect named " + effectName + " doesn't exist.");
		}
		for (List<AbstractEffect> appliedEffects : plugin.getPlayerEffectManager().getPlayersEffects().values()) {
			for (AbstractEffect appliedEffect : appliedEffects) {
				if (appliedEffect.getName() == plugin.getEffectManager().get(effectName).getName()) {
					throw new IllegalArgumentException("The effect " + effectName + " is currently applied to a player so it cannot be deleted");
				}
			}
		}
		this.effects.remove(effectName);
	}
	
	public boolean contains(String effectName) {
		return this.effects.containsKey(effectName);
	}

	public AbstractEffect createEffect(String name, String effectClassName, String effectCreationParameters) throws IllegalArgumentException {
		AbstractEffect effect = null;
		try{
			effect = effectFactory.createEffect(name, effectClassName, effectCreationParameters);
			add(effect);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("An error occured during the effect creation: " + e.getClass().toString() +".");
		}
		return effect;
	}


	public AbstractEffect getDefaultEffectForClass(String name) throws IllegalArgumentException {
		AbstractEffect effect = null;
		try {
			effect = effectFactory.createDefaultEffect(name);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | SecurityException | InstantiationException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("An error occured during the default parameters creation: " + e.getClass().toString() +".");
		}
		return effect;
	}

	public static EffectManager deserialize(Map<String,Object> map) {
		return new EffectManager(map);
	}
	
	public static EffectManager valueOf(Map<String,Object> map) {
		return new EffectManager(map);
	}

	public void clear() {
		Set<String> players = effects.keySet();
		String[] playersArray = new String[players.size()];
		players.toArray(playersArray);
		for(int i =0;i<playersArray.length; i++) {
			this.remove(playersArray[i]);
		}
	}
}
