package com.github.n0ct.effectmanagerplugin.effects.parameters.projectile;


public enum ProjectileType {
	ITEM("item"),
	BLOCK("block"),
	ORB("orb"),
	TNT("tnt");

	private String name;

	private ProjectileType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public static ProjectileType matchType(String name) {
		ProjectileType type = null;
		try {
			type = ProjectileType.valueOf(name.toLowerCase());
		} catch (IllegalArgumentException e) {
			
		}
		ProjectileType[] types = ProjectileType.values(); 
		if (type == null) for (int i = 0; i<types.length; i++) {
			if (name.contains(types[i].getName())) {
				return types[i];
			}
		}
		return type;
	}
	
	public String toString() {
		return this.name;
	}

	public static String getListAsString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<ProjectileType.values().length; i++) {
			ProjectileType type = ProjectileType.values()[i];
			sb.append(type.getName());
			if (i != ProjectileType.values().length-1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
}
