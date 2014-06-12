package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class FloatEffectParameter extends AbstractPrimitiveEffectParameter<Float> {

	protected Float max_value;
	
	protected Float min_value;
	
	public static FloatEffectParameter valueOf(Map<String,Object> map) {
		return new FloatEffectParameter(map);
	}
	
	public static FloatEffectParameter deserialize(Map<String,Object> map) {
		return new FloatEffectParameter(map);
	}
	
	public FloatEffectParameter(Map<String,Object> map) {
		super(map);
		if (map.get("maxValue") == null) {
			throw new IllegalArgumentException("maxValue is undefined.");
		}
		if (map.get("minValue") == null) {
			throw new IllegalArgumentException("minValue is undefined.");
		}
		this.max_value = Float.parseFloat((String) map.get("maxValue"));
		this.min_value = Float.parseFloat((String) map.get("minValue"));
	}
	
	@Override
	public Map<String,Object> serialize() {
		Map<String,Object> map = super.serialize();
		map.put("maxValue", this.max_value.toString());
		map.put("minValue", this.min_value.toString());
		return map;
	}
	
	public FloatEffectParameter(String name, String uniqueName,	String description, boolean optionnal, Float defaultValue, Float minValue, Float maxValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
		this.max_value = maxValue;
		this.min_value = minValue;
	}

	@Override
	public void setValueFromString(String str) {
		try{
			setValue(Float.parseFloat(str));
		} catch(NumberFormatException e) {
			setValue(null);
		}
	}

	@Override
	public boolean isValid(Float value) {
		if (value == null) {
			return false;
		}
		if (((min_value != null) && (value < min_value)) || ((max_value != null) && (value > max_value)))
			throw new IllegalArgumentException(helpMessage(value));
		return true;
	}

	protected String helpMessage(Float value) {
		StringBuilder sb = new StringBuilder(getName() + " has an invalid value ("+value+"). It must be a number");
		if (max_value != null && min_value != null) {
			sb.append(" between " + min_value + " and " + max_value);
		} else if (max_value != null) {
			sb.append(" lower than " + max_value);
		} else if (min_value != null) {
			sb.append(" greater than " + min_value);
		}
		sb.append(".");
		return sb.toString();
	}

	@Override
	protected String getString(Float value) {
		return value.toString();
	}

	@Override
	protected Float getFromString(String string) {
		return Float.parseFloat(string);
	}
}
