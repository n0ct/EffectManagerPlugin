/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;

/**
 * @author Benjamin
 *
 */
public class EffectFactory {

	private EffectManager effectManager;
	
	public EffectFactory(EffectManager effectManager) {
		this.effectManager = effectManager;
	}

	public AbstractEffect createEffect(String name, String effectClassName, String effectCreationParameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
		AbstractEffect effect = getEffectInstance(effectClassName,name);
		effect.setEffectParameterFromString(effectCreationParameters);
		return effect;
	}
	
	public boolean isEffectClassTakingParameters(String effectClassName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		AbstractEffect effect = getEffectInstance(effectClassName,"test");
		if (effect.getDefaultParameters().getMaxNumberOfSubParams() == 0) {
			return false;
		}
		return true;
	}
	
	
	private AbstractEffect getEffectInstance(String effectClassName,String effectName) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (!this.effectManager.effectClassExists(effectClassName)) {
			throw new IllegalArgumentException("there is no effect available for the name " + effectClassName);
		}
		Class<?> effectClass = this.effectManager.getEffectClass(effectClassName);
		Constructor<?> constructor = effectClass.getConstructor(String.class);
		return (AbstractEffect) constructor.newInstance(effectName);
	}

	public AbstractEffect createDefaultEffect(String name) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		AbstractEffect effect = getEffectInstance(name,"default");
		effect.setParameters(effect.getDefaultParameters());
		return effect;
	}
	
}
