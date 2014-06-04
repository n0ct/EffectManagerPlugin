package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class BooleanEffectParameter extends AbstractPrimitiveEffectParameter<Boolean> {

	public static BooleanEffectParameter valueOf(Map<String,Object> map) {
		return new BooleanEffectParameter(map);
	}
	
	public static BooleanEffectParameter deserialize(Map<String,Object> map) {
		return new BooleanEffectParameter(map);
	}
	
	public BooleanEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public BooleanEffectParameter(String name, String uniqueName,
			String description, boolean optionnal, Boolean defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
	}

	@Override
	public void setValueFromString(String str) {
		setValue(Boolean.parseBoolean(str));
	}

	@Override
	public boolean isValid(Boolean value) {
		if (value == null) return false;
		return true;
	}

	@Override
	protected String getString(Boolean value) {
		return value.toString();
	}

	@Override
	protected Boolean getFromString(String string) {
		return Boolean.parseBoolean(string);
	}

}
