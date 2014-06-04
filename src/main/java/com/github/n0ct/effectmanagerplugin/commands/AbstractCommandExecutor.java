package com.github.n0ct.effectmanagerplugin.commands;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;

public class AbstractCommandExecutor {
	
	protected EffectManagerPlugin plugin;

	public AbstractCommandExecutor(EffectManagerPlugin plugin) {
		this.plugin = plugin;
	}

}
