package com.github.n0ct.effectmanagerplugin.effects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.n0ct.effectmanagerplugin.EffectManagerPlugin;
import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;


/**
 * 
 * @author Benjamin
 *
 *
 */
public class PlayerEffectManager implements ConfigurationSerializable {

	private Map<UUID,List<AbstractEffect>> playersEffects;

	private EffectManager effectManager;

	private EffectManagerPlugin plugin;
	
	private static PlayerEffectManager instance;
	
	public static PlayerEffectManager getInstance() {
		if (instance == null) {
			throw new IllegalAccessError("The PlayerEffectManager must first be initialise by the call of getFirstInstance method");
		}
		return instance;
	}
	
	public static PlayerEffectManager getFirstInstance() {
		if (instance != null) {
			throw new IllegalAccessError("The PlayerEffectManager has already be initialised by the call of getFirstInstance method");
		}
		instance = new PlayerEffectManager();
		return instance;
	}
	private PlayerEffectManager() {
		this.plugin = EffectManagerPlugin.getPlugin(EffectManagerPlugin.class);
		this.playersEffects = new TreeMap<UUID,List<AbstractEffect>>();
		this.effectManager = plugin.getEffectManager();
	}

	@Override
	public Map<String,Object> serialize() {
		Map<String,Object> map = new TreeMap<String,Object>();
		for (UUID playerUUID : this.playersEffects.keySet()) {
			StringBuilder sb = new StringBuilder();
			List<AbstractEffect> playerEffects = this.playersEffects.get(playerUUID);
			for (int i = 0; i<playerEffects.size(); i++) {
				AbstractEffect effect = playerEffects.get(i);
				sb.append(effect.getName());
				if (i+1<playerEffects.size()) {
					sb.append(";");
				}
			}
			if (playerEffects.size() > 0) {
				map.put(playerUUID.toString(),sb.toString());
			}
		}
		return map;
	}
	
	public PlayerEffectManager(Map<String, Object> map) {
		this();
		PlayerEffectManager.instance = this;
		for (String playerUUIDStr : map.keySet()) {
			String[] playerEffects = ((String)map.get(playerUUIDStr)).split(";");
			for (int i = 0;i< playerEffects.length;i++) {
				this.add(UUID.fromString(playerUUIDStr), playerEffects[i],false);
			}
		}
	}

	/**
	 * @return the playersEffects
	 */
	public Map<UUID, List<AbstractEffect>> getPlayersEffects() {
		return playersEffects;
	}
	
	public List<AbstractEffect> getEffectsForPlayer(UUID playerUUID) {
		getPlayer(playerUUID,false);
		if (playersEffects.containsKey(playerUUID)) {
			List<AbstractEffect> effects = playersEffects.get(playerUUID);
			if (effects.size() == 0) {
				return new ArrayList<AbstractEffect>();
			}
			return effects;
		}
		return new ArrayList<AbstractEffect>();
	}
	
	public void add(UUID playerUUID, String effectName) {
		add(playerUUID,effectName,true);
	}
	
