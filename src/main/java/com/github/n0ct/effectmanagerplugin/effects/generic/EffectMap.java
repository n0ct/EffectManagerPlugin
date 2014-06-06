package com.github.n0ct.effectmanagerplugin.effects.generic;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class EffectMap extends TreeMap<String,AbstractEffect> implements ConfigurationSerializable {

	private static final long serialVersionUID = 7241196737577668439L;

	public EffectMap() {
		super();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new TreeMap<String, Object>();
		for (AbstractEffect effect : this.values()) {
			map.put(effect.getName(),effect.serialize());
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public EffectMap(Map<String, Object> map) {
		this();
		for (String key : map.keySet()) {
			Object obj = map.get(key);
			if (obj instanceof MemorySection && !key.contains(".")) {
				put(key, AbstractEffect.deserialize(((MemorySection) obj).getValues(true)));
				
			} else if (obj instanceof Map){
				put(key, AbstractEffect.deserialize((Map<String, Object>) map.get(key)));
			}
		}
	}

	public static EffectMap deserialize(Map<String,Object> map) {
		return new EffectMap(map);
	}
	public static EffectMap valueOf(Map<String,Object> map) {
		return new EffectMap(map);
	}

}
