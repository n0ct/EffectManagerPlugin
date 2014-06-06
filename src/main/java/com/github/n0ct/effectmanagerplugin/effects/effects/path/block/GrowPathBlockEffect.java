package com.github.n0ct.effectmanagerplugin.effects.effects.path.block;

import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.MaterialEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;
import com.github.n0ct.effectmanagerplugin.util.Vector;
import com.github.n0ct.effectmanagerplugin.util.Vector2D;

public class GrowPathBlockEffect extends AbstractPathBlockEffect {

	public static GrowPathBlockEffect valueOf(Map<String,Object> map) {
		return new GrowPathBlockEffect(map);
	}
	
	public static GrowPathBlockEffect deserialize(Map<String,Object> map) {
		return new GrowPathBlockEffect(map);
	}
	
	public GrowPathBlockEffect(Map<String,Object> map) {
		super(map);
	}
	
	public GrowPathBlockEffect(String name) {
		super(name);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void doPathModifications(Location from, final Player player) {
		final World world = player.getWorld();

		final int radius = getRadius();
		final ArrayList<PathSavedBlock> savedBlocks;
			savedBlocks = new ArrayList<PathSavedBlock>();
		final PathSavedBlock origin = new PathSavedBlock(world.getBlockAt((int)from.getBlockX(), (int)(int)from.getBlockY()-1,(int)from.getBlockZ()));
		
		//on met dans une liste les  blocks à modifier
		final int startX = origin.getLocation().getBlockX();
		final int startZ = origin.getLocation().getBlockZ();
		final Material supportingMaterial = getSupportMaterial();
		final Material growingMaterial =getGrowingMaterial();
		final int y = origin.getLocation().getBlockY();
		
		if (radius > 1) {
			int maxX = startX+radius;
			int minX = startX-radius;
			int maxZ = startZ+radius;
			int minZ = startZ-radius;
			Vector2D center = new Vector2D(startX,startZ);
			for (int x=minX;x<=maxX;x++) {
				for (int z=minZ;z<=maxZ;z++) {
					Vector pt = new Vector(x,y,z);
					if (pt.toVector2D().subtract(center).divide(radius).lengthSq() <= 1) {
						PathSavedBlock pathSavedBlock = new PathSavedBlock(world.getBlockAt(x, y, z));
						if (!(AbstractPathBlockEffect.UNMODIFIED_BLOCK_TYPES.contains(pathSavedBlock.getType()) ||
								getMaterials().contains(pathSavedBlock.getType()))) {
							Block upBlock = world.getBlockAt(pathSavedBlock.getLocation().getBlockX(),pathSavedBlock.getLocation().getBlockY()+1,pathSavedBlock.getLocation().getBlockZ());
							Block upperpBlock = world.getBlockAt(pathSavedBlock.getLocation().getBlockX(),pathSavedBlock.getLocation().getBlockY()+2,pathSavedBlock.getLocation().getBlockZ());
							if ((upBlock.getType() == Material.AIR || upBlock.getType() == Material.SNOW) && (upperpBlock.getType() == Material.AIR)) {
								savedBlocks.add(pathSavedBlock);
								savedBlocks.add(new PathSavedBlock(upBlock));
							}
						}
					}
				}
			}
		} else {
			if (!(AbstractPathBlockEffect.UNMODIFIED_BLOCK_TYPES.contains(origin.getType()) ||
					getMaterials().contains(origin.getType()))) {
				Block upBlock = world.getBlockAt(origin.getLocation().getBlockX(),origin.getLocation().getBlockY()+1,origin.getLocation().getBlockZ());
				Block upperpBlock = world.getBlockAt(origin.getLocation().getBlockX(),origin.getLocation().getBlockY()+2,origin.getLocation().getBlockZ());
				if ((upBlock.getType() == Material.AIR || upBlock.getType() == Material.SNOW) && (upperpBlock.getType() == Material.AIR)) {
					savedBlocks.add(origin);
					savedBlocks.add(new PathSavedBlock(upBlock));
				}
			}
		}
		
		//On programme la remise en place des blocs a leur etat d'origine
		runTaskLater(new Runnable() {
			public void run() {
				for (PathSavedBlock pathSavedBlock : savedBlocks) {
						player.getWorld().getBlockAt(pathSavedBlock.getLocation()).setTypeIdAndData(pathSavedBlock.getType().getId(), pathSavedBlock.getData(), false);
				}
			}
		},getDelay()*17);
		
		//On remplace le(s) block(s) sous ses pieds et le bloc au dessus
		for (PathSavedBlock pathSavedBlock : savedBlocks) {
			if (pathSavedBlock.getLocation().getBlockY() == y) {
				player.getWorld().getBlockAt(pathSavedBlock.getLocation()).setTypeIdAndData(supportingMaterial.getId(), (byte)0,true);
			} else {
				player.getWorld().getBlockAt(pathSavedBlock.getLocation()).setTypeIdAndData(growingMaterial.getId(), (byte)0, false);
				//On programme l'evolution du ble
				for(int i=1;i<8;i++) {
		    		runTaskLater(new DataModifier(world, pathSavedBlock.getLocation(), i), getDelay()*i);
				}
				//On programme la regression du ble
				for(int i=7;i>=0;i--) {
		    		runTaskLater(new DataModifier(world, pathSavedBlock.getLocation(), i), getDelay()*8 + getDelay()*(8-i));
				}
			}
			player.getWorld().getBlockAt(pathSavedBlock.getLocation()).setData((byte)0, false);
		}

	}

	private Material getSupportMaterial() {
		return ((MaterialEffectParameter)getParameters().getParameterWithUniqueName("supportingblock",true)).getValue();
	}

	private Material getGrowingMaterial() {
		return  ((MaterialEffectParameter)getParameters().getParameterWithUniqueName("growingblock",true)).getValue();
	}

	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("effect Parameters","params","Path Block Effect Parameters", false, " ", 3);
		EffectParameters paramBlocks = new EffectParameters("Blocks","blocks","Some blocks separated by ','.", false, ",", 2);
		MaterialEffectParameter block1 = new MaterialEffectParameter("Block", "supportingblock", "Block witch will appear under players's steps.", true, Material.SOIL);
		paramBlocks.addSubEffectParameter(block1);
		MaterialEffectParameter block2 = new MaterialEffectParameter("Block", "growingblock", "Block where plants will grow.", true, Material.CROPS);
		paramBlocks.addSubEffectParameter(block2);
		ret.addSubEffectParameter(paramBlocks);
		IntegerEffectParameter radius = new IntegerEffectParameter("Radius","radius","Radius of block modification.", true, 1, 1, 4);
		ret.addSubEffectParameter(radius);
		IntegerEffectParameter duration = new IntegerEffectParameter("Delay","delay","Delay before the blocks will be reinitialized to their original state.", true, 17, 1, 30);
		ret.addSubEffectParameter(duration);
		return ret;
	}

	@Override
	protected String getHelp() {
		return "Modify blocks under the player steps and make some plant grow over it.";
	}

	@Override
	protected String getDescription() {
		return "Modify blocks under the player steps and make it grow. The blocks within the raduis defined as parameters are randomly transformed into the blocks defined as parameter. The blocks are reinitialized to their original material after the duration defined in parameters.";
	}

}