	public void add(UUID playerUUID, String effectName, Boolean checkIfPlayerIsConnected) throws IllegalArgumentException {
		OfflinePlayer player = getPlayer(playerUUID, true);
		checkEffect(effectName);
		//verifie que ce type d'effet n'est pas deja attribue au joueur.
		AbstractEffect effect = (AbstractEffect) this.effectManager.get(effectName);
		checkEffectTypeDuplication(player.getPlayer(),effect,false);
		//recupere et si necessaire cree le(s) eventListener(s) appropries
		try {
			effect = effect.applyToPlayer(playerUUID);
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException("An error occured during the player's effect creation " + e.getClass().toString() +".");
		}
		for (Class<? extends AbstractEventListener<?>> eventListenerClass : effect.getNeededEvents()) {
			AbstractEventListener<?> eventListener = null;
			try {
				eventListener = EventListenerManager.getInstance().getEventListener(eventListenerClass);
				if (eventListener == null) {
					throw new IllegalArgumentException("An error occured during the EventListener creation.");
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("An error occured during the EventListener creation: " + e.getClass().toString() +".");
			}
			
			//associe le(s) eventListener(s) approprie(s) a l effet
			eventListener.addObserver(effect);
		}

		
		//On rempli la map des effets du joueur avec le nouvel effet.
		if (this.playersEffects.get(playerUUID) == null) {
			this.playersEffects.put(playerUUID,new ArrayList<AbstractEffect>());
		}
		this.playersEffects.get(playerUUID).add(effect);
		effect.onEnable();
	}
	
	public void del(final UUID playerUUID, String effectName) {
		OfflinePlayer player = getPlayer(playerUUID, false);
		AbstractEffect effect = getEffect(playerUUID, effectName);
		if (effect == null) {
			throw new IllegalArgumentException("The effect" + effectName + " were not associated to the player " + player.getName() + ".");
		}
		for (Class<? extends AbstractEventListener<?>> eventListenerClass : effect.getNeededEvents()) {
			AbstractEventListener<?> eventListener = null;
			try {
				eventListener = EventListenerManager.getInstance().getEventListener(eventListenerClass);
				if (eventListener == null) {
					throw new IllegalArgumentException("An error occured during the EventListener removal.");
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("An error occured during the EventListener removal: " + e.getClass().toString() +".");
			}
			eventListener.deleteObserver(effect);
			if (eventListener.countObservers() == 0) {
				try {
					EventListenerManager.getInstance().removeIfNeeded(eventListener);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("An error occured during the EventListener removal: " + e.getClass().toString() +".");
				}
			}
			
		}
		effect.onDisable();
		final AbstractEffect effectToDelete = effect;
		int disableDelay = effect.getDisableDelay();
		if (disableDelay > 0) {
			runTaskLater(new Runnable() {
				
				@Override
				public void run() {
					EffectManagerPlugin.getPlugin(EffectManagerPlugin.class).getPlayerEffectManager().getEffectsForPlayer(playerUUID).remove(effectToDelete);
				}
			}, effect.getDisableDelay());
		} else {
			this.playersEffects.get(playerUUID).remove(effect);
		}
	}
	


	public void clear(UUID playerUUID) {
		getPlayer(playerUUID,false);
		List<AbstractEffect> effects = getEffectsForPlayer(playerUUID);
		for (int i = effects.size() - 1 ; i >= 0 ; i--) {
			this.del(playerUUID,effects.get(i));
		}
	}
	
	public void callEffectForPlayer(UUID playerUUID, String effectName) {
		OfflinePlayer player = getPlayer(playerUUID,true);
		checkEffect(effectName);
		AbstractEffect effect = getEffect(playerUUID, effectName);
		if (effect == null) {
			throw new IllegalArgumentException("The effect " + effectName + " is not associated to the player " + player.getName() + " so it cannot be executed.");
		}
		effect.call();
	}
	
	public void callEffectsForPlayer(UUID playerUUID, List<AbstractEffect> effects) {
		getPlayer(playerUUID, true);
		if (effects == null || effects.size() == 0) {
			throw new IllegalArgumentException("No effect to call");
		}
		for (AbstractEffect effect : effects) {
			effect.call();
		}
	}
	
	private void del(UUID playerUUID, AbstractEffect effect) {
		try {
			for (Class<? extends AbstractEventListener<?>> eventListenerClass : effect.getNeededEvents()) {
				AbstractEventListener<?> eventListener;
	
				eventListener = EventListenerManager.getInstance().getEventListener(eventListenerClass);
				EntityDamageEvent.getHandlerList();
				eventListener.deleteObserver(effect);
				if (eventListener.countObservers() == 0) {
					Class<?> eventClass = eventListener.getObservedEvent();
					Method method = eventClass.getMethod("getHandlerList");
					HandlerList hl = (HandlerList) method.invoke(null);
					hl.unregister(plugin);
				}
			}
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("An error occured during the effect deletion: " + e.getClass().toString() +".",e);
		}
		getEffectsForPlayer(playerUUID).remove(effect);;
	}
	
	private void checkEffectTypeDuplication(Player player, AbstractEffect effect, boolean checkIfPlayerExists) {
		if (!playersEffects.containsKey(player.getUniqueId())) {
			return;
		}
		List<AbstractEffect> effects = playersEffects.get(player.getUniqueId());
		if (effects == null) {
			return;
		}
		for (AbstractEffect cEffect : effects) {
			if((cEffect.getType().equals(effect.getType()) && !effect.getType().isStackable())) {
				throw new IllegalArgumentException("The effect "+cEffect.getName()+" of the same type ("+effect.getType().getName()+") is already associated to the player "+player.getName()+".");
			}
		}
	}
	
	private void checkEffect(String effectName) {
		if(!effectManager.contains(effectName))	{
			throw new IllegalArgumentException("effect " + effectName + " doesn't exist");
		}
	}
	
	public AbstractEffect getEffect(UUID playerUUID, String effectName) {
		if (!this.playersEffects.containsKey(playerUUID)) {
			return null;
		}
		List <AbstractEffect> effects = this.playersEffects.get(playerUUID);
		for (AbstractEffect effect : effects) {
			if (effect.getName().equals(effectName)) {
				return effect;
			}
		}
		return null;
	}
	
	public OfflinePlayer getPlayer(UUID playerUUID, boolean checkIfPlayerIsOnline) {
		OfflinePlayer offlinePlayer = getOfflinePlayer(playerUUID);
		StringBuilder sb = new StringBuilder("The player ");
		if (offlinePlayer == null) {
			sb.append("with the UniqueID: \'").append(playerUUID.toString()).append("\' doesn't exist.");
			throw new IllegalArgumentException(sb.toString());
		}
		if (checkIfPlayerIsOnline && !offlinePlayer.isOnline()) {
			sb.append(offlinePlayer.getName()).append(" is not connected.");
			throw new IllegalArgumentException(sb.toString());
		}
		return offlinePlayer;
	}
	
	public boolean isPlayerOffline(UUID playerUUID) {
		return getOfflinePlayer(playerUUID) == null ? false : true;
	}

	public boolean isPlayerOnline(UUID playerUUID) {
		return getOnlinePlayer(playerUUID) == null ? false : true;
	}
	
    public Player getOnlinePlayer(UUID playerUUID) {
	    for(Player p : Bukkit.getOnlinePlayers()) {
		    if(p.getUniqueId().equals(playerUUID))
		    	return p;
		    }
	    return null;
    }
    
    public OfflinePlayer getOfflinePlayer(UUID playerUUID) {
    	for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
    		if (p.getUniqueId().equals(playerUUID)) {
    			return p;
    		}
    	}
    	return null;
    }

	public BukkitScheduler getScheduler() {
		return this.plugin.getServer().getScheduler();
	}

	public void runTaskLater(Runnable runnable, int delay) {
		getScheduler().runTaskLater(this.plugin,runnable,delay);
		
	}
	
	public static PlayerEffectManager deserialize(Map<String,Object> map) {
		return new PlayerEffectManager(map);
	}
	
	public static PlayerEffectManager valueOf(Map<String,Object> map) {
		return new PlayerEffectManager(map);
		
	}

	public void clear() {
		Set<UUID> players = this.playersEffects.keySet();
		for (UUID player : players) {
			this.clear(player);
		}
	}

}
