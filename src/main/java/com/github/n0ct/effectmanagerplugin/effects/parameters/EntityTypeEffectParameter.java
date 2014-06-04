package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import org.bukkit.entity.EntityType;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class EntityTypeEffectParameter extends AbstractPrimitiveEffectParameter<EntityType> {

	public static EntityTypeEffectParameter valueOf(Map<String,Object> map) {
		return new EntityTypeEffectParameter(map);
	}
	
	public static EntityTypeEffectParameter deserialize(Map<String,Object> map) {
		return new EntityTypeEffectParameter(map);
	}
	
	public EntityTypeEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public EntityTypeEffectParameter(String name, String uniqueName,
			String description, boolean optionnal, EntityType defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
	}

	@Override
	public void setValueFromString(String str) {
		setValue(EntityType.valueOf(str.toUpperCase()));
	}

	@Override
	public boolean isValid(EntityType value) {
		if (value != null) return true;
		return false;
	}

	@Override
	protected String getString(EntityType value) {
		return value.name();
	}

	@Override
	protected EntityType getFromString(String string) {
		return EntityType.valueOf(string);
	}

}
