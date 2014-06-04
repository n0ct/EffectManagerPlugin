package com.github.n0ct.effectmanagerplugin.old;
//package com.github.n0ct.naturesway;
//
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.bukkit.configuration.file.FileConfiguration;
//
//import com.github.n0ct.naturesway.oldeffects.NSWBlockPathEffect;
//import com.github.n0ct.naturesway.oldeffects.NSWBumpAttackerEffect;
//import com.github.n0ct.naturesway.oldeffects.NSWDamageDropEffect;
//import com.github.n0ct.naturesway.oldeffects.NSWEffect;
//import com.github.n0ct.naturesway.oldeffects.NSWEffectAttackerEffect;
//import com.github.n0ct.naturesway.oldeffects.NSWEffectType;
//import com.github.n0ct.naturesway.oldeffects.NSWEntityPathEffect;
//
//public class NSWConfigLoader {
//
//	private static NatureSWay plugin;
//	
//	public NSWConfigLoader(NatureSWay natureSWay) {
//		NSWConfigLoader.plugin = natureSWay;
//		NSWConfigLoader.plugin.saveDefaultConfig();
//		loadConfig();
//	}
//	
//	public void loadConfig() {
//		
//		FileConfiguration fileConfig = NSWConfigLoader.plugin.getConfig();
//		
//		try {
//			loadEffects(fileConfig);
//		
//			loadPlayersEffect(fileConfig);
//		} catch (Exception e) {
//			System.out.println("Error while loading Nature's Way plugin config:");
//			e.printStackTrace();
//		}
//	}
//
//	private void loadPlayersEffect(FileConfiguration fileConfig) {
//
//		ArrayList<Map<String,Object>> effectsList = (ArrayList<Map<String, Object>>)fileConfig.get("players");
//		ArrayList<NSWEffect> playerEffectsList;
//		String playerName;
//		for (Map<String, Object> map : effectsList) {
//			System.out.println(map);
//			for (Entry<String, Object> entry : map.entrySet()) {
//				playerName = entry.getKey();
//				playerEffectsList = new ArrayList<NSWEffect>();
//				for (String effectName : ((ArrayList<String>)entry.getValue())) {
//					for (NSWEffect effect : NSWConfigLoader.plugin.getEffects()) {
//						if (effectName.equals(effect.getName())) {
//							playerEffectsList.add(effect);
//						}
//					}
//				}
//				NSWConfigLoader.plugin.getPlayersEffects().put(playerName,playerEffectsList);
//			}
//		}
//	}
//
//	/**
//	 * @param fileConfig
//	 */
//	private void loadEffects(FileConfiguration fileConfig) {
//		ArrayList<? extends Map> effectsList = (ArrayList<? extends Map>) fileConfig.get("effects");
//		NSWEffect effect;
//		NSWEffectType effectType = null;
//		String effectName = null;
//		int effectAmplifier = 0;
//		String[] effectParameters = null;
//		
//		for(Map<String,Object> effectDescription : effectsList) {
//			for (Entry<String, Object> entry : effectDescription.entrySet()) {
//				if (entry.getValue()==null) {
//					effectName = (String)entry.getKey();
//					continue;
//				}
//				if (entry.getKey().equals("amplifier")) {
//					effectAmplifier = ((Integer)entry.getValue()).intValue();
//					continue;
//				}
//				if (entry.getKey().equals("parameters")) {
//					if (entry.getValue().getClass().isAssignableFrom(ArrayList.class)) {
//						ArrayList<String> parametersList = (ArrayList<String>) entry.getValue();
//						effectParameters = new String[parametersList.size()];
//						int i=0;
//						for (Object param : parametersList) {
//							if (param instanceof Integer) {
//								effectParameters[i]=param.toString();
//							}
//							i++;
//						}
//					} else {
//						effectParameters = new String[1];
//						effectParameters[0]= entry.getValue().toString();
//					}
//					
//					continue;
//				}
//				if (entry.getKey().equals("type")) {
//					for (NSWEffectType currentEffectType : NSWEffectType.values()) {
//						if (currentEffectType.getType().equals(entry.getValue())) {
//							effectType = currentEffectType;
//						}
//						
//					}
//					continue;
//				}
//			}
//			switch(effectType) {
//			case DAMAGE_DROP_EFFECT:
//				effect = new NSWDamageDropEffect(effectName, effectParameters, effectAmplifier);
//				break;
//			case PATH_BLOCK_EFFECT:
//				effect = new NSWBlockPathEffect(effectName, effectParameters, effectAmplifier);
//				break;
//			case BUMP_ATTACKER_EFFECT:
//				effect = new NSWBumpAttackerEffect(effectName, effectParameters, effectAmplifier);
//				break;
//			case EFFECT_ATTACKER_EFFECT:	
//				effect = new NSWEffectAttackerEffect(effectName, effectParameters, effectAmplifier);
//				break;
//			case PATH_ENTITY_EFFECT:	
//				effect = new NSWEntityPathEffect(effectName, effectParameters, effectAmplifier);
//				break;
//			default:
//				throw new IllegalArgumentException("That type of effect is not implemented yet");
//			}
//			NSWConfigLoader.plugin.getEffects().add(effect);
//		}
//	}
//
//}
