/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.parameters.generic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.configuration.MemorySection;

/**
 * @author Benjamin
 *
 */
public class EffectParameters extends AbstractEffectParameter implements Cloneable {
	
	public final static String ITERATION_PARAMETER = "_";
	
	ArrayList<AbstractEffectParameter> subParameters;
	
	private String splitSeparator;
	
	private int maxNumberOfSubParams;

	@Override
	public Map<String, Object> serialize() {
		return serialize(this);
	}
	
	@SuppressWarnings("unchecked")
	public EffectParameters(Map<String,Object> map) {
		super(map);
		this.splitSeparator = (String) map.get("splitSeparator");
		this.maxNumberOfSubParams = (int) map.get("maxNumberOfSubParams");
		this.subParameters = new ArrayList<AbstractEffectParameter>();
		Object obj = map.get("parameters");
		Map<String,Object> parameters;
		if (obj instanceof Map) {
			parameters = (Map<String, Object>) map.get("parameters");
		} else {
			parameters = ((MemorySection) map.get("parameters")).getValues(true);
		}
		for (String key : parameters.keySet()) {
			Object obj2 = parameters.get(key);
			if (obj2 instanceof Map) {
				this.subParameters.add(AbstractEffectParameter.deserialize((Map<String, Object>) parameters.get(key)));
			} else if (parameters.get(key) instanceof MemorySection && !(key.contains("."))) {
				this.subParameters.add(AbstractEffectParameter.deserialize(((MemorySection) parameters.get(key)).getValues(true)));
			}
		}
	}
	
	public Map<String,Object> serialize(AbstractEffectParameter AbstractEffectParameters) {
		Map<String,Object> map = super.serialize();
		map.put("splitSeparator", splitSeparator);
		map.put("maxNumberOfSubParams", maxNumberOfSubParams);
		map.put("className", getClass().getName());
		Map<String,Object> parametersMap = new TreeMap<String,Object>();
		for (AbstractEffectParameter subParam : this.subParameters) {
			parametersMap.put(subParam.getUniqueName(), subParam.serialize());
		}
		map.put("parameters",parametersMap);
		return map;
	}
	
	public static EffectParameters deserialize(Map<String,Object> map) {
		return new EffectParameters(map);
	}
	
	public static EffectParameters valueOf(Map<String,Object> map) {
		return new EffectParameters(map);
	}
	
	public EffectParameters(String name, String uniqueName, String description, boolean optionnal, String splitSeparator, int maxNumberOfSubParams) {
		super(name,uniqueName,description,optionnal);
		this.splitSeparator = splitSeparator;
		this.maxNumberOfSubParams = maxNumberOfSubParams;
		this.subParameters = new ArrayList<AbstractEffectParameter>();
	}
	
	public EffectParameters(String uniqueName, String name, String description, boolean optionnal) {
		this(uniqueName,name,description,optionnal," ",0);
	}
	
	public final ArrayList<AbstractEffectParameter> getSubParameters() {
		return subParameters;
	}

	public AbstractEffectParameter getParameterWithUniqueName(String uniqueName, boolean depthSearch) {
		for (AbstractEffectParameter subParam : this.subParameters) {
			if (subParam.getUniqueName().equals(uniqueName)) {
				return subParam;
			}
			if (depthSearch && subParam instanceof EffectParameters) {
				AbstractEffectParameter depthSearchSubParam = ((EffectParameters) subParam).getParameterWithUniqueName(uniqueName, true);
				if (depthSearchSubParam != null) {
					return depthSearchSubParam;
				}
			}
		}
		return null;
	}

	public boolean containsParameterWithUniqueName(String uniqueName, boolean depthSearch) {
		for (AbstractEffectParameter subParam : this.subParameters) {
			if (subParam.getUniqueName().equals(uniqueName)) {
				return true;
			}
			if (depthSearch && subParam instanceof EffectParameters) {
				boolean depthSearchContains = ((EffectParameters) subParam).containsParameterWithUniqueName(uniqueName, true);
				if (depthSearchContains) {
					return true;
				}
			}
			
		}
		return false;
	}


	public void addSubEffectParameter(AbstractEffectParameter abstractEffectParameter) {
		if (this.subParameters.size() == this.maxNumberOfSubParams && this.maxNumberOfSubParams > 0) {
			throw new IllegalArgumentException("The effect parameter " + this.getName() + " cannot contain more than " + this.maxNumberOfSubParams + " subParameters.");
		}
		if (this.containsParameterWithUniqueName(abstractEffectParameter.getUniqueName(),true)) {
			throw new IllegalArgumentException("[INTERNAL ERROR] The uniqueName must be unique in the effect parameters");
		}
		this.subParameters.add(abstractEffectParameter);
	}

