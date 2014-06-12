package com.github.n0ct.effectmanagerplugin.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;

public class AbstractCommandExecutor {
	
	protected EffectManagerPlugin plugin;

	public AbstractCommandExecutor(EffectManagerPlugin plugin) {
		this.plugin = plugin;
	}

	public UUID getPlayerUUID(String playerName,boolean throwIfNotFound) {
		for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (player.getName().contains(playerName)) {
				return player.getUniqueId();
			}
		}
		if (throwIfNotFound) {
			throw new IllegalArgumentException("The player " + playerName + " doesn't exist.");
		} else {
			return null;
		}
	}
	
	public UUID getPlayerUUID(String playerName) {
		return getPlayerUUID(playerName, true);
	}
	

	public String getPlayerName(UUID playerUUID) {
		for(OfflinePlayer player : Bukkit.getOfflinePlayers()) {
			if (playerUUID.equals(player.getUniqueId())) {
				return player.getName();
			}
		}
		throw new IllegalArgumentException("There is no player with the UniqueID \'" + playerUUID + "\'.");
	}
}
