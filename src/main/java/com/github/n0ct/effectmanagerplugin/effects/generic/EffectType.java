package com.github.n0ct.effectmanagerplugin.effects.generic;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class EffectType implements ConfigurationSerializable {

	/**
	 *  le nom du type d'effet
	 */
	private String name;
	
	/**
	 * La description du type d'effet
	 */
	private String description;
	
	private boolean stackable;
	
	public EffectType(String name, String description) {
		this(name,description,false);
	}
	
	public EffectType(String name, String description, boolean stackable) {
		this.name=name;
		this.description=description;
		this.stackable = stackable;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public boolean equals(EffectType e) {
		return e.getName().equals(this.getName());
	}
	
	public boolean isStackable() {
		return stackable;
	}
	
	@Override
	public String toString() {
		return this.getName() + ": " + this.getDescription();
	}

	public EffectType(Map<String,Object> map) {
		this((String)map.get("name"),(String)map.get("description"),(Boolean)map.get("stackable"));
	}
	
	public static EffectType deserialize(Map<String,Object> map) {
		return new EffectType(map);
	}
	
	public static EffectType valueOf(Map<String,Object> map) {
		return new EffectType(map); 
	}
	
	public Map<String,Object> serialize() {
		Map<String,Object> map = new TreeMap<String,Object>();
		map.put("name",this.name);
		map.put("description",this.description);
		map.put("stackable",this.stackable);
		return map;
	}
}
