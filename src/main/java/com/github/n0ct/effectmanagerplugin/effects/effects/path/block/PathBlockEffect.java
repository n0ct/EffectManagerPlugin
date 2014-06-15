package com.github.n0ct.effectmanagerplugin.effects.effects.path.block;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.n0ct.effectmanagerplugin.effects.effects.SavedBlock;
import com.github.n0ct.effectmanagerplugin.effects.parameters.IntegerEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.MaterialEffectParameter;
import com.github.n0ct.effectmanagerplugin.effects.parameters.generic.EffectParameters;
import com.github.n0ct.effectmanagerplugin.utils.Vector;
import com.github.n0ct.effectmanagerplugin.utils.Vector2D;

public class PathBlockEffect extends AbstractPathBlockEffect {

	public static PathBlockEffect valueOf(Map<String,Object> map) {
		return new PathBlockEffect(map);
	}
	
	public static PathBlockEffect deserialize(Map<String,Object> map) {
		return new PathBlockEffect(map);
	}
	
	public PathBlockEffect(Map<String,Object> map) {
		super(map);
	}
	
	public PathBlockEffect(String name) {
		super(name);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void doPathModifications(Location from, final Player player) {
		final World world = player.getWorld();

		final int radius = getRadius();
		final ArrayList<SavedBlock> savedBlocks;
			savedBlocks = new ArrayList<SavedBlock>();
		final SavedBlock origin = new SavedBlock(world.getBlockAt((int)from.getBlockX(), (int)(int)from.getBlockY()-1,(int)from.getBlockZ()));
		
		//on met dans une liste les  blocks à modifier
		final int startX = origin.getLocation().getBlockX();
		final int startZ = origin.getLocation().getBlockZ();
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
						SavedBlock savedBlock = new SavedBlock(world.getBlockAt(x, y, z));
						if (!(AbstractPathBlockEffect.UNMODIFIED_BLOCK_TYPES.contains(savedBlock.getType()) ||
								getMaterials().contains(savedBlock.getType()))) {
							Block upBlock = world.getBlockAt(savedBlock.getLocation().getBlockX(),savedBlock.getLocation().getBlockY()+1,savedBlock.getLocation().getBlockZ());
							if (upBlock.getType() == Material.AIR) {
								savedBlocks.add(savedBlock);
							} else {
								if (upBlock.getType() == Material.SNOW) {
									Block upperBlock = world.getBlockAt(origin.getLocation().getBlockX(),origin.getLocation().getBlockY()+2,origin.getLocation().getBlockZ());
									if (upperBlock.getType() == Material.AIR) {
										savedBlocks.add(savedBlock);
										savedBlocks.add(new SavedBlock(upBlock));
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (!(AbstractPathBlockEffect.UNMODIFIED_BLOCK_TYPES.contains(origin.getType()) || getMaterials().contains(origin.getType()))) {
				Block upBlock = world.getBlockAt(origin.getLocation().getBlockX(),origin.getLocation().getBlockY()+1,origin.getLocation().getBlockZ());
				
				if (upBlock.getType() == Material.AIR) {
					savedBlocks.add(origin);
				} else {
					if (upBlock.getType() == Material.SNOW) {
						Block upperBlock = world.getBlockAt(origin.getLocation().getBlockX(),origin.getLocation().getBlockY()+2,origin.getLocation().getBlockZ());
						if (upperBlock.getType() == Material.AIR) {
							savedBlocks.add(origin);
							savedBlocks.add(new SavedBlock(upBlock));
						}
					}
				}
			}
		}
		
		final Random rdm = new Random(1891864561);
		//On programme la remise en place des blocs a leur etat d'origine
		runTaskLater(new Runnable() {
			public void run() {
				for (SavedBlock savedBlock : savedBlocks) {
					player.getWorld().getBlockAt(savedBlock.getLocation()).setType(savedBlock.getType());
					player.getWorld().getBlockAt(savedBlock.getLocation()).setData(savedBlock.getData(), false);
				}
			}
		},getDelay());
		
		//On remplace le(s) block(s) sous ses pieds 
		for (SavedBlock savedBlock : savedBlocks) {
			if (savedBlock.getLocation().getY() == y) {
				Material material = getMaterials().get(rdm.nextInt(getMaterials().size()));
				player.getWorld().getBlockAt(savedBlock.getLocation()).setType(material);
				player.getWorld().getBlockAt(savedBlock.getLocation()).setData((byte)0, false);
			} else {
				player.getWorld().getBlockAt(savedBlock.getLocation()).setType(Material.AIR);
				player.getWorld().getBlockAt(savedBlock.getLocation()).setData((byte)0, false);
			}
		}

	}


	
	@Override
	public EffectParameters getDefaultParameters() {
		EffectParameters ret = new EffectParameters("effect Parameters","params","Path Block Effect Parameters", false, " ", 3);
		EffectParameters paramBlocks = new EffectParameters("Blocks","blocks","Some blocks separated by ','.", false, ",", 0);
		MaterialEffectParameter block1 = new MaterialEffectParameter("Block", "block" + EffectParameters.ITERATION_PARAMETER + "0", "Block which will appear under players's steps.", true, Material.GOLD_BLOCK);
		paramBlocks.addSubEffectParameter(block1);
		ret.addSubEffectParameter(paramBlocks);
		IntegerEffectParameter radius = new IntegerEffectParameter("Radius","radius","Radius of block modification.", true, 1, 1, 10);
		ret.addSubEffectParameter(radius);
		IntegerEffectParameter duration = new IntegerEffectParameter("Delay","delay","Delay before the blocks will be reinitialized to their original state.", true, 300, 1, 600);
		ret.addSubEffectParameter(duration);
		return ret;
	}

	@Override
	protected String getHelp() {
		return "Modify blocks under the player steps.";
	}

	@Override
	protected String getDescription() {
		return "Modify blocks under the player steps. The blocks within the raduis defined as parameters are randomly transformed into the blocks defined as parameter. The blocks are reinitialized to their original material after the duration defined in parameters.";
	}

}
