package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;

public class DoubleEffectParameter extends AbstractPrimitiveEffectParameter<Double> {

	protected Double max_value;
	
	protected Double min_value;
	
	public static DoubleEffectParameter valueOf(Map<String,Object> map) {
		return new DoubleEffectParameter(map);
	}
	
	public static DoubleEffectParameter deserialize(Map<String,Object> map) {
		return new DoubleEffectParameter(map);
	}
	
	public DoubleEffectParameter(Map<String,Object> map) {
		super(map);
		if (map.get("maxValue") == null) {
			throw new IllegalArgumentException("maxValue is undefined.");
		}
		if (map.get("minValue") == null) {
			throw new IllegalArgumentException("minValue is undefined.");
		}
		this.max_value = Double.parseDouble((String) map.get("maxValue"));
		this.min_value = Double.parseDouble((String) map.get("minValue"));
	}
	
	@Override
	public Map<String,Object> serialize() {
		Map<String,Object> map = super.serialize();
		map.put("maxValue", this.max_value.toString());
		map.put("minValue", this.min_value.toString());
		return map;
	}
	
	public DoubleEffectParameter(String name, String uniqueName,
			String description, boolean optionnal, Double defaultValue, Double minValue, Double maxValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
		this.max_value = maxValue;
		this.min_value = minValue;
	}

	@Override
	public void setValueFromString(String str) {
		try{
			setValue(Double.parseDouble(str));
		} catch(NumberFormatException e) {
			setValue(null);
		}
	}

	@Override
	public boolean isValid(Double value) {
		if (value == null) {
			return false;
		}
		if (((min_value != null) && (value < min_value)) || ((max_value != null) && (value > max_value)))
			throw new IllegalArgumentException(helpMessage(value));
		return true;
	}

	protected String helpMessage(Double value) {
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
	protected String getString(Double value) {
		return value.toString();
	}

	@Override
	protected Double getFromString(String string) {
		return Double.parseDouble(string);
	}

}
