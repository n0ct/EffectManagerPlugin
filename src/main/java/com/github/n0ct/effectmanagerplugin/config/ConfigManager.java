package com.github.n0ct.effectmanagerplugin.config;

import java.util.Map;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.effects.EffectManager;
import com.github.n0ct.effectmanagerplugin.effects.PlayerEffectManager;

public class ConfigManager {

	private EffectManagerPlugin plugin;
	
	public ConfigManager(EffectManagerPlugin effectManagerPlugin) {
		this.plugin = effectManagerPlugin;
	}

	public void loadConfig() {
		FileConfiguration fileConfig = plugin.getConfig();
		plugin.getPlayerEffectManager().clear();
		plugin.getEffectManager().clear();
		
		loadEffects(fileConfig);
		loadPlayersEffects(fileConfig);
		
	}

	@SuppressWarnings("unchecked")
	private void loadEffects(FileConfiguration fileConfig) {
		Object obj = fileConfig.get("effects");
		if (obj instanceof MemorySection) {
			plugin.setEffectManager(EffectManager.deserialize(((MemorySection)obj).getValues(true)));
		} else {
			plugin.setEffectManager(EffectManager.deserialize((Map<String, Object>) obj));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadPlayersEffects(FileConfiguration fileConfig) {
		Object obj = fileConfig.get("playersEffects");
		if (obj instanceof MemorySection) {
			plugin.setPlayerEffectManager(PlayerEffectManager.deserialize(((MemorySection)obj).getValues(true)));
			
		} else {
			plugin.setPlayerEffectManager(PlayerEffectManager.deserialize((Map<String, Object>) obj));
		}
	}


	public void saveConfig() {
		
		/*Map<String,Object> map = new TreeMap<String,Object>();
		map.put("effects",saveEffects());
		map.put("playersEffects",savePlayersEffects());
		showConfig(map,0);*/

		plugin.getConfig().set("effects",saveEffects());
		plugin.getConfig().set("playersEffects",savePlayersEffects());
		plugin.saveConfig();
	}

	@SuppressWarnings("unchecked")
	public void showConfig(Map<String, Object> map, int iteration) {
		final String unitIndentation = "  ";
		StringBuilder indentation = new StringBuilder();
		for(int i=0;i<iteration;i++) {
			indentation.append(unitIndentation);
		}
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof Map) {
				System.out.println(indentation + key + ":");
				showConfig((Map<String, Object>) value,iteration+1);
			} else {
				System.out.println(indentation + key + ":" + value);
			}
		}
		
	}

	private Map<String,Object> savePlayersEffects() {
		return plugin.getPlayerEffectManager().serialize();
	}

	private Map<String, Object> saveEffects() {
		return plugin.getEffectManager().serialize();
	}

}
