package com.github.n0ct.effectmanagerplugin.effects.effects.path.block;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class PathSavedBlock {

	final private Location location;
	final private byte data;
	final private Material type;
	
	public Location getLocation() {
		return location;
	}

	public byte getData() {
		return data;
	}

	public Material getType() {
		return type;
	}

	@SuppressWarnings("deprecation")
	public PathSavedBlock(Block block) {
		this.data = block.getData();
		this.location = block.getLocation();
		this.type = block.getType();
	}
	
}
