package com.github.n0ct.effectmanagerplugin.effects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Bukkit;
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

	private Map<String,List<AbstractEffect>> playersEffects;

	private EventListenerManager eventListenerManager;
	
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
		this.playersEffects = new TreeMap<String,List<AbstractEffect>>();
		this.effectManager = plugin.getEffectManager();
		this.eventListenerManager = new EventListenerManager();
	}

	@Override
	public Map<String,Object> serialize() {
		Map<String,Object> map = new TreeMap<String,Object>();
		for (String playerName : this.playersEffects.keySet()) {
			StringBuilder sb = new StringBuilder();
			List<AbstractEffect> playerEffects = this.playersEffects.get(playerName);
			for (int i = 0; i<playerEffects.size(); i++) {
				AbstractEffect effect = playerEffects.get(i);
				sb.append(effect.getName());
				if (i+1<playerEffects.size()) {
					sb.append(";");
				}
			}
			if (playerEffects.size() > 0) {
				map.put(playerName,sb.toString());
			}
		}
		return map;
	}
	
	public PlayerEffectManager(Map<String, Object> map) {
		this();
		PlayerEffectManager.instance = this;
		for (String playerName : map.keySet()) {
			String[] playerEffects = ((String)map.get(playerName)).split(";");
			for (int i = 0;i< playerEffects.length;i++) {
				this.add(playerName, playerEffects[i],false);
			}
		}
	}

	/**
	 * @return the playersEffects
	 */
	public Map<String,List<AbstractEffect>> getPlayersEffects() {
		return playersEffects;
	}
	
	public List<AbstractEffect> getEffectsForPlayer(String playerName) {
		checkPlayer(playerName);
		if (playersEffects.containsKey(playerName)) {
			List<AbstractEffect> effects = playersEffects.get(playerName);
			if (effects.size() == 0) {
				return new ArrayList<AbstractEffect>();
			}
			return effects;
		}
		return new ArrayList<AbstractEffect>();
	}
	
	public void add(String playerName, String effectName) {
		add(playerName,effectName,true);
	}
	
	public void add(String playerName, String effectName, Boolean checkIfPlayerIsConnected) throws IllegalArgumentException {
		if (checkIfPlayerIsConnected) {
			checkPlayer(playerName);
		}
		checkEffect(effectName);
		//verifie que ce type d'effet n'est pas deja attribue au joueur.
		AbstractEffect effect = (AbstractEffect) this.effectManager.get(effectName);
		checkEffectTypeDuplication(playerName,effect,false);
		//recupere et si necessaire cree le(s) eventListener(s) appropries
		try {
			effect = effect.applyToPlayer(playerName);
		} catch (CloneNotSupportedException e) {
			throw new IllegalArgumentException("An error occured during the player's effect creation " + e.getClass().toString() +".");
		}
		for (Class<? extends AbstractEventListener<?>> eventListenerClass : effect.getNeededEvents()) {
			AbstractEventListener<?> eventListener = null;
			try {
				eventListener = this.eventListenerManager.getEventListener(eventListenerClass);
				if (eventListener == null) {
					throw new IllegalArgumentException("An error occured during the EventListener creation.");
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("An error occured during the EventListener creation: " + e.getClass().toString() +".");
			}
			
			//associe le(s) eventListener(s) appropriees a l effet
			eventListener.addObserver(effect);
		}

		
		//On rempli la map des effets du joueur avec le nouvel effet.
		if (this.playersEffects.get(playerName) == null) {
			this.playersEffects.put(playerName,new ArrayList<AbstractEffect>());
		}
		this.playersEffects.get(playerName).add(effect);
	}
	
	public void registerEvents(AbstractEventListener<?> eventListener) {
		this.plugin.getServer().getPluginManager().registerEvents(eventListener,this.plugin);
	}
	
	public void del(String playerName, String effectName) {
		checkPlayer(playerName);
		AbstractEffect effect = getEffect(playerName, effectName);
		if (effect == null) {
			throw new IllegalArgumentException("The effect" + effectName + " were not associated to the player " + playerName + ".");
		}
		for (Class<? extends AbstractEventListener<?>> eventListenerClass : effect.getNeededEvents()) {
			AbstractEventListener<?> eventListener = null;
			try {
				eventListener = this.eventListenerManager.getEventListener(eventListenerClass);
				if (eventListener == null) {
					throw new IllegalArgumentException("An error occured during the EventListener removal.");
				}
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
				e.printStackTrace();
				throw new IllegalArgumentException("An error occured during the EventListener removal: " + e.getClass().toString() +".");
			}
			eventListener.deleteObserver(effect);
		}
		this.playersEffects.get(playerName).remove(effect);
	}
	


	public void clear(String playerName) {
		checkPlayer(playerName);
		List<AbstractEffect> effects = getEffectsForPlayer(playerName);
		for (int i = effects.size() - 1 ; i >= 0 ; i--) {
			this.del(playerName,effects.get(i));
		}
	}
	
	public void callEffectForPlayer(String playerName, String effectName) {
		checkPlayer(playerName);
		checkEffect(effectName);
		AbstractEffect effect = getEffect(playerName, effectName);
		if (effect == null) {
			throw new IllegalArgumentException("The effect " + effectName + " is not associated to the player " + playerName + " so it cannot be executed.");
		}
		effect.call();
	}
	
	public void callEffectsForPlayer(String playerName, List<AbstractEffect> effects) {
		checkPlayer(playerName);
		if (effects == null || effects.size() == 0) {
			throw new IllegalArgumentException("No effect to call");
		}
		for (AbstractEffect effect : effects) {
			effect.call();
		}
	}
	
	private void del(String playerName, AbstractEffect effect) {
		try {
			for (Class<? extends AbstractEventListener<?>> eventListenerClass : effect.getNeededEvents()) {
				AbstractEventListener<?> eventListener;
	
					eventListener = this.eventListenerManager.getEventListener(eventListenerClass);
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
		getEffectsForPlayer(playerName).remove(effect);;
	}
	
	private void checkEffectTypeDuplication(String playerName, AbstractEffect effect, boolean checkIfPlayerExists) {
		if (!playersEffects.containsKey(playerName)) {
			return;
		}
		List<AbstractEffect> effects = playersEffects.get(playerName);
		if (effects == null) {
			return;
		}
		for (AbstractEffect cEffect : effects) {
			if((cEffect.getType().equals(effect.getType()) && !effect.getType().isStackable())) {
				throw new IllegalArgumentException("The effect "+cEffect.getName()+" of the same type ("+effect.getType().getName()+") is already associated to the player "+playerName+".");
			}
		}
	}
	
	private void checkEffect(String effectName) {
		if(!effectManager.contains(effectName))	{
			throw new IllegalArgumentException("effect " + effectName + " doesn't exist");
		}
	}
	
	private AbstractEffect getEffect(String playerName, String effectName) {
		if (!this.playersEffects.containsKey(playerName)) {
			return null;
		}
		List <AbstractEffect> effects = this.playersEffects.get(playerName);
		for (AbstractEffect effect : effects) {
			if (effect.getName().equals(effectName)) {
				return effect;
			}
		}
		return null;
	}
	
	private void checkPlayer(String playerName) {
		if (!playerExists(playerName)) {
			throw new IllegalArgumentException("player " + playerName + " doesn't exist");
		}
	}
	


	private boolean playerExists(String name) {
		return getPlayer(name) == null ? false : true;
	}
	
    public Player getPlayer(String name) {
	    for(Player p : Bukkit.getOnlinePlayers()) {
		    if(p.getName().equalsIgnoreCase(name))
		    	return p;
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
		Set<String> players = this.playersEffects.keySet();
		for (String player : players) {
			this.clear(player);
		}
	}

}
