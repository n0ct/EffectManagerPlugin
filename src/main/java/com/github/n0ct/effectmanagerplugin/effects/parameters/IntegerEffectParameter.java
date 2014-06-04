package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;



/**
 * @author Benjamin
 *
 */
public class IntegerEffectParameter extends AbstractPrimitiveEffectParameter<Integer> {

	protected Integer max_value;

	protected Integer min_value;
	
	public Integer getMax() {
		return max_value;
	}

	public Integer getMin() {
		return min_value;
	}
	
	public static IntegerEffectParameter valueOf(Map<String,Object> map) {
		return new IntegerEffectParameter(map);
	}
	
	public static IntegerEffectParameter deserialize(Map<String,Object> map) {
		return new IntegerEffectParameter(map);
	}
	
	public IntegerEffectParameter(Map<String,Object> map) {
		super(map);
		this.max_value = Integer.parseInt((String) map.get("maxValue"));
		this.min_value = Integer.parseInt((String) map.get("minValue"));
	}

	@Override
	public Map<String,Object> serialize() {
		Map<String,Object> map = super.serialize();
		map.put("maxValue", this.max_value.toString());
		map.put("minValue", this.min_value.toString());
		return map;
	}
	
	public IntegerEffectParameter(String name, String uniqueName, String description, boolean optionnal, Integer defaultValue, int minValue, int maxValue) {
		super(name, uniqueName, description, optionnal, defaultValue);
		this.max_value = maxValue;
		this.min_value = minValue;
	}

	@Override
	public void setValueFromString(String value) {
		try {
			setValue(Integer.parseInt(value));
		} catch(NumberFormatException e) {
			throw new IllegalArgumentException(helpMessage(value));
		}
	}

	@Override
	public boolean isValid(Integer value) {
		if (((min_value != null) && (value < min_value)) || ((max_value != null) && (value > max_value))) return false;
		return true;
	}

	protected String helpMessage(String value) {
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
	protected String getString(Integer value) {
		return value.toString();
	}

	@Override
	protected Integer getFromString(String string) {
		return Integer.parseInt(string);
	}

}
