package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import org.bukkit.potion.PotionEffectType;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class PotionEffectTypeEffectParameter extends AbstractPrimitiveEffectParameter<PotionEffectType> {

	public static PotionEffectTypeEffectParameter valueOf(Map<String,Object> map) {
		return new PotionEffectTypeEffectParameter(map);
	}
	
	public static PotionEffectTypeEffectParameter deserialize(Map<String,Object> map) {
		return new PotionEffectTypeEffectParameter(map);
	}
	
	public PotionEffectTypeEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public PotionEffectTypeEffectParameter(String name, String uniqueName,
			String description, boolean optionnal, PotionEffectType defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setValueFromString(String str) {
		PotionEffectType effect;
		try {
			 effect = PotionEffectType.getById(Integer.parseInt(str));
		} catch (NumberFormatException e) {
			effect = null;
		}
		if (effect == null) {
			effect = PotionEffectType.getByName(str.toUpperCase());
		}
		setValue(effect);

	}

	@Override
	public boolean isValid(PotionEffectType value) {
		if (value == null) return false;
		return true;
	}

	@Override
	protected String getString(PotionEffectType value) {
		return value.getName();
	}

	@Override
	protected PotionEffectType getFromString(String string) {
		return PotionEffectType.getByName(string);
	}

}
