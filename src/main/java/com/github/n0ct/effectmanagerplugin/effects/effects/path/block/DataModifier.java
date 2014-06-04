package com.github.n0ct.effectmanagerplugin.effects.effects.path.block;

import org.bukkit.Location;
import org.bukkit.World;

public class DataModifier implements Runnable {

	
	private int dataValue;
	
	private World world;
	
	private Location blockLocation;
	
	public DataModifier(World world, Location blockLocation, int dataValue) {
		this.dataValue = dataValue;
		this.world = world;
		this.blockLocation = blockLocation;
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
		world.getBlockAt(blockLocation).setData((byte)dataValue);
	}

}
