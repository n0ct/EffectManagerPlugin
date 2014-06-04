package com.github.n0ct.effectmanagerplugin.effects.generic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.reflections.Reflections;

import com.github.n0ct.effectmanagerplugin.effects.PlayerEffectManager;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;


public abstract class AbstractEffect implements Observer, Cloneable, ConfigurationSerializable {

	protected static ArrayList<Class<? extends AbstractEventListener<?>>> NEEDED_EVENTS_LISTENERS;
	
	private EffectParameters effectParameter;
	
	private String name;
	
	private String playerName;
	

	public abstract EffectType getType();

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new TreeMap<String,Object>();
		map.put("parameters", this.effectParameter.serialize());
		map.put("name", this.name);
		map.put("className", this.getClass().getName());
		return map;
	}
	
	@SuppressWarnings({ "unchecked" })
	public AbstractEffect(Map<String,Object> map) {
		this.effectParameter = EffectParameters.deserialize((Map<String, Object>) map.get("parameters"));
		this.name = (String) map.get("name");
	}
	
	public AbstractEffect(String name) {
		this.name = name;
	}

	public final EffectParameters getParameters() {
		return this.effectParameter;
	}
	
	public final void setParameters(EffectParameters effectParameter) {
		this.effectParameter = effectParameter;
	}

	public final String getName() {
		return this.name;
	}
	
	public final void setEffectParameterFromString(String str) {
		EffectParameters effectParameters = getDefaultParameters();
		effectParameters.setValueFromString(str);
		this.effectParameter = effectParameters;
	}

	public abstract EffectParameters getDefaultParameters();

	@Override
	public final void update(Observable o, Object arg) {
		this.on((Event)arg);
	}
	
	public abstract ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents();
	
	public final void call() {
		if (getPlayerName() == null) {
			throw new IllegalArgumentException("effect " +getName()+ " cannot be called since it is not associated to a player.");
		}
		onCall();
	}
	
	protected abstract void on(Event event);
	
	protected void onCall() {
		if (!isCallable()) {
			throw new IllegalArgumentException("The effect " + getName() + " is not callable.");
		}
		throw new IllegalArgumentException("[INTERNAL ERROR]The call of effect " + getName() + " has not been implemented yet.");
	}
	
	public abstract boolean isCallable();
	
	protected abstract String getHelp();
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean showParameters) {
		StringBuilder sb = new StringBuilder(ChatColor.DARK_AQUA.toString());
		sb.append("name: ").append(ChatColor.WHITE).append(getName()).append("\n");
		sb.append(ChatColor.DARK_AQUA).append("description: ").append(ChatColor.WHITE).append(getDescription()).append("\n");
		sb.append(ChatColor.DARK_AQUA).append("type: ").append(ChatColor.WHITE).append(getType().toString());
		if (showParameters) {
			sb.append("\n").append(ChatColor.DARK_AQUA).append("Effet parameters: \n").append(ChatColor.WHITE).append(getParameters());
		}
		return sb.toString();
	}

	protected abstract String getDescription();

	public final AbstractEffect applyToPlayer(String playerName) throws CloneNotSupportedException {
		AbstractEffect newEffect = null;
		newEffect = (AbstractEffect) this.clone();
		
		newEffect.setPlayerName(playerName);
		return newEffect;
	}

	private void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerName() {
		return playerName;
	}
	
	protected final void runTaskLater(Runnable runnable, int duration) {
		PlayerEffectManager.getInstance().runTaskLater(runnable,duration);
	}

	protected final Player getPlayer() {
		return PlayerEffectManager.getInstance().getPlayer(getPlayerName());
	}

	public static AbstractEffect deserialize(Map<String,Object> map) {
		Map<String, Class<? extends AbstractEffect>> effectClasses = new TreeMap<String, Class<? extends AbstractEffect>>();
		Reflections reflections = new Reflections("com.github.n0ct.effectmanagerplugin.effects.effects");
		for (Class<? extends AbstractEffect> effectClass : reflections.getSubTypesOf(AbstractEffect.class)) {
			if (!Modifier.isAbstract(effectClass.getModifiers())) {
				effectClasses.put(effectClass.getName(),effectClass);
			}
		}
		Class<? extends AbstractEffect> effectClass = null;
		String effectClassName = (String) map.get("className");
		for(String curEffectClassName : effectClasses.keySet()) {
			if (curEffectClassName == effectClassName) {
				effectClass = effectClasses.get(curEffectClassName);
			}
		}
		Constructor<? extends AbstractEffect> constructor;
		AbstractEffect effect = null;
		try {
			constructor = effectClass.getConstructor(Map.class);
			effect = constructor.newInstance(map);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("[INTERNAL ERROR] An error ocurred durign the deserialization process.",e);
		}
		return effect;
	}
	
	public static AbstractEffect valueOf(Map<String,Object> map) {
		return deserialize(map);
	}

}
