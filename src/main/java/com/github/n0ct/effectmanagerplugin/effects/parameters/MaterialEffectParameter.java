package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import org.bukkit.Material;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class MaterialEffectParameter extends AbstractPrimitiveEffectParameter<Material> {

	public static MaterialEffectParameter valueOf(Map<String,Object> map) {
		return new MaterialEffectParameter(map);
	}
	
	public static MaterialEffectParameter deserialize(Map<String,Object> map) {
		return new MaterialEffectParameter(map);
	}
	
	public MaterialEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public MaterialEffectParameter(String name, String uniqueName,	String description, boolean optionnal, Material defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
	}

	@Override
	public boolean isValid(Material value) {
		if (value != null) return true;
		return false;
	}


	@SuppressWarnings("deprecation")
	@Override
	public void setValueFromString(String str) {
		Integer i=null;
		Material m = null;
		try {
			i = Integer.valueOf(str);
		} catch(NumberFormatException e) {
			
		}
		
		if (i == null) {
			m = Material.matchMaterial(str);
		} else {
			m = Material.getMaterial(i);
		}
		if (m == null) {
			throw new IllegalArgumentException("Material " + str + " doesn't exists.");
		}
		setValue(m);
	}

	@Override
	protected String getString(Material value) {
		return value.name();
	}

	@Override
	protected Material getFromString(String string) {
		return Material.matchMaterial(string);
	}

}
