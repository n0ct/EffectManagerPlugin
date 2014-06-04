/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;


/**
 * @author Benjamin
 *
 */
public class StringEffectParameter extends AbstractPrimitiveEffectParameter<String> {

	
	public static StringEffectParameter valueOf(Map<String,Object> map) {
		return new StringEffectParameter(map);
	}
	
	public static StringEffectParameter deserialize(Map<String,Object> map) {
		return new StringEffectParameter(map);
	}
	
	public StringEffectParameter(Map<String,Object> map) {
		super(map);
	}
	
	public StringEffectParameter(String name, String uniqueName, String description, boolean optionnal, String defaultValue, String value) {
		super(name, uniqueName, description, optionnal, defaultValue, value);
	}
	
	@Override
	public void setValueFromString(String value) {
		setValue(value);
	}

	@Override
	public boolean isValid(String value) {
		return true;
	}

	@Override
	protected String getString(String value) {
		return value;
	}

	@Override
	protected String getFromString(String string) {
		return string;
	}


	

}
