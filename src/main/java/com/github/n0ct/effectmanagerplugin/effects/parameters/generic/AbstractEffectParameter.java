/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.parameters.generic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.reflections.Reflections;



/**
 * @author Benjamin
 *
 */
public abstract class AbstractEffectParameter implements ConfigurationSerializable {

	private String name;

	private String description;
	
	private String uniqueName;
	
	private boolean optionnal;
	

	@Override
	public Map<String, Object> serialize() {
		Map<String,Object> map = new TreeMap<String,Object>();
		map.put("name",name);
		map.put("description",description);
		map.put("uniqueName", uniqueName);
		map.put("optionnal", optionnal);
		map.put("className", getClass().getName());
		return map;
	}

	public AbstractEffectParameter(Map<String, Object> map) {
		this.name = (String)map.get("name");
		this.description = (String)map.get("description");
		this.uniqueName = (String)map.get("uniqueName");
		this.optionnal = (Boolean)map.get("optionnal");
	}
	
	public AbstractEffectParameter(String name, String uniqueName, String description, boolean optionnal) {
		this.name = name;
		this.description= description;
		this.optionnal = optionnal;
		this.setUniqueName(uniqueName);
	}
	

	public final String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public boolean isOptionnal() {
		return this.optionnal;
	}

	public String getUniqueName() {
		return uniqueName;
	}
	
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}


	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOptionnal(boolean optionnal) {
		this.optionnal = optionnal;
	}
	
	public AbstractEffectParameter clone() throws CloneNotSupportedException {
		return (AbstractEffectParameter) super.clone();
		
	}
	public abstract void setValueFromString(String str);
	
	public String toString() {
		return this.getDefinition(0);
	}
	
	public abstract String getDefinition(int level);

	public static AbstractEffectParameter deserialize(Map<String, Object> map) {
		Map<String, Class<? extends AbstractEffectParameter>> effectParametersClasses = new TreeMap<String, Class<? extends AbstractEffectParameter>>();
		Reflections reflections = new Reflections("com.github.n0ct.effectmanagerplugin.effects.parameters");
		for (Class<? extends AbstractEffectParameter> effectParameterClass : reflections.getSubTypesOf(AbstractEffectParameter.class)) {
			if (!Modifier.isAbstract(effectParameterClass.getModifiers())) {
				effectParametersClasses.put(effectParameterClass.getName(),effectParameterClass);
			}
		}
		Class<? extends AbstractEffectParameter> effectParameterClass = null;
		String effectParameterClassName = (String) map.get("className");
		for(String curEffectParameterClassName : effectParametersClasses.keySet()) {
			if (curEffectParameterClassName.equals(effectParameterClassName)) {
				effectParameterClass = effectParametersClasses.get(curEffectParameterClassName);
			}
		}
		Constructor<? extends AbstractEffectParameter> constructor;
		AbstractEffectParameter effectParameter = null;
		try {
			constructor = effectParameterClass.getConstructor(Map.class);
			effectParameter = constructor.newInstance(map);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("[INTERNAL ERROR] An error ocurred durign the deserialization process.",e);
		}
		return effectParameter;
	}
	
	public static AbstractEffectParameter valueOf(Map<String,Object> map) {
		return deserialize(map);
	}

}