	@Override
	public void setValueFromString(String str) {
		if (subParameters.size() == 0) {
			throw new IllegalArgumentException("[INTERNAL ERROR] Effect definition is invalid: an EffectParameters object doesn't contain any subParameters");
		}
		String[] strArray = str.split(this.splitSeparator);
		Iterator<AbstractEffectParameter> it = subParameters.iterator();
		boolean foundFirstOptionnal = false;
		AbstractEffectParameter subParam = null;
		for (int i = 0; i< strArray.length || i < subParameters.size(); i++) {
			if (subParameters.size() > i) {
				subParam = it.next();
			} else {
				if (!foundFirstOptionnal || (this.getMaxNumberOfSubParams() > 0 && this.getMaxNumberOfSubParams() <= i)) { 
					throw new IllegalArgumentException("You can't add more than " +this.getMaxNumberOfSubParams() + " " + this.getName() + " parameters.");
				}
				try {
					subParam = subParam.clone();
					String[] nameAndIterationArray = subParam.getUniqueName().split("\\" + ITERATION_PARAMETER);
					if (nameAndIterationArray.length != 2) {
						throw new IllegalArgumentException("[INTERNAL ERROR] Effect definition is invalid: The unique name of a cloneable parameter must contain "+ITERATION_PARAMETER+"<Number>");
					}
					try {
						subParam.setUniqueName(nameAndIterationArray[0]+ITERATION_PARAMETER+(Integer.valueOf(nameAndIterationArray[1])+1));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("[INTERNAL ERROR] Effect definition is invalid: The unique name of a cloneable parameter must contain "+ITERATION_PARAMETER+"<Number>",e);
					}
					subParameters.add(subParam);
				} catch (CloneNotSupportedException e) {
					//Should not happen
					e.printStackTrace();
				}
			}
			if (!foundFirstOptionnal && subParam.isOptionnal()) {
				foundFirstOptionnal = true;
			}
			if (foundFirstOptionnal && !subParam.isOptionnal()) {
				throw new IllegalArgumentException("[INTERNAL ERROR] Effect definition is invalid: The optionnal parameters must all be at the end of the effect default parameters");
			}
			if (i < strArray.length && (i >= this.maxNumberOfSubParams && this.maxNumberOfSubParams > 0)) {
				throw new IllegalArgumentException("The number of arguments of " + this.getName() + " exceeds the maximum number of arguments allowed");
			}
			if (i >= strArray.length) {
				if (foundFirstOptionnal) {
					if (subParameters.size() <= i+1 && subParam instanceof AbstractPrimitiveEffectParameter<?>) {
						AbstractPrimitiveEffectParameter<?> primitiveParameter = (AbstractPrimitiveEffectParameter<?>)subParam;
						primitiveParameter.setDefaultValueAsValue();
					}
					continue;	
				} else {
					throw new IllegalArgumentException("Missing argument:" + getName());
				}
			}
			subParam.setValueFromString(strArray[i]);
		}
	}

	/**
	 * @return the splitSeparator
	 */
	public String getSplitSeparator() {
		return splitSeparator;
	}

	/**
	 * @param splitSeparator the splitSeparator to set
	 */
	public void setSplitSeparator(String splitSeparator) {
		this.splitSeparator = splitSeparator;
	}

	/**
	 * @return the maxNumberOfSubParams
	 */
	public int getMaxNumberOfSubParams() {
		return maxNumberOfSubParams;
	}

	/**
	 * @param maxNumberOfSubParams the maxNumberOfSubParams to set
	 */
	public void setMaxNumberOfSubParams(int maxNumberOfSubParams) {
		this.maxNumberOfSubParams = maxNumberOfSubParams;
	}

	@Override
	public String getDefinition(int level) {
		StringBuilder prefix = new StringBuilder();
		StringBuilder ret = new StringBuilder();
		if (level > 0) {
			for (int i =0; i<level; i++) {
				prefix.append("  ");
			}
		}
		ret.append(prefix).append(getName()).append(": ").append(getDescription());
		for (AbstractEffectParameter subParam : this.getSubParameters()) {
			ret.append("\n").append(prefix.toString()).append(subParam.getDefinition(level + 1));
		}
		return ret.toString(); 
	}
}
