package com.github.n0ct.effectmanagerplugin;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.n0ct.effectmanagerplugin.commands.EMCommandExecutor;
import com.github.n0ct.effectmanagerplugin.config.ConfigManager;
import com.github.n0ct.effectmanagerplugin.effects.EffectManager;
import com.github.n0ct.effectmanagerplugin.effects.PlayerEffectManager;


//TODO Credits to WorldEdit (Vector management in growing and explosion effects) and CustomProjectiles. Check their licences and if needed modify the corresponding code. 
public class EffectManagerPlugin extends JavaPlugin{

	private EffectManager effectManager;
	
	private PlayerEffectManager playerEffectManager;

	private EMCommandExecutor commandExecutor;
	
	private ConfigManager configManager;
	
	public ConfigManager getConfigManager() {
		return configManager;
	}

	public EffectManager getEffectManager() {
		return this.effectManager;
	}
	
	/** Effectue des actions au démarrage du plugin, c'est à dire:
	 * - Au demarrage du serveur
	 * - Apres un /reload
	 */
	@Override
    public void onEnable(){
		this.saveDefaultConfig();
		this.effectManager = new EffectManager(this);
		this.playerEffectManager = PlayerEffectManager.getFirstInstance();
		this.commandExecutor = new EMCommandExecutor(this); 
		this.configManager = new ConfigManager(this);
    	this.configManager.loadConfig();
	}
	
	/** Effectue des actions à la désactivation du plugin. c'est à dire:
	 * - A l'extinction du serveur
	 * - Pendant un /reload
	 */
    @Override
    public void onDisable(){
    	this.configManager.saveConfig();
    }

	/**
	 * @return the playerEffectManager
	 */
	public PlayerEffectManager getPlayerEffectManager() {
		return playerEffectManager;
	}

	/**
	 * @return the commandExecutor
	 */
	public EMCommandExecutor getCommandExecutor() {
		return commandExecutor;
	}

	public void setPlayerEffectManager(PlayerEffectManager deserializedPlayerEffectManager) {
		this.playerEffectManager = deserializedPlayerEffectManager;
	}
	
	public void setEffectManager(EffectManager deserializedEffectManager) {
		this.effectManager = deserializedEffectManager;
	}
}
