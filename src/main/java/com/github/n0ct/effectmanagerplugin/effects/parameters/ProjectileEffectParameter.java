package com.github.n0ct.effectmanagerplugin.effects.parameters;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;

import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractPrimitiveEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.projectile.ProjectileType;

public class ProjectileEffectParameter extends AbstractPrimitiveEffectParameter<Map<String,String>> {

	public final static String TYPE_MAP_KEY = "type";
	public final static String MATERIAL_MAP_KEY = "material";
	public final static String POWER_MAP_KEY = "power";
	public final static String SPLIT_SEPARATOR = ",";
	
	private ProjectileTypeEffectParameter type;
	
	private MaterialEffectParameter material;
	
	private FloatEffectParameter power;
	
	
	public static ProjectileEffectParameter valueOf(Map<String,Object> map) {
		return new ProjectileEffectParameter(map);
	}
	
	public static ProjectileEffectParameter deserialize(Map<String,Object> map) {
		return new ProjectileEffectParameter(map);
	}
	
	public ProjectileEffectParameter(Map<String,Object> map) {
		super(map);
	}

	@Override
	protected void init() {
		type = new ProjectileTypeEffectParameter("Projectile Type","projectileType","The type of the projectile. can contain " + ProjectileType.getListAsString() + ".",true,ProjectileType.TNT);
		material = new MaterialEffectParameter("Material","Material","Material of the projectile if it's a block or an item projectile.",true,Material.STONE);
		power = new FloatEffectParameter("Power","Power","The power of the projectile",true,1f,0f,5f);
		this.getValue();
	}
	
	public ProjectileEffectParameter(String name, String uniqueName, String description, boolean optionnal, Map<String, String> defaultValue) {
		super(name, uniqueName, description, optionnal, defaultValue); 
	}

	@Override
	public boolean isValid(Map<String,String> value) {
		if (value != null) {
			if (value.get(POWER_MAP_KEY) == null) {
				return false;
			}
			Float pPower = this.power.getFromString(value.get(POWER_MAP_KEY));
			if (pPower == null) {
				return false;
			}
			ProjectileType pType = this.type.getFromString(value.get(TYPE_MAP_KEY));
			if (pType == null) {
				return false;
			}
			switch (pType) {
				case BLOCK:
					Material matBlock = this.material.getFromString((String)value.get(MATERIAL_MAP_KEY));
					if (matBlock == null) return false;
					break;
				case ITEM:
					Material matItem = this.material.getFromString((String)value.get(MATERIAL_MAP_KEY));
					if (matItem == null) return false;
					break;
				case ORB:
					break;
				case TNT:
					break;
				default:
					break;
			}
		}
		return true;
	}

	@Override
	public void setValueFromString(String str) {
		setValue(getFromString(str));
		this.power.setValueFromString(getValue().get(POWER_MAP_KEY));
		this.type.setValueFromString(getValue().get(TYPE_MAP_KEY));
		if(getValue().get(MATERIAL_MAP_KEY) != null) {
			this.material.setValueFromString(getValue().get(MATERIAL_MAP_KEY));
		}
	}

	@Override
	protected String getString(Map<String,String> value) {
		if (value.get(TYPE_MAP_KEY) == null) {
			throw new IllegalStateException(TYPE_MAP_KEY + " of the projectile should not be null.");
		}
		if (value.get(POWER_MAP_KEY) == null) {
			throw new IllegalStateException(POWER_MAP_KEY + " of the projectile should not be null.");
		}
		StringBuilder sb = new StringBuilder(value.get(TYPE_MAP_KEY));
		sb.append(SPLIT_SEPARATOR).append(value.get(POWER_MAP_KEY));
		if (value.get(MATERIAL_MAP_KEY) != null) {
			sb.append(SPLIT_SEPARATOR).append(value.get(MATERIAL_MAP_KEY));
		}
		return sb.toString();
	} 
	
	@Override
	protected Map<String,String> getFromString(String string) {
		Map<String,String> map = new TreeMap<String, String>();
		String[] splittedString = string.split(SPLIT_SEPARATOR);
		if (splittedString.length < 2) {
			throw new IllegalArgumentException("A projectile definition must at least contain \'<ProjectileType>|<ProjectilePower>\'");
		}
		map.put(TYPE_MAP_KEY, splittedString[0]);
		map.put(POWER_MAP_KEY, splittedString[1]);
		if (splittedString.length > 2) {
			map.put(MATERIAL_MAP_KEY,splittedString[2]);
		}
		return map;
	}
	
	public ProjectileType getType() {
		return this.type.getValue();
	}
	
	public Float getPower() {
		return this.power.getValue();
	}
	
	public Material getMaterial() {
		return this.material.getValue();
	}

}
