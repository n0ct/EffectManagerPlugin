package com.github.n0ct.effectmanagerplugin.effects.effects.path.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;

import com.github.n0ct.effectmanagerplugin.effects.effects.path.AbstractPathEffect;
import com.github.n0ct.effectmanagerplugin.effects.generic.EffectType;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.MaterialEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.AbstractEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;

public abstract class AbstractPathBlockEffect extends AbstractPathEffect {

	@SuppressWarnings("deprecation")
	protected static final List<Material> UNMODIFIED_BLOCK_TYPES = new ArrayList<Material>() {
		private static final long serialVersionUID = 6244529649492965768L;
	{
	    add(Material.WATER);
	    add(Material.STATIONARY_WATER);
	    add(Material.LAVA);
	    add(Material.STATIONARY_LAVA);
	    add(Material.AIR);
	    add(Material.ICE);
	    add(Material.PACKED_ICE);
	    add(Material.FLOWER_POT);
	    add(Material.FLOWER_POT_ITEM);
	    add(Material.HUGE_MUSHROOM_1);
	    add(Material.HUGE_MUSHROOM_2);
	    add(Material.LEAVES);
	    add(Material.LEAVES_2);
	    add(Material.VINE);
	    add(Material.SAPLING);
	    add(Material.getMaterial(31));
	    add(Material.getMaterial(38));
	    add(Material.getMaterial(39));
	    add(Material.getMaterial(40));
	    add(Material.PUMPKIN);
	    add(Material.PUMPKIN_PIE);
	    add(Material.PUMPKIN_STEM);
	    add(Material.SNOW);
	}};
	
	private static final String EFFECT_TYPE_NAME = "changeBlockOnPath";
	
	private static final String EFFECT_DESCRIPTION = "change blocks behind the player's steps.";

	@Override
	public final EffectType getType() {
			return new EffectType(EFFECT_TYPE_NAME,EFFECT_DESCRIPTION);
	}
	
	public AbstractPathBlockEffect(String name) {
		super(name);
	}
	
	public AbstractPathBlockEffect(Map<String, Object> map) {
		super(map);
	}

	protected final int getRadius() {
		return ((IntegerEffectParameter)getParameters().getParameterWithUniqueName("radius",false)).getValue();
	}
	
	protected final int getDelay() {
		return ((IntegerEffectParameter)getParameters().getParameterWithUniqueName("delay",false)).getValue();
	}
	
	protected final ArrayList<Material> getMaterials() {
		ArrayList<AbstractEffectParameter> blocks = ((EffectParameters) getParameters().getParameterWithUniqueName("blocks", false)).getSubParameters();
		ArrayList<Material> materials = new ArrayList<Material>();
		for (int i = 0;i<blocks.size();i++) {
			AbstractEffectParameter block = blocks.get(i);
			materials.add(((MaterialEffectParameter)block).getValue());
		};
		return materials;
	}

}
