/**
 * 
 */
package com.github.n0ct.effectmanagerplugin.effects.effects.dmgdrop;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.github.n0ct.effectmanagerplugin.effects.generic.AbstractEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.listener.EntityDamageListener;
import com.github.n0ct.effectmanagerplugin.effects.listener.generic.AbstractEventListener;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.MaterialEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

/**
 * @author Benjamin
 *
 */
public class DropItemOnDamageEffect extends AbstractEffect {
	
	private static final String EFFECT_TYPE_NAME = "dmgdrop";
	
	private static final String EFFECT_DESCRIPTION = "Drops items when the player is attacked";
	
	public static DropItemOnDamageEffect valueOf(Map<String,Object> map) {
		return new DropItemOnDamageEffect(map);
	}
	
	public static DropItemOnDamageEffect deserialize(Map<String,Object> map) {
		return new DropItemOnDamageEffect(map);
	}
	
	public DropItemOnDamageEffect(Map<String,Object> map) {
		super(map);
	}
	
	public DropItemOnDamageEffect(String name) {
		super(name);
	}

	@Override
	public boolean isCallable() {
		return false;
	}
	
	@Override
	protected String getHelp() {
		return "Drops items when the player takes damages.";
	}
	
	@Override
	protected void on(Event event) {
		if (!(event instanceof EntityDamageEvent)) {
			return;
		}
		Entity entity = ((EntityDamageEvent)event).getEntity();
		if (!(entity instanceof Player)) {
			return;
		}
		Player player = (Player) entity;
		if (!player.getUniqueId().equals(getPlayerUUID())) {
			return;
		}
		EffectParameters effects = (EffectParameters) getParameters().getSubParameters().get(0);
		Random random = new Random(596274);
		int amplifier = 1;
		if (getParameters().getSubParameters().size() > 1) {
			amplifier = ((IntegerEffectParameter)getParameters().getSubParameters().get(1)).getValue();
		}
		ItemStack[] itemStacks = new ItemStack[effects.getSubParameters().size()];
		int i = 0;
    	for (AbstractEffectParameter effect : effects.getSubParameters()) {
    		itemStacks[i] = new ItemStack(((MaterialEffectParameter)effect).getValue(), 1);
    		i++;
    	}
    	
		for(int y=0;y<amplifier;y++) {
			int xRnd = random.nextInt(amplifier/2) - (amplifier/4);
			int zRnd = random.nextInt(amplifier/2) - (amplifier/4);
			int stackRnd = random.nextInt(effects.getSubParameters().size());
			Location dropLocation = new Location(player.getWorld(),player.getLocation().getX()+xRnd, player.getLocation().getY(),player.getLocation().getZ()+zRnd);
			player.getWorld().dropItemNaturally(dropLocation,itemStacks[stackRnd]);
		}
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("effect Parameters","params","Drop Item On Damage Parameters", false, " ", 2);
		EffectParameters paramBlocks = new EffectParameters("Items","item","Some items separated by ','.", false, ",", 0);
		MaterialEffectParameter block1 = new MaterialEffectParameter("Item", "item" + EffectParameters.ITERATION_PARAMETER + "0", "Item which will be dropped.", true, Material.WOOD);
		paramBlocks.addSubEffectParameter(block1);
		ret.addSubEffectParameter(paramBlocks);
		IntegerEffectParameter amplifier = new IntegerEffectParameter("Amplifier","Amplifier","Number of items dropped each time the player takes damages.", true, 1, 1, 200);
		ret.addSubEffectParameter(amplifier);
		return ret;
	}

	@Override
	protected String getDescription() {
		return "Randomly drops some items around the player when the player takes damages. The amplifier defines the number of block dropped when the player takes damages. The blocks are the blocks which can be dropped.";
	}

	@Override
	public ArrayList<Class<? extends AbstractEventListener<?>>> getNeededEvents() {
		return new ArrayList<Class<? extends AbstractEventListener<?>>>() {
			private static final long serialVersionUID = 6619922397961321409L;
			{
				add(EntityDamageListener.class);
			}
		};
	}

	@Override
	public EffectType getType() {
		return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}

	

}
