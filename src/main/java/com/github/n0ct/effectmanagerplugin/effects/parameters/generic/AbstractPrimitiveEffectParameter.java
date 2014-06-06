package com.github.n0ct.effectmanagerplugin.effects.parameters.generic;

import java.util.Map;



public abstract class AbstractPrimitiveEffectParameter<T> extends AbstractEffectParameter implements Cloneable {

	private static final String INDENTATION = "  ";


	public AbstractPrimitiveEffectParameter(Map<String,Object> map){
		super(map);
		setValueFromString((String) map.get("value"));
		setDefaultValueFromString((String)map.get("defaultValue"));
	}

	protected void setDefaultValueFromString(String string) {
		T defaultValue = getFromString(string);
		setDefaultValue(defaultValue);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = super.serialize();
		map.put("value",getValueAsString());
		map.put("defaultValue", getDefaultValueAsString());
		return map;
	}
	
	private final String getValueAsString() {
		return getString(this.value);
	}
	

	private final String getDefaultValueAsString() {
		return getString(this.defaultValue);
	}
	
	protected abstract String getString(T value);
	
	protected abstract T getFromString(String string);

	public AbstractPrimitiveEffectParameter(String name, String uniqueName,	String description, boolean optionnal, T defaultValue, T value) {
		this(name,uniqueName,description,optionnal,defaultValue);
		setValue(value);
	}

	public AbstractPrimitiveEffectParameter(String name, String uniqueName,	String description, boolean optionnal, T defaultValue) {
		super(name, uniqueName, description, optionnal);
		setDefaultValue(defaultValue);
	}
	
	private T value;
	
	private T defaultValue;


	public abstract void setValueFromString(String str);
	
	public abstract boolean isValid(T value);

	public final void setValue(T value) {
		if (!isValid(value))
		{
			throw new IllegalArgumentException("Illegal argument for parameter "+getName()+".\nParameter description: "+ getDescription());
		}
		this.value = value;
	}
	
	public final T getValue() {
		return this.value;
	}

	public final T getDefaultValue() {
		return this.defaultValue;
	}
	
	public final void setDefaultValue(T value) {
		if (!isValid(value)) {
			throw new IllegalArgumentException("[INTERNAL ERROR] Effect definition is invalid: the parameter "+getName()+" contain the default value "+value+" witch is invalid.");
		}
		this.defaultValue = value;
	}

	@Override
	public String getDefinition(int level) {
		StringBuilder prefix = new StringBuilder();
		StringBuilder ret = new StringBuilder();
		
		for (int i =0; i<level; i++) {
			prefix.append(INDENTATION);
		}
		
		ret.append(prefix.toString()).append(getName()).append(": ").append(getDescription());
		if (getValue() != null) {
			ret.append("\n").append(prefix).append(INDENTATION).append("\\=>Value: ").append(getValue());
		}
		return ret.toString(); 
	}

	public void setDefaultValueAsValue() {
		setValue(getDefaultValue());
	}
}
