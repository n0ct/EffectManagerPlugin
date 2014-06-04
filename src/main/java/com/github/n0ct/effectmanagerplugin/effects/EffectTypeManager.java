/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects;

import java.util.Map;
import java.util.TreeMap;

import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;

/**
 * @author Benjamin
 *
 */
public class EffectTypeManager {

	private static EffectTypeManager instance;
	
	private Map<String,EffectType> effectTypes;
	
	public static EffectTypeManager getInstance() {
		if (instance == null) {
			instance = new EffectTypeManager();
		}
		return instance;
	}
	
	private EffectTypeManager() {
		this.effectTypes=new TreeMap<String,EffectType>();
	}
	
	public boolean addEffectType(EffectType effectType) {
		if (effectTypeExists(effectType.getName())) return false;
		effectTypes.put(effectType.getName(),effectType);
		return true;
	}
	
	public boolean effectTypeExists(String effectTypeName) {
		if (effectTypes.containsKey(effectTypeName)) return true;
		return false;
	}
}
