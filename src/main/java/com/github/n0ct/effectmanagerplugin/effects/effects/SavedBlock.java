package com.github.n0ct.effectmanagerplugin.effects.effects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class SavedBlock {

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
	public SavedBlock(Block block) {
		this.data = block.getData();
		this.location = block.getLocation();
		this.type = block.getType();
	}
	
	@SuppressWarnings("deprecation")
	public void reinitBlock() {
		location.getBlock().setTypeIdAndData(type.getId(), data, false);
	}
	
}
