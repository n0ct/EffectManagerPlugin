package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.projectile.ProjectileType;

public class ProjectileTypeEffectParameter extends AbstractPrimitiveEffectParameter<ProjectileType> {
	
	public static ProjectileTypeEffectParameter valueOf(Map<String,Object> map) {
		return new ProjectileTypeEffectParameter(map);
	}
	
	public static ProjectileTypeEffectParameter deserialize(Map<String,Object> map) {
		return new ProjectileTypeEffectParameter(map);
	}
	
	public ProjectileTypeEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public ProjectileTypeEffectParameter(String name, String uniqueName, String description, boolean optionnal, ProjectileType defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
	}

	@Override
	public void setValueFromString(String str) {
		setValue(getFromString(str));

	}

	@Override
	public boolean isValid(ProjectileType value) {
		if (value == null) return false;
		return true;
	}

	@Override
	protected String getString(ProjectileType value) {
		return value.getName();
	}

	@Override
	protected ProjectileType getFromString(String string) {
		return ProjectileType.matchType(string);
	}
}
