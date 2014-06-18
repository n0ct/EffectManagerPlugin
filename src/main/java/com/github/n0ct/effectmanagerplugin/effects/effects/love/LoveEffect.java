package com.github.n0ct.effectmanagerplugin.effects.effects.love;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.PlayerInteractListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public class LoveEffect extends AbstractEffect {

	public static LoveEffect valueOf(Map<String,Object> map) {
		return new LoveEffect(map);
	}
	
	public static LoveEffect deserialize(Map<String,Object> map) {
		return new LoveEffect(map);
	}
	
	public LoveEffect(Map<String,Object> map) {
		super(map);
	}
	
	public LoveEffect(String name) {
		super(name);
	}
	

	public void onCall() {
		Player p = getPlayer();
		p.getInventory().addItem(getLovePotion());
	}


	private static final String EFFECT_TYPE_NAME = "loveEffect";
	
	private static final String EFFECT_DESCRIPTION = "trigger hearth animation when a player drinks a particular potion.";

	private static final String LOVE_POTION_NAME = ChatColor.RED + "Love" + ChatColor.BLUE + " Potion";

	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 7187034955063406594L;
			{
				add(PlayerInteractListener.class);
			}
		};
	}

	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}


	public ItemStack getLovePotion() {
		ItemStack akwardPotion = new ItemStack(Material.POTION, 1, (byte) 16);
		ItemMeta meta = akwardPotion.getItemMeta();
        meta.setDisplayName(LOVE_POTION_NAME);
		akwardPotion.setItemMeta(meta);
		return akwardPotion;
	}
	
	@Override
	protected void on(Event event) {
		if (!(event instanceof PlayerInteractEvent)) {
			return;
		}
		final PlayerInteractEvent playerInteractEvent = (PlayerInteractEvent) event;
		Player player = playerInteractEvent.getPlayer();
		ItemStack item = player.getItemInHand();
		if (item == null) {
			return;
		}
		if (!player.getUniqueId().equals(getPlayerUUID())) {
			return;
		}
		if(!((playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) || (playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR))) {
			return;
		}
		if (!(item.equals(getLovePotion()))) {
			return;
		}
		//TODO éviter que l'on puisse voir le loup en jeu peut-être en écoutant le spawnCreatureEvent.
		Wolf w = player.getWorld().spawn(player.getLocation(), Wolf.class);
        w.playEffect(EntityEffect.WOLF_HEARTS);
        w.remove();
	}
	
	@Override
	protected String getHelp() {
		return "Puts hearth animation on a player drinking a Love Potion. Use call to get a new Love Potion.";
	}

	@Override
	public boolean isCallable() {
		return true;
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("No parameters","No parameters","No parameters.", true, " ", 0);
		return ret;
	}
	

	@Override
	protected String getDescription() {
		return "Puts hearth animation on a player drinking a Love Potion.\nUse call to get a new Love Potion."; 
	}


}
